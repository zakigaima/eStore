package com.abyeti.functions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class Functions {
	
	public static boolean isLoggedIn(HttpServletRequest req) {
		HttpSession session = req.getSession();
		if(session.getAttribute("estore_username")==null) 
			return false;
		return true;
	}
	
	public static String getLoggedInUsername(HttpServletRequest req) {
		HttpSession session = req.getSession();
		return session.getAttribute("estore_username").toString();
	}

	public static String createJSONMessage(int CODE, String MSG) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		jsonObject.put("CODE", CODE);
		jsonObject.put("MSG", MSG);
		return jsonArray.put(jsonObject).toString();
	}
	
}
