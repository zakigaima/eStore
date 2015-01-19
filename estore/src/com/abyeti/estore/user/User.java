package com.abyeti.estore.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.map.ObjectMapper;

import com.abyeti.db.*;
import com.abyeti.functions.Functions;

@Path("/user")
public class User {


	@Context private HttpServletRequest request;
	
	@Path("/confirmLogin")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response isLoggedIn() throws Exception {
		String returnString;
		String MSG;
		int CODE;
		if(!Functions.isLoggedIn(request)) {
			MSG =  "You are not logged in, <a href='login.html'>Login</a>";
			CODE = 500;
		}
		else {
			MSG =  "Welcome,  "+Functions.getLoggedInUsername(request);
			CODE = 200;
		}
		returnString = Functions.createJSONMessage(CODE, MSG);
		return Response.ok(returnString).build();
	}
	
	@Path("/logout")
	@GET
	public void LogOut() throws Exception {
		HttpSession session = request.getSession();
		session.invalidate();
	}
	
	@Path("/login")
	@POST
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public Response LoginUser(String incomingData) throws Exception {
		
		String returnString = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		HttpSession session = request.getSession();

		try {
			
			ObjectMapper mapper = new ObjectMapper();
			UserEntry entry = mapper.readValue(incomingData, UserEntry.class);
			
			conn = PGDBConn.dbConnection();
			ps = conn.prepareStatement("SELECT username,password FROM users WHERE username=? AND PASSWORD=? ");
			ps.setString(1, entry.username);
			ps.setString(2, entry.password);
			rs = ps.executeQuery();

			int CODE;
			String MSG;
			if(rs.next()) {
				CODE = 200;
				MSG = "Login Successful";
				session.setAttribute("estore_username",entry.username);
			} else {
				CODE = 500;
				MSG = "<p class='text-danger'>Login Unsuccessful</p>";
			}
			returnString = Functions.createJSONMessage(CODE, MSG);
			
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
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			
			ObjectMapper mapper = new ObjectMapper();
			UserEntry entry = mapper.readValue(incomingData, UserEntry.class);
			
			conn = PGDBConn.dbConnection();
			ps = conn.prepareStatement("INSERT INTO users(username,password,email) VALUES(?,?,?) ");
			ps.setString(1, entry.username);
			ps.setString(2, entry.password);
			ps.setString(3, entry.email);
			
			
			int code = ps.executeUpdate();
			
			int CODE;
			String MSG;
			if( code != 0 ) {
				CODE = 200;
				MSG = "Registered Successfully";
			} else {
				CODE = 500;
				MSG = "Registration Error!!";
			}
			
			returnString = Functions.createJSONMessage(CODE, MSG);
						
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Server was not able to process your request").build();
		}
		
		return Response.ok(returnString).build();
	}
}

class UserEntry {
	public String username;
	public String password;
	public String email;
}