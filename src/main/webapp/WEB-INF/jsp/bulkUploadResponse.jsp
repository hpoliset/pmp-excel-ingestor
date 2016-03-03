<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Upload Done</title>
<link rel="stylesheet" href="/pmp/css/normalize.css">
<link rel="stylesheet" href="/pmp/css/skeleton.css">
</head>
<body>
	<div class="container">

		<h3>Heartfulness Data Upload Results</h3>

		<table class="u-full-width">
			<c:if test="${uploadReponse!=null}">
				<tr>
					<th align="left">Excel Name</th>
					<th align="left">Excel version</th>
					<th align="left">Status</th>
				</tr>
			</c:if>
			<c:forEach items="${uploadReponse}" var="excelInfo">
				<tr>
					<td><b><c:out value="${excelInfo.fileName}" /></b> <c:forEach
							items="${excelInfo.errorMsg}" var="errorList">
							<label style="color: maroon; font-size: 13px;">&nbsp;&nbsp;&nbsp;<c:out
									value="${errorList}"></c:out></label>
						</c:forEach></td>
					<td><c:out value="${excelInfo.excelVersion}" /></td>

					<c:choose>
						<c:when test="${excelInfo.status=='Success'}">
							<td style="color: green"><c:out value="${excelInfo.status}" /></td>

						</c:when>
						<c:otherwise>
							<td style="color: red"
								onclick="showErrorMsg('${excelInfo.fileName}','${excelInfo.errorMsg}')">
								<c:out value="${excelInfo.status}" />
							</td>
						</c:otherwise>

					</c:choose>
				</tr>
			</c:forEach>
		</table>
		<a class="button" href="bulkUploadForm">Upload Excels</a>
	</div>
</body>
</html>