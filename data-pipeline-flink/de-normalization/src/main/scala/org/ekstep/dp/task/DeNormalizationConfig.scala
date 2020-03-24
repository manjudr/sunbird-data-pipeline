package org.ekstep.dp.task

import org.ekstep.dp.core.BaseJobConfig

import scala.collection.JavaConverters._

class DeNormalizationConfig extends BaseJobConfig {

  private val serialVersionUID = 2905979434303791379L

  val dedupStore: Int = config.getInt("redis.database.duplicationstore.id")
  val cacheExpirySeconds: Int = config.getInt("redis.database.key.expiry.seconds")

  // Kafka Topics Configuration
  val kafkaInputTopic: String = config.getString("kafka.input.topic")
  val kafkaSuccessTopic: String = config.getString("kafka.output.success.topic")
  val kafkaDuplicateTopic: String = config.getString("kafka.output.failed.topic")
  val kafkaMalformedTopic: String = config.getString("kafka.output.malformed.topic")
  val ignorePeriodInMonths:Int = config.getInt("telemetry.ignore.period.months")
  val summaryEventsList: List[String] = config.getStringList("summary.filter.events").asScala.toList

}
