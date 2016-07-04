package io.scalac.wtf.domain

import javax.mail.internet.InternetAddress

import io.scalac.wtf.domain.Implicits._
import io.scalac.wtf.domain.User.ValidationError
import cats.data.{NonEmptyList, Reader, Xor, XorT}
import slick.dbio.DBIO
import courier._
import Defaults._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}


object UserService {

  def createUser(createdUser: User)(implicit ec: ExecutionContext): DBIO[Xor[NonEmptyList[ValidationError], UserId]] = {
    val resT = for {
      validUser <- XorT[DBIO, NonEmptyList[ValidationError], User](User.validateUser(createdUser.email, createdUser.password))
      _         <- XorT.right[DBIO, NonEmptyList[ValidationError], Unit](sendVerificationEmail(registrationMailer, validUser.email))
      userId    <- XorT.right[DBIO, NonEmptyList[ValidationError], UserId](UserRepository.save(validUser))
    } yield userId

    resT.value
  }

  private val registrationMailer = Mailer("smtp.gmail.com", 587)
    .auth(true)
    .as("you@gmail.com", "p@$$w3rd")
    .startTtls(true)()

  private def sendVerificationEmail(mailer: Mailer, email: String): DBIO[Unit] = {
      Try(InternetAddress.parse(email)) match {
      case Success(parsedAddresses) => {
        DBIO.from(
          mailer(Envelope.from("wtfverify" `@` "gmail.com")
            //TODO: This should never throw as it is run after validateUser, but to be redone
            .to(parsedAddresses.head)
            .subject("Verification")
            .content(Text("Please verify your account by clicking the link:")))
        )
      }
      case Failure(e) => DBIO.failed(e)
    }
  }
}
