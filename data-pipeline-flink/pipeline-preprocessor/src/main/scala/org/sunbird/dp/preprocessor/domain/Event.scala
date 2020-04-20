package org.sunbird.dp.preprocessor.domain

import java.util

import org.apache.commons.lang3.StringUtils
import org.joda.time.format.DateTimeFormat
import org.sunbird.dp.core.domain.{Events, EventsPath}
import org.sunbird.dp.preprocessor.task.PipelinePreprocessorConfig

class Event(eventMap: util.Map[String, Any]) extends Events(eventMap) {

  private[this] val dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZoneUTC
  private val jobName = "PipelinePreprocessor"

  override def kafkaKey(): String = {
    did
  }

  def schemaName: String = {
    if (eid != null) s"${eid.toLowerCase}.json"
    else "envelope.json"
  }

  def updateActorId(actorId: String): Unit = {
    telemetry.add(EventsPath.ACTOR_ID_PATH, actorId)
  }

  def correctDialCodeKey(): Unit = {
    val dialcodes = telemetry.read(s"${EventsPath.EDTA_FILTERS}.dialCodes").getOrElse(null)
    if (dialcodes != null) {
      telemetry.add(s"${EventsPath.EDTA_FILTERS}.dialcodes", dialcodes)
      telemetry.add(s"${EventsPath.EDTA_FILTERS}.dialCodes", null)
    }
  }

  def correctDialCodeValue(): Unit = {
    val dialcode = telemetry.read[String](EventsPath.OBJECT_ID_PATH).getOrElse(null)
    if (dialcode != null) telemetry.add(EventsPath.OBJECT_ID_PATH, dialcode.toUpperCase)
  }

  def markValidationFailure(errorMsg: String, flagName: String): Unit = {
    telemetry.addFieldIfAbsent(EventsPath.FLAGS_PATH, new util.HashMap[String, Boolean])
    telemetry.add(s"${EventsPath.FLAGS_PATH}.$flagName", false)
    telemetry.addFieldIfAbsent("metadata", new util.HashMap[String, AnyRef])
    if (null != errorMsg) {
      telemetry.add("metadata.validation_error", errorMsg)
      telemetry.add("metadata.src", jobName)
    }
  }

  def markSkipped(flagName: String): Unit = {
    telemetry.addFieldIfAbsent(EventsPath.FLAGS_PATH, new util.HashMap[String, Boolean])
    telemetry.add(s"${EventsPath.FLAGS_PATH}.$flagName", true)
  }

  def markSuccess(flagName: String): Unit = {
    telemetry.addFieldIfAbsent(EventsPath.FLAGS_PATH, new util.HashMap[String, Boolean])
    telemetry.add(s"${EventsPath.FLAGS_PATH}.$flagName", true)
    telemetry.add("type", "events")
  }

  def updateDefaults(config: PipelinePreprocessorConfig): Unit = {
    val channelString = telemetry.read[String](EventsPath.CONTEXT_CHANNEL_PATH).getOrElse(null)
    val channel = StringUtils.deleteWhitespace(channelString)
    if (channel == null || channel.isEmpty) {
      telemetry.addFieldIfAbsent(EventsPath.CONTEXT_PATH, new util.HashMap[String, AnyRef])
      telemetry.add(EventsPath.CONTEXT_CHANNEL_PATH, config.defaultChannel)
    }
    val atTimestamp = telemetry.getAtTimestamp
    val strSyncts = telemetry.getSyncts
    if (null == atTimestamp && null == strSyncts) {
      val syncts = System.currentTimeMillis
      telemetry.addFieldIfAbsent(EventsPath.SYNC_TS_PATH, syncts)
      telemetry.addFieldIfAbsent(EventsPath.TIMESTAMP, dateFormatter.print(syncts))
    }
    else if (atTimestamp != null) telemetry.addFieldIfAbsent(EventsPath.SYNC_TS_PATH, dateFormatter.parseMillis(atTimestamp))
    else if (strSyncts != null) telemetry.addFieldIfAbsent(EventsPath.TIMESTAMP, strSyncts)
  }

  def edataDir: String = telemetry.read[String](EventsPath.EDATA_DIR_PATH).getOrElse(null)

  def eventSyncTs: Long = telemetry.read[Long](EventsPath.SYNC_TS_PATH).getOrElse(System.currentTimeMillis()).asInstanceOf[Number].longValue()

  def eventTags: Seq[AnyRef] = telemetry.read[Seq[AnyRef]](EventsPath.TAGS_PATH).getOrElse(null)

  def cdata: util.ArrayList[util.Map[String, AnyRef]] = telemetry.read[util.ArrayList[util.Map[String, AnyRef]]](EventsPath.CONTEXT_CDATA).getOrElse(null)

  def eventPData: util.Map[String, AnyRef] = telemetry.read[util.Map[String, AnyRef]](EventsPath.CONTEXT_P_DATA_PATH).getOrElse(null)

  def sessionId: String = telemetry.read[String](EventsPath.CONTEXT_SID_PATH).getOrElse(null)

  def env: String = telemetry.read[String](EventsPath.CONTEXT_ENV_PATH).getOrElse(null)

  def rollup: util.Map[String, AnyRef] = telemetry.read[util.Map[String, AnyRef]](EventsPath.CONTEXT_ROLLUP_PATH).getOrElse(null)


}