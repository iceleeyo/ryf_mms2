function init(){
	  $("bdate").value = jsToday();
	  $("edate").value = jsToday();
	  dwr.util.addOptions("mstate", m_mstate);
	  dwr.util.addOptions("category", m_category);
	  initMinfos();
}

function query4ZH(){
	var uid=$("mid").value;
		 AdminZHService.getZHMapByUid(uid,function(zh){
			 dwr.util.removeAllOptions("aid");
			 dwr.util.addOptions("aid", {"":"请选择..."});
			 dwr.util.addOptions("aid", zh);
			zh_flag=zh;
		 });
}
//查询账户交易流水
function queryLS(pageNo){
	var uid=$("mid").value;
	var aid=$("aid").value;
	var bdate=$("bdate").value;
	var edate=$("edate").value;
	var tseq=$("tseq").value;
	var mstate=$("mstate").value;
	var category=$("category").value;
	if(bdate=='') {
        alert("请选择起始日期！"); 
        return false; 
    }
    if(edate==''){
         alert("请选择结束日期！"); 
         return false; 
    }
    
    AdminZHService.queryLS(pageNo,uid,aid,bdate,edate,tseq,mstate,category,callBack);
}
var currPage=1;
var callBack = function(pageObj){
	 $("LSTable").style.display="";
	   var cellFuncs = [
	                    function(obj) { return obj.uid; },
	                    function(obj) { return obj.aid; },
	                    function(obj) { return obj.trDate; },
	                    function(obj) { return getStringTime(obj.trTime); },
	                    function(obj) { return rec_pay[obj.recPay]; },
	                    function(obj) { return div100(obj.trAmt); },
	                    function(obj) { return div100(obj.trFee); },
	                    function(obj) { return div100(obj.amt); },
	                    function(obj) { return div100(obj.balance); },
	                    function(obj) { return div100(obj.allBalance+obj.balance); },
	                    function(obj) { return obj.tbId; },//流水号
	                    function(obj) { return obj.tbName; },
	                    function(obj) { return obj.remark; }
	                ];	
	                currPage=pageObj.pageNumber; 
	   			paginationTable(pageObj,"resultList",cellFuncs,"","queryLS");
  };

//下载查询结果

function downloadLS(){
	var uid=$("mid").value;
	var aid=$("aid").value;
	var bdate=$("bdate").value;
	var edate=$("edate").value;
	var tseq=$("tseq").value;
	var mstate=$("mstate").value;
	var category=$("category").value;
	if(bdate=='') {
        alert("请选择起始日期！"); 
        return false; 
    }
    if(edate==''){
         alert("请选择结束日期！"); 
         return false; 
    }
    dwr.engine.setAsync(false);//把ajax调用设置为同步
    AdminZHService.downloadLS(uid,aid,bdate,edate,tseq,mstate,category,
    				function(data){dwr.engine.openInDownload(data);});
}
function initSH(){
   // initMinfos();
    dwr.util.addOptions("mstate", m_mstate);
}  
function querySHYE(){
	var mid=$("mid").value;
	var mstate=$("mstate").value;
	AdminZHService.querySHYE(1,mid,mstate,callbacks);
}
function querySHYES(pageNo){
	var mid=$("mid").value;
	AdminZHService.querySHYE(pageNo,mid,callbacks);
}
var callbacks=function(pageObj){
	 $("shinfo").style.display="";
	   var cellFuncs = [
	                    function(obj) { return obj.uid; },
	                    function(obj) { return obj.aid; },
	                    function(obj) { return obj.aname; },
	                    function(obj) { return obj.initdate; },
	                    function(obj) { return z_state[obj.state]; },
	                    function(obj) { return div100(obj.allBalance); },
	                    function(obj) { return div100(obj.balance); }];
	                currPage=pageObj.pageNumber; 
	   			paginationTable(pageObj,"resultList",cellFuncs,"","querySHYES");
};
//商户权限修改中商户号改变时
function midsChange(){
	 if(document.getElementById("mid")) document.getElementById("mid").value= $("smid").value;
}
function downloadJYZHLS(){
	var uid=$("mid").value;
	var aid=$("aid").value;
	var bdate=$("bdate").value;
	var edate=$("edate").value;
	var mstate=$("mstate").value;
	if(bdate=='') {
        alert("请选择起始日期！"); 
        return false; 
    }
    if(edate==''){
         alert("请选择结束日期！"); 
         return false; 
    }
    dwr.engine.setAsync(false);//把ajax调用设置为同步
    AdminZHService.downloadJYZHLS(uid,aid,bdate,edate,mstate,
    				function(data){dwr.engine.openInDownload(data);});
}
//查询账户交易流水
function queryJYZHLS(pageNo){
	var uid=$("mid").value;
	var aid=$("aid").value;
	var bdate=$("bdate").value;
	var edate=$("edate").value;
	var mstate=$("mstate").value;
	if(bdate=='') {
        alert("请选择起始日期！"); 
        return false; 
    }
    if(edate==''){
         alert("请选择结束日期！"); 
         return false; 
    }
    AdminZHService.queryJYZHLS(pageNo,uid,aid,bdate,edate,mstate,callBack);
}