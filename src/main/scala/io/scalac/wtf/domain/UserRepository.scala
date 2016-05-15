package io.scalac.wtf.domain

import scala.concurrent.ExecutionContext
import slick.driver.PostgresDriver.api._
import io.scalac.wtf.domain.tables.Users


trait UserRepository {

  implicit def executionContext: ExecutionContext
  
  val usersTable: TableQuery[Users] = TableQuery[Users]

  def findByEmail(email: String): DBIO[Option[User]] =
    usersTable.filter(_.email === email).result.map(_.headOption)
    
  def save(user: User): DBIO[UserId] =
    (usersTable returning usersTable.map(_.id)) += user
  
}
