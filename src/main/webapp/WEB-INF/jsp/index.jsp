<!DOCTYPE html>
<%
	if (session.getAttribute("AuthenticationResponse") == null) {
%><jsp:forward page="Home.jsp" />
<%
	} else {
%>
<html >
<head>
<title>Heartfulness</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" href="/pmp/css/new/style.css" type="text/css" media="screen, projection, tv" />
<link rel="stylesheet" href="/pmp/css/new/style-print.css" type="text/css" media="print" />
</head>
<body style="overflow:hidden; margin:0;background-color:#fffff;">
<div id="wrapper" align="center">
  <div class="title">
    <div class="title-top">
      <div class="title-left">
        <div class="title-right">
          <div class="title-bottom">
            <div class="title-top-left">
              <div class="title-bottom-left">
                <div class="title-top-right">
                  <div class="title-bottom-right">
                    <h1 style="background-image: url(images/logo1.jpg);background-position: left center;background-repeat: no-repeat;padding-left: 110px; " > Heartfulness </h1>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <hr class="noscreen" />
  <div class="content">
    <div class="column-left">
      <h3>MENU</h3>
      <a href="#skip-menu" class="hidden">Skip menu</a>
      <ul class="menu">
      	<li><a href="/pmp/profile" target="mainFrame" >Update Profile</a></li>
        <li><a href="/pmp/ingest/inputForm" target="mainFrame">Single File Uploader</a></li>
        <li><a href="/pmp/ingest/bulkUploadForm" target="mainFrame">Bulk Uploader</a></li>
        <li class=""><a href="/pmp/reports/reportsForm" target="mainFrame">Reports</a></li>
        <li class="last"><a href="/pmp/signout" >Signout</a></li>
      </ul>
    </div>
    <div id="skip-menu"></div>
    <div class="column-right">
      <div class="box">
        <div class="box-top"></div>
        <div class="box-in">
         <!--  <h2>Welcome to my website</h2> -->
          
          	<!-- <div id="menu-content" class="container" style="padding-left: 50px;padding-top: 0px"> -->
					<iframe  class="box-in" src="/pmp/profile" id="mainFrame" frameborder="0" name="mainFrame"></iframe>
			<!-- </div> -->
        </div>
      </div>
    </div>
    <div class="cleaner">&nbsp;</div>
  </div>
</div>
</html>

<%}%>