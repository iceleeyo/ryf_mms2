var state_map={0:"风险录入",1:"风险撤销",2:"风险冻结",3:"风险解冻",4:"风险确认"};

function initParams(){
	initGateChannel();
	initMinfos();
}

function editRiskLogC(){
	 var remarks = $("edit_remarks_c").value;
     if(remarks=='') {alert("请输入确认原因");return}

     if(remarks.length>190){
         alert("确认原因长度过长");return;
        }
     var tseq = $("select_edit_c_id").value;
     RiskmanageService.editRiskLogC(tseq,remarks,function(msg){
         if(msg=='ok'){
             alert("操作完成");
             queryRisklog();
         }else{alert(msg);}

     });
 }

 function editRiskLogS(){
     var remarks = $("edit_remarks_s").value;
     if(remarks=='') {alert("请输入解冻原因");return};

     if(remarks.length>190){
         alert("解冻原因长度过长");return;
        }
     
     var amount = $("edit_remarks_s_amount").value;
     if(amount=='') {alert("请输入解冻金额");return};

     if(!isTwoPointNum(amount)){alert("解冻金额格式错误");return};
     
     var relieveAmt = amount*100;               
     if(riskAmt < relieveAmt){alert("解冻金额不能大于冻结金额");return};
     var tseq = $("select_edit_s_id").value;
     RiskmanageService.editRiskLog(tseq,remarks,relieveAmt,3,function(msg){
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
          RiskmanageService.queryALogForRisk('tseq',seleTseq,function(list){
              $("editRiskRemarksTB_C").style.display=''; 
              $("logDetail").style.display='';
              $("editRiskRemarksTB_S").style.display='none'; 
              $("select_edit_c_id").value = seleTseq;
              setValue(list[0]);
          });
     }
     if(t=='s'){
         RiskmanageService.queryALogForRisk('tseq',seleTseq,function(list){
             if(list.length==0){alert("没有符合的数据！"); return false;}
             $("editRiskRemarksTB_S").style.display=''; 
             $("editRiskRemarksTB_C").style.display='none'; 
             $("logDetail").style.display='';
             $("select_edit_s_id").value = seleTseq;
             setValue(list[0]);
         });
     }
 }
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
     $("v_bk_flag").innerHTML = Hlog.bkFlag==1?"正常":"初始状态";
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
 var riskAmt=0;
 function initSelectTseq(tseq,riskAmount){
     seleTseq = tseq;
     riskAmt=riskAmount;
 }
 
 function queryRisklog(){
     seleTseq = 0;
     var rstate = $("rstate").value;
     var bd = $("sys_date_begin").value;
     var ed = $("sys_date_end").value;
     if(bd==''||ed==''){alert("请填写录入日期!");return;}
     var tseq = $("tseq").value;
     dwr.util.removeAllRows("resultListTB");
     RiskmanageService.queryRiskLog(rstate,bd,ed,tseq,function(alist){
   	     $("resultList").style.display="";
          if(alist.length==0){
        	  $("resultListTB").appendChild(creatNoRecordTr(7));
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
             
             dwr.util.removeAllRows("resultListTB");
             var cellFuns=[
                          function(obj){
                        	  if(obj.rstate==2){
                        		  return "<input type=\"radio\" name='rtseq' onclick='initSelectTseq("+obj.tseq+","+obj.riskAmount+")'/>";
                        	  }else{
                        		  return "";
                        	  }
                          },
                          function(obj){return obj.tseq;},
                          function(obj){return state_map[obj.rstate];},
                          function(obj){return obj.addDate;},
                          function(obj){return div100(obj.riskAmount);},
                          function(obj){return obj.verifyRemarks;},
                          function(obj){return obj.confirmRemarks==null?"":obj.confirmRemarks;}
                         ];
             dwr.util.addRows("resultListTB",alist,cellFuns,{escapeHtml:false});
         }
     });
 }           
            
function goBack(t){
	$("logDetail").style.display="none";
	$("editRiskRemarksTB_"+t).style.display="none";
}