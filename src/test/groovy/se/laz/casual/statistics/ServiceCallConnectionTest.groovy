/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics


import spock.lang.Specification

class ServiceCallConnectionTest extends Specification
{
   def 'failed creation'()
   {
      when:
      new ServiceCallConnection(null)
      then:
      thrown(NullPointerException)
   }

   def 'ok creation'()
   {
      given:
      def connectionName = 'foo-connection'
      when:
      ServiceCallConnection connection = new ServiceCallConnection(connectionName)
      then:
      connection.connectionName() == connectionName
   }
}
