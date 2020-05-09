package com.codeexamples.components

import com.codeexamples.domain.model.{Example, Session}
import com.codeexamples.facade.{HighlightJS, Marked, MarkedOptions}
import fontAwesome._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import mouse.option._

import scala.scalajs.js
import scala.scalajs.js.UndefOr

object ExampleView {
  case class Props(
      session: Session,
      example: Example,
      isEdit: Boolean,
      edit: Example => Callback,
      save: Example => Callback,
      remove: Example => Callback,
      cancel: Callback
  )

  case class State(isModified: Boolean)

  class Backend($ : BackendScope[Props, State]) {

    val options = MarkedOptions(
      highlight = { (source: String, lang: UndefOr[String], _: js.Function) ⇒
        HighlightJS.highlightAuto(source).value
      }
    )

    def render(p: Props, s: State): VdomElement = {
      def onEdit: ReactEventFromTextArea => Callback = { e =>
        $.modState(_.copy(isModified = true)) >>
          p.edit(p.example.copy(markdown = e.target.value))
      }

      def onUpdate() = {
        println(!p.example.markdown.trim.isEmpty)
        $.modState(_.copy(isModified = false)) >>
          (if (s.isModified && !p.example.markdown.trim.isEmpty) p.save(p.example) else p.cancel)
      }

      <.div(
        ^.padding := "8px",
        <.div(
          ^.className := "example-header",
          <.div(
            p.example.users.toTagMod(u => <.img(^.src := u.photoUrl, ^.className := "user-icon"))
          ),
          <.div(
            if (p.isEdit)
              <.div(
                ^.className := "icon",
                ^.dangerouslySetInnerHtml := fontawesome.icon(freeSolid.faCheck).html.join(""),
                ^.onClick --> onUpdate
              )
            else
              <.div(
                ^.className := "icon",
                ^.dangerouslySetInnerHtml := fontawesome.icon(freeRegular.faEdit).html.join(""),
                ^.onClick --> p.edit(p.example)
              ),
            p.session.user.cata(
              u =>
                if (p.example.canRemove(u.id))
                  <.div(
                    ^.className := "icon",
                    ^.dangerouslySetInnerHtml := fontawesome.icon(freeSolid.faTrash).html.join("")
                  )
                else <.div(),
              <.div()
            )
          )
        ),
        if (p.isEdit) {
          <.textarea(
            ^.id := "example-edit",
            ^.className := "example-textarea",
            ^.placeholder := "EXAMPLE",
            ^.onBlur --> onUpdate,
            ^.onChange ==> onEdit,
            ^.defaultValue := p.example.markdown
          )
        } else {
          <.div(
            ^.className := "example",
            ^.dangerouslySetInnerHtml := Marked(p.example.markdown.replace("\\n", "\n"), options)
          )
        }
      )
    }
  }

  val component = ScalaComponent
    .builder[Props]("ExampleView")
    .initialState(State(false))
    .renderBackend[Backend]
    .build

  def apply(P: Props) =
    component(P)
}
