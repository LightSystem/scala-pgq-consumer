scala-pgq-consumer [![Build Status](https://travis-ci.org/BrandwatchLtd/pgq-consumer.svg)](https://travis-ci.org/BrandwatchLtd/pgq-consumer)
============

A PGQ consumer written in Scala, using Anorm for database access and Akka for scheduling consumer events.

What's PGQ?
-----------

[PGQ](https://wiki.postgresql.org/wiki/PGQ_Tutorial) is the queueing solution from [Skytools](https://wiki.postgresql.org/wiki/Skytools), which was written by [Skype](http://www.skype.com/en/). It's a neat way of writing database triggers that send events to an event queue in [PostgreSQL](http://www.postgresql.org/), which you can then poll with the PGQ API. An implementation of this polling is available in this library. 

A good presentation on PGQ is [available on SlideShare](http://www.slideshare.net/adorepump/skytools-pgq-queues-and-applications).

How do I use it?
----------------

  1. Create a PGQConsumerConfig instance which will determine the behaviour of the consumer. The following is configurable:
    - initialDelay: delay passed to scheduler determining the time to wait before sending the first consume message to the consumer actor.
    - interval: frequency with which to send consume messages to the consumer actor.
    - queueName: name of the queue from which to consume.
    - consumerName: name of the consumer that will consume the events.
    - retryEventsOnFailure: true if you want to send failed events (when the handler throws an exception) to PGQ retry event, false otherwise.
    - eventRetryDelaySeconds: delay for when the retry event should be put back into queue.
  2. Create an instance of PGQConsumerScheduler with the following parameter:
    - configuration: the configuration defined above.
    - eventHandler: your handler that will be called with the detail of each event.
    - actorSystem: optionally pass an Akka ActorSystem to be used to schedule the consumer events.
  3. That should be it!
