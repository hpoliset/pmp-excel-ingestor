$( document ).ready(function() {
	$('#logtable').DataTable( {
		"ajax": {
			"url"    	   : "https://pmpbeta.heartfulness.org/pmp/api/log/loadlogdata",
			"type" 		   : "POST",
			"contentType" : "application/json"
		},
		"columns": [
		            { "data": "serialNo" },
		            { "data": "username" },
		            { "data": "ipAddress" },
		            { "data": "apiName" },
		            { "data": "totalRequestedTime" },
		            { "data": "totalResponseTime" },
		            { "data": "timeDifference" },
		            { "data": "status" },
		            { "data": "viewAccessLogDetailsData" },
		            { "data": "viewReqRespBody" }
		            ]
	});

});

function loadPopup(id){
	//alert(id);
	//$( "#popupbody" ).load( "/pmp/api/log/loadlogdetailsform?id="+id);
	//$( "#popup" ).show();
	$("#popup-body").load("/pmp/api/log/loadlogdetailsform?id="+id);
	$("#popup").show();
	var modal = document.getElementById('popup');
	var span = document.getElementsByClassName("close")[0];
	modal.style.display = "block";
	span.onclick = function() {
		modal.style.display = "none";
	}
	window.onclick = function(event) {
	 if (event.target == modal) {
	     modal.style.display = "none";
	 }
	}
}

function loadErrorPopup(id){
	//alert(id);
	//$( "#errorpopupbody" ).load( "/pmp/api/log/loaderrorlogform?id="+id);
	//$( "#errorpopup" ).show();
	
	$("#epopup-body").load("/pmp/api/log/loaderrorlogform?id="+id);
	$("#epopup").show();
	var emodal = document.getElementById('epopup');
	var espan = document.getElementsByClassName("close")[1];
	emodal.style.display = "block";
	espan.onclick = function() {
		emodal.style.display = "none";
	}
	window.onclick = function(event) {
	 if (event.target == emodal) {
	     emodal.style.display = "none";
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

/* Toggle between adding and removing the "responsive" class to topnav when the user clicks on the icon */
function myFunction() {
    var x = document.getElementById("myTopnav");
    if (x.className === "topnav") {
        x.className += " responsive";
    } else {
        x.className = "topnav";
    }
}