<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="header.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Heartfulness Event Services</title>
</head>
<body>
	<table width="100%" height="100">
		<tr>
			<td width="30px" height="780px" valign="top" style="padding-left: 15px;">
				<div id="menu-bar">
					<h6>Services:</h6>
					<a href="/ingest/inputForm" target="mainFrame">ExcelUploader</a> <br />
					<br /> <a href="/ingest/bulkUploadForm" target="mainFrame">BulkUploader</a>
					<br /> <br /> <a href="/reports/reportsForm" target="mainFrame">Reports</a>
				</div>
			</td>
			<td width="70opx" height="780px" valign="top">
				<div id="menu-content" class="container" style="padding-left: 50px;">
					<iframe id="mainFrame" frameborder="0" name="mainFrame"	width="1100px" height="690px"></iframe>
				</div>
			</td>
		</tr>
	</table>
</body>
</html>