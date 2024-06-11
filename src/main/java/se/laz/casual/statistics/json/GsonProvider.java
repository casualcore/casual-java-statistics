/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import se.laz.casual.statistics.ServiceCallAccumulatedData;

public class GsonProvider
{
    private static final Gson GSON;
    static
    {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ServiceCallAccumulatedData.class, new ServiceCallAccumulatedDataTypeAdapter());
        GSON = builder.enableComplexMapKeySerialization().setPrettyPrinting().create();
    }
    private GsonProvider()
    {}
    public static Gson getGson()
    {
        return GSON;
    }
}
