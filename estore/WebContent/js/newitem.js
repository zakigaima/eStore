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
				if(data[0].CODE == 500) {
					$('#newitem_loggedInMsg').html("You are not logged in, Please login");
					$('#newitem_add_item').hide();
				}
			},
			//complete: function(XMLHttpRequest) {
				//console.log( XMLHttpRequest.getAllResponseHeaders() );
			//}, 
			dataType: "json" //request JSON
	},
	$.ajax(ajaxObj);		
	
	
	var $add_item = $('#newitem_add_item');
		
	$('#newitem_submit_it').click(function(e) {
		//console.log("submit button has been clicked");
		e.preventDefault(); //cancel form submit
		
		var jsObj = $add_item.serializeObject()
			, ajaxObj = {};
		
		//console.log(jsObj);
		
		ajaxObj = {  
			type: "POST",
			url: "http://localhost:8080/estore/api/item/new", 
			data: JSON.stringify(jsObj), 
			contentType:"application/json",
			error: function(jqXHR, textStatus, errorThrown) {
				console.log("Error " + jqXHR.getAllResponseHeaders() + " " + errorThrown);
				$('#newitem_div_ajaxResponse').text( "Error" );
			},
			success: function(data) { 
				console.log(data);
				
				if(data[0].CODE == 200) {
					$('#newitem_div_ajaxResponse').text( data[0].MSG );
					setTimeout( function() { window.location.href="http://localhost:8080/estore/"; }, 200);		
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