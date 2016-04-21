//admin和jsp中公用  
// 根据条件查询
function init(){
	initMinfos();
	dwr.util.addOptions("mstate", m_mstate);
}
var mid,btdate,etdate,type,state,mstate
function adjustAccount() {
	 mid = dwr.util.getValue("mid").trim();
	 btdate = dwr.util.getValue("btdate");
	 etdate = dwr.util.getValue("etdate");
	 type = dwr.util.getValue("type");
	 state = dwr.util.getValue("state");
	 mstate=dwr.util.getValue("mstate");
	if (mid != '' && !isFigure(mid)) {
		alert("商户号只能是整数!");$("mid").value='';
		return false;
	}
	 return true;
}
	function queryBaseAdjustAccount(pageNo) {
	if (!adjustAccount()) return; 
		SettlementService.queryAdjust(pageNo, mid, type, btdate, etdate, state,mstate, callBackAdjustAccountList);
}
	function downBaseAdjustAccount(){
		if (!adjustAccount()) return; 
		SettlementService.downAdjust(mid, type, btdate, etdate, state,mstate,
		{callback:function(data){dwr.engine.openInDownload(data);}, async:false});//把ajax调用设置为同步
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
	                     function(obj) { return formatDate(obj.submitDate) + "  " + getStringTime(obj.submitTime); },
	                     function(obj) { return obj.account / 100; },
	                     function(obj) { return obj.type == 1 ? "手工增加":"手工减少";},
	                     function(obj) { return  obj.state == 0 ? "调账提交":obj.state == 1 ? "审核成功":"审核失败"; },
	                     function(obj) { return obj.auditOperid;},
	                     function(obj) { return obj.auditDate==null?"":(formatDate(obj.auditDate)+"  "+getStringTime(obj.auditTime)); },
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