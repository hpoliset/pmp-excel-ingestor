<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html >
<%
	if (session.getAttribute("AuthenticationResponse") == null) {
%><jsp:forward page="Home.jsp" />
<%
	} else {
%>
<html lang="en" ng-app>
<head>
<meta charset="utf-8">
<title>Heartfulness</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">
<link rel="stylesheet" href="/pmp/css/new/style.css" type="text/css" media="screen, projection, tv" />
<link rel="stylesheet" href="/pmp/css/new/style-print.css" type="text/css" media="print" />
<link href="/pmp/css/logincss/project.css" rel="stylesheet">
<link rel="stylesheet" href="/pmp/css/logincss/bootstrap.min.css">
<link rel="stylesheet" href="/pmp/css/logincss/jquery-ui.css">
<link rel="stylesheet" href="/pmp/css/logincss/bootstrap-theme.min.css">
</head>
<body>
	<div class="container" width="100%">
		<div class="row">
			<div class="">
				<form:form class="login" method="POST" action="/pmp/profile"
					modelAttribute="user">
					<div class="row"
						style="padding-top: 10px; font-weight: bold; font-size: 23px">
						<div align="left" class="four columns">Welcome ${username} </div>
					</div>
					<div id="div_id_firstname" class="form-group" align="left" >
						<label for="id_firstname" class="control-label  requiredField">
							First Name </label>
						<div class="controls ">
							<form:input autofocus="autofocus"
								class="textinput textInput form-control" id="id_firstname"
								name="firstname" placeholder="First Name" type="text"
								path="firstName" readonly="true" />
						</div>
					</div>
					<div id="div_id_lastname" class="form-group" align="left">
						<label for="id_lastname" class="control-label  requiredField">
							Last Name</label>
						<div class="controls ">
							<form:input class="textinput textInput form-control"
								id="id_lastname" name="lastname" placeholder="Last Name"
								type="text" path="lastName" readonly="true"/>
						</div>
					</div>
					
					<div id="div_id_gender" class="form-group" align="left">
						<label for="id_gender" class="control-label  requiredField">
							Gender </label>
						<div class="controls ">
							<form:select autofocus="autofocus"
								class="textinput textInput form-control" id="id_gender"
								name="gender" placeholder="Gender" type="text" path="gender" >
								<form:option value="Male"></form:option>
								<form:option value="Female"></form:option>
								</form:select>
						</div>
					</div>
					<div id="div_id_email" class="form-group" align="left">
						<label for="id_email" class="control-label  requiredField">
							E-mail </label>
						<div class="controls ">
							<form:input class="textinput textInput form-control"
								id="id_email" name="email" placeholder="E-mail address"
								type="email" path="email" readonly="true" />
						</div>
					</div>
					<div id="div_id_mobile" class="form-group" align="left">
						<label for="id_mobile" class="control-label  requiredField">
							Mobile </label>
						<div class="controls ">
							<form:input class="textinput textInput form-control"
								id="id_mobile" name="mobile" placeholder="Mobile" type="text"
								path="mobile" />
						</div>
					</div>
					<div id="div_id_address" class="form-group" align="left">
						<label for="id_address" class="control-label  requiredField">
							Address </label>
						<div class="controls ">
							<form:input class="textinput textInput form-control"
								id="id_address" name="address" placeholder="Address" type="text"
								path="address" />
						</div>
					</div>

					<div id="div_id_city" class="form-group" align="left">
						<label for="id_city" class="control-label  requiredField">
							City </label>
						<div class="controls ">
							<form:input class="textinput textInput form-control" id="id_city"
								name="city" placeholder="city" type="text" path="city" />
						</div>
					</div>
					<div id="div_id_state" class="form-group" align="left">
						<label for="id_state" class="control-label  requiredField">
							State </label>
						<div class="controls ">
							<form:input class="textinput textInput form-control"
								id="id_state" name="sate" placeholder="State" type="text"
								path="state" />
						</div>
					</div>
					<div id="div_id_country" class="form-group" align="left">
						<label for="id_country" class="control-label  requiredField">
							Country </label>
						<div class="controls ">
							<form:input class="textinput textInput form-control"
								id="id_country" name="country" placeholder="Country" type="text"
								path="country" />
						</div>
					</div>
					<div>
						<div align="left">
							<button class="btn btn-primary" type="submit">UpdateProfile</button>
							<c:if test="${updateMsg!=null}">
								<div align="right" style="font-style: italic; color: green; font-size: large;">${updateMsg}</div>
							</c:if>
						</div>
					</div>
				</form:form>
			</div>
		</div>

	</div>
</body>
</html>

<%}%>