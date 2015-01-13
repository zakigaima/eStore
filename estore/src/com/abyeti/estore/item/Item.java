package com.abyeti.estore.item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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

@Path("/item")
public class Item {

	@Context private HttpServletRequest request;
	
	@Path("/new")
	@POST
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public Response addItem(String incomingData) throws Exception {
		
		HttpSession session = request.getSession();
		if(Functions.isLoggedIn(request)) {
			System.out.println("Not Logged In");
			return Response.status(500).entity("Login Required").build();
		}
		
		String returnString = null;
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			JSONObject itemData = new JSONObject(incomingData);
			
			conn = PGDBConn.dbConnection();
			ps = conn.prepareStatement("INSERT INTO item(itemname,itemdesc,itemprice,username) VALUES(?,?,?,?) ");
			ps.setString(1, itemData.optString("item_name"));
			ps.setString(2, itemData.optString("item_desc"));
			ps.setDouble(3, itemData.optDouble("item_price"));
			ps.setString(4, (String) session.getAttribute("estore_username"));
			
			int http_code = ps.executeUpdate();
						
			if( http_code != 0 ) {
				jsonObject.put("HTTP_CODE", "200");
				jsonObject.put("MSG", "Item has been entered successfully");
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
	
	@Path("/all")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response returnItemsOfSeller() throws Exception {
		
		PreparedStatement query = null;
		Connection conn = null;
		String returnString = null;
		Response rb = null;
		HttpSession session = request.getSession();
		String seller = session.getAttribute("estore_username").toString();
		System.out.println("Session: "+ seller);
		
		try {
			conn = PGDBConn.dbConnection();
			query = conn.prepareStatement("select i.itemid,itemname,itemdesc,itemprice, COUNT(t.itemid)  from item i,transaction t WHERE i.itemid=t.itemid AND i.username=? GROUP BY i.itemid");
			query.setString(1, seller);
			
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
	
	@Path("/{itemid}/{itemdesc}/{itemprice}")
	@PUT
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public Response UpdateItems(@PathParam("itemid") int itemid, 
								@PathParam("itemdesc") String itemdesc,
								@PathParam("itemprice") double itemprice, 
								String incomingdata) throws Exception {
		
		PreparedStatement query = null;
		Connection conn = null;
		String returnString = null;
		Response rb = null;
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();		
		try {
			conn = PGDBConn.dbConnection();
			query = conn.prepareStatement("UPDATE item SET itemdesc=?, itemprice=? WHERE itemid=?");
			query.setString(1, itemdesc);
			query.setDouble(2, itemprice);
			query.setInt(3, itemid);
			
			System.out.println(incomingdata+"\n"+query.toString());
			query.executeUpdate();
			
			jsonObject.put("CODE", "200");
			jsonObject.put("MSG", "Item has been updated successfully");
			query.close(); //close connection
			returnString = jsonArray.put(jsonObject).toString();
			
			rb = Response.ok(returnString).build();
			
		}
		catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Server was not able to process your request").build();
		}
		finally {
			 if (conn != null) conn.close();
		}
		
		return rb;
	}

	@Path("/{itemid}")
	@DELETE
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public Response DeleteItems(@PathParam("itemid") int itemid, 
								String incomingdata) throws Exception {
		
		PreparedStatement query = null;
		Connection conn = null;
		String returnString = null;
		Response rb = null;
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();		
		try {
			conn = PGDBConn.dbConnection();
			query = conn.prepareStatement("DELETE FROM item WHERE itemid=?");
			query.setInt(1, itemid);
			
			query.executeUpdate();
			
			jsonObject.put("CODE", "200");
			jsonObject.put("MSG", "Item has been deleted successfully");
			query.close(); //close connection
			returnString = jsonArray.put(jsonObject).toString();
			
			rb = Response.ok(returnString).build();
			
		}
		catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Server was not able to process your request").build();
		}
		finally {
			 if (conn != null) conn.close();
		}
		
		return rb;
	}	

	@Path("/inventory")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInventory() throws Exception {
		
		PreparedStatement query = null;
		Connection conn = null;
		String returnString = null;
		Response rb = null;
		HttpSession session = request.getSession();
		
		System.out.println("Session: "+ session.getAttribute("estore_username"));
		
		try {
			conn = PGDBConn.dbConnection();
			query = conn.prepareStatement("select itemid,itemname,itemdesc,itemprice from item");
			
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
