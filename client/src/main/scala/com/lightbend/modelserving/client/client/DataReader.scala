/*
 * Copyright (C) 2019  Lightbend
 *
 * This file is part of flink-ModelServing
 *
 * flink-ModelServing is free software: you can redistribute it and/or modify
 * it under the terms of the Apache License Version 2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.lightbend.modelserving.client.client

import com.lightbend.modelserving.client.MessageListener
import com.lightbend.modelserving.configuration.ModelServingConfiguration._

object DataReader {

  def main(args: Array[String]) {

    println(s"Using kafka brokers at ${KAFKA_BROKER}")

    val listener = MessageListener(KAFKA_BROKER, MODELS_TOPIC, MODELS_GROUP, new RecordProcessor())
    listener.start()
  }
}