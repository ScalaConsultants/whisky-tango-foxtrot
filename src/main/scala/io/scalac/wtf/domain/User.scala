package io.scalac.wtf.domain

import slick.lifted.MappedTo


case class UserId(value: Long) extends MappedTo[Long]

case class User(
    id: Option[UserId] = None,
    email: String, 
    password: String)
