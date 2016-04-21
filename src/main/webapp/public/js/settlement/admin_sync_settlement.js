function init(){
	
      $("beginDate").value = jsToday();
      $("endDate").value = jsToday();
	dwr.util.addOptions("mstate", m_mstate);
}
var merInfo = {};
RypCommon.getHashMer( function(data) {
		merInfo = data;
});
var gatesMap = {};
RypCommon.getHashGate(function(m) {
		gatesMap = m;
});
var query_cash = {};
var currentPage = 1;
function checkdata(date_b, date_e, batch) {
	
	if (!judgeDate(date_b, date_e)) {
		alert("开始日期应先于结束日期");
		return false;
	}
	if (!isFigure(batch.trim())) {
		alert("批次号只能为数字，请重新输入！");
		//clean("batch");
		return false;
	}
	return true;
}
/*function clean(id){
    document.getElementById(id).value = '';
    document.getElementById(id).focus();
}*/

function query(p){ 
	
	var date_b = document.getElementById("beginDate").value;
	var date_e = document.getElementById("endDate").value;
	var batch = document.getElementById("batch").value;
	//var state  = document.getElementById("state").value;
	var mid = document.getElementById("mid").value;
	var mstate=document.getElementById("mstate").value;
	var liqState = document.getElementById("liqState").value;
	
	 if(mid !='' && !isFigure(mid)){
			alert("商户号只能是整数!");
			$("mid").value = '';
			return false; 
 	 }
	
	
	if (checkdata(date_b,date_e,batch)) {
		SettlementVerifyService.getAccFeeLiqBath(p, date_b, date_e, mid,  batch,mstate,liqState,resultData);
	}
}

var gateRouteCache={};//缓存

//var chxCount = 0;
var resultData = function (flbPage) {
	 $("detailResultList").style.display="";
	 dwr.util.removeAllRows("bodyTable");
	 var count=0;
	if (flbPage.pageTotle != 0) {
		var cellFuncs = [
		                 //function(obj) { return  obj.state==2?"<input type='checkbox' id='toCheck' name='batchs' value='"+  obj.batch + "'/>":"";},
		                 function(obj){
		                	 count++;
				        	   gateRouteCache[count]=obj;
				        	   if(obj.accState==0){
				        		   "<input type='radio' onclick='' name='gateRouteCount' value='"+count+"' disabled='false'/>";
			                	 }
				        	   else{
				        		   return "<input type='radio' onclick='' name='gateRouteCount' value='"+count+"' />";
				        	   }
				        	   return "<input type='radio' onclick='' name='gateRouteCount' value='"+count+"' disabled='false'/>";
				           },
		                 function(obj) { return obj.mid; },
		                 function(obj) { return  merInfo[obj.mid]; },
		                 function(obj) { return obj.userId; },//账户号
		                 function(obj) { return div100(obj.transAmt); },
		                 //function(obj) { return merInfo[obj.mid]; },//交易笔数
		                 function(obj) { return div100(obj.refAmt); },
		                 function(obj) { return div100(obj.transAmt - obj.refAmt); },
		                 function(obj) { return div100(obj.feeAmt); },
		                 function(obj) { return div100(obj.refFee); },
		                 function(obj) { return div100(obj.manualAdd); },
		                 function(obj) { return div100(obj.manualSub); },
		                 function(obj) { return div100(obj.liqAmt); },
		                 function(obj) { return obj.batch; },
		                 function(obj) { return obj.liqDate; },
		                 function(obj) { 
		                	 if(obj.accState==0){
		                		 return "成功"; 
		                	 }
		                	 else if(obj.accState==1){
		                		 return "失败"; 
		                	 }
		                	},//结算同步状态	
		                 function(obj) { return formatDate1(obj.accMdate); },//结算同步时间
		                 function(obj) { return obj.accNotice; },//	备注
		                 function(obj) { 
		                	// chxCount++;
		                	 return "<input class=\"button\" type=\"button\"  onclick = downloadSyncDetail('"+obj.batch+"','"+merInfo[obj.mid]+"','"+obj.userId+"') value=\"下载XLS\"/>";
		                	 
		                	/* "<a href='javascript:showSettlement_detail(\""
		                	 +obj.batch+"\");'><u><font color='blue'>下载xls</font></u></a>";*/  	                      
		                 }
		                
		              ];
		for(var i=0;i<flbPage.pageItems.length;i++){
			var o = flbPage.pageItems[i];
   	        query_cash[o.batch] = o;
		}
		 var str="<span  style='float:left'>&nbsp;结算总金额：<font color='red'><b>" + div100(flbPage.sumResult["amtSum"])  +"</b></font>  元&nbsp;&nbsp;&nbsp;&nbsp;<input type='button' onclick='checkChx()' value='结果订单同步' id='commitButton' /></span>";		
		
		paginationTable(flbPage,"bodyTable",cellFuncs,str,"query"); 
		
	}else {
		document.getElementById("bodyTable").appendChild(creatNoRecordTr(18));
	}
};



function checkChx() {
	var countArr= getValuesByName("gateRouteCount");
		if(countArr.length==0){
			alert("请选择一条记录！");
			return false;
		}
	
	 //获取选中的第一个radio
	 var gateRoute=gateRouteCache[countArr[0]];
	var date_b = document.getElementById("beginDate").value;
	var date_e = document.getElementById("endDate").value;
	//alert(chx);
	var batch=gateRoute.batch;
	var mid=gateRoute.mid;
	if(batch == 0) {
	alert("请选择一条记录!");
	return false;
	}
	if(!confirm("确认同步？"))return false;
	SettlementVerifyService.accSync(batch,date_b,date_e,mid,function(msg){
		alert(msg);
		query(currentPage);
	});
}

//快捷用户信息查询下载
function downloadSyncDetail(batch,minfoName,userId){
	//if(!judgeCondition())return;
    dwr.engine.setAsync(false);//把ajax调用设置为同步
	  SettlementVerifyService.downloadSyncDetail(batch,minfoName,userId,
				function(data){dwr.engine.openInDownload(data);});
    
    
  
}


