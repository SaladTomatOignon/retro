package fr.umlv.retro.web;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("/conversion")
public class Conversion {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{test}_{ptdr}")
    public String hello(@PathParam String test, @PathParam String ptdr) {
        return test + " " + ptdr;
    }
    
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/convert")
    public String getWebForm(Object form) {
    	System.out.println(form);
    	return "";
    }
    
}
