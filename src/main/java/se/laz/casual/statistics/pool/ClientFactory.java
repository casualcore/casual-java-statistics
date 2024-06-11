/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics.pool;

import se.laz.casual.statistics.AugmentedEventStore;

import java.util.logging.Logger;

public final class ClientFactory
{
    private static final Logger LOG = Logger.getLogger(ClientFactory.class.getName());
    private ClientFactory()
    {}
    public static Client createClient(Address address, ClientListener listener, AugmentedEventStore store)
    {
        Client client = Client.of(address, listener, store);
        client.connect().join();
        LOG.info(() ->"Connected to " + address);
        return client;
    }
}
