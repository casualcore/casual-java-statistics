/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics.pool

import se.laz.casual.statistics.AugmentedEventStore
import se.laz.casual.statistics.configuration.Configuration
import spock.lang.Specification

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

class ClientPoolTest extends Specification
{
   def '2 clients in pool'()
   {
      given:
      Address addressOne = new Address('fast host', 1234)
      Address addressTwo = new Address('slow host', 1234)
      Configuration config = new Configuration([addressOne, addressTwo])
      long backoffMillis = 30_000L
      ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor()
      ScheduleFunction scheduleFunction = executorService::schedule
      CountDownLatch latch = new CountDownLatch(2)
      Client clientOne = Mock(Client){
         1 * connect() >> {
            CompletableFuture<Boolean> future = new CompletableFuture<>()
            future.complete(true)
            latch.countDown()
            return future
         }
      }
      Client clientTwo = Mock(Client){
         1 * connect() >> {
            CompletableFuture<Boolean> future = new CompletableFuture<>()
            future.complete(true)
            latch.countDown()
            return future
         }
      }
      CreateClientFunction createClientFunction = { Address address, ClientListener listener, AugmentedEventStore eventStore ->
         Client client =  address == addressOne ? clientOne : clientTwo
         client.connect().join()
         return client;
      }
      UUID domainId = UUID.randomUUID()
      when:
      Executors.newSingleThreadExecutor().submit ({
         ClientPool pool = ClientPool.of(config, backoffMillis, scheduleFunction, createClientFunction, domainId)
         pool.connect()
      })
      latch.await()
      then:
      noExceptionThrown()
   }
}
