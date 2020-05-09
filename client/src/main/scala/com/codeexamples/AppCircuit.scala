package com.codeexamples

import com.codeexamples.domain.model._
import diode._
import diode.data._
import diode.react.ReactConnector
import mouse.option._
import org.scalajs.dom
import org.scalajs.dom.html

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AppCircuit extends Circuit[Model] with ReactConnector[Model] with AppModule {

  val PAGE_PER_NUM = 15

  def signInEffect() =
    Effect(
      userService
        .signIn()
        .map(user => SignInSuccess(user))
    )

  def signOutEffect() =
    Effect(
      userService
        .signOut()
        .map(_ => SignOutSuccess)
    )

  def startLoadCodesEffect(categoryId: String) =
    Effect(Future { LoadCodes(categoryId) })

  def loadCategoriesEffect() =
    Effect(
      categoryService
        .categories()
        .map(categories => LoadCompletedCategories(categories))
    )

  def loadCodesEffect(categoryId: String) =
    Effect(
      codeService
        .codes(categoryId)
        .map(codes => LoadCompletedCodes(codes))
    )

  def loadCodeEffect(categoryId: String, codeId: String) =
    Effect(
      codeService
        .code(categoryId, codeId)
        .map(code => LoadCompletedCode(code))
    )

  def loadNextCodesEffect(categoryId: String) =
    Effect(
      codeService
        .codes(categoryId)
        .map(codes => LoadNextCompletedCodes(codes))
    )

  def startLoadExamplesEffect(codeId: String) =
    Effect(Future { LoadExamples(codeId) })

  def clearExamplesEffect() =
    Effect(Future { ClearExamples })

  def loadExamplesEffect(codeId: String) =
    Effect(
      exampleService
        .findByCodeId(codeId)
        .map(examples => LoadCompletedExamples(examples))
    )

  def loadNextExamplesEffect(codeId: String) =
    Effect(
      exampleService
        .findByCodeId(codeId)
        .map(examples => LoadNextCompletedExamples(examples))
    )

  def updateCodeEffect(code: Code) =
    Effect(
      codeService
        .update(code)
        .map(code => UpdateCompletedCode(code))
    )

  def removeCodeEffect(code: Code) =
    Effect(
      codeService
        .remove(code)
        .map(codeId => RemoveCompletedCode(codeId))
    )

  def updateExampleEffect(example: Example) =
    Effect(
      exampleService
        .save(example)
        .map(example => UpdateCompletedExample(example))
    )

  def removeExampleEffect(example: Example) =
    Effect(
      exampleService
        .remove(example)
        .map(exampleId => RemoveCompletedExample(exampleId))
    )

  def focusEffect(id: String) = {
    val elm = dom.document.getElementById(id)
    if (elm != null) {
      elm.asInstanceOf[html.Input].focus()
    }
    Effect(
      Future { NoOp }
    )
  }

  def selectCategoryEffect(categoryId: String) = {
    Effect(
      Future.successful(SelectCategory(categoryId))
    )
  }

  val categoryHandler = new ActionHandler(zoomTo(_.categories)) {
    override def handle = {
      case LoadCategories =>
        updated(Categories(Pot.empty[List[Category]], None), loadCategoriesEffect())

      case LoadCompletedCategories(Left(e)) =>
        updated(value.copy(list = Failed(e)))

      case LoadCompletedCategories(Right(categories)) =>
        updated(value.copy(list = Ready(categories)))

      case LoadCategory(categoryId) =>
        val category = value.list match {
          case Ready(list) =>
            list.find(l => Some(l.id.toLowerCase) == categoryId.map(_.toLowerCase))
          case _ => None
        }
        categoryId.cata(
          catId => updated(value.copy(selectedCategory = category), startLoadCodesEffect(catId)),
          updated(value.copy(selectedCategory = None))
        )

      case SelectCategory(categoryId) => {
        val category = value.list match {
          case Ready(list) =>
            list.find(l => l.id.toLowerCase == categoryId.toLowerCase)
          case _ => None
        }
        updated(value.copy(selectedCategory = category))
      }
    }
  }

  val codeHandler = new ActionHandler(zoomTo(_.codes)) {
    override def handle = {
      case NoOp =>
        noChange

      case LoadCode(categoryId, codeId) =>
        updated(
          value.copy(isLoading = true),
          loadCodeEffect(categoryId, codeId) >> startLoadExamplesEffect(codeId)
        )

      case LoadCompletedCode(Right(code)) => {
        val loadedCode = value.list match {
          case Ready(list) =>
            list.find(l => l.id.toLowerCase == code.id.toLowerCase)
          case _ => None
        }
        updated(
          value.copy(
            selectedCode = Some(code),
            list = loadedCode.cata(_ => value.list, Ready(code :: Nil)),
            isLoading = false
          ),
          selectCategoryEffect(code.categoryId)
        )
      }

      case LoadCompletedCode(Left(e)) =>
        updated(value.copy(list = Failed(e), isLoading = false))

      case LoadCodes(categoryId) =>
        updated(
          value.copy(list = Pending(), isLoading = true, hasNext = false),
          loadCodesEffect(categoryId)
        )

      case LoadCompletedCodes(Left(e)) =>
        updated(value.copy(list = Failed(e), isLoading = false, hasNext = false))

      case LoadCompletedCodes(Right(codes)) =>
        updated(
          value.copy(
            list = Ready(codes),
            selectedCode = None,
            isLoading = false,
            hasNext = codes.length >= PAGE_PER_NUM
          )
        )

      case LoadNextCodes(categoryId) =>
        if (value.hasNext && !value.isLoading)
          updated(value.copy(isLoading = true), loadNextCodesEffect(categoryId))
        else
          noChange

      case LoadNextCompletedCodes(Right(codes)) =>
        updated(
          value.copy(
            list = value.list.flatMap(l => Ready(l ++ codes)),
            isLoading = false,
            hasNext = !codes.isEmpty && codes.length >= PAGE_PER_NUM
          )
        )

      case LoadNextCompletedCodes(Left(e)) =>
        updated(value.copy(list = Failed(e), isLoading = false, hasNext = false))

      case AddCode(categoryId, userId) => {
        val newCode = Code.newCode(categoryId, userId);
        updated(
          value
            .copy(
              list = value.list.flatMap(list => Ready(newCode :: list)),
              selectedCode = Some(newCode)
            ),
          clearExamplesEffect() >> focusEffect("example-title")
        )
      }

      case UpdateCode(code) =>
        updated(value.copy(isLoading = true), updateCodeEffect(code))

      case RemoveCode(code) =>
        updated(value.copy(isLoading = true), removeCodeEffect(code))

      case RemoveCompletedCode(Right(codeId)) => {
        val list = value.list match {
          case Ready(list) => Ready(list.filter(l => l.id.toLowerCase != codeId.toLowerCase))
          case _           => Empty
        }
        updated(value.copy(list = list, isLoading = false, selectedCode = None))
      }

      case RemoveCompletedCode(Left(_)) =>
        updated(value.copy(isLoading = false))

      case EditCode(code) =>
        updated(value.copy(selectedCode = Some(code)))

      case UpdateCompletedCode(Left(e)) =>
        // TODO: update
        updated(value.copy(isLoading = false))

      case UpdateCompletedCode(Right(code)) => {
        val list = value.list match {
          case Ready(list) => list.map(l => if (l.id == code.id) code.copy(isNew = false) else l)
          case _           => List(code)
        }
        updated(value.copy(list = Ready(list), isLoading = false))
      }
    }
  }

  val sessionHandler = new ActionHandler(zoomTo(_.session)) {
    override def handle = {
      case SignIn =>
        effectOnly(signInEffect)

      case SignOut =>
        effectOnly(signOutEffect)

      case SignInSuccess(user) =>
        // TODO:
        updated(value.copy(user = Some(user)))

      case SignInFailure =>
        // TODO:
        updated(value.copy(user = None))

      case SignOutSuccess =>
        // TODO:
        updated(value.copy(user = None))

      case SignOutFailure =>
        // TODO:
        updated(value.copy(user = None))
    }
  }

  val exampleHandler = new ActionHandler(zoomTo(_.examples)) {
    override def handle = {
      case LoadExamples(codeId) =>
        updated(value.copy(examples = Pending()), loadExamplesEffect(codeId))

      case LoadCompletedExamples(Left(examples)) =>
        updated(value.copy(examples = Failed(examples)))

      case LoadCompletedExamples(Right(examples)) =>
        updated(
          value.copy(
            examples = Ready(examples),
            hasNext = !examples.isEmpty && examples.length >= PAGE_PER_NUM
          )
        )

      case LoadNextExamples(codeId) =>
        if (value.hasNext && !value.isLoading)
          updated(value.copy(isLoading = true), loadNextExamplesEffect(codeId))
        else
          noChange

      case LoadNextCompletedExamples(Left(examples)) =>
        updated(value.copy(isLoading = false, examples = Failed(examples), hasNext = false))

      case LoadNextCompletedExamples(Right(examples)) =>
        updated(
          value.copy(
            examples = value.examples.flatMap(l => Ready(l ++ examples)),
            isLoading = false,
            hasNext = !examples.isEmpty && examples.length >= PAGE_PER_NUM
          )
        )

      case AddExample(codeId, user) => {
        val newExample = Example.newExample(codeId, user)
        updated(
          value.copy(
            editExample = Some(newExample),
            examples = value.examples.flatMap(e => Ready(newExample :: e))
          ),
          focusEffect("example-edit")
        )
      }

      case EditExample(example) =>
        updated(value.copy(editExample = Some(example)))

      case CancelEditExample =>
        updated(value.copy(editExample = None))

      case UpdateExample(example) =>
        updated(value.copy(editExample = None), updateExampleEffect(example))

      case UpdateCompletedExample(Right(example)) =>
        updated(
          value.copy(
            editExample = None,
            examples = value.examples.flatMap(
              e => Ready(e.map(v => (if (v.id == example.id) example.copy(isNew = false) else v)))
            )
          )
        )

      case UpdateCompletedExample(Left(e)) =>
        updated(value.copy(editExample = None))

      case RemoveExample(example) =>
        effectOnly(removeExampleEffect(example))

      case RemoveCompletedExample(Right(exampleId)) =>
        val examples = value.examples match {
          case Ready(list) => Ready(list.filter(l => l.id.toLowerCase != exampleId.toLowerCase))
          case _           => Empty
        }
        updated(value.copy(examples = examples))

      case RemoveCompletedExample(Left(e)) =>
        // TODO: error
        noChange

      case ClearExamples =>
        updated(value.copy(examples = Empty, editExample = None))
    }
  }

  val windowHandler = new ActionHandler(zoomTo(_.window)) {
    override def handle = {
      case StartResize(pageX) =>
        updated(value.copy(position = pageX, moveX = value.x, isResize = true))

      case Resize(pageX) =>
        updated(value.copy(moveX = value.x + pageX - value.position))

      case EndResize(pageX) => {
        dom.window.localStorage.setItem(
          "codeexamples:position",
          (value.x + pageX - value.position).toString
        )
        updated(
          value.copy(
            x = value.x + pageX - value.position,
            position = 0,
            moveX = 0,
            isResize = false
          )
        )
      }
    }
  }

  override protected def initialModel = {
    val position = dom.window.localStorage.getItem("codeexamples:position")
    Model(
      Session(None),
      Categories(Empty, None),
      Codes(Empty, None),
      Examples(Empty, None),
      Window(if (position != null && !position.isEmpty()) position.toDouble else 0.0, 0, 0, false)
    )
  }

  override protected val actionHandler = composeHandlers(
    categoryHandler,
    codeHandler,
    exampleHandler,
    sessionHandler,
    windowHandler
  )
}
