package com.codeexamples.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object ProgressBar {

  implicit val isLoadingReuse = Reusability.by((isLoading: Boolean) => isLoading)

  val component = ScalaComponent
    .builder[Boolean]("ProgressBar")
    .render_P(
      isLoading =>
        if (isLoading) <.div(^.className := "progress", <.div(^.className := "indeterminate"))
        else <.div(^.className := "progress-area")
    )
    .configure(Reusability.shouldComponentUpdate)
    .build

  def apply(isLoading: Boolean) =
    component(isLoading)
}
