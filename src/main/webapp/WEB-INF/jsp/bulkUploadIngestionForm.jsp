<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Heartfulness Data Upload</title>
<script src="../js/jquery.js"></script>
<link rel="stylesheet" href="../css/normalize.css">
<link rel="stylesheet" href="../css/skeleton.css">
</head>
<body>
	<form method="POST" action="processBulkUpload"
		enctype="multipart/form-data">

		<div class="container" align="center" style="width: 100%; height: 100%; margin: 0 auto;">
			<h3>Heartfulness Event Data Upload Screen</h3>
			<div class="six columns">
				<input id="fileupload" type="file" name="uploadedExcelFiles" multiple>
			</div>
			<div class="six columns">
				<input type="submit" id="process" value="Start uploading excels"  />
			</div>
			<br>
			<div id="table-view" style="display: none;">
			<br>
			
				<table class="u-full-width" id="uploaded-files">
				</table>
			</div>
			</div>
	</form>
</body>
<script type="text/javascript">
	$('#fileupload').bind('change', function() {
		$('#table-view').show();
		$('#uploaded-files').empty();
		$('#uploaded-files').append("<tr><th align='left'>File Name</th><th align='left'>File Size</th></tr>");
		var files = $('#fileupload')[0].files
		for (var i = 0, file; file = files[i]; i++) {
			var tr = "<tr><td>"+file.name;
			if(file.size > 10000000){
				tr = tr+"<br><lable style='color: red;'>File is too large";
				//$('#fileupload').remove(i);
			}
			var ext = file.name.split('.').pop().toLowerCase();
			if($.inArray(ext, ['xlsx','xlsm']) == -1) {
			    tr = tr+"<br><lable style='color: red;'>File type not allowed";
			   // $('#fileupload').remove(file);
			   // $('#fileupload')[0].splice(file);
			}
			tr = tr+"</td><td>"+Math.round((file.size / 1024) * 100) / 100 + " Kb</td></tr>";
			$('#uploaded-files').append(tr);
		}
	});
</script>


</html>