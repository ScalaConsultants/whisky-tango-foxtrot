package io.scalac.wtf.domain

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import slick.jdbc.JdbcBackend.Database
import slick.lifted.TableQuery
import slick.driver.H2Driver.api._
import io.scalac.wtf.domain.tables.Users
import scala.concurrent.ExecutionContext.Implicits.global

trait DatabaseDependancy {

  val db = Database.forConfig("h2mem1")

  //Used for showcase only, the table should already exist in a realistic scenario
  val createSchemaWork = for {
    _ <- TableQuery[Users].schema.create
  } yield ()
  Await.result(db.run(createSchemaWork), Duration.Inf)
}