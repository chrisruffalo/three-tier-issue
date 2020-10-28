package com.tier.one.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tier.one.FrontLocal;
import tier.recur.RecurLocal;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class Service {

    private static final Logger logger = LoggerFactory.getLogger(Service.class);

    @EJB
    FrontLocal front;

    @EJB
    RecurLocal recur;

    @Context
    private HttpServletRequest servletRequest;

    @GET
    @Path("/recur")
    public Response recur(@QueryParam("remote") final String remoteString, @QueryParam("depth") final String depthString) {
        int depth = 1;
        if (depthString != null && !depthString.isEmpty()) {
            try {
                depth = Integer.parseInt(depthString);
            } catch (NumberFormatException nfe) {
                return Response.serverError().build();
            }
        }
        final boolean remote = remoteString != null && !remoteString.isEmpty() && "true".equalsIgnoreCase(remoteString.trim());
        final String protocol = servletRequest.getScheme();
        return Response.ok(recur.call(protocol, remote, depth)).build();
    }

    @GET
    @Path("/tier")
    @Produces(MediaType.TEXT_PLAIN)
    public Response ask() {
        // get protocol from request
        final String protocol = servletRequest.getScheme();
        logger.info("Entry with protocol: {}", protocol);
        final String response = front.ask(protocol);
        return Response.ok(response).build();
    }

    @GET
    @Path("/health")
    @Produces(MediaType.TEXT_PLAIN)
    public Response health() {
        return Response.ok("healthy").build();
    }
}
