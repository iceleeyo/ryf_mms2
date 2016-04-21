   function init(){
	   CommonService.getMerGatesMap($("mid").value,function(map){
		   dwr.util.addOptions("gate", map);
		   h_gate=map;
	   });
       dwr.util.addOptions("tstat", h_tstat);
       dwr.util.addOptions("type", h_type);
   }
   //查询条件的判断
   var mid,oid,gate,tstat,type,date,bdate,edate;
   function judgeCondition(){
        mid = $("mid").value;
        oid = $("oid").value;
        gate = $("gate").value;
        tstat = $("tstat").value;
        type = $("type").value;
        date = $("date").value;
        bdate = $("bdate").value;
        edate = $("edate").value;
       if(bdate=='') {
           alert("请选择起始日期！"); 
           return false; 
       }
       if(edate==''){
            alert("请选择结束日期！"); 
            return false; 
       }
       return true;
   }
   //商户明细查询 
   function queryMerHlog(pageNo){
	   if(!judgeCondition())return;
       TransactionService.queryHlogDetail(pageNo, mid,gate,tstat,type,oid,null,date,bdate,edate,null,callBack2);
   }
   //商户明细查询下载
   function downloadDetail(){
	    if(!judgeCondition())return;
	    dwr.engine.setAsync(false);//把ajax调用设置为同步
	    TransactionService.downloadDetail_MER(mid,gate,tstat,type,oid,null,date,bdate,edate,
	    				function(data){dwr.engine.openInDownload(data);})
	}
   var callBack2 = function(pageObj){
	   $("merHlogTable").style.display="";
	   if(pageObj==null)document.getElementById("resultList").appendChild(creatNoRecordTr(11));
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
	                    function(obj) { return obj.sysDate; },
	                    function(obj) {
	                        if(obj.tstat == 2 || obj.tstat==3)
	                      return "<input type=\"button\" value=\" 请求商户后台  \" onclick=\"sendRequests(" + obj.tseq + ",'hlog')\" >";
	                      else return '';
	                        }
	                ]	  
         str = "<span class='pageSum'>交易总金额：<font color='red'><b>" + div100(pageObj.sumResult.amtSum)
         		+"</b></font>  元</span> <span class='pageSum'>&nbsp;&nbsp;系统手续费总金额：<font color='red'><b>"+ div100(pageObj.sumResult.sysAmtFeeSum)+"</b></font>  元</span>";
         paginationTable(pageObj,"resultList",cellFuncs,str,"queryMerHlog");
     }
 //商户请求后台
   function sendRequests(tseq,t){
		useLoadingImage("../../public/images/wbox/loading.gif");
	   TransactionService.notifyMerBkUrl(tseq,t,function(msg){alert(msg);});
   }
