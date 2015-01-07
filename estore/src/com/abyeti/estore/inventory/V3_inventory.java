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
import org.codehaus.jettison.json.JSONObject;

import com.abyeti.estore.db.*;
import com.abyeti.util.ToJSON;

@Path("/v3/inventory")
public class V3_inventory {

	@POST
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public Response addPcParts2(String incomingData) throws Exception {
		
		String returnString = null;
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			
			/*
			 * We can create a new instance and it will accept a JSON string
			 * By doing this, we can now access the data.
			 */
			JSONObject partsData = new JSONObject(incomingData);
			System.out.println( "jsonData: " + partsData.toString() );
			conn = PGDBConn.dbConnection();
			ps = conn.prepareStatement("INSERT INTO employee(ssn,ename,age) VALUES(?,?,?) ");
			ps.setInt(1, partsData.optInt("ssn"));
			ps.setString(2, partsData.optString("ename"));
			ps.setInt(3, partsData.optInt("age"));
			
			ps.executeUpdate();
			
			jsonObject.put("HTTP_CODE", "200");
			jsonObject.put("MSG", "Item has been entered successfully, Version 3");
			returnString = jsonArray.put(jsonObject).toString();
			System.out.println( "returnString: " + returnString );
			
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Unable to enter Item").build();
		}
		
		return Response.ok(returnString).build();
	}
	
}