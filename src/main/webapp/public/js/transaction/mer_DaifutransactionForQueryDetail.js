//公共代码
        function init(){
            dwr.util.addOptions("tstat", h_tstat);
            dwr.util.addOptions("type", df_type);
//            dwr.util.addOptions("bkCheck", h_bk_chk);
            dwr.util.addOptions("mstate", m_mstate);
            initGateChannel();
            initMinfos();
        } 
        //查询条件的判断
        var mid,gate,tstat,date,bdate,edate,gateRouteId,bkCheck,tseq,oid,bkseq,mstate,type,batchNo;
        function judgeCondition(){
//             gate = $("gate").value;
             tstat = $("tstat").value;
             date = $("date").value;
             bdate = $("bdate").value;
//             gateRouteId = $("gateRouteId").value;
//             bkCheck= $("bkCheck").value;
             tseq = $("tseq").value;
             oid = $("oid").value;
//             bkseq=$("bkseq").value;
             type=$("type").value;
             batchNo=$("batchNo").value;
             
             if(bdate=='') {
                alert("请选择起始日期！"); 
                return false; 
            }
             edate = $("edate").value;
            if(edate==''){
                 alert("请选择结束日期！"); 
                 return false; 
            }
            return true;
        }
        //明细查询 
        function querypaymentHlog(pageNo){
        	if(!judgeCondition())return;
            TransactionService.querypaymentHlogDetail_mer( pageNo,gate,tstat,oid,gateRouteId,date,bdate,edate,tseq,bkCheck,bkseq,mstate,type,batchNo,callBack2);
        }
        //明细查询下载
        function downloadpaymentDetail(){
        	if(!judgeCondition())return;
            dwr.engine.setAsync(false);//把ajax调用设置为同步
            TransactionService.downloadpaymentDetail_mer(gate,tstat,oid,gateRouteId,date,bdate,edate,tseq,bkCheck,bkseq,mstate,type,batchNo,function(data){dwr.engine.openInDownload(data);});
        }
        
        var callBack2 = function(pageObj){
     	   $("merHlogTable").style.display="";
     	   dwr.util.removeAllRows("resultList");
     	   if(pageObj==null){document.getElementById("resultList").appendChild(creatNoRecordTr(14));return;}
 	        var cellFuncs = [
 	                         function(obj) { return "<a href=\"#\" onclick=\"query4Detail('"+ obj.tseq +"')\" >"+obj.tseq+"</a>"; },
 	                         function(obj) { return obj.mid; },
 	                         function(obj) { return m_minfos[obj.mid]; },
 	                         function(obj) {return obj.mid;},
 	                         function(obj) { return obj.oid; },
 	                        function(obj) { return obj.p8; },
 	                        /* function(obj) { return formatDate(obj.mdate); },*/
 	                         function(obj) {
 	                        	amount=Math.abs(obj.amount)
 	                        	return div100(-amount); },
 	                         function(obj) { return div100(obj.feeAmt); },
 	                         function(obj) { return h_tstat[obj.tstat]; },
 	                        // function(obj) { return obj.bkChk==null?"未对账":h_bk_chk[obj.bkChk]; },//
 	                         function(obj) { return h_type_all[obj.type]; },
 	                         function(obj) { return h_gate[obj.gate]; },
 	                         function(obj) { return formatDate(obj.sysDate)+" "+getStringTime(obj.sysTime);},
//	                         function(obj) { return createTip(8,obj.bk_seq1); },
	                         function(obj) {return obj.tstat == 2 ?"":createTip(8,obj.error_msg) }
 	                     ]
               str = "<span class='pageSum'>&nbsp;交易总金额：<font color='red'><b>" + div100(pageObj.sumResult.amtSum)
               		 +"</b></font>  元</span> <span class='pageSum'>&nbsp;&nbsp;系统手续费总金额：<font color='red'><b>"+ div100(pageObj.sumResult.sysAmtFeeSum)+"</b></font>  元</span>";
               paginationTable(pageObj,"resultList",cellFuncs,str,"querypaymentHlog");
           }
        
     // 查询详细信息
        function query4Detail(tseq) {
            TransactionService.queryLogByTseq2(tseq,function(Hlog){                
                $("v_mid").innerHTML = Hlog.mid;
                $("v_midName").innerHTML = m_minfos[Hlog.mid];
                $("v_oid").innerHTML = Hlog.oid;
                $("v_amount").innerHTML = - div100 (Hlog.amount);
                $("v_type").innerHTML = h_type_all[Hlog.type];
                $("v_gateName").innerHTML = Hlog.gate==0 ? '' : h_gate[Hlog.gate];
                $("v_tseq").innerHTML = Hlog.tseq;
                $("v_batch").innerHTML = Hlog.p8==0?"":Hlog.p8;
                $("v_fee_amt").innerHTML = div100(Hlog.feeAmt);
                $("v_tstat").innerHTML = h_tstat[Hlog.tstat];
                $("v_sys_time").innerHTML = formatDate(Hlog.sysDate)+" "+getStringTime(Hlog.sysTime);
                $("v_payamt").innerHTML=div100(Hlog.payAmt);
                $("v_err_msg").innerHTML = Hlog.error_msg;     
                $("v_card_no").innerHTML=Hlog.p1 ;//收款人账号
                $("v_to_acc_name").innerHTML=Hlog.p2;//收款人户名
                $("v_aid").innerHTML=Hlog.mid;
                $("to_bank_no").innerHTML=Hlog.p3;
              jQuery("#hlogDetail").wBox({title:"商户订单详细资料",show:true});//显示box
            });
        }
