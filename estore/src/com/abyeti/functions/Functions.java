package com.abyeti.functions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
	
	

}
