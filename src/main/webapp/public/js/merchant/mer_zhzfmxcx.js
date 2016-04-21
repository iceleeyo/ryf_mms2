var zh_flag =null;
var w_state={};
var w_ptype={};
var obj_;
function init(){
	initMinfos();
	if(zh_flag==null){
		 MerZHService.getZH2(function(zh){
			 dwr.util.addOptions("aid", zh);
			zh_flag=zh;
		 })
	    }
  $("bdate").value = jsToday();
	  $("edate").value = jsToday();
	  PageParam.initAdminStatePtype(function(list){
		/*  dwr.util.removeAllOptions("state");
		  dwr.util.addOptions("state", {"":"请选择…"});
		  dwr.util.addOptions("state",list[0]);*/
		  
//		  w_state=list[0];
		  dwr.util.removeAllOptions("ptype");
		  dwr.util.addOptions("ptype", {"":"请选择…"});
		  dwr.util.addOptions("ptype",list[1]);
		  w_ptype=list[1];
	  })
	  
}

//查询账户交易明细
function queryMX(pageNo){
//	var uid=$("mid").value;
	//var aid=$("aid").value;
	var ptype=$("ptype").value;
	var bdate=$("bdate").value;
	var edate=$("edate").value;
//var state=$("state").value;
	var oid=$("oid").value;
	if(bdate=='') {
        alert("请选择起始日期！"); 
        return false; 
    }
    if(edate==''){
         alert("请选择结束日期！"); 
         return false; 
    }
    MerZHService.queryZHSZMX(pageNo,ptype,bdate,edate,oid,callBack);
}
var currPage=1;
var callBack = function(pageObj){
	$("zhjymxcxTable").style.display="";
	   var cellFuncs = [
	                 function(obj) { return obj.uid; },
	                    function(obj) { return obj.aid; },
//	                    function(obj) { return obj.oid; },
	                   function(obj) { return obj.oid; },
	                   function(obj) { return obj.trans_flow; },
	                   function(obj) { return getNormalTime(obj.init_time+""); },
	                   function(obj) { return w_ptype[obj.ptype]; },
	                   function(obj){return obj.recPay==0?div100(obj.amt):"";},
	                   function(obj){return obj.recPay==1?div100(obj.amt):"";},
	                   function(obj){return div100(obj.allBalance);},
	                   
	                ];	
	                currPage=pageObj.pageNumber; 
	   			paginationTable(pageObj,"resultList",cellFuncs,"","queryMX");
  };

//下载查询结果

function downloadMX(){
	var ptype=$("ptype").value;
	var bdate=$("bdate").value;
	var edate=$("edate").value;
	var oid=$("oid").value;
	if(bdate=='') {
        alert("请选择起始日期！"); 
        return false; 
    }
    if(edate==''){
         alert("请选择结束日期！"); 
         return false; 
    }
    dwr.engine.setAsync(false);//把ajax调用设置为同步
    MerZHService.downloadLS_SZ(ptype,bdate,edate,oid,
			function(data){dwr.engine.openInDownload(data);});
}

