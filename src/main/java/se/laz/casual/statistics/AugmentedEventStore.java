/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics;

import java.util.Objects;
import java.util.concurrent.BlockingDeque;

// Use storage to store augmented events
public class AugmentedEventStore
{
    private final BlockingDeque<AugmentedEvent> events;
    public AugmentedEventStore(BlockingDeque<AugmentedEvent> events)
    {
        Objects.requireNonNull(events, "events cannot be null");
        this.events = events;
    }
    public void put(AugmentedEvent event)
    {
        Objects.requireNonNull(event, "event can not be null");
        events.add(event);
    }
    public AugmentedEvent take()
    {
        try
        {
            return events.takeFirst();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new AugmentedEventStoreInterruptedException("AugmentedEventStore::takeFirst interrupted", e);
        }
    }
}
