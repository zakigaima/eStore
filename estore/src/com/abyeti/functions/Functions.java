package com.abyeti.functions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.abyeti.db.PGDBConn;

/**
 * 
 * This class helps in reducing the code redundancy
 * It has different methods performing different functions
 * @author Abyeti-1
 *
 */
public class Functions {
	
	/**
	 * to check the session if the user is logged in
	 * @param req
	 * @return
	 */
	public static boolean isLoggedIn(HttpServletRequest req) {
		HttpSession session = req.getSession();
		if(session.getAttribute("estore_username")==null) 
			return false;
		return true;
	}
	
	/**
	 * to get the username who has logged in
	 * 
	 * @param req
	 * @return
	 */
	public static String getLoggedInUsername(HttpServletRequest req) {
		HttpSession session = req.getSession();
		return session.getAttribute("estore_username").toString();
	}

	/**
	 * 
	 * to create the JSON array for giving messages with code.
	 * 
	 * @param CODE
	 * @param MSG
	 * @return 
	 * @throws JSONException
	 */
	public static String createJSONMessage(int CODE, String MSG) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		jsonObject.put("CODE", CODE);
		jsonObject.put("MSG", MSG);
		return jsonArray.put(jsonObject).toString();
	}
	
	public static int getItemQuantity(int itemid) throws Exception {
		PreparedStatement ps = null;
		Connection conn = null;
		int qty = 0;
		try {
			conn = PGDBConn.dbConnection();
			ps = conn.prepareStatement("SELECT quantity FROM item WHERE itemid = ?");
			ps.setInt(1, itemid);
			ResultSet rs = ps.executeQuery();
			rs.next();
			qty = rs.getInt("quantity");
			rs.close();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(conn!=null) conn.close();
		}
		return qty;
	}
	
	public static boolean updateQuantity(int itemid) throws Exception {
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = PGDBConn.dbConnection();
			int quantity = getItemQuantity(itemid);
			if(quantity == 0) {
				return false;
			}
			ps = conn.prepareStatement("UPDATE item SET quantity = quantity - 1 WHERE itemid = ?");
			ps.setInt(1, itemid);
			ps.executeUpdate();
			
		} catch(Exception e) {
			
		} finally {
			if(conn!=null) conn.close();
		}
		return true;
	}
	
}
