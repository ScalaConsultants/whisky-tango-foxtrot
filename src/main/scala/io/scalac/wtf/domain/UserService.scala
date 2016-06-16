package io.scalac.wtf.domain

import cats.Monad
import io.scalac.wtf.domain.User.ValidationError
import cats.data.{NonEmptyList, Xor, XorT}
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext


object UserService {

  implicit def dbioMonad(implicit ec: ExecutionContext) = new Monad[DBIO] {
    def pure[A](a: A): DBIO[A] = DBIO.successful(a)
    def flatMap[A, B](fa: DBIO[A])(f: A => DBIO[B]) = fa.flatMap(f)
  }

  def createUser(createdUser: User)(implicit ec: ExecutionContext): XorT[DBIO, NonEmptyList[ValidationError], UserId] =
    for {
      validUser <- User.validateUser(createdUser.email, createdUser.password)
      userId    <- XorT.right[DBIO, NonEmptyList[ValidationError], UserId](UserRepository.save(validUser))
    } yield userId
  
}
