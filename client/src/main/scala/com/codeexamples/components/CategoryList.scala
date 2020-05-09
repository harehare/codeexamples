package com.codeexamples.components

import com.codeexamples.domain.model.Category
import diode.data.{Failed, Pending, Pot, Ready}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object CategoryList {

  case class Props(
      categoriesResult: Pot[List[Category]],
      selectedCategoryId: Option[String],
      onClick: Category => Callback
  )

  implicit val propsReuse = Reusability[Props](
    (p1, p2) =>
      p1.categoriesResult.getOrElse(Nil) == p2.categoriesResult
        .getOrElse(Nil) && p1.selectedCategoryId.getOrElse("") == p2.selectedCategoryId.getOrElse(
        ""
      )
  )

  class Backend($ : BackendScope[Props, Unit]) {
    def render(p: Props): VdomElement = {
      p.categoriesResult match {
        case Ready(categories) =>
          <.div(
            ^.id := "category-list",
            <.div(
              ^.className := "main",
              <.div(^.className := "label", ^.marginBottom := "8px", "CATEGORY"),
              <.nav(
                ^.className := "list",
                <.div(
                  categories.toTagMod(
                    category =>
                      <.div(
                        CategoryView(
                          CategoryView
                            .Props(
                              category = category,
                              onClick = p.onClick(category),
                              isSelected =
                                p.selectedCategoryId.map(id => id == category.id).getOrElse(false)
                            )
                        )
                      )
                  )
                )
              )
            )
          )
        case Failed(e)  => <.div("Error")
        case Pending(_) => <.div("Loading")
        case _          => <.div()
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
