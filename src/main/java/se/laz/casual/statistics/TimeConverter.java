/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TimeConverter
{
    public static final float MICROSECONDS_TO_SECONDS_FACTOR = 1000_000F;
    private TimeConverter()
    {}
    public static Instant toInstant(long microseconds)
    {
        return Instant.EPOCH.plus(microseconds, ChronoUnit.MICROS);
    }

    public static long toMicroseconds(Instant instant)
    {
        return ChronoUnit.MICROS.between(Instant.EPOCH, instant);
    }
    public static long toMicroseconds(Duration duration)
    {
        return duration.getSeconds() * 1000_000L + duration.getNano() / 1000L;
    }
    public static float toSecondsWithPrecision(Duration value, int precision)
    {
        return roundUpWithPrecision(toMicroseconds(value) / MICROSECONDS_TO_SECONDS_FACTOR, precision);
    }
    public static float roundUpWithPrecision(float value, int precision)
    {
        return BigDecimal.valueOf(value).setScale(precision, RoundingMode.UP).floatValue();
    }
}
