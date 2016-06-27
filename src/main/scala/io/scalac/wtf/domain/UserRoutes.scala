package io.scalac.wtf.domain

import akka.http.scaladsl.server.Directives._
import cats.data.{Reader, Xor}
import spray.json.{JsObject, JsString}

import UserService.createUser
import spray.json.DefaultJsonProtocol._
import cats.implicits._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

trait UserRoutes {
  implicit val userFormat = jsonFormat2(NewUser)

  def registerUserRoute = Reader((config: Config) => {
    implicit val ec = config.ec
    path("register") {
      post {
        entity(as[NewUser]) { userRequest =>
          val user = User(email = userRequest.email, password = userRequest.password)
          val result = config.db.run(createUser(user))

          complete {
            result.map {
              case Xor.Left(errors) => JsString(errors.unwrap.mkString(" "))
              case Xor.Right(_) => JsObject.empty
            }
          }
        }
      }
    }
  })
}
