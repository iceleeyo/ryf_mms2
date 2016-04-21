function doQuery() {
	var bdate = $("bdate").value;
	var edate = $("edate").value;
	if(bdate == '' || edate == ''){
		alert("请输入统计时间");
		return;
	}
	
	UserLoginAnalysisService.statisticsUserLogin(bdate, edate, callBackDisplay);
}

var callBackDisplay = function(queryResult){
	$("statisticsTable").style.display = "";
	var html = "<tr>";
	html += "<td>" + queryResult.statisticsDate + "</td>";
	html += "<td>" + queryResult.login_success_num + "</td>";
	html += "<td>" + queryResult.login_fail_num + "</td>";
	html += "<td>" + queryResult.exit_system_num + "</td>";
	html += "</tr>";
	$("resultList").innerHTML = html;
};

function doDownload() {
	var bdate = $("bdate").value;
	var edate = $("edate").value;
	if(bdate == '' || edate == ''){
		alert("请输入统计时间");
		return;
	}
    
	UserLoginAnalysisService.doDownload(bdate, edate, function(data) {
		dwr.engine.openInDownload(data);
	});
}