/**
 * Copyright (c) 2015, CodiLime Inc.
 */

package io.deepsense.deeplang.doperations

import io.deepsense.deeplang.DOperation._
import io.deepsense.deeplang.doperations.transformations.MathematicalTransformation
import io.deepsense.deeplang.{ExecutionContext, DOperation0To1}
import io.deepsense.deeplang.doperables.Transformation
import io.deepsense.deeplang.parameters.AcceptAllRegexValidator
import io.deepsense.deeplang.parameters.StringParameter
import io.deepsense.deeplang.parameters.ParametersSchema

case class MathematicalOperation() extends DOperation0To1[Transformation] {

  override val name : String = "Mathematical Operation"

  override val id : Id = "ecb9bc36-5f7c-4a62-aa18-8db6e2d73251"

  // TODO: DS-635 This operation will fail if user provide column name with '.'

  override protected def _execute(context: ExecutionContext)(): Transformation = {
    val formula = formulaParam.value.get
    MathematicalTransformation(Some(formula))
  }

  val formulaParam = StringParameter(
    "formula", None, required = true, validator = new AcceptAllRegexValidator)

  override val parameters = ParametersSchema("formula" -> formulaParam)
}

object MathematicalOperation {
  def apply(formula: String): MathematicalOperation = {

    val operation = new MathematicalOperation
    operation.formulaParam.value = Some(formula)
    operation
  }
}