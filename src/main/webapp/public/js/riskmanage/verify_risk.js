
function initParams(){
	initGateChannel();
	initMinfos();
}

function editRiskLogC(){
    var remarks = $("edit_remarks_c").value;
    if(remarks=='') {alert("请输入撤销原因");return}

    if(remarks.length>190){
        alert("撤销原因长度过长");return;
       }
    
    var tseq = $("select_edit_c_id").value;

    
    RiskmanageService.editRiskLog(tseq,remarks,1,function(msg){
        if(msg=='ok'){
        	alert("操作完成");
        	queryRisklog();
        }else{alert(msg);}

    });
}

function editRiskLogS(){
	var remarks = $("edit_remarks_s").value;

	
	if(remarks=='') {alert("请输入冻结原因");return};

	 if(remarks.length>190){
         alert("冻结原因长度过长");return;
        }
    var amount = $("edit_remarks_s_amount").value;
    if(amount=='') {alert("请输入冻结金额");return};

    if(!isTwoPointNum(amount)){alert("冻结金额格式错误");return};
    var f = amount*100;
    var f_x = $("v_amount").innerHTML;
    if(f_x*100 < f ){alert("冻结金额不能大于原订单金额");return};

    var tseq = $("select_edit_s_id").value;
    RiskmanageService.editRiskLog(tseq,remarks,f,2,function(msg){
        if(msg=='ok'){
            alert("操作完成");
            queryRisklog();
        }else{alert(msg);}
    }
    );
}

function editRiskLog(t){
    if(seleTseq==0){
    	 alert("请至少选择一条记录进行操作!");
    	
    	 return;
     }
    if(t=='c'){
    	if (confirm("确认要删除")){
    		RiskmanageService.removeRiskLog(seleTseq,function(msg){
    			if(msg=='ok'){
    	            alert("操作成功！");
    	            queryRisklog();
    			}else{
    				alert(msg);
    			}
    		});
        }
    }
    if(t=='s'){
    	RiskmanageService.queryALogForRisk('tseq',seleTseq,function(list){
            $("editRiskRemarksTB_S").style.display=''; 
            $("editRiskRemarksTB_C").style.display='none'; 
            $("logDetail").style.display='';
            $("select_edit_s_id").value = seleTseq;
            setValue(list[0]);
        });
    }
    if(t=='t'){
        editRiskRemarksTB_C 
         RiskmanageService.queryALogForRisk('tseq',seleTseq,function(list){
             setValue(list[0]);

         });
    }
    if(t=='x'){
        editRiskRemarksTB_C 
         RiskmanageService.queryALogForRisk('tseq',seleTseq,function(list){
             setValue(list[0]);

         });
    }
}
var h_risk_logs = {}
function setValue(Hlog){
    $("v_mdate").innerHTML = Hlog.mdate;
    $("v_mid").innerHTML = Hlog.mid;
    $("v_midName").innerHTML = m_minfos[Hlog.mid];
    $("v_oid").innerHTML = Hlog.oid;
    $("v_amount").innerHTML = div100 (Hlog.amount);
    $("v_type").innerHTML = h_type[Hlog.type];
    $("v_gate").innerHTML = Hlog.gate==0 ? '' : Hlog.gate;
    $("v_gateName").innerHTML = Hlog.gate==0 ? '' : h_gate[Hlog.gate];
    $("v_sys_date").innerHTML = Hlog.sysDate;
    $("v_init_sys_time").innerHTML = Hlog.initSysDate;
    $("v_tseq").innerHTML = Hlog.tseq;
    $("v_batch").innerHTML = Hlog.batch==0?"":Hlog.batch;
    $("v_fee_amt").innerHTML = div100(Hlog.feeAmt);
    $("v_tstat").innerHTML = h_tstat[Hlog.tstat];
    $("v_bk_flag").innerHTML = h_bk_flag[Hlog.bkFlag];
    $("v_refund_amt").innerHTML = div100(Hlog.refundAmt);
    $("v_sys_time").innerHTML = getStringTime(Hlog.sysTime);
    $("v_bk_send").innerHTML = getStringTime(Hlog.bkSend);
    $("v_bk_recv").innerHTML = getStringTime(Hlog.bkRecv);
    $("v_bk_chk").innerHTML = h_bk_chk[Hlog.bkChk];
    $("v_bk_date").innerHTML = (Hlog.bkDate==null || Hlog.bkDate==0)?"&nbsp;":Hlog.bkDate;
    $("v_bk_seq1").innerHTML = Hlog.bk_seq1;
    $("v_bk_seq2").innerHTML = Hlog.bk_seq2;
    $("v_bk_resp").innerHTML = Hlog.bkResp;
    $("v_bank_fee").innerHTML = div100(Hlog.bankFee);
    
}

var seleTseq = 0;
function initSelectTseq(tseq){
	seleTseq = tseq;
}

function queryRisklog(){
	seleTseq = 0;
    //var rstate = $("rstate").value;
    var rstate = 0;
    
    var bd = $("sys_date_begin").value;
    var ed = $("sys_date_end").value;
    if(bd==''||ed==''){alert("请填写录入日期!");return;}
    var tseq = $("tseq").value;
    RiskmanageService.queryRiskLog(rstate,bd,ed,tseq,function(alist){
    $("resultList").style.display='';
         dwr.util.removeAllRows("resultListTB");
         if(alist.length==0){
        	 
        	  $("resultListTB").appendChild(creatNoRecordTr(5));
              $("sub_but_id").style.display='none';
              $("editRiskRemarksTB_S").style.display='none'; 
              $("editRiskRemarksTB_C").style.display='none'; 
              $("logDetail").style.display='none';
      	      return ;
          }else{
            $("sub_but_id").style.display='';
            $("editRiskRemarksTB_S").style.display='none'; 
            $("editRiskRemarksTB_C").style.display='none'; 
            $("logDetail").style.display='none';
            var dtable = $("resultListTB");
            dwr.util.removeAllRows("resultListTB");
            for(var i = 0; i < alist.length; i++){
            	RiskLog = alist[i];
                h_risk_logs[RiskLog.tseq] = RiskLog;
                var elTr = dtable.insertRow(-1);
                elTr.setAttribute("align", "center");
                var tseqTd = elTr.insertCell(-1);
                var state = RiskLog.rstate;
                if(state==0 ){
                   tseqTd.innerHTML = "<input type=\"radio\" name='rtseq' onclick='initSelectTseq("+RiskLog.tseq+")'/>";
                }else{
                	tseqTd.innerHTML = "";
                }
                var tseqTd2 = elTr.insertCell(-1);
                tseqTd2.innerHTML = RiskLog.tseq;
                var tseqTd7 = elTr.insertCell(-1);
                var stateStr = '';
                if(state==0) stateStr = '风险录入'
                if(state==1) stateStr = '风险撤销'
                if(state==2) stateStr = '风险冻结'
                if(state==3) stateStr = '风险解冻'
                if(state==4) stateStr = '风险确认'
                tseqTd7.innerHTML = stateStr;
                var tseqTd8 = elTr.insertCell(-1);
                tseqTd8.innerHTML = RiskLog.addDate;
                var tseqTd9 = elTr.insertCell(-1);
                tseqTd9.innerHTML = createTip(30,RiskLog.addRemarks)  ;
            }
        }
    });
}