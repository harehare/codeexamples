package com.codeexamples

import java.util.UUID

import com.codeexamples.components._
import com.codeexamples.domain.model.{Model, User}
import com.codeexamples.facade.Firebase
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.{BaseUrl, Resolution, Router, RouterConfigDsl, RouterCtl}
import japgolly.scalajs.react.vdom.html_<^._
import java.net.URLDecoder
import org.scalajs.dom
import mouse.option._

sealed trait Page
case object HomePage                                                  extends Page
case class CategoryPage(categoryId: UUID)                             extends Page
case class CodePage(categoryId: UUID, codeId: UUID, codeName: String) extends Page

object App extends AppModule {
  private val categoryConnection = AppCircuit.connect(_.categories)
  private val windowConnection   = AppCircuit.connect(_.window)
  private val modelConnection =
    AppCircuit.connect(v => Model(v.session, v.categories, v.codes, v.examples, v.window))

  private val homePage = (ctl: RouterCtl[Page]) =>
    <.div(
      ^.className := "content",
      categoryList(None, ctl),
      CodeDetailView.notSelectedView()
    )

  private val categoryPage = (page: CategoryPage, ctl: RouterCtl[Page]) =>
    <.div(
      ^.className := "content",
      categoryList(Some(page.categoryId.toString), ctl),
      codeList(page.categoryId.toString, ctl),
      separator,
      codeDetail
    )

  private val codePage = (page: CodePage, ctl: RouterCtl[Page]) =>
    <.div(
      ^.className := "content",
      categoryList(Some(page.categoryId.toString), ctl),
      codeList(page.categoryId.toString, ctl),
      separator,
      codeDetail
    )

  private val categoryList = (categoryId: Option[String], ctl: RouterCtl[Page]) =>
    categoryConnection(p => {
      val proxy = p()
      CategoryList(
        CategoryList
          .Props(
            proxy.list,
            categoryId,
            c => ctl.set(CategoryPage(UUID.fromString(c.id)))
          )
      )
    })

  private val codeList = (categoryId: String, ctl: RouterCtl[Page]) =>
    modelConnection(p => {
      val proxy = p()
      CodeList(
        CodeList
          .Props(
            proxy.session,
            proxy.codes,
            categoryId,
            proxy.codes.selectedCode,
            if (proxy.window.isResize) proxy.window.moveX else proxy.window.x,
            c => ctl.set(CodePage(UUID.fromString(c.categoryId), UUID.fromString(c.id), c.name)),
            (categoryId, userId) => p.dispatchCB(AddCode(categoryId, userId)),
            categoryId => p.dispatchCB(LoadNextCodes(categoryId))
          )
      )
    })

  private val separator = windowConnection(p => Separator(x => p.dispatchCB(StartResize(x))))

  private val header =
    modelConnection(p => {
      val proxy = p()
      Header(
        Header.Props(
          proxy.session,
          proxy.codes.isLoading,
          p.dispatchCB(SignIn),
          p.dispatchCB(SignOut)
        )
      )
    })

  private val codeDetail =
    modelConnection(p => {
      val proxy = p()
      proxy.codes.selectedCode.cata[VdomElement](
        c =>
          CodeDetailView(
            CodeDetailView
              .Props(
                proxy.session,
                c,
                proxy.examples,
                code => p.dispatchCB(EditCode(code)),
                code => p.dispatchCB(UpdateCode(code)),
                code => p.dispatchCB(RemoveCode(code)),
                (codeId, user) => p.dispatchCB(AddExample(codeId, user)),
                example => p.dispatchCB(EditExample(example)),
                example => p.dispatchCB(UpdateExample(example)),
                example => p.dispatchCB(RemoveExample(example)),
                p.dispatchCB(CancelEditExample),
                codeId => p.dispatchCB(LoadNextExamples(codeId))
              )
          ),
        CodeDetailView.notSelectedView()
      )
    })

  private val config = RouterConfigDsl[Page].buildConfig { dsl =>
    import dsl._
    import japgolly.scalajs.react.extra.router.SetRouteVia
    (emptyRule
      | staticRoute(root, HomePage) ~> renderR(homePage(_))
      | dynamicRouteCT(
        ("category" / uuid)
          .caseClass[CategoryPage]
      ) ~> dynRenderR[CategoryPage, VdomElement]((page, ctl) => categoryPage(page, ctl))
      | dynamicRouteCT(
        ("category" / uuid / "code" / uuid / remainingPathOrBlank)
          .caseClass[CodePage]
      ) ~> dynRenderR[CodePage, VdomElement]((page, ctl) => codePage(page, ctl)))
      .notFound(redirectToPage(HomePage)(SetRouteVia.HistoryReplace))
      .setTitle(
        p =>
          p match {
            case CategoryPage(categoryId) =>
              s"${categoryService.getCategoryName(categoryId.toString())} | CODE EXAMPLES"
            case CodePage(categoryId, codeId, codeName) =>
              s"${categoryService.getCategoryName(categoryId.toString())} | ${URLDecoder.decode(codeName, "utf-8")} | CODE EXAMPLES"
            case _ => "CODE EXAMPLES"
          }
      )
      .renderWith(layout)

  }

  private def layout(ctl: RouterCtl[Page], r: Resolution[Page]) = {
    AppCircuit.dispatch(r.page match {
      case CategoryPage(categoryId)        => LoadCategory(Some(categoryId.toString))
      case CodePage(categoryId, codeId, _) => LoadCode(categoryId.toString, codeId.toString)
      case HomePage                        => NoOp
    })
    windowConnection(
      p => {
        val proxy = p()
        <.div(
          ^.className := "container",
          ^.cursor := (if (proxy.isResize) "col-resize" else "default"),
          header,
          ^.onMouseMove ==> (
              e =>
                if (proxy.isResize) p.dispatchCB(Resize(e.pageX))
                else Callback.empty
            ),
          ^.onMouseUp ==> (
              e => (if (proxy.isResize) p.dispatchCB(EndResize(e.pageX)) else Callback.empty)
          ),
          r.render()
        )
      }
    )
  }

  def main(args: Array[String]): Unit = {
    AppCircuit.dispatch(LoadCategories)
    new Firebase().onAuthStateChanged(
      (user) => AppCircuit.dispatch(SignInSuccess(User(user.id, user.displayName, user.photoUrl)))
    )

    val router = Router(BaseUrl.fromWindowOrigin_/, config)
    router().renderIntoDOM(dom.document.getElementById("main"))
  }
}
