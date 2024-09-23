/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics;

import java.util.Objects;
import java.util.function.BooleanSupplier;

// Waits for events from an augmented event store and uses a store function to store them in another place
// That place could be anywhere but currently is in mem
public class EventWriter
{
    private final AugmentedEventStore eventStore;
    private final StoreFunction storeFunction;
    private final BooleanSupplier condition;
    public EventWriter(AugmentedEventStore eventStore, StoreFunction storeFunction, BooleanSupplier condition)
    {
        Objects.requireNonNull(eventStore, "eventStore can not be null");
        Objects.requireNonNull(storeFunction, "storeFunction can not be null");
        Objects.requireNonNull(condition, "condition can not be null");
        this.eventStore = eventStore;
        this.storeFunction = storeFunction;
        this.condition = condition;
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
