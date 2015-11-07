package horta.pgqconsumer

import java.sql.Connection

import akka.actor.Actor
import com.brandwatch.pgqconsumer.PGQEventHandler

/**
 * Created by horta on 07/11/15.
 */
class PGQConsumerScheduler(configuration: PGQConsumerConfig, eventHandler: PGQEventHandler)(implicit connection: Connection) extends Actor {
  import context.dispatcher

  private val tick = context.system.scheduler.schedule(configuration.initialDelay, configuration.interval, self, "tick")

  override def postStop() = tick.cancel()

  private val consumer = new PGQConsumer(configuration.queueName, configuration.consumerName)

  def receive = {
    case "tick" =>
      consumer.registerIfNeeded()
      consumer.getNextBatchID().foreach(handleBatch)
  }

  def handleBatch(batchID: Long) = {
    consumer.getNextBatch(batchID).foreach(eventHandler.handle)
    consumer.finishBatch(batchID)
  }
}
