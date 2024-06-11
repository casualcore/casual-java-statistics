/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public record ServiceCallAccumulatedData(long numberOfServiceCalls, Duration totalTime, Duration minTime, Duration maxTime, long numberOfPending, Duration totalPendingTime, LocalDateTime lastCall)
{
    public ServiceCallAccumulatedData
    {
        Objects.requireNonNull(totalTime, "total time cannot be null");
        Objects.requireNonNull(minTime, "min time cannot be null");
        Objects.requireNonNull(maxTime, "max time cannot be null");
        Objects.requireNonNull(totalPendingTime, "total pending time cannot be null");
        Objects.requireNonNull(lastCall, "last call cannot be null");
    }

    public ServiceCallAccumulatedData accumulate(ServiceCallData serviceCallData)
    {
        Duration newTotalTime = totalTime.plus(serviceCallData.callTimeInMicroseconds());
        Duration newMinTime = serviceCallData.callTimeInMicroseconds().compareTo(minTime) < 0 ? serviceCallData.callTimeInMicroseconds() : minTime;
        Duration newMaxTime = serviceCallData.callTimeInMicroseconds().compareTo(maxTime) > 0 ? serviceCallData.callTimeInMicroseconds() : maxTime;
        Duration newTotalPendingTime = !serviceCallData.pendingTimeInMicroseconds().isZero() ? totalPendingTime.plus(serviceCallData.pendingTimeInMicroseconds()) : totalPendingTime;
        long newNumberOfPending = !serviceCallData.pendingTimeInMicroseconds().isZero() ? numberOfPending + 1 : numberOfPending;
        return new ServiceCallAccumulatedData(numberOfServiceCalls + 1, newTotalTime, newMinTime, newMaxTime, newNumberOfPending, newTotalPendingTime, serviceCallData.lastCall());
    }
    public Duration averageTime()
    {
        return totalTime.dividedBy(numberOfServiceCalls);
    }
    public Duration averagePendingTime()
    {
        if(0 == numberOfPending)
        {
            return Duration.ZERO;
        }
        return totalPendingTime.dividedBy(numberOfPending);
    }
    public static Builder newBuilder()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private ServiceCallData serviceCallData;
        public Builder withServiceCallData(ServiceCallData serviceCallData)
        {
            Objects.requireNonNull(serviceCallData, "serviceCallData cannot be null");
            this.serviceCallData = serviceCallData;
            return this;
        }
        public ServiceCallAccumulatedData build()
        {
            long numberOfPending = serviceCallData.pendingTimeInMicroseconds().isZero() ? 0 : 1;
            return new ServiceCallAccumulatedData(1, serviceCallData.callTimeInMicroseconds(), serviceCallData.callTimeInMicroseconds(), serviceCallData.callTimeInMicroseconds(), numberOfPending, serviceCallData.pendingTimeInMicroseconds(), serviceCallData.lastCall());
        }
    }
}
