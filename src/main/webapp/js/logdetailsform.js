$( document ).ready(function() {
	$('#logdetailstable').DataTable( {
		"ajax": {
			"url"    	   : "https://pmp.heartfulness.org/pmp/api/log/loadlogdetailsdata?id="+$("#log_details_id").val(),
			"type" 		   : "GET",
			"contentType"  : "application/json",
			"async"		   : "false"
		},
		"columns": [
		            { "data": "srNo" },
		            { "data": "endpoint" },
		            { "data": "requestedTime" },
		            { "data": "responseTime" },
		            { "data": "timeDifference" },
		            { "data": "status" },
		            { "data": "viewReqRespData" }
		            ]
	});

});

function loadLogDetailsErrorPopup(id){
	//alert(id);
	//$( "#errorpopupbody" ).load( "/pmp/api/log/loaderrorlogdetailsform?id="+id);
	//$( "#errorpopup" ).show();
	$("#eldpopup-body").load("/pmp/api/log/loaderrorlogdetailsform?id="+id);
	$("#eldpopup").show();
	var eldmodal = document.getElementById('eldpopup');
	var eldspan = document.getElementsByClassName("eclose")[0];
	var modal = document.getElementById('popup');
	eldmodal.style.display = "block";
	eldspan.onclick = function() {
		eldmodal.style.display = "none";
	}
	window.onclick = function(event) {
	 if (event.target == eldmodal) {
		 eldmodal.style.display = "none";
	 }else if(event.target == modal){
		 modal.style.display = "none";
	 }
	}
}

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