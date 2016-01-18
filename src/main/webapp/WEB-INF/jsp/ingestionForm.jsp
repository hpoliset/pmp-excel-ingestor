<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Heartfulness Data Upload</title>
</head>
<body>
<div align="center" >
	<div class="row">
	<h1>Heartfulness Event Data Upload Screen</h1>
	</div>
    <form method="POST" action="processUpload" enctype="multipart/form-data">
        <table border="0">
            <tr>
                <td class="row">Pick file #1:</td>
                <td><input type="file" name="excelDataFile" size="50" class="row"/></td>
            </tr>
            <tr>
                <td colspan="2" align="center"><input type="submit" class="button-primary" value="Process Data File" /></td>
            </tr>
        </table>
    </form>
</div>
</body>
</html>