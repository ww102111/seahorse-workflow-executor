/**
 * Copyright 2015, deepsense.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.deepsense.deeplang.catalogs.doperations

import java.lang.reflect.Constructor

import scala.collection.mutable
import scala.reflect.runtime.{universe => ru}

import io.deepsense.deeplang.catalogs.doperations.exceptions._
import io.deepsense.deeplang.{DOperation, TypeUtils}

/**
 * Catalog of DOperations.
 * Allows to register DOperations under specified categories,
 * to browse categories structure and to create operations instances.
 */
abstract class DOperationsCatalog {
  /** Tree describing categories structure. */
  def categoryTree: DOperationCategoryNode

  /** Map of all registered operation descriptors, where their ids are keys. */
  def operations: Map[DOperation.Id, DOperationDescriptor]

  /**
   * Creates instance of requested DOperation class.
   * @param id id that identifies desired DOperation
   */
  def createDOperation(id: DOperation.Id): DOperation

  /**
   * Registers DOperation, which can be later viewed and created.
   * @param category category to which this operation directly belongs
   * @param visible a flag determining if the operation should be visible in the category tree
   * @tparam T DOperation class to register
   */
  def registerDOperation(category: DOperationCategory, factory: () => DOperation, visible: Boolean = true): Unit
}

object DOperationsCatalog {
  def apply(): DOperationsCatalog = new DOperationsCatalogImpl

  private class DOperationsCatalogImpl() extends DOperationsCatalog {
    var categoryTree = DOperationCategoryNode()
    var operations = Map.empty[DOperation.Id, DOperationDescriptor]
    private val operationFactoryByOperationId = mutable.Map.empty[DOperation.Id, () => DOperation]

    def registerDOperation(category: DOperationCategory, factory: () => DOperation, visible: Boolean = true): Unit = {
      val operationInstance = factory()
      operationInstance.validate()
      val id = operationInstance.id
      val name = operationInstance.name
      val description = operationInstance.description
      val inPortTypes = operationInstance.inPortTypes.map(_.tpe)
      val outPortTypes = operationInstance.outPortTypes.map(_.tpe)
      val parameterDescription = operationInstance.paramsToJson
      val operationDescriptor = DOperationDescriptor(
        id, name, description, category, operationInstance.hasDocumentation, parameterDescription, inPortTypes,
        operationInstance.inPortsLayout, outPortTypes, operationInstance.outPortsLayout
      )

      if (operations.contains(id)) {
        val alreadyRegisteredOperation = operations(id)
        throw new RuntimeException(
          s"Trying to register operation '$name' with UUID $id, " +
          s"but there is already operation '${alreadyRegisteredOperation.name}' with the same UUID value. " +
          s"Please change UUID of one of them.")
      }
      operations += id -> operationDescriptor
      if(visible) {
        categoryTree = categoryTree.addOperation(operationDescriptor, category)
      }
      operationFactoryByOperationId(id) = factory
    }

    def createDOperation(id: DOperation.Id): DOperation = operationFactoryByOperationId.get(id) match {
      case Some(factory) => factory()
      case None => throw DOperationNotFoundException(id)
    }
  }
}
