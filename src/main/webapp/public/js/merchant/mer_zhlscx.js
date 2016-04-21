var zh_flag =null;
function init(){
	if(zh_flag==null){
		 MerZHService.getZH2(function(zh){
			 dwr.util.addOptions("aid", zh);
			zh_flag=zh;
		 })
	    }
	$("bdate").value = jsToday();
	$("edate").value = jsToday();
}

//查询账户交易流水
function queryLS(pageNo){
	var aid=$("aid").value;
	var bdate=$("bdate").value;
	var edate=$("edate").value;
	if(bdate=='') {
        alert("请选择起始日期！"); 
        return false; 
    }
    if(edate==''){
         alert("请选择结束日期！"); 
         return false; 
    }
    MerZHService.queryLS(pageNo,aid,bdate,edate,callBack);
}
var currPage=1;
var callBack = function(pageObj){
	   $("LSTable").style.display="";
	   var cellFuncs = [
	                    function(obj) { return obj.aid; },
	                    function(obj) { return obj.trDate; },
	                    function(obj) { return getStringTime(obj.trTime); },
	                    function(obj) { return rec_pay[obj.recPay]; },
	                    function(obj) { return div100(obj.trAmt); },
	                    function(obj) { return div100(obj.trFee); },
	                    function(obj) { return div100(obj.amt); },
	                    function(obj) { return div100(obj.balance); },//可用余额
	                    function(obj) { return div100(obj.allBalance+obj.balance); },
	                    //function(obj) { return obj.trFlag; },
	                    function(obj) { return obj.remark; }
	                ];	
	                currPage=pageObj.pageNumber; 
	   			paginationTable(pageObj,"resultList",cellFuncs,"","queryLS");
  }

//下载查询结果

function downloadLS(){
	var aid=$("aid").value;
	var bdate=$("bdate").value;
	var edate=$("edate").value;
	if(bdate=='') {
        alert("请选择起始日期！"); 
        return false; 
    }
    if(edate==''){
         alert("请选择结束日期！"); 
         return false; 
    }
    dwr.engine.setAsync(false);//把ajax调用设置为同步
    MerZHService.downloadLS(aid,bdate,edate,
    				function(data){dwr.engine.openInDownload(data);})
}