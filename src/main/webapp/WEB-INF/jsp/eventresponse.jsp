<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<!--  <meta http-equiv="Refresh" content="5;url=/"> -->
<title>Event Creation Page</title>
<link rel="stylesheet" href="/pmp/css/normalize.css">
<link rel="stylesheet" href="/pmp/css/skeleton.css">
</head>
<body style="background-color: #dee6ed;">

	<div class="container" align="center" style="padding-top: 3%">

		<h2>${result}</h2>
			<a href="/pmp/eventForm"><input class="button-primary" type="button" value="Back" onclick="returnToIndex()" />
		</a>
	</div>
</body>
</html>