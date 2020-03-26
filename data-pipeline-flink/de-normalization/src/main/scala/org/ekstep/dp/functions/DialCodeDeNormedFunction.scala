package org.ekstep.dp.functions

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.util.Collector
import org.ekstep.dp.domain.Event
import org.ekstep.dp.task.DeNormalizationConfig
import org.slf4j.LoggerFactory

class DialCodeDeNormedFunction(config: DeNormalizationConfig)(implicit val eventTypeInfo: TypeInformation[Event])
  extends ProcessFunction[Event, Event] {
  private[this] val logger = LoggerFactory.getLogger(classOf[DialCodeDeNormedFunction])
  override def processElement(event: Event, context: ProcessFunction[Event, Event]#Context, collector: Collector[Event]): Unit = {

    if(event.objectType().equalsIgnoreCase("dialcode") || event.objectType().equalsIgnoreCase("qr")){
      eventUpdaterFactory.getInstance("dialcode-data-updater").update(event, event.objectID())
    }



  }
}
