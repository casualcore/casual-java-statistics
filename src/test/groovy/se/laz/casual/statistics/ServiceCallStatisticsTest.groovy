/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics

import se.laz.casual.api.CasualRuntimeException
import se.laz.casual.event.Order
import spock.lang.Shared
import spock.lang.Specification

import java.time.Duration
import java.time.temporal.ChronoUnit

class ServiceCallStatisticsTest extends Specification
{
   @Shared
   ServiceCallConnection sharedServiceCallConnection = new ServiceCallConnection('a very fast connection, also blue')
   @Shared
   ServiceCall sharedServiceCall = new ServiceCall('a really nice service', Order.SEQUENTIAL)
   @Shared
   ServiceCallData sharedServiceCallData = ServiceCallData.newBuilder()
           .withPending(42)
           .withStart(0)
           .withEnd(100)
           .build()

   def 'store, wrong usage'() {
      when:
      ServiceCallStatistics.store(connection, serviceCall, data)
      then:
      thrown(NullPointerException)
      where:
      connection                || serviceCall     || data
      null                      || sharedServiceCall   || sharedServiceCallData
      sharedServiceCallConnection || null            || sharedServiceCallData
      sharedServiceCallConnection || sharedServiceCall || null
   }

   def 'fetch, wrong usage'() {
      when:
      ServiceCallStatistics.get(connection, serviceCall)
      then:
      thrown(NullPointerException)
      where:
      connection                || serviceCall
      null                      || sharedServiceCall
      sharedServiceCallConnection || null
   }

   def 'store, get, accumulate, get'()
   {
      given:
      def start = 0
      def end = 100_000
      def secondEnd = 10_000
      def thirdEnd = end * 2
      def initialPending = 10_000
      def secondPending = 5_000
      def numberOfServiceCalls = 0
      def numberOfPending = 0
      Duration initialDuration = Duration.of(end, ChronoUnit.MICROS)
      Duration initialPendingDuration = Duration.of(initialPending, ChronoUnit.MICROS)
      Duration secondPendingDuration = Duration.of(secondPending, ChronoUnit.MICROS)
      Duration secondDuration = Duration.of(secondEnd, ChronoUnit.MICROS)
      Duration thirdDuration = Duration.of(thirdEnd, ChronoUnit.MICROS)
      ServiceCallData initialData = ServiceCallData.newBuilder()
              .withPending(0)
              .withStart(start)
              .withEnd(end)
              .build()
      ServiceCallData secondCallData = ServiceCallData.newBuilder()
              .withPending(initialPending)
              .withStart(start)
              .withEnd(secondEnd)
              .build()
      ServiceCallData thirdCallData = ServiceCallData.newBuilder()
              .withPending(secondPending)
              .withStart(start)
              .withEnd(thirdEnd)
              .build()

      when:
      ServiceCallStatistics.store(sharedServiceCallConnection, sharedServiceCall, initialData)
      ServiceCallAccumulatedData accumulatedData = ServiceCallStatistics.get(sharedServiceCallConnection, sharedServiceCall).orElseThrow {new CasualRuntimeException("missing entry")}
      then:
      accumulatedData.numberOfPending() == numberOfPending
      accumulatedData.totalPendingTime() == Duration.ZERO
      accumulatedData.numberOfServiceCalls() == ++numberOfServiceCalls
      accumulatedData.minTime() == initialDuration
      accumulatedData.maxTime() == initialDuration
      accumulatedData.totalTime() == initialDuration
      accumulatedData.averageTime() == accumulatedData.totalTime().dividedBy(numberOfServiceCalls)
      when:
      ServiceCallStatistics.store(sharedServiceCallConnection, sharedServiceCall, secondCallData)
      accumulatedData = ServiceCallStatistics.get(sharedServiceCallConnection, sharedServiceCall).orElseThrow {new CasualRuntimeException("missing entry")}
      then:
      accumulatedData.numberOfPending() == ++numberOfPending
      accumulatedData.totalPendingTime() == initialPendingDuration
      accumulatedData.averagePendingTime() == accumulatedData.totalPendingTime().dividedBy(numberOfPending)
      accumulatedData.numberOfServiceCalls() == ++numberOfServiceCalls
      accumulatedData.minTime() == secondDuration
      accumulatedData.maxTime() == initialDuration
      accumulatedData.totalTime() == initialDuration + secondDuration
      accumulatedData.averageTime() == accumulatedData.totalTime().dividedBy(numberOfServiceCalls)
      when:
      ServiceCallStatistics.store(sharedServiceCallConnection, sharedServiceCall, thirdCallData)
      accumulatedData = ServiceCallStatistics.get(sharedServiceCallConnection, sharedServiceCall).orElseThrow {new CasualRuntimeException("missing entry")}
      then:
      accumulatedData.numberOfPending() == ++numberOfPending
      accumulatedData.totalPendingTime() == initialPendingDuration + secondPendingDuration
      accumulatedData.averagePendingTime() == accumulatedData.totalPendingTime().dividedBy(numberOfPending)
      accumulatedData.numberOfServiceCalls() == ++numberOfServiceCalls
      accumulatedData.minTime() == secondDuration
      accumulatedData.maxTime() == thirdDuration
      accumulatedData.totalTime() == initialDuration + secondDuration + thirdDuration
      accumulatedData.averageTime() == accumulatedData.totalTime().dividedBy(numberOfServiceCalls)
   }


}
