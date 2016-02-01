<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Heartfulness</title>
<link rel="stylesheet" href="/pmp/css/jquery-ui.css" />
<link rel="stylesheet" href="/pmp/css/normalize.css">
<link rel="stylesheet" href="/pmp/css/skeleton.css">
<link rel="stylesheet" href="/pmp/css/normalize.css">
<link rel="stylesheet" href="/pmp/css/skeleton.css">

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="/pmp/js/jquery-1.11.0.min.js"></script>
<script type="text/javascript" src="/pmp/js/jquery.leanModal.min.js"></script>
<link type="text/css" rel="stylesheet" href="/pmp/css/style.css" />
<style>
.mandatory{
	color:red;
	font-weight:bold;
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
<script type="text/javascript">
	$(document)
			.ready(
					function() {
						var url = '/pmp/login';

						$("#submit")
								.click(
										function() {
											$
													.ajax({
														url : url,
														type : "POST",
														/* async : false, */
														data : {
															username : $(
																	'#username')
																	.val(),
															password : $(
																	'#password')
																	.val()
														},
														success : function(
																response) {
															//alert(response);
															if (response == "success") {
																/* $('#login_form')[0].reset(); */
																window.location.href = "/pmp/index";
															}
															if (response == "error") {
															//	alert(response);
															/* 	$('#login_form')[0].reset(); */
																/* if ("${error}" != null) { */
																	$("#error").html("Invalid Username/Password");
																//}

															}

														}

													});
										});
						$("#register")
								.click(
										function() {
											var password= $('#password1').val();
											var confirmPassword= $('#confirmPassword').val();
											$(confirmPassword)
											if(password!=confirmPassword){
												$("#changepassword").html("");
												$("#regerror").html("");
												$("#emailerror").html("Password and confirm password doest not match");
												return false;
											}
											$.ajax({
														url : "/pmp/signup",
														type : "POST",
														async : false,
														data : $("#signup_form").serialize(),
														success : function(
																response) {
															if (response == "error") {
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
															}
														}

													});

										});
					});
</script>
<body
	style=" background: url(images/1.png) no-repeat center center fixed; -webkit-background-size: cover; -moz-background-size: cover; -o-background-size: cover; background-size: cover;">
	<div class="container">
		<div class="container">
			<!-- <h1>Hear</h1> -->
			<c:if test="${signout!=null}">
					<div style="color: red">${signout}</div>
			</c:if>
			<div style="padding-top: 150px;">
				Click <a id="modal_trigger" href="#modal" class=""> here</a> to	Login/Register
			</div>

			<div id="modal" class="popupContainer" style="display: none;">
				<header class="popupHeader">
					<span class="header_title">Login</span> <span class="modal_close"><i
						class="fa fa-times" ></i></span>
				</header>

				<section class="popupBody">
					<!-- Social Login -->
					<div class="social_login">
						<div align="center" class="row">
							<h3>Login</h3>
							<form:form method="post" action="/pmp/login">
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
									Click <a href="#" id="login_form">here</a>to Login
									</c:if>
									</div>

									<div id="changepassword" align="center" style="color: green"></div>
									<form:form class="signup" id="signup_form" method="post"
										action="/pmp/signup" modelAttribute="newUser">
										<table>
											<tr>
												<td colspan="2">
													<div id="regerror" align="center" style="color: red"></div>
													<div id="emailerror" align="center" style="color: red"></div>
													<!-- <div id="accounterror" align="center" style="color: red"></div> -->
												</td>
											</tr>
											<tr>
												<td>First Name<span class="mandatory"> *</span></td>
												<td><form:input name="firstName" id="firstname"
														type="text" class="four columns"
														placeholder="Enter FirstName" path="firstName" /></td>
											</tr>
											<tr>
												<td>Last Name<span class="mandatory"> *</span></td>
												<td><form:input name="lastName" id="lastname"
														type="text" class="four columns"
														placeholder="Enter LastName" path="lastName" /></td>
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
														class="four-columns"
														placeholder="Enter Password" path="password" /></td>
											</tr>
											<tr>
											<td>Confirm Password<span class="mandatory"> *</span></td>
												<td><form:password 
														class="four-columns"
														placeholder="Enter Confirm Password" path="confirmPassword" /></td>
											</tr>
											<%-- <tr>
												<td>State:</td>
												<td><form:input name="state" id="state" type="text"
														class="four-columns" placeholder="Enter State"
														path="state" /></td>
											</tr>
											<tr>
												<td>Country:</td>
												<td><form:input name="country" id="country" type="text"
														class="four-columns" placeholder="Enter country"
														path="country" /></td>
											</tr> --%>
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
				$(".user_register").hide();
				$(".social_login").show();
				return false;
			});

			// Calling Register Form
			$("#register_form").click(function() {
				$(".social_login").hide();
				$(".user_register").show();
				$(".header_title").text('Register');
				return false;
			});

		});
	</script>

</body>
</html>
