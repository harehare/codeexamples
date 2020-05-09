package com.codeexamples.components

import com.codeexamples.domain.model.Category
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object CategoryView {
  case class Props(onClick: Callback, category: Category, isSelected: Boolean)

  class Backend($ : BackendScope[Props, Unit]) {
    def render(p: Props): VdomElement = {
      <.div(
        ^.onClick --> p.onClick,
        ^.className := "item",
        if (p.isSelected) ^.className := "selected" else ^.className := "item",
        <.img(^.className := "category-icon", ^.src := s"/icon/${p.category.icon}.svg"),
        <.div(^.className := "category-text", p.category.name)
      )
    }
  }

  val component = ScalaComponent
    .builder[Props]("CategoryView")
    .renderBackend[Backend]
    .build

  def apply(P: Props) =
    component(P)
}
