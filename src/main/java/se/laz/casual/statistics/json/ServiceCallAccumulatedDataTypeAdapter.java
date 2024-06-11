/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import se.laz.casual.statistics.ServiceCallAccumulatedData;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;

import static se.laz.casual.statistics.TimeConverter.toSecondsWithPrecision;

public class ServiceCallAccumulatedDataTypeAdapter implements JsonSerializer<ServiceCallAccumulatedData>
{
    @Override
    public JsonElement serialize(ServiceCallAccumulatedData src, Type type, JsonSerializationContext context)
    {
        if (src == null)
        {
            return JsonNull.INSTANCE;
        }
        final int precision = 3;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("numberOfServiceCalls", src.numberOfServiceCalls());
        jsonObject.addProperty("averageTime", toSecondsWithPrecision(src.averageTime(), precision));
        jsonObject.addProperty("minTime", toSecondsWithPrecision(src.minTime(), precision));
        jsonObject.addProperty("maxTime", toSecondsWithPrecision(src.maxTime(), precision));
        jsonObject.addProperty("numberOfPending", src.numberOfPending());
        jsonObject.addProperty("pendingAverageTime", toSecondsWithPrecision(src.averagePendingTime(), precision));
        String isoLocalDateTime = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(src.lastCall());
        jsonObject.addProperty("lastCall", isoLocalDateTime);
        return jsonObject;
    }
}
