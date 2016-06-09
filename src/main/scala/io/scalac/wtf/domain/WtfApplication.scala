package io.scalac.wtf.domain

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

trait WtfApplication extends App
  with DatabaseDependancy
  with UserRoutes {

  implicit val actorSystem       = ActorSystem("wtf-system")
  implicit val actorMaterializer = ActorMaterializer()
}