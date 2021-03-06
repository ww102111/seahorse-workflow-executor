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

import scala.collection.immutable.ListMap

/**
 * Node in DOperationCategoryTree.
 * Represents certain category, holds its subcategories and assigned operations.
 * Objects of this class are immutable.
 * @param category category represented by this node or None if it is root
 * @param successors map from all direct child-categories to nodes representing them
 * @param operations operations directly in category represented by this node
 */
case class DOperationCategoryNode(
    category: Option[DOperationCategory] = None,
    successors: ListMap[DOperationCategory, DOperationCategoryNode] = ListMap.empty,
    operations: List[DOperationDescriptor] = List.empty) {

  /**
   * Adds operation to node under given path of categories.
   * @param operation descriptor of operation to be added
   * @param path requested path of categories from this node to added operation
   * @return node identical to this but with operation added
   */
  private def addOperationAtPath(
      operation: DOperationDescriptor,
      path: List[DOperationCategory]): DOperationCategoryNode = {
    path match {
      case Nil => copy(operations = operations :+ operation)
      case category :: tail =>
        val successor = successors.getOrElse(category, DOperationCategoryNode(Some(category)))
        val updatedSuccessor = successor.addOperationAtPath(operation, tail)
        copy(successors = successors + (category -> updatedSuccessor))
    }
  }

  /**
   * Adds a new DOperation to the tree represented by this node under a specified category.
   * @param operation operation descriptor to be added
   * @param category category under which operation should directly be
   * @return category tree identical to this but with operation added
   */
  def addOperation(
      operation: DOperationDescriptor,
      category: DOperationCategory) : DOperationCategoryNode = {
    addOperationAtPath(operation, category.pathFromRoot)
  }
}
