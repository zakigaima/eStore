package com.abyeti.estore.item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
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

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONArray;

import com.abyeti.db.PGDBConn;
import com.abyeti.functions.Functions;
import com.abyeti.util.ToJSON;

/**
 * This class is used from performing CRUD operations on items in eStore
 * 
 * @author Abyeti-1
 *
 */


@Path("/item")
public class Item {

	@Context private HttpServletRequest request; //request variable is mainly used to manage sessions
	
	/**
	 * This method creates a new item in the eStore.
	 * 
	 * It uses @POST annotation
	 * 
	 * @param incomingData (in JSON format)
	 * the incomingdata is then mapped with the ItemEntry class defined in the bottom of this file
	 * ObjectMapper class is used to map the incomingdata and ItemEntry class
	 * 
	 * @return Response object in JSON format.
	 * 
	 * @throws Exception
	 */
	
	@Path("/new")
	@POST
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public Response addItem(String incomingData) throws Exception {
		
		if(!Functions.isLoggedIn(request)) {
			System.out.println("Not Logged In");
			return Response.status(500).entity("Login Required").build();
		}
		
		String returnString = null;
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			ItemEntry entry = mapper.readValue(incomingData, ItemEntry.class);
			conn = PGDBConn.dbConnection();

			ps = conn.prepareStatement("INSERT INTO item(itemname,itemdesc,itemprice,username) VALUES(?,?,?,?) ");
			ps.setString(1, entry.item_name);
			ps.setString(2, entry.item_desc);
			ps.setDouble(3, entry.item_price);
			ps.setString(4, Functions.getLoggedInUsername(request)); //getting Logged in Username
			
			int http_code = ps.executeUpdate();
			
			
			if( http_code != 0 ) {
				String MSG = "Item has been entered successfully";
				returnString = Functions.createJSONMessage(200, MSG);
			} else {
				return Response.status(500).entity("Unable to enter Item").build();
			}
			
			System.out.println( "returnString: " + returnString );
			
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Server was not able to process your request").build();
		}
		
		return Response.ok(returnString).build();
	}

	/**
	 * This method returns all the items of the logged in seller.
	 * The input is taken from the session variable (Written in Functions class)
	 * 
	 * It uses @GET annotation
	 * 
	 * ***It is mandatory to be logged in for this method execution***
	 * 
	 * @return Response object of JSONArray
	 * 
	 * @throws Exception
	 */

	@Path("/all")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response returnItemsOfSeller() throws Exception {
		
		PreparedStatement query = null;
		Connection conn = null;
		String returnString = null;
		Response rb = null;
		
		if(!Functions.isLoggedIn(request)) {
			returnString = "User Should be logged in for this request";
			return Response.ok(returnString).build();
		}
		
		String seller = Functions.getLoggedInUsername(request);
		System.out.println("Session: "+ seller);
		
		try {
			conn = PGDBConn.dbConnection();
			query = conn.prepareStatement("select item.itemid,itemname,itemdesc,itemprice, "
					+ "COUNT(transaction.itemid) "
					+ "from item LEFT JOIN transaction "
					+ "ON item.itemid=transaction.itemid "
					+ "WHERE username=? GROUP BY item.itemid");
			query.setString(1, seller);
			
			ResultSet rs = query.executeQuery();
			ToJSON converter = new ToJSON();
			JSONArray json = new JSONArray();
			json = converter.toJSONArray(rs);
			if(json.length()==0) {
				String MSG = "<i>No items exist</i>";
				returnString = Functions.createJSONMessage(500, MSG);
			} else {
				returnString = json.toString();
			}
			query.close(); 
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
	
	
	/**
	 * Updates the item with description and price.
	 * 
	 * It uses @PUT annotation
	 * 
	 * @param itemid
	 * @param itemdesc
	 * @param itemprice
	 * @param incomingdata
	 * @return
	 * @throws Exception
	 */
	
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
		try {
			conn = PGDBConn.dbConnection();
			query = conn.prepareStatement("UPDATE item SET itemdesc=?, itemprice=? WHERE itemid=?");
			query.setString(1, itemdesc);
			query.setDouble(2, itemprice);
			query.setInt(3, itemid);
			
			System.out.println(incomingdata+"\n"+query.toString());
			query.executeUpdate();
			
			String MSG = "Item has been updated successfully";
			returnString = Functions.createJSONMessage(200, MSG);

			query.close(); //close connection
			
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
	
	/**
	 * It removes the item from the database using itemid
	 * It uses @DELETE annotation
	 * 
	 * @param itemid
	 * @param incomingdata
	 * @return
	 * @throws Exception
	 */

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
		try {
			conn = PGDBConn.dbConnection();
			query = conn.prepareStatement("DELETE FROM item WHERE itemid=?");
			query.setInt(1, itemid);
			
			query.executeUpdate();
			query.close(); 
			
			String MSG = "Item has been deleted successfully";
			returnString = Functions.createJSONMessage(200, MSG);
			
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

	
	/**
	 * This method gets all the items of all the sellers from the eStore.
	 * 
	 * 
	 * @return
	 * @throws Exception
	 */
	@Path("/inventory")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInventory() throws Exception {
		
		PreparedStatement query = null;
		Connection conn = null;
		String returnString = null;
		Response rb = null;
		
		try {
			conn = PGDBConn.dbConnection();
			query = conn.prepareStatement("select itemid,itemname,itemdesc,itemprice from item");
			
			ResultSet rs = query.executeQuery();
			ToJSON converter = new ToJSON();
			JSONArray json = new JSONArray();
			
			json = converter.toJSONArray(rs);
			if(json.length()==0) {
				String MSG = "<i>No items exist</i>";
				returnString = Functions.createJSONMessage(500, MSG);
			}
			else 
				returnString = json.toString();
			query.close(); 
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

/**
 * 
 * Simple class to creation of POJO
 * 
 * @author Abyeti-1
 *
 */
class ItemEntry {
	public String item_name;
	public String item_desc;
	public double item_price;
}