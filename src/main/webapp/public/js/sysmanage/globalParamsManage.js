var globalParamsCache = {};
function init() {
	$("editTableId").style.display = "none";
	dwr.util.removeAllRows("list");
	SysManageService.queryAllParams(callBackList);
}
function callBackList(data) {
	var dtable = $("list");
	if (data.length > 0) {
		for ( var i = 0; i < data.length; i++) {
			var globalParams = data[i];
//			if (globalParams.parName != 'WY_XYK_WAR') {
				var elTr = dtable.insertRow(-1);
				elTr.setAttribute("align", "center");
				var parName = elTr.insertCell(-1);
				parName.innerHTML = globalParams.parName;
				var parValue = elTr.insertCell(-1);
				parValue.innerHTML = globalParams.parValue;
				var parDesc = elTr.insertCell(-1);
				parDesc.innerHTML = globalParams.parDesc;
				var parOpr = elTr.insertCell(-1);
				parOpr.innerHTML = "<input type='button' value='修改' onclick='openEditWindow("+ i + ")' >";
				globalParamsCache[i] = globalParams;
//			}
		}
	}
}

function openEditWindow(i) {
	var param = globalParamsCache[i];
	if(param) $("editTableId").style.display = "";
	dwr.util.setValues({v_i:i,v_parName:param.parName,v_parValue:param.parValue,v_parDesc:param.parDesc});

}

function editParam() {
	var i = $("v_i").value;
	var globalParams = globalParamsCache[i];
	if(!globalParams) return;
	if ($("v_parValue").value.trim() == "") {
		alert("参数值不能为空！");
		$("v_parValue").focus();
		return false;
	}
	globalParams.parValue = $("v_parValue").value;
	globalParams.parDesc = $("v_parDesc").value;
	SysManageService.editParam(globalParams, callBackEdit);
}

function callBackEdit(msg) {
	if (msg == "ok") {
		alert("修改成功！");
		init();
	}else {alert(msg);}
}

function qx(){
   dwr.util.setValues({v_i:'',v_parName:'',v_parValue:'',v_parDesc:''});
   $("editTableId").style.display = "none";
}		
