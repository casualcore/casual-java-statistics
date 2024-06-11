/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics.pool;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class EventLoopGroupFactory
{
    private static final EventLoopGroup INSTANCE = new NioEventLoopGroup();
    private EventLoopGroupFactory()
    {}
    public static EventLoopGroup getInstance()
    {
        return INSTANCE;
    }
}
