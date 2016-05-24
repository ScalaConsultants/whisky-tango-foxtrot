package io.scalac.wtf.domain

import io.scalac.wtf.domain.tables.Users
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import slick.driver.H2Driver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import slick.driver.H2Driver._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import UserService.createUser
import cats.data.Validated.{Invalid, Valid}
import io.scalac.wtf.domain.User.{PasswordTooShort, WrongEmailPattern}

import cats.implicits._

import scala.util.Success

class UserServiceSpec extends FlatSpec with Matchers with BeforeAndAfter {

  val db         = Database.forConfig("h2mem1")
  val usersTable = TableQuery[Users]

  val validEmail    = "aaa@scalac.io"
  val validPassword = "Password123"

  val validUser         = User(email = validEmail, password = validPassword)
  val noEmailUser       = User(email = "", password = validPassword)
  val noPasswordUser    = User(email = validEmail, password = "")
  val noCredentailsUser = User(email = "", password = "")

  before {
    val createSchemaWork = for {
      _ <- usersTable.schema.create
    } yield ()

    Await.result(db.run(createSchemaWork), Duration.Inf)
  }

  "An UserService" should "create an user when it's valid and e-mail not taken" in {
    val createUserWork = for {
      result <- createUser(validUser)
    } yield result

    val resultFuture = db.run(createUserWork)

    resultFuture.onComplete { result =>
      assert(result.isSuccess)

      result match {
        case Success(r) => assert(r.isValid)
        case _          => assert(false)
      }
    }
  }

  "An UserService" should "not create an user when it's e-mail is not valid" in {
    val createUserWork = for {
      result <- createUser(noEmailUser)
    } yield result

    val resultFuture = db.run(createUserWork)

    resultFuture.onComplete { result =>
      assert(result.isSuccess)

      result match {
        case Success(r) => assert(r.isInvalid)
        case _          => assert(false)
      }
    }
  }

  "An UserService" should "not create an user when it's password is not valid" in {
    val createUserWork = for {
      result <- createUser(noPasswordUser)
    } yield result

    val resultFuture = db.run(createUserWork)

    resultFuture.onComplete { result =>
      assert(result.isSuccess)

      result match {
        case Success(r) => assert(r.isInvalid)
        case _          => assert(false)
      }
    }
  }

  "An UserService" should "not create an user when the credentials are empty" in {
    val createUserWork = for {
      result <- createUser(noCredentailsUser)
    } yield result

    val resultFuture = db.run(createUserWork)

    resultFuture.onComplete { result =>
      assert(result.isSuccess)

      result match {
        case Success(r) => assert(r.isInvalid)
        case _          => assert(false)
      }
    }
  }

  "An UserService" should "not create an user when it's the e-mail is taken" in {
    val createUserWork = for {
      _      <- createUser(validUser)
      result <- createUser(validUser)
    } yield result

    val resultFuture = db.run(createUserWork)

    resultFuture.onComplete { result =>
      assert(result.isSuccess)

      result match {
        case Success(r) => assert(r.isInvalid)
        case _          => assert(false)
      }
    }
  }

  "An UserService" should "not create an user when the credentials are empty and return 2 validation errors in the result" in {
    val createUserWork = for {
      result <- createUser(noCredentailsUser)
    } yield result

    val resultFuture = db.run(createUserWork)

    resultFuture.onComplete { result =>
      assert(result.isSuccess)

      result match {
        case Success(r) => {
          assert(r.isInvalid)
          r match {
            case Valid(_) => assert(false)
            case Invalid(e) => {
              val errorList = e.unwrap
              assert(errorList.contains(WrongEmailPattern))
              assert(errorList.contains(PasswordTooShort))
            }
          }
        }
        case _ => assert(false)
      }
    }
  }

  after {
    db.close()
  }
}