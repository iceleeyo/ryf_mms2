/**
 * 结算失败查询
 */
var category, bToDate, eTodate, mid, bLiqDate, eLiqDate,reason;
function queryFailRecord(pageNo){
	category=$("category").value;
	bToDate=$("bToDate").value;
	eTodate=$("eToDate").value;
	reason=$("reason").value;
	mid=$("mid").value;
	bLiqDate=$("bLiqDate").value;
	eLiqDate=$("eLiqDate").value;
	if(!bLiqDate||!eLiqDate){
		alert("请输入结算发起日期");
		return;
	}
	doQueryFailRecord(pageNo);
}

function doQueryFailRecord(pageNo){
	LiqService.queryLiqFailRecord(category, bToDate, eTodate, mid, bLiqDate, eLiqDate,reason,pageNo,callBack);
}
var types = {0:'RYF商户',1:'VAS商户',2:'POS商户',3:'POS代理商'};
var colors = {1:'red',2:'blue',3:'green',4:'silver',5:'lightskyblue',6:'aqua',7:'coral',8:'violet'};
var reasons;
function callBack(pageObj){
	if(pageObj==null){
		document.getElementById("resultList").appendChild(creatNoRecordTr(6));
	return;
	}
	var cellFuncs = [
	       function(obj) { return types[obj.category]; },
	       function(obj) { return obj.mid; },
	       function(obj) { return obj.name; },
	       function(obj) { return '<font color="blue">'+formatDate(obj.expDate)+'</font>'; },
	       function(obj) { return '<font color="blue">'+formatDate(obj.liqDate)+' '+ obj.liqTime1+'</font>'; },
	       function(obj) { return '<div style="background-color: '+colors[obj.reason]+';width:100%;height:100%" ><span style="color:white;font-weight:bold;">'+reasons[obj.reason]+'</span></div>';}
	   ];
	var str = "&nbsp;&nbsp;&nbsp;&nbsp;结算发起总数：<font color='red'>"+(pageObj.sumResult["successCount"]+pageObj.pageTotle)+"</font>&nbsp;&nbsp;个   成功：<font color='red'>"+pageObj.sumResult["successCount"]+"</font>   失败：<font color='red'>"+pageObj.pageTotle+"</font>";
	paginationTable(pageObj,"resultList",cellFuncs,str,"doQueryFailRecord");
	jQuery("#failListTable").show();
}
function downLiqFailList(){
	LiqService.downLiqFailList(category, bToDate, eTodate, mid, bLiqDate, eLiqDate,reason,function(data){
			dwr.engine.openInDownload(data);
		});
}
function formatDate(date){
	return (date+"").substr(0,4)+"-"+(date+"").substr(4,2)+"-"+(date+"").substr(6,2);
}

jQuery(document).ready(function(){
	LiqService.getFailReasonsAsMap(function(data){
		reasons = data;
		dwr.util.addOptions("reason", reasons);
	});
});

