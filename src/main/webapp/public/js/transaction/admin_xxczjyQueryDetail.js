
//公共代码
        function init(){
        
            dwr.util.addOptions("tstat", h_tstat);
            dwr.util.addOptions("rg_tstat", pstates);
//            dwr.util.addOptions("bkCheck", h_bk_chk);
            dwr.util.addOptions("mstate", m_mstate);
            $("bdate").value = jsToday();
            $("edate").value = jsToday();
            initGateChannel();
            initMinfos();
        }
        //查询条件的判断
        var mid,gate,tstat,date,bdate,edate,gateRouteId,bkCheck,tseq,oid,bkseq,mstate,type,batchNo,rg_tstat;
        function judgeCondition(){
             mid = $("mid").value;
             tstat = $("tstat").value;
             date = $("date").value;
             bdate = $("bdate").value;
             tseq = $("tseq").value;
             oid = $("oid").value;
             mstate=$("mstate").value;
             rg_tstat=$("rg_tstat").value;
             edate=$("edate").value;
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
            TransactionService.querypaymentHlogDetail_cz( pageNo,mid,tstat,date,bdate,edate,tseq,oid,mstate,rg_tstat,callBack2);
        }
        //明细查询下载
        function downloadpaymentDetail(){
        	if(!judgeCondition())return;
            dwr.engine.setAsync(false);//把ajax调用设置为同步
            TransactionService.downloadpaymentDetail_cz(mid,tstat,date,bdate,edate,tseq,oid,mstate,rg_tstat,function(data){dwr.engine.openInDownload(data);});
        }
        
        var callBack2 = function(pageObj){
     	   $("merHlogTable").style.display="";
     	   dwr.util.removeAllRows("resultList");
     	   if(pageObj==null){document.getElementById("resultList").appendChild(creatNoRecordTr(14));return;}
 	        var cellFuncs = [
 	                         function(obj) { return "<a href=\"#\" onclick=\"query4Detail('"+ obj.tseq +"','"+obj.pstate+"','"+ obj.auditRemark +"','"+ obj.orgn_remark +"','"+ obj.cert_remark +"','"+ obj.rechargeAmt +"')\" >"+obj.tseq+"</a>"; },
 	                         function(obj) { return obj.mid; },
 	                         function(obj) { return m_minfos[obj.mid]; },
 	                         function(obj) {return obj.mid;},
 	                         function(obj) { return obj.oid; },
 	                         function(obj) { return div100(obj.amount); },
 	                         function(obj) { return h_tstat[obj.tstat]; },
 	                        function(obj) { return pstates[obj.pstate]; },//
 	                         function(obj) { return h_type_all[obj.type]; },
 	                         function(obj) { return formatDate(obj.sysDate)+" "+getStringTime(obj.sysTime);}
 	                     ]
               str = "<span class='pageSum'>&nbsp;交易总金额：<font color='red'><b>" + div100(pageObj.sumResult.amtSum)
               		 +"</b></font>  元</span> <span class='pageSum'>&nbsp;&nbsp;系统手续费总金额：<font color='red'><b>"+ div100(pageObj.sumResult.sysAmtFeeSum)+"</b></font>  元</span>";
               paginationTable(pageObj,"resultList",cellFuncs,str,"querypaymentHlog");
           }
        
     // 查询详细信息
        function query4Detail(tseq,pstate,auditRemark,orgnRemark,certRemark,rechargeAmt) {
            TransactionService.queryLogByTseq2(tseq,function(Hlog){  
                $("v_mid").innerHTML = Hlog.mid;
                $("v_midName").innerHTML = m_minfos[Hlog.mid];
                $("v_oid").innerHTML = Hlog.oid;
                $("v_amount").innerHTML = div100 (Hlog.amount);
                $("v_type").innerHTML = h_type_all[Hlog.type];
                $("v_tseq").innerHTML = Hlog.tseq;
                $("v_tstat").innerHTML = h_tstat[Hlog.tstat];
                $("v_sys_time").innerHTML = formatDate(Hlog.sysDate)+" "+getStringTime(Hlog.sysTime);
                $("v_aid").innerHTML=Hlog.mid;
                $("v_audit_remark").innerHTML=auditRemark=='null'?"":auditRemark;
                $("v_orgn_remark").innerHTML=orgnRemark=='null'?"":orgnRemark;
                $("v_cert_remark").innerHTML=certRemark=='null'?"":certRemark;
                $("v_picAdd").innerHTML=(Hlog.trans_proof== 'null' || Hlog.trans_proof == null)?"" :'<a href="#" title='+Hlog.trans_proof+'>'+Hlog.trans_proof+'</a>';
                $("v_recharge_amt").innerHTML=div100(rechargeAmt);
                $("v_pstate").innerHTML=pstates[pstate]==undefined ?"" :pstates[pstate];
                
              jQuery("#hlogDetail").wBox({title:"商户订单详细资料",show:true});//显示box
            });
        }
