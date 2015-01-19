package com.abyeti.functions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

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
	
}
