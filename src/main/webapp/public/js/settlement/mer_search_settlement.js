
//与商户后台的search_settlement.jsp中的js一样   
function initParams(){
	 PageService.getGatesMap(function(m) {
		h_gate = m;
	 });  
	 initMinfos();
}
   
/*************************商户结算单查询***************************/
//DIV隐藏显示控制方法
function showDivOne(){
  document.getElementById("DivOne").style.display = '';
  document.getElementById("DivTwo").style.display = 'none';
  document.getElementById("DivThree").style.display = 'none';    
}
function showDivTwo(){
  document.getElementById("DivOne").style.display = 'none';
  document.getElementById("DivTwo").style.display = '';
  document.getElementById("DivThree").style.display = 'none';    
}
function showDivThree(){
  document.getElementById("DivOne").style.display = 'none';
  document.getElementById("DivTwo").style.display = 'none';
  document.getElementById("DivThree").style.display = '';    
}
function showNoDiv(){
  document.getElementById("DivOne").style.display = 'none';
  document.getElementById("DivTwo").style.display = 'none';
  document.getElementById("DivThree").style.display = 'none';    
}
//选择单选框事件
function ChooseRadio(value,action){
  document.getElementById(action).value = value;
}
//根据条件查询
function queryBaseSettlement(pageNo) {

  var mid = document.getElementById("mid").value.trim();
  var date_b = document.getElementById("bdate").value.trim();
  var date_e = document.getElementById("edate").value.trim();
  
  if(mid == ''){
      alert("请输入商户号！");
      return false;
  }
  if (!isFigure(mid)){
	  alert("商户号只能是整数!");$("mid").value='';
      return false;
  }
  if (date_b == '' || date_e == ''){
      alert("请选择查询日期!");
      return false;
  }
  if (!judgeDate(date_b, date_e)){
      alert("开始日期应先于结束日期");
      return false;
  }
     document.getElementById("radio_batch").value = "nodata";
     document.getElementById("radio_gate").value = "nodata";
     MerSettlementService.searchFeeLiqBath(pageNo, mid, date_b, date_e, callSettlementList);
    //提取用户列表的回设函数：callSettlementList中放的是FeeLiqBath对象
    
	function callSettlementList(SettlementList) {
					showDivOne();			
				    var cellFuncs = [
				                     function(obj) { return "<input type='radio' value='"+obj.batch+"' name='select_bid' id='select_bid' onclick=\"ChooseRadio(this.value,'radio_batch')\">"; },
				                     function(obj) { return obj.mid; },
				                     function(obj) { return m_minfos[obj.mid]; },
				                     function(obj) { return obj.batch; },
				                     function(obj) { return obj.liqDate; },
				                     function(obj) { return div100(obj.transAmt); },
				                     function(obj) { return div100(obj.refAmt);},
				                     function(obj) { return div100(obj.manualAdd); },
				                     function(obj) { return div100(obj.manualSub);},
				                     function(obj) { return div100(obj.feeAmt); },
				                     function(obj) { return div100(obj.refFee);},  
				                     function(obj) { return div100(obj.liqAmt);}
				                 ]
				         var str = "<input type='button' class='button' value='查看明细' onclick=\"queryDetail('FormOne','qlog')\">&nbsp;&nbsp;&nbsp;"
	              			+ "<input type='button' class='button' value='下载Excel明细' onclick=\"downLoadXLS('FormOne','"+mid+"','"+date_b+"','"+date_e+"','qlog')\">";
		    paginationTable(SettlementList,"TableOne",cellFuncs,str,"queryBaseSettlement");
	};
};
//根据条件查询
function querySettlement(page, mid, date_b, date_e) {
  document.getElementById("radio_batch").value = "nodata";
  document.getElementById("radio_gate").value = "nodata";
  MerSettlementService.searchFeeLiqBath(page, mid, date_b, date_e, callSettlementList);
}

//第一次查看详细
function queryLiqFeeLog(batch) {
  document.getElementById("radio_gate").value = "nodata";
    MerSettlementService.queryLiqFeeLog(batch, callSettlementOneList);
}
//提取用户列表的回设函数：callSettlementOneList中放的是FeeLiqLog对象
var callSettlementOneList = function(SettlementOneList) {
if (SettlementOneList.length == 0) {
alert("没有符合条件的数据");
showDivOne();
return false;
}
showDivTwo();
dwr.util.removeAllRows("TableTwo");
//取得html中表格对象
var dtable = document.getElementById("TableTwo");
for (var i = 0; i < SettlementOneList.length; i++) {

   FeeLiqLog = SettlementOneList[i];
   // 在table中新建一行
   var elTr = dtable.insertRow(-1);
   // 选择
   var chooseTd = elTr.insertCell(-1);
   chooseTd.innerHTML = "<input type='radio' value='"+FeeLiqLog.gate+"' name='select_gid' id='select_gid' onclick=\"ChooseRadio(this.value,'radio_gate')\">";
   // 商户号
   var midTd = elTr.insertCell(-1);
   midTd.innerHTML = FeeLiqLog.mid;
   // 商户简称
   var abbrevTd = elTr.insertCell(-1);
   abbrevTd.innerHTML = m_minfos[FeeLiqLog.mid];
   // 银行网关
   var batchTd = elTr.insertCell(-1);
   batchTd.innerHTML = h_gate[FeeLiqLog.gate];
   // 支付金额
   var purAmtTd = elTr.insertCell(-1);
   purAmtTd.innerHTML = FeeLiqLog.purAmt/100;
   // 退款金额
   var refAmtTd = elTr.insertCell(-1);
   refAmtTd.innerHTML = FeeLiqLog.refAmt/100;
   // 手续费
   var feeAmtTd = elTr.insertCell(-1);
   feeAmtTd.innerHTML = FeeLiqLog.feeAmt/100;
   // 退还商户手续费
   var feeAmtTd = elTr.insertCell(-1);
   feeAmtTd.innerHTML = FeeLiqLog.refFee/100;
}
var pageTable = document.getElementById("TableTwo");
//dwr.util.removeAllRows("TableTwo");
var actionTr = pageTable.insertRow(-1);
var bTd = actionTr.insertCell(-1);
 bTd.setAttribute("colSpan","10");
 actionTr.setAttribute("role","bottom");
 actionTr.setAttribute("align","center")
var mid = document.getElementById("mid").value.trim();
var date_b = document.getElementById("bdate").value.trim();
var date_e = document.getElementById("edate").value.trim();
var str_action = "<input type='button' class='button' value='查看明细' onclick=\"queryDetail('FormTwo','qhlog')\">&nbsp;&nbsp;&nbsp;"
              + "<input type='button' class='button' value='下载Excel明细' onclick=\"downLoadXLS('FormTwo','"+mid+"','"+date_b+"','"+date_e+"','qhlog')\">&nbsp;&nbsp;&nbsp;"
              + "<input type='button' class='button' value='返回' onclick=\"javascript:showDivOne()\">";
bTd.innerHTML = str_action;
bTd.style.textAlign = "left";
};
//第二次查看详细
function queryHlog(batch,gate) {
  MerSettlementService.queryHlog(batch,gate, callSettlementTwoList);
}
//提取用户列表的回设函数：callSettlementTwoList中放的是FeeLiqLog对象
var callSettlementTwoList = function(SettlementTwoList) {
if (SettlementTwoList.length == 0) {
alert("没有符合条件的数据");
showDivTwo();
return false;
}
showDivThree();
dwr.util.removeAllRows("TableThree");
//取得html中表格对象
var dtable = document.getElementById("TableThree");
for (var i = 0; i < SettlementTwoList.length; i++) {

   theHlog = SettlementTwoList[i];
   // 在table中新建一行
   var elTr = dtable.insertRow(-1);
   // 商户号
   var midTd = elTr.insertCell(-1);
   midTd.innerHTML = theHlog.mid;
   // 商户简称
   var abbrevTd = elTr.insertCell(-1);
   abbrevTd.innerHTML = m_minfos[theHlog.mid];
   // 商户交易日期
   var batchTd = elTr.insertCell(-1);
   batchTd.innerHTML = theHlog.mdate;
   // 订单号
   var purAmtTd = elTr.insertCell(-1);
   purAmtTd.innerHTML = theHlog.oid;
   // 交易金额
   var refAmtTd = elTr.insertCell(-1);
   refAmtTd.innerHTML = theHlog.amount/100;
   // 手续费
   var feeAmtTd = elTr.insertCell(-1);
   feeAmtTd.innerHTML = theHlog.feeAmt/100;
   // 交易类型
   var typeTd = elTr.insertCell(-1);
   typeTd.innerHTML = h_settle_type[theHlog.type];
   // 系统日期
   var sysDateTd = elTr.insertCell(-1);
   sysDateTd.innerHTML = theHlog.sysDate;
   // 交易流水号
   var tseqTd = elTr.insertCell(-1);
   tseqTd.innerHTML = theHlog.tseq;
   // 银行
   var gateTd = elTr.insertCell(-1);
   gateTd.innerHTML = h_gate[theHlog.gate];
}
var pageTable = document.getElementById("TableThree");
var actionTr = pageTable.insertRow(-1);
var bTd = actionTr.insertCell(-1);
 bTd.setAttribute("colSpan","10");
 actionTr.setAttribute("role","bottom");
bTd.innerHTML = "<input type='button' class='button' value='返回' onclick=\"javascript:showDivTwo()\">";
bTd.style.textAlign = "center";
};
//查看明细
function queryDetail(formName,action){
  var f = document.forms[formName];
  var k;
  var batch = document.getElementById("radio_batch").value;
  var gate = document.getElementById("radio_gate").value;
  for(var i=0;i<f.elements.length;i++)  {  
      if(f.elements[i].id=='select_bid')  {  
           k = batch;
       }  
       if(f.elements[i].id=='select_gid')  {  
           k = gate;
       }
  }
  if(k == 'nodata'){
      alert("请选择要查看的记录！");
      return false;
  }else{
     if(action == 'qlog'){
         queryLiqFeeLog(batch);
     }
     if(action == 'qhlog'){
         queryHlog(batch,gate);
     }
  }
}
//下载
function downLoadXLS(formName,mid,bdate,edate,action){ 
	var f = document.forms[formName];
  var k;
  var batch = document.getElementById("radio_batch").value;
  var gate = document.getElementById("radio_gate").value;
  for(var i=0;i<f.elements.length;i++)  {  
      if(f.elements[i].id=='select_bid')  {  
           k = batch;
       }  
       if(f.elements[i].id=='select_gid')  {  
           k = gate;
       }
  }
  
  var p={};
  p.a=action;
  p.mid=mid;
  p.bdate=bdate;
  p.edate=edate;
  p.b=batch;
  p.g=gate;
  
  if(k == 'nodata'){
      alert("请选择要下载的记录！");
      return false;
	}else{
		dwr.engine.setAsync(false);
		MerSettlementService.downloadSettleDetail(p,function(data){dwr.engine.openInDownload(data);})
	}
}