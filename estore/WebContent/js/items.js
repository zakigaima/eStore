$(document).ready(function() {
	
	var $update_form = $('#update_form');
	$update_form.hide();
	
	getItems();

	
	$(document.body).on('click', '.edit', function(e) {
		//console.log(this);
		$update_form.show();
		var $this = $(this)
			, $tr = $this.closest('tr')
			, itemid = $tr.find('.itemid').text()
			, itemname = $tr.find('.itemname').text()
			, itemdesc = $tr.find('.itemdesc').text()
			, itemprice = $tr.find('.itemprice').text();
		
		$('#form_itemid').val(itemid);
		$('#form_itemname').text(itemname);
		$('#form_itemdesc').text(itemdesc);
		$('#form_itemprice').val(itemprice);
		
		$('#update_response').text("");
	});
	
	$update_form.submit(function(e) {
		e.preventDefault(); //cancel form submit
		
		var obj = $update_form.serializeObject()
			, id = $('#form_itemid').val()
			, desc = $('#form_itemdesc').val()
			, price = $('#form_itemprice').val();
		
		updateItem(obj, id, desc, price);
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

function updateItem(obj, id, desc, price) {
	console.log(JSON.stringify(obj));
	ajaxObj = {  
			type: "PUT",
			url: "http://localhost:8080/estore/api/item/" + id + "/" + desc + "/" + price,
			data: JSON.stringify(obj), 
			contentType:"application/json",
			error: function(jqXHR, textStatus, errorThrown) {
				console.log(jqXHR.responseText);
			},
			success: function(data) {
				//console.log(data);
				$('#update_response').text( data[0].MSG );
			},
			complete: function(XMLHttpRequest) {
				//console.log( XMLHttpRequest.getAllResponseHeaders() );
				getItems();
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
				$('#delete_response').text( data[0].MSG );
			},
			complete: function(XMLHttpRequest) {
				//console.log( XMLHttpRequest.getAllResponseHeaders() );
				getItems();
			}, 
			dataType: "json" //request JSON
		};
		
	return $.ajax(ajaxObj);
}




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
					$('#status').html(data[0].MSG);
				}
				else {
					$.each(data, function(index1, val1) {
						//console.log(val1);
						html_string = html_string + templateGetInventory(val1);
					});
					
					$('#get_items').html("<table class='table table-hover'>" +
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

