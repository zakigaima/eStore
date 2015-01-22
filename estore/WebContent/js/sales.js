estoreApp.controller('SalesController',function($scope,$http) {
	$http.get("http://localhost:8080/estore/api/transaction/sales").success( function(response) {
		if(response[0].CODE==500)
			$('#sales_status').html(response[0].MSG);
		else 
			$scope.sales = response;
	 });
});