package com.codeexamples

import com.codeexamples.domain.model.{Category, Code, Example, User}
import diode.Action

case object NoOp                 extends Action
case class Search(query: String) extends Action

case object LoadCategories                                                        extends Action
case class LoadCompletedCategories(categories: Either[Throwable, List[Category]]) extends Action
case class LoadCategory(category: Option[String])                                 extends Action
case class SelectCategory(category: String)                                       extends Action

case class LoadCode(categoryId: String, codeId: String)                 extends Action
case class LoadCompletedCode(codes: Either[Throwable, Code])            extends Action
case class LoadCodes(categoryId: String)                                extends Action
case class LoadCompletedCodes(codes: Either[Throwable, List[Code]])     extends Action
case class LoadNextCodes(categoryId: String)                            extends Action
case class LoadNextCompletedCodes(codes: Either[Throwable, List[Code]]) extends Action
case class EditCode(code: Code)                                         extends Action
case class AddCode(categoryId: String, userId: String)                  extends Action
case class UpdateCode(code: Code)                                       extends Action
case class UpdateCompletedCode(code: Either[Throwable, Code])           extends Action
case class RemoveCode(code: Code)                                       extends Action
case class RemoveCompletedCode(result: Either[Throwable, String])       extends Action

case class LoadExamples(codeId: String)                                          extends Action
case class LoadCompletedExamples(examples: Either[Throwable, List[Example]])     extends Action
case class LoadNextExamples(codeId: String)                                      extends Action
case class LoadNextCompletedExamples(examples: Either[Throwable, List[Example]]) extends Action
case class AddExample(codeId: String, user: User)                                extends Action
case class EditExample(example: Example)                                         extends Action
case object CancelEditExample                                                    extends Action
case class UpdateExample(example: Example)                                       extends Action
case class UpdateCompletedExample(example: Either[Throwable, Example])           extends Action
case class RemoveExample(example: Example)                                       extends Action
case class RemoveCompletedExample(example: Either[Throwable, String])            extends Action
case object ClearExamples                                                        extends Action

case object SignIn                   extends Action
case object SignOut                  extends Action
case class SignInSuccess(user: User) extends Action
case object SignInFailure            extends Action
case object SignOutSuccess           extends Action
case object SignOutFailure           extends Action

case class StartResize(position: Double) extends Action
case class Resize(position: Double)      extends Action
case class EndResize(position: Double)   extends Action
