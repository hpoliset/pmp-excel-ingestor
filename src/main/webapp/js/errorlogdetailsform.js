$( document ).ready(function() {
	$('#errorlogdetailstable').DataTable( {
		"ajax": {
			"url"    	   : "https://pmpbeta.heartfulness.org/pmp/api/log/loaderrorlogdetailsdata?id="+$("#error_log_details_id").val(),
			"type" 		   : "GET",
			"contentType"  : "application/json",
			"async"		   : "false"
		},
		"columns": [
		           { "data": "srNo" },
		           { "data": "errorMessage" },
		           { "data": "requestBody" },
		           { "data": "responseBody" }
		]
	});

});


/*$(function() {
    //----- OPEN
    $('[data-popup-open]').on('click', function(e)  {
    	e.preventDefault();
        var targeted_popup_class = jQuery(this).attr('data-popup-open');
        $('[data-popup="' + targeted_popup_class + '"]').fadeIn(350);
    });
 
    //----- CLOSE
    $('[data-popup-close]').on('click', function(e)  {
    	e.preventDefault();
        var targeted_popup_class = jQuery(this).attr('data-popup-close');
        $('[data-popup="' + targeted_popup_class + '"]').fadeOut(350);
    });
});*/