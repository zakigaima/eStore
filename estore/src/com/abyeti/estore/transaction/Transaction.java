package com.abyeti.estore.transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import com.abyeti.functions.Functions;
import com.abyeti.util.ToJSON;


@Path("/transaction")
public class Transaction {

	@Context private HttpServletRequest request;
	
	@Path("new/{itemid}")
	@POST
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public Response addItem(@PathParam("itemid") int itemid) throws Exception {
		
		String returnString = null;
		if(!Functions.isLoggedIn(request)) {
			returnString = "User Should be logged in for this request";
			return Response.ok(returnString).build();
		}
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
			ps.setString(2, Functions.getLoggedInUsername(request));
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
	
	@Path("/sales")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response returnAllSales() throws Exception {
		
		PreparedStatement query = null;
		Connection conn = null;
		String returnString = "sss";
		Response rb = null;
		String seller = Functions.getLoggedInUsername(request);
		if(!Functions.isLoggedIn(request)) {
			returnString = "User Should be logged in for this request";
			return Response.ok(returnString).build();
		}
		System.out.println("Session: "+ seller);
		
		try {
			conn = PGDBConn.dbConnection();
			query = conn.prepareStatement("select i.itemid,itemname,itemdesc,itemprice,t.buyername from item i,transaction t WHERE i.itemid=t.itemid AND i.username=?");
			query.setString(1, seller);
			
			ResultSet rs = query.executeQuery();
			
			ToJSON converter = new ToJSON();
			JSONArray json = new JSONArray();

			json = converter.toJSONArray(rs);

			if(json.length()==0) {
				JSONObject jsObj = new JSONObject();
				jsObj.put("CODE", "500");
				jsObj.put("MSG", "<i>No items exist</i>");
				json.put(jsObj);
			}

			
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
	
	@Path("/purchases")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response returnAllPurchases() throws Exception {
		
		PreparedStatement query = null;
		Connection conn = null;
		String returnString = null;
		Response rb = null;
		if(!Functions.isLoggedIn(request)) {
			returnString = "User Should be logged in for this request";
			return Response.ok(returnString).build();
		}
		String seller = Functions.getLoggedInUsername(request);
		
		try {
			conn = PGDBConn.dbConnection();
			query = conn.prepareStatement("select i.itemid,itemname,itemdesc,itemprice,username from item i,transaction t WHERE i.itemid=t.itemid AND t.buyername=?");
			query.setString(1, seller);
			
			ResultSet rs = query.executeQuery();
			
			ToJSON converter = new ToJSON();
			JSONArray json = new JSONArray();
			
			json = converter.toJSONArray(rs);

			if(json.length()==0) {
				JSONObject jsObj = new JSONObject();
				jsObj.put("CODE", "500");
				jsObj.put("MSG", "<i>No items exist</i>");
				json.put(jsObj);
			}
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
