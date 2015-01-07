package com.abyeti.estore.inventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;

import com.abyeti.estore.db.*;
import com.abyeti.util.ToJSON;

@Path("/v2/inventory")
public class V2_inventory {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response returnEmployees(@QueryParam("age") int age) throws Exception {
		
		PreparedStatement query = null;
		Connection conn = null;
		String returnString = null;
		Response rb = null;
		
		try {
			conn = PGDBConn.dbConnection();
			query = conn.prepareStatement("SELECT ename,age FROM employee WHERE age = ?");
			query.setInt(1,age);
			
			ResultSet rs = query.executeQuery();
			
			ToJSON converter = new ToJSON();
			JSONArray json = new JSONArray();
			
			json = converter.toJSONArray(rs);
			query.close(); //close connection
			
			returnString = json.toString();
			rb = Response.ok(returnString).build();
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			 if (conn != null) conn.close();
		}
		
		return rb;
	}
	
}