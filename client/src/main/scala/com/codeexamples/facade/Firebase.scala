package com.codeexamples.facade

import scala.scalajs.js
import scala.scalajs.js.Promise
import scala.scalajs.js.annotation.JSGlobal

@js.native
trait User extends js.Object {
  var id: String          = js.native
  var displayName: String = js.native
  var photoUrl: String    = js.native
}

@js.native
trait Category extends js.Object {
  var id: String         = js.native
  var icon: String       = js.native
  var name: String       = js.native
  var updatedAt: js.Date = js.native
}

@js.native
trait Code extends js.Object {
  var id: String          = js.native
  var categoryId: String  = js.native
  var userId: String      = js.native
  var name: String        = js.native
  var description: String = js.native
  var updatedAt: js.Date  = js.native
}

@js.native
trait Example extends js.Object {
  var id: String            = js.native
  var codeId: String        = js.native
  var users: js.Array[User] = js.native
  var markdown: String      = js.native
  var updatedAt: js.Date    = js.native
}

@js.native
@JSGlobal("codeexamples.FirebaseFacade")
class Firebase extends js.Object {
  def signIn(): Promise[User]                                                          = js.native
  def signOut(): Promise[Unit]                                                         = js.native
  def onAuthStateChanged(callback: js.Function1[User, Unit]): js.Function1[Unit, Unit] = js.native
  def fetchCategories(): Promise[js.Array[Category]]                                   = js.native
  def fetchCodes(categoryId: String): Promise[js.Array[Code]]                          = js.native
  def fetchCode(categoryId: String, codeId: String): Promise[Code]                     = js.native
  def saveCode(data: Code): Promise[Code]                                              = js.native
  def removeCode(data: Code): Promise[Code]                                            = js.native
  def fetchExamples(codeId: String): Promise[js.Array[Example]]                        = js.native
  def saveExample(data: Example): Promise[Example]                                     = js.native
}
