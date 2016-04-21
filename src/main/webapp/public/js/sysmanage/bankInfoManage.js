//联行号维护
var mapArr;
jQuery(document).ready(function(){
	init();
});
//从缓存中获取 对私/对公 代付的网关 及代付渠道
function init(){
	PageService.getDFGateChannelMapByType3([11],function(mapArr1){
		 mapArr = mapArr1;
		dwr.util.addOptions("gateId", mapArr[0]);
//		dwr.util.addOptions("type",typeMap);
	}); 
}

function queryForPage(pageNo){
	var bankNoInfo = wrapFormByName("queryForm");
	BankInfoService.queryForPage(bankNoInfo,pageNo,function(pageObj){
		document.getElementById("customTable").style.display="";
		dwr.util.removeAllRows("customTbody");
		if (pageObj == null) {
			document.getElementById("customTbody").appendChild(creatNoRecordTr(4));
			return;
		}
		var cellFuncs = [ 
	  		                 function(obj) {return mapArr[0]["71"+obj.gid];},
	  		                 function(obj) {return obj.bkNo;},
			                 function(obj) {return obj.bkName;}, 
			                 function(obj) {return '<input class="button" type="button" value = "修改" onclick="toModify('+obj.id+');"/><input class="button" type="button" value = "删除" onclick="del('+obj.id+')"/>';}];
		paginationTable(pageObj,"customTbody",cellFuncs,"","queryForPage");
	});
}


function toAdd() {
	var mapArr2;
	dwr.engine.setAsync(false);
	PageService.getDFGateChannelMapByType3([11],function(mapArr1){
		 mapArr2 = mapArr1;
	});
	dwr.engine.setAsync(true);  
	//开户行
    var html = '<tr><td class="th1" align="right">开户行：</td><td><select name="gid" id ="gid" style="width: 150px;height: 20px;">';

    dwr.util.removeAllOptions("gid");
    for(var key in mapArr2[0]){
		html += '<option value="'+key+'">'+mapArr2[0][key]+'</option>';
		
	}
    html += '</select>&nbsp;<font color="red">*</font>&nbsp;<input type="text" maxlength="6" id="gateName1" name="gateName" onblur="gateRouteList1()"/></td></tr>';
    //联行行号
    html += '<tr><td class="th1" align="right">联行行号：</td><td><input name="bkNo" type="text" maxlength="12"/>&nbsp;<font color="red">*</font>&nbsp;</td></tr>';
    //联行名称
    html += '<tr><td class="th1" align="right">联行名称：</td><td><input name="bkName" type="text" maxlength="20"/>&nbsp;<font color="red">*</font>&nbsp;</td></tr>';
    
    html += '<tr><td colspan="2" align="center"><input type="button" value="确认" onclick="add();"/>&nbsp;&nbsp;<input type="button" value="取消" onclick="jQuery(\'#wBox_close\').click();"/></td></tr>';
	jQuery("#pupUpTable").html(html);
	jQuery("#pupUpTable").wBox({title : "&nbsp;&nbsp;新增联行行号",show : true});//显示弹出层
	
	
}

function toModify(id) {
	var mapArr3;
	PageService.getDFGateChannelMapByType3([11],function(mapArr1){
		 mapArr3 = mapArr1;
	});
	BankInfoService.queryById(id,function(bni){
		//开户行
	    var html = '<tr><td class="th1" align="right">开户行：</td><td><select name="gid" id="ugid" style="width: 150px;height: 20px;">';
	    dwr.util.removeAllOptions("ugid");
	    for(var key in mapArr3[0]){
			html += '<option value="'+key+'">'+mapArr3[0][key]+'</option>';
		}
	    html += '</select>&nbsp;<font color="red">*</font>&nbsp;<input type="hidden" name="id" value="'+bni.id+'"/><input type="text" maxlength="6" id="gateName2" name="gateName" onblur="gateRouteList2()"/></td></tr>';
	    //联行行号
	    html += '<tr><td class="th1" align="right">联行行号：</td><td><input name="bkNo" type="text" value="'+bni.bkNo+'" maxlength="20"/>&nbsp;<font color="red">*</font>&nbsp;</td></tr>';
	    //联行名称
	    html += '<tr><td class="th1" align="right">联行名称：</td><td><input name="bkName" type="text" value="'+bni.bkName+'" maxlength="20"/>&nbsp;<font color="red">*</font>&nbsp;</td></tr>';
	    
	    html += '<tr><td colspan="2" align="center"><input type="button" value="确认" onclick="modify();"/>&nbsp;&nbsp;<input type="button" value="取消" onclick="jQuery(\'#wBox_close\').click();"/></td></tr>';
		jQuery("#pupUpTable").html(html);
		jQuery("#pupUpTable").wBox({title : "&nbsp;&nbsp;修改联行行号",show : true});//显示弹出层
		jQuery('#wBox #pupUpTable select[name=gid]').val("71"+bni.gid);//渠道选中当前值
	});
}

function modify(){
	var bni = {};
	bni.gid = jQuery('#wBox #pupUpTable select[name=gid]').val();
	bni.bkName = jQuery('#wBox #pupUpTable input[name=bkName]').val();
	bni.bkNo = jQuery('#wBox #pupUpTable input[name=bkNo]').val();
	bni.id = jQuery('#wBox #pupUpTable input[name=id]').val();
	if(bni.bkNo == ""){
		alert("联行号不能为空");
		return;
	}
	if(bni.bkName == ""){
		alert("联行名称不能为空");
		return;
	}
	BankInfoService.update(bni,function(count){
		if(count == 1){
			alert("更新成功");
			jQuery("#wBox_close").click();//关闭弹出层
			queryForPage(1);
		}else{
			alert("更新失败");
		}
	});
}
//wrapObj(formId,attrNameFun)

function add(){
	//查找wbox弹出的隐藏层中的元素 需要在#wBox下查找
	var bankNoInfo = {};
	bankNoInfo.bkNo = jQuery('#wBox #pupUpTable input[name=bkNo]').val().trim();
	bankNoInfo.gid = jQuery('#wBox #pupUpTable select[name=gid]').val();
	bankNoInfo.bkName = jQuery('#wBox #pupUpTable input[name=bkName]').val().trim();
	if(bankNoInfo.bkNo == ""){
		alert("联行号不能为空");
		return;
	}
	
	if (isFigure(bankNoInfo.bkNo) && bankNoInfo.bkNo.length == 12){
		
	}
	else{
		alert("联行号只能是12位的数字！");
		return false;
	}
	
	if(bankNoInfo.bkName == ""){
		alert("联行名称不能为空");
		return;
	}
	BankInfoService.add(bankNoInfo,function(count){
		if(count == 1){
			alert("新增成功");
			jQuery("#wBox_close").click();//关闭弹出层
			queryForPage(1);
		}else{
			alert("新增失败");
		}
	});
}

function del(id){
	if(confirm("确认删除？")){
		BankInfoService.delById(id,function(count){
			if(count == 1){
				alert("删除成功");
				queryForPage(1);
			}else{
				alert("删除失败");
			}
		});
	}
}

//根据id自动封装表单对象
function wrapFormByName(formId,attrNameFun){
  var obj={};
   var elements=document.getElementById(formId).elements;
   for(var i=0;i<elements.length;i++){
     var nodeName=elements[i].nodeName;
     var nodeType=elements[i].getAttribute("type");
     if((nodeName=="INPUT"&&(nodeType==null||nodeType=="text"||nodeType=="hidden"||
     		nodeType=="radio"||nodeType=="checkbox"))||nodeName=="SELECT"||nodeName=="TEXTAREA"){
        var objName=elements[i].getAttribute("name");
         if(attrNameFun!=undefined)objName=attrNameFun(objName);//对objName的属性做处理的函数；
         objName=reversionChar(objName,1);//_的转换
         obj[objName]=elements[i].value;
     }
   }
   return obj;
}


function gateRouteList(){
	 var gateName = $("gateName").value;
	 PageService.getGateMapByTypeAndName([11],gateName,function(mapArr1){
		 mapArr = mapArr1;
		 dwr.util.removeAllOptions("gateId");
		dwr.util.addOptions("gateId", mapArr[0]);
	 });
	 
}

function gateRouteList1(){
	 var gateName = $("gateName1").value;
	 PageService.getGateMapByTypeAndName([11],gateName,function(mapArr1){
		var mapArr2 = mapArr1;
		 dwr.util.removeAllOptions("gid");
		dwr.util.addOptions("gid", mapArr2[0]);
	 });
	 
}

function gateRouteList2(){
	 var gateName = $("gateName2").value;
	 PageService.getGateMapByTypeAndName([11],gateName,function(mapArr1){
		var mapArr3 = mapArr1;
		 dwr.util.removeAllOptions("ugid");
		dwr.util.addOptions("ugid", mapArr3[0]);
	 });
	 
}