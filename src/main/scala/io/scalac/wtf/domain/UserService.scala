package io.scalac.wtf.domain

import cats.data.Validated.{Invalid, Valid}
import cats.{Applicative, Functor, Monad}
import cats.implicits._
import slick.dbio.DBIO
import slick.driver.H2Driver.api._

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
  def createUser(createdUser: User)(implicit executionContext: ExecutionContext): DBIO[UserId] = {
    UserRepository.findByEmail(createdUser.email).flatMap {
      case Some(_) => DBIO.failed(new Exception("Provided email is already taken"))
      case _ => {
        val validation = User.validateUser(createdUser.email, createdUser.password)

        validation match {
          case Valid(u)   => UserRepository.save(u)
          case Invalid(e) => DBIO.failed(new Exception(e.unwrap.mkString(" ")))
        }
      }
    }.transactionally
  }
}