//初始化options
function initOptions(){
     dwr.util.addOptions("trans_mode", {"-1":"所有支付网关"});
	 dwr.util.addOptions("trans_mode", h_gate_route_type);
} 

	/**
	 * @param o
	 */
	function toggleAll(o){
		var status = o.checked;
		var checkboxes=document.getElementsByName("check");
		for(var i = 0;i<checkboxes.length;i++){
			checkboxes[i].checked=status;
		}
	}
	
	function reverse(){
		var checkboxes=document.getElementsByName("check");
		for(var i = 0;i<checkboxes.length;i++){
			checkboxes[i].checked=!checkboxes[i].checked;
		}
	}
	
	/**
	 * @param gateIdList
	 * @param route
	 * 申请将全部商户的选中网关的渠道切换为route
	 */
	function applySetRouteOfGate(){//设置fee_calc_mode表
		var gateIdList = new Array();
		var routeId = $("gateRouteId").value;
		var checkboxes=document.getElementsByName("check");
		for(var i = 0;i<checkboxes.length;i++){
			if(checkboxes[i].checked){
				gateIdList.push(checkboxes[i].value);
			}
		}
		SysManageService.applySetRouteOfGate(gateIdList,routeId,function(msg){
			alert("abcd");
		});
	}

	/**
	 * @param routeId 渠道id
	 * 根据渠道查询出所有支持该渠道的网关
	 */
	function queryGatesByRoute(routeId){//查询gates表
		SysManageService.queryGatesByRoute(routeId,function(gateList){
			var gateStr = '<table frame="void" width="100%">' +
						  '<tr height="20px">' +
						  '<td colspan="8" align="left" >' +
					      '<input onclick="toggleAll(this);" style="vertical-align: middle;margin-left:100px;" type="checkbox"/>&nbsp;全选' +
						  '<input onclick="reverse();" style="vertical-align: middle;margin-left:100px;" type="checkbox"/>&nbsp;反选</tr>';
	    	var tmp=0;
			for (var i = 0; i < gateList.length; i++) {
	        		var gate = gateList[i];
	        			tmp=tmp+1;
	        			gateStr += '<td><input name="check" style="vertical-align: middle;" type="checkbox" value="'+gate.rytGate+'"/>&nbsp;'+ gate.gateDesc + '</td>';
	        			if(tmp%8==0) gateStr += "</tr><tr>";
	        }
			if(tmp!=0){
				var tds=8-(tmp%8);
	    		if(tds>0&&tds!=8){
	    			for ( var int = 0; int < tds; int++) {
	    				gateStr +="<td style='width:12%;'></td>";
					}
	    			gateStr+="</tr>";	
	    		}
			}
	     
	    	gateStr += "</table>";
	    	$("gateTable").innerHTML = gateStr;
		});
	}
	
