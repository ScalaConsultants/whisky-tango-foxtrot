package io.scalac.wtf.domain.tables

import io.scalac.wtf.domain.{User, UserId}
import slick.driver.PostgresDriver.api._


class Users(tag: Tag)
  extends Table[User](tag, "users") {

  def id: Rep[UserId] = column[UserId]("id", O.PrimaryKey, O.AutoInc)
  def email: Rep[String] = column[String]("email")
  def password: Rep[String] = column[String]("password")

  def * = (id.?, email, password).shaped <> (fromTuple, toTuple)

  def fromTuple = (tuple: (Option[UserId], String, String)) =>
    User(
      tuple._1, 
      tuple._2,
      tuple._3)

  def toTuple: User => Option[(Option[UserId], String, String)] = { user => Some((
    user.id, 
    user.email,
    user.password))
  }
}
