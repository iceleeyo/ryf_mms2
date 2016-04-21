 var merInfo = {};
 SettlementService.getHashMer( function(data) {
		merInfo = data;
	});
//	var gatesMap = {};
//	RypCommon.getHashGate(function(m) {
//		gatesMap = m;
//	});
	function hasChecked(){
//	    var mids = document.getElementById("mids").value;
		var exp_date = document.getElementById("exp_date").value;
		if(exp_date == ''){
			alert("请选择要截至日期");
			return false;
		}
//		if(mids!='' && !checkMids(mids)){
//		   alert("您输入的商户号格式有误,请参照提示重新输入!");
//		   return false;
//		}
		return true;
	}
//	function checkMids(data){
//		var reg = /^\d{1,9}(,\d{1,9})*$/;
//	return reg.test(data);
//	}
	function queryBegin_settlement(p){
		SettlementService.getFeeLiqBath(p, 0,0, 0, 1, 0,resultData);
	}
var resultData = function (flbPage) {
	if (flbPage.pageTotle != 0) {
		document.getElementById("resultTable").style.display = '';	
			var cellFuncs=[
							 function(obj) { return obj.mid; },
	                         function(obj) { return merInfo[obj.mid]; },
	                         function(obj) { return div100(obj.transAmt); },
	                         function(obj) { return div100(obj.refAmt); },
	                         function(obj) { return div100(obj.transAmt - obj.refAmt); },
	                         function(obj) { return div100(obj.feeAmt); },
	                         function(obj) { return div100(obj.manualAdd); },
	                         function(obj) { return div100(obj.manualSub); },
	                         function(obj) { return div100(obj.liqAmt); },
	                         function(obj) { return obj.batch; },
	                         function(obj) { return obj.liqDate; },
	                         function(obj) { return "已发起";}
//	                         function(obj) { return "<a href='javascript:showSettlement_detail("
//		                	  +obj.batch+");'><u><font color='blue'>交易明细</font></u></a>";}
						   ]

		 paginationTable(flbPage,"bodyTable",cellFuncs,"","queryBegin_settlement");
	}else {
		document.getElementById("resultTable").style.display = "none";
	}
}
/***
* 与verify_settlement.js中的函数相同
*
*/
//显示Settlement的详情
//function showSettlement_detail(batch){
//	document.getElementById("settlement_detail").style.display="";
//	document.getElementById("resultTable").style.display="none";
//	
//   SettlementService.getFeeLiqBathByBatch(batch,function(flb){
//        var cond = flb.liqCond ==1 ? "有条件结算" : "无条件结算";
//        var type = flb.liqType ==1 ? "全额" : "净额";
//        dwr.util.setValues({mid_:flb.mid,name:merInfo[flb.mid],payAmt:flb.transAmt/100,refAmt:flb.refAmt/100,
//        tradeAmt:(flb.transAmt-flb.refAmt)/100,feeAmt:flb.feeAmt/100,manualAdd:flb.manualAdd/100,
//        manualSub:flb.manualSub/100,liqAmt:flb.liqAmt/100,batch_:flb.batch,liq_cond:cond,liq_type:type});
//        
//        SettlementService.getSettleDetail(batch,function(sdList){
//            var sdCount = sdList.length;
//            dwr.util.setValue("count",sdCount);
//            var sdArray = new Array(sdCount);
//            for (i = 0; i < sdCount; i ++){
//                sdArray[i] = new Array(flb.mid,merInfo[flb.mid],gatesMap[sdList[i].gate],sdList[i].tseq,sdList[i].amount/100,
//                sdList[i].refAmt/100,sdList[i].tradeAmt/100,sdList[i].feeAmt/100);
//            }
//           
//        	drawBody(sdArray,"bodyTable2");
//        });
//        });
//}
////点击返回按钮，返回
//function backSettlement(){
//	document.getElementById("settlement_detail").style.display="none";
//	document.getElementById("resultTable").style.display="";
//}