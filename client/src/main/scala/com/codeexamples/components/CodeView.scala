package com.codeexamples.components

import com.codeexamples.domain.model.Code
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object CodeView {
  case class Props(onClick: Callback, code: Code, isSelected: Boolean)

  class Backend($ : BackendScope[Props, Unit]) {
    def render(p: Props): VdomElement =
      <.div(
        ^.onClick --> p.onClick,
        ^.className := "item",
        if (p.isSelected) ^.className := "selected" else ^.className := "item",
        <.div(^.className := "title", p.code.name),
        <.div(^.className := "subtitle", p.code.description)
      )
  }

  val component = ScalaComponent
    .builder[Props]("CodeView")
    .renderBackend[Backend]
    .build

  def apply(P: Props) =
    component.withKey(P.code.id.toString)(P)
}
