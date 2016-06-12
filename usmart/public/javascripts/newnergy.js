function deleteMeter(delUrl) {
	$.ajax({
		url: delUrl,
		type: 'DELETE',
		success: function(results){
			location.reload();
		},
		error: function() {
			alert("Error deleting")
		}
	});
}

/**
 * Ajax call that handles the deletion of the project
 * @param delUrl
 */
function deleteProject(delUrl){
	$.ajax({
		url: delUrl,
		type: 'DELETE',
		success: function(results){
			location.reload();
		},
		error: function() {
			alert("Error deleting")
		}
	});
}


function activateDayType(dayType, checkboxId){
	var meterId = document.getElementById("meterId").innerHTML
	alert(meterId);
	/* look for all checkboes that have a parent id called 'checkboxlist' attached to it and check if it was checked */
	$("#daytypecheckboxlist input:checked").each(function() {
		
		var dayTypeUrl = jsTimeSeriesRoutes.controllers.TimeSeriesController.activateDayType(dayType,meterId);
		
		$.ajax({
			url: dayTypeUrl.url,
			type: 'POST',
			success: function(results){
				location.reload();
			},
			error: function() {
				alert("Error deleting")
			}
		});
	});
	
	/* we join the array separated by the comma */
	var selected;
	selected = chkArray.join(',') + ",";
	
	/* check if there is selected checkboxes, by default the length is 1 as it contains one single comma */
	if(selected.length > 1){
		alert("You have selected " + selected);	
	}else{
		alert("Please at least one of the checkbox");	
	}
	
}

function showDailyTimeSeries(meterId) {
	//alert(meterId);
	var r = jsTimeSeriesRoutes.controllers.TimeSeriesController.getTimeSeriesJson(meterId);
	$.ajax({
		url: r,
		dataType: 'json',
		success: function(data) {
			$("#csv").html(data);
			loadChart();
		}, 
		error: function(){
			alert("error in time series chart");
		}
	});
}