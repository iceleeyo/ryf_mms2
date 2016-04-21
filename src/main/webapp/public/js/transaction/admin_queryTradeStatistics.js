//公共代码
function init() {
	dwr.util.addOptions("type", h_type);
	initGateChannel();
}

var bdate, edate, type;
function judgeCondition() {
	bdate = $("bdate").value;
	edate = $("edate").value;
	type = $("excelType").value;
	if (bdate == '') {
		alert("请选择起始时间");
		return false;

	}
	if (edate == '') {
		alert("请选择结束时间");
		return false;

	}
	if (type != "0" && type != "1") {
		alert("报表类型不正确");
		return false;

	}
	return true;
}
// 查询
function querytrans(pageNo) {
	if (!judgeCondition())return;
	QueryTradeStatisticService.queryTradeStatistics(bdate, edate, type, pageNo, callBack2);
}

//导出报表
function exportTransExcel(){
	if(!judgeCondition())return;
	QueryTradeStatisticService.exportTransExcel(bdate,edate,jQuery("#excelType").val(),function(data){
		dwr.engine.openInDownload(data);
	});
}

//查询的回调函数
var callBack2 = function(pageObj) {
	var table = "smsTable";
	var tableBody = "resultList";
	var anotherTable = "smsTable1";
	if(type == "1"){
		table = "smsTable1";
		tableBody = "resultList1";
		anotherTable = "smsTable";
	}
	$(table).style.display = "";
	$(anotherTable).style.display = "none";
	dwr.util.removeAllRows(tableBody);
	if (pageObj == null) {
		document.getElementById(tableBody).appendChild(creatNoRecordTr(5));
		return;
	}
	var cellFuncs;
	if(type == "0"){
		cellFuncs = [ 
		                 function(obj) {return h_gate_route_type[obj.transMode];},
		                 function(obj) {return obj.totalCount;}, 
		                 function(obj) {return obj.successCount;}, 
		                 function(obj) {return div100(obj.successAmt);},
		                 function(obj) {return toPercent(obj.totalCount!=0?(obj.successCount/obj.totalCount):0);} ];
	}else{cellFuncs = [ 
  		                 function(obj) {return h_gate_route_type[obj.transMode];},
  		                 function(obj) {return h_gate[obj.gate];},
		                 function(obj) {return obj.totalCount;}, 
		                 function(obj) {return obj.successCount;}, 
		                 function(obj) {return div100(obj.successAmt);},
		                 function(obj) {return toPercent(obj.totalCount!=0?(obj.successCount/obj.totalCount):0);}];
	}
	//ryt.js
   	paginationTable(pageObj,tableBody,cellFuncs,"","querytrans");
};

function toPercent(data) {
	var strData = parseFloat(data) * 100;
	var ret=strData.toFixed(2);
	var ret1 = ret.toString() + "%";
	return ret1;

}
