/*
 * Copyright (C) 2017-2019  Lightbend
 *
 * This file is part of the Lightbend model-serving-tutorial (https://github.com/lightbend/model-serving-tutorial)
 *
 * The model-serving-tutorial is free software: you can redistribute it and/or modify
 * it under the terms of the Apache License Version 2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lightbend.modelserving.winemodel.tensorflow

import com.lightbend.model.winerecord.WineRecord
import com.lightbend.modelserving.model.tensorflow.TensorFlowModel
import com.lightbend.modelserving.model.{Model, ModelFactory}
import com.lightbend.modelserving.model.ModelToServe
import org.tensorflow.Tensor

/**
  * TensorFlow model implementation for wine data
  */
class WineTensorFlowModel(inputStream : Array[Byte]) extends TensorFlowModel[WineRecord, Double](inputStream) {

  import WineTensorFlowModel._

  /** Score a wine record with the model */
  override def score(input: WineRecord): Double = {

    // Create input tensor
    val modelInput = toTensor(input)
    // Serve model using TensorFlow APIs
    val result = session.runner.feed("dense_1_input", modelInput).fetch("dense_3/Sigmoid").run().get(0)
    // Get result shape
    val rshape = result.shape
    // Map output tensor to shape
    var rMatrix = Array.ofDim[Float](rshape(0).asInstanceOf[Int], rshape(1).asInstanceOf[Int])
    result.copyTo(rMatrix)
    // Get result
    rMatrix(0).indices.maxBy(rMatrix(0)).toDouble
  }
}

/** Factory for wine data PMML model */
object WineTensorFlowModel extends  ModelFactory[WineRecord, Double] {

  def toTensor(record: WineRecord) : Tensor[_] = {
    val data = Array(
      record.fixedAcidity.toFloat,
      record.volatileAcidity.toFloat,
      record.citricAcid.toFloat,
      record.residualSugar.toFloat,
      record.chlorides.toFloat,
      record.freeSulfurDioxide.toFloat,
      record.totalSulfurDioxide.toFloat,
      record.density.toFloat,
      record.pH.toFloat,
      record.sulphates.toFloat,
      record.alcohol.toFloat
    )
    Tensor.create(Array(data))
  }

  override def create(input: ModelToServe): Option[Model[WineRecord, Double]] = {
    try {
      Some(new WineTensorFlowModel(input.model))
    }catch{
      case t: Throwable => None
    }
  }

  override def restore(bytes: Array[Byte]): Model[WineRecord, Double] = new WineTensorFlowModel(bytes)
}
