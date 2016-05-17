package io.scalac.wtf.domain

import cats.data.{NonEmptyList, Validated}
import cats.data.Validated.{Invalid, Valid}
import io.scalac.wtf.domain.User.ValidationError
import org.scalatest._

class UserSpec extends FlatSpec with Matchers{
  "An User" should "be invalid if the e-mail is empty" in {
    val email    = ""
    val password = "aaa"

    val result = User.validateUser(email, password)

    result.isInvalid should be (true)
  }

  "An User" should "be invalid if the password is empty" in {
    val email    = "aaa@aaa.com"
    val password = ""

    val result = User.validateUser(email, password)

    result.isInvalid should be (true)
  }

  "The validation" should "return a list with more than one error when the e-mail and password are both empty" in {
    val email    = ""
    val password = ""

    val result = User.validateUser(email, password)

    result should be ('Invalid)

    result.toEither.left.get.tail should not be empty
  }
}

