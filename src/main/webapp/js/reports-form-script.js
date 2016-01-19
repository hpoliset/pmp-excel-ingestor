$(document).ready(function() {
	$("#fromDate").datepicker({
		dateFormat:'dd-mm-yy',
		changeMonth: true,
		changeYear:true,
		showOn:"both",
		buttonImage: "../images/calendar.png",
		buttonImageOnly: true,
		buttonText: "Event From Date"
	});
	$("#tillDate").datepicker({
		dateFormat:'dd-mm-yy',
		changeMonth: true,
		changeYear:true,
		showOn:"both",
		buttonImage: "../images/calendar.png",
		buttonImageOnly: true,
		buttonText: "Event Till Date"
	});
	$('#country').change(function(event){
		if($(this).val()!='ALL'){
			populateStatesForCountry($(this).val());
		}else{
			$('#state').empty();
			$('<option>').val("ALL").text("All States").appendTo('#state');
		}
	});
});

function populateStatesForCountry(countryName){
	$.ajax({
		url: 'getStates',
		type:'POST',
		data:{country:countryName},
		async: false,
		datatype:"json",
		statusCode: {
             407: function() {
                 $.ajaxSetup({ dataType: "jsonp" });
                 populateStatesForCountry(countryName);
             }
        },
		success: function(result) {
			$('#state').empty();
			$('<option>').val("ALL").text("All States").appendTo('#state');
			$.each(result, function(i, item) {
				$('<option>').val(item).text(item).appendTo('#state');
			});
		},
		error: function(error, status, msg) {
			alert("Error while fetching state for country");
		}
	});
}