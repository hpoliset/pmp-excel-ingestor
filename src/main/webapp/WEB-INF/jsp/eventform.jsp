<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/ecmascript" src="/pmp/js/jquery-1.11.0.min.js"></script>
<script type="text/ecmascript" src="/pmp/js/grid.locale-en.js"></script>
<script type="text/ecmascript" src="/pmp/js/jquery.jqGrid.min.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="/pmp/css/jquery-ui.css" />
<link rel="stylesheet" type="text/css" media="screen" href="/pmp/css/ui.jqgrid.css" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Heartfulness Event List</title>
<link rel="stylesheet" href="/pmp/css/skeleton.css">
<link rel="stylesheet" href="/pmp/css/jqgridcss/jqx.base.css" type="text/css" />
<script type="text/javascript" src="/pmp/js/jqgridjs/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/jqxcore.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/jqxdata.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/jqxbuttons.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/jqxscrollbar.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/jqxmenu.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/jqxcheckbox.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/jqxlistbox.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/jqxdropdownlist.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/jqxgrid.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/jqxgrid.pager.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/jqxgrid.sort.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/jqxgrid.filter.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/jqxgrid.columnsresize.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/jqxgrid.selection.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/demos.js"></script>
<script type="text/javascript" src="/pmp/js/jqgridjs/generatedata.js"></script>
</head>
<body style="background-color: #dee6ed; overflow: hidden;" >
	<script type="text/javascript">
	  $(document).ready(function () {
	      var url = "/pmp/getEventList";
          var source =
         {	 
        	 type : "POST",
             datatype: "json",	 
             datafields:
             [
                 { name: 'encryptedId', type: 'string' },
                 { name: 'programChannel', type: 'string' },
                 { name: 'programStartDate', type: 'string' },
                 { name: 'programEndDate', type: 'string' },
                 { name: 'coordinatorName', type: 'string' },
                 { name: 'coordinatorEmail', type: 'string' },
                 { name: 'coordinatorMobile', type: 'string' },
                 { name: 'eventPlace', type: 'string' },
                 { name: 'eventCity', type: 'string' },
                 { name: 'eventState', type: 'string' },
                 { name: 'eventCountry', type: 'string' },
                 { name: 'organizationDepartment', type: 'string' },
                 { name: 'organizationName', type: 'string' },
                 { name: 'organizationWebSite', type: 'string' },
                 { name: 'organizationContactName', type: 'string' },
                 { name: 'organizationContactEmail', type: 'string' },
                 { name: 'organizationContactMobile', type: 'string' },
                 { name: 'preceptorName', type: 'string' },
                 { name: 'preceptorIdCardNumber', type: 'string' },
                 { name: 'welcomeCardSignedByName', type: 'string' },
                 { name: 'welcomeCardSignerIdCardNumber', type: 'string' },
                 { name: 'remarks', type: 'string' }
             ],
             id: 'encryptedId',
             url: url
         };
         var dataAdapter = new $.jqx.dataAdapter(source);
         // initialize jqxGrid
         $("#jqxgrid").jqxGrid(
         {	
             width: 1100,
             height: 500,
           //  sortable: true,
             pageable: true,
             columnsresize: true,
             source: dataAdapter,
             columns: [
               { text: 'Event ID', datafield: 'encryptedId', width: 200 ,hidden: true },
               { text: 'Event Name', datafield: 'programChannel', width: 200 },
               { text: 'Program Start Date', datafield: 'programStartDate', width: 200 },
               { text: 'Program End Date', datafield: 'programEndDate', width: 200, cellsalign: 'left' },
               { text: 'Coordinator Name', datafield: 'coordinatorName', width: 200,cellsalign: 'left', cellsformat: 'c2' },
               { text: 'Coordinator Email', datafield: 'coordinatorEmail', width: 200,cellsalign: 'left', cellsformat: 'c2' },
               { text: 'Coordinator Mobile', datafield: 'coordinatorMobile', width: 200,cellsalign: 'left', cellsformat: 'c2' },
               { text: 'Event Place', datafield: 'eventPlace', width: 200,cellsalign: 'left', cellsformat: 'c2' },
               { text: 'Event City', datafield: 'eventCity', width: 200,cellsalign: 'left', cellsformat: 'c2' },
               { text: 'Event State', datafield: 'eventState', width: 200,cellsalign: 'left', cellsformat: 'c2' },
               { text: 'Event Country', datafield: 'eventCountry', width: 200,cellsalign: 'left', cellsformat: 'c2' },
               { text: 'Organization Department', datafield: 'organizationDepartment', width: 200, cellsalign: 'left', cellsformat: 'c2' },
               { text: 'Organization Website', datafield: 'organizationWebSite', width: 200, cellsalign: 'left', cellsformat: 'c2' },
               { text: 'Organization COntact Name', datafield: 'organizationContactName', width: 200, cellsalign: 'left', cellsformat: 'c2' },
               { text: 'Organization Contact Email', datafield: 'organizationContactEmail', width: 200,cellsalign: 'left', cellsformat: 'c2' },
               { text: 'Organization Contact Mobile', datafield: 'organizationContactMobile', width: 200,cellsalign: 'left', cellsformat: 'c2' },
               { text: 'Preceptor ID Card No.', datafield: 'preceptorIdCardNumber', width: 200,cellsalign: 'left', cellsformat: 'c2' },
               { text: 'Welcome Card Signed By Name ', datafield: 'welcomeCardSignedByName', width: 200, cellsalign: 'left', cellsformat: 'c2' },
               { text: 'Welcome Card Signers ID Card No.', datafield: 'welcomeCardSignerIdCardNumber', width: 200,cellsalign: 'left', cellsformat: 'c2' },
               { text: 'Remarks', datafield: 'remarks', width: 200,cellsalign: 'left', cellsformat: 'c2' },
             ]
         });
       
         // display selected row index.
         $("#jqxgrid").on('rowselect', function (event) {
        	 var index = event.args.row.encryptedId;
             $('#EncryptedProgramId').val(index);
         });
        
     });


		function getSelectedRow() {
				var encryptedProgramId = $("#EncryptedProgramId").val();
	          
			if (encryptedProgramId) {
				//window.location.href = "http://10.1.29.80:7080/pmp/programForm?programId="+ rowKey;
				window.open("/pmp/programForm?programId="+ encryptedProgramId,"_self","",false);
				/*  $.ajax({
					url: 'programForm',
					type:'GET',
					data:{programId:rowKey},
					async: false,
					datatype:"json",
				 	 statusCode: {
				         407: function() {
				             $.ajaxSetup({ dataType: "jsonp" });
				             populateStatesForCountry(countryName);
				         }
				    },
					success: function(result) {
						alert(result);
						window.open("http://10.1.29.80:7080/pmp/programForm","Heartfulness","fullscreen=Yes",false);
					},
					error: function(error, status, msg) {
						alert("Error while loading Event form");
					} 
				});   */

			} else {
				alert("Please select a row first");
			}  
			
		}
		
		function createEventWindow() {
			window.open("/pmp/programForm","_self","",false);
		}
		
	</script>

	<div class="row">
		<div class="six columns" style="padding-top: 2%">
			<h3 class="eight columns" style="padding-left: 25%;">Heartfulness
				Event List</h3>
		</div>

		<div class="six columns" style="padding-left: 57%">
			<input class="button-primary" type="button" value="Create Event"
				onclick="createEventWindow()" /> <input class="button-primary"
				type="button" value="Edit Event" onclick="getSelectedRow()" />
		</div>

	</div>

	<div style="padding-top: 3%;">
		<!-- <div id="jqxgrid"></div> -->

		<div id='jqxWidget'
			style="font-size: 13px; font-family: Verdana; float: center;">
			<div id="jqxgrid" style="margin:0 auto;"></div>
		</div>
	</div>
	<div>
		<input type="hidden" id="EncryptedProgramId" name="EncryptedProgramId" />
	</div>


</body>
</html>