package org.ekstep.dp.functions

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.OutputTag
import org.apache.flink.util.Collector
import org.ekstep.dp.cache.{DedupEngine, RedisConnect}
import org.ekstep.dp.domain.Event
import org.ekstep.dp.task.DeNormalizationConfig
import org.slf4j.LoggerFactory

class DeNormalizationFunction(config: DeNormalizationConfig)(implicit val eventTypeInfo: TypeInformation[Event])
  extends ProcessFunction[Event, Event] {

  private[this] val logger = LoggerFactory.getLogger(classOf[DeNormalizationFunction])

  lazy val denormedEventOutPut: OutputTag[Event] = new OutputTag[Event](id = "denormed-event")
  lazy val failedEventOutPut: OutputTag[Event] = new OutputTag[Event](id = "failed-event")
  lazy val errorEventOutPut: OutputTag[Event] = new OutputTag[Event](id = "error-event")
  lazy val skippedEventsOutPut: OutputTag[Event] = new OutputTag[Event](id = "skipped-event")
  lazy val redisConnect = new RedisConnect(config)

  override def processElement(event: Event,
                              ctx: ProcessFunction[Event, Event]#Context,
                              out: Collector[Event]
                             ): Unit = {
    val eventIdentifier = event.eid();

//    if (eventIdentifier.eq("ERROR")) {
//      logger.info("Skipping ERROR Event denormalization");
//      ctx.output(denormedEventOutPut, event);
//    }
//    if (eventIdentifier.contains(config.summaryEventsList)) {
//      // invoke denorm
//    }else if(eventIdentifier.){
//
//    }






    //val duplicationCheckRequired = isDuplicateCheckRequired(event)
    //    if (duplicationCheckRequired) {
    //      if (!dedupEngine.isUniqueEvent(event.mid)) {
    //        logger.info(s"Duplicate Event mid: ${event.mid}")
    //        event.markDuplicate()
    //        ctx.output(duplicateEventOutput, event)
    //      } else {
    //        logger.info(s"Adding mid: ${event.mid} to Redis")
    //        dedupEngine.storeChecksum(event.mid)
    //        event.markSuccess()
    //        ctx.output(uniqueEventOuput, event)
    //      }
    //    } else {
    //      event.markSuccess()
    //      ctx.output(uniqueEventOuput, event)
    //    }
  }
}
