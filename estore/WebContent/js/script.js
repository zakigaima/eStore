var estoreApp = angular.module('estoreApp',[]);

estoreApp.controller('NavController',function($scope,$http) {
	$http.get("http://localhost:8080/estore/api/nav").success( function(response) {
	    $scope.navtabs = response;
	 });
});

$(document).ready(function() {
	ajaxObj = {  
		type: "GET",
		url: "http://localhost:8080/estore/api/user/confirmLogin", 
		contentType:"application/json",
		error: function(jqXHR, textStatus, errorThrown) {
			console.log("Error " + jqXHR.getAllResponseHeaders() + " " + errorThrown);
		},
		success: function(data) { 
				$('#loginstatus').html(data[0].MSG);
		},
		dataType: "json" //request JSON
	},
	$.ajax(ajaxObj);	
	
	$(document.body).on('click', '#logout', function(e) {
		window.location.href="http://localhost:8080/estore/api/user/logout";
		setTimeout( function() { window.location.href="http://localhost:8080/estore/"; }, 200);		

	});
});
