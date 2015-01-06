package com.abyeti.estore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/myclass")
public class MyClass {
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String returnTitle() {
		return "<h3>Hello World </h3>";
	}	
}
