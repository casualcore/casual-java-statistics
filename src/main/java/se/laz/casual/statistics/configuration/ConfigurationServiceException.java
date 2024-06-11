/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics.configuration;

import se.laz.casual.api.CasualRuntimeException;

import java.io.Serial;

public class ConfigurationServiceException extends CasualRuntimeException
{
    @Serial
    private static final long serialVersionUID = 1L;
    public ConfigurationServiceException(String s, Throwable e)
    {
        super(s, e);
    }

    public ConfigurationServiceException(String s)
    {
        super(s);
    }
}
