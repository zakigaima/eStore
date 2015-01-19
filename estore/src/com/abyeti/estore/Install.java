package com.abyeti.estore;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.abyeti.db.PGDBConn;
import com.abyeti.functions.Functions;

/**
 * 
 * This class is used the install the eStore application in the server.
 * It requires the database to be created and then the database name should be reflected in com.abyeti.db.PGDBConn class
 * 
 * @author Abyeti-1
 *
 */
@Path("/install")
public class Install {
	
	@Context private HttpServletRequest request;
	
	/**
	 * This method creates table in the database 
	 * @param query
	 * @return 1, if the table is created, 0 if there was error in creation 
	 * @throws Exception
	 */
	
	public int createTable(String query) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = PGDBConn.dbConnection();
			ps = conn.prepareStatement(query);
			ps.executeUpdate();
		}catch(Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			ps.close();
			conn.close();
		}
		return 1;
	}
	
	/**
	 * 
	 * This method installs the eStore Application by creating the database tables.
	 * 
	 * @return
	 *  A message in JSON format that says whether the application is installed or not
	 * 
	 * @throws Exception
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response installEStore() throws Exception {
		HttpSession session = request.getSession();
		session.invalidate();
		String tbl_users = "CREATE TABLE users ( "
				+ "userid serial NOT NULL , "
				+ "username text, password text, email text, "
				+ "CONSTRAINT user_pkey PRIMARY KEY (userid), "
				+ "CONSTRAINT users_username_key UNIQUE (username))";
		
		String tbl_item = "CREATE TABLE item ("
				+ "itemid serial NOT NULL, itemname text, itemdesc text, "
				+ "itemprice double precision, username text, "
				+ "CONSTRAINT item_pkey PRIMARY KEY (itemid), "
				+ "CONSTRAINT item_username_fkey FOREIGN KEY (username) "
				+ "REFERENCES users (username) MATCH SIMPLE "
				+ "ON UPDATE NO ACTION ON DELETE NO ACTION)";
		String tbl_transaction ="CREATE TABLE transaction ( "
				+ "trans_id serial NOT NULL, itemid integer, buyername text, sellername text, "
				+ "CONSTRAINT transaction_pkey PRIMARY KEY (trans_id), "
				+ "CONSTRAINT transaction_buyername_fkey FOREIGN KEY (buyername) "
				+ "REFERENCES users (username) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION, "
				+ "CONSTRAINT transaction_sellername_fkey FOREIGN KEY (sellername) "
				+ "REFERENCES users (username) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION)";
		
		
		String returnString = null;
		int t1 = createTable(tbl_users);
		int t2 = createTable(tbl_item);
		int t3 = createTable(tbl_transaction);
		String MSG;
		
		if((t1+t2+t3)!=3) 
			MSG = "eStore Already Installed or the database is not empty";
		else 
			MSG = "eStore Application Installed";
		
		returnString = Functions.createJSONMessage(t1+t2+t3, MSG);
		
		return Response.ok(returnString).build();
	}
}
