package org.ekstep.dp.domain

import java.util

import org.ekstep.dp.functions.DialCodeDeNormedFunction
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import org.slf4j.LoggerFactory


object IEventUpdater {
  private[this] val logger = LoggerFactory.getLogger(classOf[IEventUpdater])
}

abstract class IEventUpdater {
  val dataCache = null
  val cacheType = null
  private[domain] val df = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").withZoneUTC
  private[domain] val df1 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").withZoneUTC

  def update(event: Event): Unit

  def update(event: Event, key: String): Unit = {
//    if (key != null && !key.isEmpty) {
//      val data = dataCache.getData(key)
//      if (data != null && !data.isEmpty) event.addMetaData(cacheType, getConvertedData(data))
//      else event.setFlag(DeNormalizationConfig.getJobFlag(cacheType), false)
//    }
  }

  private def getConvertedData(data: Map[String, String]):Map[String, Any] = {
    if ("content" == cacheType || "collection" == cacheType) getEpochConvertedContentDataMap(data)
    else if ("dialcode" == cacheType) getEpochConvertedDialcodeDataMap(data)
    else data
  }

  private def getTimestamp(ts: String, df: DateTimeFormatter): Long = try df.parseDateTime(ts).getMillis
  catch {
    case ex: Exception =>
      0L
  }

  private def getConvertedTimestamp(ts: String): Long = {
    var epochTs = getTimestamp(ts, df)
    if (epochTs eq 0) epochTs = getTimestamp(ts, df1)
    epochTs
  }

  private def getEpochConvertedContentDataMap(data: Map[String, String]): Map[String, Long] = {
    val lastSubmittedOn = getConvertedTimestamp(data.getOrElse("lastsubmittedon", ""));
    val lastUpdatedOn = getConvertedTimestamp(data.getOrElse("lastupdatedon", ""));
    val lastPublishedOn = getConvertedTimestamp(data.getOrElse("lastpublishedon", ""));
    Map("lastsubmittedon" -> lastSubmittedOn, "lastsubmittedon" -> lastUpdatedOn, "lastpublishedon" -> lastPublishedOn)
  }



  private def getEpochConvertedDialcodeDataMap(data: Map[String, String]): Map[String, Long] = {
    val generatedOn = getConvertedTimestamp(data.getOrElse("generatedon", ""))
    val publishedOn = getConvertedTimestamp(data.getOrElse("publishedon", ""))
    Map("generatedon" -> generatedOn, "publishedon" -> publishedOn)
  }
}



