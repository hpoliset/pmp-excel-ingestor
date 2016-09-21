$( document ).ready(function() {
	$('#errorlogtable').DataTable( {
		"ajax": {
			"url"    	   : "http://10.1.29.80:7081/pmp/api/log/loaderrorlogdata?id="+$("#error_log_id").val(),
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

