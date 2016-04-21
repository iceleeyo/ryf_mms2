var w_state={};
var w_ptype={};
window.onload=function(){
	 PageService.getGatesMap(function(gateMap){
		 h_gate=gateMap;
	 });
	 PageParam.initAdminStatePtype(function(list){
		  w_state=list[0];
		  w_ptype=list[1];
	  })
}

function queryMX(){
	var accAname=$("acc_name").value;
	var oid=$("oid").value;
	if(oid==''){alert("请输入订单号");return ;}
	CompanyService.queryOrderDetails(accAname,oid,callback);
}
var TrOrders=null;
var Hlog=null;
function callback(list){
	if(list == null){
       alert( "没有符合条件的查询记录!" );
	    return;
	 }
	TrOrders=list[0];
	if(list[1]!=null)Hlog=list[1];
	if(TrOrders!=null){
	$("xiangxi").style.display = "";
	$("a_acc_name").innerHTML = TrOrders.accName;
	$("a_oid").innerHTML = TrOrders.oid;
	$("a_init_time").innerHTML = getNormalTime(TrOrders.initTime+"");
	$("a_tseq").innerHTML = TrOrders.tseq;
	$("a_trans_amt").innerHTML = div100(TrOrders.transAmt);
	$("a_trans_fee").innerHTML = div100(TrOrders.transFee);
	$("a_ptype").innerHTML = w_ptype[TrOrders.ptype];
	$("a_state").innerHTML = w_state[TrOrders.state];
	$("a_to_acc_name").innerHTML = TrOrders.toAccName;
	$("a_to_acc_no").innerHTML = TrOrders.toAccNo;
	$("a_sms_mobiles").innerHTML = TrOrders.smsMobiles;
	$("a_remark").innerHTML = TrOrders.remark;
	}
	
	if(Hlog!=null){
	$("showTable").style.display = "";
    $("v_mdate").innerHTML = Hlog.mdate;
    $("v_mid").innerHTML = Hlog.mid;
    $("v_oid").innerHTML = Hlog.oid;
    $("v_amount").innerHTML = div100 (Hlog.amount);
    $("v_type").innerHTML = h_type[Hlog.type];
    $("v_gateName").innerHTML = Hlog.gate==0 ? '' : h_gate[Hlog.gate];
    $("v_sys_date").innerHTML = Hlog.sysDate;
    $("v_init_sys_time").innerHTML = Hlog.initSysDate;
    $("v_tseq").innerHTML = Hlog.tseq;
    $("v_batch").innerHTML = Hlog.batch==0?"":Hlog.batch;
    $("v_fee_amt").innerHTML = div100(Hlog.feeAmt);
    $("v_tstat").innerHTML = h_tstat[Hlog.tstat];
    $("v_bk_flag").innerHTML = h_bk_flag[Hlog.bkFlag];
    $("v_refund_amt").innerHTML = div100(Hlog.refundAmt);
    $("v_bk_send").innerHTML = getStringTime(Hlog.bkSend);
    $("v_bk_recv").innerHTML = getStringTime(Hlog.bkRecv);
    $("v_bk_chk").innerHTML = h_bk_chk[Hlog.bkChk];
    $("v_bk_date").innerHTML = (Hlog.bkDate==null || Hlog.bkDate==0)?"&nbsp;":Hlog.bkDate;
    $("v_bk_seq1").innerHTML = Hlog.bk_seq1;
    $("v_bk_seq2").innerHTML = Hlog.bk_seq2;
    $("v_bk_resp").innerHTML = Hlog.bkResp;
	}
}

