
function init(){
	dwr.util.addOptions("mstate", m_mstate);
	initMinfos();
}

var merInfo = {};
SettlementTableService.getHashMer(function(data) {
			merInfo = data;
});		
var currentPage = 1;
function queryDownload_settlement(p) {
	var date_b = document.getElementById("beginDate").value;
	var date_e = document.getElementById("endDate").value;
	var batch = document.getElementById("batch").value;
	var state  = document.getElementById("state").value;
	var mid = document.getElementById("mid").value;
	var mstate=document.getElementById("mstate").value;
//	var liqObj=document.getElementById("jsdx").value;
	var liqObj="";
	 if(mid !='' && !isFigure(mid)){
			alert("商户号只能是整数!");
			$("mid").value = '';
			return false; 
 	 }
	if (checkdata(date_b,date_e,batch)) {
		SettlementTableService.getFeeLiqBath(p, date_b, date_e, mid, state, batch,mstate,liqObj,resultData);
	}
}
function checkdata(date_b, date_e, batch) {
	if (date_b == '' || date_e == '') {
		alert("请选择结算发起日期!");
		return false;
	}
	if (!judgeDate(date_b, date_e)) {
		alert("开始日期应先于结束日期");
		return false;
	}
	if (!isFigure(batch.trim())) {
		alert("批次号只能为数字，请重新输入！");
		   document.getElementById("batch").value = '';
   		   document.getElementById("batch").focus();
		return false;
	}
	return true;
}
var resultData = function (flbPage) {
    var chxCount=0;
    dwr.util.removeAllRows("bodyTable");
    document.getElementById("resultTable").style.display ="";
	if (flbPage.pageTotle != 0) {
			var cellFuncs = [										 
		                 function(obj) { return  obj.state==1?"<input type='checkbox' id='toCheck' name='batchs' value='"+obj.batch + "'/>":"";},
		                 function(obj) { return obj.mid; },
		                 function(obj) { return  merInfo[obj.mid]; },
		                 function(obj) { return div100(obj.transAmt); },
		                 function(obj) { return div100(obj.refAmt); },
		                 function(obj) { return div100(obj.transAmt - obj.refAmt); },
		                 function(obj) { return div100(obj.feeAmt); },
		                 function(obj) { return div100(obj.refFee); },
		                 function(obj) { return div100(obj.manualAdd); },
		                 function(obj) { return div100(obj.manualSub); },
		                 function(obj) { return div100(obj.liqAmt); },
		                 function(obj) { return obj.batch; },
		                 function(obj) { return obj.liqDate; },
		                 function(obj) { chxCount++;return obj.state==1?"已发起":"已制表";}
		                 ];
			str="<span  style='float:left'>&nbsp;&nbsp;&nbsp;&nbsp;<input type='button' onclick='checkChx()' value='制表提交' id='commitButton' style='display:none' /></span>";//
		 paginationTable(flbPage,"bodyTable",cellFuncs,str,"queryDownload_settlement");
		document.getElementById("allSelect").checked = false;
		if (chxCount > 0) {// 如果没有已发起的数据【制表提交】按钮不显示 全选框 不可用
			document.getElementById("allSelect").disabled = false;
			document.getElementById("commitButton").style.display = "";
			chxCount = 0;
		}else {
			document.getElementById("allSelect").disabled = true;
			document.getElementById("commitButton").style.display = "none";
		}
	}else {
			   var trElement = document.createElement("tr");
		       var tdElement = document.createElement("td");
		       tdElement.colSpan = 15;
		       tdElement.innerHTML = "没有符合条件的查询记录!";
		       trElement.appendChild(tdElement);
		       $("bodyTable").appendChild(trElement);
	}
}

function allSelected() {
	var whole = document.getElementById("allSelect");
	var elems = document.getElementsByName("batchs");
	for (i = 0; i < elems.length; i++) {
		elems[i].checked = whole.checked;
	}
}
function downloadSettleTable(batch){
	dwr.engine.setAsync(false);
	SettlementService.downloadSettleTB(batch,function(data) {
		  if(data==null){alert("您下载的文件不存在，请联系管理员重新制表！");return;}
			dwr.engine.openInDownload(data);});
}
function checkChx() {
	var chx = document.getElementsByName("batchs");
	var batchs = new Array();
	var k = 0;
	for (var i = 0; i < chx.length; i++) {
		if (chx[i].checked){
			batchs[k] =chx[i].value;
			k++;
		}
	}
	if (k == 0) {
		alert("请选择一条记录!");
	} else {
		var button = document.getElementById('commitButton');
		button.disabled = 'true';
		try {
			SettlementTableService.drawSettleTB(batchs, function(msg) {
				alert(msg);
				queryDownload_settlement(currentPage);
				document.getElementById("allSelect").disabled = true;
				button.disabled = "";
				button.style.display = "none";
			});
		} catch (e) {
			alert("制表文件生成异常!");
		}
	}
}

     