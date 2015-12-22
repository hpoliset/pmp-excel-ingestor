<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Heartfulness Reports</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href='//fonts.googleapis.com/css?family=Raleway:400,300,600' rel='stylesheet' type='text/css'>
    <link rel="stylesheet" href="../css/normalize.css">    <link rel="stylesheet" href="../css/skeleton.css">    
</head>

<body>
	<div class="container">
  
      <div class="row">    
        <h3>Heartfulness Reports Form</h3>
      </div>
      
      <form method="POST" action="generate">
          <div class="row">
            <div class="four columns">
              <label for="channel">Enter Channel</label>
              <select class="u-full-width" id="channel" name="channel">
                <option value="Heartfulness">Heartfulness</option>
                <option value="U-Connect">U-Connect</option>
                <option value="C-Connect">C-Connect</option>
                <option value="G-Connect">G-Connect</option>
                <option value="V-Connect">V-Connect</option>
                <option value="ALL">All Channels</option>
              </select>
            </div>
            <div class="four columns">
              <label for="fromDate">From Date</label>
              <input class="u-full-width" type="text" placeholder="Enter From Date" id="fromDate" name="fromDate">
            </div>
            <div class="four columns">
              <label for="fromDate">Till Date</label>
              <input class="u-full-width" type="text" placeholder="Enter Till Date" id="tillDate" name="tillDate">
            </div>
          </div>

          <div class="row">
            <div class="four columns">
              <label for="city">Enter City</label>
              <input class="u-full-width" type="text" placeholder="Enter City" id="city" name="city">
            </div>
            <div class="four columns">
              <label for="fromDate">Enter State</label>
              <input class="u-full-width" type="text" placeholder="Enter State" id="state" name="state">
            </div>
            <div class="four columns">
              <label for="fromDate">Enter County</label>
              <input class="u-full-width" type="text" placeholder="Enter Country" id="country" name="country">
            </div>
          </div>

          <div class="row">
            <div class="six columns">          
				<input class="button-primary" type="submit" value="Download Report" />
            </div>
          </div>
          <div class="row">
            <div class="six columns">
				<p><i>(Report is tab separated file. currently available only by channel)</i></p>
            </div>
          </div>
      </form>
   </div>
</body>
</html>