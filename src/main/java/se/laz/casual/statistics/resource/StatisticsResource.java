/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.statistics.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import se.laz.casual.statistics.ServiceCallConnection;
import se.laz.casual.statistics.ServiceCallStatistics;
import se.laz.casual.statistics.json.GsonProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/statistics")
public class StatisticsResource
{
    private static final Logger LOG = Logger.getLogger(StatisticsResource.class.getName());
    @Path("/")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response all()
    {
        try
        {
            String json = GsonProvider.getGson().toJson(ServiceCallStatistics.getAll());
            return Response.ok(json).build();
        }
        catch(Exception e)
        {
            LOG.log(Level.WARNING, e, () -> "Failed to get statistics");
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
    @Path("{connection}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response perConnection(@PathParam("connection") String connection)
    {
        try
        {
            String json = GsonProvider.getGson().toJson(ServiceCallStatistics.get(new ServiceCallConnection(connection)));
            return Response.ok(json).build();
        }
        catch(Exception e)
        {
            LOG.log(Level.WARNING, e, () -> "Failed to get statistics for connection " + connection);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
