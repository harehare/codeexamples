package com.codeexamples.domain.service

import com.codeexamples.domain.model.Category
import com.codeexamples.domain.repository.CategoryRepository
import mouse.option._

import scala.concurrent.Future
import scala.scalajs.js

class CategoryService(repository: CategoryRepository) {
  val defaultCategories = List(
    Category("89a59df2-32aa-4446-8010-59193b63041c", "android", "Android", new js.Date()),
    Category("20b87945-32b6-4695-8d11-75c704360f16", "angular", "Angular", new js.Date()),
    Category("2ad75411-8df8-4362-a674-e85637dc4dce", "aws", "AWS", new js.Date()),
    Category("dd650697-87ac-4cf3-a24f-6dedbc780869", "c", "C", new js.Date()),
    Category("39239270-2089-4ceb-8aa7-f06a70acc9c9", "clojure", "Clojure", new js.Date()),
    Category("2afb89cb-9cac-48c0-97dc-44d4637432fe", "csharp", "C#", new js.Date()),
    Category("d5804ea6-bd3f-44b0-baac-4deaac40a79f", "css3", "CSS", new js.Date()),
    Category("93a9e200-2e8f-436a-ba52-bff374fe5d1e", "docker", "Docker", new js.Date()),
    Category("60463300-4268-4cc4-8be9-67eda4e43b10", "elm", "Elm", new js.Date()),
    Category("a9a9e3b0-067f-427a-85c5-772a37d661ba", "firebase", "Firebase", new js.Date()),
    Category("997a3775-0002-4e29-a21b-c3c1343dbec4", "git", "git", new js.Date()),
    Category("f99ad6a7-052a-4ff1-89cd-04ed249f1f3e", "go", "Golang", new js.Date()),
    Category("2525e2a2-b4af-4d91-be3f-3d0fb49c73af", "haskell", "Haskell", new js.Date()),
    Category("6aacf9e5-5696-4ef5-8fda-619d4ed77118", "java", "Java", new js.Date()),
    Category("3038c58e-2a6b-4d7b-a9d8-d888c6f771da", "javascript", "JavaScript", new js.Date()),
    Category("bcfed125-f88f-4402-a607-347cbc010390", "mysql", "MySQL", new js.Date()),
    Category("b553ddb3-f0be-4809-ad65-446883cfe62f", "php", "PHP", new js.Date()),
    Category("0e3d5c34-700e-4ae6-aaf1-33ba2cac900f", "postgres", "PostgreSQL", new js.Date()),
    Category("7d237f9e-980a-41b1-818a-56f26192db3e", "python", "Python", new js.Date()),
    Category("dd73ef9c-6867-4de2-acb0-54ed288655bf", "react", "React", new js.Date()),
    Category("a6aa9d15-b82e-43f5-b533-25982e5fa247", "ruby", "Ruby", new js.Date()),
    Category("0a61b086-db6c-437e-9b9d-301ff70ce6c9", "rust", "Rust", new js.Date()),
    Category("17ef7c7c-b1b5-42b6-9201-c3f08e3f73fc", "scala", "Scala", new js.Date()),
    Category("a0e1e288-2fa5-417c-a19a-3788bf82ad1b", "typescript", "TypeScript", new js.Date()),
    Category("4e972b30-86ca-4e7b-a923-408ecdeca7e1", "vuejs", "Vue.js", new js.Date()),
    Category("c340336e-3cd2-4b4b-8642-d90915435bd8", "webpack", "Webpack", new js.Date())
  )

  def getCategoryName(categoryId: String) =
    defaultCategories.find(c => c.id == categoryId).cata(_.name, "")

  def categories(): Future[Either[Throwable, List[Category]]] =
    Future.successful(Right(defaultCategories))
}
