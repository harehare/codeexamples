package com.codeexamples.domain.service

import com.codeexamples.domain.model.Example
import com.codeexamples.domain.repository.ExampleRepository

class ExampleService(repository: ExampleRepository) {

  def findByCodeId(codeId: String) =
    repository.findByCodeId(codeId)

  def save(example: Example) =
    repository.save(example)

  def remove(example: Example) =
    repository.remove(example)
}
