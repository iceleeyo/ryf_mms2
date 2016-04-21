document.write("<script type='text/javascript' src='../../public/js/ryt.js'></script>");
function seachOperInfos(page) {
	var date_b = document.getElementById("statd").value;
	var date_e = document.getElementById("endd").value;
	var mid = document.getElementById("mid").value.trim();
	var pageIndex = page == '' ? 1 : page;
	var operName = document.getElementById("oper_name").value.trim();
	if (mid == '' && operName == '') {
		alert("商户号或操作员名至少输入一项！");
		return false;
	}
	if (mid != '' && !isFigure(mid)) {
		alert("商户号只能为整数");
		return false;
	}
	if (date_b == '') {
		alert("请选择系统起始日期！");
		return false;
	}
	if (date_e == '') {
		alert("请选择系统结束日期！");
		return false;
	}
	MerchantService.getOperLog4DWR(mid, operName, date_b, date_e, pageIndex,
			callback);
}
var callback = function(operlogList) {
	document.getElementById("operlogTable").style.display = "";
	if (operlogList.length == 0) {
		document.getElementById("operlogBody").appendChild(creatNoRecordTr(8));
		return false;
	}
	var cellFuncs = [ function(operLog) {
		return operLog.mid;
	}, function(operLog) {
		return operLog.name;
	}, function(operLog) {
		return operLog.operId;
	}, function(operLog) {
		return operLog.oper_name;
	}, function(operLog) {
		return operLog.sysDate;	
	}, function(operLog) {
		return getStringTime(operLog.sysTime);
	}, function(operLog) {
		return operLog.operIp;
	}, function(operLog) {
		return operLog.action;
	}, function(operLog) {
		return operLog.actionDesc;
	} ];
	paginationTable(operlogList, "operlogBody", cellFuncs, "", "seachOperInfos");
};

function downloadOperLog() {
	var bdate = document.getElementById("statd").value;
	var edate = document.getElementById("endd").value;
	var mid = document.getElementById("mid").value.trim();
	var operName = document.getElementById("oper_name").value.trim();
	if (mid == '' && operName == '') {
		alert("商户号或操作员名至少输入一项！");
		return false;
	}
	if (mid != '' && !isFigure(mid)) {
		alert("商户号只能为整数");
		return false;
	}
	if (bdate == '') {
		alert("请选择系统起始日期！");
		return false;
	}
	if (edate == '') {
		alert("请选择系统结束日期！");
		return false;
	}
	MerchantService.downloadOperLog(bdate, edate, mid, operName,
			function(data) {
				dwr.engine.openInDownload(data);
			});
}