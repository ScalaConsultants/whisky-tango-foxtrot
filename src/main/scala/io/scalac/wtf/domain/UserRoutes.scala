package io.scalac.wtf.domain

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import cats.data.{Reader, Xor}
import cats.implicits._
import io.scalac.wtf.domain.UserService.createUser
import scala.concurrent.ExecutionContext.Implicits.global
import slick.jdbc.JdbcBackend.Database
import spray.json.DefaultJsonProtocol._
import spray.json.{JsObject, JsString}


trait UserRoutes {
  implicit val userFormat = jsonFormat2(NewUser)

  def registerUserRoute = Reader((db: Database) =>
    path ("register") {
      post {
        entity(as[NewUser]) { userRequest =>
          val user = User(email = userRequest.email, password = userRequest.password)
          val result = db.run(createUser(user))
          
          complete {
            result.map {
              case Xor.Left(errors) => JsString(errors.unwrap.mkString(" "))
              case Xor.Right(_) => JsObject.empty
            }
          }
        }
      }
  })
}
