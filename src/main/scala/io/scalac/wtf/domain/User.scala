package io.scalac.wtf.domain

import cats._
import cats.data.{Validated, Xor, XorT}
import cats.data.Validated._
import cats.data.{NonEmptyList}
import cats.implicits._
import slick.dbio.DBIO
import slick.lifted.MappedTo

import scala.concurrent.ExecutionContext


case class UserId(value: Long) extends MappedTo[Long]

case class User(
    id: Option[UserId] = None,
    email: String, 
    password: String)

object User {

  sealed trait ValidationError
  final case object WrongEmailPattern extends ValidationError
  final case object PasswordTooShort  extends ValidationError
  final case object UserAlreadyExists extends ValidationError

  private val emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".r

  //we might need to move it to a more globally accessible place at some point  
  implicit def dbioMonad(implicit ec: ExecutionContext) = new Monad[DBIO] {
    def pure[A](a: A): DBIO[A] = DBIO.successful(a)
    def flatMap[A, B](fa: DBIO[A])(f: A => DBIO[B]) = fa.flatMap(f)
  }

  def validateUser(email: String, password: String)(implicit ec: ExecutionContext): DBIO[Xor[NonEmptyList[ValidationError], User]] = {
    val validatedUser = for {
      emailV <- validateEmail(email).toValidated
      emailNel = emailV.toValidatedNel
      passwordNel = validatePassword(password).toValidatedNel
    } yield (emailNel |@| passwordNel) map { 
      case (email, password) => User(email = email, password = password)
    }

    validatedUser.map(_.toXor)
  }

  private def validateEmail(email: String)(implicit ec: ExecutionContext): XorT[DBIO, ValidationError, String] = {
    val emailPatternValidation = (emailPattern findFirstIn email) match {
      case Some(_) => Xor.right(email)
      case None => Xor.left(WrongEmailPattern)
    }

    val emailUniqueValidation = UserRepository.findByEmail(email) map { userOpt => 
      userOpt match {
        case Some(_) => Xor.left(UserAlreadyExists)
        case None => Xor.right(email)
      }
    }

    for {
      e1 <- XorT[DBIO, ValidationError, String](DBIO.successful(emailPatternValidation))
      e2 <- XorT[DBIO, ValidationError, String](emailUniqueValidation)
    } yield (e1 |+| e2)
  }

  private def validatePassword(password: String): Validated[ValidationError, String] =
    if(password.length >= 6)
      valid(password)
    else
      invalid(PasswordTooShort)
      
}
