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

  import java.util.regex.Pattern

  sealed trait ValidationError
  final case object WrongEmailPattern extends ValidationError
  final case object EmptyPassword     extends ValidationError

  def validateUser(email: String, password: String): ValidatedNel[ValidationError, User] = {
    val validation = (validateEmail(email) |@| validatePassword(password))

    (validation) map { case (email, password) => User(email = email, password = password)}
  }

  private val emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)

  private def validateEmail(email: String): ValidatedNel[ValidationError, String] = {
    val matches = emailPattern.matcher(email).matches()

    matches match {
      case true => valid(email)
      case false => invalidNel(WrongEmailPattern)
    }
  }

  private def validatePassword(password: String): ValidatedNel[ValidationError, String] = password match {
    case "" => invalidNel(EmptyPassword)
    case _  => valid(password)
  }
}