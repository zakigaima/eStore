var estoreApp = angular.module('estoreApp',['ngRoute']);

estoreApp.config(['$routeProvider', function ($routeProvider) {
	$routeProvider
	.when('/',
			{
				templateUrl: 'inventory.html',
				controller: 'AppCtrl'
			})
	.when('/login',
			{
				templateUrl: 'login.html',
				controller: 'AppCtrl'
			})
	.when('/items',
			{
				templateUrl: 'items.html',
				controller: 'AppCtrl'
			})
	.when('/sales',
			{
				templateUrl: 'sales.html',
				controller: 'AppCtrl'
			})
	.when('/purchases',
			{
				templateUrl: 'purchases.html',
				controller: 'AppCtrl'
			})
	.when('/newuser',
			{
				templateUrl: 'newuser.html',
				controller: 'AppCtrl'
			})
	.when('/newitem',
			{
				templateUrl: 'newitem.html',
				controller: 'AppCtrl'
			})
	.otherwise({redirectTo: 'inventory.html'});
}]);

estoreApp.controller('AppCtrl', function() {
	console.log("Checking in console");
});

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
