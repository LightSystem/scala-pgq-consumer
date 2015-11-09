package horta.pgqconsumer

import scala.concurrent.duration.FiniteDuration

/**
 * Created by horta on 07/11/15.
 */
case class PGQConsumerConfig (
  initialDelay: FiniteDuration,
  interval: FiniteDuration,
  queueName: String,
  consumerName: String,
  registerConsumer: Boolean = true
)
