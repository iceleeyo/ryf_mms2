
var query_cash={};
function Onit(){
	initGateChannel();
	initMinfos();
	dwr.util.setValues({
		state:0
	});
	OnlineRefundService.queryLJRefundJBLogs(1,"",0,"","","","","",callBack2);
 dwr.util.addOptions("mstate", m_mstate);//????
}

function downOnlineRefundMotions(){
	var  mid = document.getElementById("mid").value; 
	 dwr.engine.setAsync(false);//把ajax调用设置为同步
	 OnlineRefundService.downOnlineRefundMotions(mid,function(data){dwr.engine.openInDownload(data);});
}

function queryLJRefundMotions(pageNo){
	var  mid = document.getElementById("mid").value;  
	var  state = document.getElementById("state").value;
    var  bdate = document.getElementById("date_begin").value; 
    var  edate = document.getElementById("date_end").value;
    var mstate=document.getElementById("mstate").value;
    var tseq=document.getElementById("tseq").value;
    var gateRouteId=$("gateRouteId").value;
    if(mid !='' && !isFigure(mid)){
        alert("商户号只能是整数!");
        $("mid").value = '';
        return false; 
    }
    if(mid!=""&&m_minfos[mid]==undefined){
        alert("商户号不存在!");
        $("mid").value = '';
        return false; 
    }
    if(bdate!=""&&edate==""){
   	 alert("请选择一个时间段!");
   	 return false;
    }
    
    OnlineRefundService.queryLJRefundJBLogs(pageNo,mid,state,bdate,edate,mstate,tseq,gateRouteId,callBack2);
}

var cellFuncs = [
             	function(obj) { return (obj.onlineRefundState!==2)?"<input type='checkbox'  id='toCheck' name='refId' value='"+ obj.id +"'/>":"";},
             	function(obj) { return obj.id;},
             	function(obj) { return obj.tseq+"";},
             	function(obj) { return obj.onlineRefundId;},
             	function(obj) { return obj.mid;},
             	function(obj) { return m_minfos[obj.mid];},
             	function(obj) { return obj.org_oid; },
             	function(obj) { return gate_route_map[obj.gid]; },
             	function(obj) { return  h_gate[obj.gate];},
             	function(obj) { return  div100(obj.ref_amt); },
                 function(obj) { return div100(obj.merFee); },
             	function(obj) { return  obj.onlineRefundState==0&&obj.stat==6?"撤销退款":h_mer_Onlinerefund_tstat[obj.onlineRefundState]; },
             	function(obj) { return obj.onlineRefundReason;},
             	function(obj) { return  formatDate(obj.sys_date);},
             	function(obj) { return  formatDate(obj.req_date);},
             	function(obj) {return   createTip(6,obj.refund_reason);},
             	function(obj) {return "<input type=\"text\" maxlength=\"50\" style=\"width: 200px;border: 0\" id=\"reason_"+obj.id+"\">";},
             	function(obj) {return  (obj.onlineRefundState==3||obj.onlineRefundState==4)?"<input type=\"button\" value = \"人工退款经办\" name=\"SUCCESS\"  onclick=\"success('"+obj.id+"')\">":"";}
             	];
var callBack2 = function(pageObj){
	  $("detailResultList").style.display="";
	  //把查询出来的对象缓存起来
	  for(var i=0;i<pageObj.pageItems.length;i++){
		  var o = pageObj.pageItems[i];
		  query_cash[o.id] = o;
	  }
	  str = "<span style='float:left;color:blue'>&nbsp;申请退款总金额：<font color='red'><b>" + div100(pageObj.sumResult.refFeeSum)
	  +"</b></font>  元&nbsp;&nbsp;&nbsp;&nbsp;";
	  str+= "&nbsp;&nbsp;<input type=\"button\" id=\"chooseAll\" name=\"chooseAll\" value=\"全 选\" onclick=\"checkAll('refId');\" />";
	  str+= "&nbsp;&nbsp;<input type=\"button\" id=\"checkAccount\" name=\"checkAccount\" value=\"联机退款\" onclick=\"submitCheckAccount('');\" />";
	  str+= "&nbsp;&nbsp;<input type=\"button\" id=\"cancelAll\" name=\"cancelAll\" value=\"撤销退款\" onclick=\"submitCheckAccount('retroversion');\" />";
	  str+= "&nbsp;&nbsp;<input type=\"button\" id=\"labour_jb\" name=\"labour_jb\" value=\"人工经办\" onclick=\"submitCheckAccount('labour');\" />";
	  str +="</span>";
	  paginationTable(pageObj,"resultList",cellFuncs,str,"queryLJRefundMotions");
	};

function submitCheckAccount(action) {
    var refLogArray = new Array();
    var chkArray = document.getElementsByName("refId");//获取被选中的列
    var index = 0;
    for (var i = 0 ; i < chkArray.length; i++){
    if(chkArray[i].checked){
      var RefundLog = query_cash[chkArray[i].value];
      if(action == "retroversion"){
             var reasonValue = document.getElementById("reason_" + chkArray[i].value).value.replace(/(^\s*)|(\s*$)/g, "");
             if(reasonValue == ''){
                 alert("请填写经办撤销退款的原因!");
                 return false;
             }
             RefundLog.etro_reason = reasonValue; 
         }
         refLogArray[index] = RefundLog;
         index ++;
    	}
    }
	  if (index == 0){
	      alert("请至少选中一条记录!");
	      return false;  
	  }
	  if (action == "retroversion"){
	    if (confirm("确定撤销退款?")) {
	    	OnlineRefundService.retroversionRefund(refLogArray,callback4MotionHandle); 
	      }
	    }else if(action == "labour"){ ///人工经办退款
	    	if(confirm("退款单将进入经办审核，请确认是否人工经办？")){
	    		OnlineRefundService.verifyAccount(refLogArray,callback4MotionHandle);
	    	}
	    }else {
	    	
	      if (confirm("确定联机退款?")) {
	    	  OnlineRefundService.OnlineRefund(refLogArray,callback4MotionHandle);
	          }
	      }
	  
	  }

//状态同步
function statesynchro(obj){
	 var index = 0;
	 var refLogArray = new Array();
	 var RefundLog = query_cash[obj];
	 refLogArray[index] = RefundLog;
		      if (confirm("确定状态同步?")) {
		    	  OnlineRefundService.OnlinerefundStateSynchro(refLogArray,callback4MotionHandle);
		    }
}
//更新列表
function callback4MotionHandle(msg) {
    if (msg != undefined) {
        alert(msg);
    }
    queryLJRefundMotions(1);
}

//人工退款经办、人工银联退款经办
function success (obj){
	 var index = 0;
	 var refLogArray = new Array();
	 var RefundLog = query_cash[obj];
	 refLogArray[index] = RefundLog;
		      if (confirm("确定人工退款经办完成?")) {
		    	  OnlineRefundService.verifyAccount(refLogArray,callback4MotionHandle);
		          }
	
}

//搜索支付渠道
var gate_route_map2 =  {};
function gateRouteIdList(){
		 var gateName = $("gateName").value;
		 PageService.getGRouteByName(gateName,function(m){
			 gate_route_map2=m;
			 dwr.util.removeAllOptions("gateRouteId");
			 dwr.util.addOptions("gateRouteId",{'':'全部...'});
			dwr.util.addOptions("gateRouteId", gate_route_map2);
		 });
		 
	 }
