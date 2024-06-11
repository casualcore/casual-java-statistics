/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics;

import se.laz.casual.statistics.entries.EntriesPerConnection;
import se.laz.casual.statistics.entries.Entry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceCallStatistics
{
    private static final String CONNECTION_CAN_NOT_BE_NULL = "connection can not be null";
    private static final String SERVICE_CALL_CAN_NOT_BE_NULL = "serviceCall can not be null";
    private static final Map<ServiceCall, ServiceCallAccumulatedData> EMPTY_MAP = new ConcurrentHashMap<>();
    private static final Map<ServiceCallConnection, Map<ServiceCall, ServiceCallAccumulatedData>> DATA = new ConcurrentHashMap<>();
    private ServiceCallStatistics()
    {}
    public static void store(ServiceCallConnection connection, ServiceCall serviceCall, ServiceCallData data)
    {
        Objects.requireNonNull(connection, CONNECTION_CAN_NOT_BE_NULL);
        Objects.requireNonNull(serviceCall, SERVICE_CALL_CAN_NOT_BE_NULL);
        Objects.requireNonNull(data, "data can not be null");
        DATA.compute(connection, (conn, accumulatedByServiceCall) -> {
            if (accumulatedByServiceCall == null)
            {
                accumulatedByServiceCall = new ConcurrentHashMap<>();
            }
            accumulatedByServiceCall.compute(serviceCall, (call, maybeCachedData) -> null == maybeCachedData ?  ServiceCallAccumulatedData.newBuilder().withServiceCallData(data).build() : maybeCachedData.accumulate(data));
            return accumulatedByServiceCall;
        });
    }
    public static Optional<ServiceCallAccumulatedData> get(ServiceCallConnection connection, ServiceCall serviceCall)
    {
        Objects.requireNonNull(connection, CONNECTION_CAN_NOT_BE_NULL);
        Objects.requireNonNull(serviceCall, SERVICE_CALL_CAN_NOT_BE_NULL);
        Map<ServiceCall, ServiceCallAccumulatedData> accumulatedDataByServiceCall = DATA.get(connection);
        return null == accumulatedDataByServiceCall ? Optional.empty() :Optional.ofNullable(accumulatedDataByServiceCall.get(serviceCall));
    }
    public static List<EntriesPerConnection> get(ServiceCallConnection connection)
    {
        Objects.requireNonNull(connection, CONNECTION_CAN_NOT_BE_NULL);
        List<Entry> entries = Optional.ofNullable(DATA.get(connection))
                                      .orElseGet(() -> EMPTY_MAP)
                                      .entrySet()
                                      .stream()
                                      .map(item -> new Entry(item.getKey(), item.getValue()))
                                      .toList();
        return entries.isEmpty() ? Collections.emptyList() : List.of(new EntriesPerConnection(connection, entries));
    }
    public static List<EntriesPerConnection> getAll()
    {
        List<EntriesPerConnection> result = new ArrayList<>();
        DATA.keySet().forEach(item -> result.addAll(get(item)));
        return result;
    }
}
