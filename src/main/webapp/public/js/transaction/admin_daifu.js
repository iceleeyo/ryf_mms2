var mapArr;
var isJGTB = false;
var pageNo1;
var limitTypeMap ={0:"不限",1:"5万元以下",2:"5万元以上"};
var typeMap = {11:"对私代付",12:"对公代付"};
var stateMap = {0:"初始状态",1:"银行处理中",2:"交易成功",3:"交易失败"};
jQuery(document).ready(function(){
	init();
});
//从缓存中获取 对私/对公 代付的网关 及代付渠道
function init(){
    $("bdate").value = jsToday();
    $("edate").value = jsToday();
	PageService.getDFGateChannelMapByType3([11,12],function(mapArr1){
		mapArr = mapArr1;
		dwr.util.addOptions("gid", mapArr[1]);
		dwr.util.addOptions("type",typeMap);
		dwr.util.addOptions("tstat",stateMap);
		//填充交易银行select,关表中以71/72开头的网关,网关号取后三位剔重
		var html = jQuery("#gate").html();
		for ( var gateId in mapArr1[0]) {
			var subId = gateId.substring(2,gateId.length);
			html += '<option value="'+subId+'">'+mapArr1[0][gateId]+'</option>';
		}
		jQuery("#gate").html(html);
	});
}

/**
 * 同步代付结果
 */
function syncDfResult(){
	var ids = getDfIds();
	if(ids.length == 0){
		alert("请至少选择一条记录");
		return;
	}
	DfService.syncDfResult(ids,function(map){
		var html = '<tr><td class="th1" style="text-align:center;padding:0px 20px;">电银流水号</td><td class="th1" style="text-align:center;padding:0px 20px;">同步结果</td></tr>';
		var count = 0;
		for (var tseq in map) {
			count++;
			html += '<tr><td style="text-align:center;">'+tseq+'</td><td style="text-align:center;">'+map[tseq]+'</td></tr>';
		}
		html+= '<tr><td class="th1" style="text-align:center;" colspan="2"><input type="button" class="wBox_close button" value="确定" onclick="queryForPage('+pageNo1+')"/></td></tr>';
		jQuery("#pupUpTable").html(html);
		jQuery("#pupUpTable").wBox({title : "&nbsp;&nbsp;同步结果",show : true});//显示弹出层
	});
}

/**
 * 通知商户
 */
function notifyMerchant(){
	var ids = getDfIds();
	if(ids.length == 0){
		alert("请至少选择一条记录");
		return;
	}
	DfService.notifyMerchant(ids,function(map){
		var html = '<tr><td class="th1" style="text-align:center;padding:0px 20px;">电银流水号</td><td class="th1" style="text-align:center;padding:0px 20px;">通知结果</td></tr>';
		var count = 0;
		for (var tseq in map) {
			count++;
			html += '<tr><td style="text-align:center;">'+tseq+'</td><td style="text-align:center;">'+map[tseq]+'</td></tr>';
		}
		html+= '<tr><td class="th1" style="text-align:center;" colspan="2"><input type="button" class="wBox_close button" value="确定"/></td></tr>';
		jQuery("#pupUpTable").html(html);
		jQuery("#pupUpTable").wBox({title : "&nbsp;&nbsp;通知商户结果",show : true});//显示弹出层
	
		
	});
}

/**
 * @returns {Array}
 * 获取所有选中的代付记录id
 */
function getDfIds(){
	var arr = new Array();
	jQuery("#resultList input[type=checkbox]").each(function(){
		var obj = jQuery(this);
		if(obj.prop("checked")){
			arr.push(obj.val());
		}
	});
	return arr;
}

var dataSource = {1:"支付系统",2:"账户系统",3:"清算系统",4:"资金托管系统",5:"POS系统",6:"新账户系统"};
function getDataSource(code){
	for ( var key in dataSource) {
		if(key == code){
			return dataSource[key];
		}
	}
	return "未知";
}

function showDfDetails(tseq){
	var columsInfo = {tseq:"电银流水号",accountId:"账户号",orderId:"订单号",
					  transAmt:"交易金额",gate:"交易银行",gid:"代付渠道",dataSource:"数据来源",
					  sysDateStr:"系统时间",bkSendDateStr:"发送银行时间",bkDateStr:"银行响应时间",
					  tstat:"交易状态",bkSeq:"银行流水号",bkNo:"开户行号",
					  purpose:"用途",accNo:"收款人帐号",areaProv:"开户省份",
					  errorCode:"错误代码",accName:"收款人户名",areaCity:"开户城市",
					  errorMsg:"失败原因"};
	DfService.queryByTseq(tseq,function(dfTr){
		var html = '<tr>';
		var count = 0;
		for (var key in columsInfo) {
			count++;
			var columSpan = key=='errorMsg'?' colspan="5"':'';
			var columVal = dfTr[key];
			if(key == "gate")columVal = mapArr[0][columVal];
			if(key == "gid") columVal = mapArr[1][columVal];
			if(key == "tstat") columVal = stateMap[columVal];
			if(key == "transAmt") columVal = div100(columVal);
			if(key == "dataSource") columVal = getDataSource(columVal);
			if(columVal==null || columVal == undefined){
				columVal = "";//若为null或者undefined用空字符串代替
			}
			html += '<td class="th1" align="right">'+columsInfo[key]+'：</td><td'+columSpan+' style="padding: 0px 10px;">'+columVal+'</td>';
			if(count % 3 == 0){
				html += key=='errorMsg'?'</tr>':'</tr><tr>';
			}
		}
		jQuery("#pupUpTable").html(html);
		jQuery("#pupUpTable").wBox({title : "&nbsp;&nbsp;代付交易明细",show : true});//显示弹出层
	});
}
function queryForPage(pageNo){
	pageNo1 = pageNo;
	var dfTr = wrapObj("queryForm");
	if(dfTr.bdate == '' || dfTr.edate == ''){
		alert("请输入系统日期");
		return;
	}
	DfService.queryForPage(dfTr,pageNo,function(pageObj){
		document.getElementById("dfjyTable").style.display = "";
		dwr.util.removeAllRows("resultList");
		if (pageObj == null) {
			var rows = 11;
			if(isJGTB) rows=12;
			document.getElementById("resultList").appendChild(creatNoRecordTr(rows));
			return;
		}
		var cellFuncs = [ 
	  		                 function(obj) {return '<a href="#" onclick="showDfDetails(\''+obj.tseq+'\')" style="text-decoration: none;cursor: pointer;padding:0px 10px;" >'+obj.tseq+'</a>';},
	  		                 function(obj) {return obj.accountId;},
	  		                 function(obj) {return obj.orderId;},
			                 function(obj) {return div100(obj.transAmt);}, 
			                 function(obj) {return stateMap[obj.tstat];}, 
			                 function(obj) {return typeMap[obj.type];}, 
			                 function(obj) {return mapArr[0][obj.gate];}, 
			                 function(obj) {return mapArr[1][obj.gid];}, 
			                 function(obj) {return getDataSource(obj.dataSource);}, 
			                 function(obj) {return obj.sysDateStr;}, 
			                 function(obj) {return obj.bkSeq;}, 
			                 function(obj) {return obj.errorMsg?createTip(16,obj.errorMsg):obj.errorCode;}];
		var footer = isJGTB?"":"<span class='pageSum' style='margin-left:20px;'>交易总金额 <font color='red'>"+div100(pageObj.sumResult.amtSum)+"</font> 元</span>";
		if(isJGTB) {
			cellFuncs.splice(0, 0,  function(obj) {return '<input type="checkbox" value="'+obj.tseq+'"/>';});
			cellFuncs.splice(11, 1,  function(obj) {return obj.accNo;});
			cellFuncs.splice(12, 1,  function(obj) {return obj.accName;});
			footer = '&nbsp;<button onclick="toggleAll()">全选</button>&nbsp;<button onclick="syncDfResult()">银行结果同步</button>&nbsp;<button onclick="notifyMerchant()">通知商户</button>';
		}
		
		paginationTable(pageObj,"resultList",cellFuncs,footer,"queryForPage");
	});
}

//导出报表 0代付交易查询 1代付结果同步
function downloadXls(type){
	var dfTr = wrapObj("queryForm");
	if(dfTr.bdate == '' || dfTr.edate == ''){
		alert("请输入系统日期");
		return;
	}
	DfService.downloadXls(dfTr,type,function(data){
		dwr.engine.openInDownload(data);
	});
}
//如果没有全选则全选 如果已经全选 则全部取消选择
function toggleAll(){
	var isAllChecked = true;
	jQuery("input[type=checkbox]").each(function(){
		var item = jQuery(this);
		if(!item.prop("checked")){
			isAllChecked = false;
			item.prop("checked",true);
		}
	 });
	if(isAllChecked){
		jQuery("input[type=checkbox]").each(function(){
			jQuery(this).prop("checked",false);
		 });
	}
}
