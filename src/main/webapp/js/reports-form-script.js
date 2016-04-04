$(document).ready(function() {
	$("#fromDate").datepicker({
		dateFormat:'dd-mm-yy',
		changeMonth: true,
		changeYear:true,
		showOn:"both",
		buttonImage: "/pmp/images/calendar.png",
		buttonImageOnly: true,
		buttonText: "Event From Date"
	});
	$("#tillDate").datepicker({
		dateFormat:'dd-mm-yy',
		changeMonth: true,
		changeYear:true,
		showOn:"both",
		buttonImage: "/pmp/images/calendar.png",
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

function isValidDate() {
	var fromDate = document.getElementById('fromDate').value;
	var tillDate = document.getElementById('tillDate').value;
	var pattern = /^([0-9]{2})-([0-9]{2})-([0-9]{4})$/;
    if (!pattern.test(fromDate)) {
    	alert("Please enter valid From Date");
        return false;
    }
    if(!pattern.test(tillDate)){
    	alert("Please enter valid Till Date");
        return false;
    }
    if(calculateDayRange(parseDate(fromDate), parseDate(tillDate))>30){
    	alert("Please select valid Date range.Report can be generated for a maximum of 30 days.");
    	return false;
    }
    if(calculateDayRange(parseDate(fromDate), parseDate(tillDate))<0){
    	alert("Please select valid Date range.");
    	return false;
    }
    return true;
}
function parseDate(str) {
    var mdy = str.split('-')
    var date =  new Date(mdy[2], mdy[1]-1, mdy[0]);
    return date;
}

function calculateDayRange(first, second) {
    return Math.round((second-first)/(1000*60*60*24));
}
