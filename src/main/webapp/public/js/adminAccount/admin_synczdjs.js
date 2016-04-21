//////////////自动结算同步结果 js文件
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
	queryDataforAutoSettlement(1);
}
var query_cash = {};

function doSyncRes(){
	loadingMessage("正在处理，请稍候......");
	HKResultHandleService.reqQuery_Bank(array,function(str){
		alert(str);
		queryDataforAutoSettlement(1);
	});
}


//查询代发请求银行失败数据
function queryDataforAutoSettlement(pagNo){
	var uid=$("mid").value;
	var liqBatch=$("liqBatch").value; //结算批次
	var edate = $("edate").value;
    var bdate = $("bdate").value;
    var state=$("state").value ;//划款状态
    HKResultHandleService.queryDataForAutoSettlement(pagNo,15,uid,liqBatch,state,bdate,edate,function(alist){
	var cellFuns=[	
	              	function(obj) {
	              	return "<input type=\"checkbox\"  name=\"toCheck\" id=\"toCheck\" value=\""+ obj.tseq +"\">";
	              	},
	              	function(obj){ return obj.tseq;},//电银流水号
	              	function(obj){ return obj.mid;}, //商户号
	              	function(obj){ return obj.p9;},//结算批次号 电银
	              	function(obj){return obj.sysDate;},//结算确认日期
					function(obj){ return m_minfos[obj.mid];},//商户名称
//					function(obj){return obj.bank_branch;},//开户支行银行名称
					function(obj){return obj.p5;},//开户支行银行名称
					function(obj){return obj.p2;},//开户账户名称
					function(obj){return shadowAcc(obj.p1);},//开户账户号
					function(obj){return h_tstat[obj.tstat];},//划款状态
					function(obj){ return div100(obj.amount);},//划款金额
					function(obj){ return h_gate2[obj.gate];},//交易银行
					function(obj){return gate_route_map2[obj.gid];}//支付渠道
					];
	for(var i=0;i<alist.pageItems.length;i++){
		var o = alist.pageItems[i];
        query_cash[o.tseq] = o;
	}
		var str="<span style='float:left;'>&nbsp;<input type='button' id='chooseAll' name='chooseAll' value='全 选' onclick=\"checkAll('toCheck');;\">&nbsp;&nbsp;&nbsp;";
//		 str +="<select    id='handleType' name='handleType' ><option value='0'>请选择...</option><option value='1'>手工提交代付</option><option value='2'>同步银行结果</option><option value='3'>手工重新提交代付</option></select>&nbsp;&nbsp;&nbsp;&nbsp;";
		 str +="<input type='button' id='dfF' value = '手工同步结果' onclick='do_confirm(1);'/>&nbsp;&nbsp;&nbsp;&nbsp;";
		 str +="</span>";
		 paginationTable(alist,"resultList",cellFuns,str,"queryDataforAutoSettlement");
	});
}
//经办查询下载
function downAutoSettlementData(){
	var uid=$("mid").value;
	var state=$("state").value;
	var liqBatch=$("liqBatch").value;
	var edate = $("edate").value;
    var bdate = $("bdate").value;
    HKResultHandleService.downAutoSettlementData(uid,liqBatch,state,bdate,edate,
    		{callback:function(data){dwr.engine.openInDownload(data);}, async:false});}//把ajax调用设置为同步

/***
 *1 手工同步结果
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
	 }
	  
	 if (index1==1 ){
			 if(checkSyncRes()){
				if(confirm("确认发起操作？")){
					addAttrDisabled();
					doSyncRes();////手工同步代付结果
				}
			 }
	 }		
}


//提交 按钮改成灰色
function addAttrDisabled(){
	  jQuery("#dfF").attr("disabled","disabled");
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