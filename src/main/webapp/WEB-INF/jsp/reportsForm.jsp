<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Heartfulness Reports</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href='//fonts.googleapis.com/css?family=Raleway:400,300,600' rel='stylesheet' type='text/css'>
    <link rel="stylesheet" href="/pmp/css/normalize.css">    <link rel="stylesheet" href="/pmp/css/skeleton.css"> 
    <script type="text/javascript" src="/pmp/js/jquery.js"></script>
	<script type="text/javascript" src="/pmp/js/jquery-ui.js"></script>
	<script type="text/javascript" src="/pmp/js/reports-form-script.js"></script>
	<link rel="stylesheet" href="/pmp/css/jquery-ui.css"/>
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
<body style="background-color: #dee6ed; overflow: hidden;">
	<!-- <div class="container"  align="center" style="padding-top: 20px">
  
      <div class="row">    
       <b><h3>Heartfulness Reports Form</h3></b> 
      </div> -->
      <div class="container" align="center" style="padding-top: 20px; margin: 0 auto;">
			<h3 align="center">Heartfulness Reports Form</h3>
      
      <form method="POST" action="generate">
          <div class="row">
            <div class="four columns">
              <label for="channel">Enter Channel</label>
              <select class="u-full-width" id="channel" name="channel">
                <%-- <c:forEach items="${eventTypes}" var="eventType"> 
				  	<option value="${eventType}">${eventType}</option>
				</c:forEach> --%>
				<option value="ALL">All Channels</option>
                <option value="Heartfulness">Heartfulness</option>
                <option value="U-Connect">U-Connect</option>
                <option value="C-Connect">C-Connect</option>
                <option value="G-Connect">G-Connect</option>
                <option value="V-Connect">V-Connect</option>
              </select>
            </div>
            <div class="four columns">
              <label for="fromDate">From Date</label>
              <input class="width:90%" type="text" placeholder="Enter From Date" id="fromDate" name="fromDate">
            </div>
            <div class="four columns">
              <label for="fromDate">Till Date</label>
              <input class="width:90%" type="text" placeholder="Enter Till Date" id="tillDate" name="tillDate">
            </div>
          </div>

          <div class="row">
	          <div class="four columns">
	              <label for="fromDate">Enter County</label>
	              <!-- <input class="u-full-width" type="text" placeholder="Enter Country" id="country" name="country"> -->
	              <select class="u-full-width" id="country" name="country">
	                <option value="ALL">All Countries</option>
	                <c:forEach items="${eventCountries}" var="countryElement"> 
					  	<option value="${countryElement}">${countryElement}</option>
					</c:forEach>
	               </select>
	            </div>
	            <div class="four columns">
	              <label for="fromDate">Enter State</label>
	              <!-- <input class="u-full-width" type="text" placeholder="Enter State" id="state" name="state"> -->
	              <select class="u-full-width" id="state" name="state">
	                <option value="ALL">All States</option>
	               </select>
	            </div>
	            <div class="four columns">
	              <label for="city">Enter City</label>
	              <input class="u-full-width" type="text" placeholder="Enter City" id="city" name="city">
	            </div>
          </div>
          <div class="row">
            <div class="six columns">          
				<input class="button-primary" type="submit" value="Download Report" />
            </div>
          </div>
          <div class="row">
            <div class="six columns">
				<p><i>(Report is downloaded into the downloads folder with name starting as Report_.The report file is a Tab separated file, can be opened in MS Excel )</i></p>
            </div>
          </div>
      </form>
   </div>
</body>
</html>