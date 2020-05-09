package com.codeexamples.domain.model

import java.{util => ju}

import scala.scalajs.js

case class Example(
    id: String,
    codeId: String,
    users: List[User],
    markdown: String,
    updatedAt: js.Date,
    isNew: Boolean = false
) {
  def canRemove(userId: String) =
    users.length == 1 && users.contains(userId)
}

object Example {
  def newExample(codeId: String, user: User): Example = Example(
    ju.UUID.randomUUID.toString,
    codeId,
    List(user),
    "",
    new js.Date(),
    true
  )
}
