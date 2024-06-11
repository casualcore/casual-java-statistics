/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import se.laz.casual.statistics.configuration.Configuration;
import se.laz.casual.statistics.configuration.ConfigurationService;
import se.laz.casual.statistics.pool.ClientFactory;
import se.laz.casual.statistics.pool.ClientPool;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@QuarkusMain
public class Main
{
    public static void main(String... args)
    {
        Quarkus.run(StatisticsApp.class, args);
    }
    public static class StatisticsApp implements QuarkusApplication
    {
        @Override
        public int run(String... args)
        {
            UUID domainId = UUID.randomUUID();
            try(ExecutorService executorService = Executors.newSingleThreadExecutor();
                ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);)
            {
                EventWriter eventWriter = new EventWriter(AugmentedEventStoreFactory.getStore(domainId), ServiceCallStatistics::store, () -> true);
                executorService.submit(eventWriter::waitForMessageAndStore);
                Configuration configuration = ConfigurationService.of().getConfiguration();
                ClientPool pool = ClientPool.of(configuration, 30_000L, scheduledExecutorService::schedule, ClientFactory::createClient, domainId);
                pool.connect();
                Quarkus.waitForExit();
            }
            return 0;
        }
    }
}
