package io.scalac.wtf.domain

import akka.http.scaladsl.Http

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main extends WtfApplication {
  override def main(args: Array[String]) {

    val config = Config(ec, db)

    Await.result(db.run(createSchemaWork(config)), Duration.Inf)
    Http().bindAndHandle(registerUserRoute(config), "localhost", 8080)
  }
}