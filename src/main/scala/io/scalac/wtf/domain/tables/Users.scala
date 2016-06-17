package io.scalac.wtf.domain.tables

import io.scalac.wtf.domain.{User, UserId, UserMapping}
import slick.driver.PostgresDriver.api._


class Users(tag: Tag)
  extends Table[UserMapping](tag, "users") {

  def id: Rep[UserId] = column[UserId]("id", O.PrimaryKey, O.AutoInc)
  def email: Rep[String] = column[String]("email")
  def passwordHash: Rep[String] = column[String]("passwordHash")

  def * = (id.?, email, passwordHash).shaped <> (fromTuple, toTuple)

  def fromTuple = (tuple: (Option[UserId], String, String)) =>
    UserMapping(
      tuple._1, 
      tuple._2,
      tuple._3)

  def toTuple: UserMapping => Option[(Option[UserId], String, String)] = { user => Some((
    user.id, 
    user.email,
    user.passwordHash))
  }
}
