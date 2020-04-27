package com.tier.one.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tier.one.FrontLocal;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/front")
public class FrontService  {

    private static final Logger logger = LoggerFactory.getLogger(FrontService.class);

    @EJB
    FrontLocal front;

    @Context
    private HttpServletRequest servletRequest;

    @GET
    @Path("/ask")
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
