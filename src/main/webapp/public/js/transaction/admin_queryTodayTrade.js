//公共代码
        function init(){
            dwr.util.addOptions("tstat", h_tstat);
            dwr.util.addOptions("type", h_type);
            dwr.util.addOptions("mstate", m_mstate);
            initGateChannel1();
            initMinfos();
            
        }  
        var mid,oid,gate,tstat,type,gateRouteId,bkseq,tseq,mstate,begintrantAmt,endtrantAmt;
        function judgeCondition(){
             mid = $("mid").value;
             oid = $("oid").value;
             gate = $("gate").value;
             tstat = $("tstat").value;
             type = $("type").value;
             gateRouteId = $("gateRouteId").value;
             bkseq = $("bkseq").value;             
             tseq =  $("tseq").value;
             mstate=$("mstate").value;
             p15=$("p15").value;
             begintrantAmt=$("begintrantAmt").value;
             endtrantAmt=$("endtrantAmt").value;
            if (tseq !=    "") {
                tseq = tseq.trim();
                if (!isNumber(tseq)) {
                    alert("电银流水号必须是数字!");
                    $("tseq").value = '';
                    $("tseq").focus();
                    return false;
                 }
            }
            if (begintrantAmt !=    "") {
            	begintrantAmt = begintrantAmt.trim();
                if (!isPlusInteger(begintrantAmt)) {
                    alert("金额必须是正整数!");
                    $("begintrantAmt").value = '';
                    $("begintrantAmt").focus();
                    return false;
                 }
            }
            if (endtrantAmt !=    "") {
            	endtrantAmt = endtrantAmt.trim();
                if (!isPlusInteger(endtrantAmt)) {
                    alert("金额必须是正整数!");
                    $("endtrantAmt").value = '';
                    $("endtrantAmt").focus();
                    return false;
                 }
            }
            return true;
        }
        //当天交易查询
        function queryMerToday(pageNo){
        	if(!judgeCondition())return;
        	QueryMerTodayService.queryMerToday(pageNo,mid,gate,tstat, type,tseq,oid,gateRouteId,bkseq,mstate,begintrantAmt,endtrantAmt,p15,callBack2);
        }
      //当天交易查询下载
        function downloadToday(){
        	if(!judgeCondition())return;
            dwr.engine.setAsync(false);//把ajax调用设置为同步
            QueryMerTodayService.downloadToday(mid,gate,tstat, type,tseq,oid,gateRouteId,bkseq,mstate,begintrantAmt,endtrantAmt,p15,
            			function(data){dwr.engine.openInDownload(data);});
        }
       //查询的回调函数
       var callBack2 = function(pageObj){ 
    	   $("merTodayTable").style.display="";
    	   dwr.util.removeAllRows("resultList");
    	   if(pageObj==null){
        	   document.getElementById("resultList").appendChild(creatNoRecordTr(13));
    		   return;
    	   }  
	        var cellFuncs = [
	                         function(obj) { return "<a href=\"#\" onclick=query4Detail('"+obj.tseq+"'); class='box_detail'>"+obj.tseq+"</a>"; },
	                         function(obj) { return obj.mid; },
	                         function(obj) { return m_minfos[obj.mid]; },
	                         function(obj) { return obj.oid; },
	                         function(obj) { return formatDate(obj.mdate); },
	                         function(obj) { return div100(obj.amount); },
	                         function(obj) { return h_tstat[obj.tstat]; },
	                         function(obj) { return h_type[obj.type]; },
	                         function(obj) { return h_gate1[obj.gate]; },
	                         function(obj) { return gate_route_map1[obj.gid]; },
	                         function(obj) { return div100(obj.feeAmt); },
	                         function(obj) { return formatDate(obj.sysDate)+" "+getStringTime(obj.sysTime);},
	                         function(obj) { return createTip(8,obj.bk_seq1); },
	                         function(obj) { return obj.p15; },
	                         function(obj) { return obj.error_msg==''? createTip(8,obj.errorCode):createTip(8,obj.error_msg);}
	                     ];

	               str ="<span class='pageSum'>&nbsp;交易总金额：<font color='red'><b>" + div100(pageObj.sumResult["amtSum"])
	              		     +"</b></font>  元</span> <span class='pageSum'>&nbsp;&nbsp;系统手续费总金额：<font color='red'><b>"+ div100(pageObj.sumResult["sysAmtFeeSum"])+"</b></font>  元</span>";	             
	              paginationTable(pageObj,"resultList",cellFuncs,str,"queryMerToday");
          };
     // 查询详细信息
        function query4Detail(tseq) {
        	QueryMerTodayService.queryLogByTseq(tseq,1,function(Hlog){   
               $("v_mdate").innerHTML = formatDate(Hlog.mdate);
               $("v_mid").innerHTML = Hlog.mid;
               $("v_midName").innerHTML = m_minfos[Hlog.mid];
               
               $("v_oid").innerHTML = Hlog.oid;
               $("v_amount").innerHTML = div100 (Hlog.amount);
               $("v_type").innerHTML = h_type[Hlog.type];
               
               $("v_gate").innerHTML = Hlog.gate==0 ? '' : Hlog.gate;
               $("v_gateName").innerHTML = Hlog.gate==0 ? '' : h_gate1[Hlog.gate];
               $("v_sys_date").innerHTML = formatDate(Hlog.sysDate);
               
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
               $("v_bank_fee").innerHTML = div100(Hlog.bankFee);
               $("v_bk_chk").innerHTML = h_bk_chk[Hlog.bkChk];
               
               $("v_bk_date").innerHTML = (Hlog.bkDate==null || Hlog.bkDate==0)?"&nbsp;":Hlog.bkDate;
               $("v_bk_seq1").innerHTML = Hlog.bk_seq1;
               $("v_bk_seq2").innerHTML = Hlog.bk_seq2;
               
               $("v_bk_resp").innerHTML = Hlog.bkResp;
               $("v_version").innerHTML = Hlog.version;
               $("v_ip").innerHTML = getStringIP(Hlog.ip);
               
               $("v_bid").innerHTML = Hlog.bid;
               $("v_pay_amt").innerHTML = div100(Hlog.payAmt);
               $("v_org_seq").innerHTML = Hlog.orgSeq; 
               
               $("v_ref_seq").innerHTML = Hlog.refSeq;
               $("v_mer_priv").innerHTML = Hlog.merPriv;
               $("v_mobile_no").innerHTML = shadowPhone(Hlog.mobileNo);
               $("v_trans_period").innerHTML = Hlog.transPeriod;
               $("v_card_no").innerHTML = shadowAcc(Hlog.cardNo);
               $("v_oper_id").innerHTML = Hlog.operId;
               $("v_pre_amt").innerHTML = div100(Hlog.preAmt);
               $("v_bk_fee_model").innerHTML = Hlog.bkFeeModel;
               $("v_pre_amt1").innerHTML = div100(Hlog.preAmt1);
               $("v_phone_no").innerHTML = shadowPhone(Hlog.phoneNo);
               $("v_gid").innerHTML = Hlog.gid==0 ? '' :gate_route_map1[Hlog.gid];
               
               $("v_bk_url").innerHTML = Hlog.bkUrl;
               
               $("v_fg_url").innerHTML = Hlog.fgUrl;
               $("v_err_msg").innerHTML = Hlog.error_msg;
               $("v_error_code").innerHTML = Hlog.errorCode;
               $("v_pro_num").innerHTML =(Hlog.gate==85000 || Hlog.gate==85001 || Hlog.gate==85002 || Hlog.gate==85003) ? Hlog.p2 :'';
               $("v_trade_plan").innerHTML = (Hlog.gate==85000 || Hlog.gate==85001 || Hlog.gate==85002 || Hlog.gate==85003) ?Hlog.p3 :'';
               $("v__down_payamt").innerHTML =(Hlog.gate==85000 || Hlog.gate==85001 || Hlog.gate==85002 || Hlog.gate==85003) ? div100(Hlog.p4):'';
               $("v_each_pay_num").innerHTML =(Hlog.gate==85000 || Hlog.gate==85001 || Hlog.gate==85002 || Hlog.gate==85003) ? div100(Hlog.p5):'';
               $("v_pro_poundage").innerHTML =(Hlog.gate==85000 || Hlog.gate==85001 || Hlog.gate==85002 || Hlog.gate==85003) ? div100(Hlog.p6):'';
               $("v__Grant_Number").innerHTML = (Hlog.gate==85000 || Hlog.gate==80002 || Hlog.gate==85001 || Hlog.gate==85002 || Hlog.gate==85003) ?Hlog.p7:'';
               
              jQuery("#hlogDetail").wBox({title:"商户订单详细资料",show:true});//显示box
            });
           
        }
    /****
     * 选择交易类型  交易银行联动功能
     * 
     * 
     */    
  function  onChangeGate(){
	 var t = $("type").value;
	 if(t==''){
		 t=-1;
	 }
	 PageService.getGateChannelMapByType(t,function(m){
			h_gate1=m[0];
			gate_route_map1=m[1];
			if($("gate")){
				dwr.util.removeAllOptions("gate");
				dwr.util.addOptions("gate",{'':'全部...'});
				dwr.util.addOptions("gate", h_gate1);
			}
			/*if($("gateRouteId")){
				dwr.util.removeAllOptions("gateRouteId");
				dwr.util.addOptions("gateRouteId",{'':'全部...'});
				dwr.util.addOptions("gateRouteId", gate_route_map1);
			}
		 */
	 });
	 
	 
  }  
 var gate_route_map2 =  {};
//搜索支付渠道
  function gateRouteIdList(){
		 var gateName = $("gateName").value;
		 PageService.getGRouteByName(gateName,function(m){
			 gate_route_map2=m;
			 dwr.util.removeAllOptions("gateRouteId");
			 dwr.util.addOptions("gateRouteId",{'':'全部...'});
			dwr.util.addOptions("gateRouteId", gate_route_map2);
		 });
		 
	 }