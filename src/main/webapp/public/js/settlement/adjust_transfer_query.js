function init() {
	var obj = [11,12];
	initGateChannel2(obj);
	dwr.util.addOptions("tstat", h_tstat);
	
}

var mid, bdate, edate, batchNo, tstat,type,gateRouteId;
function judgeCondition() {
	mid = $("mid").value;
	bdate = $("bdate").value;
	edate = $("edate").value;
	batchNo = $("batchNo").value;
	tstat = $("tstat").value;
	type=$("type").value;
	gateRouteId=$("gateRouteId").value;
	if (bdate == '') {
		alert("请选择起始日期！");
		return false;
	}
	if (edate == '') {
		alert("请选择结束日期！");
		return false;
	}
	return true;
}
function querytransfer(pageNo) {
	if (!judgeCondition())return;
	QuerytransferService.querytransfer(pageNo, mid, bdate, edate, batchNo, tstat,type,gateRouteId,callBack2);
}

function downloadtransfer() {
	if(!judgeCondition())return;
    dwr.engine.setAsync(false);//把ajax调用设置为同步
    QuerytransferService.downloadtransfer(mid, bdate, edate, batchNo, tstat,type,gateRouteId,function(data){dwr.engine.openInDownload(data);});
}
var callBack2 = function(pageObj){
	   $("queryTransferTable").style.display="";
	   dwr.util.removeAllRows("resultList");
	   if(pageObj==null){document.getElementById("resultList").appendChild(creatNoRecordTr(13));return;}
      var cellFuncs = [
                       function(obj) { return obj.tseq; },
                       function(obj) { return obj.bankNo; },
                       function(obj) { return obj.mid; },
                       function(obj) { return obj.batch; },
                       function(obj) { return obj.liqDate;},
                       function(obj) { return obj.name; },
                       function(obj) { return obj.bankName; },//开户银行名称  p4
                       function(obj) { return obj.bankBranch; },//开户银行支行名 p5
                       function(obj) { return obj.bankAcctName; }, //开户账户名称 p2 
                       function(obj) { return shadowAcc(obj.bankAcct); },//开户账户号 p1
                       function(obj) { return h_tstat[obj.tstat]; },
                       function(obj) { return div100(obj.amount); },
                       function(obj) { return div100(obj.feeAmt); },
                       function(obj) { return obj.type==11?"对私代付":"对公代付"; },
                       function(obj) { return h_gate2[obj.gate]; },
                       function(obj) { return gate_route_map2[obj.gid]; },
                      function(obj) {return obj.tstat == 2 ? "" :createTip(8,obj.errorMsg) }
                   ]
        str = "<span class='pageSum'>&nbsp;交易总金额：<font color='red'><b>" + div100(pageObj.sumResult.amtSum)
        		 +"</b></font>  元</span>";
        paginationTable(pageObj,"resultList",cellFuncs,str,"querytransfer");
    }