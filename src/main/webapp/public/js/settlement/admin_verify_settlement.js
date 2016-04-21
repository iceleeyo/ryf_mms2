function init(){
	initMinfos();
	  var obj=[11];
      initGateChannel3(obj);
      $("beginDate").value = jsToday();
      $("endDate").value = jsToday();
	dwr.util.addOptions("mstate", m_mstate);
}
var merInfo = {};
RypCommon.getHashMer( function(data) {
		merInfo = data;
})
var gatesMap = {};
RypCommon.getHashGate(function(m) {
		gatesMap = m;
});
var query_cash = {};
var currentPage = 1;
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
		clean("batch");
		return false;
	}
	return true;
}
function clean(id){
    document.getElementById(id).value = '';
    document.getElementById(id).focus();
}

function query(p){ 
	
	var date_b = document.getElementById("beginDate").value;
	var date_e = document.getElementById("endDate").value;
	var batch = document.getElementById("batch").value;
	var state  = document.getElementById("state").value;
	var mid = document.getElementById("mid").value;
	var mstate=document.getElementById("mstate").value;
	var liqGid = document.getElementById("liqGid").value;
	var gid=$("gateRouteId").value;
	 if(mid !='' && !isFigure(mid)){
			alert("商户号只能是整数!");
			$("mid").value = '';
			return false; 
 	 }
	
	
	if (checkdata(date_b,date_e,batch)) {
		backSettlement();
		SettlementVerifyService.getFeeLiqBath(p, date_b, date_e, mid, state, batch,mstate,liqGid,gid,resultData);
	}
}

var chxCount = 0;
var resultData = function (flbPage) {
	 dwr.util.removeAllRows("bodyTable");
	if (flbPage.pageTotle != 0) {	
		var cellFuncs = [
		                 function(obj) { return  obj.state==2?"<input type='checkbox' id='toCheck' name='batchs' value='"+  obj.batch + "'/>":"";},
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
		                 function(obj) { return obj.state==2?"已制表":"已确认";},
		                 function(obj) { 
		                	 chxCount++;
		                	 return "<a href='javascript:showSettlement_detail(\""
		                	 +obj.batch+"\");'><u><font color='blue'>交易明细</font></u></a>";  	                      
		                 }
		              ]
		for(var i=0;i<flbPage.pageItems.length;i++){
			var o = flbPage.pageItems[i];
   	        query_cash[o.batch] = o;
		}
		 var str="<span  style='float:left'>&nbsp;&nbsp;&nbsp;&nbsp;<input type='button' onclick='checkChx()' value='结算完成' id='commitButton' /></span>";		
		
		paginationTable(flbPage,"bodyTable",cellFuncs,str,"query"); 
		document.getElementById("allSelect").checked = false;
		if (chxCount > 0) {// 如果有已发起的数据【结算确认】按钮显示  全选框 可用
			document.getElementById("allSelect").disabled = false;
			chxCount = 0;
		} else {
			document.getElementById("allSelect").disabled = true;
			//document.getElementById("commitButton").style.display = "none";
		}
	}else {
		document.getElementById("bodyTable").appendChild(creatNoRecordTr(15));
	}
}

function allSelected() {
	var whole = document.getElementById("allSelect");
	var elems = document.getElementsByName("batchs");
	for (i = 0; i < elems.length; i++) {
		elems[i].checked = whole.checked;
	}
}

function checkChx() {
	var chx = document.getElementsByName("batchs");
	var batchs = new Array();
	var k = 0;
	for (var i = 0; i < chx.length; i++) {
		if (chx[i].checked){
			 batchs[k]=query_cash[chx[i].value];
			k++;
		}
	}
	if(k == 0) {
	alert("请选择一条记录!");
	return false;
	}
	if(!confirm("确认结算完成？"))return false;
	SettlementVerifyService.verifySettle(batchs,function(msg){
		alert(msg);
		query(currentPage);
		
	});
	//推送结算单
	SettlementNoticeService.pushSettlement(batchs,function(msg){
		
	});
}
//显示Settlement的详情    //清算金额=交易净额（支付金额-退款金额）-系统手续费+手工增加-手工减少+退回商户手续费
function showSettlement_detail(batch){

	SettlementVerifyService.getFeeLiqBathByBatch(batch,function(flb){
		if(flb==null){return;};
		document.getElementById("settlement_detail").style.display="";
		document.getElementById("detailResultList").style.display="none";
        var cond = flb.liqCond ==1 ? "有条件结算" : "无条件结算";
        var type = flb.liqType ==1 ? "全额" : "净额";
        dwr.util.setValues({
        	mid_:flb.mid,
        	name:merInfo[flb.mid],
        	payAmt:div100(flb.transAmt),
        	refAmt:div100(flb.refAmt),
            tradeAmt:div100(flb.transAmt-flb.refAmt),
            feeAmt:div100(flb.feeAmt),
            manualAdd:div100(flb.manualAdd),
            manualSub:div100(flb.manualSub),
            refFee:div100(flb.refFee),
            liqAmt:div100(flb.liqAmt),
            batch_:flb.batch,
            liq_cond:cond,
            liq_type:type
         });
        SettlementVerifyService.getSettleDetail(batch,function(sdList){
            var sdCount = sdList.length;
            dwr.util.setValue("count",sdCount);
            dwr.util.removeAllRows("bodyTable2");
            var cellfuncs=[ 
                            function(obj) { return flb.mid; },
                            function(obj) { return merInfo[flb.mid]; },
                            function(obj) { return gatesMap[obj.gate]; },
                            function(obj) { return obj.tseq; },
                            function(obj) { return div100(obj.amount); },
                            function(obj) { return div100(obj.refAmt); },
                            function(obj) { return div100(obj.tradeAmt); },
                            function(obj) { return div100(obj.feeAmt); },
                            function(obj) { return div100(obj.merFee); }
                           ];
          if(sdCount==0)document.getElementById("bodyTable2").appendChild(creatNoRecordTr(cellfuncs.length));
           dwr.util.addRows("bodyTable2",sdList,cellfuncs);
        });
     });
}
//点击返回按钮，返回
function backSettlement(){
	document.getElementById("settlement_detail").style.display="none";
	document.getElementById("detailResultList").style.display="";
}
