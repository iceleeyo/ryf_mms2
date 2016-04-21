var w_ptype={};
var array=null;
//初始化
function initB2EGate(){
//	queryDaiFaJingBan();
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
	queryDaiFaJingBan(1);
}
var query_cash = {};
function doB2EAction(){
	loadingMessage("正在查询，请稍候......");
	//alert("可以到账户明细中查询订单状态。。");
	 var option=$("option").value.replace(/(\s*$)/g, "");
	DaiFuService.doB2EAction(array,option,function(str){
		alert(str);
		jQuery("#back").click();
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
	var num=$("num").value;
	var trans_flow=$("trans_flow").value;
	var mstate=$("mstate").value;
	var edate = $("edate").value;
    var bdate = $("bdate").value;
	DaiFuService.queryDaiFaJingBan_(pagNo,num,uid,trans_flow,""+ptype,oid,mstate,bdate,edate,function(alist){
//		if(alist.pageItems.length==0){
//		alert("当前没有代发经办数据");
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
	              	/*function(obj){if(obj.gate==0){return "";} return obj.gate==40001?"中国银行(银企)":"交通银行(银企)";},*/
					function(obj){ return obj.uid;},
					function(obj){ return obj.aid;},
					function(obj){ return obj.aname;},
					function(obj){
						 return div100(obj.transAmt);},
					function(obj){
							totalTransFee+=Number(obj.transFee);
							return div100(obj.transFee);},
					function(obj){totalPayAmt+=Number(obj.payAmt);
					        return div100(obj.payAmt);},
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
		 str +="<input type='button' id='dfS' value = '代发经办' onclick='do_confirm(1);'/>&nbsp;&nbsp;&nbsp;&nbsp;";
		 str +="<input type='button' id='dfF' value = '代发拒绝' onclick='do_confirm(2);'/>&nbsp;&nbsp;&nbsp;&nbsp;";
		 str +="</span>";
		 paginationTable(alist,"resultList",cellFuns,str,"queryDaiFaJingBan");
		 if(alist.pageItems.length!=0){
		 document.getElementById("totalPayAmt").innerHTML=div100(totalPayAmt);
		 document.getElementById("totalTransFee").innerHTML=div100(totalTransFee);
		 }
	});
}
//经办查询下载
function downqueryDaiFaJingBan(){
	var uid=$("mid").value;
	var ptype=$("ptype").value;
	var oid=$("oid").value;
	var trans_flow=$("trans_flow").value;
	var mstate=$("mstate").value;
	var edate = $("edate").value;
    var bdate = $("bdate").value;
	DaiFuService.downqueryDaiFaJingBan(uid,trans_flow,""+ptype,oid,mstate,edate,bdate,
    		{callback:function(data){dwr.engine.openInDownload(data);}, async:false});}//把ajax调用设置为同步
//代发经办失败
function doActionFail(flag){
	loadingMessage("正在查询，请稍候......");
	 var option=$("option").value.replace(/(\s*$)/g, "");
	DaiFuService.doB2EActionFail(array,option,function(res){
		alert(res);
		jQuery("#back").click();
		queryDaiFaJingBan(1);
	});
}
  

/***
 * 1 成功  2 失败
 */
function do_confirm(index1){
	$("flag").value=index1;
	array = new Array();
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
//	  }else if(index1==2 && index>1) {
//		alert("请选择一条进行经办失败处理!");return false;
	}
	jQuery("#hlogDetail_").wBox({title:"经办意见",show:true});//显示box
	
	
}
/***
 * 点击提交
 */
function doAction(){
	 var option=$("option").value.replace(/(\s*$)/g, "");
	if(option==""){
		alert("经办意见不能为空");return false;
	}
	var index=$("flag").value;
	if(confirm("确认操作？")){
		if(index=="1"){
			addAttrDisabled();
	//		代发经办成功
			doB2EAction();
		}else if(index=="2"){
			addAttrDisabled();
			//代发经办失败
			doActionFail(2);
		}
	}
}
//==================yyf
//线下充值经办初始化
function  initCZJB(){
	PageParam.initAdminStatePtype(function(list){
		  w_ptype=list[1];
	  });
	initMinfos();
	dwr.util.addOptions("mstate",m_mstate);
	queryXXCZJingBan();
}
//查询线下充值经办的数据
function queryXXCZJingBan(){
	var num=$("num").value;
	var uid=$("mid").value;
	var mstate=$("mstate").value;
	DaiFuService.queryXXCZJingBan(uid,num,mstate,function(alist){
	if(alist.length==0){
		dwr.util.removeAllRows("resultList");
		var trElement = document.createElement("tr");
		var tdElement = document.createElement("td");
		tdElement.setAttribute("align","center");
		tdElement.setAttribute("colspan", "7");
		tdElement.innerHTML='没有符合条件的记录';
		trElement.appendChild(tdElement);
		document.getElementById("resultList").appendChild(trElement);
		return ;
	}
	var totalPayAmt=0;
	var cellFuns=[	
	              	function(obj) {
	              	return "<input type=\"checkbox\"  name=\"toCheck\" id=\"toCheck\" value=\""+ obj.oid +"\">";
	              	},
	              	function(obj){ return obj.oid;},
	              	function(obj){ return w_ptype[obj.ptype];},
					function(obj){ return obj.uid;},
					function(obj){ return obj.aid;},
					function(obj){ return obj.aname;},
					function(obj){ return div100(obj.transAmt);},
					];
		for(var i=0;i<alist.length;i++){
			var o = alist[i];
  	  query_cash[o.oid] = o;
		}
	dwr.util.removeAllRows("resultList");
  dwr.util.addRows("resultList",alist,cellFuns,{escapeHtml:false});
  var str="<span style='float:left;'>&nbsp;<input type='button' id='chooseAll' name='chooseAll' value='全 选' onclick=\"checkAll('toCheck');;\">&nbsp;&nbsp;&nbsp;";
	str +="&nbsp;&nbsp;<span id='b2egatebalance' ></span>&nbsp;&nbsp;";
	str +="<input type='button'  value = '代发经办' onclick='do_confirm_xxczjb(1);'/>&nbsp;&nbsp;&nbsp;&nbsp;";
	str +="<input type='button' value = '代发失败'  onclick='do_confirm_xxczjb(2);'/>&nbsp;&nbsp;&nbsp;&nbsp;";
	    createBottom_CZJB();
	});
}
//创建table的foot的格式
function   createBottom_CZJB(){
	var trElement = document.createElement("tr");
	var tdElement = document.createElement("td");
	tdElement.colSpan = 14;
	tdElement.setAttribute("style", "color: blue");
	var str="<span style='float:left;'>&nbsp;<input type='button' id='chooseAll' name='chooseAll' value='全 选' onclick=\"checkAll('toCheck');;\">&nbsp;&nbsp;&nbsp;";
	str +="&nbsp;&nbsp;<span id='b2egatebalance' ></span>&nbsp;&nbsp;";
	str +="<input type='button'  value = '代发经办' onclick='do_confirm_xxczjb(1);'/>&nbsp;&nbsp;&nbsp;&nbsp;";
	str +="<input type='button' value = '代发失败'  onclick='do_confirm_xxczjb(2);'/>&nbsp;&nbsp;&nbsp;&nbsp;";
	tdElement.innerHTML =str;
	trElement.appendChild(tdElement);
	trElement.setAttribute("role", "bottom");
	document.getElementById("resultList").appendChild(trElement);
	dwr.util.addOptions("b2egateid",bk_c_b2b );
}   
/***
 * 1 成功  2 失败
 */
function do_confirm_xxczjb(index1){
	$("flag").value=index1;
	array = new Array();
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
	  }else if(index1==2 && index>1) {
		alert("请选择一条进行经办失败处理!");return false;
	}
	jQuery("#hlogDetail_").wBox({title:"线下充值经办意见",show:true});//显示box
}
function doAction_XXCZ(){
	var option=$("option").value.replace(/(\s*$)/g, "");
	if(option==""){
		alert("经办意见不能为空");return;
	}
	var index=$("flag").value;
	if(confirm("确认操作？")){
		if(index=="1"){
	//		代发经办成功
			doB2EAction_XXCZ();
		}else if(index=="2"){
			//代发经办失败
			doActionFail_XXCZ(2);
		}
	}
}//线下充值经办通过
function doB2EAction_XXCZ(){
	var option=$("option").value.replace(/(\s*$)/g, "");
	loadingMessage("正在查询，请稍候......");
	//alert("可以到账户明细中查询订单状态。。");
	DaiFuService.doB2EAction_XXCZ(array,option,function(str){
		alert(str);
		jQuery("#back").click();
		queryXXCZJingBan();
	});
}
//线下充值经办不通过
function doActionFail_XXCZ(flag){
	var option=$("option").value.replace(/(\s*$)/g, "");
	loadingMessage("正在操作，请稍候......");
	DaiFuService.doB2EActionFail_XXCZ(array,option,function(res){
		alert(res);
		jQuery("#back").click();
		queryXXCZJingBan();
	});
	
}

//提交 按钮改成灰色
function addAttrDisabled(){
	  jQuery("#submit").attr("disabled","disabled");
	  jQuery("#back").attr("disabled","disabled");
	  jQuery("#dfF").attr("disabled","disabled");
	  jQuery("#dfS").attr("disabled","disabled");
}	