$(document).ready(function() {
	//console.log("ready");
	
	ajaxObj = {  
			type: "GET",
			url: "http://localhost:8080/estore/api/user/confirmLogin", 
			contentType:"application/json",
			error: function(jqXHR, textStatus, errorThrown) {
				console.log("Error " + jqXHR.getAllResponseHeaders() + " " + errorThrown);
			},
			success: function(data) { 
				//console.log(data);
				if(data[0].CODE == 200) {
					$('#login_loggedInMsg').html("You've already logged In");
					$('#login_login').hide();
				}
			},
			//complete: function(XMLHttpRequest) {
				//console.log( XMLHttpRequest.getAllResponseHeaders() );
			//}, 
			dataType: "json" //request JSON
		},
		$.ajax(ajaxObj);		

	
	var $login_form = $('#login_login');
		
	/**
	 * This is for the 2nd Submit button "Submit v2"
	 * It will do the same thing as Submit above but the api
	 * will process it in a different way.
	 */
	$(document.body).on('click', '#login_submit_it', function(e) {
		console.log("submit button has been clicked");
		e.preventDefault(); //cancel form submit
		
		var jsObj = $login_form.serializeObject()
			, ajaxObj = {};
		
		console.log(jsObj);
		console.log($login_form);
		
		ajaxObj = {  
			type: "POST",
			url: "http://localhost:8080/estore/api/user/login", 
			data: JSON.stringify(jsObj), 
			contentType:"application/json",
			error: function(jqXHR, textStatus, errorThrown) {
				console.log("Error " + jqXHR.getAllResponseHeaders() + " " + errorThrown);
				$('#login_div_ajaxResponse').text( "Error" );
			},
			success: function(data) { 
				//console.log(data);
				$('#login_div_ajaxResponse').html( data[0].MSG );
				//if(data[0].CODE == 200) 
					//window.location.href = "index.html";
			},
			complete: function(XMLHttpRequest) {
				//console.log( XMLHttpRequest.getAllResponseHeaders() );
			}, 
			dataType: "json" //request JSON
		};
		
		$.ajax(ajaxObj);
	});
});