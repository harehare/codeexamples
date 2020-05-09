package com.codeexamples

import com.codeexamples.domain.service.{CategoryService, CodeService, ExampleService, UserService}
import com.codeexamples.infrastructure.{
  CategoryPersistenceFirestore,
  CodePersistenceFirestore,
  ExamplePersistenceFirestore
}
import com.softwaremill.macwire._

trait AppModule {
  lazy val categoryRepository = wire[CategoryPersistenceFirestore]
  lazy val codeRepository     = wire[CodePersistenceFirestore]
  lazy val exampleRepository  = wire[ExamplePersistenceFirestore]
  lazy val categoryService    = wire[CategoryService]
  lazy val codeService        = wire[CodeService]
  lazy val userService        = wire[UserService]
  lazy val exampleService     = wire[ExampleService]
}
