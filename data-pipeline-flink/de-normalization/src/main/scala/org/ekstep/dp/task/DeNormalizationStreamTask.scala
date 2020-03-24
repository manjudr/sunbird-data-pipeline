package org.ekstep.dp.task

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.OutputTag
import org.ekstep.dp.domain.Event
import org.ekstep.dp.functions.{DeNormFilterEventsFunction, DeNormalizationFunction, DeduplicationFunction, DialCodeDeNormedFunction, TelemetryValidationFunction}


class DeNormalizationStreamTask(config: DeNormalizationConfig) extends BaseStreamTask(config) {

  private val serialVersionUID = 146697324640926024L

  def process(): Unit = {
    implicit val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    implicit val eventTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])
    env.enableCheckpointing(config.checkpointingInterval)

    try {
      val kafkaConsumer = createObjectStreamConsumer[Event](config.kafkaInputTopic)

      /**
       * Filter "ERROR" and other than "ME_WORKFLOW_SUMMARY" summary events.
       */
      val filteredEventsStream: SingleOutputStreamOperator[Event] =
        env.addSource(kafkaConsumer, "kafka-telemetry-denorm-consumer")
          .process(new DeNormFilterEventsFunction(config))
          .setParallelism(1)

      val dialCodeDeNormedStream: SingleOutputStreamOperator[Event] =
        filteredEventsStream.getSideOutput(new OutputTag[Event]("dial-code-events"))
          .process(new DialCodeDeNormedFunction(config)).name("dialCodeDeNormed")
          .setParallelism(1)


//      val contentDeNormedStream: SingleOutputStreamOperator[Event] =
//        dialCodeDeNormedStream.getSideOutput(new OutputTag[Event]("valid-events"))
//          .process(new contentDenormedFunction(config)).name("Deduplication")
//          .setParallelism(2)
//
//      val usedDataDeNormedStream: SingleOutputStreamOperator[Event] =
//        contentDeNormedStream.getSideOutput(new OutputTag[Event]("valid-events"))
//          .process(new usedDataDeNormedFunction(config)).name("Deduplication")
//          .setParallelism(2)
//
//      val collectionDataDeNormedStream: SingleOutputStreamOperator[Event] =
//        usedDataDeNormedStream.getSideOutput(new OutputTag[Event]("valid-events"))
//          .process(new collectionDataDeNormedStream(config)).name("Deduplication")
//          .setParallelism(2)

      dialCodeDeNormedStream.getSideOutput(new OutputTag[Event]("de-normed-success-events"))
        .addSink(createObjectStreamProducer[Event](config.kafkaSuccessTopic))
        .name("kafka-telemetry-denorm-success-producer")

      dialCodeDeNormedStream.getSideOutput(new OutputTag[Event]("de-normed-failed-events"))
        .addSink(createObjectStreamProducer[Event](config.kafkaMalformedTopic))
        .name("kafka-telemetry-denorm-failed-producer")
      env.execute("DeNormalizationFlinkJob")

    } catch {
      case ex: Exception =>
        ex.printStackTrace()
    }
  }

  def canEqual(other: Any): Boolean = other.isInstanceOf[DeNormalizationStreamTask]

  override def equals(other: Any): Boolean = other match {
    case that: DeNormalizationStreamTask =>
      (that canEqual this) &&
        serialVersionUID == that.serialVersionUID
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(serialVersionUID)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

object DeNormalizationStreamTask {
  val config = new DeNormalizationConfig

  def apply(): DeNormalizationStreamTask = new DeNormalizationStreamTask(config)

  def main(args: Array[String]): Unit = {
    DeNormalizationStreamTask.apply().process()
  }
}
