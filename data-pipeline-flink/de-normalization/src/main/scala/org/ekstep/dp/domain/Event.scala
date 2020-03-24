package org.ekstep.dp.domain

import java.text.{DateFormat, SimpleDateFormat}
import java.util
import java.util.Date

import org.ekstep.ep.samza.events.domain.Events
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

class Event(eventMap: util.Map[String, AnyRef]) extends Events(eventMap) {
  private val dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd").withZoneUTC
  private val formatter = new SimpleDateFormat("yyyy-MM-dd")

  def markDenormFailure(error: String, jobName: String): Unit = {
    telemetry.addFieldIfAbsent("flags", Map[String, Boolean])
    telemetry.add("flags.denorm_processed", false)
    telemetry.addFieldIfAbsent("metadata", Map[String, AnyRef])
    telemetry.add("metadata.denorm_error", error)
    telemetry.add("metadata.src", jobName)
  }

  def markDenormSuccess(): Unit = {
    telemetry.addFieldIfAbsent("flags", Map[String, Boolean])
    telemetry.add("flags.denorm_processed", true)
  }

  def isOlder(periodInMonths: Int): Boolean = {
    val eventEts = ets
    val periodInMillis = new DateTime().minusMonths(periodInMonths).getMillis
    if (eventEts < periodInMillis) true
    else false
  }

  def CompareAndUpdateETS(): Unit = {
    val currentMillis = new Date().getTime
    if (ets() > getEndTimestampOfDay(formatter.format(currentMillis))) telemetry.add("ets", currentMillis)
  }

  def getEndTimestampOfDay(date: String): Long = {
    dateFormat.parseDateTime(date).plusHours(23).plusMinutes(59).plusSeconds(59).getMillis
  }


}
