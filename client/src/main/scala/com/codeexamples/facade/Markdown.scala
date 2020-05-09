package com.codeexamples.facade

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSImport, JSName, ScalaJSDefined}

@js.native
@JSImport("marked", JSImport.Namespace)
object Marked extends Marked

@js.native
trait Marked extends js.Object {

  def apply(markdownString: String, options: js.Object = ???, callback: js.Function = ???): String =
    js.native

  def setOptions(options: js.Object): Unit = js.native
}

@ScalaJSDefined
class MarkedOptions extends js.Object {
  val highlight: js.Any = js.undefined
}

object MarkedOptions {

  import scala.scalajs.js.UndefOr

  def apply(highlight: UndefOr[(String, UndefOr[String], js.Function) ⇒ String]): MarkedOptions = {
    val _highlight = highlight
    new MarkedOptions {
      override val highlight: js.Any = _highlight
    }
  }
}

@js.native
@JSImport("highlight.js", JSImport.Namespace)
object HighlightJS extends HighlightJS

@js.native
trait HighlightJS extends js.Object {
  def highlight(
      name: String,
      value: String,
      ignoreIllegals: Boolean = ???,
      continuation: js.Object = ???
  ): HighlightJSResult = js.native
  def highlightAuto(value: String, languageSubset: js.Array[String] = ???): HighlightJSResult =
    js.native
}

@js.native
trait HighlightJSResult extends js.Object {
  def language: String = js.native
  def relevance: Int   = js.native
  def value: String    = js.native
  def top: js.Object   = js.native
  @JSName("second_best")
  def secondBest: js.Object = js.native
}
