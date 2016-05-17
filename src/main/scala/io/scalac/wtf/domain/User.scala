package io.scalac.wtf.domain

import cats.data.Validated.{ invalidNel, valid }
import cats.data.{ NonEmptyList, ValidatedNel }
import cats.implicits._
import slick.lifted.MappedTo

case class UserId(value: Long) extends MappedTo[Long]

case class User(
    id: Option[UserId] = None,
    email: String, 
    password: String)

object User {

  sealed trait ValidationError
  final case object EmptyEmail    extends ValidationError
  final case object EmptyPassword extends ValidationError

  def validateUser(email: String, password: String): ValidatedNel[ValidationError, User] = {
    val validation = (validateEmail(email) |@| validatePassword(password))

    (validation) map { case (email, password) => User(email = email, password = password)}
  }

  private def validateEmail(email: String): ValidatedNel[ValidationError, String] = email match {
    case "" => invalidNel(EmptyEmail)
    case _  => valid(email)
  }

  private def validatePassword(password: String): ValidatedNel[ValidationError, String] = password match {
    case "" => invalidNel(EmptyPassword)
    case _  => valid(password)
  }
}