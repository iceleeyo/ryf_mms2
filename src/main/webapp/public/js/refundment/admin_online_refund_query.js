	
	function init(){
		
       // dwr.util.addOptions("stat", h_mer_refund_tstat_);
        dwr.util.addOptions("mstate", m_mstate);
        initGateChannel();
        initMinfos();
	}  
    var mid,gate,bdate,edate,stat,tseq,gateRouteId,mstate,refundType,begintrantAmt,endtrantAmt,refBkSeq;
    var param={};
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
        // dateState = $("dateState").value; 
         stat = $("stat").value;
         tseq = $("tseq").value;
         gateRouteId = $("gateRouteId").value; 
         mstate=$("mstate").value;
         rId=$("rId").value;
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
         param={
         		"mid":mid,
         		"gate":gate,
         		"bdate":bdate,
         		"edate":edate,
         		"stat":stat,
         		"tseq":tseq,
         		"gateRouteId":gateRouteId,
         		"mstate":mstate,
         		"rId":rId,
         		"begintrantAmt":begintrantAmt,
         		"endtrantAmt":endtrantAmt
         		
         };
         return true;
    }
    //下载
    function downloadDetail(){
    	 if(!validQuery())return;   
    	 OnlineRefundService.downloadDetail(param,{callback:function(data){dwr.engine.openInDownload(data);}, async:false});//把ajax调用设置为同步
    }
    //查询
    function queryOnlineRefund(pageNo){
       if(!validQuery())return; 
       OnlineRefundService.queryOnlineRefunds( pageNo,param,callBack2);
    }
    var callBack2 = function(pageObj){
    $("detailResultList").style.display="";
    	   dwr.util.removeAllRows("resultList");
    	   	var cellFuncs = [
    	   	         function(obj) { return obj.id; },
                     //function(obj) { return obj.refBkSeq; },
                     function(obj) { return obj.tseq; },
                     function(obj) { return obj.mid; },
                     function(obj) { return m_minfos[obj.mid]; },
                     function(obj) { return obj.orgOid; },
                     function(obj) { return gate_route_map[obj.gid]; },
                     function(obj) { return h_gate[obj.gate]; },
                     function(obj) { return div100(obj.orgAmt); },
                     function(obj) { return div100(obj.refAmt); },
                     function(obj) { return obj.reqBkDate == 0 ? "" : formatDate(obj.reqBkDate); },
                     function(obj) { 
                    	 if(obj.refStatus==2)
                    		 return"退款成功";
                         else if(obj.refStatus==1)
                        	 return"待处理";
                         else if(obj.refStatus==3)
                        	 return"退款失败";
                    	 },
                     function(obj) { return  "<input type=\"button\"  name=\"toCheck\"  value=\"  详 细  \" onclick=\"queryAObj("+obj.id+");\">"; }
                 ];
          if(pageObj==null){document.getElementById("resultList").appendChild(creatNoRecordTr(cellFuncs.length));return;}
    	   $("detailResultList").style.display="";
          str = "<span style='float:left;color:blue'>&nbsp;退款总金额：<font color='red'><b>" +div100(pageObj.sumResult.sysAmtFeeSum)+"</b></font>  元</span>";
          
          paginationTable(pageObj,"resultList",cellFuncs,str,"queryOnlineRefund");
     };


    function queryAObj(id){
        if(!id) return;
        OnlineRefundService.getOnlineRefund(id,function(onlineRefund){
             if(!onlineRefund) return;
             onlineRefund.mid= onlineRefund.mid;
             onlineRefund.name= m_minfos[onlineRefund.mid];
             onlineRefund.tseq= onlineRefund.tseq;
             onlineRefund.org_oid= onlineRefund.orgOid;
             onlineRefund.ref_bk_seq= onlineRefund.id;
             onlineRefund.oid= onlineRefund.refOid;
             onlineRefund.gate= h_gate[onlineRefund.gate];
             onlineRefund.gid= gate_route_map[onlineRefund.gid];
             onlineRefund.orgBkSeq= onlineRefund.orgBkSeq;
             onlineRefund.orgAmt= div100(onlineRefund.orgAmt);
             onlineRefund.ref_amt= div100(onlineRefund.refAmt);
             onlineRefund.ref_date=onlineRefund.refDate==0?"":formatDate(onlineRefund.refDate)+" "+getStringTime(onlineRefund.refTime);
             onlineRefund.req_bk_date=onlineRefund.reqBkDate==0?"":formatDate(onlineRefund.reqBkDate)+" "+getStringTime(onlineRefund.reqBkTime);
             onlineRefund.bk_resp_date=onlineRefund.bkRespDate==0?"":formatDate(onlineRefund.bkRespDate) +" "+getStringTime(onlineRefund.bkRespTime);
             //onlineRefund.ref_status=onlineRefund.refStatus==0?"":onlineRefund.refStatus;
             onlineRefund.ljtk="联机退款";
             onlineRefund.reason=onlineRefund.errorMsg;
//             
             if(onlineRefund.refStatus==2)
            	 onlineRefund.ref_status="退款成功";
             else if(onlineRefund.refStatus==1)
            	 onlineRefund.ref_status="待处理";
             else if(onlineRefund.refStatus==3)
            	 onlineRefund.ref_status="退款失败";
             innerVal(onlineRefund);
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
    
    var gate_route_map2 =  {};
    function gateRouteIdList(){
		 var gateName = $("gateName").value;
		 PageService.getGRouteByName(gateName,function(m){
			 gate_route_map2=m;
			 dwr.util.removeAllOptions("gateRouteId");
			 dwr.util.addOptions("gateRouteId",{'':'全部...'});
			dwr.util.addOptions("gateRouteId", gate_route_map2);
		 });
		 
	 }

    

