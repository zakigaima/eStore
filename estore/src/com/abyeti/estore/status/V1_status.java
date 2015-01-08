package com.abyeti.estore.status;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.abyeti.db.PGDBConn;

import java.sql.*;
@Path("/v1/status")
public class V1_status {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String returnTitle() throws Exception {
		return "<h2>Hello World</h2>";
	}
	
	@Path("/database")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String returnDatabaseStatus() throws Exception {
		
		PreparedStatement query = null;
		String myString = "test ";
		String returnString = null;
		Connection conn = null;
		
		try {
			conn = PGDBConn.dbConnection();
			query = conn.prepareStatement("select extract(epoch from now());");
			ResultSet rs = query.executeQuery();
			
			
			while(rs.next()) 
				myString += rs.getString(1);
			
			query.close();
			
			returnString = "<p>Database Status</p> " +
				"<p>Database Date/Time return: " + myString + "</p>";
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if(conn!=null) conn.close();
		}
		
		return returnString; 
	}
}