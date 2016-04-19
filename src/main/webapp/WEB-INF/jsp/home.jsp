<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Heartfulness</title>
<link rel="stylesheet" href="/pmp/css/jquery-ui.css" />
<link rel="stylesheet" href="/pmp/css/normalize.css">
<link rel="stylesheet" href="/pmp/css/skeleton.css">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="/pmp/js/jquery-1.11.0.min.js"></script>
<script type="text/javascript" src="/pmp/js/jquery.leanModal.min.js"></script>
<link type="text/css" rel="stylesheet" href="/pmp/css/style.css" />

<style type="text/css">
#horizon {
	background-color: rgba(0, 0, 0, 0);
	color: #67676;
	display: block;
	height: 120px;
	left: 0;
	overflow: visible;
	position: absolute;
	text-align: center;
	top: 50%;
	visibility: visible;
	width: 100%;
}

#content {
	height: 400px;
	left: 50%;
	margin-left: -400px;
	position: absolute;
	top: -200px;
	visibility: visible;
	width: 800px;
}
</style>
<script>
$(document).ready(function(){
    $(document).ajaxStart(function(){
        $("#wait").css("display", "block");
    });
    $(document).ajaxComplete(function(){
        $("#wait").css("display", "none");
    });
});
</script>
<script type="text/javascript" language="javascript">
	$(document)
			.ready(
					function() {
						var url = 'https://pmpbeta.heartfulness.org/pmp/api/authenticate';
						var getuserurl = 'https://pmpbeta.heartfulness.org/pmp/api/v1/user';
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
																			alert(response);
																				window.location.href = "/pmp/index";
																		},
																		error : function(
																				loginresponse) {
																		}

																	});

																	/* window.location.href = "/pmp/index"; */
														},
														error : function(
																response) {
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
														url : "https://pmpbeta.heartfulness.org/pmp/api/users",
														type : "POST",
														/* async : false, */
														contentType : "application/json",
														dataType : "json",
														data : JSON
																.stringify(getFormData($("#signup_form"))),
														success : function(
																response) {
															$("#changepassword")
																	.html(
																			"Registered successfully.! Login to continue.!");
															$('#signup_form')[0]
																	.reset();
														},
														error : function(
																response) {
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
</head>
<body style="background-color:  #dee6ed;">
	<div id="mainDiv" style="width: 100%; height: 100%;">
		<div id="horizon">
			<div id="content">
				<div
					style="width: 800px; height: 400px; margin: 0 auto; background-color: white; border-radius: 10px 10px 10px 10px; -moz-border-radius: 10px 10px 10px 10px; -webkit-border-radius: 10px 10px 10px 10px; -webkit-box-shadow: 10px 10px 5px 0px rgba(103, 104, 107, 1); -moz-box-shadow: 10px 10px 5px 0px rgba(103, 104, 107, 1); box-shadow: 10px 10px 5px 0px rgba(103, 104, 107, 1);">
					<div
						style="width: 750px; height: 380px; margin: 0 auto; padding-top: 20px; background-image: url('/pmp/images/poster.jpg'); no-repeat center center fixed; -webkit-background-size: cover; -moz-background-size: cover; -o-background-size: cover; background-size: cover;">
						<table width="400px" height="650px" align="center"
							style="padding-left: 100px">
							<tr>
								<td style="padding: 70px 0px 90px 525px">
									<table width="280px;" border="0">
										<tr>
											<td style="font-size: medium;">
											<!-- <input type="button" id="modal_trigger" class="button-primary" value="Login/Register" /> -->
											Click <a id="modal_trigger" href="#modal" class="" > here</a> to Login/Register
											</td>
											<c:if test="${redirecturl!=null}">
												<input id="url" type="hidden" value="<%=session.getAttribute("redirecturl")%>" />
											</c:if>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</div>
				</div>
			</div>
		</div>
		<div id="modal" class="popupContainer"
				style="display: none; top: 10px">
				<header class="popupHeader">
					<span class="header_title">Login</span> 
					<a class="modal_close" href="#"></a>
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
						</div>
						
						<div id="menu-bar" class="container">
							<div>
								New User? <a href="#" id="register_form"> Register</a> here
							</div>
						</div>
						
						
						<div id="wait" style="display:none;width:69px;height:89px;position:absolute;top:50%;left:50%;padding:2px;">
						<img src='/pmp/images/demo_wait.gif' width="64" height="64" /><br>Loading..</div>
						
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
								
								<div id="wait" style="display:none;width:69px;height:89px;position:absolute;top:50%;left:50%;padding:2px;">
								<img src='/pmp/images/demo_wait.gif' width="64" height="64" /><br>Loading..</div>
						
							</div>
						</div>
					</div>
				</section>
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