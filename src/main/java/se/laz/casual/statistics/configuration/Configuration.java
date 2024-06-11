/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics.configuration;

import se.laz.casual.statistics.pool.Address;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record Configuration(List<Address> addresses)
{
    public Configuration
    {
        Objects.requireNonNull(addresses, "addresses cannot be null");
    }
    public List<Address> addresses()
    {
        return Collections.unmodifiableList(addresses);
    }
}
