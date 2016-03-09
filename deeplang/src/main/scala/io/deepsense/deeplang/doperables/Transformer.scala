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

import org.apache.hadoop.fs.Path
import org.apache.spark.sql.types.StructType

import io.deepsense.commons.utils.Logging
import io.deepsense.deeplang._
import io.deepsense.deeplang.doperables.dataframe.DataFrame
import io.deepsense.deeplang.doperables.report.{CommonTablesGenerators, Report}
import io.deepsense.deeplang.doperables.serialization.{Loadable, ParamsSerialization}
import io.deepsense.deeplang.inference.{InferContext, InferenceWarnings}
import io.deepsense.deeplang.params.Params
import io.deepsense.reportlib.model.ReportType

/**
 * Able to transform a DataFrame into another DataFrame.
 * Can have mutable parameters.
 */
abstract class Transformer
    extends DOperable
    with Params
    with Logging
    with ParamsSerialization
    with Loadable {

  /**
   * Creates a transformed DataFrame based on input DataFrame.
   */
  private[deeplang] def _transform(ctx: ExecutionContext, df: DataFrame): DataFrame

  /**
   * Should be implemented in subclasses.
   * For known schema of input DataFrame, infers schema of output DataFrame.
   * If it is not able to do it for some reasons, it returns None.
   */
  private[deeplang] def _transformSchema(schema: StructType): Option[StructType] = None

  /**
    * Can be implemented in a subclass, if access to infer context is required.
    * For known schema of input DataFrame, infers schema of output DataFrame.
    * If it is not able to do it for some reasons, it returns None.
    */
  private[deeplang] def _transformSchema(
      schema: StructType,
      inferContext: InferContext): Option[StructType] = _transformSchema(schema)

  def transform: DMethod1To1[Unit, DataFrame, DataFrame] = {
    new DMethod1To1[Unit, DataFrame, DataFrame] {
      override def apply(ctx: ExecutionContext)(p: Unit)(df: DataFrame): DataFrame = {
        _transform(ctx, df)
      }

      override def infer(
        ctx: InferContext)(
        p: Unit)(
        k: DKnowledge[DataFrame]): (DKnowledge[DataFrame], InferenceWarnings) = {
        val df = DataFrame.forInference(k.single.schema.flatMap(s => _transformSchema(s, ctx)))
        (DKnowledge(df), InferenceWarnings.empty)
      }
    }
  }

  override def report: Report =
    super.report
      .withReportName(s"${this.getClass.getSimpleName} Report")
      .withReportType(ReportType.Model)
      .withAdditionalTable(CommonTablesGenerators.params(extractParamMap()))

  def save(ctx: ExecutionContext, path: String): Unit = {
    saveObjectWithParams(ctx, path)
    saveTransformer(ctx, path)
  }

  override def load(ctx: ExecutionContext, path: String): Unit = {
    loadObjectWithParams(ctx, path)
    loadTransformer(ctx, path)
  }

  protected def saveTransformer(ctx: ExecutionContext, path: String): Unit = {}

  protected def loadTransformer(ctx: ExecutionContext, path: String): Unit = {}
}

object Transformer {

  private val modelFilePath = "deepsenseModel"
  private val transformerFilePath = "deepsenseTransformer"
  private val parentEstimatorFilePath = "deepsenseParentEstimator"
  private val pipelineFilePath = "deepsensePipeline"
  private val wrappedModelFilePath = "deepsenseWrappedModel"

  def load(ctx: ExecutionContext, path: String): Transformer = {
    ParamsSerialization.load(ctx, path).asInstanceOf[Transformer]
  }

  def modelFilePath(path: String): String = {
    combinePaths(path, modelFilePath)
  }

  def parentEstimatorFilePath(path: String): String = {
    combinePaths(path, parentEstimatorFilePath)
  }

  def stringIndexerPipelineFilePath(path: String): String = {
    combinePaths(modelFilePath(path), pipelineFilePath)
  }

  def stringIndexerWrappedModelFilePath(path: String): String = {
    combinePaths(modelFilePath(path), wrappedModelFilePath)
  }

  def transformerSparkTransformerFilePath(path: String): String = {
    combinePaths(path, transformerFilePath)
  }

  private def combinePaths(path1: String, path2: String): String = {
    new Path(path1, path2).toString
  }
}
