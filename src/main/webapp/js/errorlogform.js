$( document ).ready(function() {
	$('#errorlogtable').DataTable( {
		"ajax": {
			"url"    	   : "https://pmp.heartfulness.org/pmp/api/log/loaderrorlogdata?id="+$("#error_log_id").val(),
			"type" 		   : "GET",
			"contentType"  : "application/json"
		},
		"columns": [
		            { "data": "serialNo" },
		            { "data": "errorMessage" },
		            { "data": "requestBody" },
		            { "data": "responseBody" }
		            ]
	});

});

