package io.scalac.wtf.domain

import cats.Monad
import slick.dbio._

import scala.concurrent.ExecutionContext

object Implicits {

  implicit def dbioMonad(implicit ec: ExecutionContext) = new Monad[DBIO] {
    def pure[A](a: A): DBIO[A] = DBIO.successful(a)
    def flatMap[A, B](fa: DBIO[A])(f: A => DBIO[B]) = fa.flatMap(f)
  }
}