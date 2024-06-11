/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import se.laz.casual.api.util.time.InstantUtil;
import se.laz.casual.event.Order;
import se.laz.casual.statistics.ServiceCall;
import se.laz.casual.statistics.ServiceCallConnection;
import se.laz.casual.statistics.ServiceCallData;
import se.laz.casual.statistics.ServiceCallStatistics;
import se.laz.casual.statistics.TimeConverter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class StatisticsResourceTest
{
    @Test
    void testAllWithNoData()
    {
        given()
                .when().get("/statistics")
                .then()
                .statusCode(200)
                .contentType("application/json");
    }
    @Test
    void testAllWithData()
    {
        long callTimeMicroseconds = 8500;
        long pendingTimeMicroseconds = 2750;
        int precision = 3;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusNanos(callTimeMicroseconds * 1000);
        ServiceCallConnection connection = new ServiceCallConnection("asdf");
        ServiceCallConnection connectionTwo = new ServiceCallConnection("bazinga");
        ServiceCall serviceCall = new ServiceCall("fast-service", Order.CONCURRENT);
        ServiceCallData data = ServiceCallData.newBuilder()
                                              .withStart(InstantUtil.toEpochMicro(start.toInstant(ZoneOffset.UTC)))
                                              .withEnd(InstantUtil.toEpochMicro(end.toInstant(ZoneOffset.UTC)))
                                              .withPending(pendingTimeMicroseconds)
                                              .build();
        ServiceCall serviceCallTwo = new ServiceCall("slow-service", Order.CONCURRENT);
        ServiceCallStatistics.store(connection, serviceCall, data);
        ServiceCallStatistics.store(connection, serviceCallTwo, data);
        ServiceCallStatistics.store(connectionTwo, serviceCall, data);
        float expectedCallTimeInSeconds = TimeConverter.roundUpWithPrecision(8500 / TimeConverter.MICROSECONDS_TO_SECONDS_FACTOR, precision);
        float expectedPendingTimeInSeconds = TimeConverter.roundUpWithPrecision(2750 / TimeConverter.MICROSECONDS_TO_SECONDS_FACTOR, precision);
        given()
                .when().get("/statistics")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(2))
                .body("connection.connectionName", hasItems(connection.connectionName(), connectionTwo.connectionName()))
                .body("entries.flatten {it.serviceCall}.serviceName", hasItems(serviceCall.serviceName(), serviceCallTwo.serviceName()))
                .body("entries.flatten{it.accumulatedData}.numberOfServiceCalls", everyItem(is(1)))
                .body("entries.flatten{it.accumulatedData}.averageTime", everyItem(is(expectedCallTimeInSeconds)))
                .body("entries.flatten{it.accumulatedData}.minTime", everyItem(is(expectedCallTimeInSeconds)))
                .body("entries.flatten{it.accumulatedData}.maxTime", everyItem(is(expectedCallTimeInSeconds)))
                .body("entries.flatten{it.accumulatedData}.numberOfPending", everyItem(is(1)))
                .body("entries.flatten{it.accumulatedData}.pendingAverageTime", everyItem(is(expectedPendingTimeInSeconds)));
    }

    @Test
    void testConnectionWithNoData()
    {
        given()
                .when().get("/statistics/10.98.129.216:7698")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(0));
    }

    @Test
    void testConnectionWithData()
    {
        given()
                .when().get("/statistics/10.98.129.216:7698")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(0));
        long callTimeMicroseconds = 8500;
        long pendingTimeMicroseconds = 2750;
        int precision = 3;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusNanos(callTimeMicroseconds * 1000);
        ServiceCallConnection connection = new ServiceCallConnection("connectionOne");

        ServiceCall serviceCall = new ServiceCall("fast-service", Order.CONCURRENT);
        ServiceCallData data = ServiceCallData.newBuilder()
                                              .withStart(InstantUtil.toEpochMicro(start.toInstant(ZoneOffset.UTC)))
                                              .withEnd(InstantUtil.toEpochMicro(end.toInstant(ZoneOffset.UTC)))
                                              .withPending(pendingTimeMicroseconds)
                                              .build();

        ServiceCallStatistics.store(connection, serviceCall, data);
        float expectedCallTimeInSeconds = TimeConverter.roundUpWithPrecision(8500 / TimeConverter.MICROSECONDS_TO_SECONDS_FACTOR, precision);
        float expectedPendingTimeInSeconds = TimeConverter.roundUpWithPrecision(2750 / TimeConverter.MICROSECONDS_TO_SECONDS_FACTOR, precision);
        given()
                .when().get("/statistics/" + connection.connectionName())
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(1))
                .body("connection.connectionName", hasItems(connection.connectionName()))
                .body("entries.flatten {it.serviceCall}.serviceName", hasItems(serviceCall.serviceName()))
                .body("entries.flatten{it.accumulatedData}.numberOfServiceCalls", everyItem(is(1)))
                .body("entries.flatten{it.accumulatedData}.averageTime", everyItem(is(expectedCallTimeInSeconds)))
                .body("entries.flatten{it.accumulatedData}.minTime", everyItem(is(expectedCallTimeInSeconds)))
                .body("entries.flatten{it.accumulatedData}.maxTime", everyItem(is(expectedCallTimeInSeconds)))
                .body("entries.flatten{it.accumulatedData}.numberOfPending", everyItem(is(1)))
                .body("entries.flatten{it.accumulatedData}.pendingAverageTime", everyItem(is(expectedPendingTimeInSeconds)));

        // use another connection
        ServiceCallConnection connectionTwo = new ServiceCallConnection("connectionTwo");
        ServiceCall serviceCallTwo = new ServiceCall("slow-service", Order.CONCURRENT);
        ServiceCallStatistics.store(connectionTwo, serviceCallTwo, data);
        given()
                .when().get("/statistics/" + connectionTwo.connectionName())
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(1))
                .body("connection.connectionName", hasItems(connectionTwo.connectionName()))
                .body("entries.flatten {it.serviceCall}.serviceName", hasItems(serviceCallTwo.serviceName()))
                .body("entries.flatten{it.accumulatedData}.numberOfServiceCalls", everyItem(is(1)))
                .body("entries.flatten{it.accumulatedData}.averageTime", everyItem(is(expectedCallTimeInSeconds)))
                .body("entries.flatten{it.accumulatedData}.minTime", everyItem(is(expectedCallTimeInSeconds)))
                .body("entries.flatten{it.accumulatedData}.maxTime", everyItem(is(expectedCallTimeInSeconds)))
                .body("entries.flatten{it.accumulatedData}.numberOfPending", everyItem(is(1)))
                .body("entries.flatten{it.accumulatedData}.pendingAverageTime", everyItem(is(expectedPendingTimeInSeconds)));
    }

}