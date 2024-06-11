/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics;

import java.util.Objects;

public record AugmentedEvent(ServiceCallConnection connection, ServiceCall serviceCall, ServiceCallData data)
{
    public AugmentedEvent
    {
        Objects.requireNonNull(connection, "connection can not be null");
        Objects.requireNonNull(serviceCall, "serviceCall can not be null");
        Objects.requireNonNull(data, "serviceCallData can not be null");
    }
}
