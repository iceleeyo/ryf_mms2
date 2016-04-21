
function initAdminQuery(){
    PageService.getMinfosMap(function(m){
    	m_minfos=m;
    });
}
//下载
function downData(){
	var p={};
	p.transtate = $("transtate").value;//订单状态,2成功，3失败
	p.begindate = $("begindate").value;
	p.enddate = $("enddate").value;
	p.other_id = $("other_id").value;//其他条件，'all'所有，'card'同一卡号，'id'证件号，'phone'手机号
	p.other_id_num = $("other_id_num").value;//支付次数
    p.amount_num = $("amount_num").value;//单笔金额超过多少元
    p.isChecked=$("has_num").checked;//是否使用标识
    p.tradetype=$("tradetype").value;//交易类型
    
    dwr.engine.setAsync(false);//把ajax调用设置为同步
    RiskmanageService.downloadReturn(p,function(data){dwr.engine.openInDownload(data);})
}
 //验证查询条件数据的合法性
 function searchRiskLog(pageNo){
      var transtate = $("transtate").value;//订单状态,2成功，3失败
      var begindate = $("begindate").value;
      var enddate = $("enddate").value;
      var other_id = $("other_id").value;//其他条件，'all'所有，'card'同一卡号，'id'证件号，'phone'手机号
      var other_id_num = $("other_id_num").value;//支付次数
      var has_amount_num = $("has_num");//是否使用标识
      var amount_num = $("amount_num").value;//单笔金额超过多少元
      var tradetype = $("tradetype").value;//交易类型
      //去除查询条件的空格
      if (other_id!='') {other_id = other_id.trim();}
      if (other_id_num!='') {other_id_num = other_id_num.trim();}
      if (amount_num!='') {amount_num = amount_num.trim();}
      if(begindate==''||enddate==''){
         alert("请输入交易日期");
         return false;
      }
      var sysdate_b = begindate.substring(0,4)+"-"+begindate.substring(4,6)+"-"+begindate.substring(6,8);
      var sysdate_e = enddate.substring(0,4)+"-"+enddate.substring(4,6)+"-"+enddate.substring(6,8);
      var iDays = DateDiff(sysdate_b,sysdate_e);
      if(iDays>92){
         alert("查询跨度不得超过三个月");
         return false;
      }
      if(other_id != 'all' && !isFigure(other_id_num)){
         alert("支付次数必须为整数。");
         return false;
      }    
      if(has_amount_num.checked && amount_num==''){
          alert("单笔金额必须填写。");
          return false;
       }
      if(has_amount_num.checked && !isFigure(amount_num)){
         alert("单笔金额必须为正整数。");
         return false;
      }
      var bl = false;
      if(has_amount_num.checked){
    	  bl = true;
      }else{
    	  amount_num=0;
      }
      RiskmanageService.searchMlogs(pageNo,transtate,tradetype,begindate,enddate,other_id,other_id_num
              ,bl,amount_num,callback);     
 }
 //回调函数
 var callback = function(mloglist){
 	$("logDetail").style.display='none';
     $("resultList").style.display='';
    if(mloglist.length==0){
    	document.getElementById("mlogBody").appendChild(creatNoRecordTr(10));
 	    return false;
    }
           var cellFuncs = [
                  function(Mlog) { return Mlog.tseq; },
                  function(Mlog) { return Mlog.mid; },
                  function(Mlog) { return m_minfos[Mlog.mid]; },
                  function(Mlog) { return div100(Mlog.payAmount); },
                  function(Mlog) { return Mlog.sysDate; },
                  function(Mlog) { return shadowAcc(Mlog.payCard); },
                  function(Mlog) { return dell_tradetype(Mlog.transType); },
                  function(Mlog) { return shadowAcc(Mlog.payId); },
                  function(Mlog) { return Mlog.mobileNo=="null"?"无":shadowPhone(Mlog.mobileNo);},
                  function(Mlog) { return h_tstat[Mlog.tstat]; }
                 // function(Mlog){ return "<input type=\"button\" value=\"  详 细   \" onclick='showOrder("+Mlog.tseq+")'>"; }         
              ]                
       paginationTable(mloglist,"mlogBody",cellFuncs,"","searchRiskLog");
    $('downsubmit').disabled = false;
	}
 function back(){
 	  $("logDetail").style.display='none';
       $("resultList").style.display='';
 }
function dell_tradetype(type){
	var A;
  if (type == 3) A="信用卡支付";
  if (type == 11) A="对私代付";
  if (type == 12) A="对公代付";
  if (type == 15) A="对私代扣";
	  return A;
  
}
 function showOrder(tseq){
		 RiskmanageService.queryALogForRisk('tseq',tseq,function(list){
			    //Hlog = list[0];
			    setValue(list[0]);
	      });
 }
 function setValue(Hlog){ 
	 if(Hlog==null){alert("数据有误或异常，详细信息不存在！");return;}
	 $("logDetail").style.display='';
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
  }
		
        