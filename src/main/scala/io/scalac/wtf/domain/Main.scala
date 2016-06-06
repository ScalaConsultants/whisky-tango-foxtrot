package io.scalac.wtf.domain

import slick.driver.H2Driver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import slick.driver.H2Driver._
import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import UserService.createUser
import cats.data.Validated.{Invalid, Valid}
import cats.implicits._
import io.scalac.wtf.domain.tables.Users

import scala.concurrent.Await
import scala.concurrent.duration.Duration

case class JSONUser(email: String, password: String)

object Main extends App {
  override def main(args: Array[String]) {

    implicit val actorSystem       = ActorSystem("system")
    implicit val actorMaterializer = ActorMaterializer()

    implicit val userFormat = jsonFormat2(JSONUser)

    val db         = Database.forConfig("h2mem1")
    val usersTable = TableQuery[Users]

    //Used for showcase only, the table should already exist in a realistic scenario
    val createSchemaWork = for {
      _ <- usersTable.schema.create
    } yield ()
    Await.result(db.run(createSchemaWork), Duration.Inf)

    val createUserRoute = path("register") {
      post {
        entity(as[JSONUser]) { user =>
          val createdUser = User(email = user.email, password = user.password)
          val result = db.run(createUser(createdUser))

          onSuccess(result) {
            case Valid(u) => complete("Successfuly registered user!")
            case Invalid(e) => complete(e.unwrap.mkString(" "))
          }
        }
      }
    }

    Http().bindAndHandle(createUserRoute, "localhost", 8080)
  }
}