/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics.pool;

import se.laz.casual.api.util.work.BackoffHelper;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public record RepeatUntilSuccessTask<T>(Supplier<T> supplier, Consumer<T> consumer, ScheduleFunction scheduleFunction, BackoffHelper backoffHelper) implements Runnable
{
    private static final Logger LOG = Logger.getLogger(RepeatUntilSuccessTask.class.getName());
    public RepeatUntilSuccessTask
    {
        Objects.requireNonNull(supplier, "supplier cannot be null");
        Objects.requireNonNull(consumer, "consumer cannot be null");
        Objects.requireNonNull(scheduleFunction, "scheduleFunction cannot be null");
        Objects.requireNonNull(backoffHelper, "backoffHelper cannot be null");
    }
    public void start()
    {
        schedule();
    }
    @Override
    public void run()
    {
        try
        {
            consumer.accept(supplier.get());
        }
        catch(Exception e)
        {
            LOG.log(Level.WARNING, e, () -> "failed - will reschedule");
            schedule();
        }
    }
    private void schedule()
    {
        long currentBackoff = backoffHelper.registerFailure();
        scheduleFunction.schedule(this, currentBackoff, TimeUnit.MILLISECONDS);
    }
}
