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

object Main extends WtfApplication {
  override def main(args: Array[String]) {

    Http().bindAndHandle(registerUserRoute(db), "localhost", 8080)
  }
}