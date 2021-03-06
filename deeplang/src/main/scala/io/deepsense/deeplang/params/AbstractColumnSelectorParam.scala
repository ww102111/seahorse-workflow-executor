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

package io.deepsense.deeplang.params

import spray.json.DefaultJsonProtocol._
import spray.json._

abstract class AbstractColumnSelectorParam[T: JsonFormat] extends ParamWithJsFormat[T] {

  /** Tells if this selectors selects single column or many. */
  protected val isSingle: Boolean

  /** Input port index of the data source. */
  protected val portIndex: Int

  override protected def extraJsFields: Map[String, JsValue] =
    super.extraJsFields ++ Map("isSingle" -> isSingle.toJson, "portIndex" -> portIndex.toJson)
}
