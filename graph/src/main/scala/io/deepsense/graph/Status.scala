/**
 * Copyright (c) 2015, CodiLime Inc.
 */

package io.deepsense.graph

object Status extends Enumeration {
  // TODO: Merge with Experiment.Status if appropriate https://codilime.atlassian.net/browse/DS-775
  type Status = Value
  val Draft = Value(0, "DRAFT")
  val Queued = Value(1, "QUEUED")
  val Running = Value(2, "RUNNING")
  val Completed = Value(3, "COMPLETED")
  val Failed = Value(4, "FAILED")
  val Aborted = Value(5, "ABORTED")
}