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
    queryData(1);
}
var query_cash = {};

//查询代发请求银行失败数据
function queryData(pagNo){
	var uid=$("mid").value;
	var ptype=$("ptype").value;
	var tseq=$("tseq").value;
	var trans_flow=$("trans_flow").value;
	var mstate=$("mstate").value;
	var edate = $("edate").value;
	var bdate = $("bdate").value;
	var againPayStatus=$("againPayStatus").value;
	if(edate=="" || edate==undefined){
		alert("请选择结束日期!");
		return;
	}
    if(againPayStatus=="全部" ||againPayStatus==undefined || againPayStatus==""){
    	againPayStatus=null;
    }
    if(ptype==undefined || ptype==0){
    	ptype="";
    }
    SgDfQueryService.queryTlogInfo(pagNo,15,uid,tseq,trans_flow,""+ptype,mstate,againPayStatus,bdate,edate,function(alist){
	var totalPayAmt=0;
	var totalTransFee=0;
	var cellFuns=[	
	              	function(obj){ return obj.tseq;},
	              	function(obj){ return obj.type==11?"对私代付":"对公代付";},//交易类型
	              	function(obj){ return obj.p8;},//批次号 电银
	              	function(obj){ return obj.mid;}, //商户号
	              	function(obj){ return m_minfos[obj.mid];},//账户名称
	              	function(obj){ return obj.oid;},//账户号 
					function(obj){ return gate_route_map2[obj.gid];},
					function(obj){ return div100(-obj.amount);},
					function(obj){ return div100(obj.feeAmt);},
					function(obj){ return div100(-obj.payAmt);},
					function(obj){ return h_gate2[obj.gate];},
					function(obj){ return obj.p2;},
					function(obj){ return shadowAcc(obj.p1);},
					function(obj){ return h_tstat[obj.tstat];},
					function(obj){ return obj.tstat == 2 ? "" :createTip(8,obj.error_msg);},
					function(obj){ return convertAgainPayStatus(obj.againPay_status+"")}
					
					];
		for(var i=0;i<alist.pageItems.length;i++){
			var o = alist.pageItems[i];
   	        query_cash[o.tseq] = o;
   	  
		}		
		var str="";
		 str +=" 交易总金额:<font color='red'><b><span id='totalPayAmt'>"+div100(alist.sumResult.amtSum)+"</span>元</font></b>";
		 str +=" 系统手续费总金额:<font color='red'><b><span id='totalTransFee'>"+div100(alist.sumResult.sysAmtFeeSum)+"</span>元</font></b>";
		 str +="</span>";
		 paginationTable(alist,"resultList",cellFuns,str,"queryData");
	});
}
//经办查询下载
function downSGDFData(){
	var uid=$("mid").value;
	var ptype=$("ptype").value;
	var tseq=$("tseq").value;
	var trans_flow=$("trans_flow").value;
	var mstate=$("mstate").value;
	var edate = $("edate").value;
    var bdate = $("bdate").value;
	var againPayStatus=$("againPayStatus").value;
	if(edate=="" || edate==undefined){
		alert("请选择结束日期!");
		return;
	}
    if(againPayStatus=="全部" ||againPayStatus==undefined || againPayStatus==""){
    	againPayStatus=null;
    }
    if(ptype==undefined || ptype==0){
    	ptype="";
    }
	SgDfQueryService.downSGDFData(uid,tseq,trans_flow,""+ptype,mstate,againPayStatus,bdate,edate,
    		{callback:function(data){dwr.engine.openInDownload(data);}, async:false});//把ajax调用设置为同步
}

function convertAgainPayStatus(againPayStatus){
	if("2"==againPayStatus){
		return "申请成功";
	}else if("5"==againPayStatus){
		return "申请撤销";
	}else if("3"==againPayStatus){
		return "审核成功";
	}else if("4"==againPayStatus){
		return "审核撤销";
	}
	
}
