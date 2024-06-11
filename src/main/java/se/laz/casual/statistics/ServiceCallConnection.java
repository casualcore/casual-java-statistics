/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics;

import java.util.Objects;

public record ServiceCallConnection(String connectionName)
{
    public ServiceCallConnection
    {
        Objects.requireNonNull(connectionName, "connectionName cannot be null");
    }
}
