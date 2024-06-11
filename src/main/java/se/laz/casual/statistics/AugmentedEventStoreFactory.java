/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public record AugmentedEventStoreFactory()
{
    private static final Map<UUID, AugmentedEventStore> STORES = new ConcurrentHashMap<>();
    public static AugmentedEventStore getStore(UUID domainId)
    {
        return STORES.computeIfAbsent(domainId, id -> new AugmentedEventStore(new LinkedBlockingDeque<>()));
    }
}
