<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Heartfulness Events Form</title>
<!-- <meta name="viewport" content="width=device-width, initial-scale=1">
<link href='//fonts.googleapis.com/css?family=Raleway:400,300,600'
	rel='stylesheet' type='text/css'>
<link rel="stylesheet" href="/pmp/css/normalize.css">
<link rel="stylesheet" href="/pmp/css/skeleton.css">
<link rel="stylesheet" href="/pmp/css/responsetable.css">
<script type="text/javascript" src="/pmp/js/jquery.js"></script>
<script type="text/javascript" src="/pmp/js/jquery-ui.js"></script>
<script type="text/javascript" src="/pmp/js/reports-form-script.js"></script> -->
<meta name="viewport" content="width=device-width, initial-scale=1">
<link href='//fonts.googleapis.com/css?family=Raleway:400,300,600'
	rel='stylesheet' type='text/css'>
<link rel="stylesheet" href="/pmp/css/normalize.css">
<link rel="stylesheet" href="/pmp/css/skeleton.css">
<script type="text/javascript" src="/pmp/js/jquery.js"></script>
<script type="text/javascript" src="/pmp/js/jquery-ui.js"></script>
<script type="text/javascript" src="/pmp/js/reports-form-script.js"></script>
<link rel="stylesheet" href="/pmp/css/jquery-ui.css" />
<style>
div.ui-datepicker {
	font-size: 10px;
	width: 195px;
}

.ui-datepicker-trigger {
	height: 24px;
	margin-bottom: -3px;
	padding-left: 2px;
	/* width: 16px; */
}
</style>

</head>
<body style="background-color: #dee6ed;">

	<div class="container" align="center"
		style="padding-top: 20px; padding-left: 60px;">

		<div class="row">
			<h3>Heartfulness Events Form</h3>
		</div>

		<form:form method="POST" action="createEvent" modelAttribute="program"
			id="programForm">
			<%-- <div>
			<c:if test="${eventUpdate!=null}">
					<div align="right"
						style="font-style: italic; color: green; font-size: large;"
						id="eventUpdate">${eventUpdate}</div>
				</c:if>
			</div> --%>
			<div class="row">

				<div class="four columns">

					<label for="channel">Program Channel </label>
					<form:input class="u-full-width" type="text"
						placeholder="Enter Program Channel" id="programChannel"
						path="programChannel" name="programChannel" />
				</div>

				<div class="four columns">
					<label for="fromDate">Program Start Date*</label>
					<form:input class="" type="text" cssStyle="width: 90%"
						placeholder="Enter Program Start Date" id="fromDate"
						path="programStartDate" name="fromDate" />
				</div>

				<div class="four columns">
					<label for="tillDate">Program End Date</label>
					<form:input class="" type="text" cssStyle="width: 90%"
						placeholder="Enter Program End Date" id="tillDate"
						path="programEndDate" name="tillDate" />
				</div>

			</div>

			<div class="row">

				<div class="four columns">
					<label for="eventPlace">Event Place*</label>
					<form:input class="u-full-width" type="text"
						placeholder="Enter Event Place" id="eventPlace" path="eventPlace" />
				</div>

				<div class="four columns">
					<label for="eventCity">Event City*</label>
					<form:input class="u-full-width" type="text"
						placeholder="Enter Event City" id="eventCity" path="eventCity" />
				</div>

				<div class="four columns">
					<label for="eventCountry">Event Country*</label>
					<form:input class="u-full-width" type="text"
						placeholder="Enter Event Country" id="eventCountry"
						path="eventCountry" />
				</div>

			</div>

			<div class="row">

				<div class="four columns">
					<label for="eventState">Event State*</label>
					<form:input class="u-full-width" type="text"
						placeholder="Enter Event State" id="eventState" path="eventState" />
				</div>

				<div class="four columns">
					<label for="coordinatorName">Coordinator Name*</label>
					<%-- <form:select class="u-full-width" id="coordinatorName"
						name="coordinatorName" path="coordinatorName">
						<c:forEach items="${coordinatorList}" var="coordinators">
							<option value="${coordinators.getEmail()}">${coordinators.getName()}</option>
						</c:forEach> 
					</form:select> --%>
					<form:input class="u-full-width" type="text"
						placeholder="Enter Coordinator Name" id="coordinatorName"
						path="coordinatorName" />
				</div>

				<div class="four columns">
					<label for="coordinatorMobile">Coordinator Mobile*</label>
					<form:input class="u-full-width" type="text"
						placeholder="Enter Coordinator Mobile" id="coordinatorMobile"
						path="coordinatorMobile" />
				</div>

			</div>

			<div class="row">

				<div class="four columns">
					<label for="coordinatorEmail">Coordinator Email*</label>
					<%-- <form:input class="u-full-width" type="text"
						placeholder="Enter Coordinator Email" id="coordinatorEmail"
						path="coordinatorEmail" readonly="true"  cssStyle="cursor:not-allowed;background-color: #eee;opacity: 1;"/> --%>
					<form:input class="u-full-width" type="text"
						placeholder="Enter Coordinator Email" id="coordinatorEmail"
						path="coordinatorEmail" />
				</div>

				<div class="four columns">
					<label for="organizationName">Organization Name*</label>
					<form:input class="u-full-width" type="text"
						placeholder="Enter Organization Name" id="organizationName"
						path="organizationName" />
				</div>

				<div class="four columns">
					<label for="organizationContactName">Organization Contact
						Name*</label>
					<form:input class="u-full-width" type="text"
						placeholder="Enter Organization Contact Name"
						id="organizationContactName" path="organizationContactName" />
				</div>

			</div>

			<div class="row">
				<div class="four columns">
					<label for="organizationWebSite">Organization Web Site</label>
					<form:input class="u-full-width" type="text"
						placeholder="Enter Organization Web Site" id="organizationWebSite"
						path="organizationWebSite" />
				</div>
				<div class="four columns">
					<label for="organizationContactEmail">Organization Contact
						Email*</label>
					<form:input class="u-full-width" type="text"
						placeholder="Enter Organization Contact Email"
						id="organizationContactEmail" path="organizationContactEmail" />
				</div>

				<div class="four columns">
					<label for="organizationDepartment">Organization Department</label>
					<form:input class="u-full-width" type="text"
						placeholder="Enter Organization Department"
						id="organizationDepartment" path="organizationDepartment" />
				</div>

			</div>
			<div class="row">

				<div class="four columns">
					<label for="organizationContactMobile">Organization Contact
						Mobile*</label>
					<form:input class="u-full-width" type="text"
						placeholder="Enter Organization Contact Mobile"
						id="organizationContactMobile" path="organizationContactMobile" />
				</div>

				<div class="four columns">
					<label for="preceptorName">Preceptor Name*</label>
					<form:input class="u-full-width" type="text"
						placeholder="Enter Preceptor Name" id="preceptorName"
						path="preceptorName" />
				</div>

				<div class="four columns">
					<label for="preceptorIdCardNumber">Preceptor Id Card
						Number*</label>
					<form:input class="u-full-width" type="text"
						placeholder="Enter Preceptor Id Card Number"
						id="preceptorIdCardNumber" path="preceptorIdCardNumber" />
				</div>

			</div>

			<div class="row">

				<div class="four columns">
					<label for="welcomeCardSignedByName">Welcome Card Signed By
						Name</label>
					<form:input class="u-full-width" type="text"
						placeholder="Welcome Card Signed By Name"
						id="welcomeCardSignedByName" path="welcomeCardSignedByName" />
				</div>

				<div class="four columns">
					<label for="welcomeCardSignerIdCardNumber">Welcome Card
						Signer's Id Card No.</label>
					<form:input class="u-full-width" type="text"
						placeholder="Welcome Card Signer Id Card No."
						id="welcomeCardSignerIdCardNumber"
						path="welcomeCardSignerIdCardNumber" />
				</div>

				<div class="four columns">
					<label for="remarks">Remarks</label>
					<form:textarea path="remarks" class="u-full-width" id="remarks"
						cssStyle="resize: none" />
				</div>

			</div>

			<div class="row">

				<div class="six columns">
					<input class="button-primary" type="submit" value="Save changes" />
				</div>

				<div class="six columns" style="color: green;">${result}</div>

			</div>
			<div style="display: none">
				<form:hidden id="programId" path="programId" />
				<form:hidden path="programChannelId" />
			</div>
		</form:form>



		<c:if test="${participantList != null}">
			<div style="overflow: scroll; overflow-y: scroll; height: 400px;">
				<table class="responstable">
					<!-- here should go some titles... -->
					<tr>
						<th>Paricipant Name</th>
						<th>First Name</th>
						<th>Middle Name</th>
						<th>Last Name</th>
						<th>Email</th>
						<th>Mobile No.</th>
						<th>Gender</th>
						<th>Date of Birth</th>
						<th>City</th>
						<th>State</th>
						<th>Country</th>
						<th>Introduction Date</th>
						<th>Introduced By</th>
						<th>Welcome Card No</th>
						<th>Welcome Card Date</th>
						<th>First Sitting Date</th>
						<th>Second Sitting Date</th>
						<th>Third Sitting Date</th>
						<th>Remarks</th>
					</tr>
					<c:forEach items="${participantList}" var="partcipant">
						<tr>
							<td><c:out value="${partcipant.getPrintName()}" /></td>
							<td><c:out value="${partcipant.getFirstName()}" /></td>
							<td><c:out value="${partcipant.getMiddleName()}" /></td>
							<td><c:out value="${partcipant.getLastName()}" /></td>
							<td><c:out value="${partcipant.getEmail()}" /></td>
							<td><c:out value="${partcipant.getMobilePhone()}" /></td>
							<td><c:out value="${partcipant.getGender()}" /></td>
							<td><c:out value="${partcipant.getDateOfBirth()}" /></td>
							<td><c:out value="${partcipant.getCity()}" /></td>
							<td><c:out value="${partcipant.getState()}" /></td>
							<td><c:out value="${partcipant.getCountry()}" /></td>
							<td><c:out value="${partcipant.getIntroductionDate()}" /></td>
							<td><c:out value="${partcipant.getIntroducedBy()}" /></td>
							<td><c:out value="${partcipant.getWelcomeCardNumber()}" /></td>
							<td><c:out value="${partcipant.getWelcomeCardDate()}" /></td>
							<td><c:out value="${partcipant.getFirstSittingDate()}" /></td>
							<td><c:out value="${partcipant.getSecondSittingDate()}" /></td>
							<td><c:out value="${partcipant.getThirdSittingDate()}" /></td>
							<td><c:out value="${partcipant.getRemarks()}" /></td>

						</tr>
					</c:forEach>
				</table>
			</div>
		</c:if>

	</div>
	<%-- <c:if test="${participantList != null}">
		<div align="left">
			<table id="jqGrid"></table>
			<div id="jqGridPager"></div>
		</div>
   </c:if> --%>

</body>
</html>