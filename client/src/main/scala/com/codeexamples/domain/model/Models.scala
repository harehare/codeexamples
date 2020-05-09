package com.codeexamples.domain.model

import diode.data.Pot
import scala.scalajs.js

case class Category(id: String, icon: String, name: String, updatedAt: js.Date)

case class Categories(list: Pot[List[Category]], selectedCategory: Option[Category])

case class Codes(
    list: Pot[List[Code]],
    selectedCode: Option[Code],
    isLoading: Boolean = false,
    hasNext: Boolean = false
)

case class Examples(
    examples: Pot[List[Example]],
    editExample: Option[Example],
    hasNext: Boolean = false,
    isLoading: Boolean = false
)

case class Session(user: Option[User])

case class Window(x: Double, position: Double, moveX: Double, isResize: Boolean = false)

case class Model(
    session: Session,
    categories: Categories,
    codes: Codes,
    examples: Examples,
    window: Window
)
