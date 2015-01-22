estoreApp.controller('PurchasesController',function($scope,$http) {
	$http.get("http://localhost:8080/estore/api/transaction/purchases").success( function(response) {
		if(response[0].CODE==500)
			$('#purchases_status').html(response[0].MSG);
		else 
			$scope.purchases = response;
	 });
});