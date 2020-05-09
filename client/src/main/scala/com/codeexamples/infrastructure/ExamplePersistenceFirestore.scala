package com.codeexamples.infrastructure

import com.codeexamples.domain.model.{Example, User}
import com.codeexamples.domain.repository.ExampleRepository
import com.codeexamples.facade.{Firebase, Example => JsExample, User => JsUser}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js

class ExamplePersistenceFirestore extends ExampleRepository {

  val firebase = new Firebase()

  override def findByCodeId(codeId: String): Future[Either[Throwable, List[Example]]] =
    firebase
      .fetchExamples(codeId)
      .toFuture
      .map(
        examples =>
          Right(
            examples
              .map(
                example =>
                  Example(
                    example.id,
                    example.codeId,
                    example.users.map(u => new User(u.id, u.displayName, u.photoUrl)).toList,
                    example.markdown,
                    example.updatedAt
                  )
              )
              .toList
          )
      )
      .recover(e => Left(e))

  override def save(
      example: Example
  ): Future[Either[Throwable, Example]] =
    firebase
      .saveExample(
        js.Dynamic
          .literal(
            id = example.id,
            codeId = example.codeId,
            users = js.Array(
              example.users
                .map(
                  u =>
                    js.Dynamic
                      .literal(
                        id = u.id,
                        displayName = u.displayName,
                        photoUrl = u.photoUrl
                      )
                      .asInstanceOf[JsUser]
                ): _*
            ),
            markdown = example.markdown,
            updatedAt = new js.Date()
          )
          .asInstanceOf[JsExample]
      )
      .toFuture
      .map(
        example =>
          Right(
            Example(
              example.id,
              example.codeId,
              example.users.map(u => new User(u.id, u.displayName, u.photoUrl)).toList,
              example.markdown,
              example.updatedAt
            )
          )
      )
      .recover(e => Left(e))

  override def remove(
      example: Example
  ): Future[Either[Throwable, String]] =
    firebase
      .saveExample(
        js.Dynamic
          .literal(
            id = example.id,
            codeId = example.codeId,
            users = js.Array(
              example.users
                .map(
                  u =>
                    js.Dynamic
                      .literal(
                        id = u.id,
                        displayName = u.displayName,
                        photoUrl = u.photoUrl
                      )
                      .asInstanceOf[JsUser]
                ): _*
            ),
            markdown = example.markdown,
            updatedAt = new js.Date()
          )
          .asInstanceOf[JsExample]
      )
      .toFuture
      .map(
        example => Right(example.id)
      )
      .recover(e => Left(e))
}
