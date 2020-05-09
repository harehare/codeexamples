package com.codeexamples.domain.service

import com.codeexamples.domain.model.User
import com.codeexamples.facade.Firebase

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserService() {

  def signIn(): Future[User] = {
    new Firebase()
      .signIn()
      .toFuture
      .map(
        user => User(user.id, user.displayName, user.photoUrl)
      )
  }
  def signOut() =
    new Firebase()
      .signOut()
      .toFuture
}
