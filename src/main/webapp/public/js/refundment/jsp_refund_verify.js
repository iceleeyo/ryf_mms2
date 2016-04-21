
    function submitCheckAccount(action) {
        var refundIds = "(";
        var tseq_s = document.getElementsByName("toCheck");
        for (var j = 0 ; j < tseq_s.length; j++) {
            if (tseq_s[j].checked) {
              refundIds = refundIds+tseq_s[j].value+",";
            }
        }
        if (refundIds.length == 1 ){
            alert("请至少选中一条记录!");
            return ;
        } 
        refundIds = refundIds.substring(0,refundIds.length-1) + ")";
        if (action == "retroversion"){
            if (confirm("撤销退款?")) {  
           	  // alert(typeof eval("("+rlist+")"));
                RefundmentService.verifyFail(arr,refundIds,callback);
            }
        
       } else {
           if (confirm("确定退款?")) {  
               RefundmentService.verifySure(refundIds,callback);
           }
       }
    }

    function callback(msg) {
        if (msg)  alert(msg);
        var page = $("pageChoose").value;
        queryVerifyRefund(page);
    }


    function queryVerifyRefund(pageNo){
        //$("detail4One").style.display = "none";
        var mid = $("mid").value;
        if(!mid || mid=='') return;
        var gate = $("gate").value;
        var bdate = $("bdate").value;
        var edate = $("edate").value;
        var dateState = 1;
        RefundmentService.queryRefundLogs( pageNo,mid,5,null,dateState,bdate,edate,gate,null,null,null,callBack2);
    }
    var arr=new Array();//在submitCheckAccount中使用
    var callBack2 = function(pageObj){
    $("refundList").style.display="";
    var cellFuncs = [
                     function(obj) { return  "<input type=\"checkbox\"  name=\"toCheck\"  value=\""+ obj.id +"\"   >" },
                     function(obj) { return obj.tseq; },
                     function(obj) { return obj.mid; },
                     function(obj) { return obj.org_oid; },
                     function(obj) { return h_gate[obj.gate]; },
                     function(obj) { return div100(obj.ref_amt); },
                     function(obj) { return '商户申请退款'; },
                     function(obj) { return obj.mdate; },
                     function(obj) { return obj.refund_reason; }
                     
                 ]
                 
                 arr=pageObj.pageItems;
          str = "<span style='float:left;color:blue'>&nbsp;申请退款总金额：<font color='red'><b>" + div100(pageObj.sumResult.refFeeSum)+"</b></font>  元";
          str+= "&nbsp;&nbsp;&nbsp;&nbsp;"
          str+= "&nbsp;&nbsp;<input type=\"button\" id=\"chooseAll\" name=\"chooseAll\" value=\"全 选\" onclick=\"checkAll('toCheck');\">";
          str+= "&nbsp;&nbsp;<input type=\"button\" id=\"checkAccount\" name=\"checkAccount\" value=\"确认退款\" onclick=\"submitCheckAccount('');\">";
          str+= "&nbsp;&nbsp;<input type=\"button\" id=\"cancelAll\" name=\"cancelAll\" value=\"撤销退款\" onclick=\"submitCheckAccount('retroversion');\">";
          str +="</span>";
          paginationTable(pageObj,"resultList",cellFuncs,str,"queryVerifyRefund");
      }

    function init(){
    	CommonService.getMerGatesMap($("mid").value,function(map){
    		dwr.util.addOptions("gate", map);
    		h_gate=map;
    	});
    	//dwr.util.addOptions("gate", h_gate);
    }