
    var mid,gate,bdate,edate,stat,tseq,orgid,begintrantAmt,endtrantAmt;
    //查询数据获取和验证
    function validQuery(){
        bdate = $("bdate").value;
        edate = $("edate").value;
        mid = $("mid").value;
        if(bdate==''){
            alert('请选择起始日期');
            return false;
        }
        if(edate==''){
            alert('请选择结束日期');
            return false;
        }
        if(!mid || mid=='') return false;
         gate = $("gate").value;
         stat = $("stat").value;
         tseq = $("tseq").value;
         orgid = $("orgid").value;
	     begintrantAmt = $("begintrantAmt").value;
	     endtrantAmt = $("endtrantAmt").value;
	if (begintrantAmt != "") {
		begintrantAmt = begintrantAmt.trim();
		if (!isPlusInteger(begintrantAmt)) {
			alert("金额必须是正整数!");
			$("begintrantAmt").value = '';
			$("begintrantAmt").focus();
			return false;
		}
	}
	if (endtrantAmt != "") {
		endtrantAmt = endtrantAmt.trim();
		if (!isPlusInteger(endtrantAmt)) {
			alert("金额必须是正整数!");
			$("endtrantAmt").value = '';
			$("endtrantAmt").focus();
			return false;
		}
		if (tseq != "") {
			tseq = tseq.trim();
			if (!isNumber(tseq)) {
				alert("电银流水号必须是数字!");
				$("tseq").value = '';
				$("tseq").focus();
				return false;
			}
		}

	}
	return true;
}
    // 下载
    function downloadReturnQuery(){
        if(!validQuery())return;
         var dateState=1; // dateState1为申请日期2为确认日期3为经办日期4为审核日期
         QueryMerRefundLogsService.downloadReturn(mid,stat,tseq,dateState,bdate,edate,gate,orgid,null,null,begintrantAmt,endtrantAmt,
       			{callback:function(data){dwr.engine.openInDownload(data);}, async:false});
    }
   //查询
    function queryVerifyRefund(pageNo){
       if(!validQuery())return;
        var dateState=1; //dateState1为申请日期2为确认日期3为经办日期4为审核日期
        QueryMerRefundLogsService.queryRefundLogs( pageNo,mid,stat,tseq,dateState,bdate,edate,gate,orgid,null,null,
    			   begintrantAmt,endtrantAmt,callBack2);
    }
     var callBack2 = function(pageObj){
    	 	   $("refundListTable").style.display="";
         	   dwr.util.removeAllRows("resultList");
        	   if(pageObj==null){document.getElementById("resultList").appendChild(creatNoRecordTr(8));return;}
			    var cellFuncs = [
			                     function(obj) { return obj.tseq; },
			                     function(obj) { return obj.mid; },
			                     function(obj) { return obj.org_oid; },
			                     function(obj) { return h_gate[obj.gate]; },
			                     function(obj) { return div100(obj.ref_amt); },
			                     function(obj) { return obj.mdate; },
			                     function(obj) { return h_mer_refund_tstat[obj.stat]; },
			                     function(obj) { return  "<input type=\"button\" style=\"height:20px;\" name=\"toCheck\"  value=\"  详 细  \" onclick=\"queryAObj("+obj.id+");\">" }
			                 ]
          str = "<span style='float:left;color:blue'>&nbsp;申请退款总金额：<font color='red'><b>" + div100(pageObj.sumResult.refFeeSum)+"</b></font>  元</span>";         
          paginationTable(pageObj,"resultList",cellFuncs,str,"queryVerifyRefund");
      }

    function init(){
    	CommonService.getMerGatesMap($("mid").value,function(map){
    		dwr.util.addOptions("gate", map);
    		h_gate=map;
    	});
        dwr.util.addOptions("stat", h_mer_refund_tstat_);
        
    }

    function queryAObj(id){
        if(!id) return;
        QueryMerRefundLogsService.queryRefundLogById(id,function(oneRefundLog){
             if(!oneRefundLog) return;
        	 $("v_id").innerHTML = oneRefundLog.id;
             $("v_mid").innerHTML = oneRefundLog.mid;
             $("v_org_oid").innerHTML = oneRefundLog.org_oid;
             $("v_oid").innerHTML = oneRefundLog.oid;
             $("v_gate").innerHTML = h_gate[oneRefundLog.gate] ;       
             $("v_ref_amt").innerHTML = div100( oneRefundLog.ref_amt );
             $("v_mdate").innerHTML =  oneRefundLog.mdate == null ? "" : oneRefundLog.mdate;
             $("v_req_date").innerHTML = oneRefundLog.req_date == 0 ? "" : oneRefundLog.req_date;
             $("v_pro_date").innerHTML = (oneRefundLog.pro_date == null||oneRefundLog.pro_date == 0) ? "" : oneRefundLog.pro_date;
             $("v_ref_date").innerHTML = (oneRefundLog.ref_date == null || oneRefundLog.ref_date == 0) ? "" : oneRefundLog.ref_date;
//             $("auth_no").innerHTML =oneRefundLog.authNo==null?"无":oneRefundLog.authNo;
             $("v_refund_reason").innerHTML = oneRefundLog.refund_reason == null ? "": oneRefundLog.refund_reason ;
             $("v_etro_reason").innerHTML = oneRefundLog.etro_reason == null ? "": oneRefundLog.etro_reason ;
             $("v_reason").innerHTML = oneRefundLog.reason == null ? "": oneRefundLog.reason ;
             $("v_tseq").innerHTML = oneRefundLog.tseq;
             $("v_stat").innerHTML =  h_mer_refund_tstat[oneRefundLog.stat] ;
             $("v_merFee").innerHTML = div100(oneRefundLog.merFee);//退还手续费
             $("v_orgBkSeq").innerHTML = oneRefundLog.orgBkSeq;
             $("v_orgAmt").innerHTML = div100(oneRefundLog.orgAmt);
            jQuery("#detail4One").wBox({title:"退款订单信息详情",show:true});//显示box
       })
    }