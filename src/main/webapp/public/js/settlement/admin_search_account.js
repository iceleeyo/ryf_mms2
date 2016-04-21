function init(){
	initMinfos();
	 dwr.util.addOptions("mstate", m_mstate);
}
var accountTypeMap = {};
	RypCommon.getAccountType(function(d) {
		accountTypeMap = d;
	});      
// 根据条件查询(第一次查询)
function queryBaseAccount(pageNo) {
	var mid = dwr.util.getValue("mid");
	 if(mid !='' && !isFigure(mid)){
			alert("商户号只能是整数!");
			$("mid").value = '';
			return false; 
 	 }
	var begindate = dwr.util.getValue("begin_date");
	var enddate = dwr.util.getValue("end_date");
	var mstate=dwr.util.getValue("mstate");
	if (begindate == '' || enddate == '') {
		alert("请输入查询日期");
		return false;
	}
	if(pageNo==-1){
		dwr.engine.setAsync(false);
		SettlementService.downLoadAccount(mid, begindate, enddate,mstate,function(data){
				dwr.engine.openInDownload(data);
		});
	}else{
		SettlementService.searchAccount(pageNo, mid, begindate, enddate,mstate,callBackAccountList);
	}
}
// 提取用户列表的回设函数：accountList中放的是account对象
function callBackAccountList(accountList) {
		document.getElementById("accountList").style.display = '';
		var chxCount=1
		var cellFuncs = [										 
		                 function(obj) { return  chxCount++;},
		                 function(obj) { return obj.mid; },
		                 function(obj) { return  obj.abbrev; },
		                 function(obj) { return obj.liqType == 1 ? "全额结算" : "净额结算"; },
		                 function(obj) { return obj.date; },
		                 function(obj) { return getStringTime(obj.time); },
		                 function(obj) { return accountTypeMap[obj.type]; },
		                 function(obj) { return obj.tseq; },
		                 function(obj) { return div100(obj.amount); },
		                 function(obj) { return div100(obj.fee); },
		                 function(obj) { return div100(obj.account); },
		                 function(obj) { return div100(obj.balance ); }
		              ]
		paginationTable(accountList,"accountTable",cellFuncs,"","queryBaseAccount");
}