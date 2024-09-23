/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics.pool

import se.laz.casual.api.util.work.BackoffHelper
import spock.lang.Specification

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.function.Consumer
import java.util.function.Supplier

class RepeatUntilSuccessTaskTest extends Specification
{
   def 'fail first then succeed'()
   {
      given:
      ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor()
      ScheduleFunction scheduleFunction = executorService::schedule
      BackoffHelper backoffHelper = BackoffHelper.of(10L)
      boolean expectedValue = true
      Supplier<Boolean> supplier = Mock(Supplier){
         get() >>> [{throw new RuntimeException('Ooopsie')},{expectedValue}]
      }
      Consumer<Boolean> consumer = {it ->
         assert it == expectedValue
      }
      when:
      new RepeatUntilSuccessTask<>(supplier, consumer, scheduleFunction, backoffHelper).start()
      then:
      noExceptionThrown()
   }
}
