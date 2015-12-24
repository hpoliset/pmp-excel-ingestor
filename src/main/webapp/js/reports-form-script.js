$(document).ready(function() {
	$("#fromDate").datepicker({
		dateFormat:'dd/M/yy',
		changeMonth: true,
		changeYear:true,
		showOn:"both",
		buttonImage: "../images/calendar.png",
		buttonImageOnly: true,
		buttonText: "Event From Date"
	});
	$("#tillDate").datepicker({
		dateFormat:'dd/M/yy',
		changeMonth: true,
		changeYear:true,
		showOn:"both",
		buttonImage: "../images/calendar.png",
		buttonImageOnly: true,
		buttonText: "Event Till Date"
	});
	$('#country').change(function(event){
		populateStatesForCountry($(this).val());		
	});
});

function populateStatesForCountry(countryName){
	$.ajax({
		url: '/reports/getStates',
		type:'POST',
		data:{eventCountry:countryName},
		async: false,
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