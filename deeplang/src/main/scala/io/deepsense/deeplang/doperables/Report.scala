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

package io.deepsense.deeplang.doperables

import io.deepsense.deeplang.{ExecutionContext, DOperable}
import io.deepsense.reportlib.model.ReportContent

case class Report(content: ReportContent = ReportContent("empty report"))
  extends DOperable {

  def this() = this(ReportContent("empty report"))

  override def toInferrable: DOperable = new Report()

  override def report(executionContext: ExecutionContext): Report = this

  override def save(context: ExecutionContext)(path: String): Unit = ???
}

/**
  * Level of details for generated reports
  */
object ReportLevel extends Enumeration {
  type ReportLevel = Value
  val HIGH, MEDIUM, LOW = Value
}
