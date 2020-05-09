package com.codeexamples.domain.repository

import com.codeexamples.domain.model.Example

import scala.concurrent.Future

trait ExampleRepository {
  def findByCodeId(codeId: String): Future[Either[Throwable, List[Example]]]
  def save(example: Example): Future[Either[Throwable, Example]]
  def remove(example: Example): Future[Either[Throwable, String]]
}
