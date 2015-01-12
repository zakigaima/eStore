package com.abyeti.estore.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.abyeti.db.*;

@Path("/user")
public class User {

	@Context private HttpServletRequest request;
	@Path("/confirmLogin")
	@POST
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public Response isLoggenIn() throws Exception {
		HttpSession session = request.getSession();
		if(session.getAttribute("estore_username")==null) {
			System.out.println("Not Logged In");
			//return Response.status(500).entity("Login Required").build();
			return Response.ok("You are not logged in").build();
		}
		return Response.ok("You are logged in").build();
	}
	
	
	
	@Path("/login")
	@POST
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public Response LoginUser(String incomingData) throws Exception {
		
		String returnString = null;
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		HttpSession session = request.getSession();

		try {
			
			/*
			 * We can create a new instance and it will accept a JSON string
			 * By doing this, we can now access the data.
			 */
			JSONObject userData = new JSONObject(incomingData);
			System.out.println( "jsonData: " + userData.toString() );
			
			conn = PGDBConn.dbConnection();
			ps = conn.prepareStatement("SELECT username,password FROM users WHERE username=? AND PASSWORD=? ");
			ps.setString(1, userData.optString("username"));
			ps.setString(2, userData.optString("password"));
			rs = ps.executeQuery();
			if(rs.next()) {
				/*
				 * The put method allows you to add data to a JSONObject.
				 * The first parameter is the KEY (no spaces)
				 * The second parameter is the Value
				 */
				jsonObject.put("HTTP_CODE", "200");
				jsonObject.put("MSG", "Login Successful");
				/*
				 * When you are dealing with JSONArrays, the put method is used to add
				 * JSONObjects into JSONArray.
				 */
				session.setAttribute("estore_username",userData.optString("username"));
				returnString = jsonArray.put(jsonObject).toString();
				System.out.println("Session variable : "+session.getAttribute("estore_username"));
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
	
	
	
	@Path("/new")
	@POST
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public Response addUser(String incomingData) throws Exception {
		
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
			JSONObject userData = new JSONObject(incomingData);
			System.out.println( "jsonData: " + userData.toString() );
			
			conn = PGDBConn.dbConnection();
			ps = conn.prepareStatement("INSERT INTO users(username,password,email) VALUES(?,?,?) ");
			ps.setString(1, userData.optString("username"));
			ps.setString(2, userData.optString("password"));
			ps.setString(3, userData.optString("email"));
			
			
			int http_code = ps.executeUpdate();
						
			if( http_code != 0 ) {
				/*
				 * The put method allows you to add data to a JSONObject.
				 * The first parameter is the KEY (no spaces)
				 * The second parameter is the Value
				 */
				jsonObject.put("HTTP_CODE", "200");
				jsonObject.put("MSG", "New User Added");
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