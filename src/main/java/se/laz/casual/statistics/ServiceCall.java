/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics;

import se.laz.casual.event.Order;

import java.util.Objects;

public record ServiceCall(String serviceName, Order order)
{
    public ServiceCall
    {
        Objects.requireNonNull(serviceName, "serviceName cannot be null");
        Objects.requireNonNull(order, "order cannot be null");
    }
}
