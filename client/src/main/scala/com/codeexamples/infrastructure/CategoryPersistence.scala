package com.codeexamples.infrastructure

import com.codeexamples.domain.model.Category
import com.codeexamples.domain.repository.CategoryRepository
import com.codeexamples.facade.Firebase

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CategoryPersistenceFirestore extends CategoryRepository {
  override def fetchAll(): Future[Either[Throwable, List[Category]]] = {
    new Firebase()
      .fetchCategories()
      .toFuture
      .map(
        categories =>
          Right(
            categories
              .map(
                category => Category(category.id, category.icon, category.name, category.updatedAt)
              )
              .toList
          )
      )
      .recover(e => Left(e))
  }
}
