/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics.pool

import se.laz.casual.api.flags.ErrorState
import se.laz.casual.event.Order
import se.laz.casual.event.ServiceCallEvent
import se.laz.casual.statistics.*
import se.laz.casual.test.CasualEmbeddedServer
import spock.lang.Shared
import spock.lang.Specification

import javax.transaction.xa.Xid
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import static se.laz.casual.statistics.TimeConverter.toMicroseconds

class ClientTest extends Specification
{
   @Shared
   CasualEmbeddedServer embeddedServer

   String service1 = "test1"
   String parent1 = "parent"
   int pid1 = 123
   UUID execution1 = UUID.randomUUID()
   Xid transactionId1 = Mock(Xid)
   long pending1 = 5L
   ErrorState code1 = ErrorState.OK
   Order order1 = Order.CONCURRENT

   Instant start1 = ZonedDateTime.parse( "2024-04-15T12:34:56.123456Z", DateTimeFormatter.ISO_ZONED_DATE_TIME).toInstant()
   Instant end1 = ZonedDateTime.parse( "2024-04-15T12:35:04.123456Z", DateTimeFormatter.ISO_ZONED_DATE_TIME).toInstant()
   ServiceCallEvent event = ServiceCallEvent.createBuilder(  )
           .withService(service1)
           .withParent(parent1)
           .withPID(pid1)
           .withExecution(execution1)
           .withTransactionId(transactionId1)
           .withPending( pending1 )
           .withStart( start1 )
           .withEnd( end1 )
           .withCode(code1)
           .withOrder(order1)
           .build()

   @Shared
   URI eventServerUrl
   UUID domainId = UUID.randomUUID()

   def setupSpec()
   {
      embeddedServer = CasualEmbeddedServer.newBuilder()
              .eventServerEnabled( true )
              .build(  )
      embeddedServer.start(  )

      eventServerUrl = URI.create("tcp://localhost:" + embeddedServer.getEventServerPort(  ).get() )
   }

   def cleanupSpec()
   {
      if( embeddedServer != null )
      {
         embeddedServer.shutdown(  )
      }
   }

   def connectAndGetEvent()
   {
      given:
      AugmentedEventStore eventStore = AugmentedEventStoreFactory.getStore(domainId)
      def clientListener = Mock(ClientListener){
         1 * disconnected(_)
      }
      Address host = new Address(eventServerUrl.getHost(), eventServerUrl.getPort())
      Client client = Client.of(host, clientListener, eventStore)
      client.connect().join()
      when:
      embeddedServer.publishEvent( event )
      AugmentedEvent augmentedEvent = eventStore.take()
      then:
      augmentedEvent != null
      augmentedEvent.serviceCall()  == new ServiceCall(event.service, order1)
      augmentedEvent.connection() == new ServiceCallConnection(host.connectionName())
      augmentedEvent.data() == ServiceCallData.newBuilder()
                                              .withStart(toMicroseconds(start1))
                                              .withEnd(toMicroseconds(end1))
                                              .withPending(pending1)
                                              .build()
      when:
      embeddedServer.shutdown()
      then:
      noExceptionThrown()
   }

}
