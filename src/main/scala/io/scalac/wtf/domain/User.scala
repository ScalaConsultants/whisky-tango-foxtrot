package io.scalac.wtf.domain

import io.scalac.wtf.domain.Implicits._
import cats._
import cats.data.{Xor, XorT}
import cats.data.Xor._
import cats.data.Validated._
import cats.data.{NonEmptyList}
import cats.implicits._
import slick.dbio.DBIO
import slick.lifted.MappedTo
import com.github.t3hnar.bcrypt._

import scala.concurrent.ExecutionContext

case class UserId(value: Long) extends MappedTo[Long]
/*
case class User(
    id: Option[UserId] = None,
    email: String, 
    password: String)
*/

abstract class User(email: String, password: String)
case class UserPrototype(email: String, password: String)                              extends User(email, password)
case class UserValidated(email: String, password: String)                              extends User(email, password)
case class UserMapping(id: Option[UserId] = None, email: String, passwordHash: String) extends User(email, passwordHash)

object User {

  sealed trait ValidationError
  case object WrongEmailPattern extends ValidationError
  case object PasswordTooShort  extends ValidationError
  case object UserAlreadyExists extends ValidationError

  private val emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".r

  def validateUser(userPrototype: UserPrototype)(implicit ec: ExecutionContext): DBIO[Xor[NonEmptyList[ValidationError], UserValidated]] = {

    val validation = validateEmail(userPrototype.email).toValidated map { emailV =>
      (emailV.toValidatedNel |@| validatePassword(userPrototype.password).toValidatedNel) map (UserValidated.apply(_, _))
    }

    validation.map(_.toXor)
  }

  def mapUser(userValidated: UserValidated): UserMapping = UserMapping(email = userValidated.email, passwordHash = userValidated.password.bcrypt)

  private def validateEmail(email: String)(implicit ec: ExecutionContext): XorT[DBIO, ValidationError, String] = {

    val emailPatternValidation = Xor.fromOption(emailPattern findFirstIn email, WrongEmailPattern)

    val emailUniqueValidation = UserRepository.findByEmail(email) map {
        case Some(_) => left(UserAlreadyExists)
        case None    => right(email)
    }

    for {
      e1 <- XorT[DBIO, ValidationError, String](DBIO.successful(emailPatternValidation))
      e2 <- XorT[DBIO, ValidationError, String](emailUniqueValidation)
    } yield e1 |+| e2
  }

  private def validatePassword(password: String): Xor[ValidationError, String] =
    if(password.length >= 6)
      right(password)
    else
      left(PasswordTooShort)
      
}
