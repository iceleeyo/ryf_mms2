function init(){
	
      $("beginDate").value = jsToday();
      $("endDate").value = jsToday();
}

function checkdata(date_b, date_e) {
	
	if (!judgeDate(date_b, date_e)) {
		alert("开始日期应先于结束日期");
		return false;
	}
	
	return true;
}
var query_cash={};
function query(p){ 
	
	var date_b = document.getElementById("beginDate").value;
	var date_e = document.getElementById("endDate").value;
	var name = document.getElementById("name").value;
	//var state  = document.getElementById("state").value;
	var mid = document.getElementById("mid").value;
	var syncState=document.getElementById("syncState").value;
	
	 if(mid !='' && !isFigure(mid)){
			alert("商户号只能是整数!");
			$("mid").value = '';
			return false; 
 	 }
	
	
	if (checkdata(date_b,date_e)) {
		SettlementNoticeService.getNoticeSyncs(p, date_b, date_e, mid,  name,syncState,resultData);
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
		                 function(obj){
		                	 count++;
				        	   gateRouteCache[count]=obj;
				        	   return "<input type='checkbox' onclick='' name='gateRouteCount' value='"+ count +"' />";
				        	   
				           },
		                 function(obj) { return obj.mid; },
		                 function(obj) { return  obj.name; },
		                 function(obj) { return obj.liqDate; },
		                 function(obj) { return obj.batch; },
		                 function(obj) { 
		                	 if(obj.syncState==0){
		                		 return "成功"; 
		                	 }
		                	 else if(obj.syncState==1){
		                		 return "失败"; 
		                	 }
		                	 else if(obj.syncState==2){
		                		 return "处理中"; 
		                	 }
		                	},//结算同步状态	
		                function(obj) { return obj.reason; }
		                 
		              ];
	
		 var str="<span  style='float:left'><input type=\"button\" id=\"chooseAll\" name=\"chooseAll\" value=\"全 选\" onclick=\"checkAll('gateRouteCount');\" />" +
		 		"<input type='button' onclick='checkChx()' value='结算单同步' id='commitButton' /></span>";		
		
		paginationTable(flbPage,"bodyTable",cellFuncs,str,"query"); 
		
	}else {
		document.getElementById("bodyTable").appendChild(creatNoRecordTr(7));
	}
};



function checkChx() {
	 var refLogArray = new Array();
	 var chkArray = document.getElementsByName("gateRouteCount");//获取被选中的列
	 var index = 0;
	 for (var i = 0 ; i < chkArray.length; i++){
		    if(chkArray[i].checked){
		      var MinfoNoticeSync = gateRouteCache[chkArray[i].value];
		         refLogArray[index] = MinfoNoticeSync;
		         index ++;
		    	}
		    }
			  if (index == 0){
			      alert("请至少选中一条记录!");
			      return false;  
			  }
			 
	if(!confirm("确认同步？"))return false;
	SettlementNoticeService.noticesSync(refLogArray,callback);
}

//更新列表
function callback(msg) {
    if (msg != undefined) {
        alert(msg);
    }
    query(1);
}




