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
    <link rel="stylesheet" href="../css/normalize.css">    <link rel="stylesheet" href="../css/skeleton.css"> 
    <script type="text/javascript" src="../js/jquery.js"></script>
	<script type="text/javascript" src="../js/jquery-ui.js"></script>
	<script type="text/javascript" src="../js/reports-form-script.js"></script>
	<link rel="stylesheet" href="../css/jquery-ui.css"/>
		<script>
	function validateForm(){
		if(!document.getElementById('tillDate').value.trim().length && !document.getElementById('fromDate').value.trim().length){
			 alert("Please enter the From Date and Till Date");
		     return false;
		}
		if(!document.getElementById('fromDate').value.trim().length){
	        alert("Please enter the From Date");
	        return false;
	    }
		if(!document.getElementById('tillDate').value.trim().length){
	        alert("Please enter the Till Date");
	        return false;
	    }
		if(!isValidDate()){
			return false;
		}
		return true;
	}
	</script>
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
<body>
	<div class="container">
  
      <div class="row">    
        <h3>Heartfulness Reports Form</h3>
      </div>
      
       <form method="POST" action="generate"  onsubmit="return validateForm()">
          <div class="row">
            <div class="four columns">
              <label for="channel">Enter Channel</label>
              <select class="u-full-width" id="channel" name="channel">
              	<option value="ALL">All Channels</option>
                <c:forEach items="${programChannels}" var="programChannel"> 
				  	<option value="${programChannel.name}">${programChannel.name}</option>
				</c:forEach>
				<!-- <option value="ALL">All Channels</option>
                <option value="Heartfulness">Heartfulness</option>
                <option value="U-Connect">U-Connect</option>
                <option value="C-Connect">C-Connect</option>
                <option value="G-Connect">G-Connect</option>
                <option value="V-Connect">V-Connect</option> -->
              </select>
            </div>
           <div class="four columns">
              <label for="fromDate">From Date<font color="red">*</font></label>
              <input class="width:90%" type="text" placeholder="Enter From Date" id="fromDate" name="fromDate">
            </div>
            <div class="four columns">
              <label for="fromDate">Till Date<font color="red">*</font></label>
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
            <div class="ten columns">
					<p>
						<i>(Report is downloaded into the downloads folder as a zip
							file with name starting with Report_. Zip file will contains the
							report file ,which is a Tab separated file for the selected date
							range, can be opened in MS Excel )</i>
					</p>
				</div>
          </div>
      </form>
   </div>
</body>
</html>