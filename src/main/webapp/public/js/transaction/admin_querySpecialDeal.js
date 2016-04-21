//公共代码
	       function initOption(){
	    	   initMinfos();
	    	   initGateChannel();
	    	  
	       }
             function changeSearch(id) {
                    if (id == 'meroid') {
                        $("meroid").style.display = "";
                        $("bytseq").style.display = "none";
                        $("showTable").style.display = "none";
                        $("bybkseq").style.display = "none";
                        $("footMenuTable").style.display = "none";
                        $("oid").value = "";
                        $("c_mdate").value = "";
                    } else if (id == 'bytseq') {
                        $("bytseq").style.display = "";
                        $("meroid").style.display = "none";
                        $("showTable").style.display = "none";
                        $("bybkseq").style.display = "none";
                        $("footMenuTable").style.display = "none";
                        $("tseq").value = "";
                        $("mid").value = "";
                        $("smid").value = "";
                    } else if(id=="bybkseq"){
                    	$("bytseq").style.display = "none";
                        $("meroid").style.display = "none";
                        $("showTable").style.display = "none";
                        $("footMenuTable").style.display = "none";
                        $("bybkseq").style.display = "";
                        $("bktseq").value = "";
                        $("gateRouteId").value = "";
                    }
                }
                function queryOne(id){
                    dwr.engine.setAsync(false);
                    if (id == 'meroid') {
                    	var mid = $("mid").value;
                        if(mid=='') {
                        	$("showTable").style.display = "none";
                            alert("商户号不能为空!");
                            return false;
                         }
                        var oid = $("oid").value.trim();
                        if (oid=='') {
                        	 $("showTable").style.display = "none";
                            alert("请输入商户订单号!");
                            return false;
                        }
                        var mdate = $("c_mdate").value.trim(); 
                        TransactionService.queryHlogByMer( mid,  mdate,  oid,callBack4Special);
                    } else if (id == 'bytseq') {
        
                        var tseq = $("tseq").value.trim();
                        if (tseq=='') {
                        	 $("showTable").style.display = "none";
                            alert("请输入电银流水号!");
                            return false;
                        }
                        if (!isNumber(tseq)) {
                        	  $("showTable").style.display = "none";
                            alert("电银流水号必须是数字!");
                            return false;
                        }
                        TransactionService.queryLogByTseq(tseq,2,callBack4Special);
                    } else if(id=='bybkseq'){

                    	var bybkseq = ($("bktseq").value).trim();
                    	var gateRouteId = ($("gateRouteId").value).trim();
                    	
                        if (bybkseq=='') {
                        	$("showTable").style.display = "none";
                            alert("请输入银行流水号!");
                            return false;
                        }
                        if (gateRouteId==-1||gateRouteId=="") {
                        	$("showTable").style.display = "none";
                            alert("请选择支付银行!");
                            return false;
                        }
                        TransactionService.queryHlogByBkseq(gateRouteId,bybkseq,callBack4Special);
                     }
                   
                }
        function changePayChannel(){
        	 TransactionService.checkUpdateChannelAuth(function(flag){
        		 if(flag){
                     //验证权限显示footMenuTable
        			 $("footMenuTable").style.display="";
                     var size=$("gateRouteId2").length;
                     if(size==0){
                        dwr.util.addOptions("gateRouteId2", gate_route_map);
                     }
        	         $("gateRouteId2").value=gid;
        		 }else{
                     //验证权限隐藏footMenuTable
        			 $("footMenuTable").style.display="none";
        		 }
        	 })
        }
        function submitChangePayChannel(){
        	if(!confirm("你确认修改吗？"))return;
        	var tseq=$("v_tseq").innerHTML;
        	var gid=$("gateRouteId2").value;
    		TransactionService.updateGid(tseq,gid,function(msg){
			        alert(msg);
    		});
        }
        var gid=0;
                var callBack4Special = function (Hlog){
                     if(Hlog == null){
                        $("showTable").style.display = "none";
                        alert( "没有符合条件的查询记录!" );
                        return;
                     }
                     gid=Hlog.gid;
                     $("showTable").style.display = "";
                     $("v_mdate").innerHTML = formatDate(Hlog.mdate);
                     $("v_mid").innerHTML = Hlog.mid;
                     $("v_midName").innerHTML = m_minfos[Hlog.mid];
                     
                     $("v_oid").innerHTML = Hlog.oid;
                     $("v_amount").innerHTML = div100 (Hlog.amount);
                     $("v_type").innerHTML = h_type[Hlog.type];
                     
                     $("v_gate").innerHTML = Hlog.gate==0 ? '' : Hlog.gate;
                     $("v_gateName").innerHTML = Hlog.gate==0 ? '' : h_gate[Hlog.gate];
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
                     $("v_mobile_no").innerHTML = Hlog.mobileNo==null?"":shadowPhone(Hlog.mobileNo);
                     $("v_trans_period").innerHTML = Hlog.transPeriod;
                     $("v_card_no").innerHTML = Hlog.cardNo==null?"":shadowAcc(Hlog.cardNo);
                     $("v_oper_id").innerHTML = Hlog.operId==null?"": Hlog.operId;
                     $("v_pre_amt").innerHTML = div100(Hlog.preAmt);
                     $("v_bk_fee_model").innerHTML = Hlog.bkFeeModel;
                     $("v_pre_amt1").innerHTML = div100(Hlog.preAmt1);
                     $("v_phone_no").innerHTML = shadowPhone(Hlog.phoneNo);
                     $("v_gid").innerHTML = Hlog.gid==0 ? '' :gate_route_map[Hlog.gid];
                     
                     $("v_bk_url").innerHTML = Hlog.bkUrl==null?"":Hlog.bkUrl;
                     
                     $("v_fg_url").innerHTML = Hlog.fgUrl==null?"":Hlog.fgUrl;
                     $("v_err_msg").innerHTML = Hlog.error_msg;
                     $("v_error_code").innerHTML = Hlog.errorCode;
                     $("v_pro_num").innerHTML =(Hlog.gate==85000 || Hlog.gate==85001 || Hlog.gate==85002 || Hlog.gate==85003) ? Hlog.p2 :'';
                     $("v_trade_plan").innerHTML = (Hlog.gate==85000 || Hlog.gate==85001 || Hlog.gate==85002 || Hlog.gate==85003) ?Hlog.p3 :'';
                     $("v__down_payamt").innerHTML =(Hlog.gate==85000 || Hlog.gate==85001 || Hlog.gate==85002 || Hlog.gate==85003) ? div100(Hlog.p4):'';
                     $("v_each_pay_num").innerHTML =(Hlog.gate==85000 || Hlog.gate==85001 || Hlog.gate==85002 || Hlog.gate==85003) ? div100(Hlog.p5):'';
                     $("v_pro_poundage").innerHTML =(Hlog.gate==85000 || Hlog.gate==85001 || Hlog.gate==85002 || Hlog.gate==85003) ? div100(Hlog.p6):'';
                     $("v__Grant_Number").innerHTML = (Hlog.gate==85000 || Hlog.gate==80002 || Hlog.gate==85001 || Hlog.gate==85002 || Hlog.gate==85003) ?Hlog.p7:'';
                     changePayChannel();
                }  