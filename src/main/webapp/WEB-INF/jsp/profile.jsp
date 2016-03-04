<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html >
<%-- <%
	if (session.getAttribute("AuthenticationResponse") == null) {
%><jsp:forward page="Home.jsp" />
<%
	} else {
%> --%>
<html lang="en" ng-app>
<head>
<meta charset="utf-8">
<title>Heartfulness</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">
<link rel="stylesheet" href="/pmp/css/new/style.css" type="text/css"
	media="screen, projection, tv" />
<link rel="stylesheet" href="/pmp/css/new/style-print.css"
	type="text/css" media="print" />
<link href="/pmp/css/logincss/project.css" rel="stylesheet">
<link rel="stylesheet" href="/pmp/css/logincss/bootstrap.min.css">
<link rel="stylesheet" href="/pmp/css/logincss/$-ui.css">
<link rel="stylesheet" href="/pmp/css/logincss/bootstrap-theme.min.css">
<script type="text/javascript" src="/pmp/js/jquery-1.11.0.min.js"></script>
<script type="text/javascript" src="/pmp/js/jquery.leanModal.min.js"></script>
</head>
<script type="text/javascript">
	 $(document).ready(function() {
		var url = 'http://pmpbeta.heartfulness.org/pmp/api/v1/user/${user.id}';
		var getuserurl = 'http://pmpbeta.heartfulness.org/pmp/api/v1/user';
		var access_token='${user.access_token}';
		$("#update").click(function() {
			$.ajax({
				url : url,
				contentType : "application/json",
				dataType : "json",
				type : "PUT",
				async:false,
				headers : {
					'Content-Type' : 'application/json',
					'Authorization' : access_token
				},
				data : JSON.stringify(getFormData($("#profileForm"))),
				success : function(response) {
					$('#state').val(response.state);
					$('#id').val(response.id);
					$('#first_name').val(response.first_name);
					$('#last_name').val(response.last_name);
					$('#email').val(response.email);
					$('#gender').val(response.gender);
					$('#mobile').val(response.mobile);
					$('#address').val(response.address);
					$('#state').val(response.state);
					$('#country').val(response.country);
					$('#city').val(response.city);
					getprofile(getuserurl,access_token);
					$("#updateDiv").html("Profile updated successfully");
				},
				error : function(response) {
					$("#updateMsg").html("error while updating profile");
				}
			});
		});
	}); 
	 function ajaxindicatorstop()
	 {
	 	$('#resultLoading .bg').height('100%');
	 	$('#resultLoading').fadeOut(300);
	 	$('body').css('cursor', 'default');
	 }

	 function ajaxindicatorstart(text){
	 	alert("loading");
	 	if($('body').find('#resultLoading').attr('id') != 'resultLoading'){
	 		$('body').append('<div id="resultLoading" style="display:none"><div><img src="images/loader.gif"><div id="loadingTextDiv">'+text+'</div></div><div class="bg"></div></div>');
	 	}
	 	$('#loadingTextDiv').html(text);
	 	$('#resultLoading').css({
	 		'width':'100%',
	 		'height':'100%',
	 		'position':'fixed',
	 		'z-index':'10000000',
	 		'top':'0',
	 		'left':'0',
	 		'right':'0',
	 		'bottom':'0',
	 		'margin':'auto'
	 	});
	 	$('#resultLoading .bg').css({
	 		'background':'#000000',
	 		'opacity':'0.7',
	 		'width':'100%',
	 		'height':'100%',
	 		'position':'absolute',
	 		'top':'0'
	 	});
	 	$('#resultLoading>div:first').css({
	 		'width': '250px',
	 		'height':'75px',
	 		'text-align': 'center',
	 		'position': 'fixed',
	 		'top':'0',
	 		'left':'0',
	 		'right':'0',
	 		'bottom':'0',
	 		'margin':'auto',
	 		'font-size':'16px',
	 		'z-index':'10',
	 		'color':'#ffffff'

	 	});
	 	$('#resultLoading .bg').height('100%');
	 	$('#resultLoading').fadeIn(300);
	 	$('body').css('cursor', 'wait');
	 	//console.log('ajax loading ended');
	 }
	 function getFormData($form) {
			var unindexed_array = $form.serializeArray();
			var indexed_array = {};

			$.map(unindexed_array, function(n, i) {
				indexed_array[n['name']] = n['value'];
			});

			return indexed_array;
		}
	 
	 function getprofile(getuserurl,access_token){
		<%--  alert("getprofile"+$('#profileForm').serialize());
			$
			.ajax({
				url : "/pmp/profile",
				contentType : "application/json",
				dataType : "json",
				type : "POST",
				async:false,
				data:$('#profileForm').serialize(),
				/* headers : {
					'Content-Type' : 'application/json',
					'Authorization' : access_token
				}, */
				success : function(
						response) {
							id = response.id,
							alert(response.id);
					//alert(JSON.parseJSON(response.responseText));
				
					window.location.href = "/pmp/index";
				/* 	$('#access_token').val(loginresponse.access_token);
					$('#id').val(response.id);
					$('#first_name').val(response.first_name);
					$('#last_name').val(response.last_name);
					$('#email').val(response.email);
					$('#gender').val(response.gender);
					$('#mobile').val(response.mobile);
					$('#address').val(response.address);
					$('#state').val(response.state);
					$('#country').val(response.country);
					$('#city').val(response.city);
					$('#index').submit(); */
					

				},
				error : function(
						loginresponse) {
					//alert('error user');
				}

			}); --%>
			$('#profileForm').submit();
	 }
</script>
<body>
	<div class="container" width="100%">
		<div class="row">
			<div class="">
				<form:form class="login" method="POST" modelAttribute="user"
					id="profileForm" action="/pmp/profile">
					<div class="row"
						style="padding-top: 10px; font-weight: bold; font-size: 23px">
						<div align="left" class="four columns">Welcome ${username}</div>
					</div>
					<div id="div_id_firstname" class="form-group" align="left">
						<label for="id_firstname" class="control-label  requiredField">
							First Name </label>
						<div class="controls ">
							<form:input autofocus="autofocus"
								class="textinput textInput form-control" id="id_firstname"
								name="first_name" placeholder="First Name" type="text"
								path="first_name" readonly="true" />
						</div>
					</div>
					<div id="div_id_lastname" class="form-group" align="left">
						<label for="id_lastname" class="control-label  requiredField">
							Last Name</label>
						<div class="controls ">
							<form:input class="textinput textInput form-control"
								id="id_lastname" name="last_name" placeholder="Last Name"
								type="text" path="last_name" readonly="true" />
						</div>
					</div>

					<div id="div_id_gender" class="form-group" align="left">
						<label for="id_gender" class="control-label  requiredField">
							Gender </label>
						<div class="controls ">
							<form:select autofocus="autofocus"
								class="textinput textInput form-control" id="id_gender"
								name="gender" placeholder="Gender" type="text" path="gender">
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
								placeholder="State" type="text"
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
					<form:hidden class="textinput textInput form-control"
								path="id"/>
					<div>
						<div align="left">
							<button class="btn btn-primary" id="update" type="button">UpdateProfile</button>
								<c:if test="${updateMsg!=null}">
								<div align="right"
									style="font-style: italic; color: green; font-size: large;" id="updateMsg">${updateMsg}</div>
								</c:if>
								<c:if test="${updateDiv!=null}">
								<div align="right"
									style="font-style: italic; color: green; font-size: large;" id="updateDiv">${updateDiv}</div>
								</c:if>
						</div>
					</div>
				</form:form>
			</div>
		</div>

	</div>
</body>
</html>
<%-- 
<%}%> --%>