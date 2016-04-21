var w_state={};
var w_ptype={};
var p_state={};
var obj_;
function init(){
	initMinfos();
	  $("bdate").value = jsToday();
	  $("edate").value = jsToday();
	  PageParam.initAdminStatePtype(function(list){
		  dwr.util.removeAllOptions("state");
		  dwr.util.addOptions("state", {"":"请选择…"});
		  dwr.util.addOptions("mstate",m_mstate);
		  dwr.util.addOptions("state",list[2]);
		  
		  w_state=list[2];
		  p_state=list[3];
		  dwr.util.removeAllOptions("ptype");
		  dwr.util.addOptions("ptype", {"":"请选择…"});
		  dwr.util.addOptions("ptype",list[1]);
		  w_ptype=list[1];
	  });
	  
}

//查询账户交易明细
function queryMX(pageNo){
	var uid=$("mid").value;
	var ptype=$("ptype").value;
	var bdate=$("bdate").value;
	var edate=$("edate").value;
	var state=$("state").value;
	var oid=$("oid").value;
	var trans_f=$("trans_f").value;
	var mstate=$("mstate").value;
	if(bdate=='') {
        alert("请选择起始日期！"); 
        return false; 
    }
    if(edate==''){
         alert("请选择结束日期！"); 
         return false; 
    }
    AdminZHService.queryZHJYMX_(pageNo,uid,ptype,bdate,edate,state,oid,trans_f,mstate,callBack);
}
var currPage=1;
var callBack = function(pageObj){
	$("zhjymxcxTable").style.display="";
	   var cellFuncs = [
	                    function(obj) { return obj.uid; },
	                    function(obj) { return obj.aid; },
	                    function(obj) { return obj.aname; },
	                    function(obj) { return obj.oid; },
	                    function(obj){return obj.trans_flow;},
	                    function(obj) { return div100(obj.payAmt); },
	                    function(obj) { return div100(obj.transFee); },
	                   /* function(obj) { return div100(obj.transAmt); },*/
	                /*    function(obj) { return obj.sysDate; },
	                    function(obj) { return getStringTime(obj.sysTime); },*/
	                    function(obj) { return w_state[obj.state]; },
	                    function(obj) { return obj.pstate==0?"":p_state[obj.pstate]; },
	                    function(obj) { return w_ptype[obj.ptype]; },
	                    //function(obj) { return obj.toUid+"["+obj.toAid+"]"; },
//	                    function(obj) { return obj.toInfo; },
	                    function(obj) { return obj.sysDate+" "+getStringTime(obj.sysTime); },
//	                    function(obj) { return "<a href=\"#\" onclick=query4Remark('"+obj.oid+"'); class='box_detail'>详细说明</a>";; },
	                    function(obj) { var sysDate=obj.sysDate+" "+getStringTime(obj.sysTime);return "<a href=\"#\" onclick=\"queryOrderDetail('"+obj.uid+"','"+obj.aid+"','"+obj.aname+"','"+obj.oid+"','"+obj.trans_flow+"','"+div100(obj.transAmt)+"','"+div100(obj.transFee)+"','"+div100(obj.payAmt)+"','"+w_state[obj.state]+"','"+w_ptype[obj.ptype]+"','"+sysDate+"','"+obj.remark+"');\" class='box_detail'>订单详细</a>"; }
	                    //function(obj) { return getNormalTime(obj.initTime+""); },
	                    //function(obj) { return obj.tseq; },
	                ];
	                currPage=pageObj.pageNumber; 
	   			paginationTable(pageObj,"resultList",cellFuncs,"","queryMX");
  };
function query4Remark(oid){
	AdminZHService.query4Remark(oid,function(data){
		jQuery("#remarkDetail").wBox({title:"详细说明",show:true});//显示box
		$("i_remark").value=data;
		
	});
}
/****
 * 显示订单详细列表
 * @param pageObj
 * @returns
 */
var callBack_ZHJXMX = function(pageObj){
	 
	   var cellFuncs = [
	                    function(obj){return obj.oid;},
	                    function(obj){return w_state[obj.state];},
	                    function(obj) { return p_state[obj.pstate]; },
	                    function(obj) {return obj.toBkNo; },
	                    function(obj) {return obj.toBkName; },
	                    function(obj) {return obj.toAccName; },
	                    function(obj) {return obj.toAccNo; },
	                    function(obj) { return obj.auditRemark; },
//	                    function(obj) { return obj.trans_proof; },
	                ];	
	                currPage=pageObj.pageNumber; 
	   			paginationTable(pageObj,"resultList_",cellFuncs,"","queryZHJXMX");
};
/***
 * 查询账户交易明细
 * @param oid
 * @param trans_flow
 */
function queryZHJXMX(pageNo,oid,trans_flow){
	
	if(oid==undefined && trans_flow==undefined){
		oid=$("h_oid").value;
		trans_flow=$("h_trans_flow").value;
	}
	AdminZHService.queryZHJYMX(pageNo,oid,trans_flow,callBack_ZHJXMX);
}
/***
 * 查询订单详细
 * params -> 交易金额,交易手续费,实际金额
 */
function queryOrderDetail(uid,aid,aname,oid,trans_flow,trans_sum_amt,trans_sum_fee,trans_sum_payamt,state,ptype,sysDate,remark){
	$("trans_flow").innerHTML=trans_flow=='null'?"":trans_flow;
	$("trans_uid").innerHTML=uid;
	$("trans_aname").innerHTML=aname;
	$("trans_aid").innerHTML=aid;
	$("trans_oid").innerHTML=oid;
	$("sys_date").innerHTML=sysDate;
	$("trans_amt").innerHTML=trans_sum_payamt+"元";
	$("trans_fee").innerHTML=trans_sum_fee+"元";
	/*$("pay_amt").innerHTML=trans_sum_amt+"元";*/
	$("remark").innerHTML=remark; 
	$("trans_ptype").innerHTML=ptype;
	$("h_oid").value=oid;
	$("h_trans_flow").value=trans_flow=='null'?"":trans_flow;
	jQuery("#hlogDetail_").wBox({title:"详细说明",show:true});//显示box
	queryZHJXMX(1,oid,trans_flow=='null'?"":trans_flow);
	
}

//下载查询结果

function downloadMX(){
	var uid=$("mid").value;
	var ptype=$("ptype").value;
	var bdate=$("bdate").value;
	var edate=$("edate").value;
	var state=$("state").value;
	var oid=$("oid").value;
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
    AdminZHService.downloadMX(uid,ptype,bdate,edate,state,oid,mstate,
    				function(data){dwr.engine.openInDownload(data);});
}

