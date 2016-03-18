<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<link rel="stylesheet" href="/pmp/css/normalize.css">
<link rel="stylesheet" href="/pmp/css/skeleton.css">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Heartfulness Data Upload</title>
</head>
<body style="background-color: #dee6ed; overflow: hidden;">
<div class="container" align="center" style="margin: 0 auto; padding-top: 10px">
			<h3 >Heartfulness Event Data Upload Screen</h3>
    <form method="POST" action="processUpload" enctype="multipart/form-data">
        <table border="0" style="padding-top: 20px">
            <tr>
               <!--  <td style="padding-left: 100px">Pick file #1:</td> -->
                <td style="padding-left: 100px"><input type="file" name="excelDataFile" size="50" /></td>
            </tr>
           <%-- <tr>
                <td>Pick file #2:</td>
                <td><input type="file" name="excelDataFile" size="50" /></td>
            </tr>--%>
            <tr>
                <td  style="padding-left: 100px" colspan="3" align="center"><input style="padding-left: 30px" type="submit" class="button-primary" value="Process Data File" /></td>
            </tr>
        </table>
    </form>
</div>
</body>
</html>