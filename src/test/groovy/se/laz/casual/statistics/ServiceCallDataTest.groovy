/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics

import spock.lang.Specification

import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

import static se.laz.casual.statistics.TimeConverter.toInstant
import static se.laz.casual.statistics.TimeConverter.toMicroseconds

class ServiceCallDataTest extends Specification
{
   def 'failed creation'()
   {
      when:
      new ServiceCallData(callTime, pendingTime, lastCall)
      then:
      thrown(NullPointerException)
      where:
      callTime                                    || pendingTime                            || lastCall
      null                                        || Duration.of(100, ChronoUnit.MICROS)    || LocalDateTime.now()
      Duration.of(100, ChronoUnit.MICROS)         || null                                   || LocalDateTime.now()
      Duration.of(100, ChronoUnit.MICROS)         || Duration.of(100, ChronoUnit.MICROS)    || null
   }

   def 'ok creation'()
   {
      given:
      LocalDateTime lastCall = LocalDateTime.now()
      Duration callTime = Duration.of(1000, ChronoUnit.MICROS)
      Duration pendingTime = Duration.ZERO
      when:
      ServiceCallData data = new ServiceCallData(callTime, pendingTime, lastCall)
      then:
      data.callTimeInMicroseconds() == callTime
      data.pendingTimeInMicroseconds() == pendingTime
      data.lastCall() == lastCall
   }

   def  'builder creation'()
   {
      given:
      Instant instantNow = Instant.now()
      long start = toMicroseconds(instantNow)
      long callTime = 50000
      long pending = 100
      LocalDateTime lastCall = LocalDateTime.ofInstant(toInstant(start), ZoneId.systemDefault())
      Duration callDuration = Duration.of(callTime, ChronoUnit.MICROS)
      Duration pendingDuration = Duration.of(pending, ChronoUnit.MICROS)
      when:
      ServiceCallData data = ServiceCallData.newBuilder()
              .withStart(start)
              .withEnd(start + callTime)
              .withPending(pending)
              .build()
      then:
      data.callTimeInMicroseconds() == callDuration
      data.pendingTimeInMicroseconds() == pendingDuration
      data.lastCall() == lastCall
   }

}