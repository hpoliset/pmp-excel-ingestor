<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>SRCM Login</title>
</head>
<body>
	login page
	 	 <%
	        String redirectURL = "http://profile.srcm.net/o/authorize?state=my_code_to_handle_return&response_type=code&client_id=RoFUqYpsTM3eCAI0177E89094PMrL7oH1KbDx50E&redirect_uri=http://pmpbeta.heartfulness.org/pmp/index";
        response.sendRedirect(redirectURL);
        
        %>  
</body>
</html>