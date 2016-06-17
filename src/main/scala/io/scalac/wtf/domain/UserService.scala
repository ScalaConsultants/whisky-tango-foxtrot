package io.scalac.wtf.domain

import io.scalac.wtf.domain.Implicits._
import io.scalac.wtf.domain.User.ValidationError
import cats.data.{NonEmptyList, Xor, XorT}
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext


object UserService {

  def createUser(createdUser: User)(implicit ec: ExecutionContext): DBIO[Xor[NonEmptyList[ValidationError], UserId]] = {
    val resT = for {
      validUser <- XorT[DBIO, NonEmptyList[ValidationError], User](User.validateUser(createdUser.email, createdUser.password))
      userId    <- XorT.right[DBIO, NonEmptyList[ValidationError], UserId](UserRepository.save(validUser))
    } yield userId

    resT.value
  }

}
