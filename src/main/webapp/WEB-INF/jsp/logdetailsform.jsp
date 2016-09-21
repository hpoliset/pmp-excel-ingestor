<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Log Details</title>
<link rel="stylesheet" href="/pmp/css/pmpapilogdetails.css" />
<link rel="stylesheet" href="/pmp/css/jquery.dataTables.min.css" />
<!-- <link rel="stylesheet" href="/pmp/css/modal.css" /> -->
<script type="text/javascript" src="/pmp/js/jquery-1.11.0.min.js"></script>
<script type="text/javascript" src="/pmp/js/logdetailsform.js"></script>
<script type="text/javascript" src="/pmp/js/jquery.dataTables.min.js"></script>
</head>
<body>
	<h2 align="center">PMP API Log Details</h2>
	<input type="hidden" id="log_details_id" value = "<%=request.getParameter("id")%>" >
	<table class="responstable" id="logdetailstable" width="100%" >
		<thead>
			<tr>
				<th>Sr.No</th>
				<th>Endpoint</th>
				<th>Requested Time</th>
				<th>Response Time</th>
				<th>Total Time Taken(ms)</th>
				<th>Status</th>
				<th>View Errors</th>
			</tr>
		</thead>
	</table>
	
	<!-- <div class="popup" data-popup="popup-1" id="errorpopup">
	 <a class="popup-close" data-popup-close="popup-1" href="#">X</a>
	    <div class="popup-inner" id="errorpopupbody">
	     
	    </div>
	</div> -->
	
	<div class="modal" id="eldpopup">
		<span class="eclose">x</span>
		<div class="modal-content" id="eldpopup-body" >
		</div>
	</div>
</body>
</html>