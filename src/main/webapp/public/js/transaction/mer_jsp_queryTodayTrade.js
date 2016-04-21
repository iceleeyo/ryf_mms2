
	    
 function init(){
	 CommonService.getMerGatesMap($("mid").value,function(map){
		 dwr.util.addOptions("gate", map);
		 h_gate=map;
	});
     dwr.util.addOptions("tstat", h_tstat);
     dwr.util.addOptions("type", h_type);
 }
 var mid,oid,gate,tstat,type,bkseq,tseq,begintrantAmt,endtrantAmt;
 function judgeCondition(){
      mid = $("mid").value;
      oid = $("oid").value;
      gate = $("gate").value;
      tstat = $("tstat").value;
      type = $("type").value;
      tseq = $("tseq").value;
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
 // 查询
 function queryMerToday(pageNo){
	 if(!judgeCondition())return;
		jQuery("#toggleAll").attr("checked",false);
	 QueryMerMerTodayService.queryMerToday(pageNo,mid,gate,tstat, type,tseq,oid,null,"",begintrantAmt,endtrantAmt,callBack2);

 }
 //下载
 function downloadToday(){
	  if(!judgeCondition())return;
  	  dwr.engine.setAsync(false);//把ajax调用设置为同步
  	QueryMerMerTodayService.downloadToday_MER(mid,gate,tstat, type,tseq,oid,null,"",begintrantAmt,endtrantAmt,
    		       function(data){dwr.engine.openInDownload(data);});
}		
 //回调函数
 var callBack2 = function(pageObj){
	  $("merTodayTable").style.display="";
	   dwr.util.removeAllRows("resultList");
	   if(pageObj==null){document.getElementById("resultList").appendChild(creatNoRecordTr(14));return;}
		 var cellFuncs = [
		                  function(obj) { if(obj.tstat == 2 || obj.tstat==3) return '<input name="check" type="checkbox" value="'+obj.tseq+'">';
		                  				  else return '';},
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
		                    return "<input type=\"button\" value=\" 请求商户后台  \" onclick=\"sendRequests('" + obj.tseq + "','tlog')\" >";
		                    else return '';
		                      }
		              ];
      str = "<span class='pageSum'><input onclick='batchNotifyMerBkUrl();' style='margin-left:20px;' type='button' value='批量通知'>&nbsp;&nbsp;交易总金额：<font color='red'><b>" + div100(pageObj.sumResult.amtSum)
       	   +"</b></font>  元</span> <span class='pageSum'>&nbsp;&nbsp;系统手续费总金额：<font color='red'><b>"+ div100(pageObj.sumResult.sysAmtFeeSum)+"</b></font>  元</span>";
       paginationTable(pageObj,"resultList",cellFuncs,str,"queryMerToday");
   };
 //商户请求后台
 function sendRequests(tseq,t){
	 	useLoadingImage("../../public/images/wbox/loading.gif");
	 	QueryMerMerTodayService.notifyMerBkUrl(tseq,t,function(msg){
	 		alert(msg);
	 		queryMerToday(1);
 		});
 }
 
 /**
	 * @param status
	 * 批量启用
	 */
	function batchNotifyMerBkUrl(){
		var tseqs = new Array();
		var checkboxes=document.getElementsByName("check");
		for(var i = 0;i<checkboxes.length;i++){
			if(checkboxes[i].checked){
				tseqs.push(checkboxes[i].value);
			}
		}
		if(!tseqs.length){
			alert('请选择要通知的交易');
			return;
		}
	 	useLoadingImage("../../public/images/wbox/loading.gif");
	 	QueryMerMerTodayService.batchNotifyMerBkUrl(tseqs,'tlog',function(msg){
			alert(msg);
			queryMerToday(1);
		});
	}
  
 /**
 * @param o 
 * 全选
 */
	function toggleAll(o){
		var status = o.checked;
		var checkboxes=document.getElementsByName("check");
		for(var i = 0;i<checkboxes.length;i++){
			checkboxes[i].checked=status;
		}
	} 

 /****
  * 选择交易类型  交易银行联动功能
  * 
  * 
  */    
function  onChangeGate(){
	 var t = $("type").value;
	 if(t==''){
		 t=-1;
	 }
	 PageService.getGateChannelMapByType(t,function(m){
			h_gate=m[0];
		/*	gate_route_map=m[1];*/
			if($("gate")){
				dwr.util.removeAllOptions("gate");
				dwr.util.addOptions("gate",{'':'全部...'});
				dwr.util.addOptions("gate", h_gate);
			}
		/*	if($("gateRouteId")){
				dwr.util.removeAllOptions("gateRouteId");
				dwr.util.addOptions("gateRouteId",{'':'全部...'})
				dwr.util.addOptions("gateRouteId", gate_route_map);
			}*/
		 
	 });
} 
 