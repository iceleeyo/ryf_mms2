function doQuery() {
	var bdate = $("bdate").value;
	var edate = $("edate").value;
	var moduleId = $("moduleId").value;
	if(bdate == '' || edate == ''){
		alert("请输入系统日期");
		return;
	}
	
	SystemModuleStatisticsService.doQuery(bdate, edate, moduleId, callBackDisplay);
}

var callBackDisplay= function(queryResult){
	$("statisticsTable").style.display = "";
	 dwr.util.removeAllRows("resultList");
	var table = document.getElementById("resultList");
	for(var i in queryResult){	
		var tr = document.createElement("tr");
		var td = document.createElement("td");
		td.innerHTML =queryResult[i].serialNumber;
		tr.appendChild(td);
		var td2 = document.createElement("td");
		td2.innerHTML =queryResult[i].action;
		tr.appendChild(td2);
		var td3 = document.createElement("td");
		td3.innerHTML =queryResult[i].last_oper_time;
		tr.appendChild(td3);
		var td4 = document.createElement("td");
		td4.innerHTML =queryResult[i].oper_num;
		tr.appendChild(td4);
		table.appendChild(tr);
	}
};

function doDownload() {
	var bdate = $("bdate").value;
	var edate = $("edate").value;
	var moduleId = $("moduleId").value;
	if(bdate == '' || edate == ''){
		alert("请输入系统日期");
		return;
	}
    
	SystemModuleStatisticsService.doDownload(bdate, edate, moduleId, function(data) {
		dwr.engine.openInDownload(data);
	});
}