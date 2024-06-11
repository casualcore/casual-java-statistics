/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics.pool;

import se.laz.casual.statistics.AugmentedEventStore;

@FunctionalInterface
public interface CreateClientFunction
{
    Client create(Address address, ClientListener listener, AugmentedEventStore eventStore);
}
