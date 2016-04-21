	
	function init(){
		
        dwr.util.addOptions("stat", h_mer_refund_tstat_);
        dwr.util.addOptions("mstate", m_mstate);
        initGateChannel();
        initMinfos();
	}  
    var mid,gate,dateState,bdate,edate,stat,tseq,orgid,gateRouteId,mstate,refundType,begintrantAmt,endtrantAmt;
    function validQuery(){
        bdate = $("bdate").value;
        edate = $("edate").value;
        if(bdate==''){
            alert('请选择起始日期');
            return false;
        }
        if(edate==''){
            alert('请选择结束日期');
            return false;
        }
         mid = $("mid").value;
         gate = $("gate").value;
         dateState = $("dateState").value; 
         stat = $("stat").value;
         tseq = $("tseq").value;
         gateRouteId = $("gateRouteId").value; 
         mstate=$("mstate").value;
         refundType=$("refundType").value;
         begintrantAmt=$("begintrantAmt").value;
         endtrantAmt=$("endtrantAmt").value;
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
    //下载
    function downloadReturn(){
    	 if(!validQuery())return;   
    	 QueryRefundService.downloadReturn(mid,stat,tseq,dateState,bdate,edate,gate,null,null,gateRouteId,mstate,refundType,
    			 begintrantAmt,endtrantAmt,{callback:function(data){dwr.engine.openInDownload(data);}, async:false});//把ajax调用设置为同步
    }
    //查询
    function queryVerifyRefund(pageNo){
       if(!validQuery())return; 
        //添加了dateState  1为申请日期2为确认日期3为经办日期4为审核日期
       QueryRefundService.queryRefundLogs( pageNo,mid,stat,tseq,dateState,bdate,edate,gate,null,null,gateRouteId,mstate,
    		   refundType,begintrantAmt,endtrantAmt,callBack2);
    }
    var callBack2 = function(pageObj){
    $("detailResultList").style.display="";
    	   dwr.util.removeAllRows("resultList");
    	   	var cellFuncs = [
    	   	         function(obj) { return obj.id; },
                     function(obj) { return obj.tseq; },
                     function(obj) { return obj.mid; },
                     function(obj) { return m_minfos[obj.mid]; },
                     function(obj) { return obj.org_oid; },
                     function(obj) { return gate_route_map[obj.gid]; },
                     function(obj) { return h_gate[obj.gate]; },
                     function(obj) { return div100(obj.ref_amt); },
                     function(obj) { return div100(obj.merFee); },
                     function(obj) { return div100(obj.pre_amt1); },
                     function(obj) { return obj.req_date == 0 ? "" : formatDate(obj.req_date); },
                     function(obj) { return h_mer_refund_tstat[obj.stat]; },
                     function(obj) { return  "<input type=\"button\"  name=\"toCheck\"  value=\"  详 细  \" onclick=\"queryAObj("+obj.id+");\">"; }
                 ];
          if(pageObj==null){document.getElementById("resultList").appendChild(creatNoRecordTr(cellFuncs.length));return;}
    	   $("detailResultList").style.display="";
          str = "<span style='float:left;color:blue'>&nbsp;申请退款总金额：<font color='red'><b>" +div100(pageObj.sumResult.refFeeSum)+"</b></font> 元 ";
          str += "&nbsp;&nbsp;退回商户手续费总金额：<font color='red'><b>" +div100(pageObj.sumResult.merRefFeeSum)+"</b></font>  元</span>";
          
          paginationTable(pageObj,"resultList",cellFuncs,str,"queryVerifyRefund");
     };


    function queryAObj(id){
        if(!id) return;
        QueryRefundService.queryRefundLogById(id,function(refundLog){
             if(!refundLog) return;
             refundLog.stat= h_mer_refund_tstat[refundLog.stat];
             refundLog.gate= h_gate[refundLog.gate];
             refundLog.ref_amt=div100(refundLog.ref_amt);
             refundLog.orgAmt=div100(refundLog.orgAmt); 
             refundLog.orgPayAmt=div100(refundLog.orgPayAmt);
             refundLog.preAmt=div100(refundLog.preAmt);
             refundLog.pre_amt1=div100(refundLog.pre_amt1);
             refundLog.pro_date=refundLog.pro_date==0?"":refundLog.pro_date;
             refundLog.ref_date=refundLog.ref_date==0?"":refundLog.ref_date;
             refundLog.reason=refundLog.refundType==0?refundLog.reason:refundLog.onlineRefundReason;
             
             if(refundLog.refundType==2)
            	 refundLog.refundType="联机人工经办";
             else if(refundLog.refundType==1)
                 refundLog.refundType="联机退款";
             else
            	 refundLog.refundType="人工经办";
             refundLog.gid= gate_route_map[refundLog.gid];
             innerVal(refundLog);
          jQuery("#detail4One").wBox({title:"&nbsp;&nbsp;退款订单信息详情",show:true});//显示box
       });
    }
    //对象显示在表格中
    function innerVal(obj){

    	for (var attr in obj){
    		if(document.getElementById("v_"+attr)==null)continue;
    		if(obj[attr]==null){
    			document.getElementById("v_"+attr).innerHTML="";}
    		else{
    			document.getElementById("v_"+attr).innerHTML=obj[attr];}
    	}
    }

    

