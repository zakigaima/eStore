$(document).ready(function() {
	ajaxObj = {  
		type: "GET",
		url: "http://localhost:8080/estore/api/install", 
		contentType:"application/json",
		error: function(jqXHR, textStatus, errorThrown) {
			console.log("Error " + jqXHR.getAllResponseHeaders() + " " + errorThrown);
			$('#div_ajaxResponse').text( "Error" );
		},
		success: function(data) { 
			//console.log(data);
			$('#installMsg').text(data[0].MSG);
			if(data[0].CODE == 3)
				setTimeout( function() { window.location.href="http://localhost:8080/estore/newuser.html"; }, 500);		
		},
		complete: function(XMLHttpRequest) {
			//console.log( XMLHttpRequest.getAllResponseHeaders() );
		}, 
		dataType: "json" //request JSON
	};
	
	$.ajax(ajaxObj);
});