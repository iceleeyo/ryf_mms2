function init() {
	initMinfos();
	dwr.util.addOptions("category", m_category);
}

var mid, category, bdate, edate;
function Accounts() {
	mid = $("mid").value;
	category = $("category").value;
	bdate = $("bdate").value;
	edate = $("edate").value;
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

function queryAccounts(pageNo) {
	if (!Accounts())return;
	SettlementService.queryaccounts(pageNo,mid, category, bdate, edate, callBack2);
}
function downAccounts() {
	if (!Accounts())return;
 	SettlementService.downaccounts(mid, category, bdate, edate,
	{callback:function(data){dwr.engine.openInDownload(data);}, async:false});//把ajax调用设置为同步
}

var callBack2 = function(pageObj){
	  $("AccountTable").style.display="";
	  dwr.util.removeAllRows("resultList");
var cellFuncs = [
                 function(obj) { return obj.mid; },
                 function(obj) { return m_minfos[obj.mid]; },
                 function(obj) { return mer_category_map[obj.category]; },
                 function(obj) { return div100(obj.previousBalance); }, 
                 function(obj) { return bdate; },
                 function(obj) { return edate; },
                 function(obj) { return div100(obj.currentBalance); }
             ];
      paginationTable(pageObj,"resultList",cellFuncs,"","queryAccounts");
  };