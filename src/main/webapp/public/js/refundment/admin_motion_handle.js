var query_cash = {};
function  init(){
	initGateChannel();
	initMinfos();
	queryRefundMotions(1);
 dwr.util.addOptions("mstate", m_mstate);
}
function queryRefundMotions(pageNo){
	query_cash = {};
	var  mid = document.getElementById("mid").value;  
	var  stat = document.getElementById("stat").value;
    var  bdate = document.getElementById("date_begin").value; 
    var  edate = document.getElementById("date_end").value;
    var mstate=document.getElementById("mstate").value;
    var dateState=2; //dateState1为申请日期2为确认日期3为经办日期4为审核日期
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
    
	if(pageNo==-1){
		  dwr.engine.setAsync(false);//把ajax调用设置为同步
		  RefundmentService.downloadRefundMotions(mid,function(data){dwr.engine.openInDownload(data);});
		  
	}else{
		RefundmentService.queryRefundJBLogs(pageNo,mid,stat,null,dateState,bdate,edate,null,null,null,null,mstate,callBack2);
	}
}
var cellFuncs = [
	function(obj) {return obj.stat==1?"<input type='checkbox'  id='toCheck' name='refId' value='"+ obj.id +"'/>":"";},
	//function(obj) { return "<a href='javascript:query4OneRLog("+obj.id+","+obj.tseq+","+obj.sys_date+","+obj.stat+")' title='点击可查看详情'>"+obj.tseq+"</a>";},
	function(obj) { return obj.tseq+"";},
	function(obj) { return obj.mid;},
	function(obj) { return m_minfos[obj.mid];},
	function(obj) { return obj.org_oid; },
	function(obj) { return gate_route_map[obj.gid]; },
	function(obj) { return  h_gate[obj.gate];},
	function(obj) { return  div100(obj.ref_amt); },
    function(obj) { return div100(obj.merFee); },
	function(obj) { return  h_admin_refund_tstat_yxjb[obj.stat]; },
	function(obj) { return  formatDate(obj.sys_date);},
	function(obj) { return  formatDate(obj.req_date);},
	function(obj) {return   createTip(6,obj.refund_reason);},
	function(obj) {return "<input type=\"text\" maxlength=\"50\" style=\"width: 300px;border: 0\" id=\"reason_"+obj.id+"\">";}
];

var callBack2 = function(pageObj){
  $("detailResultList").style.display="";
  //把查询出来的对象缓存起来
  for(var i=0;i<pageObj.pageItems.length;i++){
	  var o = pageObj.pageItems[i];
	  query_cash[o.id] = o;
  }
	var  stat = document.getElementById("stat").value; 
  str = "<span style='float:left;color:blue'>&nbsp;申请退款总金额：<font color='red'><b>" + div100(pageObj.sumResult.refFeeSum)
  +"</b></font>  元&nbsp;&nbsp;&nbsp;&nbsp;";

  if(stat==1){//商户已提交
  str+= "&nbsp;&nbsp;<input type=\"button\" id=\"chooseAll\" name=\"chooseAll\" value=\"全 选\" onclick=\"checkAll('refId');\" />";
  str+= "&nbsp;&nbsp;<input type=\"button\" id=\"checkAccount\" name=\"checkAccount\" value=\"退款完成\" onclick=\"submitCheckAccount('');\" />";
  str+= "&nbsp;&nbsp;<input type=\"button\" id=\"cancelAll\" name=\"cancelAll\" value=\"撤销退款\" onclick=\"submitCheckAccount('retroversion');\" />";
  }
  str +="</span>";
  paginationTable(pageObj,"resultList",cellFuncs,str,"queryRefundMotions");
};

  function submitCheckAccount(action) {
      var refLogArray = new Array();
      var chkArray = document.getElementsByName("refId");
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
	        RefundmentService.retroversionRefund(refLogArray,callback4MotionHandle); 
	      }
	  } else {
	      if (confirm("确定退款完成?")) {
	              RefundmentService.verifyAccount(refLogArray,callback4MotionHandle);
	          }
	      }
	  }
  
	  function callback4MotionHandle(msg) {
	      if (msg != undefined) {
	          alert(msg);
	      }
	      //var page = $("pageChoose").value;
	      queryRefundMotions(1);
	  }


	 //查询详细
function query4OneRLog(id,tseq,sys_date,state){

	RefundmentService.queryRefundAndHlog(id,tseq,sys_date,function(list){
		if(list==null){
		   alert("没有查询到你要的数据...");
		   return false;
		}
		$("detailResultList").style.display="none";
		$("detail4One").style.display="";
	  var ref_amt_sum = list[0];
	  var oneHlogOrTlog = list[1];
	  var oneRefundLog = list[2];
	  $("v_id").innerHTML = oneRefundLog.id;
	  $("v_mid").innerHTML = oneRefundLog.mid;
	  $("v_org_oid").innerHTML = oneRefundLog.org_oid;
	  $("v_oid").innerHTML = oneRefundLog.oid; 
	  $("v_gate").innerHTML = h_gate[oneRefundLog.gate] ;
	  $("v_amount").innerHTML = div100( oneHlogOrTlog.amount ); // 原交易金额
	  $("v_fee_amt").innerHTML = div100( oneHlogOrTlog.feeAmt ); //--原系统手续费
	  $("v_refund_amt").innerHTML = div100( oneHlogOrTlog.refundAmt ) ;//--总申请退款金额	  
	  $("v_ref_amt").innerHTML = div100( oneRefundLog.ref_amt );   //-- 本次退款金额：
	  $("v_org_mdate").innerHTML = oneRefundLog.org_mdate==null ? "" : formatDate(oneRefundLog.org_mdate);
	  $("v_mdate").innerHTML =  oneRefundLog.mdate == null ? "" : formatDate(oneRefundLog.mdate);
	  $("v_req_date").innerHTML = oneRefundLog.req_date == 0 ? "" : oneRefundLog.req_date;
	  $("v_pro_date").innerHTML = (oneRefundLog.pro_date == null||oneRefundLog.pro_date == 0) ? "" : oneRefundLog.pro_date;
	  $("v_ref_amt_sum").innerHTML = div100( ref_amt_sum);
	  $("v_ref_date").innerHTML = (oneRefundLog.ref_date == null || oneRefundLog.ref_date == 0) ? "" : oneRefundLog.ref_date;//退款审核日期
	  $("v_refund_reason").innerHTML = oneRefundLog.refund_reason == null ? "": oneRefundLog.refund_reason ;
	  $("v_etro_reason").innerHTML = oneRefundLog.etro_reason == null ? "": oneRefundLog.etro_reason ;
	  $("v_reason").innerHTML = oneRefundLog.reason == null ? "": oneRefundLog.reason ;
	  $("v_name").innerHTML =m_minfos[oneRefundLog.mid];
	  $("v_tseq").innerHTML = oneRefundLog.tseq;
	  $("v_batch").innerHTML = oneRefundLog.batch == 0 ?"":oneRefundLog.batch;//--结算批次号：
	  $("v_sys_date").innerHTML = oneRefundLog.sys_date;
	  $("v_stat").innerHTML =h_admin_refund_tstat_yxjb[oneRefundLog.stat];
	});

} 
//退还手续费
function refundFeeAmt(){
	  if(!confirm("你确认退还手续费？"))return false;
	  var  loginMid = document.getElementById("loginMid").value;  
	  var  loginUid = document.getElementById("loginUid").value; 
	RefundmentService.RefundFeeAmt(loginMid,loginUid,function(msg){alert(msg);});
}