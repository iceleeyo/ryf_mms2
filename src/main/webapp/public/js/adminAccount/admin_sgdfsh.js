//手动代付审核 js文件
var w_ptype={};
var array=null;
//初始化成功
function initB2EGate(){
	PageParam.initAdminStatePtype(function(list){
		  w_ptype=list[1];
	  });
	initMinfos();
	$("bdate").value = jsToday();
    $("edate").value = jsToday();
	initGateChannel2([11,12,16,17]);
    dwr.util.addOptions("mstate",m_mstate);
    queryDataforReqFail(1);
}
var query_cash = {};

//查询代发请求银行失败数据
function queryDataforReqFail(pagNo){
	var uid=$("mid").value;
	var ptype=$("ptype").value;
	var tseq=$("tseq").value;
	var trans_flow=$("trans_flow").value;
	var mstate=$("mstate").value;
	var edate = $("edate").value;
	if(edate=="" || edate==undefined){
		alert("请选择结束日期!");
		return ;
	}
    var bdate = $("bdate").value;
    SgDfShService.queryDataForReq(pagNo,15,uid,trans_flow,""+ptype,tseq,mstate,bdate,edate,function(alist){
	var totalPayAmt=0;
	var totalTransFee=0;
	var cellFuns=[	
	              	function(obj) {
	              	return "<input type=\"checkbox\"  name=\"toCheck\" id=\"toCheck\" value=\""+ obj.tseq +"\">";
	              	},
	              	function(obj){ return obj.tseq;},//流水
	              	function(obj){ return obj.type==11?"对私代付":"对公代付";},//交易类型
	              	function(obj){ return obj.p8;},//批次号 电银
					function(obj){ return obj.mid;}, //商户号
					function(obj){ return m_minfos[obj.mid];},//账户简称
					function(obj){ return obj.oid;},//商户订单号
					function(obj){return gate_route_map2[obj.gid];},//支付渠道
					function(obj){ 
						totalPayAmt+=Number(obj.payAmt);
						 return div100(-obj.amount);},//交易金额
					function(obj){
							totalTransFee+=Number(obj.feeAmt);
							return div100(obj.feeAmt);},//交易手续费
					function(obj){return div100(-obj.payAmt);},//付款金额
					function(obj){ return h_gate2[obj.gate];},//收款银行
					function(obj){ return obj.p2;},//收款人用户名
					function(obj){ return shadowAcc(obj.p1);},//收款人账号
					function(obj){ return h_tstat[obj.tstat];},//交易状态
					function(obj){ return createTip(8,obj.error_msg);}//失败原因
					
					];
	for(var i=0;i<alist.pageItems.length;i++){
		var o = alist.pageItems[i];
	        query_cash[o.tseq] = o;
	}
	var str="<span style='float:left;'>&nbsp;<input type='button' id='chooseAll' name='chooseAll' value='全 选' onclick=\"checkAll('toCheck');;\">&nbsp;&nbsp;&nbsp;";
	 str +=" 交易总金额:<font color='red'><b><span id='totalPayAmt'>"+div100(alist.sumResult.amtSum)+"</span>元</font></b>";
	 str +=" 系统手续费总金额:<font color='red'><b><span id='totalTransFee'>"+div100(alist.sumResult.sysAmtFeeSum)+"</span>元</font></b>";
	 str +="<input type='button' id='dfS' class='button' value = '审 核' onclick='do_confirm(1,0);'/>&nbsp;&nbsp;&nbsp;&nbsp;";
	 str +="<input type='button' id='dfF' class='button' value = '审核撤销' onclick='do_confirm(2,0);'/>&nbsp;&nbsp;&nbsp;&nbsp;";
	 str +="</span>";
		 paginationTable(alist,"resultList",cellFuns,str,"queryDataforReqFail");
	});
}

//下载当日手工代付代付成功数据
function downSGDFData(){
	var uid=$("mid").value;
	var ptype=$("ptype").value;
	var tseq=$("tseq").value;
	var trans_flow=$("trans_flow").value;
	var mstate=$("mstate").value;
	SgDfShService.downSGDFData(uid,trans_flow,""+ptype,tseq,mstate,
    		{callback:function(data){dwr.engine.openInDownload(data);}, async:false});}//把ajax调用设置为同步
//撤销
function doRevocation(flag){
	loadingMessage("正在处理，请稍候......");
	 var option=$("option").value.replace(/(\s*$)/g, "");
	 SgDfShService.reqRevocation(array,option,function(res){
		alert(res);
		remAttrDisabled();
		queryDataforReqFail(1);
	});
}
  
//发起请求银行
function doB2EAction(){
	loadingMessage("正在处理，请稍候......");
	SgDfShService.reqPayDf(array,function(str){
		alert(str);
		remAttrDisabled();
		queryDataforReqFail(1);
	});
}

/***
 * 1 审核  2 拒绝
 */
function do_confirm(index1,tseq){
	array = new Array();
	var chkArray = document.getElementsByName("toCheck");
    var index = 0;
	if(tseq==0){
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
	}else{
	    	var tseq1=query_cash[tseq];
	    	array[index]=tseq1;
	}
	if (index1==2 && confirm("确认发起审核撤销操作？")) {
		addAttrDisabled();
		doRevocation();
	 }else if (index1==1 && confirm("确认发起审核操作？") ){
		 addAttrDisabled();
		 doB2EAction();
	 }		
}

//提交 按钮释放
function remAttrDisabled(){
	  jQuery("#dfS").removeAttr("disabled");
	  jQuery("#dfF").removeAttr("disabled");
}	

function addAttrDisabled(){
	  jQuery("#dfS").attr("disabled","disabled");
	  jQuery("#dfF").attr("disabled","disabled");
}