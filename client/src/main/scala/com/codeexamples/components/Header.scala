package com.codeexamples.components

import com.codeexamples.domain.model.{Code, Session}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import mouse.option._

object Header {
  case class Props(
      session: Session,
      isLoading: Boolean,
      onSignIn: Callback,
      onSignOut: Callback
  )

  implicit val propsReuse = Reusability[Props](
    (p1, p2) =>
      p1.session.user.cata(_.id, "") == p2.session.user.cata(_.id, "")
        && p1.isLoading == p2.isLoading
  )

  class Backend($ : BackendScope[Props, Unit]) {
    def render(p: Props): VdomElement = {
      <.div(
        ^.id := "header",
        <.div(
          ^.className := "main",
          <.div(
            ^.className := "title",
            ^.display := "flex",
            ^.alignItems := "center",
            ^.justifyContent := "center",
            <.img(^.src := "/icon/icon.svg", ^.className := "logo"),
            <.div(^.fontWeight := "600", ^.fontSize := "0.95rem", "EXAMPLES")
          ),
          <.div(
            ^.className := "actions",
            p.session.user.cata(
              u =>
                <.img(^.src := u.photoUrl, ^.className := "user-icon", ^.onClick --> p.onSignOut),
              <.div(^.className := "button", ^.onClick --> p.onSignIn, "SIGN IN")
            )
          )
        ),
        ProgressBar(p.isLoading)
      )
    }
  }

  val component = ScalaComponent
    .builder[Props]("Header")
    .renderBackend[Backend]
    .configure(Reusability.shouldComponentUpdate)
    .build

  def apply(P: Props) =
    component(P)
}
