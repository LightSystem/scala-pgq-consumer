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
    - registerConsumer: (optional) by default the library registers the consumer for you, set to false if you want to handle that yourself.
  2. Create an instance of PGQConsumerScheduler with the following parameters:
    - configuration: the configuration defined above.
    - batchHandler: your handler that will be called with the detail of each event.
    - actorSystem: optionally pass an Akka ActorSystem to be used to schedule the consumer events.
  
The library will take care to call your batch handler with the list of events every time the consumer fetched a batch from PGQ.
