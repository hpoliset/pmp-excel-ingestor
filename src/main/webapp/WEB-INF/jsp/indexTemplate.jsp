<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Heartfulness</title>
<link rel="stylesheet" href="/pmp/css/indexstyle.css">
<link rel="stylesheet" href="/pmp/css/new/style.css" type="text/css" media="screen, projection, tv" />
<link rel="stylesheet" href="/pmp/css/new/style-print.css" type="text/css" media="print" />
<script type="text/javascript" src="/pmp/js/jquery-1.11.0.min.js"></script>
<script type="text/javascript" src="/pmp/js/jquery.leanModal.min.js"></script>
<script type="text/javascript" src="/pmp/js/jquery.js"></script>
</head>
<script type="text/javascript">
	function callIngestionForm(object) {
	   $.ajax({
	       url:"/pmp/session",
	       type: "GET",
	       success: function(response) {
	    	 if(response=="success"){
	    		 if ($("#indexmenu").attr("target") == undefined) {
	  				$("#indexmenu").attr("target", "out");
	  			}
	 			if (object.id === "singlefileupload") {
	 				 $("#indexmenu").attr("action", "/pmp/ingest/inputForm"); 
	 			}
	 		/* 	if (object.id === "updateProfile") {
	 				$("#indexmenu").attr("action", "/pmp/profile");
	 			} */
	 			if (object.id === "bulkUploadForm") {
	 				$("#indexmenu").attr("action", "/pmp/ingest/bulkUploadForm");
	 			}
	 			if (object.id === "reportsForm") {
	 				$("#indexmenu").attr("action", "/pmp/reports/reportsForm");
	 			}
	 			if (object.id === "home") {
	 				$("#indexmenu").attr("action", "/pmp/hfn");
	 			}
	 			if (object.id === "eventsform") {
	 				$("#indexmenu").attr("action", "/pmp/eventForm");
	 			}
	 			document.forms['indexmenu'].submit();
	    	 }else if(response=="sessionexpired"){
	         	 window.location.href = "/pmp/signout"; 
	         }				           
	       },
	       error:function(response){
	       }
	    }); 
			
		
	}
</script>
<body style="min-width: 900px;max-width:1200px;margin:0 auto; background-color:#dee6ed ; ">
<br>
<table style="width: 1100px; height:90px; border: 1px solid #737373; margin: 0 auto; ">
<tr><td><img src="/pmp/images/3.JPG" alt="" style="width: 1120px; height:100px;"></td></tr>
</table>
<div class="template"  style="padding: 0 39px 0 39px; ">
		<ul id="menu">
			<li><a style="visibility:hidden; "></a>
			<li class=""><a  onmouseover="" style="padding-left: 15px;cursor: pointer;" onclick="javascript:callIngestionForm(this);"
				  id="home" >Home</a></li>
			<!-- <li><a onclick="javascript:callIngestionForm(this);"
				id="updateProfile" class="menus">Update Profile</a></li> -->
			<li class=""><a onclick="javascript:callIngestionForm(this);"
				id="singlefileupload"onmouseover="" style="cursor: pointer;">Single File Uploader</a></li>
			<li class=""><a onclick="javascript:callIngestionForm(this);"
				id="bulkUploadForm"  onmouseover="" style="cursor: pointer;">Bulk Uploader</a></li>
			<li class=""><a onclick="javascript:callIngestionForm(this);"
				id="reportsForm"  onmouseover="" style="cursor: pointer;">Reports</a></li>
			<li class=""><a onclick="javascript:callIngestionForm(this);"
				id="eventsform"  onmouseover="" style="cursor: pointer;">Events</a></li>
			<li class="last"><a href="/pmp/signout">Signout</a></li>
		</ul>
		<form action="" id="indexmenu"></form>
		<form>
			<input id="auth" type="hidden" value="<%=session.getAttribute("Authentication")%>" />
		</form>
	</div>
	<div class="template" >
		<iframe class="box-in"  src="/pmp/hfn" id="mainFrame" name="out" frameborder="0" name="mainFrame"></iframe>
	</div>
</body>
</html>