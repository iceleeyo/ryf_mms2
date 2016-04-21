//////////////手动代付申请 js文件
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
		return;
	}
    var bdate = $("bdate").value;
    SgDfSqService.queryDataForReq(pagNo,15,uid,trans_flow,""+ptype,tseq,mstate,bdate,edate,function(alist){
	var totalPayAmt=0;
	var totalTransFee=0;
	var cellFuns=[	
	              	function(obj) {
	              	return (checkStatus(obj.tstat,obj.againPay_status)==true)? "<input type=\"checkbox\"  name=\"toCheck\" id=\""+obj.tseq+"\" value=\""+ obj.tseq +"\">":"";
	              	},
	              	function(obj){ return obj.tseq;},
	              	function(obj){ return obj.oid;},//账户号 
	              	function(obj){ return obj.type==11?"对私代付":"对公代付";},//交易类型
	              	function(obj){ return obj.p8;},//批次号 电银
	              	/*function(obj){if(obj.gate==0){return "";} return obj.gate==40001?"中国银行(银企)":"交通银行(银企)";},*/
					function(obj){ return obj.mid;}, //商户号
					function(obj){ return m_minfos[obj.mid];},//账户名称
					function(obj){ return gate_route_map2[obj.gid];},
					function(obj){ return div100(-obj.amount);},
					function(obj){ return div100(obj.feeAmt);},
					function(obj){ return div100(-obj.payAmt);},
					function(obj){ return h_gate2[obj.gate];},
					function(obj){ return obj.p2;},
					function(obj){ return shadowAcc(obj.p1);},
					function(obj){ return h_tstat[obj.tstat];},
					function(obj){ return obj.tstat == 2 ? "" :createTip(8,obj.error_msg);}
					
					];
		for(var i=0;i<alist.pageItems.length;i++){
			var o = alist.pageItems[i];
   	        query_cash[o.tseq] = o;
   	  
		}		
		var str="<span style='float:left;'>&nbsp;<input type='button' id='chooseAll' name='chooseAll' value='全 选' onclick=\"checkAll('toCheck');;\">&nbsp;&nbsp;&nbsp;";
		 str +=" 交易总金额:<font color='red'><b><span id='totalPayAmt'>"+div100(alist.sumResult.amtSum)+"</span>元</font></b>";
		 str +=" 系统手续费总金额:<font color='red'><b><span id='totalTransFee'>"+div100(alist.sumResult.sysAmtFeeSum)+"</span>元</font></b>";
		 str +="<input type='button' id='dfS' class='button' value = '申 请' onclick='do_confirm(1);'/>&nbsp;&nbsp;&nbsp;&nbsp;";
		 str +="<input type='button' id='dfF' class='button' value = '撤 销' onclick='do_confirm(2);'/>&nbsp;&nbsp;&nbsp;&nbsp;";
		 str +="</span>";
		 paginationTable(alist,"resultList",cellFuns,str,"queryDataforReqFail");
	});
}
//经办查询下载
function downSGDFData(){
	var uid=$("mid").value;
	var ptype=$("ptype").value;
	var tseq=$("tseq").value;
	var trans_flow=$("trans_flow").value;
	var mstate=$("mstate").value;
	/*var edate = $("edate").value;
    var bdate = $("bdate").value;*/
	SgDfSqService.downSGDFData(uid,trans_flow,""+ptype,tseq,mstate,
    		{callback:function(data){dwr.engine.openInDownload(data);}, async:false});}//把ajax调用设置为同步

/***
 * 1 申请  2 拒绝
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
	 }else if (index1==2 && confirm("确定发起撤销操作？")) {
		 addAttrDisabled();
		 doPlRefuse();
	 }else if (index1==1 && confirm("确定发起申请？")){//批量申请页面
		 addAttrDisabled();
		 doPlSq();
	 }		
}

function doPlRefuse(){
	loadingMessage("正在处理，请稍候......");
	SgDfSqService.sgdfRefuse(array,function(result){
		if(result==true){
			alert("撤销操作成功");
		}else{
			alert("撤销操作失败");
		}
		remAttrDisabled();
		jQuery("#back").click();
		queryDataforReqFail(1);
	});
}
//发起批量申请请求
function doPlSq(){
	loadingMessage("正在处理，请稍候......");
	SgDfSqService.sgdf(array,function(result){
		if(result==true){
			alert("申请操作成功");
		}else{
			alert("申请操作失败");
		}
		remAttrDisabled();
		jQuery("#back").click();
		queryDataforReqFail(1);
	});
}
//申请代付请求 单笔doSingleSq
function doSSq(tseq){
	var checked=document.getElementById(tseq);
	array=new Array();
	if(checked.checked){
		array[0]=query_cash[checked.value];
	}
	if(array.length==0){
		 alert("请至少选中一条记录!");
	}else{
		if(!confirm("确认发起申请？")){
			return;
		}
		doPlSq();
	}
}


//提交 按钮改成灰色
function addAttrDisabled(){
	  jQuery("#submit").attr("disabled","disabled");
	  jQuery("#back").attr("disabled","disabled");
	  jQuery("#dfF").attr("disabled","disabled");
	  jQuery("#dfS").attr("disabled","disabled");
}	


//提交 按钮释放
function remAttrDisabled(){
	  jQuery("#submit").removeAttr("disabled");
	  jQuery("#back").removeAttr("disabled");
}	
//判断条件是否允许申请
function checkStatus(tstat,againPayStatus){
	if(tstat==3){
		return true;
	}
	return false;
}