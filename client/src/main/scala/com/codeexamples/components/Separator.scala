package com.codeexamples.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object Separator {
  val component = ScalaComponent
    .builder[Double => Callback]("Separator")
    .render_P(
      onStartResize =>
        <.div(^.className := "separator", ^.onMouseDown ==> (e => onStartResize(e.pageX)))
    )
    .build

  def apply(onStartResize: Double => Callback) =
    component(onStartResize)
}
