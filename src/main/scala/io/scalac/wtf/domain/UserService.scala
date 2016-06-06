package io.scalac.wtf.domain

import io.scalac.wtf.domain.User.ValidationError
import cats.data.{NonEmptyList, Xor}
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext


object UserService {

  def createUser(createdUser: User)(implicit ec: ExecutionContext): DBIO[Xor[NonEmptyList[ValidationError], UserId]] =
    for {
      validatedUser <- User.validateUser(createdUser.email, createdUser.password)
      userId <- UserRepository.save(createdUser)
    } yield Xor.right(userId)
  
}
