/**
 * Copyright 2015, CodiLime Inc.
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

import org.apache.spark.mllib.tree.{RandomForest => SparkRandomForest}

import io.deepsense.deeplang._
import io.deepsense.deeplang.doperables.dataframe.DataFrame
import io.deepsense.deeplang.inference.{InferContext, InferenceWarnings}
import io.deepsense.reportlib.model.ReportContent

case class UntrainedRandomForestClassification(
    modelParameters: RandomForestParameters)
  extends RandomForestClassifier
  with Trainable
  with CategoricalFeaturesExtractor {

  def this() = this(null)

  override def toInferrable: DOperable = new UntrainedRandomForestClassification()

  override val train = new DMethod1To1[Trainable.Parameters, DataFrame, Scorable] {
    override def apply(context: ExecutionContext)(
        parameters: Trainable.Parameters)(
        dataFrame: DataFrame): Scorable = {

      val (featureColumns, targetColumn) = parameters.columnNames(dataFrame)

      val labeledPoints = dataFrame.toSparkLabeledPointRDDWithCategoricals(
        featureColumns, targetColumn)
      labeledPoints.cache()

      val trainedModel = SparkRandomForest.trainClassifier(
        labeledPoints,
        2,
        extractCategoricalFeatures(dataFrame, featureColumns),
        modelParameters.numTrees,
        modelParameters.featureSubsetStrategy,
        modelParameters.impurity,
        modelParameters.maxDepth,
        modelParameters.maxBins)

      val result = TrainedRandomForestClassification(trainedModel, featureColumns, targetColumn)

      labeledPoints.unpersist()
      result
    }

    override def infer(context: InferContext)(
        parameters: Trainable.Parameters)(
        dataframeKnowledge: DKnowledge[DataFrame]): (DKnowledge[Scorable], InferenceWarnings) = {
      (DKnowledge(new TrainedRandomForestClassification()), InferenceWarnings.empty)
    }
  }

  override def report(executionContext: ExecutionContext): Report =
    Report(ReportContent("Report for UntrainedRandomForestClassification"))

  override def save(context: ExecutionContext)(path: String): Unit = ???
}