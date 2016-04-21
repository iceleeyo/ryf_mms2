var f = new GreyFrame('INLINEIFRAME',700,400);
var type;
var cfg = {};
var win;
function config(configType,mc){
	type = configType;
	if(mc)
		cfg = mc;
	else cfg = {}; 
	f.setClassName("MyGreyFrame");
	if(!win){
		var changePart = '';
		var html = '';
		//var h_gate = {};
		var title = '';
		if(configType == 0){
			
			title='交易模式配置';
			var transMode = '';
			jQuery.each(h_gate_route_type, function(key, value) {
				transMode += '<option value = '+key+'>'+value+'</option>';
			}); 
			changePart = "<tr><td colspan='5'>交易类型：&nbsp;<select style='width:104px;' id='targetId' onchange=\"query4NewGate(this.value);\">"+transMode+"</select><font color='red'>&nbsp;*&nbsp;</font></td></tr>";
			
			html ="<tr><td colspan='5'>交易银行：&nbsp;<select style='width:104px;'  id='gateId'></select></td></tr>";
			//query4NewGate();
			
		}else if(configType == 1){
			title='重要商户配置';
			changePart = "<tr><td colspan='5'>重要商户：&nbsp;<input id='targetId' style='width:102px;color:silver;' onblur='validateMerNo(this)' onfocus='if(this.value==\"商户号..\")this.value=\"\"; jQuery(this).css(\"color\",\"black\");' type='text' value='商户号..'/><font color='red'>&nbsp;*&nbsp;</font></td></tr>";
			html ="<tr><td><input id='gateId' type='hidden' value='0' /></td></tr>";
		}
		var win = ""; 
		win += "<form id='configForm'><table class=\"tablelist tablelrightist2\" >";
		win += changePart;
		win += html;
		win += "<tr><td colspan='5'>闲忙状态：&nbsp;<select onchange='change()' style='width:60px;' id='busyIdle'><option value='0'>忙时</option><option value='1'>闲时</option></select><font color='red'>&nbsp;请切换此选项卡分别配置闲忙时间&nbsp;*&nbsp;</font></td></tr>";
		win += "<tr><td>交易类型</td><td>数量范围<font color='red'>&nbsp;*&nbsp;</font></td><td>警告值<font color='red'>&nbsp;*&nbsp;</font></td><td>紧急值<font color='red'>&nbsp;*&nbsp;</font></td><td>时间范围<font color='red'>&nbsp;*&nbsp;</font></td></tr>";
		win += "<tr><td style='text-align:right;'><span style='letter-spacing: 5px;'>成功订单数</span>：</td><td><select id='judgerIndexS'><option value='0'>≥</option><option value='1'>≤</option></select></td><td style='text-align:left;padding-left:10px;'><input maxlength='10' id='successN' type='text' style='width:50px;'/></td><td><input maxlength='10' id='successM' type='text' style='width:50px;'/></td><td><input maxlength='4' id='busyIntervalS' type='text' style='width:50px;'/><input id='idleIntervalS' maxlength='4' type='text' style='width:50px;display: none;'/>分</td></tr>";
		win += "<tr><td style='text-align:right;'><span style='letter-spacing: 5px;'>失败订单数</span>：</td><td><select id='judgerIndexF'><option value='0'>≥</option><option value='1'>≤</option></select></td><td style='text-align:left;padding-left:10px;'><input maxlength='10' id='failN' type='text' style='width:50px;'/></td><td><input maxlength='10' id='failM' type='text' style='width:50px;'/></td><td><input maxlength='4' id='busyIntervalF' type='text' style='width:50px;'/><input maxlength='4' id='idleIntervalF' type='text' style='width:50px;display: none;'/>分</td></tr>";
		win += "<tr><td style='text-align:right;'>连续失败订单数：</td><td><select id='judgerIndexC'><option value='0'>≥</option><option value='1'>≤</option></select></td><td style='text-align:left;padding-left:10px;'><input maxlength='10' id='continualFailN' type='text' style='width:50px;'/></td><td><input maxlength='10' id='continualFailM' type='text' style='width:50px;'/></td><td><input maxlength='4' id='busyIntervalC' type='text' style='width:50px;'/><input maxlength='4' id='idleIntervalC' type='text' style='width:50px;display: none;'/>分</td></tr>";
		//win += "<tr><td style='text-align:right;'><span style='letter-spacing: 2px;'>待支付订单数</span>：</td><td><select id='judgerIndexW'><option value='0'>≥</option><option value='1'>≤</option></select></td><td style='text-align:left;padding-left:10px;'><input maxlength='10' id='waitN' type='text' style='width:50px;'/></td><td><input maxlength='10' id='waitM' type='text' style='width:50px;'/></td><td>当天</td></tr>";
		//win += "<tr><td style='text-align:right;'>累计失败订单数：</td><td><select id='judgerIndexSf'><option value='0'>≥</option><option value='1'>≤</option></select></td><td style='text-align:left;padding-left:10px;'><input maxlength='10' id='sumFailN' type='text' style='width:50px;'/></td><td><input maxlength='10' id='sumFailM' type='text' style='width:50px;'/></td><td>当天</td></tr>";
		win += "<tr style='height: 64px;'><td colspan='5'><input id='submitBtn' style='width:80px;' onclick='addConfig()' type='button' value='保存'/><input style='width:80px;' type='button' onclick='f.close()' value='取消'/></td></tr>";
	    win += "</table><input id='id' type='hidden'/></form>";
	    f.openHtml(title, win, 400,245);
	    if(configType==0){
	    	 targetId =  $("targetId").value;
	 	    query4NewGate(targetId);
	    }
	    //alert(win);
	   
	}
    
}

function validateMerNo(o){
	if(!o.value){
		o.value="商户号.."; 
		jQuery(o).css("color","silver");
	}else{
		MonitorConfigService.isMerNoExists(o.value,function(flag){
			if(!flag){
				alert("商户号不存在");
				o.value="商户号.."; 
				jQuery(o).css("color","silver");
			}
		});
	}
}

/**
 *数据校验
 *judgerIndexS,SuccessN,successM
 *judgerIndexF,failN,failM
 *judgerIndexC,continualFailN,continualFailM
 */
var fields = {successN:"成功订单数警告值",successM:"成功订单数紧急值",busyIntervalS:"成功订单数忙时监控时间",idleIntervalS:"成功订单数闲时监控时间"
	,failN:"失败订单数警告值",failM:"失败订单数紧急值",busyIntervalF:"失败订单数忙时监控时间",idleIntervalF:"失败订单数闲时监控时间"
		,continualFailN:"连续失败订单数警告值",continualFailM:"连续失败订单数紧急值",busyIntervalC:"连续失败订单数忙时监控时间",idleIntervalC:"连续失败订单数闲时监控时间"};
function validate(config){
	for (var key in fields) {
		if(!isInteger(config[key])){
			alert(fields[key]+"必须为正整数");
			return false;
		}
	}
	if(config.judgerIndexS==0 && (parseInt(config.successM)<=parseInt(config.successN))){
		alert("当成功订单'判定规则'为'≥'时,紧急值必须大于警告值");
		return false;
	}else if(config.judgerIndexS == 1 && (parseInt(config.successM) >= parseInt(config.successN))){
		alert("当成功订单监控判定规则为 ≤ 时,紧急值必须小于警告值");
		return false;
	}
	if(config.judgerIndexF==0 && (parseInt(config.failM)<=parseInt(config.failN))){
		alert("当失败订单监控判定规则为 ≥ 时,紧急值必须大于警告值");
		return false;
	}else if(config.judgerIndexF == 1 && (parseInt(config.failM) >= parseInt(config.failN))){
		alert("当失败订单监控判定规则为 ≤ 时,紧急值必须小于警告值");
		return false;
	}
	if(config.judgerIndexC==0 && (parseInt(config.continualFailM)<= parseInt(config.continualFailN))){
		alert("当连续失败订单监控判定规则为 ≥ 时,紧急值必须大于警告值");
		return false;
	}else if(config.judgerIndexC == 1 && (parseInt(config.continualFailM)>=parseInt(config.continualFailN))){
		alert("当连续失败订单监控判定规则为 ≤ 时,紧急值必须小于警告值");
		return false;
	}
	/*if(config.judgerIndexW==0 && (parseInt(config.waitM)<=parseInt(config.waitN))){
		alert("当待支付订单监控判定规则为 ≥ 时,紧急值必须大于警告值");
		return false;
	}else if(config.judgerIndexW == 1 && (parseInt(config.waitM) >= parseInt(config.waitN))){
		alert("当待支付订单监控判定规则为 ≤ 时,紧急值必须小于警告值");
		return false;
	}*/
	/*if(config.judgerIndexSf==0 && (parseInt(config.sumFailM)<=parseInt(config.sumFailN))){
		alert("当累计失败订单监控判定规则为 ≥ 时,紧急值必须大于警告值");
		return false;
	}else if(config.judgerIndexSf == 1 && (parseInt(config.sumFailM) >= parseInt(config.sumFailN))){
		alert("当累计失败订单监控判定规则为 ≤ 时,紧急值必须小于警告值");
		return false;
	}*/
	return true;
}


function addConfig(){
	cfg = wrapObj("configForm");
	if(!validate(cfg)){
		return;
	}
	cfg["type"]=type;
	MonitorConfigService.addConfig(cfg,callback);
}

function change(){
	jQuery("#busyIntervalS").toggle();
	jQuery("#idleIntervalS").toggle();
	jQuery("#busyIntervalF").toggle();
	jQuery("#idleIntervalF").toggle();
	jQuery("#busyIntervalC").toggle();
	jQuery("#idleIntervalC").toggle();
}

function callback(msg){
	if("添加成功" == msg){
		f.close();
	}
	alert(msg);
	queryConfigPage(1);
}
jQuery(document).ready(function() {
	initMinfos();
	PageService.getGateRouteMap(function(gateRouteMap){
		gate_route_map=gateRouteMap;
	});
	PageService.getGateChannelMap1(function(mapArr){
		h_gate1=mapArr[0];
		
	});
	setTimeout('queryConfigPage(1)',1000);
}); 

function queryConfigPage(pageNo){
	MonitorConfigService.queryConfigPage(pageNo,pageCallBack);
}

function pageCallBack (list) {
      dwr.util.removeAllRows("resultList");
	  if(list==null){
		  document.getElementById("resultList").appendChild(creatNoRecordTr(7));
		  return;
	   }  
	var count = 0;
    var cellFuncs = [
                     function(obj) { return ++count; },
                     function(obj) { if(obj.type == 0){
			                    	 	return h_gate_route_type[obj.targetId];
			                    	 }else{
			                    		// return m_minfos[obj.targetId];
			                    		 return obj.targetName;
			                    	 } },
                	 function(obj) { return obj.type == 0?"交易类型":"重要商户"; },
                	 function(obj) {if(obj.type == 0){ return h_gate1[obj.gateId]; }},
                     function(obj) { return "成功订单："+obj.busyIntervalS+"分钟"+getSymbol(obj.judgerIndexS)+obj.successN+"笔(N), "+obj.busyIntervalS+"分钟"+getSymbol(obj.judgerIndexS)+obj.successM+"笔(M)<br/>"+
                		 					"失败订单："+obj.busyIntervalF+"分钟"+getSymbol(obj.judgerIndexF)+obj.failN+"笔(N), "+obj.busyIntervalF+"分钟"+getSymbol(obj.judgerIndexF)+obj.failM+"笔(M)<br/>"+
                		 					"连续失败订单："+obj.busyIntervalC+"分钟"+getSymbol(obj.judgerIndexC)+obj.continualFailN+"笔(N), "+obj.busyIntervalC+"分钟"+getSymbol(obj.judgerIndexC)+obj.continualFailM+"笔(M)<br/>"; },
 					function(obj) { return  "成功订单："+obj.idleIntervalS+"分钟"+getSymbol(obj.judgerIndexS)+obj.successN+"笔(N), "+obj.idleIntervalS+"分钟"+getSymbol(obj.judgerIndexS)+obj.successM+"笔(M)<br/>"+
					 						"失败订单："+obj.idleIntervalF+"分钟"+getSymbol(obj.judgerIndexF)+obj.failN+"笔(N), "+obj.idleIntervalF+"分钟"+getSymbol(obj.judgerIndexF)+obj.failM+"笔(M)<br/>"+
					 						"连续失败订单："+obj.idleIntervalC+"分钟"+getSymbol(obj.judgerIndexC)+obj.continualFailN+"笔(N), "+obj.idleIntervalC+"分钟"+getSymbol(obj.judgerIndexC)+obj.continualFailM+"笔(M)<br/>"; },
                     //function(obj) { return	"待支付订单：当天"+getSymbol(obj.judgerIndexW)+obj.waitN+"笔(N)，当天"+getSymbol(obj.judgerIndexW)+obj.waitM+"笔(M)<br/>" +
        		 	//						"累计失败订单：当天"+getSymbol(obj.judgerIndexSF)+obj.sumFailN+"笔(N)，当天"+getSymbol(obj.judgerIndexSF)+obj.sumFailM+"笔(M)"},
                     function(obj) { return "<a href='#' onclick='getConfigById("+obj.id+")'>修改</a>"
					 							 +"&nbsp;&nbsp;&nbsp;&nbsp;<a href=\"#\" onclick=\"delConfig("+ obj.id +")\" >删除</a>"
					 							;}
					 							//+" &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<a href='#' onclick='delConfig("+obj.id+")'>删除</a> ";}

                 ];
  	paginationTable(list,"resultList",cellFuncs,"","queryConfigPage");
  };

  function getSymbol(index){
	  if(index == 1){
		  return "≤";
	  }else{
		  return "≥";
	  }
  }
  
  function getConfigById(id){
	  MonitorConfigService.getConfigById(id,function(mc){
		  if(!mc){
			  alert("查询数据失败");
			  return;
		  }
		  
		  config(mc.type,mc);
		  jQuery("#targetId").prop("disabled",true);
		  jQuery("#submitBtn").attr("onclick","modifyConfig()");
		  jQuery("#gateId").prop("disabled",true);
		  jQuery.each(mc, function(key, value) {
				jQuery("#"+key).val(value);
			}); 
	  });
  }
  
function modifyConfig(){
		cfg= wrapObj('configForm');
		cfg["type"]=type;
		
		if(!validate(cfg)){
			return;
		}
		MonitorConfigService.modifyConfig(cfg,function(msg){
			alert(msg);
			if("更新成功" == msg){
				f.close();
				queryConfigPage(1);
			}
		});
	}

//根据id自动封装表单对象
function wrapObj(formId){
  var obj={};
   var elements=document.getElementById(formId).elements;
   for(var i=0;i<elements.length;i++){
     var nodeName=elements[i].nodeName;
     var nodeType=elements[i].getAttribute("type");
     if((nodeName=="INPUT"&&(nodeType==null||nodeType=="text"||nodeType=="hidden"||
     		nodeType=="radio"||nodeType=="checkbox"))||nodeName=="SELECT"||nodeName=="TEXTAREA"){
        var objName=elements[i].getAttribute("id");
         if(objName) obj[objName]=elements[i].value;
     }
   }
   return obj;
}

function query4NewGate(targetId){
	dwr.engine.setAsync(false);
	if(!targetId) targetId =  $("targetId").value;
	
    SysManageService.queryGatesMapByTransMode(targetId,function(gateList){
    	 dwr.util.removeAllOptions("gateId");
   	  	dwr.util.addOptions("gateId", gateList);	 

    });
    dwr.engine.setAsync(true);
 }

function delConfig(id){
	 if (confirm("是否确定删除?")) {
		 MonitorConfigService.delConfig(id,function(msg){
			 alert(msg);
			 queryConfigPage(1);
		 });
		 };
		
}