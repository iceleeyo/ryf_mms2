
var minfoMap = {};
RypCommon.getHashMer(function(map) {
    minfoMap = map;
});
var gatesMap = {};
RypCommon.getHashGate(function(m) {
    gatesMap = m;
});

function init(){
	PageService.getGateRouteMap(function(gateRouteMap){
		gate_route_map=gateRouteMap;
		dwr.util.addOptions("gateRoute", gate_route_map);
	});
}

function checkDate(){
    var date_begin = dwr.util.getValue("date_begin");
    var date_end = dwr.util.getValue("date_end");
    if(date_begin=='' || date_end==''){
        alert("请输入交易日期！");
        return false;
    }
    return true;
}

function downloadResult(){
	var p={};
    p.date_begin = dwr.util.getValue("date_begin");
    p.date_end = dwr.util.getValue("date_end");
    p.error_type = dwr.util.getValue("error_type");
    p.check_date = dwr.util.getValue("check_date");
    if(p.date_begin=='' || p.date_end==''){
        alert("请输入交易日期！");
        return false;
    }
    dwr.engine.setAsync(false);//把ajax调用设置为同步
    SettlementService.downloadResult(p,function(data){dwr.engine.openInDownload(data);})
}
var currPage=1;
    // 根据条件查询(第一次查询)
function queryBaseSettleResult(pageNo) {
    var page = 1;
    var date_begin = dwr.util.getValue("date_begin");
    var date_end = dwr.util.getValue("date_end");
    var error_type = dwr.util.getValue("error_type");
    var check_date = dwr.util.getValue("check_date");
    var gate = dwr.util.getValue("gateRoute");
    if(date_begin=='' || date_end==''){
        alert("请输入交易日期！");
        return false;
    }
    currPage=pageNo;
    SettlementService.searchSettleResult(pageNo, date_begin, date_end,error_type,check_date,gate,callBackList);
	 function callBackList(settleResultList) {
		    document.getElementById("settleResultList").style.display = '';
		    	var cellFuncs = [  
		    	                 function(obj) {
			                         if(obj.state == 0){
			                             return "<input type=\"checkbox\"  name=\"toCheck\" id=\"toCheck\" value=\""+ obj.id +"\">";
								      }else{
								           return "";
								      }
			                      },
			                     function(obj) { return obj.mid == 0 ?"":obj.mid; },
			                     function(obj) { return obj.mid == null ?"":minfoMap[obj.mid]; },									 
			                     function(obj) { return obj.tseq == null ?"":obj.tseq; },
			                     function(obj) { return obj.payDate; },
			                     function(obj) { return obj.mid == null ?"":div100(obj.amount); }, 
			                     function(obj) { return obj.gate == null ?"":gatesMap[obj.gate];}, 
			                     function(obj) { return obj.bkMerOid== null ?"":obj.bkMerOid;}, 
			                     function(obj) { return div100(obj.bkAmount);},
			                     function(obj) { return obj.errorType == 0 ? "失败交易":"可疑交易"; },
			                     function(obj) { return obj.state == 0 ? "未处理" : "已处理";},
			                     function(obj) { return obj.checkDate==0?"":obj.checkDate;},  
			                     function(obj) { return obj.solveRemark;} , 
			                     function(obj) { 
			                    	 var  gate = obj.gate;
			                    	 var gateName = gate_route_map[gate];
			                    	 return gate_route_map[obj.gate];
			                    	 } 
			            ];
				str="<span  style='float:left'>&nbsp;&nbsp;&nbsp;<input type='button' id='chooseAll' name='chooseAll' value='全 选' onclick=\"chooseAllRefund();\">&nbsp;&nbsp;&nbsp;"
		         +"处理说明：&nbsp;<select id='remark' name='remark'><option value='-1'>请选择…</option><option value='全额退款处理'>全额退款处理</option><option value='手工掉单提交处理'>手工掉单提交处理</option><option value='调账处理'>调账处理</option></select><font color='red'>*</font>&nbsp;&nbsp;&nbsp;"
		         +"<input type='button' id='confirm' name='confirm' value='确认处理' onclick=\"confirmSolve();\"></span>";
		   paginationTable(settleResultList,"settleResultTable",cellFuncs,str,"queryBaseSettleResult");
	}
}
//全选
var choosetype = 'true';
function chooseAllRefund() {
  var f = document.forms["checkForm"];
  if (choosetype == 'true'){
      for (i=0;i<f.elements.length;i++){
          if(f.elements[i].id=="toCheck"){
           f.elements[i].checked = true;
          }
      }
   }else if (choosetype == 'false'){
      for (i=0;i<f.elements.length;i++){
          if(f.elements[i].id=="toCheck"){
           f.elements[i].checked = false;
          }
      }
   }
   if (choosetype == 'true'){
         choosetype = 'false';
   } else {
         choosetype = 'true';
   }
 }
 //确认处理
 function confirmSolve(){
    var checkboxSelete = "Nothing"
    var seleteId = "";
    if (document.forms["checkForm"].length == 11) {
        if (document.forms["checkForm"].toCheck.checked) {
            checkboxSelete = "seleted";
            var checkvalue = document.forms["checkForm"].toCheck.value;
            seleteId += checkvalue + "&";
        }
    } else {
        for (i = 0; i < document.forms["checkForm"].toCheck.length; i++) {
            if (document.forms["checkForm"].toCheck[i].checked) {
                checkboxSelete = "seleted";
                var checkvalue = document.forms["checkForm"].toCheck[i].value;
                seleteId += checkvalue + "&";
            }
        }
    }
    if (checkboxSelete == "Nothing") {
        alert("请至少选择一条记录！");
        return false;
    }
    var remark = document.getElementById("remark").value;
    if(remark == '-1'){
        alert("请选择处理说明！");
        return false;
    }
    var loginmid = document.getElementById("loginmid").value;
    var loginuid = document.getElementById("loginuid").value;
    if (confirm("确认处理吗?")) {
        SettlementService.confirmSolve(loginmid, loginuid, remark,seleteId,callbackConfirm);
    }
 }
 function callbackConfirm(msg){
    alert(msg);
    queryBaseSettleResult(currPage);    
 }