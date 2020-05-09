package com.codeexamples.domain.repository

import com.codeexamples.domain.model.Category

import scala.concurrent.Future

trait CategoryRepository {
  def fetchAll(): Future[Either[Throwable, List[Category]]]
}
