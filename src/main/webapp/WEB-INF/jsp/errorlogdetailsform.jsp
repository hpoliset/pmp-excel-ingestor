<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Api Log Details Error</title>
<link rel="stylesheet" href="/pmp/css/pmpapilogdetails.css" />
<link rel="stylesheet" href="/pmp/css/jquery.dataTables.min.css" />
<link rel="stylesheet" href="/pmp/css/modal.css" />
<script type="text/javascript" src="/pmp/js/jquery-1.11.0.min.js"></script>
<script type="text/javascript" src="/pmp/js/errorlogdetailsform.js"></script>
<script type="text/javascript" src="/pmp/js/jquery.dataTables.min.js"></script>
</head>
<body>
	<h2 align="center">Request-Response Details</h2>
	<input type="hidden" id="error_log_details_id" value="<%=request.getParameter("id")%>">
	<table class="responstable" id="errorlogdetailstable" width="100%">
		<thead>
			<tr>
				<th>Sr.No</th>
				<th>Error Message</th>
				<th>Request Body</th>
				<th>Response Body</th>
			</tr>
		</thead>
	</table>
</body>
</html>