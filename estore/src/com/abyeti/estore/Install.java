package com.abyeti.estore;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.abyeti.db.PGDBConn;

@Path("/install")
public class Install {
	
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
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response installEStore() throws Exception {
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
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();		
		
		int t1 = createTable(tbl_users);
		int t2 = createTable(tbl_item);
		int t3 = createTable(tbl_transaction);
		if((t1+t2+t3)!=3) {
			jsonObject.put("CODE", t1+t2+t3);
			jsonObject.put("MSG", "eStore Already Installed or the database is not empty");
			returnString = jsonArray.put(jsonObject).toString();
		} else {
			jsonObject.put("CODE", t1+t2+t3);
			jsonObject.put("MSG", "eStore Application Installed");
			returnString = jsonArray.put(jsonObject).toString();
		}
		return Response.ok(returnString).build();
	}
}
