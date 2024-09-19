/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */
package se.laz.casual.statistics.json

import com.google.gson.JsonElement
import se.laz.casual.statistics.ServiceCallAccumulatedData
import spock.lang.Specification

import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class ServiceCallAccumulatedDataTypeAdapterTest extends Specification
{
   def 'roundtrip with standard date UTC with offset'()
   {
      given:
      long numberOfServiceCalls = 1
      Duration totalTime = Duration.of(10, ChronoUnit.MILLIS)
      Duration minTime = Duration.of(10, ChronoUnit.MILLIS)
      Duration maxTime = Duration.of(10, ChronoUnit.MILLIS)
      long numberOfPending = 0
      Duration totalPendingTime = Duration.ZERO
      LocalDateTime lastCall = LocalDateTime.now()
      ZonedDateTime zonedDateTime = ZonedDateTime.of(lastCall, ZoneId.systemDefault())
      ServiceCallAccumulatedData data =
              new ServiceCallAccumulatedData(numberOfServiceCalls, totalTime, minTime,
                      maxTime, numberOfPending, totalPendingTime, lastCall)
      when:
      JsonElement element = new ServiceCallAccumulatedDataTypeAdapter().serialize(data, null,null)
      def json = element.getAsJsonObject()
      String lastCallUTCOffset = json['lastCall']
      lastCallUTCOffset = lastCallUTCOffset.takeBetween('"')
      ZonedDateTime roundtrip = ZonedDateTime.parse(lastCallUTCOffset, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
      then:
      null != lastCallUTCOffset
      zonedDateTime.toOffsetDateTime() == roundtrip.toOffsetDateTime()
   }
}
