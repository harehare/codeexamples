package com.codeexamples.components

import com.codeexamples.domain.model._
import fontAwesome._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import mouse.option._

import scala.math.max

object CodeDetailView {

  case class Props(
      session: Session,
      code: Code,
      examples: Examples,
      edit: Code => Callback,
      update: Code => Callback,
      remove: Code => Callback,
      addExample: (String, User) => Callback,
      editExample: Example => Callback,
      saveExample: Example => Callback,
      removeExample: Example => Callback,
      cancelExample: Callback,
      loadExample: String => Callback
  )

  case class State(isModified: Boolean, descriptionHeight: Int)

  class Backend($ : BackendScope[Props, State]) {
    def render(p: Props, s: State): VdomElement = {
      def onUpdate: ReactEventFromInput => Callback = { e =>
        if (s.isModified) {
          $.modState(_.copy(isModified = false)) >> p.update(p.code)
        } else {
          $.modState(_.copy(isModified = false))
        }
      }

      def onChangeTitle: ReactEventFromTextArea => Callback = { e =>
        val newValue = e.target.value
        $.modState(_.copy(isModified = true)) >>
          p.edit(p.code.copy(name = newValue))
      }

      def onChangeDescription: ReactEventFromInput => Callback = { e =>
        val newValue = e.target.value
        $.modState(
          _.copy(
            isModified = true,
            descriptionHeight = max(newValue.split("\n").length * 18, 5)
          )
        ) >>
          p.edit(p.code.copy(description = newValue))
      }

      def onScroll: ReactUIEvent => Callback = { e =>
        if (p.examples.hasNext) {
          val node = e.target.domAsHtml
          if (node.scrollHeight - node.scrollTop - 20 < node.clientHeight) {
            p.loadExample(p.code.id)
          } else {
            Callback.empty
          }
        } else {
          Callback.empty
        }
      }

      <.div(
        ^.id := "detail",
        <.div(
          ^.className := "main",
          ^.onScroll ==> onScroll,
          p.session.user.cata(
            user =>
              if (user.id == p.code.userId)
                <.div(
                  ^.className := "title",
                  <.input(
                    ^.id := "example-title",
                    ^.className := "input",
                    ^.placeholder := "TITLE",
                    ^.value := p.code.name,
                    ^.onChange ==> onChangeTitle,
                    ^.onBlur ==> onUpdate
                  ),
                  if (p.examples.examples.map(v => v.length).getOrElse(0) == 0)
                    <.div(
                      ^.className := "icon",
                      ^.dangerouslySetInnerHtml := fontawesome
                        .icon(freeSolid.faTrash)
                        .html
                        .join(""),
                      ^.onClick --> p.remove(p.code)
                    )
                  else
                    <.div()
                )
              else <.div(^.className := "title", <.div(^.className := "input", p.code.name)),
            <.div(^.className := "title", <.div(^.className := "input", p.code.name))
          ),
          <.div(
            ^.className := "description",
            ^.height := s"${s.descriptionHeight + 24}px",
            p.session.user.cata(
              user =>
                if (user.id == p.code.userId)
                  <.textarea(
                    ^.className := "textarea",
                    ^.placeholder := "DESCRIPTION",
                    ^.onChange ==> onChangeDescription,
                    ^.onBlur ==> onUpdate,
                    ^.height := s"${s.descriptionHeight}px",
                    ^.value := p.code.description
                  )
                else <.div(^.className := "textarea", p.code.description),
              <.div(^.className := "textarea", p.code.description)
            )
          ),
          <.div(
            ExampleList(
              ExampleList.Props(
                session = p.session,
                code = p.code,
                examples = p.examples,
                add = p.addExample,
                edit = p.editExample,
                save = p.saveExample,
                remove = p.removeExample,
                cancel = p.cancelExample
              )
            )
          )
        )
      )
    }
  }

  val component = ScalaComponent
    .builder[Props]("CodeDetail")
    .initialState(State(false, 50))
    .renderBackend[Backend]
    .componentDidMount(
      $ =>
        $.modState(
          _.copy(
            descriptionHeight =
              max($.props.code.description.split("\n").length * 18, $.state.descriptionHeight)
          )
        )
    )
    .build

  val notSelectedView =
    ScalaComponent.static("NotSelectedView")(<.div(^.className := "center", "NOT SELECTED"))

  def apply(P: Props) =
    component(P)
}
