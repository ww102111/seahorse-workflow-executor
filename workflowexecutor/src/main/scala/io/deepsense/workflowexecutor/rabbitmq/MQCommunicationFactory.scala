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

package io.deepsense.workflowexecutor.rabbitmq

import java.util

import akka.actor.{ActorRef, ActorSystem}
import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.{Channel, DefaultConsumer, Envelope}
import com.thenewmotion.akka.rabbitmq.{ChannelActor, CreateChannel, RichConnectionActor}

case class MQCommunicationFactory(system: ActorSystem, connection: ActorRef) {

  val exchangeType = "fanout"

  def createCommunicationChannel(name: String, subscriber: SubscriberActor): MQPublisher = {
    createSubscriber(name, subscriber)
    createPublisher(name)
  }

  private def createSubscriber(exchangeName: String, subscriber: SubscriberActor): Unit = {
    val subscriberName = s"${exchangeName}_subscriber"
    connection ! CreateChannel(
      ChannelActor.props(setupSubscriber(exchangeName, subscriber)),
      Some(subscriberName))
  }

  private def setupSubscriber(
    exchangeName: String,
    subscriberActor: SubscriberActor)(
    channel: Channel, self: ActorRef): Unit = {
    val queue = channel.queueDeclare(
      s"${exchangeName}_to_executor",
      false,
      false,
      true,
      new util.HashMap[String, AnyRef]()).getQueue
    channel.queueBind(queue, exchangeName, "to_executor")
    val basicSubscriber = MQSubscriber(subscriberActor)
    val consumer = new DefaultConsumer(channel) {
      override def handleDelivery(
        consumerTag: String,
        envelope: Envelope,
        properties: BasicProperties,
        body: Array[Byte]): Unit =
        basicSubscriber.handleDelivery(body)
    }
    channel.basicConsume(queue, true, consumer)
  }

  private def createPublisher(exchangeName: String): MQPublisher = {
    val publisherName = s"${exchangeName}_publisher"
  val channelActor: ActorRef =
    connection.createChannel(ChannelActor.props(setupPublisher(exchangeName)), Some(publisherName))
    MQPublisher(exchangeName, channelActor)
  }

  private def setupPublisher(exchangeName: String)(channel: Channel, self: ActorRef): Unit = {
    val queue = channel.queueDeclare(
      publishQueueName(exchangeName),
      false,
      false,
      true,
      new util.HashMap[String, AnyRef]()).getQueue
    channel.exchangeDeclare(exchangeName, exchangeType)
    channel.queueBind(queue, exchangeName, "to_executor")
  }

  private def publishQueueName(exchangeName: String): String = exchangeName + "_to_editor"
}
