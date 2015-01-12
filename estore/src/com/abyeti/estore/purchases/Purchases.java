package com.abyeti.estore.purchases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;

import com.abyeti.db.*;
import com.abyeti.util.ToJSON;

@Path("/purchases")
public class Purchases {

	@Context private HttpServletRequest request;
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response returnAllPcParts() throws Exception {
		
		PreparedStatement query = null;
		Connection conn = null;
		String returnString = "sss";
		Response rb = null;
		HttpSession session = request.getSession();
		String seller = session.getAttribute("estore_username").toString();
		System.out.println("Session: "+ seller);
		
		try {
			conn = PGDBConn.dbConnection();
			query = conn.prepareStatement("select i.itemid,itemname,itemdesc,itemprice,username from item i,transaction t WHERE i.itemid=t.itemid AND t.buyername=?");
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
	
}