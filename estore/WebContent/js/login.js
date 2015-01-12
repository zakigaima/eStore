$(document).ready(function() {
	//console.log("ready");
	
	var $post_example = $('#login');
		
	/**
	 * This is for the 2nd Submit button "Submit v2"
	 * It will do the same thing as Submit above but the api
	 * will process it in a different way.
	 */
	$('#login_submit_it').click(function(e) {
		console.log("submit button has been clicked");
		e.preventDefault(); //cancel form submit
		
		var jsObj = $post_example.serializeObject()
			, ajaxObj = {};
		
		//console.log(jsObj);
		
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
				console.log(data);
				if(data[0].HTTP_CODE == 200) {
					$('#login_div_ajaxResponse').text( data[0].MSG );
				}
			},
			complete: function(XMLHttpRequest) {
				//console.log( XMLHttpRequest.getAllResponseHeaders() );
			}, 
			dataType: "json" //request JSON
		};
		
		$.ajax(ajaxObj);
	});
});