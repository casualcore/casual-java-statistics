/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics.pool;

import se.laz.casual.api.util.work.BackoffHelper;
import se.laz.casual.statistics.AugmentedEventStore;
import se.laz.casual.statistics.AugmentedEventStoreFactory;
import se.laz.casual.statistics.configuration.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class ClientPool implements ClientListener
{
    private static final Logger LOG = Logger.getLogger(ClientPool.class.getName());
    private final List<Client> clients = new ArrayList<>();
    private final UUID domainId;
    private final Configuration configuration;
    private final ScheduleFunction scheduleFunction;
    private final CreateClientFunction createClientFunction;
    long maxBackoffMilliseconds;
    private ClientPool(Configuration config, long maxBackoffMilliseconds, ScheduleFunction scheduleFunction, CreateClientFunction createClientFunction, UUID domainId)
    {
        this.configuration = config;
        this.maxBackoffMilliseconds = maxBackoffMilliseconds;
        this.scheduleFunction = scheduleFunction;
        this.createClientFunction = createClientFunction;
        this.domainId = domainId;
    }
    public static ClientPool of(Configuration config, long maxBackoffMilliseconds, ScheduleFunction scheduleFunction, CreateClientFunction createClientFunction, UUID domainId)
    {
        Objects.requireNonNull(config, "config cannot be null");
        Objects.requireNonNull(scheduleFunction, "scheduleFunction cannot be null");
        Objects.requireNonNull(createClientFunction, "clientCreator cannot be null");
        Objects.requireNonNull(domainId, "domainId cannot be null");
        return new ClientPool(config, maxBackoffMilliseconds, scheduleFunction, createClientFunction, domainId);
    }
    public void connect()
    {
        configuration.addresses().parallelStream().forEach(this::connect);
    }
    private void connect(Address address)
    {
        Objects.requireNonNull(configuration, "configuration cannot be null");
        Objects.requireNonNull(scheduleFunction, "scheduleFunction cannot be null");
        AugmentedEventStore eventStore = AugmentedEventStoreFactory.getStore(domainId);
        Supplier<Client> clientSupplier = () -> createClientFunction.create(address, this, eventStore);
        Consumer<Client> clientConsumer = clients::add;
        new RepeatUntilSuccessTask<>(clientSupplier, clientConsumer, scheduleFunction, BackoffHelper.of(maxBackoffMilliseconds)).start();
    }
    @Override
    public void disconnected(Client client)
    {
        LOG.info(() ->"Disconnected from " + client);
        clients.removeIf(instance -> Objects.equals(instance, client));
        connect(client.getHost());
    }
}
