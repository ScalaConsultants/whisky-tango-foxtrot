package io.scalac.wtf.domain

import cats.data.Reader
import slick.jdbc.JdbcBackend.Database
import slick.lifted.TableQuery
import slick.driver.H2Driver.api._
import io.scalac.wtf.domain.tables.Users

trait DatabaseDependancy {

  val db = Database.forConfig("h2mem1")

  //Used for showcase only, the table should already exist in a realistic scenario
  def createSchemaWork = Reader((config: Config) =>
    {
      implicit val ec = config.ec
      for {
        _ <- TableQuery[Users].schema.create
      } yield ()
    }
  )
}