<html>
<head>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jquery-ui-timepicker-addon/1.6.3/jquery-ui-timepicker-addon.min.css" />
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jqueryui/1.12.1/jquery-ui.min.css" />
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jqueryui/1.12.1/jquery-ui.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-ui-timepicker-addon/1.6.3/jquery-ui-timepicker-addon.min.js"></script>

<title>Set Offset for Consumer By Topic</title>

</head>
<body onload="getGMTTime();">

<script type="text/javascript">

function displayGMTTime(){
	var refresh=1000; // Refresh rate in milli seconds
	setTimeout('getGMTTime()',refresh)
}

function getGMTTime() {
	var date = new Date();
	gmtTime = date.toGMTString();
	$('#gmtTime').html(gmtTime);
	displayGMTTime();
}

function submitFormData(){

var offsetTopicArray = [];
var offset={};
var errFlag = "true";

    	
 $("div#dynamic").each(function(){
    var offsetDate = $('#offsetDate', this).val();
    var topicName = $('#topicName', this).val();
    var groupId = $('#groupId', this).val();
    
    if(offsetDate == "" || offsetDate == null){
    	$("span#offsetDateErr").show();
    	$("#offsetDate",this).next("span.text-danger").show();
    	errFlag = false;
    }
    
    if(groupId == "" || groupId == null){
    	$("span#groupIdErr").show();
    	$("#groupId",this).next("span.text-danger").show();
    	errFlag = false;
    }
    
    offset = {
    	"offsetDate": offsetDate, 
    	"topicName": topicName, 
    	"groupId":groupId
    };
    offsetTopicArray.push(offset);
  });
  
  if(errFlag){
	  $.ajax({
			url: '/setOffsetByDateTime',
			type: 'POST',
			contentType: 'application/json; charset=utf-8',
			data: JSON.stringify(offset),
			success: function(data) {
				
		        console.log(data)
		        $("#responseMsg").html(data);
		        
			}
	 });  
	}
}

$(function(){
	$('#offsetDate').datetimepicker({
		controlType: 'select',
		dateFormat: 'mm/dd/y',
		timeFormat: 'hh:mm TT',
		oneLine: true,
		timeInput: true,
		separator: ', ',
		timezone: '+0530'
	});
	
	/* var ex13 = $('#offsetDate');
	
	ex13.datetimepicker({
		controlType: 'select',
		dateFormat: 'mm/dd/y',
		timeFormat: 'hh:mm TT',
		timeInput: true,
		separator: ', ',
		timezone: '+0530'
	}); */
});

</script>

<div id="offSetDiv">
	<h2>Set Offset for Consumer By Topic </h2>
	
	<div id="offSetInfoFormPanel" class="card">
		<form id="offSetInfoForm" class="form-inline card-body">
	        <div class="form-row" id="dynamic">
	        
	        <!-- #list topics![] as c-->
	        <div class="form=group">
	        	<label class="control-label">Current GMT </label>
	        	&nbsp;<span id="gmtTime">&nbsp;</span>
	        </div>
	        &nbsp;&nbsp;
	        <div class="form-group">
	            <label class="control-label" for="offsetDate">Offset Date </label>
	            &nbsp;&nbsp;<input type="text" id="offsetDate" name="offsetDate" attributes='class="form-control" size="20"'/>
	            <span class="text-danger" style="display:none;"><i class="fa fa-times-circle"></i></span>
	            <div style="color:red;padding-left: 85px;font-size: 12px;">*Timestamp should be in GMT</div>
	        </div>
	        &nbsp;&nbsp;
	        <br/>
	        <div class="form-group">
	        	<label class="control-label" for="topicName">Topic Name</label>
	            &nbsp;<input type="text" id="topicName" attributes='class="form-control" size="20"'/>
	        </div>
	        &nbsp;&nbsp;
	        <div class="form-group">
	            <label class="control-label" for="groupId">Group Id </label>
	            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="text" id="groupId" attributes='class="form-control" size="20"'/>
	        </div>
	         &nbsp;&nbsp;
	         
			</div>
			<div class="form-group">
	        	<button id="searchMessagesBtn" class="btn btn-success" type="button" onClick="submitFormData()" ><i class="fa fa-database"></i> Set Offset</button>
	        </div>
	        <div class="container">	
	        	<span id="offsetDateErr" class="text-danger" style="display:none;">	            
		            <i class="fa fa-times-circle">Offset Date cannot be empty</i>
	            </span>
	        </div>
	        <br/>
	        <div class="container">	
	            <span id="groupIdErr" class="text-danger" style="display:none;">
	                <i class="fa fa-times-circle">Group Id cannot be empty</i>
                </span>
	        </div>
		</form>
		<div id="responseMsg">&nbsp;</div>
	</div>
</div>

</body>
</html>

