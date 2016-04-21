var mapArr;
var limitTypeMap ={0:"不限",1:"5万元以下",2:"5万元以上"};
var typeMap = {11:"对私代付",12:"对公代付"};
jQuery(document).ready(function(){
	init();
});
//从缓存中获取 对私/对公 代付的网关 及代付渠道
function init(){
	PageService.getDFGateChannelMapByType3([11,12],function(mapArr1){
		mapArr = mapArr1;
		dwr.util.addOptions("gid", mapArr[1]);
		dwr.util.addOptions("type",typeMap);
		queryDefault();
		queryCustom(1);
	});
}

function refresh(){
	DfGateRouteService.refresh(function(msg){
		alert(msg);
	});
}

function queryDefault(){
	var conf = {};
	conf.configType = 0;
	DfGateRouteService.query(conf,1,function(pageObj){
		var html = "";
		for ( var i = 0; i < pageObj.pageItems.length; i++) {
			var obj = pageObj.pageItems[i];
			html+='<tr><td>'+typeMap[obj.type]+'</td><td>'+mapArr[1][obj.gid]+'</td><td>'+ limitTypeMap[obj.limitType]+'</td><td><input type="button" value="修改" onclick="toModify('+obj.id+')"/></td></tr>';
		}
		jQuery("#defaultTbody").html(html);
	});
}

function queryCustom(pageNo){
	var config = wrapFormByName("queryForm");
	config.configType = 1;
	DfGateRouteService.query(config,pageNo,function(pageObj){
		dwr.util.removeAllRows("customTbody");
		if (pageObj == null) {
			document.getElementById("customTbody").appendChild(creatNoRecordTr(5));
			return;
		}
		var cellFuncs = [ 
	  		                 function(obj) {return typeMap[obj.type];},
	  		                 function(obj) {return  mapArr[0][obj.gateId];},
			                 function(obj) {return mapArr[1][obj.gid];}, 
			                 function(obj) {return limitTypeMap[obj.limitType];}, 
			                 function(obj) {return '<input class="button" type="button" value = "修改" onclick="toModify('+obj.id+');"/><input class="button" type="button" value = "删除" onclick="del('+obj.id+')"/>';}];
		paginationTable(pageObj,"customTbody",cellFuncs,"","queryCustom");
	});
}

function toAdd() {
	//交易类型
    var html = '<tr><td class="th1" align="right">交易类型：</td><td><select onchange="filterGate();" name="type" style="width: 150px;height: 20px;">';
    for ( var key in typeMap) {
    	html += '<option value="'+key+'">'+typeMap[key]+'</option>';
	}
    html += '</select></td></tr>';
    //网关
    html += '<tr><td class="th1" align="right">代付网关：</td><td><select name="gateId" style="width: 150px;height: 20px;">';
//    for(var key in mapArr[0]){
//    	html += '<option value="'+key+'">'+mapArr[0][key]+'</option>';
//    }
    html += '</select>&nbsp;<input id="keyword" name ="keyword" maxlength="30" type="text" value="输入网关名称自动匹配" style="height:22px;color:silver;" onfocus="if(this.value==\'输入网关名称自动匹配\'){this.value=\'\';this.style.color=\'black\'}" onblur="if(this.value==\'\'){this.value=\'输入网关名称自动匹配\';this.style.color=\'silver\';}"/></td></tr>';
    //渠道
    html += '<tr><td class="th1" align="right">代付渠道：</td><td><select name="gid" style="width: 150px;height: 20px;">';
    for ( var key in mapArr[1]) {
    	html += '<option value="'+key+'">'+mapArr[1][key]+'</option>';
	}
    html += '</select></td></tr>';
    //交易额度
    html += '<tr><td class="th1" align="right">交易额度：</td><td><select name="limitType" style="width: 150px;height: 20px;">';
    for ( var key in limitTypeMap) {
    	html += '<option value="'+key+'">'+limitTypeMap[key]+'</option>';
	}
    html += '</select></td></tr>';
    
    html += '<tr><td colspan="2" align="center"><input type="button" value="确认新增" onclick="add();"/></td></tr>';
	jQuery("#pupUpTable").html(html);
	jQuery("#pupUpTable").wBox({title : "&nbsp;&nbsp;新增代付网关配置",show : true});//显示弹出层
	//给网关搜索框绑定事件
	jQuery('#wBox #keyword').live('keyup', function(event) {
	    var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode; 
		if(keyCode == 13){ 
		selectGateIdByName();
		return false; 
		} 
	});
	//根据代付类型 过滤网关
	filterGate();
}

/**
 * 根据对公对私代付 过滤网关 
 */
function filterGate(){
	var tradeType = jQuery('#wBox #pupUpTable select[name=type]').val();
	var options = "";
	if(tradeType == "11"){//对私代付
		for ( var gateId in  mapArr[0]) {
			if(gateId.indexOf('71') == 0){//gateId以71开头
				options += '<option value="'+gateId+'">'+mapArr[0][gateId]+'</option>';
			}
		}
	}else{//对公代付
		for ( var gateId in  mapArr[0]) {
			if(gateId.indexOf('72') == 0){//gateId以72开头
				options += '<option value="'+gateId+'">'+mapArr[0][gateId]+'</option>';
			}
		}
	}
	jQuery('#wBox #pupUpTable select[name=gateId]').html(options);
}

function toModify(id) {
	DfGateRouteService.queryById(id,function(config){
		//交易类型
	    var html = '<tr><td class="th1" align="right">交易类型：</td><td>&nbsp;'+typeMap[config.type]+'<input name="id" type="hidden" value="'+config.id+'" /></td></tr>';
	    //网关
	    if(config.configType ==1){
	    	html += '<tr><td class="th1" align="right">代付网关：</td><td>&nbsp;'+mapArr[0][config.gateId]+'</td></tr>';
	    }
	    //渠道
	    html += '<tr><td class="th1" align="right">代付渠道：</td><td><select name="gid" value="'+config.gid+'" style="width: 150px;height: 20px;">';
	    for ( var key in mapArr[1]) {
	    	html += '<option value="'+key+'">'+mapArr[1][key]+'</option>';
		}
	    html += '</select></td></tr>';
	    //交易额度
    	html += '<tr><td class="th1" align="right">交易额度：</td><td>&nbsp;'+limitTypeMap[config.limitType]+'</td></tr>';
	    html += '<tr><td colspan="2" align="center"><input type="button" value="确认修改" onclick="modify();"/></td></tr>';
		jQuery("#pupUpTable").html(html);
		jQuery("#pupUpTable").wBox({title : "&nbsp;&nbsp;新增代付网关配置",show : true});//显示弹出层
		jQuery('#wBox #pupUpTable select[name=gid]').val(config.gid);//渠道选中当前值
	});
}

function modify(){
	var config = {};
	config.gid = jQuery('#wBox #pupUpTable select[name=gid]').val();
	config.id = jQuery('#wBox #pupUpTable input[name=id]').val();
	DfGateRouteService.doUpdate(config,function(count){
		if(count == 1){
			alert("更新成功");
			jQuery("#wBox_close").click();//关闭弹出层
			queryDefault();
			queryCustom(1);
		}else{
			alert("更新失败");
		}
	});
}
//wrapObj(formId,attrNameFun)

function add(){
	//查找wbox弹出的隐藏层中的元素 需要在#wBox下查找
	var config = {};
	config.type = jQuery('#wBox #pupUpTable select[name=type]').val();
	config.gid = jQuery('#wBox #pupUpTable select[name=gid]').val();
	config.gateId = jQuery('#wBox #pupUpTable select[name=gateId]').val();
	config.limitType = jQuery('#wBox #pupUpTable select[name=limitType]').val();
	
	config.keyword = jQuery('#wBox #pupUpTable input[name=keyword]').val().trim();
	if(!chinese(config.keyword)){
		alert("只能输入中文");
		return false;
	}
		
	DfGateRouteService.add(config,function(count){
		if(count == 1){
			alert("新增成功");
			jQuery("#wBox_close").click();//关闭弹出层
			queryCustom(1);
		}else{
			alert("新增失败");
		}
	});
}

function del(id){
	if(confirm("确认删除？")){
		DfGateRouteService.delById(id,function(count){
			if(count == 1){
				alert("删除成功");
//				jQuery("#wBox_close").click();
				queryCustom(1);
			}else{
				alert("删除失败");
			}
		});
	}
}

function selectGateIdByName(){
	var name = jQuery("#wBox #keyword").val();
	var gateId = "";
	for ( var key in mapArr[0]) {
		if(mapArr[0][key].indexOf(name)!= -1){
			gateId = key;
			break;
		}
	}
	if(gateId!=""){
		jQuery('#wBox #pupUpTable select[name=gateId]').val(gateId);
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
