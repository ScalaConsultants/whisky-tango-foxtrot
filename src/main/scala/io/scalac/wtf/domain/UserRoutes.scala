package io.scalac.wtf.domain

import akka.http.scaladsl.server.Directives._
import cats.data.Validated.{Invalid, Valid}
import UserService.createUser
import spray.json.DefaultJsonProtocol._
import cats.data.Reader
import slick.jdbc.JdbcBackend.Database
import cats.implicits._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import scala.concurrent.ExecutionContext.Implicits.global

trait UserRoutes {
  implicit val userFormat = jsonFormat2(NewUser)

  def registerUserRoute = Reader((db: Database) =>
    path ("register") {
      post {
        entity(as[NewUser]) { user =>
          val createdUser = User(email = user.email, password = user.password)
          val result      = db.run(createUser(createdUser))

          onSuccess(result) {
            case Valid(u)   => complete("Successfuly registered user!")
            case Invalid(e) => complete(e.unwrap.mkString(" "))
          }
        }
      }
  })
}