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

class ServiceCallAccumulatedDataTest extends Specification
{
   def 'accumulate'()
   {
      given:
      def numberOfCalls = 1
      def numberOfPending = 1
      Instant instantNow = Instant.now()
      long start = toMicroseconds(instantNow)
      LocalDateTime lastCall = LocalDateTime.ofInstant(toInstant(start), ZoneId.systemDefault())
      long initialCallTimeMicros =  1000
      long secondCallTimeMicros = 5000
      long thirdCallTimeMicros = 12500
      long initialPendingtimeMicros = 5000
      long secondPendingtimeMicros = 1000
      Duration initialCallTime = Duration.of(initialCallTimeMicros, ChronoUnit.MICROS)
      Duration secondCallTime = Duration.of(secondCallTimeMicros, ChronoUnit.MICROS)
      Duration thirdCallTime = Duration.of(thirdCallTimeMicros, ChronoUnit.MICROS)
      Duration initialPendingTime = Duration.of(initialPendingtimeMicros, ChronoUnit.MICROS)
      Duration secondPendingTime = Duration.of(secondPendingtimeMicros, ChronoUnit.MICROS)
      ServiceCallData initialData = ServiceCallData.newBuilder()
              .withStart(start)
              .withEnd(start + initialCallTimeMicros)
              .withPending(initialPendingtimeMicros)
              .build()
      ServiceCallData noPendingData = ServiceCallData.newBuilder()
              .withStart(start)
              .withEnd(start + secondCallTimeMicros)
              .build()
      ServiceCallData moreDataWithPending = ServiceCallData.newBuilder()
              .withStart(start)
              .withEnd(start + thirdCallTimeMicros)
              .withPending(secondPendingtimeMicros)
              .build()
      when:
      ServiceCallAccumulatedData accumulatedData = ServiceCallAccumulatedData.newBuilder()
              .withServiceCallData(initialData)
              .build()
      then: // initial check
      accumulatedData.numberOfServiceCalls() == numberOfCalls
      accumulatedData.minTime() == initialCallTime
      accumulatedData.maxTime() == initialCallTime
      accumulatedData.averageTime() == initialCallTime
      accumulatedData.totalTime() == initialCallTime
      accumulatedData.numberOfPending() == numberOfPending
      accumulatedData.totalPendingTime() == initialPendingTime
      accumulatedData.lastCall() == lastCall
      when: // add call but no pending
      accumulatedData = accumulatedData.accumulate(noPendingData)
      then:
      accumulatedData.numberOfServiceCalls() == ++numberOfCalls
      accumulatedData.minTime() == initialCallTime
      accumulatedData.maxTime() == secondCallTime
      accumulatedData.averageTime() == (initialCallTime + secondCallTime).dividedBy(numberOfCalls)
      accumulatedData.totalTime() == initialCallTime + secondCallTime
      accumulatedData.numberOfPending() == numberOfPending
      accumulatedData.totalPendingTime() == initialPendingTime
      accumulatedData.averagePendingTime() == initialPendingTime
      accumulatedData.lastCall() == lastCall
      when: // add call with pending
      accumulatedData = accumulatedData.accumulate(moreDataWithPending)
      then:
      accumulatedData.numberOfServiceCalls() == ++numberOfCalls
      accumulatedData.minTime() == initialCallTime
      accumulatedData.maxTime() == thirdCallTime
      accumulatedData.averageTime() == (initialCallTime + secondCallTime + thirdCallTime).dividedBy(numberOfCalls)
      accumulatedData.totalTime() == initialCallTime + secondCallTime + thirdCallTime
      accumulatedData.numberOfPending() == ++numberOfPending
      accumulatedData.totalPendingTime() == initialPendingTime + secondPendingTime
      accumulatedData.averagePendingTime() == (initialPendingTime + secondPendingTime).dividedBy(numberOfPending)
      accumulatedData.lastCall() == lastCall
   }
}
