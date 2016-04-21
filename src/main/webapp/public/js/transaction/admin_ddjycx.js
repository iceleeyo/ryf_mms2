 function init(){
     dwr.util.addOptions("tstat", h_tstat);
     dwr.util.addOptions("type", h_type);
     onChangeGate();
 }
 
 jQuery(function(){
		jQuery(":checkbox[name='check']").live('click', function() {
			var status = this.checked?"true":"false";
			var checkboxes=document.getElementsByName("check");
			for(var i = 0;i<checkboxes.length;i++){
				var status1 = checkboxes[i].checked?"true":"false";
				if(status == status1){
					continue;
				}else{
					if(!this.checked){
						jQuery("#toggleAll").attr("checked",this.checked);
					}
					return;
				}
			}
			jQuery("#toggleAll").attr("checked",this.checked);
		});
	});
 var mid,oid,gate,tstat,type,tseq,amount,bdate,edate;
 function judgeCondition(){
      mid = $("mid").value.trim();
      oid = $("oid").value.trim();
      gate = $("gate").value;
      tstat = $("tstat").value;
      type = $("type").value;
      tseq = $("tseq").value.trim();
      amount = $("amount").value.trim();
      bdate = $("bdate").value;
      edate = $("edate").value;
	if (amount) {
		var regExp = /^(([1-9]\d*)|\d)(\.\d{1,2})?$/
		if (!regExp.test(amount)) {
			alert("金额必须是正数，小数部分不能多于两位!");
			$("amount").value = '';
			amount = "";
			$("amount").focus();
			return false;
		}
	}
	if (tseq) {
		tseq = tseq.trim();
		if (!isNumber(tseq)) {
			alert("电银流水号必须是数字!");
			$("tseq").value = '';
			$("tseq").focus();
			tseq='';
			return false;
		}
	}
	if(!bdate){
		alert("请输入交易开始时间");
		$("bdate").focus();
		return false;
	}
	if(!edate){
		alert("请输入交易截至时间");
		$("edate").focus();
		return false;
	}
	return true;
}
 // 查询
 function queryNotifyFailedOrderRecord(pageNo){
	 if(!judgeCondition())return;
	 jQuery("#toggleAll").attr("checked",false);
	 TransactionService.queryNotifyFailedOrderRecord(pageNo,mid,oid,gate,tstat, type,tseq,amount,bdate,edate,callBack2);

 }
 //下载
 function downloadNotifyFailedRecord(){
	  if(!judgeCondition())return;//根据当前的查询条件下载
  	  dwr.engine.setAsync(false);//把ajax调用设置为同步
  	  TransactionService.downloadNotifyFailedOrderRecord(mid,oid,gate,tstat, type,tseq,amount,bdate,edate,function(data){
  		  dwr.engine.openInDownload(data);
	  });
}		
 //回调函数
 var callBack2 = function(pageObj){
	  $("notifyFailedTable").style.display="";
	   dwr.util.removeAllRows("resultList");
	   if(pageObj==null){ document.getElementById("resultList").appendChild(creatNoRecordTr(14)); return; }
	   var cellFuncs = [
		                  function(obj) { return '<input name="check" type="checkbox" value="'+obj.tseq+'">'; },
		                  function(obj) { return obj.mid; },
		                  function(obj) { return obj.oid; },
		                  function(obj) { return div100(obj.amount); },
		                  function(obj) { return h_tstat[obj.tstat]; },
		                  function(obj) { return h_type[obj.type]; },
		                  function(obj) { return h_gate[obj.gate]; },
		                  function(obj) { return div100(obj.feeAmt); },
		                  function(obj) { return obj.sysDate;},
		                  function(obj) { return "<input type=\"button\" value=\" 通知商户  \" onclick=\"sendRequests('" + obj.tseq + "','tlog')\" >"; }
		              ];
      str = "<input onclick='batchNotifyMerBkUrl(\"tlog\");' style='margin-left:20px;' type='button' value='批量通知'>";
      paginationTable(pageObj,"resultList",cellFuncs,str,"queryNotifyFailedOrderRecord");
   }
 
//商户请求后台
 function sendRequests(tseq,t){//怎么知道哪条记录是来自那个表？？
	 if(!confirm("通知商户？")){
		 return;
	 }
 	var array = new Array();
 	array.push(tseq);
 	useLoadingImage("../../public/images/wbox/loading.gif");
 	QueryMerMerTodayService.batchNotifyMerBkUrl(array,t,function(msg){
 		alert(msg);
 		queryNotifyFailedOrderRecord(1);
	});
 }
 
function batchNotifyMerBkUrl(t){
	if(!confirm("通知商户？")){
		return;
	}
	var array = new Array();
	jQuery(":checkbox[name='check'][checked=true]").each(function(){
		array.push(this.value);
	});
	if(array.length==0){
		alert("请选择订单!");
		return;
	}
	QueryMerMerTodayService.batchNotifyMerBkUrl(array,t,function(msg){
 		alert(msg);
 		queryNotifyFailedOrderRecord(1);
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
  */    
function  onChangeGate(){
	 var t = $("type").value;
	 if(t==''){
		 t=-1;
	 }
	 PageService.getGateChannelMapByType(t,function(m){
			h_gate=m[0];
			if($("gate")){
				dwr.util.removeAllOptions("gate");
				dwr.util.addOptions("gate",{'':'全部...'})
				dwr.util.addOptions("gate", h_gate);
			}
	 })
} 
 