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
      _ <- createUser(validUser)
    } yield ()

    val resultFuture = db.run(createUserWork)

    resultFuture.onComplete { result =>
      assert(result.isSuccess)
    }
  }

  "An UserService" should "not create an user when it's e-mail is not valid" in {
    val createUserWork = for {
      _ <- createUser(noEmailUser)
    } yield ()

    val resultFuture = db.run(createUserWork)

    resultFuture.onComplete { result =>
      assert(result.isFailure)
    }
  }

  "An UserService" should "not create an user when it's password is not valid" in {
    val createUserWork = for {
      _ <- createUser(noPasswordUser)
    } yield ()

    val resultFuture = db.run(createUserWork)

    resultFuture.onComplete { result =>
      assert(result.isFailure)
    }
  }

  "An UserService" should "not create an user when the credentials are empty" in {
    val createUserWork = for {
      _ <- createUser(noCredentailsUser)
    } yield ()

    val resultFuture = db.run(createUserWork)

    resultFuture.onComplete { result =>
      assert(result.isFailure)
    }
  }

  "An UserService" should "not create an user when it's the e-mail is taken" in {
    val createUserWork = for {
      _ <- createUser(validUser)
      _ <- createUser(validUser)
    } yield ()

    val resultFuture = db.run(createUserWork)

    resultFuture.onComplete { result =>
      assert(result.isFailure)
    }
  }

  after {
    db.close()
  }
}