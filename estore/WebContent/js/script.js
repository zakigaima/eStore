$(document).ready(function() {
	ajaxObj = {  
		type: "GET",
		url: "http://localhost:8080/estore/api/user/confirmLogin", 
		//data: JSON.stringify(), 
		contentType:"application/json",
		error: function(jqXHR, textStatus, errorThrown) {
			console.log("Error " + jqXHR.getAllResponseHeaders() + " " + errorThrown);
		},
		success: function(data) { 
			//console.log(data);
			//if(data[0].HTTP_CODE == 200) {
				$('#loginstatus').html(data[0].MSG);
			//}
		},
		//complete: function(XMLHttpRequest) {
			//console.log( XMLHttpRequest.getAllResponseHeaders() );
		//}, 
		dataType: "json" //request JSON
	},
	$.ajax(ajaxObj);
	
	$('#logout').click(function() {
		window.location("http://localhost:8080/estore/api/user/logout"),
	    location.reload();
	});	
});