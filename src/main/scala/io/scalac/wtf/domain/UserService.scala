package io.scalac.wtf.domain

import io.scalac.wtf.domain.Implicits._
import io.scalac.wtf.domain.User.ValidationError
import cats.data.{NonEmptyList, Xor, XorT}
import slick.dbio.DBIO

import io.scalac.wtf.domain.User._

import com.github.t3hnar.bcrypt._

import scala.concurrent.ExecutionContext


object UserService {

  def createUser(userPrototype: UserPrototype)(implicit ec: ExecutionContext): DBIO[Xor[NonEmptyList[ValidationError], UserId]] = {
    val resT = for {
      validUser  <- XorT[DBIO, NonEmptyList[ValidationError], UserValidated](validateUser(userPrototype))
      mappedUser =  mapUser(validUser)
      userId     <- XorT.right[DBIO, NonEmptyList[ValidationError], UserId](UserRepository.save(mappedUser))
    } yield userId

    resT.value
  }
}
