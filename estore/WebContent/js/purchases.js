$(document).ready(function() {
	
	getPurchases();
	
});
function getPurchases() {
	
	var d = new Date()
		, n = d.getTime();
	
	ajaxObj = {  
			type: "GET",
			url: "http://localhost:8080/estore/api/transaction/purchases", 
			data: "ts="+n, 
			contentType:"application/json",
			error: function(jqXHR, textStatus, errorThrown) {
				console.log(jqXHR.responseText);
			},
			success: function(data) { 
				//console.log(data);
				var html_string = "";
				
				$.each(data, function(index1, val1) {
					//console.log(val1);
					html_string = html_string + templateGetInventory(val1);
				});
				
				$('#get_purchases').html("<table class='table table-striped table-bordered'>" +
										"<tbody><tr><th>Item</th><th>Desc</th><th>Price</th><th>Bought from</th></tr>"+ html_string + "</tbody></table>");
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
				'<td class="itemdesc">' + param.itemdesc + '</td>' +
				'<td class="itemprice">&#8377;' + param.itemprice + '</td>' +
				'<td class="buyername">' + param.username + '</td>' +
			'</tr>';
}

