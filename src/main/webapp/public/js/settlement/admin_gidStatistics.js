//公共代码
	       function initOption(){
	    	   initMinfos();
	    	   initGateChannel();
	    	  
	       }
             function changeSearch(id) {
                    if (id == 'meroid') {
                        $("meroid").style.display = "";
                        $("bygid").style.display = "none";
                        $("showTable").style.display = "none";
                        $("showTableGid").style.display = "none";
                        $("gateRouteId").value = "";
                        $("mid").value = "";
                        $("beginDate").value = "";
                        $("endDate").value = "";
                        $("bDate").value = "";
                        $("eDate").value = "";
                    } else if (id == 'bygid') {
                        $("bygid").style.display = "";
                        $("meroid").style.display = "none";
                        $("showTable").style.display = "none";
                        $("showTableGid").style.display = "none";
                        $("gateRouteId").value = "";
                        $("mid").value = "";
                        $("beginDate").value = "";
                        $("endDate").value = "";
                        $("bDate").value = "";
                        $("eDate").value = "";
                    }
                }
             var id;
                function queryOne(pageNo,id1){
                	if(id1!=undefined){
                		id=id1;
                	}
                	
                    dwr.engine.setAsync(false);
                    if (id == 'A') {
                    	var mid = $("mid").value;
                        var beginDate = $("beginDate").value.trim();
                        var endDate = $("endDate").value.trim(); 
                        if ((beginDate == null || beginDate == "") || (endDate == null || endDate == "")) {
            				alert("请选择交易日期！");
            				return;
            			}
                        SettlementService.queryTradeStatisticByMid( beginDate,  endDate,  mid,pageNo,callBack2);
                    }
                    else if(id == 'B'){
                    	var gateRouteId = $("gateRouteId").value;
                        var bDate = $("bDate").value.trim();
                        var eDate = $("eDate").value.trim();
                        if ((bDate == null || bDate == "") || (eDate == null || eDate == "")) {
            				alert("请选择交易日期！");
            				return;
            			}
                        SettlementService.queryTradeStatisticByGid(bDate,eDate,gateRouteId,pageNo,callBack11);
                    }
                   
                }
       
        var callBack2 = function(pageObj){
                     if(pageObj == null){
                        $("showTable").style.display = "none";
                        alert( "没有符合条件的查询记录!" );
                        return;
                     }
                     $("showTable").style.display="";
                     $("showTableGid").style.display="none";
              	   dwr.util.removeAllRows("resultList");
              	   if(pageObj==null){
                  	   document.getElementById("resultList").appendChild(creatNoRecordTr(3));
              		   return;
              	   }  
          	        var cellFuncs = [
          	                         function(obj) { return obj.mid; },
          	                         function(obj) { return div100(obj.amount); },
          	                         function(obj) { return obj.count; }
          	                     ];

          	               str ="<span class='pageSum'>&nbsp;交易总金额：<font color='red'><b>" + div100(pageObj.sumResult["amtSum"])
          	              		     +"</b></font>  元</span><span class='pageSum'>&nbsp;&nbsp;交易总笔数：<font color='red'><b>"+ pageObj.sumResult["sysAmtFeeSum"]+"</b></font>  笔</span>";	             
          	              paginationTable(pageObj,"resultList",cellFuncs,str,"queryOne");
                }; 
                
                var callBack11 = function(pageObj){
                    $("showTable").style.display="none";
                    $("showTableGid").style.display="";
             	   dwr.util.removeAllRows("resultListgid");
             	   if(pageObj==null){
                 	   document.getElementById("resultListgid").appendChild(creatNoRecordTr(3));
             		   return;
             	   }  
         	        var cellFuncs = [
         	                         function(obj) { return obj.gateName; },
         	                         function(obj) { return div100(obj.amount); },
         	                         function(obj) { return obj.count; }
         	                     ];

         	               str ="<span class='pageSum'>&nbsp;交易总金额：<font color='red'><b>" + div100(pageObj.sumResult["amtSum"])
         	              		     +"</b></font>  元</span> <span class='pageSum'>&nbsp;&nbsp;交易总笔数：<font color='red'><b>"+ pageObj.sumResult["sysAmtFeeSum"]+"</b></font> 笔</span>";	             
         	              paginationTable(pageObj,"resultListgid",cellFuncs,str,"queryOne");
               };
                
               function exportTransExcel(id){
                   dwr.engine.setAsync(false);
                   if (id == 'A') {
                   	var mid = $("mid").value;
                       var beginDate = $("beginDate").value.trim();
                       var endDate = $("endDate").value.trim();
                       
                      if ((beginDate == null || beginDate == "") || (endDate == null || endDate == "")) {
       				alert("请选择交易日期！");
       				return;
       			}
                       SettlementService.downStatisticsmid11( beginDate,  endDate,  mid,function(data){dwr.engine.openInDownload(data);});
                   }
                   else if(id == 'B'){
                   	var gateRouteId = $("gateRouteId").value;
                       var bDate = $("bDate").value.trim();
                       var eDate = $("eDate").value.trim();
                        if ((bDate == null || bDate == "") || (eDate == null || eDate == "")) {
       				alert("请选择交易日期！");
       				return;
       			}
                      SettlementService.downStatisticsGid11(bDate,eDate,gateRouteId,function(data){dwr.engine.openInDownload(data);});
                   }
                  
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