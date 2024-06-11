/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics.pool;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@FunctionalInterface
public interface ScheduleFunction
{
    // sonar
    // This is the exact signature of the JDKs:
    // ScheduledExecutorService::schedule
    @SuppressWarnings("java:S1452")
    ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit);
}
