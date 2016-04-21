//////////////手动代付申请 js文件
var w_ptype={};
var array=null;
var route={};
//初始化成功
function initB2EGate(){
	PageParam.initAdminStatePtype(function(list){
		  w_ptype=list[1];
	  });
	$("bdate").value = jsToday();
    $("edate").value = jsToday();
	dwr.util.addOptions("mstate",m_mstate);
	initMinfos();
	initGateChannel2([11,12]);
	queryDataforSGSYNC(1);
}
var query_cash = {};
function doSyncRes(){
	loadingMessage("正在处理，请稍候......");
	SGSyncDFResultService.reqQuery_Bank(array,function(str){
		alert(str);
		jQuery("#back").click();
		queryDataforSGSYNC(1);
	});
}


//查询代发请求银行失败数据
function queryDataforSGSYNC(pagNo){
	var uid=$("mid").value;
	var ptype=$("type").value;
	var tseq=$("tseq").value;
	var trans_flow=$("trans_flow").value;
	var mstate=$("mstate").value;
	var edate = $("edate").value;
    var bdate = $("bdate").value;
    var gate=$("gateRouteId").value;
    var state=$("state").value;
    SGSyncDFResultService.queryDataForSGSYNC(pagNo,15,uid,trans_flow,""+ptype,tseq,mstate,state,gate,bdate,edate,function(alist){
	var totalPayAmt=0;
	var totalTransFee=0;
	var cellFuns=[	
	              	function(obj) {
	              	return "<input type=\"checkbox\"  name=\"toCheck\" id=\"toCheck\" value=\""+ obj.tseq +"\">";
	              	},
	              	function(obj){ return obj.tseq;},
	              	function(obj){ return obj.type==11?"对私代付":"对公代付";},//交易类型
	              	function(obj){ return obj.p8;},//批次号 电银
					function(obj){ return obj.mid;}, //商户号
					function(obj){ return obj.mid;},//账户号 
					function(obj){ return m_minfos[obj.mid];},//账户名称
					function(obj){return gate_route_map2[obj.gid];},
					function(obj){ 
						totalPayAmt+=Number(obj.payAmt);
						 return div100(obj.amount);},
					function(obj){
							totalTransFee+=Number(obj.feeAmt);
							return div100(obj.feeAmt);},
					function(obj){return div100(obj.payAmt);},
					function(obj){return h_tstat[obj.tstat];},
					function(obj){ return h_gate2[obj.gate];},
					function(obj){ return obj.p2;},
					function(obj){ return shadowAcc(obj.p1);}
					];
	for(var i=0;i<alist.pageItems.length;i++){
			var o = alist.pageItems[i];
   	        query_cash[o.tseq] = o;
   	  
		}
		var str="<span style='float:left;'>&nbsp;<input type='button' id='chooseAll' name='chooseAll' value='全 选' onclick=\"checkAll('toCheck');;\">&nbsp;&nbsp;&nbsp;";
		 str +=" 交易总金额:<font color='red'><b><span id='totalPayAmt'></span>元</font></b>";
		 str +=" 系统手续费总金额:<font color='red'><b><span id='totalTransFee'></span>元</font></b>&nbsp;&nbsp;&nbsp;&nbsp;";
		 str +="<input type='button' id='dfS' value = '手工银行结果同步' onclick='do_confirm(1);'/>&nbsp;&nbsp;&nbsp;&nbsp;";
		 str +="<input type='button' id='dfF' value = '通知商户结果同步' onclick='do_confirm(2);'/>&nbsp;&nbsp;&nbsp;&nbsp;";
		 str +="</span>";
		 paginationTable(alist,"resultList",cellFuns,str,"queryDataforSGSYNC");
		 if(alist.pageItems.length!=0){
		 document.getElementById("totalPayAmt").innerHTML=div100(totalPayAmt);
		 document.getElementById("totalTransFee").innerHTML=div100(totalTransFee);
		 }
	});
}
//经办查询下载
function downSGDFData(){
	var uid=$("mid").value;
	var ptype=$("type").value;
	var tseq=$("tseq").value;
	var state=$("state").value;
	var gate=$("gateRouteId").value;
	var trans_flow=$("trans_flow").value;
	var mstate=$("mstate").value;
	var edate = $("edate").value;
    var bdate = $("bdate").value;
    SGSyncDFResultService.downSGSYNCDFData(uid,trans_flow,""+ptype,tseq,mstate,state,gate,bdate,edate,
    		{callback:function(data){dwr.engine.openInDownload(data);}, async:false});}//把ajax调用设置为同步
//代发经办失败
function doNoticeMer(){
	loadingMessage("正在处理，请稍候......");
	SGSyncDFResultService.reqNotice_Mer(array,function(res){
		if(res!="")alert(res);
		jQuery("#back").click();
		queryDataforSGSYNC(1);
	});
}

/***
 * 1 成功  2 失败
 */
function do_confirm(index1){
	array = new Array();
	var chkArray = document.getElementsByName("toCheck");
    var index = 0;
    for (var i = 0 ; i < chkArray.length; i++){
    	if(chkArray[i].checked){
	      var Log = query_cash[chkArray[i].value];
	      array[index] = Log;
	      index ++;
    	}
    }
	  if (index == 0){
	      alert("请至少选中一条记录!");
	      return false;  
	 }else if (index1==2 ) {
		 if(checkNoticeMer()){
			 addAttrDisabled();
			 doNoticeMer();///通知商户
		 }
	 }else if (index1==1 ){
		 if(checkSyncRes()){
			 addAttrDisabled();
			 doSyncRes();////手工同步代付结果
		 }
	 }		
}


//提交 按钮改成灰色
function addAttrDisabled(){
	  jQuery("#submit").attr("disabled","disabled");
	  jQuery("#back").attr("disabled","disabled");
	  jQuery("#dfF").attr("disabled","disabled");
	  jQuery("#dfS").attr("disabled","disabled");
}	
//check 发起手工同步时数据校验
function checkSyncRes(){
	var size=array.length;
	var alerm="代付流水：";
	var alerm2="";
	for(var i=0;i<=size-1;i++){
		var tseq=array[i].tseq;
		var state=array[i].tstat;
		if(state ==3 || state ==4 || state==2 ){
			alerm+=tseq+"/";
			alerm2+="12";
		}
	}
	alerm=alerm.substring(0, alerm.length-1);
	alerm+="该交易已存在最终结果状态，无需同步银行结果"+"  \r\n";
	if(alerm2!=""){alert(alerm);return false;}
	return true;
}
//check 发起手工通知商户时数据校验
function checkNoticeMer(){
	var size=array.length;
	var alerm="代付流水：";
	var alerm2="";
	for(var i=0;i<=size-1;i++){
		var tseq=array[i].tseq;
		var state=array[i].tstat;
		if(state ==1){
			alerm+=tseq+"/";
			alerm2+="12";
		} 
		
	}
	alerm=alerm.substring(0, alerm.length-1);
	alerm+="该交易不存在最终结果状态，无需通知商户"+"  \r\n";
	if(alerm2!=""){alert(alerm);return false;}
	return true;
}
/*加载人工操作按钮*/
function loadOperating(obj){
	var state=obj.tstat;
	var p8=obj.p8;
	if ((state==3 ||state ==2 ) && p8==""){
		return "<input type='button' id='dfF' value = '通知商户结果同步' onclick='doNoticeMer("+obj+");'/>";
	}else if(state==4 || state ==1){
		return "<input type='button' id='dfS' value = '请求银行结果同步' onclick='doSyncRes("+obj+");'/>";
	}else{
		return "";
	}
}