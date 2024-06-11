/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics.pool;

import io.netty.channel.socket.nio.NioSocketChannel;
import se.laz.casual.event.Order;
import se.laz.casual.event.ServiceCallEvent;
import se.laz.casual.event.client.ConnectionObserver;
import se.laz.casual.event.client.EventClient;
import se.laz.casual.event.client.EventClientBuilder;
import se.laz.casual.event.client.EventObserver;
import se.laz.casual.statistics.AugmentedEvent;
import se.laz.casual.statistics.AugmentedEventStore;
import se.laz.casual.statistics.ServiceCall;
import se.laz.casual.statistics.ServiceCallConnection;
import se.laz.casual.statistics.ServiceCallData;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class Client implements EventObserver, ConnectionObserver
{
    private final Address address;
    private final ClientListener clientListener;
    private final AugmentedEventStore eventStore;

    private Client(Address address, ClientListener clientListener, AugmentedEventStore eventStore)
    {
        this.address = address;
        this.clientListener = clientListener;
        this.eventStore = eventStore;
    }
    public static Client of(Address address, ClientListener clientListener, AugmentedEventStore eventStore)
    {
        Objects.requireNonNull(address, "address can not be null");
        Objects.requireNonNull(clientListener, "clientListener can not be null");
        Objects.requireNonNull(eventStore, "eventStore can not be null");
        return new Client(address, clientListener, eventStore);
    }
    public CompletableFuture<Boolean> connect()
    {
        EventClient client = EventClientBuilder.createBuilder()
                                               .withHost(address.hostName())
                                               .withPort(address.portNumber())
                                               .withEventLoopGroup(EventLoopGroupFactory.getInstance())
                                               .withChannel(NioSocketChannel.class)
                                               .withConnectionObserver(this)
                                               .withEventObserver(this)
                                               .build();
        return client.connect();
    }
    public Address getHost()
    {
        return address;
    }
    @Override
    public void notify(ServiceCallEvent event)
    {
        ServiceCallConnection connection = new ServiceCallConnection(address.connectionName());
        ServiceCall serviceCall = new ServiceCall(event.getService(), Order.unmarshall(event.getOrder()));
        ServiceCallData data = ServiceCallData.newBuilder()
                                              .withStart(event.getStart())
                                              .withEnd(event.getEnd())
                                              .withPending(event.getPending())
                                              .build();
        eventStore.put(new AugmentedEvent(connection, serviceCall, data));
    }
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Client client))
        {
            return false;
        }
        return Objects.equals(address, client.address) && Objects.equals(clientListener, client.clientListener);
    }
    @Override
    public int hashCode()
    {
        return Objects.hash(address, clientListener);
    }
    @Override
    public String toString()
    {
        return "Client{" +
                "address=" + address +
                ", connectionObserver=" + clientListener +
                '}';
    }
    @Override
    public void disconnected(EventClient client)
    {
        clientListener.disconnected(this);
    }
}
