package com.codeexamples.domain.service

import com.codeexamples.domain.model.Code
import com.codeexamples.domain.repository.CodeRepository

import scala.collection.mutable.Map
import scala.concurrent.Future

class CodeService(repository: CodeRepository) {

  val cache: Map[String, Future[Either[Throwable, Code]]] =
    Map.empty

  def codes(categoryId: String) =
    repository.fetchCodes(categoryId)

  def code(categoryId: String, codeId: String) = {
    val key = s"${categoryId}-${codeId}"
    cache.get(key) match {
      case Some(c) => c
      case None => {
        val ret = repository.fetchCode(categoryId, codeId)
        cache.put(key, ret)
        ret
      }
    }
  }

  def add(code: Code) =
    repository.save(code)

  def update(code: Code) = {
    val key = s"${code.categoryId}-${code.id}"
    if (cache.get(key).isDefined)
      cache.remove(key)
    repository.save(code)
  }

  def remove(code: Code) = {
    repository.remove(code)
  }
}
