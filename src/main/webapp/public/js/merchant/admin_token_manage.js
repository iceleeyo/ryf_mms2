var token,operId,currentPageNo;
function toQuery(){
	token = wrapFormByName("queryForm");
	queryForPage(1);
}
function queryForPage(pageNo){
	if(!token) return;
	TokenManagementService.queryConfigForPage(token,pageNo,function(pageObj){
		currentPageNo = pageNo;
		document.getElementById("operlogTable").style.display="";
		if(pageObj.length==0){
			document.getElementById("resultList").appendChild(creatNoRecordTr(9));
			return false;
		}
//		系统名称	令牌序列号	商户	操作员号	操作员名	绑定时间	禁用时间	令牌状态	操作
        var cellFuncs = [
                         function(obj) { return sysMap[obj.system]; },
                         function(obj) { return obj.tokenSn; },
                         function(obj) { return obj.mid; },
                         function(obj) { return obj.operId; },
                         function(obj) { return obj.operName; },
                         function(obj) { return obj.bindTimeStr; },
                         function(obj) { return obj.disableTimeStr; },
                         function(obj) { return obj.status==0?"禁用":"启用"; },
                         function(obj) { 
                        	 			var text = obj.status==0?"启用":"禁用";
                        	 			return "<button onclick='toggleStatus("+obj.status+","+obj.id+");'>"+text+"</button>&nbsp;&nbsp;<button onclick='toModify("+obj.id+")'>修改</button>";	 }
                     ];
              paginationTable(pageObj,"resultList",cellFuncs,"","queryForPage");
          });
}

function toBind(){
	$("id").value = "";
	jQuery("#subBtn").unbind("click");//先解除之前绑定的方法
	jQuery("#subBtn").bind("click", function() { bind(); });
	jQuery("#addToken").wBox({title:"&nbsp;&nbsp;新增绑定信息",show:true});//显示box
}

function toggleStatus(currentStatus,id){
	if(!confirm("确认"+(currentStatus==0?"启用":"禁用")+"?"))
		return;
	TokenManagementService.toggleStatus(currentStatus,id,function(msg){
		alert(msg);
		if("设置成功" == msg) 
			queryForPage(currentPageNo); 
	});
}

function toModify(id){
	if(!id)
		return;
	TokenManagementService.queryConfigById(id,function(tokenConfig){
		$("id").value = tokenConfig.id;
		$("tokenSn").value = tokenConfig.tokenSn;
		$("system").value = tokenConfig.system;
		$("mid").value = tokenConfig.mid;
		operId = tokenConfig.operId;
		jQuery("#mid").change();
	});
	jQuery("#subBtn").unbind( "click" );//先解除之前绑定的方法
	jQuery("#subBtn").bind("click", function() { modify(); });
	jQuery("#addToken").wBox({title:"&nbsp;&nbsp;修改绑定信息",show:true});//显示box
}

function bind(){
	if(!confirm("确认新增?"))
		return;
	var newToken = new Object();
	newToken.tokenSn = $("tokenSn").value.trim();
	newToken.system = $("system").value.trim();
	newToken.mid = $("mid").value.trim();
	newToken.operId = $("operId").value.trim();
	$("wBox_close").click();
	TokenManagementService.addToken(newToken,function(msg){
		alert(msg);
		if (msg=="新增成功") {
			queryForPage(currentPageNo);
		}
	});
}

function modify(){
	if(!confirm("确认修改?"))return;
	var toBeModified = new Object();
	toBeModified.tokenSn = $("tokenSn").value.trim();
	toBeModified.system = $("system").value.trim();
	toBeModified.mid = $("mid").value.trim();
	toBeModified.operId = $("operId").value.trim();
	toBeModified.id = $("id").value.trim();
	$("wBox_close").click();
	TokenManagementService.modifyTokenConfig(toBeModified,function(msg){
		alert(msg);
		if (msg=="修改成功") {
			queryForPage(currentPageNo);
		}
	});
}

function init(){
	TokenManagementService.getSystemTypeMap(function(map){
		sysMap = map;
		dwr.util.addOptions("system", map);
	});
}

//根据选择的商户显示他的操作员
function showOper(operId){
	var mid = $("mid").value;
	if(mid&&!isFigure(mid)){
   	   alert("商户号只能是整数!");
   	   $("mid").value = "";
           return false;
       }
	if(mid){
		 MerchantService.getImportentMsgByMid(mid, function(minfo) {
	  	  		if (minfo == null) {
	  	  			alert("商户不存在!");
	  	  			dwr.util.removeAllOptions("operId");
	  	  			dwr.util.addOptions("operId",{"":"请选择..."});
	  	  			return false;
	  	  		}
	       	     MerchantService.showOPers(mid,"",showOperCallback);
        	});
	}else{
			dwr.util.removeAllOptions("operId");
			dwr.util.addOptions("operId",{"":"请选择..."});
		MerchantService.showOPers(mid,"",showOperCallback);
	}
}
	
function showOperCallback(dataMap){
		opermap=dataMap;
		dwr.util.removeAllOptions("operId");
		dwr.util.addOptions("operId",{"":"请选择..."});
	    dwr.util.addOptions("operId",dataMap);
	    if($("operId")){
	    	$("operId").value = operId;
	    	operId = undefined;
	    }
}

//根据id自动封装表单对象
function wrapFormByName(formId){
  var obj={};
   var elements=document.getElementById(formId).elements;
   for(var i=0;i<elements.length;i++){
     var nodeName=elements[i].nodeName;
     var nodeType=elements[i].getAttribute("type");
     if((nodeName=="INPUT"&&(nodeType==null||nodeType=="text"||nodeType=="hidden"||
     		nodeType=="radio"||nodeType=="checkbox"))||nodeName=="SELECT"||nodeName=="TEXTAREA"){
    	 var objName=elements[i].getAttribute("name");
         obj[objName]=elements[i].value;
     }
   }
   return obj;
}
