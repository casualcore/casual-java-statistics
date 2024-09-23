/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics.pool;

// An interface for when a client is disconnected
public interface ClientListener
{
    void disconnected(Client client);
}
