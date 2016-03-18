<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Heartfulness Program Form</title>

<meta name="viewport" content="width=device-width, initial-scale=1">
<link href='//fonts.googleapis.com/css?family=Raleway:400,300,600'
	rel='stylesheet' type='text/css'>
<link rel="stylesheet" href="/pmp/css/jqgridcss/jqx.base.css"
	type="text/css" />
<link rel="stylesheet" href="/pmp/css/jquery-ui.css" />
<script type="text/javascript"
	src="/pmp/js/jqgridjs/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/jqxcore.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/jqxdata.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/jqxbuttons.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/jqxscrollbar.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/jqxmenu.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/jqxcheckbox.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/jqxlistbox.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/jqxdropdownlist.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/jqxgrid.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/jqxgrid.pager.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/jqxgrid.sort.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/jqxgrid.filter.js"></script>
<script type="text/javascript"
	src="/pmp/js/jqgridjs/jqxgrid.columnsresize.js"></script>
<script type="text/javascript"
	src="/pmp/js/jqgridjs/jqxgrid.selection.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/demos.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/generatedata.js"></script>
<script type="text/javascript" src="/pmp/js/reports-form-script.js"></script>
<script type="text/javascript" src="/pmp/js/jquery-ui.js"></script>
<style>
.ui-datepicker {
	font-size: 10px;
	width: 195px;
}

.ui-datepicker-trigger {
	height: 24px;
	margin-bottom: -3px;
	padding-left: 2px;
	/* width: 16px; */
}

.background {
	background-color: #dee6ed;
}

input[type="email"], input[type="number"], input[type="search"], input[type="text"],
	input[type="tel"], input[type="url"], input[type="password"], select {
	height: 30px;
	padding: 6px 10px;
	/* The 6px vertically centers text on FF, ignored by Webkit */
	background-color: #fff;
	border: 1px solid #D1D1D1;
	border-radius: 4px;
	box-shadow: none;
	box-sizing: border-box;
}

textarea {
	height: 40px;
	border-radius: 4px;
	padding: 6px 10px;
	/* The 6px vertically centers text on FF, ignored by Webkit */
}

.button, button, input[type="submit"], input[type="reset"], input[type="button"]
	{
	display: inline-block;
	height: 38px;
	padding: 0 30px;
	color: #555;
	text-align: center;
	font-size: 11px;
	font-weight: 600;
	line-height: 38px;
	letter-spacing: .1rem;
	text-transform: uppercase;
	text-decoration: none;
	white-space: nowrap;
	background-color: transparent;
	border-radius: 4px;
	border: 1px solid #bbb;
	cursor: pointer;
	box-sizing: border-box;
}

.button:hover, button:hover, input[type="submit"]:hover, input[type="reset"]:hover,
	input[type="button"]:hover, .button:focus, button:focus, input[type="submit"]:focus,
	input[type="reset"]:focus, input[type="button"]:focus {
	color: #333;
	border-color: #888;
	outline: 0;
}

.button.button-primary, button.button-primary, input[type="submit"].button-primary,
	input[type="reset"].button-primary, input[type="button"].button-primary
	{
	color: #FFF;
	background-color: #33C3F0;
	border-color: #33C3F0;
}

.button.button-primary:hover, button.button-primary:hover, input[type="submit"].button-primary:hover,
	input[type="reset"].button-primary:hover, input[type="button"].button-primary:hover,
	.button.button-primary:focus, button.button-primary:focus, input[type="submit"].button-primary:focus,
	input[type="reset"].button-primary:focus, input[type="button"].button-primary:focus
	{
	color: #FFF;
	background-color: #1EAEDB;
	border-color: #1EAEDB;
}

.container {
	position: relative;
	width: 100%;
	/* padding-left: 20%; */
	/* padding-left: 20% */
	/* max-width: 960px; */
	/* margin: 0 auto; */
	/*  padding: 0 20px; */
	box-sizing: border-box;
}
</style>
</head>
<body class="background"
	style="min-width: 900px; max-width: 1200px; margin: 0 auto; background-color: #dee6ed">

	<script type="text/javascript">
		$(document).ready(
				function() {
					var encryptedProgramId = '${encryptedProgramId}';
					var url = "/pmp/getParticipantList?programId="
							+ encryptedProgramId;
					var source = {
						type : "POST",
						datatype : "json",
						datafields : [ {
							name : 'printName',
							type : 'string'
						}, {
							name : 'firstName',
							type : 'string'
						}, {
							name : 'middleName',
							type : 'string'
						}, {
							name : 'lastName',
							type : 'string'
						}, {
							name : 'email',
							type : 'string'
						}, {
							name : 'mobilePhone',
							type : 'string'
						}, {
							name : 'gender',
							type : 'string'
						}, {
							name : 'dateOfBirth',
							type : 'date'
						}, {
							name : 'city',
							type : 'string'
						}, {
							name : 'state',
							type : 'string'
						}, {
							name : 'country',
							type : 'string'
						}, {
							name : 'introductionDate',
							type : 'string'
						}, {
							name : 'introducedBy',
							type : 'string'
						}, {
							name : 'welcomeCardNumber',
							type : 'string'
						}, {
							name : 'welcomeCardDate',
							type : 'date'
						}, {
							name : 'firstSittingDate',
							type : 'date'
						}, {
							name : 'secondSittingDate',
							type : 'date'
						}, {
							name : 'thirdSittingDate',
							type : 'date'
						}, {
							name : 'remarks',
							type : 'string'
						} ],
						id : 'printName',
						url : url
					};
					var dataAdapter = new $.jqx.dataAdapter(source);
					// initialize jqxGrid
					$("#jqxgrid").jqxGrid({
						width : 1050,
						height : 400,
						//  sortable: true,
						pageable : true,
						columnsresize : true,
						source : dataAdapter,
						columns : [ {
							text : 'Paricipant Name',
							datafield : 'printName',
							width : 200
						}, {
							text : 'First Name',
							datafield : 'firstName',
							width : 200
						}, {
							text : 'Middle Name',
							datafield : 'middleName',
							width : 200
						}, {
							text : 'Last Name',
							datafield : 'lastName',
							width : 200,
							cellsalign : 'left'
						}, {
							text : 'Email',
							datafield : 'email',
							width : 200,
							cellsalign : 'left',
							cellsformat : 'c2'
						}, {
							text : 'Mobile No.',
							datafield : 'mobilePhone',
							width : 200,
							cellsalign : 'left',
							cellsformat : 'c2'
						}, {
							text : 'Gender',
							datafield : 'gender',
							width : 200,
							cellsalign : 'left',
							cellsformat : 'c2'
						}, {
							text : 'Date of Birth',
							datafield : 'dateOfBirth',
							width : 200,
							cellsalign : 'left',
						}, {
							text : 'City',
							datafield : 'city',
							width : 200,
							cellsalign : 'left',
							cellsformat : 'c2'
						}, {
							text : 'State',
							datafield : 'state',
							width : 200,
							cellsalign : 'left',
							cellsformat : 'c2'
						}, {
							text : 'Country',
							datafield : 'country',
							width : 200,
							cellsalign : 'left',
							cellsformat : 'c2'
						}, {
							text : 'Introduction Date',
							datafield : 'introductionDate',
							width : 200,
							cellsalign : 'left',
							cellsformat : 'c2'
						}, {
							text : 'Introduced By',
							datafield : 'introducedBy',
							width : 200,
							cellsalign : 'left',
							cellsformat : 'c2'
						}, {
							text : 'Welcome Card No',
							datafield : 'welcomeCardNumber',
							width : 200,
							cellsalign : 'left',
							cellsformat : 'c2'
						}, {
							text : 'Welcome Card Date',
							datafield : 'welcomeCardDate',
							width : 200,
							cellsalign : 'left'
						}, {
							text : 'First Sitting Date',
							datafield : 'firstSittingDate',
							width : 200,
							cellsalign : 'left'
						}, {
							text : 'Second Sitting Date',
							datafield : 'secondSittingDate',
							width : 200,
							cellsalign : 'left'
						}, {
							text : 'Third Sitting Date ',
							datafield : 'thirdSittingDate',
							width : 200,
							cellsalign : 'left'
						}, {
							text : 'Remarks',
							datafield : 'remarks',
							width : 200,
							cellsalign : 'left',
							cellsformat : 'c2'
						}, ]
					});

				});
	</script>

	<div class="container" align="center">
		<!-- style="width:100%;height:100%;margin:0 auto; ">-->

		<div class="row" style="">
			<h1 class="row">Heartfulness Event</h1>
		</div>
		<form:form method="POST" action="createEvent" modelAttribute="program"
			id="programForm">
			<table >
				<tr>
					<td align="right" colspan="2" style="margin: 0 auto; float: right; padding-left: 75%"><input class="button-primary"
						type="submit" value="Save changes" /> <form:input
							path="encryptedId" type="hidden" id="encryptedId"
							name="encryptedId" value="${encryptedProgramId}" /> <form:hidden
							path="autoGeneratedEventId" /> <form:hidden
							path="autoGeneratedIntroId" /></td>
				</tr>
			</table>
			<table>
				<tr>
					<td colspan="2">
						<fieldset>
							<legend align="left" style="font-weight: bold;">Program
								Details</legend>
							<table id="programtab" width="100%">
								<tr>
									<td width="15%;"><label for="channel">Name</label></td>
									<td colspan="3"><form:input style="width: 100%"
											type="text" placeholder="Program Name" id="programChannel"
											path="programChannel" name="programChannel" /></td>
								</tr>
								<tr>
									<td colspan="4"><form:errors cssStyle="color: red;"
											path="programChannel" /></td>
								</tr>
								<tr>
									<td><label for="fromDate">Start Date</label><span
										style="color: #ff6666">*</span></td>
									<td><form:input style="min-width:55%;max-width:85%;"
											type="text" placeholder="Program Start Date" id="fromDate"
											path="programStartDate" name="fromDate" /></td>
									<td width="15%"><label for="tillDate">End Date</label></td>
									<td><form:input style="min-width:55%;max-width:85%;"
											type="text" placeholder=" Program End Date" id="tillDate"
											path="programEndDate" name="tillDate" /></td>
								</tr>
								<tr>
									<td colspan="2"><form:errors cssStyle="color: red;"
											path="programStartDate" /></td>
									<td colspan="2"><form:errors cssStyle="color: red;"
											path="programEndDate" /></td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td>
						<fieldset>
							<legend align="left" style="font-weight: bold;">Venue</legend>
							<table id="venuetab" width="100%">
								<tr>
									<td width="32%"><label for="eventPlace">Place</label><span
										style="color: #ff6666">*</span></td>
									<td><form:input style="width:100%" type="text"
											placeholder=" Event Place" id="eventPlace" path="eventPlace" /></td>
								</tr>
								<tr>
									<td colspan="2"><form:errors cssStyle="color: red;"
											path="eventPlace" /></td>
								</tr>
								<tr>
									<td><label for="eventCountry">Country</label><span
										style="color: #ff6666">*</span></td>
									<td><form:input style="width:100%" type="text"
											placeholder=" Event Country" id="eventCountry"
											path="eventCountry" /></td>
										<%-- 	<td><label for="eventCountry">Country</label><span
										style="color: #ff6666">*</span></td>
										<td><form:select style="width:100%"  placeholder=" Event Country" id="eventCountry"
											path="eventCountry">
											  <option value="ALL">All Countries</option>
	               								 <c:forEach items="${eventCountries}" var="countryElement"> 
					  							<option value="${countryElement}">${countryElement}</option>
											</c:forEach>
											</form:select></td> --%>
	            
								</tr>
								<tr>
									<td colspan="2"><form:errors cssStyle="color: red;"
											path="eventCountry" /></td>
								</tr>
								<tr>
									<td><label for="eventState">State</label><span
										style="color: #ff6666">*</span></td>
									<td><form:input style="width:100%" type="text"
											placeholder=" Event State" id="eventState" path="eventState" /></td>
								</tr>
								<tr>
									<td colspan="2"><form:errors cssStyle="color: red;"
											path="eventState" /></td>
								</tr>
								<tr>
									<td><label for="eventCity">City</label><span
										style="color: #ff6666">*</span></td>
									<td><form:input style="width:100%" type="text"
											placeholder=" Event City" id="eventCity" path="eventCity" /></td>
								</tr>
								<tr>
									<td colspan="2"><form:errors cssStyle="color: red;"
											path="eventCity" /></td>
								</tr>
							</table>
						</fieldset>
					</td>
					<td width="50%">
						<fieldset>
							<legend align="top" style="font-weight: bold;">Co-ordinator</legend>
							<table id="co-ordinatortab" width="100%">
								<tr>
									<td width="27%"><label for="coordinatorName">Name</label>
										<span style="color: #ff6666">*</span></td>
									<td><form:input style="width:100%" type="text"
											placeholder=" Coordinator Name" id="coordinatorName"
											path="coordinatorName" /></td>
								</tr>
								<tr>
									<td colspan="2"><form:errors cssStyle="color: red;"
											path="coordinatorName" /></td>
								</tr>
								<tr>
									<td><label for="coordinatorEmail">Email</label> <span
										style="color: #ff6666">*</span></td>
									<td><form:input style="width:100%" type="text"
											placeholder=" Coordinator Email" id="coordinatorEmail"
											path="coordinatorEmail" /></td>
								</tr>
								<tr>
									<td colspan="2"><form:errors cssStyle="color: red;"
											path="coordinatorEmail" /></td>
								</tr>
								<tr>
									<td><label for="coordinatorMobile">Mobile</label> <span
										style="color: #ff6666">*</span></td>
									<td><form:input style="width:100%" type="text"
											placeholder=" Coordinator Mobile" id="coordinatorMobile"
											path="coordinatorMobile" /></td>
								</tr>
								<tr>
									<td colspan="2"><form:errors cssStyle="color: red;"
											path="coordinatorMobile" /></td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<fieldset>
							<legend align="left" style="font-weight: bold;">Organization</legend>
							<table width="100%">
								<tr>
									<td>
										<table width="100%">
											<tr>
												<td width="31%"><label for="organizationName">Name</label>
													<span style="color: #ff6666">*</span></td>
												<td><form:input style="width:100%" type="text"
														placeholder=" Organization Name" id="organizationName"
														path="organizationName" /></td>
											</tr>
											<tr>
												<td colspan="2"><form:errors cssStyle="color: red;"
														path="organizationName" /></td>
											</tr>
											<tr>
												<td><label for="organizationWebSite">Web Site</label></td>
												<td><form:input style="width:100%" type="text"
														placeholder=" Organization Web Site"
														id="organizationWebSite" path="organizationWebSite" /></td>
											</tr>
											<tr>
												<td colspan="2"><form:errors cssStyle="color: red;"
														path="organizationWebSite" /></td>
											</tr>
											<tr>
												<td><label for="organizationContactName">
														Contact Name</label> <span style="color: #ff6666">*</span></td>
												<td><form:input style="width:100%" type="text"
														placeholder=" Organization Contact Name"
														id="organizationContactName"
														path="organizationContactName" /></td>
											</tr>
											<tr>
												<td colspan="2"><form:errors cssStyle="color: red;"
														path="organizationContactName" /></td>
											</tr>
										</table>
									</td>
									<td>
										<table width="100%">
											<tr>
												<td width="31%"><label for="organizationDepartment">Department</label></td>
												<td><form:input style="width:100%" type="text"
														placeholder=" Organization Department"
														id="organizationDepartment" path="organizationDepartment" /></td>
											</tr>
											<tr>
												<td colspan="2"><form:errors cssStyle="color: red;"
														path="organizationDepartment" /></td>
											</tr>
											<tr>
												<td><label for="organizationContactMobile">Contact
														Mobile</label><span style="color: #ff6666">*</span></td>
												<td><form:input style="width:100%" type="text"
														placeholder=" Organization Contact Mobile"
														id="organizationContactMobile"
														path="organizationContactMobile" /></td>
											</tr>
											<tr>
												<td colspan="2"><form:errors cssStyle="color: red;"
														path="organizationContactMobile" /></td>
											</tr>
											<tr>
												<td><label for="organizationContactEmail">Contact
														Email</label> <span style="color: #ff6666">*</span></td>
												<td><form:input style="width:100%" type="text"
														placeholder="Organization Contact Email"
														id="organizationContactEmail"
														path="organizationContactEmail" /></td>
											</tr>
											<tr>
												<td colspan="2"><form:errors cssStyle="color: red;"
														path="organizationContactEmail" /></td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td>
						<fieldset>
							<legend align="left" style="font-weight: bold;">Preceptor</legend>
							<table id="preceptortab" width="100%">
								<tr>
									<td width="32%"><label for="preceptorName">Name</label><span
										style="color: #ff6666">*</span></td>
									<td><form:input style="width:100%" type="text"
											placeholder=" Preceptor Name" id="preceptorName"
											path="preceptorName" /></td>
								</tr>
								<tr>
									<td colspan="2"><form:errors cssStyle="color: red;"
											path="preceptorName" /></td>
								</tr>
								<tr>
									<td><label for="preceptorIdCardNumber">ID Card No</label><span
										style="color: #ff6666">*</span></td>
									<td><form:input style="width:100%" type="text"
											placeholder=" Preceptor Id Card Number"
											id="preceptorIdCardNumber" path="preceptorIdCardNumber" /></td>
								</tr>
								<tr>
									<td colspan="2"><form:errors cssStyle="color: red;"
											path="preceptorIdCardNumber" /></td>
								</tr>
							</table>
						</fieldset>
					</td>
					<td>
						<fieldset>
							<legend align="left" style="font-weight: bold;">Welcome
								Card Signer</legend>
							<table id="preceptortab" width="100%">
								<tr>
									<td width="27%"><label for="welcomeCardSignedByName">Name</label></td>
									<td><form:input style="width:100%" type="text"
											placeholder="Welcome Card Signed By Name"
											id="welcomeCardSignedByName" path="welcomeCardSignedByName" /></td>
								</tr>
								<tr>
									<td colspan="2"><form:errors cssStyle="color: red;"
											path="welcomeCardSignedByName" /></td>
								</tr>
								<tr>
									<td><label for="welcomeCardSignerIdCardNumber">ID
											Card No</label></td>
									<td><form:input style="width: 100%" type="text"
											placeholder="Welcome Card Signer Id Card No."
											id="welcomeCardSignerIdCardNumber"
											path="welcomeCardSignerIdCardNumber" /></td>
								</tr>
								<tr>
									<td colspan="2"><form:errors cssStyle="color: red;"
											path="welcomeCardSignerIdCardNumber" /></td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<div>
							<table>
								<tr>
									<td><label for="remarks">Remarks</label></td>
									<td><form:textarea path="remarks" style="width:100%"
											id="remarks" cols="100%" /></td>
								</tr>
								<tr>
									<td colspan="2"><form:errors cssStyle="color: red;"
											path="remarks" /></td>
								</tr>
							</table>
						</div>
					</td>
				</tr>
			</table>
		</form:form>
		<c:if test="${not empty participantList}">
			<!-- <div id='jqxWidget' style="padding-top: 1.5%;"> -->
			<div id="jqxgrid"></div>
			<!--  </div> -->
		</c:if>
	</div>
</body>
</html>