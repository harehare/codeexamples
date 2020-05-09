package com.codeexamples.infrastructure

import com.codeexamples.domain.model.Code
import com.codeexamples.domain.repository.CodeRepository
import com.codeexamples.facade.{Firebase, Code => JsCode}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js

class CodePersistenceFirestore extends CodeRepository {

  val firebase = new Firebase()

  override def fetchCodes(categoryId: String): Future[Either[Throwable, List[Code]]] =
    firebase
      .fetchCodes(categoryId)
      .toFuture
      .map(
        codes =>
          Right(
            codes
              .map(
                code =>
                  Code(
                    code.id,
                    code.categoryId,
                    code.userId,
                    code.name,
                    code.description,
                    code.updatedAt
                  )
              )
              .toList
          )
      )
      .recover(e => Left(e))

  override def fetchCode(categoryId: String, codeId: String): Future[Either[Throwable, Code]] =
    firebase
      .fetchCode(categoryId, codeId)
      .toFuture
      .map(
        code =>
          Right(
            Code(
              code.id,
              code.categoryId,
              code.userId,
              code.name,
              code.description,
              code.updatedAt
            )
          )
      )
      .recover(e => Left(e))

  override def save(
      code: Code
  ): Future[Either[Throwable, Code]] =
    firebase
      .saveCode(
        js.Dynamic
          .literal(
            id = code.id,
            categoryId = code.categoryId,
            userId = code.userId,
            name = code.name,
            description = code.description,
            updatedAt = null
          )
          .asInstanceOf[JsCode]
      )
      .toFuture
      .map(
        code =>
          Right(
            Code(
              code.id,
              code.categoryId,
              code.userId,
              code.name,
              code.description,
              null
            )
          )
      )
      .recover(e => Left(e))

  def remove(code: Code): Future[Either[Throwable, String]] =
    firebase
      .removeCode(
        js.Dynamic
          .literal(
            id = code.id,
            categoryId = code.categoryId,
            userId = code.userId,
            name = code.name,
            description = code.description,
            null
          )
          .asInstanceOf[JsCode]
      )
      .toFuture
      .map(code => Right(code.id))
      .recover(e => Left(e))
}
