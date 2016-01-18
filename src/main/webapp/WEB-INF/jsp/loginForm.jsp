<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" href="../css/jquery-ui.css" />
<link rel="stylesheet" href="../css/normalize.css">
<link rel="stylesheet" href="../css/skeleton.css">
<link href='//fonts.googleapis.com/css?family=Raleway:400,300,600'
	rel='stylesheet' type='text/css'>
<link rel="stylesheet" href="../css/normalize.css">
<link rel="stylesheet" href="../css/skeleton.css">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>User Login</title>
</head>
<body>
	<div align="center" class="row">
		<h2>HEARTFULNESS</h2>
		<h3>USER LOGIN</h3>
		<form:form method="post" action="eventServices">
			<table>
				<tr>
					<td>User Name:</td>
					<td><input name="userName" type="text" class="four-columns"
						placeholder="Enter UserName"></td>
				</tr>
				<tr>
					<td>Password:</td>
					<td><input name="password" type="password"
						class="four-columns" placeholder="Enter Password"></td>
				</tr>
				<tr>
					<td colspan="2" align="right" ><input type="submit" value="Submit" class="button-primary"></td>
				</tr>
			</table>
			<div style="color: red">${error}</div>
		</form:form>
	</div>
</body>
</html>