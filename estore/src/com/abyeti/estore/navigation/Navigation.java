package com.abyeti.estore.navigation;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.abyeti.functions.Functions;

@Path("/nav")
public class Navigation {

	@Context private HttpServletRequest request;
	
	
	public JSONObject createJSONObject(String link, String classname, String value) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("link", link);
		jsonObject.put("class", classname);
		jsonObject.put("value", value);
		return jsonObject;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response returnNavigation() throws Exception {
		JSONArray jsonArray = new JSONArray();
		String returnString = "";
		if(Functions.isLoggedIn(request)) {
			jsonArray.put(createJSONObject("items.html", "", "My Items"));
			jsonArray.put(createJSONObject("newitem.html", "", "Sell an Item"));
			jsonArray.put(createJSONObject("sales.html", "", "My Sales"));
			jsonArray.put(createJSONObject("purchases.html", "", "My Purchases"));
			jsonArray.put(createJSONObject("", "logout", "Logout"));
		} else {
			jsonArray.put(createJSONObject("login.html", "", "Login"));
			jsonArray.put(createJSONObject("newuser.html", "", "Register"));
		}
		returnString = jsonArray.toString();
		return Response.ok(returnString).build();
	}
}
