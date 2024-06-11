/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics.configuration;

import se.laz.casual.api.external.json.JsonProviderFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

public class ConfigurationService
{
    public static final String ENV_VAR_NAME = "CASUAL_STATISTICS_CONFIGURATION_FILE";
    private ConfigurationService()
    {
    }
    public static ConfigurationService of()
    {
        return new ConfigurationService();
    }
    public Configuration getConfiguration()
    {
        String configurationFile = Optional.ofNullable(System.getenv(ENV_VAR_NAME)).orElseThrow(() -> new ConfigurationServiceException("Missing environment variable " + ENV_VAR_NAME));
        Configuration configuration = getConfiguration(configurationFile);
        if(configuration.addresses().isEmpty())
        {
            throw new ConfigurationServiceException("There are zero addresses configured in the pool configuration file " +  System.getenv(ENV_VAR_NAME));
        }
        return configuration;
    }
    private Configuration getConfiguration(String filename)
    {
        Objects.requireNonNull(filename, "filename cannot be null");
        try
        {
            return JsonProviderFactory.getJsonProvider().fromJson(new FileReader(filename, StandardCharsets.UTF_8), Configuration.class);
        }
        catch (FileNotFoundException e)
        {
            throw new ConfigurationServiceException("could not find configuration file: " + filename, e);
        }
        catch (IOException e)
        {
            throw new ConfigurationServiceException("failed to load configuration file: " + filename, e);
        }
    }
}
