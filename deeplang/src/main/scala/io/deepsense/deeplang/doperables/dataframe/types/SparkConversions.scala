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

package io.deepsense.deeplang.doperables.dataframe.types

import org.apache.spark.mllib.linalg.VectorUDT
import org.apache.spark.sql

import io.deepsense.commons.types.ColumnType
import io.deepsense.commons.types.ColumnType._

object SparkConversions {

  private val columnTypeToSparkColumnTypeMap: Map[ColumnType, sql.types.DataType] = Map(
    ColumnType.numeric -> sql.types.DoubleType,
    ColumnType.string -> sql.types.StringType,
    ColumnType.boolean -> sql.types.BooleanType,
    ColumnType.timestamp -> sql.types.TimestampType,
    ColumnType.categorical -> sql.types.IntegerType,
    ColumnType.vector -> new VectorUDT()
  )

  private val sparkColumnTypeToColumnTypeMap: Map[sql.types.DataType, ColumnType] =
    columnTypeToSparkColumnTypeMap.map(_.swap).toMap

  def columnTypeToSparkColumnType(columnType: ColumnType): sql.types.DataType =
    columnTypeToSparkColumnTypeMap(columnType)

  def sparkColumnTypeToColumnType(sparkColumnType: sql.types.DataType): ColumnType =
    sparkColumnTypeToColumnTypeMap(sparkColumnType)
}
