function init(){
	initMinfos();
	dwr.util.addOptions("mstate", m_mstate);
}
// 根据条件查询(第一次查询)
	var currPage=1;
function queryBaseAuditAccount(pageNo) {
	var mid = dwr.util.getValue("mid").trim();
	var btdate = dwr.util.getValue("btdate");
	var etdate = dwr.util.getValue("etdate");
	var type = dwr.util.getValue("type");
	var state = dwr.util.getValue("state");
	var mstate=dwr.util.getValue("mstate");
	if (mid != '' && !isFigure(mid)) {
		alert("商户号只能是整数!");$("mid").value='';
		return false;
	}
	currPage=pageNo;
	SettlementService.queryAdjust(pageNo, mid, type, btdate, etdate, state,mstate,callBackAuditAccountList);
}
// 提取用户列表的回设函数：adjustaccountList中放的是adjustaccount对象
var callBackAuditAccountList = function(auditaccountList) {
	document.getElementById("auditaccountList").style.display = '';

	    var cellFuncs = [
	                     function(obj) { 
	                    	// if(obj.state == 0&&obj.reason!="退款手续费退回"&&obj.reason!="TKSXFTH")//
	                    	if(obj.state == 0)return "<input type='checkbox' id='toValidate' name='toValidate' value='"+ obj.id + "'>"; 
	                    	else return "";
	                     },
	                     function(obj) { return obj.mid; },
	                     function(obj) { return m_minfos[obj.mid]; },
	                     function(obj) { return obj.submitOperid; },
	                     function(obj) { return obj.submitDate + "  " + getStringTime(obj.submitTime); },
	                     function(obj) { return div100(obj.account); },
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
	                 ];
	    var loginusermid = document.getElementById("loginusermid").value;
		var loginuseroperid = document.getElementById("loginuseroperid").value;
		var str = "<span  style='float:left'>&nbsp;&nbsp;&nbsp;<input type='button' id='Validate' name='Validate' value='审核成功' onclick=\"audit_Adjust('success','"
				+ loginusermid+ "','"+ loginuseroperid+ "')\">&nbsp;&nbsp;&nbsp;";
		   str += "<input type='button' id='Validate' name='Validate' value='审核失败' onclick=\"audit_Adjust('failure','"
				+ loginusermid+ "','"+ loginuseroperid+ "')\">&nbsp;&nbsp;&nbsp;</span>";
	   paginationTable(auditaccountList,"adjustaccountTable",cellFuncs,str,"queryBaseAuditAccount");
};	
function allSelected() {
	var whole = document.getElementById("allSelect");
	var elems = document.getElementsByName("toValidate");
	for (var i = 0; i < elems.length; i++) {
		elems[i].checked = whole.checked;
	}
}
function audit_Adjust(action, loginmid, loginuid) {
	var checkboxSelete = "Nothing";
	var seleteAuditId = "";
	if (document.forms["form2"].length == 9) {
		if (document.forms["form2"].toValidate.checked) {
			checkboxSelete = "seleted";
			var checkvalue = document.forms["form2"].toValidate.value;
			seleteAuditId += checkvalue + "&";
		}
	} else {
		for (var i = 0; i < document.forms["form2"].toValidate.length; i++) {
			if (document.forms["form2"].toValidate[i].checked) {
				checkboxSelete = "seleted";
				var checkvalue = document.forms["form2"].toValidate[i].value;
				seleteAuditId += checkvalue + "&";
			}
		}
	}
	if (checkboxSelete == "Nothing") {
		alert("请至少选择一条记录！");
		return false;
	}
	if (action == 'success' && confirm("确定审核成功吗?")) {
		SettlementService.auditAdjust(action, loginmid, loginuid, seleteAuditId,
				callbackAudit);
	}
	if (action == 'failure' && confirm("确定审核失败吗?")) {
		SettlementService.auditAdjust(action, loginmid, loginuid, seleteAuditId,
				callbackAudit);
	}
}
function callbackAudit(msg) {
	if (msg != undefined) {
		alert(msg);
		// 没有查到数据的时候就不调用重新查询
		queryBaseAuditAccount(currPage);
	}
}
     