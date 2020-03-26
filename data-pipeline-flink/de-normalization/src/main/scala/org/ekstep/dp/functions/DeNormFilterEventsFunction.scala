package org.ekstep.dp.functions

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.OutputTag
import org.apache.flink.util.Collector
import org.ekstep.dp.domain.Event
import org.ekstep.dp.task.DeNormalizationConfig
import org.slf4j.LoggerFactory

class DeNormFilterEventsFunction(config: DeNormalizationConfig)(implicit val eventTypeInfo: TypeInformation[Event])
  extends ProcessFunction[Event, Event] {

  lazy val deNormAcceptedEventsOutPut: OutputTag[Event] = new OutputTag[Event](id = "denorm-accepted-event")
  lazy val deNormFailedEventsOutPut: OutputTag[Event] = new OutputTag[Event](id = "denorm-failed-event")
  lazy val deNormedRejectedEventsOutPut: OutputTag[Event] = new OutputTag[Event](id = "denorm-rejected-event")


  private[this] val logger = LoggerFactory.getLogger(classOf[DialCodeDeNormedFunction])

  override def processElement(event: Event, context: ProcessFunction[Event, Event]#Context, collector: Collector[Event]): Unit = {
    val eventId = event.eid()
    if (eventId.eq("ERROR")) {
      context.output(deNormedRejectedEventsOutPut, event);
    } else if (eventId.contains(config.summaryEventsList)) {
      event.CompareAndUpdateETS()
      context.output(deNormAcceptedEventsOutPut, event)
    } else if (eventId.startsWith("ME_")) {
      context.output(deNormedRejectedEventsOutPut, event)
    } else if (event.isOlder(config.ignorePeriodInMonths)) {
      context.output(deNormFailedEventsOutPut, event)
    } else {
      event.CompareAndUpdateETS()
      context.output(deNormAcceptedEventsOutPut, event);
    }
  }
}
