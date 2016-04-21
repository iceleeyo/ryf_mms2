//刷新
function reflushGate(){
	 SysManageService.refreshGateRoute(function callback(msg){if(msg==true) alert('刷新成功');else alert('刷新失败');});
	  } 

//初始化
function initParams(){
	 var hasAuth = '1'== authStr.charAt(191);
	 if(hasAuth){
		 jQuery("#addBtn").prop({disabled: false});
	 }else{
		 jQuery("#addBtn").prop({disabled: true});
	 }
	 queryPayGate();
 }

//校验修改地址条件
function canEditBut(gid){
	//if(gid == 50100 || gid == 80002){
		$("editReqUrlBut").disabled = false;
	//}else{
		//$("editReqUrlBut").disabled = 'disabled';
	//}
}

 var gateRouteCache={};//缓存
//查询支付渠道信息
function queryPayGate(){
	 SysManageService.queryGateRoute(function(gateRouteList){
		 var count=0;
		 cellfuns=[
		           function(obj){
		        	   count++;
		        	   gateRouteCache[count]=obj;
		        	   return "<input type='radio' onclick='' name='gateRouteCount' value='"+count+"'/>";
		           },
		           function(obj){return obj.gid;},
		           function(obj){return obj.name;},
		           function(obj){return obj.merNo;},
		           function(obj){
		        	   return obj.className == '' ? '' : '已完成';
		        	   
		           },
		           function(obj){
		        	   //return createTip(30,obj.requestUrl);
		        	   return obj.requestUrl;
		           },
		           function(obj){
		        	   return createTip(30,obj.remark);
		           }
		           //function(obj){return obj.recAccNo;},
		           //function(obj){return obj.recAccName;},
		           //function(obj){return obj.recBankNo;},
		           //function(obj){return obj.recBankName;},
		           //function(obj){return obj.bfBkNo;}
		         ];
		 dwr.util.removeAllRows("gateRouteList");
		 dwr.util.addRows("gateRouteList",gateRouteList,cellfuns,{escapeHtml:false});
	 });
	 
}

 /**
  * 显示编辑的窗口
  * @param flag
  * @return
  */
var bk_no_map=null;
function showEditNameWin(flag){
	var countArr= getValuesByName("gateRouteCount");
	if(flag != 4){
		if(countArr.length==0){
			alert("请选择一条记录！");
			return false;
		}
	}
	 //获取选中的第一个radio
	 var gateRoute=gateRouteCache[countArr[0]];
	//基本信息
		//修改  以渠道ID为依据修改，渠道名称、商户号、请求地址、说明这几个字段来进行修改渠道，
		//每个字段为必填选项，渠道ID和电银商户需验证只能为数字
	 if(flag==1){
		 var gid=gateRoute.gid;
		 var gName=gateRoute.name;//渠道名称
		 var merNo=gateRoute.merNo;//电银在银行的商户号
		 var requestUrl=gateRoute.requestUrl;//请求地址
		 var remark=gateRoute.remark;//说明
		 dwr.util.setValue("gateRouteId",gid);//支付渠道ID
		 dwr.util.setValue("gateRouteName",gName);//支付渠道名
		 dwr.util.setValue("gateRouteMerNo",merNo);
		 dwr.util.setValue("gateRouteReqUrl",requestUrl);
		 dwr.util.setValue("gateRouteRemark",remark);
		 jQuery("#gateRouteId").prop({disabled: true});
		 jQuery("#subBtn").unbind( "click" );//先解除之前绑定的方法
		 jQuery("#subBtn").bind("click", function() { updateGateRouteBasicInfo(); });
		 jQuery("#basicInfoTable").wBox({title:"&nbsp;&nbsp;渠道名称修改",show:true});//显示box
	 }
	 if(flag==2){//其他信息修改
		 fillObj(gateRoute);
		 jQuery("#payGateMsgDiv").wBox({title:"&nbsp;&nbsp;修改其他信息",show:true});
	 }
	 if(flag==3){//修改备付金银行行号
		 var gid=gateRoute.gid;
		 dwr.util.setValue("gateRouteId1",gid);
		 if(bk_no_map==null){
			 dwr.engine.setAsync(false);
			 SysManageService.getBkNoMap(function(bkNoMap){
				 dwr.util.addOptions("bfBkNo", bkNoMap);
				 bk_no_map=bkNoMap;
			 });
		 }
		 jQuery("#bfBkNoTable").wBox({title:"&nbsp;&nbsp;备付金银行修改",show:true});
		 $("bfBkNo").value=gateRoute.bfBkNo;
		 
	 }
	 if(flag==4){//渠道新增
		 dwr.util.setValue("gateRouteId","");//支付渠道ID
		 dwr.util.setValue("gateRouteName","");
		 dwr.util.setValue("gateRouteMerNo","");
		 dwr.util.setValue("gateRouteReqUrl","");
		 dwr.util.setValue("gateRouteRemark","");
		 jQuery("#gateRouteId").prop({disabled: false});
		 jQuery("#subBtn").unbind( "click" );//先解除之前绑定的方法
		 jQuery("#subBtn").bind("click", function() { addGateRoute(); });
		 jQuery("#basicInfoTable").wBox({title:"&nbsp;&nbsp;支付渠道新增",show:true});//显示box
	 }
}
	
//自动填充input
function fillObj(obj){
	for(var attr in obj){
		if($(attr)){
			$(attr).value=obj[attr];
		}
	}
}
/**
 * ryf4.5 支付渠道名称修改和修改请求地址合并为基本信息修改
 * @return
 */
 function updateGateRouteBasicInfo(){
	 var gateRouteId = $("gateRouteId").value.trim();//支付渠道ID
	 var gateRouteName = $("gateRouteName").value.trim();
	 var gateRouteMerNo = $("gateRouteMerNo").value.trim();
	 var gateRouteReqUrl = $("gateRouteReqUrl").value.trim();
	 var gateRouteRemark = $("gateRouteRemark").value.trim();
	 if(gateRouteMerNo==""){
    	  alert("商户号不能为空！");
    	  return false;
      }else if(gateRouteName==""){
    	  alert("渠道名不能为空！");
    	  return false;
      }else if(gateRouteReqUrl==""){
    	  alert("请求地址不能为空！");
    	  return false;
      }else if(gateRouteRemark==""){
    	  alert("说明不能为空！");
    	  return false;
      }
	 SysManageService.updateGateRouteBasicInfo(gateRouteId,gateRouteName,gateRouteMerNo,gateRouteReqUrl,gateRouteRemark,editCallback);
 }
// function eidtRequestUrl(){
//	 if(!confirm("你确认修改吗？"))return;
//	 var gid = dwr.util.getValue("gateRouteId4");
//	 var requestUrl = dwr.util.getValue("edit_request_url");
//	 SysManageService.eidtRequestUrlByGid(gid,requestUrl,editCallback);
// }
 
 /**
  * 修改网关其他信息
  */
 function editGateRouteMsg(){
	 var gateRoute=wrapObj("payGateMsgForm");
	 SysManageService.editeGateMassageByGid(gateRoute,editCallback);
 }
 /**
  * 修改备付金银行行号
  */
 function eidtBfBkNo(){
	 
	 if(!confirm("你确认修改吗？"))return;
	 
	 var gid=$("gateRouteId1").value;
	 var bfBkNo=$("bfBkNo").value;
	 
	 SysManageService.eidtBfBkNoByBkNo(gid,bfBkNo,editCallback);
 }
 /**
  * 修改的回调函数
  * @param msg
  * @return
  */
 function editCallback(msg){
	 if(msg=="ok"){
//		 canEditBut(-1);
		 alert("更新渠道信息成功");
		 jQuery("#wBox_close").click();
		 queryPayGate();
	 }else{
		 alert(msg);
	 }
 }

   /**
	 * 通过渠道ID、渠道名称、商户号、请求地址、
	 * 说明这几个字段来进行新增渠道，每个字段为必填选项，
	 * 渠道ID和电银商户需验证只能为数字
	 * **需要进行按钮权限控制**
	 */
function addGateRoute(){
	  var gateRouteId = $("gateRouteId").value.trim();
      var gateRouteName = $("gateRouteName").value.trim();
      var gateRouteMerNo = $("gateRouteMerNo").value.trim();
      var gateRouteReqUrl = $("gateRouteReqUrl").value.trim();
      var gateRouteRemark = $("gateRouteRemark").value.trim();
      if(gateRouteId=="" || !isFigure(gateRouteId)){
    	  alert("渠道号不能为空且必须为数字！");
    	  return false;
      }else if(gateRouteMerNo==""){
    	  alert("商户号不能为空！");
    	  return false;
      }else if(gateRouteName==""){
    	  alert("渠道名不能为空！");
    	  return false;
      }else if(gateRouteReqUrl==""){
    	  alert("请求地址不能为空！");
    	  return false;
      }else if(gateRouteRemark==""){
    	  alert("说明不能为空！");
    	  return false;
      }else{
    	  SysManageService.addGateRoute(gateRouteId,gateRouteName,gateRouteMerNo,gateRouteReqUrl,gateRouteRemark,callbackAddNewGate);
      }
      
   }
   function callbackAddNewGate(msg){
      if(msg=='ok'){
          alert("新增成功！");
          window.location.reload();
      }else{
          alert(msg);
      }
   }  
function selectByKey(){
	var key=$("sel_key").value;
	if(key.trim()==""){
		queryPayGate();
		return;
	}
	SysManageService.selectByKey(key,function(gateRouteList){
		 var count=0;
		 cellfuns=[
		           function(obj){
		        	   count++;
		        	   gateRouteCache[count]=obj;
		        	   return "<input type='radio' onclick='' name='gateRouteCount' value='"+count+"'/>";
		           },
		           function(obj){return obj.gid;},
		           function(obj){return obj.name;},
		           function(obj){return obj.merNo;},
		           function(obj){
		        	   return obj.className == '' ? '' : '已完成';
		        	   
		           },
		           function(obj){
		        	   //return createTip(30,obj.requestUrl);
		        	   return obj.requestUrl;
		           },
		           function(obj){
		        	   return createTip(30,obj.remark);
		           }
		           //function(obj){return obj.recAccNo;},
		           //function(obj){return obj.recAccName;},
		           //function(obj){return obj.recBankNo;},
		           //function(obj){return obj.recBankName;},
		           //function(obj){return obj.bfBkNo;}
		         ];
		 dwr.util.removeAllRows("gateRouteList");
		 dwr.util.addRows("gateRouteList",gateRouteList,cellfuns,{escapeHtml:false});
	 });
}
