
	    
 function init(){
	 CommonService.getMerGatesMap($("mid").value,function(map){
		 dwr.util.addOptions("gate", map);
		 h_gate=map;
	});
     dwr.util.addOptions("tstat", h_tstat);
     dwr.util.addOptions("type", h_type);
 }
 var mid,oid,gate,tstat,type,bkseq,tseq;
 function judgeCondition(){
      mid = $("mid").value;
      oid = $("oid").value;
      gate = $("gate").value;
      tstat = $("tstat").value;
      type = $("type").value;
      tseq = $("tseq").value;
     if (tseq != "") {
         tseq = tseq.trim();
         if (!isNumber(tseq)) {
             alert("电银流水号必须是数字!");
             $("tseq").value = '';
             $("tseq").focus();
             return false;
          }
     }
     return true;
 }
 //查询
 function queryMerToday(pageNo){
	 if(!judgeCondition())return;
     TransactionService.queryMerToday(pageNo,mid,gate,tstat, type,tseq,oid,null,"",callBack2);

 }
 //下载
 function downloadToday(){
	  if(!judgeCondition())return;
  	  dwr.engine.setAsync(false);//把ajax调用设置为同步
      TransactionService.downloadToday_MER(mid,gate,tstat, type,tseq,oid,null,"",
    		       function(data){dwr.engine.openInDownload(data);});
}		
 //回调函数
 var callBack2 = function(pageObj){
	  $("merTodayTable").style.display="";
	   dwr.util.removeAllRows("resultList");
	   if(pageObj==null){document.getElementById("resultList").appendChild(creatNoRecordTr(13));return;}
		 var cellFuncs = [
		                  function(obj) { return obj.tseq; },
		                  function(obj) { return obj.mid; },
		                  function(obj) { return obj.oid; },
		                  function(obj) { return obj.mdate; },
		                  function(obj) { return div100(obj.amount); },
		                  function(obj) { return h_tstat[obj.tstat]; },
		                  function(obj) { return h_type[obj.type]; },
		                  function(obj) { return h_gate[obj.gate]; },
		                  function(obj) { return div100(obj.feeAmt); },
		                  function(obj) { return obj.sysDate;},
		                  function(obj) {
		                      if(obj.tstat == 2 || obj.tstat==3)
		                    return "<input type=\"button\" value=\" 请求商户后台  \" onclick=\"sendRequests(" + obj.tseq + ",'tlog')\" >";
		                    else return '';
		                      }
		              ]
      str = "<span class='pageSum'>&nbsp;&nbsp;交易总金额：<font color='red'><b>" + div100(pageObj.sumResult.amtSum)
       	   +"</b></font>  元</span> <span class='pageSum'>&nbsp;&nbsp;系统手续费总金额：<font color='red'><b>"+ div100(pageObj.sumResult.sysAmtFeeSum)+"</b></font>  元</span>";
       paginationTable(pageObj,"resultList",cellFuncs,str,"queryMerToday");
   }
 //商户请求后台
 function sendRequests(tseq,t){
	 	useLoadingImage("../../public/images/wbox/loading.gif");
	   TransactionService.notifyMerBkUrl(tseq,t,function(msg){alert(msg);});
 }
   