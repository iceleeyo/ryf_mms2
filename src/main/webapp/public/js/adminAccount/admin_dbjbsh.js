var w_ptype=null;
//初始化
function initB2EGate(){
	PageParam.initAdminStatePtype(function(list){
//		  dwr.util.removeAllOptions("state");
//		  dwr.util.addOptions("state", {"":"请选择…"});
//		  dwr.util.addOptions("state",list[0]);
//		  
//		  w_state=list[0];
//		  dwr.util.removeAllOptions("ptype");
//		  dwr.util.addOptions("ptype", {"":"请选择…"});
//		  dwr.util.addOptions("ptype",list[1]);
		  w_ptype=list[1];
	  });
	initMinfos();
	dwr.util.addOptions("mstate",m_mstate);
//	queryDaiFaJingBan(1);
	
}
var query_cash = {};
function doB2EAction(){
	var array = new Array();
	var chkArray = document.getElementsByName("toCheck");
    var index = 0;
    var gid = dwr.util.getValue("b2egateid");
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
	  
	loadingMessage("正在查询，请稍候......");
	alert("可以到账户明细中查询订单状态。。");
	DaiFuService.doB2EAction(array,gid,function(str){
		alert(str);
		queryDaiFaJingBan(1);
	});
}
//查询B2E网关余额
function queryB2EGateBalance(){
	var gid = dwr.util.getValue("b2egateid");
if(gid){
	loadingMessage("正在查询，请稍候......");	
	DaiFuService.queryB2EGateBl(gid,function(blStr){
		dwr.util.setValue("b2egatebalance",blStr);
	});
}else{
		alert("请选择银企直连网关");
    }
}
//查询代发经办的数据
function queryDaiFaJingBan(pagNo){
	var uid=$("mid").value;
	var ptype=$("ptype").value;
	var oid=$("oid").value;
	var trans_flow=$("trans_flow").value;
	var mstate=$("mstate").value;
	var bdate=$("bdate").value;
	var edate=$("edate").value;
	var num=$("num").value;
	DaiFuService.queryDaiFaJingBan_SH(pagNo,num,uid,trans_flow,""+ptype,oid,mstate,bdate,edate,function(alist){
		$("fxpzTable").style.display="";
//		if(alist.length==0){
//		alert("当前没有代发经办审核数据");
//		dwr.util.removeAllRows("resultList");
//		return ;
//	}
	var totalPayAmt=0;
	var totalTransFee=0;
	var cellFuns=[	
	              	function(obj) {
	              	return "<input type=\"checkbox\"  name=\"toCheck\" id=\"toCheck\" value=\""+ obj.oid +"\">";
	              	},
	              	function(obj){ return obj.oid;},
	              	function(obj){ return obj.ptype==7?"对私代付":"对公代付";},
	              	function(obj){ return obj.trans_flow;},
	              /*	function(obj){ return obj.gate==40001?"中国银行(银企)":"交通银行(银企)";},*/
					function(obj){ return obj.uid;},
					function(obj){ return obj.aid;},
					function(obj){ return obj.aname;},
					function(obj){
						return div100(obj.transAmt);},
					function(obj){
							totalTransFee+=Number(obj.transFee);
							return div100(obj.transFee);},
					function(obj){totalPayAmt += Number(obj.transAmt);
					        return div100(obj.payAmt); },
					function(obj){ return obj.toBkName;},
					function(obj){ return obj.toAccName;},
					function(obj){ return obj.toAccNo;}					
					];
		for(var i=0;i<alist.pageItems.length;i++){
			var o = alist.pageItems[i];
   	        query_cash[o.oid] = o;
   	  
		}	
		var str="<span style='float:left;'>&nbsp;<input type='button' id='chooseAll' name='chooseAll' value='全 选' onclick=\"checkAll('toCheck');;\">&nbsp;&nbsp;&nbsp;";
		str +=" 交易总金额:<font color='red'><b><span id='totalPayAmt'></span>元</font></b>";
		str +=" 系统手续费总金额:<font color='red'><b><span id='totalTransFee'></span>元</font></b>";
		str +="&nbsp;&nbsp;&nbsp;选择银企直连网关:&nbsp;<select id='b2egateid'></select>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		str +="&nbsp;&nbsp;<span id='b2egatebalance' ></span>&nbsp;&nbsp;";
		str +="<input type='button' value = '查询当日余额' onclick='queryB2EGateBalance()'/>&nbsp;&nbsp;&nbsp;&nbsp;";
		str +="<input type='button' name='dfS' id='dfS' value = '代发审核成功' onclick='do_confirm(1);'/>&nbsp;&nbsp;&nbsp;&nbsp;";
		str +="<input type='button' name='dfF' id='dfF' value = '代发审核失败' onclick='do_confirm(2);'/>&nbsp;&nbsp;&nbsp;&nbsp;";
		 paginationTable(alist,"resultList",cellFuns,str,"queryDaiFaJingBan");
		 if(totalPayAmt!=0){
			 document.getElementById("totalPayAmt").innerHTML=div100(totalPayAmt);
			 document.getElementById("totalTransFee").innerHTML=div100(totalTransFee);
		 }
		 dwr.util.addOptions("b2egateid",bk_c_b2b );
	});
}

function downqueryDaiFaJingBansh(){
	var uid=$("mid").value;
	var ptype=$("ptype").value;
	var oid=$("oid").value;
	var trans_flow=$("trans_flow").value;
	var mstate=$("mstate").value;
	var bdate=$("bdate").value;
	var edate=$("edate").value;
   DaiFuService.downqueryDaiFaJingBansh(uid,trans_flow,""+ptype,oid,mstate,bdate,edate,
		{callback:function(data){dwr.engine.openInDownload(data);}, async:false});}//把ajax调用设置为同步

//人工触发Action
function doRGAction(){
	var array = new Array();
	var chkArray = document.getElementsByName("toCheck");
    var index = 0;
//    var gid = dwr.util.getValue("b2egateid");
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
	  if(!window.confirm("确认人工支付？")){
		 return;
	  }
	  
	loadingMessage("正在查询，请稍候......");
	DaiFuService.doRGAction(array,function(res){
		alert(res);
		queryDaiFaJingBan(1);
	});
}  

var array = null;
function do_confirm(index1){
	array=new Array();
	var chkArray = document.getElementsByName("toCheck");
    var index = 0;
    var option=$("option").value;
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
	  }else if(index1==2 && index <1){
		  alert("请选中一条记录进行代发审核失败处理!");
	      return false;
	  }
	  $("flag").value=index1+"";
	  jQuery("#hlogDetail_TX").wBox({title:"审核意见",show:true});//显示box
}

//提现经办，提现成功，提现失败   
//代付审核
function solve(flag){
		var flag=$("flag").value;
		var flag1=$("flag1").value;
		var option1=$("option").value.replace(/(\s*$)/g, "");
		if(option1==null||option1==""){
			alert("审核意见不能为空");return;
		}
	  if(flag==1){
		  if (confirm("确定审核?")){
			  addAttrDisabled();
			  SH_SUC();
		  }
	  }
//	  if(flag==2){
//		  if(confirm("确定审核？")){
//			  
//			  AdminZHService.updateSuccessTXCL(array,$("option").value,callback4Solve2);
//		  }
//	  }
	  if(flag==2){
		  if (confirm("确定审核失败?")){
			  addAttrDisabled();
			  SH_FAIL();
		  }
	  }
	  
}

function SH_SUC(){
	  
	loadingMessage("正在查询，请稍候......");
	var gid = dwr.util.getValue("b2egateid");
	var option=$("option").value.replace(/(\s*$)/g, "");
	DaiFuService.doB2EAction_SH_S(array,gid,option,function(str){
		alert(str);
		jQuery("#back").click();
		queryDaiFaJingBan(1);
		/*window.location.href="A_111_DFJBSH.jsp";*/
	});
	
}

function SH_FAIL(){
	  
	loadingMessage("正在查询，请稍候......");
	var option=$("option").value.replace(/(\s*$)/g, "");
	var gid = dwr.util.getValue("b2egateid");
	DaiFuService.doB2EAction_SH_F(array,gid,option,function(str){
		alert(str);
		jQuery("#back").click();
		queryDaiFaJingBan(1);
		/*window.location.href="A_111_DFJBSH.jsp";*/
	});	
}

//提交 按钮改成灰色
function addAttrDisabled(){
	  jQuery("#submit").attr("disabled","disabled");
	  jQuery("#back").attr("disabled","disabled");
	  jQuery("#dfF").attr("disabled","disabled");
	  jQuery("#dfS").attr("disabled","disabled");
	  jQuery("#dfRG").attr("disabled","disabled");
}	