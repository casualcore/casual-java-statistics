/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics.pool;

import se.laz.casual.statistics.AugmentedEventStore;

public final class ClientFactory
{
    private ClientFactory()
    {}
    // We build with logging level WARN, however we do want to output this information regardless
    // it is also not a warning
    @SuppressWarnings("java:S106")
    public static Client createClient(Address address, ClientListener listener, AugmentedEventStore store)
    {
        Client client = Client.of(address, listener, store);
        client.connect().join();
        System.out.println("Connected to " + address);
        return client;
    }
}
