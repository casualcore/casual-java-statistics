/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics;

@FunctionalInterface
public interface StoreFunction
{
    void store(ServiceCallConnection connection, ServiceCall serviceCall, ServiceCallData data);
}
