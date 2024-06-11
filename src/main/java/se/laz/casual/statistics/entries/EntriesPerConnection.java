/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics.entries;

import se.laz.casual.statistics.ServiceCallConnection;

import java.util.List;
import java.util.Objects;

public record EntriesPerConnection(ServiceCallConnection connection, List<Entry> entries)
{
    public EntriesPerConnection
    {
        Objects.requireNonNull(connection, "connection can not be null");
        Objects.requireNonNull(entries, "entries can not be null");
    }
}
