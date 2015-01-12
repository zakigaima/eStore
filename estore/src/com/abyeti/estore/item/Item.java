package com.abyeti.estore.item;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.abyeti.db.PGDBConn;

@Path("/item")
public class Item {

	@Context private HttpServletRequest request;
	
	@Path("/new")
	@POST
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public Response addItem(String incomingData) throws Exception {
		
		HttpSession session = request.getSession();
		if(session.getAttribute("estore_username")==null) {
			System.out.println("Not Logged In");
			return Response.status(500).entity("Login Required").build();
		}
		
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
			JSONObject itemData = new JSONObject(incomingData);
			System.out.println( "jsonData: " + itemData.toString() );
			
			conn = PGDBConn.dbConnection();
			ps = conn.prepareStatement("INSERT INTO item(itemname,itemdesc,itemprice,username) VALUES(?,?,?,?) ");
			ps.setString(1, itemData.optString("item_name"));
			ps.setString(2, itemData.optString("item_desc"));
			ps.setDouble(3, itemData.optDouble("item_price"));
			ps.setString(4, (String) session.getAttribute("estore_username"));
			
			int http_code = ps.executeUpdate();
						
			if( http_code != 0 ) {
				/*
				 * The put method allows you to add data to a JSONObject.
				 * The first parameter is the KEY (no spaces)
				 * The second parameter is the Value
				 */
				jsonObject.put("HTTP_CODE", "200");
				jsonObject.put("MSG", "Item has been entered successfully");
				/*
				 * When you are dealing with JSONArrays, the put method is used to add
				 * JSONObjects into JSONArray.
				 */
				returnString = jsonArray.put(jsonObject).toString();
			} else {
				System.out.println("Invalid");
				return Response.status(500).entity("Unable to enter Item").build();
			}
			
			System.out.println( "returnString: " + returnString );
			
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Server was not able to process your request").build();
		}
		
		return Response.ok(returnString).build();
	}
	
}
