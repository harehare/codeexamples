package com.codeexamples.domain.model

import java.{util => ju}

import scala.scalajs.js

case class Code(
    id: String,
    categoryId: String,
    userId: String,
    name: String,
    description: String,
    updatedAt: js.Date,
    isNew: Boolean = false
)

object Code {
  def newCode(categoryId: String, userId: String): Code = Code(
    ju.UUID.randomUUID.toString,
    categoryId,
    userId,
    "",
    "",
    new js.Date(),
    true
  )
}
