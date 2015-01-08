package com.abyeti.estore.inventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONArray;

import com.abyeti.db.*;
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

	@Path("/{age}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response returnEmployeesImproved(@PathParam("age") int age) throws Exception {
		
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
	
	@POST
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
	//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addPcParts(String incomingData) throws Exception {
		
		String returnString = null;
		Connection conn = null;
		PreparedStatement ps = null;
		//JSONArray jsonArray = new JSONArray(); //not needed
		
		try {
			System.out.println("incomingData: " + incomingData);
			
			/*
			 * ObjectMapper is from Jackson Processor framework
			 * http://jackson.codehaus.org/
			 * 
			 * Using the readValue method, you can parse the json from the http request
			 * and data bind it to a Java Class.
			 */
			ObjectMapper mapper = new ObjectMapper();  
			ItemEntry itemEntry = mapper.readValue(incomingData, ItemEntry.class);
			
			conn = PGDBConn.dbConnection();
			ps = conn.prepareStatement("INSERT INTO employee(ssn,ename,age) VALUES(?,?,?) ");
			ps.setInt(1, itemEntry.ssn);
			ps.setString(2, itemEntry.ename);
			ps.setInt(3, itemEntry.age);
			
			ps.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Server was not able to process your request").build();
		}
		returnString = "Item inserted";
		return Response.ok(returnString).build();
	}
}

/*
* This is a class used by the addPcParts method.
* Used by the Jackson Processor
* 
* Note: for re-usability you should place this in its own package.
*/
class ItemEntry {
	public int ssn;
	public String ename;
	public int age;
}