/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics.entries;

import se.laz.casual.statistics.ServiceCall;
import se.laz.casual.statistics.ServiceCallAccumulatedData;

import java.util.Objects;

public record Entry(ServiceCall serviceCall, ServiceCallAccumulatedData accumulatedData)
{
    public Entry
    {
        Objects.requireNonNull(serviceCall, "serviceCall can not be null");
        Objects.requireNonNull(accumulatedData, "accumulatedData can not be null");
    }
}
