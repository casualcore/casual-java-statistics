/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics;

import java.util.Objects;
import java.util.function.BooleanSupplier;

public record EventWriter(AugmentedEventStore eventStore, StoreFunction storeFunction, BooleanSupplier condition)
{
    public EventWriter
    {
        Objects.requireNonNull(eventStore, "eventStore can not be null");
        Objects.requireNonNull(storeFunction, "storeFunction can not be null");
        Objects.requireNonNull(condition, "condition can not be null");
    }
    public void waitForMessageAndStore()
    {
        while(condition.getAsBoolean())
        {
            AugmentedEvent event = eventStore.take();
            storeFunction.store(event.connection(), event.serviceCall(), event.data());
        }
    }
}
