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

import com.abyeti.db.PGDBConn;
import com.abyeti.functions.Functions;
import com.abyeti.util.ToJSON;

/**
 * 
 * This class has the methods that are related to transactions in eStore Application
 * 
 * @author Abyeti-1
 *
 */
@Path("/transaction")
public class Transaction {

	@Context private HttpServletRequest request;
	
	/**
	 * It creates the new transaction that has buyer name coming from the session variable.
	 * it takes the itemid and creates the new entry in the transaction table
	 * 
	 * @param itemid
	 * @return
	 * @throws Exception
	 */
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
		int CODE;
		String MSG;
		if(Functions.getItemQuantity(itemid)==0) {
			CODE = 500;
			MSG = "<i class='text-danger'>Sorry, Item out of Stock</i>";
		}
		else {
			Connection conn = null;
			PreparedStatement ps = null;
			
			try {
				conn = PGDBConn.dbConnection();
				ps = conn.prepareStatement("INSERT INTO transaction(itemid,buyername) VALUES(?,?) ");
				ps.setInt(1, itemid);
				ps.setString(2, Functions.getLoggedInUsername(request));
				
				int http_code = ps.executeUpdate();
				
				if( http_code != 0 ) {
					Functions.updateQuantity(itemid);
					CODE = 200;
					MSG = "Thanks for Buying";
				} else {
					CODE = 500;
					MSG = "Error in the Transaction";
				}
				
			} catch(Exception e) {
				e.printStackTrace();
				return Response.status(500).entity("Server was not able to process your request").build();
			}
		}
		returnString = Functions.createJSONMessage(CODE, MSG);
		return Response.ok(returnString).build();
	}
	
	/**
	 * This method returns all the sales of the logged in user
	 * @return
	 * @throws Exception
	 */
	@Path("/sales")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response returnAllSales() throws Exception {
		
		PreparedStatement query = null;
		Connection conn = null;
		String returnString = null;
		Response rb = null;
		String seller = Functions.getLoggedInUsername(request);
		if(!Functions.isLoggedIn(request)) {
			returnString = "User Should be logged in for this request";
			return Response.ok(returnString).build();
		}
		
		try {
			conn = PGDBConn.dbConnection();
			query = conn.prepareStatement("select i.itemid,itemname,itemdesc,itemprice,t.buyername from item i,transaction t WHERE i.itemid=t.itemid AND i.username=?");
			query.setString(1, seller);
			
			ResultSet rs = query.executeQuery();
			
			ToJSON converter = new ToJSON();
			JSONArray json = new JSONArray();

			json = converter.toJSONArray(rs);

			if(json.length()==0) 
				returnString = Functions.createJSONMessage(500, "<i>No items exist</i>");
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
	
	/**
	 * This method returns all the purchases of the logged in user
	 * @return
	 * @throws Exception
	 */
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
		String buyer = Functions.getLoggedInUsername(request);
		
		try {
			conn = PGDBConn.dbConnection();
			query = conn.prepareStatement("select i.itemid,itemname,itemdesc,itemprice,username from item i,transaction t WHERE i.itemid=t.itemid AND t.buyername=?");
			query.setString(1, buyer);
			
			ResultSet rs = query.executeQuery();
			
			ToJSON converter = new ToJSON();
			JSONArray json = new JSONArray();
			
			json = converter.toJSONArray(rs); 

			if(json.length()==0) 
				returnString = Functions.createJSONMessage(500, "<i>No items exist</i>");
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
