<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="org.srcm.heartfulness.model.CurrentUser"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="/pmp/css/pmpapilogdetails.css" />
<link rel="stylesheet" href="/pmp/css/jquery.dataTables.min.css" />
<link rel="stylesheet" href="/pmp/css/modal.css" />
<script type="text/javascript" src="/pmp/js/jquery-1.11.0.min.js"></script>
<script type="text/javascript" src="/pmp/js/logform.js"></script>
<script type="text/javascript" src="/pmp/js/jquery.dataTables.min.js"></script>
<title>Log Details</title>
</head>
<body>
	<!-- <img style="width: 20px; height: 100px;" src="/pmp/images/heartfulness.jpg"> -->
	<%
		CurrentUser currentUser = (CurrentUser)session.getAttribute("Authentication");
	%>
	<%-- <ul class="topnav" id="myTopnav">
		<li style="padding-left: 43%; font-size: 25px;"><a href=""><b>HEARTFULNESS</b></a></li>
		<li style="padding-left: 13%;"><a href="" style="padding-top:9%;"> Welcome : <%= currentUser.getUsername() %></a></li>
		<li style="padding-left: 7%;"><a href="/pmp/signout" style="padding-top:35%;">Logout</a></li>
		<li class="icon"><a href="javascript:void(0);" onclick="myFunction()">&#9776;</a></li>
	</ul> --%>
	<ul class="topnav" id="myTopnav">
		<li style="font-size: 25px;"><a href=""><b>HEARTFULNESS</b></a></li>
		<li style="float: right;"><a href="/pmp/signout">Logout</a></li>
		<li style="float: right;"><a href=""> Welcome : <%= currentUser.getUsername() %></a></li>
		<li class="icon"><a href="javascript:void(0);"
			onclick="myFunction()">&#9776;</a></li>
	</ul>
	<h2 align="center">Heartfulness Log Details</h2>

	<table class="responstable" id="logtable" cellspacing="0" width="100%">
		<thead>
			<tr>
				<th>Sr.No</th>
				<th>Username</th>
				<th>IP Address</th>
				<th>API Name</th>
				<th>Requested Time</th>
				<th>Response Time</th>
				<th>Total Time Taken(ms)</th>
				<th>Status</th>
				<th>View Details</th>
				<th>View Errors</th>
			</tr>
		</thead>

	</table>

	<!-- <div class="popup" data-popup="popup-1" id="popup">
	 <a class="popup-close" data-popup-close="popup-1" href="#">X</a>
	    <div class="popup-inner" id="popupbody">
	     
	    </div>
	</div>
	
	<div class="popup" data-popup="popup-1" id="errorpopup">
	 <a class="popup-close" data-popup-close="popup-1" href="#">X</a>
	    <div class="popup-inner" id="errorpopupbody">
	     
	    </div>
	</div> -->

	<div class="modal" id="popup">
		<span class="close">x</span>
		<div class="modal-content" id="popup-body" >
		</div>
	</div>
	<div class="modal" id="epopup">
		<span class="close">x</span>
		<div class="modal-content" id="epopup-body" >
		</div>
	</div>

</body>
</html>