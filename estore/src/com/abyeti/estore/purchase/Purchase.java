package com.abyeti.estore.purchase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.abyeti.db.PGDBConn;

@Path("/purchase")
public class Purchase {

	@Context private HttpServletRequest request;
	@Path("/{itemid}")
	@POST
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public Response addItem(@PathParam("itemid") int itemid/*, String incomingData*/) throws Exception {
		
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
			//JSONObject itemData = new JSONObject(incomingData);
			//System.out.println( "jsonData: " + itemData.toString() );
			
			conn = PGDBConn.dbConnection();
			
			PreparedStatement query = conn.prepareStatement("SELECT username FROM item WHERE itemid=?");
			query.setInt(1, itemid);
			ResultSet rs = query.executeQuery();
			rs.next();
			String seller = rs.getString("username");
			
			ps = conn.prepareStatement("INSERT INTO transaction(itemid,buyername,sellername) VALUES(?,?,?) ");
			ps.setInt(1, itemid);
			ps.setString(2, (String) session.getAttribute("estore_username"));
			ps.setString(3, seller);
			
			int http_code = ps.executeUpdate();
						
			if( http_code != 0 ) {
				/*
				 * The put method allows you to add data to a JSONObject.
				 * The first parameter is the KEY (no spaces)
				 * The second parameter is the Value
				 */
				jsonObject.put("HTTP_CODE", "200");
				jsonObject.put("MSG", "Thanks for Buying");
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
