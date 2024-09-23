/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics.pool


import se.laz.casual.statistics.AugmentedEventStore
import se.laz.casual.statistics.AugmentedEventStoreFactory
import se.laz.casual.test.CasualEmbeddedServer
import spock.lang.Shared
import spock.lang.Specification

class ClientFactoryTest extends Specification
{
   @Shared
   CasualEmbeddedServer embeddedServer

   @Shared
   URI eventServerUrl
   UUID domainId = UUID.randomUUID()

   def setupSpec()
   {
      embeddedServer = CasualEmbeddedServer.newBuilder()
              .eventServerEnabled( true )
              .build(  )
      embeddedServer.start(  )

      eventServerUrl = URI.create("tcp://localhost:" + embeddedServer.getEventServerPort().get() )
   }

   def cleanupSpec()
   {
      if( embeddedServer != null )
      {
         embeddedServer.shutdown(  )
      }
   }

   def 'creates client, shutdown of server, client gets disconnected'()
   {
      given:
      AugmentedEventStore eventStore = AugmentedEventStoreFactory.getStore(domainId)
      def clientListener = Mock(ClientListener){
         1 * disconnected(_)
      }
      Address address = new Address(eventServerUrl.getHost(), eventServerUrl.getPort())
      when:
      Client client = ClientFactory.createClient(address, clientListener, eventStore)
      then:
      client != null
      when:
      embeddedServer.shutdown()
      then:
      noExceptionThrown()
   }
}
