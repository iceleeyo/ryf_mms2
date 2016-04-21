//admin和jsp中公用  
// 根据条件查询
function queryBaseAdjustAccount(pageNo) {
	var mid = dwr.util.getValue("mid").trim();
	var btdate = dwr.util.getValue("btdate");
	var etdate = dwr.util.getValue("etdate");
	var type = dwr.util.getValue("type");
	var state = dwr.util.getValue("state");
	if (mid != '' && !isFigure(mid)) {
		alert("商户号只能是整数!");$("mid").value='';
		return false;
	}
	MerSettlementService.queryAdjust(pageNo, mid, type, btdate, etdate, state,callBackAdjustAccountList);
}
// 提取用户列表的回设函数：adjustaccountList中放的是adjustaccount对象
var callBackAdjustAccountList = function(adjustaccountList) {
		document.getElementById("adjustaccountList").style.display = '';
		var count=1;
	    var cellFuncs = [
	                     function(obj) { return count++ },
	                     function(obj) { return obj.mid; },
	                     function(obj) { return m_minfos[obj.mid]; },
	                     function(obj) { return obj.submitOperid; },
	                     function(obj) { return obj.submitDate + "  " + getStringTime(obj.submitTime); },
	                     function(obj) { return obj.account / 100; },
	                     function(obj) { return obj.type == 1 ? "手工增加":"手工减少";},
	                     function(obj) { return  obj.state == 0 ? "调账提交":obj.state == 1 ? "审核成功":"审核失败"; },
	                     function(obj) { return obj.auditOperid;},
	                     function(obj) { return obj.auditDate==null?"":(obj.auditDate+"  "+getStringTime(obj.auditTime)); },
	                     function(obj) {  
	                    	 if(obj.reason=="TKSXFTH"){return "退款手续费退回";}
	                    	 if(obj.reason.length > 20){
	                    		 return "<a href=\"#\" tip='"+obj.reason+"' class='showTip' >"+obj.reason.substring(0,10)+"...[详细]";
	             		     } 
	                    	 else{
	             			     return obj.reason;
	             		      }
	                    }
	                 ]
	     paginationTable(adjustaccountList,"adjustaccountTable",cellFuncs,"","queryBaseAdjustAccount");	     
}