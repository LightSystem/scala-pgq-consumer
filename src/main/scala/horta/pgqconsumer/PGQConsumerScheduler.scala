package horta.pgqconsumer

import java.sql.Connection

import akka.actor.{ActorSystem, Actor}
import com.brandwatch.pgqconsumer.PGQEventHandler

/**
 * Created by horta on 07/11/15.
 */
class PGQConsumerScheduler(
  configuration: PGQConsumerConfig, eventHandler: PGQEventHandler, actorSystem: Option[ActorSystem] = None
)(implicit connection: Connection) extends Actor {
  import context.dispatcher

  private val tick = {
    if(actorSystem.isDefined)
      actorSystem.get.scheduler.schedule(configuration.initialDelay, configuration.interval, self, "tick")
    else
      context.system.scheduler.schedule(configuration.initialDelay, configuration.interval, self, "tick")
  }

  override def postStop() = tick.cancel()

  private val consumer = new PGQConsumer(configuration.queueName, configuration.consumerName)

  def receive = {
    case "tick" =>
      consumer.registerIfNeeded()
      consumer.getNextBatchID().foreach(handleBatch)
  }

  def handleBatch(batchID: Long) = {

    consumer.getNextBatch(batchID).foreach(event => {
      try {
        eventHandler.handle(event)
      } catch {
        case e: Exception if configuration.retryEventsOnFailure =>
          consumer.retryEventLater(batchID, event.getId, configuration.eventRetryDelaySeconds)
      }
    })

    consumer.finishBatch(batchID)
  }
}
