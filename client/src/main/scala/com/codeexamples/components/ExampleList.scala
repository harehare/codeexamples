package com.codeexamples.components

import com.codeexamples.domain.model._
import diode.data.{Failed, Empty, Pending, Ready}
import fontAwesome._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import mouse.option._

object ExampleList {

  case class Props(
      session: Session,
      code: Code,
      examples: Examples,
      add: (String, User) => Callback,
      edit: Example => Callback,
      save: Example => Callback,
      remove: Example => Callback,
      cancel: Callback
  )

  implicit val propsReuse = Reusability[Props](
    (p1, p2) =>
      p1.examples.examples == p2.examples.examples
        && p1.examples.editExample == p2.examples.editExample
        && p1.code.id == p2.code.id
        && p1.session.user.cata(_.id, "") == p2.session.user.cata(_.id, "")
  )

  class Backend($ : BackendScope[Props, Unit]) {
    def render(p: Props): VdomElement = {

      val addExampleButton = (p.session.user, p.examples.editExample) match {
        case (Some(user), Some(example)) =>
          <.div(
            if (example.isNew) ^.className := "disabled-icon" else ^.className := "icon",
            ^.dangerouslySetInnerHtml := fontawesome.icon(freeSolid.faPlus).html.join(""),
            ^.onClick --> (if (example.isNew) Callback.empty else p.add(p.code.id, user))
          )
        case (Some(user), _) =>
          <.div(
            ^.className := "icon",
            ^.dangerouslySetInnerHtml := fontawesome.icon(freeSolid.faPlus).html.join(""),
            ^.onClick --> p.add(p.code.id, user)
          )
        case _ =>
          <.div()
      }

      <.div(
        ^.id := "example-list",
        <.div(
          ^.className := "list",
          p.examples.examples match {
            case Ready(examples) => {
              <.div(
                <.div(
                  ^.className := "title",
                  <.div(^.className := "label", s"${examples.length.toString} EXAMPLES"),
                  addExampleButton
                ),
                examples
                  .toTagMod(
                    example =>
                      <.div(
                        ExampleView(
                          ExampleView.Props(
                            session = p.session,
                            example =
                              if (p.examples.editExample.cata(_.id, "") == example.id)
                                p.examples.editExample.getOrElse(example)
                              else example,
                            p.examples.editExample.cata(_.id, "") == example.id,
                            save = p.save,
                            edit = p.edit,
                            remove = p.remove,
                            cancel = p.cancel
                          )
                        )
                      )
                  )
              )
            }
            case Pending(_) =>
              <.div(
                <.div(
                  ^.className := "title",
                  <.div(^.className := "label", "0 EXAMPLES"),
                  addExampleButton
                ),
                <.div(
                  0.to(4)
                    .toTagMod(
                      _ =>
                        <.div(
                          ^.className := "example example-loading shimmer"
                        )
                    )
                )
              )
            case Empty =>
              <.div(
                <.div(
                  ^.className := "title",
                  <.div(^.className := "label", "0 EXAMPLES"),
                  addExampleButton
                )
              )
            case _ => {
              <.div(
                <.div(
                  ^.className := "title",
                  <.div(^.className := "label", "0 EXAMPLES"),
                  addExampleButton
                )
              )
            }
          }
        )
      )
    }
  }

  val component = ScalaComponent
    .builder[Props]("ExampleView")
    .renderBackend[Backend]
    .configure(Reusability.shouldComponentUpdate)
    .build

  def apply(P: Props) =
    component(P)
}
