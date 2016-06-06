package io.scalac.wtf.domain

import scala.concurrent.ExecutionContext
import slick.driver.PostgresDriver.api._
import io.scalac.wtf.domain.tables.Users


object UserRepository {

  val usersTable: TableQuery[Users] = TableQuery[Users]

  def findByEmail(email: String)(implicit executionContext: ExecutionContext): DBIO[Option[User]] =
    usersTable.filter(_.email === email).result.map(_.headOption)
    
  def save(user: User)(implicit executionContext: ExecutionContext): DBIO[UserId] =
    (usersTable returning usersTable.map(_.id)) += user
    
}
