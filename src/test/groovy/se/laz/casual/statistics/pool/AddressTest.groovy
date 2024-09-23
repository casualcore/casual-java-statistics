/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics.pool

import spock.lang.Specification

class AddressTest extends Specification
{
   def 'failed creation'()
   {
      when:
      new Address(host, port)
      then:
      thrown(NullPointerException)
      where:
      host   || port
      null   || 1234
      'asdf' || null
   }
   def 'working as expected'()
   {
      given:
      def hostName = 'shiny'
      def portNumber = 1234
      when:
      def host = new Address(hostName, portNumber)
      then:
      host.hostName() == hostName
      host.portNumber() == portNumber
      host.connectionName() == "${hostName}:${portNumber}"
   }
}
