/*var getItemsController = {};
getItemsController.MyItemsController = function ($scope,$http) {
	$http.get("http://localhost:8080/estore/api/item/all").success( function(response) {
		if(response[0].CODE==500) 
			$('#items_status').html(response[0].MSG);
		else
			$scope.myitems = response;
	 });
};
estoreApp.controller(getItemsController);
*/
function MyItemsController($scope,$http) {
	$http.get("http://localhost:8080/estore/api/item/all").success( function(response) {
		if(response[0].CODE==500) 
			$('#items_status').html(response[0].MSG);
		else
			$scope.myitems = response;
	 });
}
$(document).ready(function() {
	
//	MyItemsController($scope, $http);
	var $update_form = $('#items_update_form');
	$update_form.hide();
		
	$(document.body).on('click', '.edit', function(e) {
		//console.log(this);
		$update_form.show();
		var $this = $(this)
			, $tr = $this.closest('tr')
			, itemid = $tr.find('.itemid').text()
			, itemname = $tr.find('.itemname').text()
			, itemdesc = $tr.find('.itemdesc').text()
			, itemprice = $tr.find('.itemprice').text()
			, quantity = $tr.find('.quantity').text();
		
		$('#items_form_itemid').val(itemid);
		$('#items_form_itemname').text(itemname);
		$('#items_form_itemdesc').text(itemdesc);
		$('#items_form_itemprice').val(itemprice);
		$('#items_form_quantity').val(quantity);
		
		$('#items_update_response').text("");
	});
	
	$update_form.submit(function(e) {
		e.preventDefault(); //cancel form submit
		
		var obj = $update_form.serializeObject()
			, id = $('#items_form_itemid').val()
			, desc = $('#items_form_itemdesc').val()
			, price = $('#items_form_itemprice').val()
			, qty = $('#items_form_quantity').val();
		
		updateItem(obj, id, desc, price, qty);
	});
	
	$(document.body).on('click', '.remove', function(e) {
		var $this = $(this)
		, $tr = $this.closest('tr')
		, itemid = $tr.find('.itemid').text();
		if(confirm("Are you sure you want to delete ?")) {
			deleteItem(itemid);
		}
	});
});

estoreApp.controller('MyItemsController',MyItemsController);


function updateItem(obj, id, desc, price, qty) {
	console.log(JSON.stringify(obj));
	$('#items_btn_update').removeClass('btn-default');
	$('#items_btn_update').addClass('btn-warning');
	$('#items_btn_update_span').addClass('glyphicon glyphicon-refresh glyphicon-refresh-animate');
	ajaxObj = {  
			type: "PUT",
			url: "http://localhost:8080/estore/api/item/" + id + "/" + desc + "/" + price + "/" + qty,
			data: JSON.stringify(obj), 
			contentType:"application/json",
			error: function(data, jqXHR, textStatus, errorThrown) {
				console.log("Coming from Log: "+jqXHR.responseText);
				$('#items_update_response').text( data );
			},
			success: function(data) {
				$('#items_update_response').text( data[0].MSG );
				//location.reload();
			},
			complete: function(XMLHttpRequest) {
				//console.log( XMLHttpRequest.getAllResponseHeaders() );
				$('#items_btn_update').addClass('btn-default');
				$('#items_btn_update').removeClass('btn-warning');
				$('#items_btn_update_span').removeClass('glyphicon glyphicon-refresh glyphicon-refresh-animate');
			}, 
			dataType: "json" //request JSON
		};
		
	return $.ajax(ajaxObj);
}

function deleteItem(id) {
	ajaxObj = {  
			type: "DELETE",
			url: "http://localhost:8080/estore/api/item/" + id,
			contentType:"application/json",
			error: function(jqXHR, textStatus, errorThrown) {
				console.log(jqXHR.responseText);
			},
			success: function(data) {
				//console.log(data);
				$('#items_delete_response').text( data[0].MSG );
				location.reload();
			},
			complete: function(XMLHttpRequest) {
				//console.log( XMLHttpRequest.getAllResponseHeaders() );
				//getItems();
			}, 
			dataType: "json" //request JSON
		};
		
	$.ajax(ajaxObj);

}



/*
function getItems() {
	
	var d = new Date()
		, n = d.getTime();
	
	ajaxObj = {  
			type: "GET",
			url: "http://localhost:8080/estore/api/item/all", 
			data: "ts="+n, 
			contentType:"application/json",
			error: function(jqXHR, textStatus, errorThrown) {
				console.log(jqXHR.responseText);
			},
			success: function(data) { 
				//console.log(data);
				var html_string = "";
				if(data[0].CODE==500) {
					$('#items_status').html(data[0].MSG);
				}
				else {
					$.each(data, function(index1, val1) {
						//console.log(val1);
						html_string = html_string + templateGetInventory(val1);
					});
					
					$('#items_get_items').html("<table class='table table-hover'>" +
											"<tbody><tr><th>Item</th><th>Price</th><th>Sold</th><th></th><th></th></tr>"+ html_string + "</tbody></table>");
				}
			},
			complete: function(XMLHttpRequest) {
				//console.log( XMLHttpRequest.getAllResponseHeaders() );
			}, 
			dataType: "json" //request JSON
		};
		
	return $.ajax(ajaxObj);
}

function templateGetInventory(param) {
	return '<tr>' +
				'<td class="itemid hidden">' + param.itemid + '</td>' +
				'<td class="itemname">' + param.itemname + '</td>' +
				'<td class="itemdesc hidden">' + param.itemdesc + '</td>' +
				'<td class="itemprice">' + param.itemprice + '</td>' +
				'<td class="itemcount">' + param.count + ' times</td>' +
				'<td><a href="#" class="edit"><span class="glyphicon glyphicon-pencil"></span></a></td>' +
				'<td><a href="#" class="remove"><span class="glyphicon glyphicon-remove"></span></a></td>' +
			'</tr>';
}

*/