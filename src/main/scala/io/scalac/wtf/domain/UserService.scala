package io.scalac.wtf.domain

import io.scalac.wtf.domain.User.{UserAlreadyExists, ValidationError}
import slick.dbio.DBIO
import slick.driver.H2Driver.api._

import cats.data.Validated.{invalidNel, valid}
import cats.data.{NonEmptyList, ValidatedNel}
import cats.implicits._
import slick.lifted.MappedTo

import scala.concurrent.ExecutionContext

object UserService {

  import scala.concurrent.ExecutionContext.Implicits.global

  /*
  //TODO: Move this somewhere else
  //Evidence for DBIO being a cats Monad
  implicit val dbioMonad = new Functor[DBIO] with Applicative[DBIO] with Monad[DBIO] {
    def pure[A](a: A): DBIO[A] = DBIO.successful(a)
    def flatMap[A, B](fa: DBIO[A])(f: A => DBIO[B]) = fa.flatMap(f)
  }
*/
  def createUser(createdUser: User)(implicit executionContext: ExecutionContext): DBIO[ValidatedNel[ValidationError, UserId]] = {
    val validation = User.validateUser(createdUser.email, createdUser.password)

    UserRepository.findByEmail(createdUser.email).flatMap {
      case Some(_) => {
        val userExistsValidated: ValidatedNel[ValidationError, UserId] = invalidNel(UserAlreadyExists)
        DBIO.successful((validation |@| userExistsValidated) map { case (user, userId) => userId})
      }
      case None => UserRepository.save(createdUser).map(userId => (validation |@| valid(userId)) map { case (user, userId) => userId })
    }
  }
}