package io.scalac.wtf.domain

import slick.jdbc.JdbcBackend.Database

import scala.concurrent.ExecutionContext

case class Config(ec: ExecutionContext,
                  db: Database)