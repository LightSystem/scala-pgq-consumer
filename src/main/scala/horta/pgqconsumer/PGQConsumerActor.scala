package horta.pgqconsumer

import java.sql.Connection

import akka.actor.Actor

/**
 * Created by horta on 07/11/15.
 */
class PGQConsumerActor(configuration: PGQConsumerConfig, batchHandler: PGQBatchHandler)(implicit connection: Connection) extends Actor {

  private val tick = {
    context.system.scheduler.schedule(configuration.initialDelay, configuration.interval, self, "tick")
  }

  override def postStop() = tick.cancel()

  private val consumer = new PGQConsumer(configuration.queueName, configuration.consumerName)

  def receive = {
    case "tick" =>
      if(configuration.registerConsumer) consumer.registerIfNeeded()
      consumer.getNextBatchID().foreach(handleBatch)
  }

  def handleBatch(batchID: Long) = {
    batchHandler.handleBatch(consumer.getNextBatch(batchID))

    consumer.finishBatch(batchID)
  }
}
