<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Heartfulness</title>
<link rel="stylesheet" href="/pmp/css/jquery-ui.css" />
<link rel="stylesheet" href="/pmp/css/normalize.css">
<link rel="stylesheet" href="/pmp/css/skeleton.css">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="/pmp/js/jquery-1.11.0.min.js"></script>
<script type="text/javascript" src="/pmp/js/jquery.leanModal.min.js"></script>
<link type="text/css" rel="stylesheet" href="/pmp/css/style.css" />
<style>
.mandatory {
	color: red;
	font-weight: bold;
}
</style>
</head>
<header id="home" class="header" style="min-height: 91px">
	<div id="main-nav" class="navbar  bs-docs-nav" align="center">
		<div class="container">
			<a href="" class="navbar-brand"><img src="/pmp/images/logo.jpg"
				alt=""></a>
		</div>
	</div>
</header>
<script type="text/javascript" language="javascript">
	$(document)
			.ready(
					function() {
						var url = 'http://10.1.29.23:7080/pmp/api/authenticate';
						var getuserurl = 'http://10.1.29.23:7080/pmp/api/v1/user';
						var id;
						$("#submit")
								.click(
										function() {
											$
													.ajax({
														url : url,
														contentType : "application/json",
														dataType : "json",
														type : "POST",
														data : JSON
																.stringify(getFormData($("#loginform"))),
														success : function(
																loginresponse) {
															$
																	.ajax({
																		url : getuserurl,
																		contentType : "application/json",
																		dataType : "json",
																		type : "GET",
																		headers : {
																			'Content-Type' : 'application/json',
																			'Authorization' : loginresponse.access_token
																		},
																		success : function(
																				response) {
<%-- 		id = response.id,
																					alert(response.id);
																			//alert(JSON.parseJSON(response.responseText));
																		
																			window.location.href = "/pmp/index"; --%>
	$(
																					'#access_token')
																					.val(
																							loginresponse.access_token);
																			$(
																					'#id')
																					.val(
																							response.id);
																			$(
																					'#first_name')
																					.val(
																							response.first_name);
																			$(
																					'#last_name')
																					.val(
																							response.last_name);
																			$(
																					'#email')
																					.val(
																							response.email);
																			$(
																					'#gender')
																					.val(
																							response.gender);
																			$(
																					'#mobile')
																					.val(
																							response.mobile);
																			$(
																					'#address')
																					.val(
																							response.address);
																			$(
																					'#state')
																					.val(
																							response.state);
																			$(
																					'#country')
																					.val(
																							response.country);
																			$(
																					'#city')
																					.val(
																							response.city);
																			$(
																					'#index')
																					.submit();

																		},
																		error : function(
																				loginresponse) {
																			//alert('error user');
																		}

																	});

														},
														error : function(
																response) {
															//alert(response);
															$("#changepassword")
																	.html("");
															$("#error")
																	.html(
																			"Invalid Username/Password");
														}
													});
										});
						$("#register")
								.click(
										function() {
											var password = $('#password1')
													.val();
											var confirmPassword = $(
													'#password1').val();
											if (password != confirmPassword) {
												$("#changepassword").html("");
												$("#regerror").html("");
												$("#emailerror")
														.html(
																"Password and confirm password doest not match");
												$('#signup_form')[0].reset();
												return false;
											}
											$
													.ajax({
														url : "http://10.1.29.23:7080/pmp/api/users",
														type : "POST",
														/* async : false, */
														contentType : "application/json",
														dataType : "json",
														data : JSON
																.stringify(getFormData($("#signup_form"))),
														success : function(
																response) {
															/* 	if (response == "error") {
																	$('#signup_form')[0].reset();
																	$("#emailerror").html("");
																	$("#changepassword").html("");
																	$("#regerror").html("Error While Creating account..! Please Provide all Mandatory Fileds..!");
																}
																if (response == "accounterror") {
																	//alert("accounterror");
																	$('#signup_form')[0].reset();
																	$("#changepassword").html("");
																	$("#regerror").html("");
																	$("#emailerror").html("Email already exists for this email");
																}
																if (response == "changepassword") {
																	$('#signup_form')[0].reset();
																	$("#emailerror").html("");
																	$("#regerror").html("");
																	$("#changepassword").html("Registered successfully.! Login to continue.!");
																} */
															$("#changepassword")
																	.html(
																			"Registered successfully.! Login to continue.!");
															$('#signup_form')[0]
																	.reset();
														},
														error : function(
																response) {
															alert(response);
															$("#changepassword")
																	.html(
																			"Email Id is not vaild & should be unique set.");
														}

													});

										});
					});
	function getFormData($form) {
		var unindexed_array = $form.serializeArray();
		var indexed_array = {};

		$.map(unindexed_array, function(n, i) {
			indexed_array[n['name']] = n['value'];
		});

		return indexed_array;
	}
</script>
<body
	style="background: url(images/1.png) no-repeat center center fixed; -webkit-background-size: cover; -moz-background-size: cover; -o-background-size: cover; background-size: cover;">

	<div class="container">
		<div class="container">
			<!-- <h1>Hear</h1> -->
			<c:if test="${signout!=null}">
				<div style="color: red">${signout}</div>
			</c:if>
			<div style="padding-top: 150px;">
				Click <a id="modal_trigger" href="#modal" class=""> here</a> to
				Login/Register
			</div>
			<div id="modal" class="popupContainer"
				style="display: none; top: 10px">
				<header class="popupHeader">
					<span class="header_title">Login</span> <span class="modal_close"><i
						class="fa fa-times"></i></span>
				</header>

				<section class="popupBody">
					<!-- Social Login -->
					<div class="social_login">
						<div align="center" class="row">
							<h3>Login</h3>
							<form:form method="post" action="/pmp/login" id="loginform">
								<table width="400" height="100">
									<tr>
										<td colspan="2"><div id="error" align="center"
												style="color: red"></div></td>
									</tr>
									<tr>
										<td>User name<span class="mandatory"> *</span></td>
										<td><input id="username" name="username" type="email"
											class="six columns" placeholder="Enter eMail"></td>
									</tr>
									<tr>
										<td>Password<span class="mandatory"> *</span></td>
										<td><input id="password" name="password" type="password"
											class="six columns" placeholder="Enter Password"></td>
									</tr>
									<tr>
										<td colspan="3" align="left"><input type="button"
											value="Submit" id="submit" class="button-primary"></td>
									</tr>
								</table>
							</form:form>
							<form:form method="POST" action="/pmp/index" id="index"
								modelAttribute="user">
								<form:input id="id" name="id" type="hidden" path="id" />
								<form:input id="first_name" name="first_name" type="hidden"
									path="first_name" />
								<form:input id="last_name" name="last_name" type="hidden"
									path="last_name" />
								<form:input id="email" name="email" type="hidden" path="email" />
								<form:input id="gender" name="gender" type="hidden"
									path="gender" />
								<form:input id="mobile" name="mobile" type="hidden"
									path="mobile" />
								<form:input id="address" name="address" type="hidden"
									path="address" />
								<form:input id="country" name="country" type="hidden"
									path="country" />
								<form:input id="state" name="state" type="hidden" path="state" />
								<form:input id="city" name="city" type="hidden" path="city" />
								<form:input id="access_token" name="access_token"
									path="access_token" type="hidden" />
								<input type="hidden" value="Submit" id="indexbtn"
									class="button-primary" />
							</form:form>
						</div>
						<div id="menu-bar" class="container">
							<div>
								New User? <a href="#" id="register_form"> Register</a> here
							</div>
						</div>
					</div>


					<!-- Register Form -->
					<div class="user_register" align="center">
						<div class="container" align="center">
							<div class="container2">
								<h3 class="text-center">Register</h3>
								<div id="menu-bar" class="container" align="left">
									<div>
										Already Registered User? <a href="#" id="login_form">
											Login</a> here
									</div>
									<div>
										<c:if test="${changepassword!=null}">
									Click <a href="#" id="login_form">here</a>to Login </c:if>
									</div>

									<div id="changepassword" align="center" style="color: green"></div>
									<form:form class="signup" id="signup_form" method="post"
										action="/pmp/signup" modelAttribute="newUser">
										<table>
											<tr>
												<td>First Name<span class="mandatory"> *</span></td>
												<td><form:input name="firstName" id="firstname"
														type="text" class="four columns"
														placeholder="Enter FirstName" path="first_name" /></td>
											</tr>
											<tr>
												<td>Last Name<span class="mandatory"> *</span></td>
												<td><form:input name="lastName" id="lastname"
														type="text" class="four columns"
														placeholder="Enter LastName" path="last_name" /></td>
											</tr>
											<tr>
												<td>eMail<span class="mandatory"> *</span></td>
												<td><form:input name="eMail" id="email" type="text"
														class="four columns" placeholder="Enter eMail"
														path="email" /></td>
											</tr>
											<tr>
												<td>Password<span class="mandatory"> *</span></td>
												<td><form:password name="password" id="password1"
														class="four-columns" placeholder="Enter Password"
														path="password" /></td>
											</tr>
											<tr>
												<td>Confirm Password<span class="mandatory"> *</span></td>
												<td><form:password class="four-columns"
														placeholder="Enter Confirm Password"
														path="confirmPassword" /></td>
											</tr>
											<tr>
												<td colspan="2" align="right"><input type="button"
													value="Register" id="register" class="button-primary"></td>
											</tr>
										</table>
									</form:form>
								</div>
							</div>
						</div>
					</div>
				</section>
			</div>
		</div>
	</div>

	<script type="text/javascript">
		//Plugin options and our code
		$("#modal_trigger").leanModal({
			top : 100,
			overlay : 0.6,
			closeButton : ".modal_close"
		});

		$(function() {
			// Calling Login Form
			$("#login_form").click(function() {
				$('#loginform')[0].reset();
				$(".user_register").hide();
				$(".social_login").show();
				return false;
			});

			// Calling Register Form
			$("#register_form").click(function() {
				$('#signup_form')[0].reset();
				$(".social_login").hide();
				$(".user_register").show();
				$(".header_title").text('Register');
				return false;
			});

		});
	</script>

</body>
</html>