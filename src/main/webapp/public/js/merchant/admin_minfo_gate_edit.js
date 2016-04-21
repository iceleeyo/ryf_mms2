function initParam(){
		initGateChannel();
		initMinfos();
		if($("trans_mode")){
			dwr.util.addOptions("trans_mode", h_gate_route_type);
		}
}

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
					jQuery("#toggleAll1").attr("checked",this.checked);
					jQuery("#toggleAll2").attr("checked",this.checked);
				}
				return;
			}
		}
		jQuery("#toggleAll").attr("checked",this.checked);
		jQuery("#toggleAll1").attr("checked",this.checked);
		jQuery("#toggleAll2").attr("checked",this.checked);
	});
});
var fullname;
var mer_type;
var transMode;
var flush=true;//配置银行后判断是否有刷新
	  
	  function reflushGate(){
		  var mid = $("mid").value;
		  if(mid == ''){alert("请选择商户号！");return false;}
		  flush=true;
		  CommonService.refreshFeeCalcModel(mid, function callback(msg){if(msg==true) alert('刷新成功');else alert('刷新失败');});
	  }
	  window.onbeforeunload = function(e){
		  if(!flush) return '你配置完商户网关，还没有刷新';
	  };
      var feeCalcModeCache = new Array();
     //查询所�?
      function queryGate(){
//    	  $("selected_gates_trans_model_id").value=6;//?
          var mid = $("mid").value; 
          //var smid = $("smid").value;
          if(mid ==""){alert("请选择商户号！");return false;}
          //if(smid==""){alert("商户不存在，请重新输入！");return false;}
          $("selected_mer_id").value = mid; 
          MerchantService.getMerGateConfigInfo(mid,transMode,callback);
      }
     function callback(aList){
    	var gList = aList[0];//未开通网�?
	    var fList = aList[1];//已开通网�?"select * from fee_calc_mode where mid ='"+mid+"'"
	    var mid = $("mid").value;
        dwr.util.removeAllRows("FeeModelTable");
        //未开通网关显�?
        $("gateRouteTable").style.display="";
        $("gateRouteTitle").innerHTML = "&nbsp;&nbsp;商户<b> ["+m_minfos[mid]+"]</b> 未开通的支付网关,全称：<b> ["+fullname+"]</b>,商户类型：<b> ["+mer_type+"]</b>";
        var gateStr = "<table  style=\"width: 100%;\">";
        var wg =[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18];
        var wg_Name=["","网银支付网关:","消费充值卡支付:","信用卡支付","","增值业务","语音支付:","企业网银支付:","手机WAP支付:","","POS支付:","对私代付:","对公代付:","B2B充值:","","对私代扣:","对公提现:","对私提现:","快捷支付:"];
		gateStr += "<tr><td colspan=8><font style=\"color:blue;\">"+wg_Name[transMode]+"</font></td></tr><tr>";
    	var tmp=0;
		for (var i = 1; i <= gList.length; i++) {
        		var RYTGate = gList[i-1];
        			tmp=tmp+1;
        			gateStr += "<td><input type=\"checkbox\" name=\"rytGate\" ttype='"+RYTGate.transMode+"' value='"+RYTGate.gate+"'/>&nbsp;"+ RYTGate.gateName + "</td>";
        			if(tmp%8==0) gateStr += "</tr><tr>";
        }
		if(tmp!=0){
			var trs=8-(tmp%8);
    		if(trs>0&&trs!=8){
    			for ( var int = 0; int < trs; int++) {
    				gateStr +="<td style='width:140px;'></td>";
				}
    			gateStr+="</tr>";	
    		}
		}
     
    	gateStr += "</table>";
    	$("gateRouteCell").innerHTML = gateStr;
    	//已开通网关显�?
		$("openGatesTable").style.display = "";
		feeCalcModeCache.length = 0;//清空缓存
		showSupportGateList(fList,true);
     }

     function SelGateByTransModel(form,ttype){
        for (var i=0;i<form.elements.length;i++){
           var e = form.elements[i];
           var ttype_ = e.getAttribute("ttype");
           if(ttype_ == ttype){
        	   e.checked = true;
	       }else{
	    	   e.checked = false;
		   }
        }
     }
     function AddToMer(form){
    	 var mid = $("mid").value;
    	var gs = new Array();
        for (var i=0;i<form.elements.length;i++)
        {
           var e = form.elements[i];
           if(e.checked){
        	   gs.push(e.value);
           }
        }
        if(gs.length==0){
            alert('请选择支付网关!');
            return ;
        }else{
//        	flush=false;//设置为未刷新
        	MerchantService.addGatesToMer(mid,gs,function(msg){
        		 if(msg=='ok'){
        			 queryGate();
        			 $("show_mer_fee_model_id").value=-1;
        		 }else{
        			 alert(msg);
        		 }
        	});
        }
     }

     function callEdit(msg){
	    if(msg=='ok'){ 
	    	queryGate();
	    	setTimeout('showMerFeeCalaModels($("selected_gates_trans_model_id").value)', 1000 )
	    	$("show_mer_fee_model_id").value=$("selected_gates_trans_model_id").value;
		  alert("手续费配置成功");
	    }
	    else alert(msg);
     }
     
     function editMerFeeModel(){
    	var selectId = $("selected_mer_id").value;
    	var transModel = $("selected_gates_trans_model_id").value;
    	var calcModel = $("mer_calc_model_id").value;
    	MerchantService.addMerFeeModels(selectId,transModel,calcModel,callEdit);
	 }
     function showMerFeeCalaModels(){
    	 $('toggleAll').checked = false;
    	 var tm = $("show_mer_fee_model_id").value;
    	 var status= $("status1").value;
    	 var fList1 = new Array();
    	 var fList2 = new Array();
    	 if(tm==-1){
    		 fList1 = feeCalcModeCache;
	     }else{
	    	 for(var i = 0; i < feeCalcModeCache.length; i++){
                 aFeeCalcMode = feeCalcModeCache[i];
                 if(aFeeCalcMode.transMode==tm){
                     fList1.push(aFeeCalcMode);
                 }
             }
	     }
    	 if(status==-1){
    		 fList2 = fList1;
    	 }else{
//    		 feeCalcMode.state==0?"已关�?:"已开�?
    		 for(var i = 0; i < fList1.length; i++){
                 aFeeCalcMode = fList1[i];
                 if(aFeeCalcMode.state==status){
                     fList2.push(aFeeCalcMode);
                 }
             }
    	 }
    	 dwr.util.removeAllRows("FeeModelTable");
    	 showSupportGateList(fList2,false);
	 }
//已开通网关配置显�?
     function showSupportGateList(fList,cache){
    	 var j=0;
    	 var cellFuncs=[
	               function(feeCalcMode){
	            	   var args = feeCalcMode.mid +","+feeCalcMode.gate;
	            	   return '<input type="checkbox" status='+feeCalcMode.state+' name="check" value="'+args+'"/>';},
	               function(feeCalcMode){return feeCalcMode.mid;},
	               function(feeCalcMode){return feeCalcMode.gate;},
	               function(feeCalcMode){return h_gate[feeCalcMode.gate];},
	               function(feeCalcMode){return feeCalcMode.gateId;},
	               function(feeCalcMode){return h_gate_route_type[feeCalcMode.transMode];},
	               function(feeCalcMode){return feeCalcMode.bkFeeMode;},
	               function(feeCalcMode){return feeCalcMode.calcMode;},
	               function(feeCalcMode){return feeCalcMode.gateId =="" ? "" : gate_route_map[feeCalcMode.gid];},
	               function(feeCalcMode){return feeCalcMode.state==0?"已关闭":"已开启";},
	               function(feeCalcMode){
	            	  if(cache) feeCalcModeCache[j] = feeCalcMode;
	            	  ++j;
	            	   var btText=feeCalcMode.state==0?"开启申请":"关闭申请";
	            	   var str  = '&nbsp;&nbsp;<a href="#" onclick="toggleGateApply(' + feeCalcMode.mid 
    					+','+feeCalcMode.gate+','+feeCalcMode.state+');" id="'+feeCalcMode.gate+'close">' + btText + '</a>';
    	                str += '&nbsp;&nbsp;<a href="#" onclick="editGate(' + feeCalcMode.mid 
	   					+','+feeCalcMode.gate+','+feeCalcMode.gid+',\''+feeCalcMode.calcMode+'\');">网关配置申请</a>';
    	                return str;
	               }
	        ];
    	 dwr.util.addRows("FeeModelTable",fList,cellFuncs,{escapeHtml:false});
    	 var oTable = document.getElementById("FeeModelTable");
         var oTr = oTable.insertRow();
         var oTd = oTr.insertCell();
         oTd.colSpan=11;
         oTd.style.textAlign ="left";
         oTd.innerHTML = "&nbsp;&nbsp;<input type='button' onclick='batchToggleGateApply(0)' value='关闭申请'/>&nbsp;&nbsp;<input onclick='batchToggleGateApply(1)' type='button' value='开通申请'/>";
     }
     
     function editGate(mid,gate,gid,calcMode){
			MerchantService.getAuthTypeList(mid,gate,gid,calcMode,showEditAuthTypeWin);
	 }
   //close gate
	  var gate_temp;

     /**
     * @param o
     * checkbox全选
     */
    function toggleAll(o){
    		var status = o.checked;
    		var checkboxes=document.getElementsByName("check");
    		for(var i = 0;i<checkboxes.length;i++){
    			checkboxes[i].checked=status;
    		}
    	}
     
	      var f = new GreyFrame('INLINEIFRAME',700,400);
		  f.setClassName("MyGreyFrame"); //指定框架的样式名�?
		var currentConfig;
		function showEditAuthTypeWin(aList){
            var mid = aList[0];
            var gate = aList[1];
            var thisGateRoute = aList[2];
            var merFeeModel = aList[3];
            currentConfig = thisGateRoute+merFeeModel;
			var gateRoutes = aList[4];//select gid from gates 根据网关查渠�?为什么要查gates�?不查gate_route表？
			if(gateRoutes.length==0){
				alert("请在 [系统配置]>[增加银行网关] 中为银行网关增加渠道后，再配置此网关");
				return false;
			}
			var win = ""; 
			win += "<table class=\"tablelist tablelist2\" >";
			win += "<tr><td>商户手续费：</td>";
			win += "<td><input type=\"text\" id='calcMode' value='"+merFeeModel+"' style=\"width: 200px;margin-left: 30px;height:20px \"></td></tr>";
			win += "<tr><td><span style='letter-spacing: 3.5px;'>使用渠道<span>：</td>";
            win += "<td><select id='gid' style=\"width: 200px;margin-left: 30px;height:20px \">";
            for(var k=0;k<gateRoutes.length;k++){
                if(gateRoutes[k]==thisGateRoute){//gate_route_map select gid,name from gate_route order by gid
            	 win +=  "<option value='"+gateRoutes[k]+"'>"+gate_route_map[gateRoutes[k]]+"</option>";
            	    break;
            	 }
            }
            for(var k=0;k<gateRoutes.length;k++){
                if(gateRoutes[k]!=thisGateRoute){
                  win +=  "<option value='"+gateRoutes[k]+"'>"+gate_route_map[gateRoutes[k]]+"</option>";
                 }
            }
            win += "</select>";
            win += "</td></tr>";
            win += "<tr><td rowspan='2'><span style='letter-spacing: 3.5px;'>生效时间<span>：</td><td><input style='vertical-align: middle;' checked='checked' name='effectTime' value='0' type='radio'>实时生效&nbsp;&nbsp;<input style='vertical-align: middle;' name='effectTime' value='1' type='radio'>定时生效</td></tr>"
            win += "<tr><td><input id='efctvTime' class='Wdate' onfocus='WdatePicker({skin:\"ext\",minDate:\"%y-%M-%d\",maxDate:\"2099-12-31\",dateFmt:\"yyyyMMddHHmm\",readOnly:\"true\"});' type='text' /></td></tr>"
            win += "<tr><td colspan='2' style=\"height:30px \">";
            win += "<input type=\"button\" value='配置申请' onclick=configGateApply('"+mid+"',"+gate+"); style=\"width: 100px;margin-left: 30px;height:25px \"></td></tr>";
            win += "</table>";
            f.openHtml('银行网关配置', win, 500, 150);

		}
		
		var args_mid,args_mName,args_status,args_state,args_type;
		function searchPage(){
			args_mid = $("mid").value;
			args_mName = $("mName").value;
			args_status = $("status").value;
			args_state = $("state").value;
			args_type = $("type").value;
			showGateConfigApply(1);
		}
		
		function toggleTh(){
			var var1 = jQuery("#toggleAll1Th");
			var var2 = jQuery("#toggleAll2Th");
			if(args_status==0){
				if(var1.length == 0 ){
					jQuery("#shh1").before('<th id="toggleAll1Th" sort="false" ><input id="toggleAll1" type="checkbox" style="vertical-align: middle;" onclick="toggleAll(this);"/>全选</th>');
				}
				if(var2.length == 0 ){
					jQuery("#shh2").before('<th id="toggleAll2Th" sort="false" ><input id="toggleAll2" type="checkbox" style="vertical-align: middle;" onclick="toggleAll(this);"/>全选</th>');
				}
			}else{
				jQuery("#toggleAll1Th").remove();
				jQuery("#configTable tr").each(function(){
					var tds = jQuery(this).children("td");
						for(var i=0;i<tds.length;i++){
							if(jQuery(tds[i]).html()==""){
								jQuery(tds[i]).remove();
								break;
							}
						}
					});
			jQuery("#toggleAll2Th").remove();
			jQuery("#switchTable tr").each(function(){
				var tds = jQuery(this).children("td");
					for(var i=0;i<tds.length;i++){
						if(jQuery(tds[i]).html()==""){
							jQuery(tds[i]).remove();
							break;
						}
					}
				});
		}
	}
		
		/**
		 * 申请查询
		 */
		function showGateConfigApply(pageNo){
			jQuery("#toggleAll1").attr("checked",false);
			jQuery("#toggleAll2").attr("checked",false);
			MerchantService.showGateConfigApply(args_mid,args_mName,pageNo,args_status,args_state,args_type,function(pageObj){
				var cellFuncs;
				var list;
				if(args_type == 0){//开关申请
					  $("switchTable").style.display="";
					  $("configTable").style.display="none";
					   dwr.util.removeAllRows("switchList");
					   dwr.util.removeAllRows("configList");
					   if(pageObj==null){
				 	   document.getElementById("switchList").appendChild(creatNoRecordTr(9));
						   return;
					   }  
				     cellFuncs = [
				                   function(obj){ return obj.status==0?'<input align="left" name="check" type="checkbox" value='+obj.id+' />':'' ; },
			                       function(obj){ return obj.mid;},
				   	               function(obj){ return obj.gate;},
				   	               function(obj){ return h_gate[obj.gate];},
				   	               function(obj){ return h_gate_route_type[obj.transMode];},
				   	               function(obj){ return obj.toState == 0?"关闭申请":"开启申请";},
				   	               function(obj){ return obj.state ==0 ? "关闭":"开启";},
			                       function(obj){ return obj.status==0?'未审核':obj.status==2?'审核失败':'审核通过'; },
			                       function(obj){ return obj.status==0?('<a href="#" onclick="checkGateConfig(1,'+obj.id+')">审核通过</a>&nbsp;&nbsp;<a href="#" onclick="checkGateConfig(0,'+obj.id+')">审核失败</a>'):'<span style="color:silver;">审核通过</span>&nbsp;&nbsp;<span style="color:silver;">审核失败</span>';}
				                  ];
				     list="switchList";
				}else{//配置申请
					  $("switchTable").style.display="none";
					  $("configTable").style.display="";
					   dwr.util.removeAllRows("switchList");
					   dwr.util.removeAllRows("configList");
					   if(pageObj==null){
				 	   document.getElementById("configList").appendChild(creatNoRecordTr(13));
						   return;
					   }  
				     cellFuncs = [
				                   function(obj){ return obj.status==0?'<input align="left" name="check" type="checkbox" value='+obj.id+' />':'' ; },
			                       function(obj){ return obj.mid;},
				   	               function(obj){ return obj.gate;},
				   	               function(obj){ return h_gate[obj.gate];},
				   	               function(obj){ return obj.gate;},
				   	               function(obj){ return h_gate_route_type[obj.transMode];},
				   	               function(obj){ return obj.bkFeeMode;},
				   	               function(obj){ return obj.calcMode == obj.toCalcMode?('<span >'+obj.calcMode+'</span>'):('<span title="申请时在用商户手续费:'+obj.calcMode+'" style="color:red;">'+obj.toCalcMode+'</span>');},
				   	               function(obj){ return obj.gid == obj.toGid?('<span >'+gate_route_map[obj.gid]+'</span>'):('<span title="申请时在用渠道:'+gate_route_map[obj.gid]+'" style="color:red;">'+gate_route_map[obj.toGid]+'</span>');},
				   	               function(obj){ return obj.state ==0 ? "关闭" : "开启";},
				   	               function(obj){ return obj.effectiveTime;},
			                       function(obj){ return obj.status==0?'未审核':obj.status==2?'审核失败':'审核通过'; },
			                       function(obj){ return obj.status==0?('<a href="#" onclick="checkGateConfig(1,'+obj.id+')">审核通过</a>&nbsp;&nbsp;<a href="#" onclick="checkGateConfig(0,'+obj.id+')">审核失败</a>'):'<span style="color:silver;">审核通过</span>&nbsp;&nbsp;<span style="color:silver;">审核失败</span>';}
				                  ];
				     list="configList";
				}
				var str = args_status==0?'&nbsp;&nbsp;&nbsp;&nbsp;<input onclick="batchCheckGateConfig(1)" type="button" value="审核通过"/>&nbsp;&nbsp;&nbsp;&nbsp;<input onclick="batchCheckGateConfig(2)" type="button" value="审核失败"/>':'';
				paginationTable(pageObj,list,cellFuncs,str,"showGateConfigApply");
				toggleTh();
			});
		}
		
		function batchCheckGateConfig(status){
			if(!confirm("提交申请？")){
				return;
			}
			var ids = new Array();
			jQuery(":checkbox[name='check'][checked=true]").each(function(){
				ids.push(this.value);
			});
			if(ids.length == 0){
				alert("请选择要审核的记录");
				return;
			}
			MerchantService.batchCheckGateConfig(ids,status,function(msg){
				alert(msg);
				showGateConfigApply(1);
			});
		}
		
		/**
		 * @param status
		 * @param id
		 * 审核申请
		 */
		function checkGateConfig(status,id){
			if(!confirm("提交审核？")){
				return;
			}
			MerchantService.checkGateConfig(status,id,function(msg){
				alert(msg);
				showGateConfigApply(1);
			});
		}
		 /**
	      * 批量 开关 网关 申请
	      */
	     function batchToggleGateApply(toState){
	    	 var rytGateList = new Array();
	    	 jQuery(":checkbox[name='check'][checked=true]").each(function(i){
	    		 rytGateList.push(this.value);
	    	 });
			if(rytGateList.length == 0){
				alert("请选择要申请的网关");
				return;
			}
	    	 MerchantService.batchToggleGateApply(rytGateList,toState,function(msg){
	    		 alert(msg);
	    	 });
	     }
	     
	     /**
	      * 开关网关申请
	      */
	     function toggleGateApply(mid,rytGate,state){//state 当前状态 1开启 0关闭
	     	var cfm = confirm("确定要申请"+(state==0?"开启":"关闭")+"该网关吗？");
	     	if(!cfm){
	     		return;
	     	}
	     	var params = {};
	     	params["type"]=0;
	     	params["state"]=state;
	     	params["mid"]=mid;
	     	params["rytGate"]=rytGate;
	     	//申请关网关
	     	MerchantService.merGateConfigApply(params,function(msg){
	     		alert(msg);
	     	});
	 	}
	     
	     /**
	      * 配置网关申请
	      */
	     function configGateApply(mid,rytGate){
	    	 if(!confirm("提交申请？")){
	    		 return;
	    	 }
	    	 var gid = $("gid").value;
	    	 var calcMode = $("calcMode").value;
	    	 if(!calcMode){
	    		 alert("计费公式不能为空");
	    		 return;
	    	 }
	    	 if(!checkFeeModel(calcMode)){
		        	alert("计费公式输入有误，请重新输入");
		        	$('calcMode').value='';
		        	return;
	        }
	    	 if(currentConfig==(gid+calcMode)){
	    		 alert("配置未修改");
	    		 return;
	    	 }
	    	 var effectiveTime = 0;//0实时生效 1 定时生效
	    	 var radios = document.getElementsByName("effectTime");
	    	 for(var i = 0; i< radios.length; i++){
	    		 if(radios[i].checked){
	    			 if(radios[i].value == '1'){
	    				 effectiveTime = $("efctvTime").value;
	    				 if(!effectiveTime){
	    					 alert("请选择生效时间");
	    					 return;
	    				 }
	    			 }
	    			 break;
	    		 }
	    	 }
	    	 var params = {};
	    	 params["mid"] = mid;
	    	 params["rytGate"] = rytGate;
	    	 params["gid"] = gid;
	    	 params["calcMode"] = calcMode;
	    	 params["effectiveTime"] = effectiveTime;
	     	 params["type"]=1;//网关配置申请
	    	 MerchantService.merGateConfigApply(params,function(msg){
	     		alert(msg);
	     	});
	    	f.close();
	     }
	     
	     function batchApply(status){//0关闭 1 开启
	    	 var argsArr = new Array();
	 		var checkboxes=document.getElementsByName("check");
	 		for(var i = 0;i<checkboxes.length;i++){
	 			var box = checkboxes[i];
				var boxStatus = box.attributes.status;
	 			if(box.checked && boxStatus != status){//只操作需要操作的
	 				argsArr.push(box.value);
	 			}
	 		}
//	 		MerchantService
	     }
		function configMerGate(mid,gate){
	        var mobj = document.getElementById('config_win_mer_fee'); 
	        if(mobj.value==''){
	            alert('请填写手续费公式');
	            return false;
	        }
	        
	        //var obj = document.getElementById('ensure'); 
	        var feeModel = mobj.value.trimAll();
	        document.getElementById('config_win_mer_fee').value = feeModel;
	        //判断公式书写是否�?�?
	        if(checkFeeModel(feeModel)){
	            //alert("计费公式输入正确!");
	            //mobj.disabled = true;
	            //o.value = "重置";
	            //o.onclick = function(){ $('mer_calc_model_id').value=''; reset(this);}
	            //obj.disabled = false;
	        }else{
	        	alert("计费公式输入有误，请重新输入");
	        	$('config_win_mer_fee').value='';
	        	return false;
	        }
		   var merFeeModel = $("config_win_mer_fee").value;//判断
		   var gid  = $("config_win_auth_type").value;
		   MerchantService.configMerGate(mid,gate,gid,merFeeModel,
			  function(msg){
		    	    if(msg=='ok'){
		    	        f.close();
		    	        //queryGate();
		    		    //showMerFeeCalaModels($("show_mer_fee_model_id").value);
		    	        changeConf();
			    	    }else{ alert(msg);} 
		      });

		   $(gate+'close').disabled=false;

	   }
		  
		  function returnValue(msg){
			  
			  if(msg==1){ 
				  alert('关闭成功!');
				  queryGate();
				  showMerFeeCalaModels($("show_mer_fee_model_id").value);
				  $(gate_temp).disabled=true;
			  }else{
				  alert('关闭失败!');
			  }
		  }
	    function checkModel(o){
	        
	        var mobj = document.getElementById('mer_calc_model_id'); 
	        if(mobj.value==''){
	            alert('请填写手续费计费公式');
	            return false;
	        }
	        
	        var obj = document.getElementById('ensure'); 
	        var feeModel = mobj.value.trimAll();
	        document.getElementById('mer_calc_model_id').value = feeModel;
	        //判断公式书写是否正确
	        if(checkFeeModel(feeModel)){
	            alert("计费公式输入正确!");
	            mobj.disabled = true;
	            o.value = "重置";
	            o.onclick = function(){ $('mer_calc_model_id').value=''; reset(this);};
	            obj.disabled = false;
	        }else{
	        	alert("计费公式输入有误，请重新输入");
	        }
	    } 
	     function reset(o){
		       o.value = "检查";
	       o.onclick = function(){checkModel(this);};
	       document.getElementById('mer_calc_model_id').disabled = false; 
	       document.getElementById('ensure').disabled = true;     
	    }
	     function changeConf(){
	    	 var mid = $("mid").value; 
	         // var smid = $("smid").value;
	          if(mid ==""){alert("请选择商户号");return false;}
	         // if(smid==""){alert("商户不存在，请重新输入！");return false;}
	          $("selected_mer_id").value = mid; 
	          MerchantService.getMerGateConfigInfo(mid,transMode,changeConf_callbacks);
	    	 
	     }
	     function changeConf_callbacks(aList){
	     	var gList = aList[0];
	 	    var fList = aList[1];
	 	    var mid = $("mid").value;
	         dwr.util.removeAllRows("FeeModelTable");
	         //未开通网关显�?
	         $("gateRouteTable").style.display="";
	         $("gateRouteTitle").innerHTML = "&nbsp;&nbsp;商户<b> ["+m_minfos[mid]+"]</b> 未开通的支付网关";
	         var gateStr = "<table  style=\"width: 100%;\">";
	         var wg =[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18];
	         var wg_Name=["","网银支付网关:","消费充值卡支付:","信用卡支付","","增值业务","语音支付:","企业网银支付:","手机WAP支付:","","POS支付:","对私代付:","对公代付:","B2B充值:","","对私代扣:","对公提现:","对私提现:","快捷支付:"];
	 		 gateStr += "<tr><td colspan=8><font style=\"color:blue;\">"+wg_Name[transMode]+"</font></td></tr><tr>";
	     	 var tmp=0;
	 		 for (var i = 1; i <= gList.length; i++) {
	         		var RYTGate = gList[i-1];
	         			tmp=tmp+1;
	         			gateStr += "<td><input type=\"checkbox\" name=\"rytGate\" ttype='"+RYTGate.transMode+"' value='"+RYTGate.gate+"'/>&nbsp;"+ RYTGate.gateName + "</td>";
	         			if(tmp%8==0) gateStr += "</tr><tr>";
	         }
	 		 if(tmp!=0){
	 			var trs=8-(tmp%8);
	     		if(trs>0&&trs!=8){
	     			for ( var int = 0; int < trs; int++) {
	     				gateStr +="<td style='width:140px;'></td>";
	 				}
	     			gateStr+="</tr>";	
	     		}
	 		 }
	     	 gateStr += "</table>";
	     	 $("gateRouteCell").innerHTML = gateStr;
	     	 //已开通网关显�?
	 		 $("openGatesTable").style.display = "";
	 		 feeCalcModeCache.length = 0;//清空缓存
	 		 showSupportGateList(fList,true);
	 		 showMerFeeCalaModels($("show_mer_fee_model_id").value);
	      }
	     //查询所�?
	      function queryGate_init(){
	    	  transMode = $("trans_mode").value;
	    	  $("selected_gates_trans_model_id").value=-1;
	    	  $("show_mer_fee_model_id").value=-1;
	          var mid = $("mid").value; 
	          if(mid ==""){alert("请选择商户号！");return false;}
	          if(!transMode) {alert("请选择网关类型");return false;}
	          MerchantService.checkmidiscunzai(mid,function callback1(msg){
	        	  if(null==msg){
	        		  alert("商户不存在！");
	        		  window.location.href="A_68_SHWGPZSQ.jsp";
	        		  return false;	  
	        	  }else{
	    	          $("selected_mer_id").value = mid; 
	    	          fullname=msg.name;
	    	          mer_type=msg.merType==0?"企业":msg.merType==1?"个人":"集团";
	    	          MerchantService.getMerGateConfigInfo(mid,transMode,callback); 
	        	  }
	          })
	         
	      }
		 