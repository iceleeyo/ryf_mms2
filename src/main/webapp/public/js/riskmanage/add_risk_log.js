 function addRiskLog(){
	 var tseq = $("add_remarks_tseq").value;
     var remarks = $("add_remarks").value;
     if(remarks.length>190){
     	 alert("备注原因长度过长");
     	 return false;
      }else if(!checkIllegalChar(remarks)){
          return false;
      }else{
         RiskmanageService.addRiskLog(tseq,remarks,function(msg){
	                    if(msg=='ok'){
	                        alert("录入成功");
	                        $("submit_id").disabled = true;
	                     }else{
	                         alert(msg);
	                     }
	                    });
      }
 }

     
 var h_hlogs = {}
 function setValue(Hlog){
 	$("logDetail").style.display='';
     $("addRiskRemarksTB").style.display=''; 
     $("resultList").style.display='none';
     $("v_mdate").innerHTML = Hlog.mdate;
     $("v_mid").innerHTML = Hlog.mid;
     $("v_midName").innerHTML = m_minfos[Hlog.mid];
     $("v_oid").innerHTML = Hlog.oid;
     $("v_amount").innerHTML = div100 (Hlog.amount);
     $("v_type").innerHTML = m_types[Hlog.type];
     $("v_gate").innerHTML = Hlog.gate==0 ? '' : Hlog.gate;
     $("v_gateName").innerHTML = Hlog.gate==0 ? '' : m_gates[Hlog.gate];
     $("v_sys_date").innerHTML = Hlog.sysDate;
     $("v_init_sys_time").innerHTML = Hlog.initSysDate;
     $("v_tseq").innerHTML = Hlog.tseq;
     $("v_batch").innerHTML = Hlog.batch==0?"":Hlog.batch;
     $("v_fee_amt").innerHTML = div100(Hlog.feeAmt);
     $("v_tstat").innerHTML = m_states[Hlog.tstat];
     $("v_bk_flag").innerHTML = m_bk_flags[Hlog.bkFlag];
     $("v_refund_amt").innerHTML = div100(Hlog.refundAmt);
     $("v_sys_time").innerHTML = getStringTime(Hlog.sysTime);
     $("v_bk_send").innerHTML = getStringTime(Hlog.bkSend);
     $("v_bk_recv").innerHTML = getStringTime(Hlog.bkRecv);
     $("v_bk_chk").innerHTML = m_bk_chks[Hlog.bkChk];
     $("v_bk_date").innerHTML = (Hlog.bkDate==null || Hlog.bkDate==0)?"&nbsp;":Hlog.bkDate;
     $("v_bk_seq1").innerHTML = Hlog.bk_seq1;
     $("v_bk_seq2").innerHTML = Hlog.bk_seq2;
     $("v_bk_resp").innerHTML = Hlog.bkResp;
     $("v_bank_fee").innerHTML = div100(Hlog.bankFee);
     if(Hlog.tstat==2){
     	$("submit_id").disabled = false;
     	$("add_remarks").disabled=false;
     	$("add_remarks").value="";
     }else{
    	 $("add_remarks").disabled=true;
    	 $("add_remarks").value="只有成功的交易才可以提交为风险交易!";
         $("submit_id").disabled = true;
     }
 }
 function showLogInfo(tseq){
     var Hlog = h_hlogs[tseq];
     $("logDetail").style.display='';
     $("addRiskRemarksTB").style.display=''; 
     $("resultList").style.display='none';
     $("add_remarks_tseq").value = tseq;
    // $("add_remarks").value="";
    
    dwr.util.setValues({add_remarks:''});
     setValue(Hlog);
 }

 function queryAlog(){
     var k = $("k").value;
     if(k==''){
         alert("请输入关键字查询");
         return;
     }
     var t = $("t").value;
    
     RiskmanageService.queryALogForRisk(t,k,function(alist){
         if(alist.length==0){
             $("logDetail").style.display='none';
             $("addRiskRemarksTB").style.display='none'; 
             $("resultList").style.display='none';
             $("noMsg").style.display='';
             return;
         }else if(alist.length==1){
         	$("logDetail").style.display='';
             $("addRiskRemarksTB").style.display=''; 
             $("resultList").style.display='none';
             $("noMsg").style.display='none';
             Hlog = alist[0];
             $("add_remarks_tseq").value = Hlog.tseq;
             setValue(Hlog);
         }else{
         	$("logDetail").style.display='none';
             $("addRiskRemarksTB").style.display='none'; 
             $("noMsg").style.display='none';
             $("resultList").style.display='';
             var dtable = $("resultListTB");
             dtable.style.display='';
             dwr.util.removeAllRows("resultListTB");
             for(var i = 0; i < alist.length; i++){
             	Hlog = alist[i];
             	h_hlogs[Hlog.tseq] = Hlog;
             	var elTr = dtable.insertRow(-1);
                 elTr.setAttribute("align", "center");
                 var tseqTd = elTr.insertCell(-1);
                 tseqTd.innerHTML = Hlog.tseq;
                 var tseqTd2 = elTr.insertCell(-1);
                 tseqTd2.innerHTML = Hlog.oid;
                 var tseqTd3 = elTr.insertCell(-1);
                 tseqTd3.innerHTML = Hlog.bk_seq1;
                 var tseqTd4 = elTr.insertCell(-1);
                 tseqTd4.innerHTML = Hlog.bk_seq2;
                 var tseqTd5 = elTr.insertCell(-1);
                 tseqTd5.innerHTML = Hlog.mid;
                 var tseqTd6 = elTr.insertCell(-1);
                 tseqTd6.innerHTML = div100(Hlog.amount);
                 var tseqTd7 = elTr.insertCell(-1);
                 var state = Hlog.tstat;
                 var stateStr = '';
                 if(state==0) stateStr = '初始状态'
                 if(state==1) stateStr = '待支付..'
                 if(state==2) stateStr = '支付成功'
                 if(state==3) stateStr = '支付失败'
                 tseqTd7.innerHTML = stateStr;
                 var tseqTd8 = elTr.insertCell(-1);
                 tseqTd8.innerHTML = "<input type=\"button\" value=\" 确 认  \" onclick=\"showLogInfo("+Hlog.tseq+")\">";
             }
         }
     });
 }         
 var a = [{name:'',text:'全部'}];
var m_gates = {};
var m_states = {};
var m_types = {};
var m_minfos = {};
var m_author_types = {}
var m_bk_flags = {};
var m_bk_chks = {};
        //管理员查询页面	初始化
function initAdminQuery(table){
	// t = table;
	 role = 'admin';
	 PageParam.initAdminQueryPage(function(m){
		 m_gates = m[0];
		 m_states = m[1];
		 m_types = m[2];
		 m_minfos = m[3];
		 m_author_types = m[4];
		 m_bk_flags = m[5];
		 m_bk_chks = m[6];
	     dwr.util.removeAllOptions("gate");
         dwr.util.addOptions("gate", a,'name','text',{escapeHtml:false});
         dwr.util.addOptions("gate", m_gates);
         dwr.util.removeAllOptions("tstat");
         dwr.util.addOptions("tstat", a,'name','text',{escapeHtml:false});
         dwr.util.addOptions("tstat", m_states);
         dwr.util.removeAllOptions("type");
         dwr.util.addOptions("type", a,'name','text',{escapeHtml:false});
         dwr.util.addOptions("type", m_types);
	 });
}