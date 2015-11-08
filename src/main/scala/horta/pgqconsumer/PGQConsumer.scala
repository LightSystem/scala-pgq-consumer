package horta.pgqconsumer

import java.sql.Connection
import anorm._
import anorm.SqlParser._
import com.brandwatch.pgqconsumer.{PGQEvent, EventDataProcessor}

/**
 * Created by horta on 07/11/15.
 */
class PGQConsumer(queueName: String, consumerName: String) {

  val eventDataProcessor = new EventDataProcessor
  val eventParser = long("ev_id") ~
    date("ev_time") ~
    long("ev_txid") ~
    int("ev_retry") ~
    str("ev_type") ~
    str("ev_data") ~
    str("ev_extra1") ~
    str("ev_extra2") ~
    str("ev_extra3") ~
    str("ev_extra4") map {
    case id ~ time ~ txid ~ retry ~ evType ~ data ~ extra1 ~ extra2 ~ extra3 ~ extra4 =>
        val dataMap = eventDataProcessor.processData(data)
        new PGQEvent(id, time, txid, retry, evType, dataMap, extra1, extra2, extra3, extra4)
    }

  /**
   * @return The result of the registration. 1 means success, 0 means already registered.
   */
  def registerIfNeeded()(implicit connection: Connection) = {
    SQL(
      """
        | select pgq.register_consumer({queueName}, {consumerName})
      """.stripMargin
    ).on(
      'queueName -> queueName,
      'consumerName -> consumerName
    ).as(scalar[Int].single)
  }

  /**
   * Get the ID of the next batch to be processed.
   *
   * @return The next batch ID to process, or None if there are no more events available.
   */
  def getNextBatchID()(implicit connection: Connection) = {
    SQL(
      """
        | select pgq.next_batch({queueName}, {consumerName})
      """.stripMargin
    ).on(
      'queueName -> queueName,
      'consumerName -> consumerName
    ).as(scalar[Long].singleOpt)
  }

  /**
   * Get the next batch of events.
   *
   * @param batchID Batch of events to retrieve.
   * @return List of Event objects that were in that batch.
   */
  def getNextBatch(batchID: Long)(implicit connection: Connection) = {
    SQL(
      """
        | select * from pgq.get_batch_events({batchId})
      """.stripMargin
    ).on(
      'batchId -> batchID
    ).as(eventParser.*)
  }

  /**
   * Finish the given batch.
   *
   * @param batchID The batch to finish.
   * @return 1 if batch was found, 0 otherwise.
   */
  def finishBatch(batchID: Long)(implicit connection: Connection) = {
    SQL(
      """
        | select pgq.finish_batch({batchId})
      """.stripMargin
    ).on(
      'batchId -> batchID
    ).as(scalar[Int].single)
  }

  /**
   *
   * @param batchID Batch ID of the event to retry
   * @param eventID Event to retry
   * @param eventRetrySeconds Time to elapse before putting the event back into the queue
   */
  def retryEventLater(batchID: Long, eventID: Long, eventRetrySeconds: Int)(implicit connection: Connection) = {
    SQL(
      """
        | select * from pgq.event_retry({batchId}, {eventId}, {eventRetrySeconds})
      """.stripMargin
    ).on(
      'batchId -> batchID,
      'eventId -> eventID,
      'eventRetrySeconds -> eventRetrySeconds
    ).as(scalar[Int].single)
  }
}
