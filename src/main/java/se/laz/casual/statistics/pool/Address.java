/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics.pool;

import java.util.Objects;

public record Address(String hostName, Integer portNumber)
{
    public Address
    {
        Objects.requireNonNull(hostName, "hostName cannot be null");
        Objects.requireNonNull(portNumber, "portNumber cannot be null");
    }
    public String connectionName()
    {
        return String.format("%s:%s",hostName, portNumber);
    }
}
