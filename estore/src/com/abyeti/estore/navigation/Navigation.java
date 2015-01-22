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

/**
 * This class is used to create the dynamic navigation bar.
 * If the user is logged in, it should not show the login / register links
 * and if the user in not logged in, it should only show login / register links
 * 
 * @author Abyeti-1
 *
 */
@Path("/nav")
public class Navigation {

	@Context private HttpServletRequest request;
	/**
	 * 
	 * It creates the JSON object of three variables - link, class, value
	 * 
	 * @param link
	 * @param classname
	 * @param value
	 * @return
	 * @throws JSONException
	 */
	
	public JSONObject createJSONObject(String link, String classname, String value) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("link", link);
		jsonObject.put("class", classname);
		jsonObject.put("value", value);
		return jsonObject;
	}
	
	/**
	 * It produces the list of navigation dynamically
	 * 
	 * @return
	 *  Response Object in JSON format so that AngularJS catches it.
	 * 
	 * @throws Exception
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response returnNavigation() throws Exception {
		JSONArray jsonArray = new JSONArray();
		String returnString = "";
		if(Functions.isLoggedIn(request)) { //if user is logged in 
			jsonArray.put(createJSONObject("#/items", "", "My Items"));
			jsonArray.put(createJSONObject("#/newitem", "", "Sell an Item"));
			jsonArray.put(createJSONObject("#/sales", "", "My Sales"));
			jsonArray.put(createJSONObject("#/purchases", "", "My Purchases"));
			jsonArray.put(createJSONObject("", "logout", "Logout"));
		} else {
			jsonArray.put(createJSONObject("#/login", "", "Login"));
			jsonArray.put(createJSONObject("#/newuser", "", "Register"));
		}
		returnString = jsonArray.toString();
		return Response.ok(returnString).build();
	}
}
