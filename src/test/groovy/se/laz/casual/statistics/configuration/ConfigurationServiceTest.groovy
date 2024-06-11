/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics.configuration


import se.laz.casual.statistics.pool.Address
import spock.lang.Specification

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable

class ConfigurationServiceTest extends Specification
{
   def 'host configuration from file'()
   {
      given:
      Configuration expected = new Configuration(addresses)
      ConfigurationService instance
      when:
      Configuration actual
      withEnvironmentVariable( ConfigurationService.ENV_VAR_NAME, "src/test/resources/" + file )
                .execute( {
                   instance = ConfigurationService.of()
                   actual = instance.getConfiguration()
                })
      then:
      actual == expected
      where:
      file                || addresses
      'config.json'  || [new Address('fast', 2134), new Address('slow', 556)]
   }
}
