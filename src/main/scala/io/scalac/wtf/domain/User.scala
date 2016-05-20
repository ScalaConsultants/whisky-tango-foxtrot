package io.scalac.wtf.domain

import cats.data.Validated.{invalidNel, valid}
import cats.data.{NonEmptyList, ValidatedNel}
import cats.implicits._
import slick.lifted.MappedTo

import scala.util.matching.Regex

case class UserId(value: Long) extends MappedTo[Long]

case class User(
    id: Option[UserId] = None,
    email: String, 
    password: String)

object User {

  sealed trait ValidationError
  final case object WrongEmailPattern extends ValidationError
  final case object EmptyPassword     extends ValidationError

  def validateUser(email: String, password: String): ValidatedNel[ValidationError, User] = {
    val validation = (validateEmail(email) |@| validatePassword(password))

    (validation) map { case (email, password) => User(email = email, password = password)}
  }

  private val emailPattern    = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$".r
  private val passwordPattern = ".{6,}".r

  private def validateEmail(email: String): ValidatedNel[ValidationError, String] = {
    val matches = emailPattern findFirstIn email

    matches match {
      case Some(_) => valid(email)
      case None    => invalidNel(WrongEmailPattern)
    }
  }

  private def validatePassword(password: String): ValidatedNel[ValidationError, String] = {
    val matches = passwordPattern findFirstIn password

    matches match {
      case Some(_) => valid(password)
      case None    => invalidNel(EmptyPassword)
    }
  }
}