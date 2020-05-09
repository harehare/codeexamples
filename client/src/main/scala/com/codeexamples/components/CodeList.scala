package com.codeexamples.components

import com.codeexamples.domain.model.{Code, Codes, Session, User}
import diode.data.{Pot, Failed, Pending, Ready}
import fontAwesome._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import mouse.option._

object CodeList {

  case class Props(
      session: Session,
      codes: Codes,
      selectedCategoryId: String,
      selectedCode: Option[Code],
      position: Double,
      onClick: Code => Callback,
      onAddCode: (String, String) => Callback,
      onLoad: String => Callback
  )

  implicit val propsReuse = Reusability[Props](
    (p1, p2) =>
      p1.codes.list == p2.codes.list
        && p1.selectedCategoryId == p2.selectedCategoryId
        && p1.selectedCode.getOrElse("") == p2.selectedCode.getOrElse("")
        && p1.position == p2.position
        && p1.session.user.cata(_.id, "") == p2.session.user.cata(_.id, "")
  )

  class Backend($ : BackendScope[Props, Unit]) {

    def render(p: Props): VdomElement = {

      val addCodeButton = (p.session.user, p.selectedCode) match {
        case (Some(user), Some(code)) =>
          <.div(
            if (code.isNew) ^.className := "disabled-icon" else ^.className := "icon",
            ^.dangerouslySetInnerHtml := fontawesome.icon(freeSolid.faPlus).html.join(""),
            ^.onClick --> (if (code.isNew) Callback.empty
                           else p.onAddCode(p.selectedCategoryId, user.id))
          )
        case (Some(user), _) =>
          <.div(
            ^.className := "icon",
            ^.dangerouslySetInnerHtml := fontawesome.icon(freeSolid.faPlus).html.join(""),
            ^.onClick --> p.onAddCode(p.selectedCategoryId, user.id)
          )
        case _ =>
          <.div()
      }

      def width() = s"${if (250 + p.position < 250) 250 else 250 + p.position}px"

      p.codes.list match {
        case Ready(codes) => {
          def onScroll: ReactUIEvent => Callback = { e =>
            if (p.codes.hasNext) {
              val node = e.target.domAsHtml
              if (node.scrollHeight - node.scrollTop - 10 < node.clientHeight) {
                p.onLoad(p.selectedCategoryId)
              } else
                Callback.empty
            } else {
              Callback.empty
            }
          }

          <.div(
            ^.id := "code-list",
            ^.width := width(),
            <.div(
              ^.className := "header",
              <.div(^.className := "label", "ALL EXMAPLES"),
              addCodeButton
            ),
            <.div(
              ^.className := "main",
              ^.onScroll ==> onScroll,
              if (codes.isEmpty)
                // TODO:
                <.div(^.className := "center", "EMPTY")
              else
                codes.toTagMod(
                  code =>
                    <.div(
                      CodeView(
                        CodeView.Props(
                          code = code,
                          onClick = p.onClick(code),
                          isSelected = p.selectedCode.map(s => s.id == code.id).getOrElse(false)
                        )
                      )
                    )
                ),
              if (p.codes.hasNext)
                <.div(
                  ^.className := "item",
                  ^.alignItems := "center",
                  ^.justifyContent := "center",
                  ^.onClick --> p.onLoad(p.selectedCategoryId),
                  "LOAD MORE"
                )
              else if (p.codes.isLoading)
                <.div(
                  ^.className := "item",
                  ^.alignItems := "center",
                  ^.justifyContent := "center",
                  "LOADING"
                )
              else <.div()
            )
          )
        }
        case Failed(e) => <.div(^.className := "center", "ERROR")
        case Pending(_) =>
          <.div(
            ^.id := "code-list",
            ^.width := width(),
            <.div(
              ^.className := "header",
              <.div(^.className := "label", "ALL EXMAPLES"),
              addCodeButton
            ),
            <.div(
              ^.className := "main",
              0.to(14)
                .toTagMod(
                  _ =>
                    <.div(
                      ^.className := "item",
                      <.div(^.className := "title title-loading shimmer"),
                      <.div(^.className := "subtitle subtitle-loading shimmer")
                    )
                )
            )
          )
        case _ => <.div(^.className := "main")
      }
    }
  }

  val component = ScalaComponent
    .builder[Props]("CategoryView")
    .renderBackend[Backend]
    .configure(Reusability.shouldComponentUpdate)
    .build

  def apply(P: Props) =
    component(P)
}
