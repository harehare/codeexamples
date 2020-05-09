package com.codeexamples.domain.repository

import com.codeexamples.domain.model.Code

import scala.concurrent.Future

trait CodeRepository {
  def fetchCodes(categoryId: String): Future[Either[Throwable, List[Code]]]
  def fetchCode(categoryId: String, codeId: String): Future[Either[Throwable, Code]]
  def save(code: Code): Future[Either[Throwable, Code]]
  def remove(code: Code): Future[Either[Throwable, String]]
}
