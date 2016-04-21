function init(){
	dwr.util.addOptions("trans_model_x", h_gate_route_type);
	PageService.getGateRouteMap(function(gateRouteMap){
		gate_route_map=gateRouteMap;
	    dwr.util.addOptions("gateRouteId", gateRouteMap);
	});
    queryGatesByTJ();
}


var f = new GreyFrame('feeFrame',700,400);
f.setClassName("MyGreyFrame");     
if (typeof window["DWRUtil"] == "undefined") {
	window.DWRUtil = dwr.util;
}

 // 缓存
var gaterouteCache = {};
function showListDiv() {
	$("listGate").style.display = "";
	$("detailGate").style.display = "none";
}
function showDetailDiv() {
	$("listGate").style.display = "none";
	$("detailGate").style.display = "";
}
function detailGate(id) {
	showDetailDiv();
	newgate = gaterouteCache[id];
	fillGate(newgate);
}

 
function editGates(){
	var edited_id = $("edited_id").value;
	var trans_model_id = $("trans_model_id").value;
	var rytGate = $("ryt_gate_v").value;
	var feeModel = $("fee_model").value;
	var gateId = $("gateid").value;
	var feeFlag = $("fee_flag").value;
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
					  queryGatesByTJ();
					}else{
					  alert(result);
					}
				}
		);
	}
}
        

           //根据条件查询银行网关
function queryGatesByTJ() {
	var transModel = $("trans_model_x").value;
	var gateRouteId = $("gateRouteId").value;
	SysManageService.queryGates(transModel, gateRouteId, callBackList);
}                

        

        // 提取用户列表的回设函数：gateList中放的是gate对象
var callBackList = function (gateList) {
	if (gateList.length == 0) {
		alert("没有符合条件的数据!");
		return;
	}
	showListDiv();
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
		            	   return "<input type=\"button\" value=\"修改\" onclick=\"detailGate(" + obj.id + ");\"/>";},
		               ];
		dwr.util.addRows("gateRouteTable",gateList,cellfuns,{escapeHtml:false});
};

                

// 填充商户信息
function fillGate(newgate) {
    var transMode = newgate.transMode;
    var gid = newgate.gid;
	dwr.util.setValues({
		ryt_gate_v:newgate.rytGate,
		edited_id : newgate.id,
		gate_desc_short:newgate.gateDescShort, 
		gateid:newgate.gateId,
		trans_mode_v: h_gate_route_type[transMode],
		trans_model_id:transMode,
		gid_v : gate_route_map[gid],
		fee_model : newgate.feeModel,
		fee_flag:newgate.feeFlag
	});
}

