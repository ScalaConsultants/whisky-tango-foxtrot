package io.scalac.wtf.domain

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

trait AkkaDependancy {
  implicit val actorSystem = ActorSystem("wtf-system")
  implicit val actorMaterializer = ActorMaterializer()

  val ec = actorSystem.dispatcher
}