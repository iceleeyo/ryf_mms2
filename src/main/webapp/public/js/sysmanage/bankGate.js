  var frame = new GreyFrame('feeFrame',700,400);
      frame.setClassName("MyGreyFrame");

//初始化options
function initOptions(){
	if($("trans_mode")){
		dwr.util.addOptions("trans_mode", h_gate_route_type);
	}
	if($("v_trans_mode")){
		dwr.util.addOptions("v_trans_mode", h_gate_route_type);
	}
	if($("trans_model_x")){
		dwr.util.addOptions("trans_model_x", h_gate_route_type);
	}
	PageService.getGateRouteMap(function(gateRouteMap){
		gate_route_map=gateRouteMap;
		if($("gateRouteId")){
			dwr.util.addOptions("gateRouteId", gateRouteMap);//支付渠道
		}
		if($("gateRouteId1")){
			dwr.util.addOptions("gateRouteId1", gateRouteMap);//支付渠道
		}
		if($("gateRouteId2")){
			dwr.util.addOptions("gateRouteId2", gateRouteMap);//支付渠道
		}
	});
    //initGateChannel();
    query4NewGate(null);
    queryGatesByTJ();
} 

//联动查询 (交易方式--》网关)
	function query4NewGate(trans_mode){
    	if(!trans_mode) trans_mode =  $("trans_mode").value;
        SysManageService.queryGatesMapByTransMode(trans_mode,function(gateList){
	            	  dwr.util.removeAllOptions("gate_desc_short");
	            	  dwr.util.addOptions("gate_desc_short", gateList);

        });
     }
     
	//搜索支付渠道
	 var gate_route_map2 =  {};
	function gateRouteIdList(){
		 var gateName = $("gateName").value;
		 PageService.getGRouteByName(gateName,function(m){
			 gate_route_map2=m;
			 dwr.util.removeAllOptions("gateRouteId1");
			 //dwr.util.addOptions("gateRouteId1",{'-1':'全部...'});
			dwr.util.addOptions("gateRouteId1", gate_route_map2);
		 });
		 
	 }
	
	//搜索支付渠道
	 var gate_route_map3 =  {};
	function gateRouteIdList1(){
		 var gateName = $("gateName1").value;
		 PageService.getGRouteByName(gateName,function(m){
			 gate_route_map3=m;
			 dwr.util.removeAllOptions("gateRouteId2");
			 dwr.util.addOptions("gateRouteId2",{'-1':'全部...'});
			dwr.util.addOptions("gateRouteId2", gate_route_map3);
		 });
		 
	 }
// 增加银行网关---DWR调用
 function addGates() {
	var gateDescShort =  dwr.util.getText("gate_desc_short").replace("(W)","");
	var bankgate =  dwr.util.getValue("gateid");
	var fee_model =  dwr.util.getValue("fee_model");
	var ryt_Gate =  dwr.util.getValue("gate_desc_short");
	var transMode =  dwr.util.getValue("trans_mode");
	var gateRouteId =  dwr.util.getValue("gateRouteId1");
	var feeFlag =  dwr.util.getValue("fee_flag");
    if(fee_model !='') {fee_model = fee_model.trim();}
	if(bankgate==''){
		alert("请输入支付银行网关号！");
		return false;
	}
	if(fee_model == ''){
		alert("请输入计费公式!");
		return false;
	}
	if(!checkFeeModel(fee_model)){
		alert("您输入的计费公式格式有误,请参照计费公式说明重新输入！");
		return false;
	}
	SysManageService.addGates(gateDescShort,bankgate,fee_model,ryt_Gate,transMode,gateRouteId,feeFlag, function (alertMsg) {alert(alertMsg);});
 }
 //=================================
 // 缓存
 var gaterouteCache = {};
 
 //根据条件查询银行网关
 function queryGatesByTJ() {
 	var transModel = $("trans_model_x").value;
 	var gateRouteId = $("gateRouteId2").value;
 	SysManageService.queryGates(transModel, gateRouteId, callBackList);
 }         
 // 提取用户列表的回设函数：gateList中放的是gate对象
 var callBackList = function (gateList) {
 	if (gateList.length == 0) {
 		alert("没有符合条件的数据!");
 		return;
 	}
 	dwr.util.removeAllRows("gateRouteTable");
 			var  cellfuns=[
 		               function(obj){return obj.rytGate;},
 		               function(obj){return obj.gateDescShort;},
 		               function(obj){return h_gate_route_type[obj.transMode];},
 		               function(obj){return gate_route_map[obj.gid];},
 		               function(obj){return obj.gateId;},
 		               function(obj){return obj.feeModel;},
 		               function(obj){
 		            	   gaterouteCache[obj.id] = obj;
 		            	   return "<input type=\"button\" value=\"修改\" onclick=\"detailGate(" + obj.id + ");\"/>";}
 		               ];
 		dwr.util.addRows("gateRouteTable",gateList,cellfuns,{escapeHtml:false});
 };
//修改银行网关
 function detailGate(id) {
	 	jQuery("#detailGate").wBox({title:"&nbsp;&nbsp;银行网关修改",show:true});//显示box
		newgate = gaterouteCache[id];
		fillGate(newgate);
}
 
//填充银行网关信息
 function fillGate(newgate) {
     var transMode = newgate.transMode;
 	dwr.util.setValues({
 		ryt_gate2:newgate.rytGate,
 		edited_id2 : newgate.id,
 		gate_desc_short2:newgate.gateDescShort, 
 		gateid2:newgate.gateId,
 		trans_mode2: h_gate_route_type[transMode],
 		trans_model_id2:transMode,
 		gid2 : gate_route_map[newgate.gid],
 		fee_model2 : newgate.feeModel,
 		fee_flag2:newgate.feeFlag
 	});
 }
 function editGates(){
		var edited_id = $("edited_id2").value;
		var trans_model_id = $("trans_model_id2").value;
		var rytGate = $("ryt_gate2").value;
		var feeModel = $("fee_model2").value;
		var gateId = $("gateid2").value;
		var feeFlag = $("fee_flag2").value;
		if(edited_id ==''|| trans_model_id==''){
			alert("发生错误");
		}else if(rytGate ==''){
			alert("请输入电银信息网关号！");
		}else if(gateId == ''){
			alert("请输入支付银行网关号！");
		}else if(feeModel == ''){
			alert("请输入计费公式!");
		}else if(!checkFeeModel(feeModel)){
			alert("您输入的计费公式格式有误,请参照计费公式说明重新输入！");
		}else{
			var gid=gaterouteCache[edited_id].gid;
			SysManageService.editGates(gid,edited_id,trans_model_id,rytGate,gateId,feeModel ,feeFlag,
					function (result) {
						if(result=='ok'){
						  alert("修改成功!");
						  jQuery("#wBox_close").click();
						  queryGatesByTJ();
						}else{
						  alert(result);
						}
					}
			);
		}
}
 // 
function addGate(){
	jQuery("#add4Gate").wBox({title:"&nbsp;&nbsp;银行网关添加",show:true});//显示box
	$("v_trans_mode").value =  dwr.util.getValue("trans_mode");
}
function addRytGate(){
	var vGate = $("v_gate").value;
	var vStatFlag = $("v_stat_flag").value;
	var vTransMode = $("v_trans_mode").value;
	var transMode = $("trans_mode").value;
	var vGateName = $("v_gate_name").value;
	if(vGate==''){
		alert("请输入银行网关号！");
		return false;
	}
	if(vGateName==''){
		alert("请输入银行网关名称！");
		return false;
	}
	if(!isFigure(vGate)){
		alert("网关号必须是整数！");
		return false;
	}
	SysManageService.addRytGate(vGate,vTransMode,vGateName,
			function (result) {
		if(result=='ok'){
		  alert("添加成功!");
		  jQuery("#wBox_close").click();
		  query4NewGate(transMode);
		}else{
		  alert(result);
		}
	}		
	);
}