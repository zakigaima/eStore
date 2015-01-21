estoreApp.controller('ItemsController',function($scope,$http) {
	$http.get("http://localhost:8080/estore/api/item/inventory").success( function(response) {
		if(response[0].CODE==500)
			$('#status').html(response[0].MSG);
		else 
			$scope.items = response;
	 });
});

$(document).ready(function() {
	
	var $inventory = $('#buy_item');
		
	$(document.body).on('click', ':button, .viewItem', function(e) {
		//console.log(this);
		var $this = $(this)
			, itemid = $this.val()
			, $tr = $this.closest('tr')
			, itemname = $tr.find('.itemname').text()
			, itemdesc = $tr.find('.itemdesc').text()
			, itemprice = $tr.find('.itemprice').text()
			, quantity = $tr.find('.quantity').text();
		
		$('#item_name').text(itemname);
		$('#pre_item_desc').text(itemdesc);
		$('#item_price').text(itemprice);
		
		$('#update_response').text("");
		btn_text = (quantity==0)?"Out of Stock":"Buy Now";
		btn_class = (quantity==0)?"btn-default":"btn-primary";
		$('#buy_item').html("<input type='hidden' id='item_id' value='"+ itemid +"' /><input type='submit' id='submit_it' value='"+btn_text+"' class='btn "+btn_class+"'/>");
	});
	
	$inventory.submit(function(e) {
		e.preventDefault(); //cancel form submit
		
		var obj = $inventory.serializeObject(),
			itemid = $('#item_id').val();
		
		buyItem(obj,itemid);
	});

});
function buyItem(obj,itemid) {
	
	ajaxObj = {  
			type: "POST",
			url: "http://localhost:8080/estore/api/transaction/new/" + itemid,
			data: JSON.stringify(obj), 
			contentType:"application/json",
			error: function(jqXHR, textStatus, errorThrown) {
				console.log(jqXHR.responseText);
				$('#update_response').html( "Error" );
			},
			success: function(data) {
				//console.log(data);
				$('#update_response').html( data[0].MSG );
			},
			dataType: "json" //request JSON
		};
		
	return $.ajax(ajaxObj);
}

/*function getInventory() {
	
	var d = new Date()
		, n = d.getTime();
	
	ajaxObj = {  
			type: "GET",
			url: "http://localhost:8080/estore/api/item/inventory", 
			data: "ts="+n, 
			contentType:"application/json",
			error: function(jqXHR, textStatus, errorThrown) {
				console.log(jqXHR.responseText);
			},
			success: function(data) { 
				//console.log(data);
				var html_string = "";
				if(data[0].CODE==500) {
					$('#status').html(data[0].MSG);
				}
				else {
					$.each(data, function(index1, val1) {
						//console.log(val1);
						html_string = html_string + templateGetInventory(val1);
					});
					
					$('#get_inventory').html("<table class='table table-hover'>" +
											"<tbody><tr><th>Item</th><th>Price</th><th></th></tr>"+ html_string + "</tbody></table>");
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
				'<td class="itemname">' + param.itemname + '</td>' +
				'<td class="itemdesc hidden">' + param.itemdesc + '</td>' +
				'<td class="itemprice">&#8377;' + param.itemprice + '</td>' +
				'<td><button class="viewItem btn btn-default" value="' + param.itemid + '" type="button">View</button></td>'+
			'</tr>';
}

*/
