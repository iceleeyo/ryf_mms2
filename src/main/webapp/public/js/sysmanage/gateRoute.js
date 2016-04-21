//初始化options
function initOptions(){
	if($("trans_mode")){
		dwr.util.addOptions("trans_mode", h_gate_route_type);
	}
	if($("trans_mode1")){
		dwr.util.addOptions("trans_mode1", {"":"所有交易类型"});
		dwr.util.addOptions("trans_mode1", h_gate_route_type);
	}
	PageService.getGateRouteMap(function(gateRouteMap){
		gate_route_map=gateRouteMap;
		if($("gateRouteId")){
			dwr.util.addOptions("gateRouteId", gateRouteMap);//支付渠道
		}
			PageService.getGateChannelMap(function(mapArr){
				h_gate=mapArr[0];//select gate,gate_name from ryt_gate
				gate_route_map=mapArr[1];
				if($("gateId")){
					dwr.util.addOptions("gateId", {"":"所有网关类型"});
					dwr.util.addOptions("gateId", h_gate);//网关
				}
			});
	});
	
	jQuery(function(){
		jQuery(":checkbox[name='check']").live('click', function() {
			var status = this.checked?"true":"false";
			var checkboxes=document.getElementsByName("check");
			for(var i = 0;i<checkboxes.length;i++){
				var status1 = checkboxes[i].checked?"true":"false";
				if(status == status1){
					continue;
				}else{
					if(!this.checked){
						jQuery("#toggleAll").attr("checked",this.checked);
					}
					return;
				}
			}
			jQuery("#toggleAll").attr("checked",this.checked);
		});
	});
	
//	alert(document.getElementById('gateRouteId').onchange);
	if($("gateRouteId")&&$("gateRouteId").onchange){
		document.getElementById('gateRouteId').onchange();
	}
} 
	var args_ransMode,args_gate_id;
	function cacheArgs(){
		args_ransMode = $('trans_mode1').value;
		args_gate_id = $('gateId').value;
		showApply(1);
	}
	/**
	 * 查询申请记录
	 */
	function showApply(pageNo){
		jQuery("#toggleAll").attr("checked",false);
		SysManageService.showBatchRouteApply(args_ransMode,args_gate_id,pageNo,callBack);
	}
	
	/**
	 * @param list
	 * 回调显示列表
	 */
	function callBack(pageObj){
//		h_gate=mapArr[0];网关map
//		gate_route_map=mapArr[1];渠道map
		   $("pageTable").style.display="";
		   dwr.util.removeAllRows("resultList");
		   if(pageObj==null){
	 	   document.getElementById("resultList").appendChild(creatNoRecordTr(6));
			   return;
		   }  
	     var cellFuncs = [
	                      function(obj) { return '<input align="left" name="check" type="checkbox" value='+obj.id+' />' ; },
	                      function(obj) { return h_gate_route_type[obj.transMode]; },
	                      function(obj) { return h_gate[obj.rytGate]; },
	                      function(obj) { return gate_route_map[obj.gid]; },
	                      function(obj) { return '未启用'; },
	                      function(obj) { return'<a href="#" onclick="check('+obj.id+',1)">启用</a>&nbsp;&nbsp;<a onclick="check('+obj.id+',2)" href="#">撤销</a>';}
	                  ]
		str = '<input style="margin-left:20px;" type="button"value="批量启用" onclick="batchCheck(1);" />';
 		//ryt.js
       	paginationTable(pageObj,"resultList",cellFuncs,str,"showApply");
       	//resultList中td标签中的文字是右对齐 不知道是什么原因 通过一下代码改为居中
       	jQuery("#resultList").find("td").each(function(){
       		jQuery(this).css("text-align","center");
       	});
	}
	
	/**
	 * @param id
	 * @param status
	 * 审核
	 */
	function check(id,status){
		SysManageService.checkChangeRouteByGate(id,status,function(msg){
			alert(msg);
			showApply(1);
		});
	}
	
	/**
	 * @param status
	 * 批量启用
	 */
	function batchCheck(status){
		var ids= new Array();
		var checkboxes=document.getElementsByName("check");
		for(var i = 0;i<checkboxes.length;i++){
			if(checkboxes[i].checked){
				ids.push(checkboxes[i].value);
			}
		}
		if(ids.length==0){
			alert("请选择要审核的申请");
			return;
		}
		SysManageService.checkBatchChangeRouteByGate(ids,status,function(msg){
			alert(msg);
			showApply(1);
		});
	}
	
	/**
	 * @param o
	 * 全选
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
		var transMode = $("trans_mode").value;
		var checkboxes=document.getElementsByName("check");
		for(var i = 0;i<checkboxes.length;i++){
			if(checkboxes[i].checked){
				gateIdList.push(checkboxes[i].value);
			}
		}
		if(gateIdList.length==0){
			alert("请选择支付网关");
			return;
		}
		SysManageService.applySetRouteOfGate(gateIdList,routeId,transMode,function(msg){
			alert(msg);
		});
	}

	/**
	 * @param routeId 渠道id
	 * 根据渠道查询出所有支持该渠道的网关
	 */
	function queryGatesByRoute(){//查询gates表
		var routeId = $('gateRouteId').value;
		var transMode1 = $('trans_mode').value;
		SysManageService.queryGatesByRoute(routeId,transMode1,function(gateList){
			var gateStr = '<table frame="void" width="100%">' +
						  '<tr height="20px">' +
						  '<td colspan="8" align="left" >' +
					      '<input onclick="toggleAll(this);" style="vertical-align: middle;margin-left:100px;" type="checkbox"/>&nbsp;全选' +
						  '<input onclick="reverse();" style="vertical-align: middle;margin-left:100px;" type="checkbox"/>&nbsp;反选</tr><tr>';
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
	
