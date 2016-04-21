var m_mer_trade_type={};
 		var m_trade_type_map={};
 		function initOptions(){
 		  dwr.util.addOptions("state",h_tstat);
 		 dwr.util.addOptions("mstate",m_mstate);
 		  initMinfos();
 		  initGateChannel();
 		  PageService.getTradeTypeMap(function(mapData){
 				  m_trade_type_map=mapData;
 				  dwr.util.addOptions("merTradeType",mapData);
 		  });
 		}
 		var refund_tstat={"":"全部...","1":"退款成功","2":"退款失败"};
 		function changeQuery(qType){
 		    dwr.util.removeAllOptions("state");
 		    if(qType==1){
 		      document.getElementById("queryDate").innerHTML="交易系统日期：";
 		      dwr.util.addOptions("state",{"":"全部..."});
 		      dwr.util.addOptions("state",h_tstat);
 		    }else{
 		      document.getElementById("queryDate").innerHTML="退款经办日期：";
 		      dwr.util.addOptions("state",refund_tstat);
 		    }
 		}
 		function queryAnalysisData(pageNo){
             var queryType = document.getElementById("queryType").value;
 		    var mid = document.getElementById("mid").value;
             var state = document.getElementById("state").value;
             var bdate = document.getElementById("bdate").value;
             var edate = document.getElementById("edate").value;
             var merTradeType = document.getElementById("merTradeType").value;
             var mstate = document.getElementById("mstate").value;
             if(bdate==""||edate=="") {
                 alert("请选择起始日期！"); 
                 return false; 
             }
             if(queryType==1){
                TransactionService.hlogAnalysis( pageNo, mid, bdate, edate, state,merTradeType,mstate,callback1);
             }else{
                TransactionService.refundAnalysis( pageNo, mid, bdate, edate, state,merTradeType,mstate,callback2);
             }
 		}
 		var callback1=function(transDataList){
 		    $("refundTable").style.display="none";
 		    $("transTable").style.display="";
     	    dwr.util.removeAllRows("transBody");
     	   if(transDataList==null){document.getElementById("transBody").appendChild(creatNoRecordTr(13));return;}
 		  		 var cellFuncs=[
 		  		 			 function(obj) { return obj.tseq; },
 		  	                 function(obj) { return obj.mid; },
 	                         function(obj) { return m_minfos[obj.mid]; },
 	                         function(obj) { return m_trade_type_map[obj.merTradeType]; },
 	                         function(obj) { return obj.oid; },
 	                         function(obj) { return div100(obj.amount); },
 	                         function(obj) { return h_tstat[obj.tstat]; },
 	                         function(obj) { return h_type[obj.type]; },
 	                         function(obj) { return h_gate[obj.gate]; },
 	                         function(obj) { return gate_route_map[obj.gid]; },
 	                         function(obj) { return div100(obj.feeAmt); },
 	                         function(obj) { return div100(obj.bankFee); },
 	                         function(obj) { return formatDate(obj.sysDate)+" "+getStringTime(obj.sysTime);}
 		  				];
 		     str = "<span class='pageSum'>&nbsp;交易总金额：<font color='red'><b>" + div100(transDataList.sumResult.amtSum)+"</b></font>元</span> ";
               paginationTable(transDataList,"transBody",cellFuncs,str,"queryAnalysisData");
 		}
 		var callback2=function(refundDataList){
 		    $("transTable").style.display="none";
 		    $("refundTable").style.display="";
     	    dwr.util.removeAllRows("refundBody");
     	   if(refundDataList==null){document.getElementById("refundBody").appendChild(creatNoRecordTr(13));return;}
 		  		 var cellFuncs=[
 		  		 			 function(obj) { return obj.tseq; },
 		                     function(obj) { return obj.mid; },
 		                     function(obj) { return m_minfos[obj.mid]; },
 		                     function(obj) { return m_trade_type_map[obj.merTradeType]; },
 		                     function(obj) { return obj.org_oid; },
 		                     function(obj) { return h_gate[obj.gate]; },
 		                     function(obj) { return gate_route_map[obj.gid]; },
 		                     function(obj) { return div100(obj.ref_amt); },
 		                     function(obj) { return h_mer_refund_tstat[obj.stat]; },
 		                     function(obj) { return obj.pro_date; }
 		  				];
 		     str = "<span class='pageSum'>&nbsp;退款总金额：<font color='red'><b>" + div100(refundDataList.sumResult.amtSum)+"</b></font>元</span> ";
               paginationTable(refundDataList,"refundBody",cellFuncs,str,"queryAnalysisData");
 		}