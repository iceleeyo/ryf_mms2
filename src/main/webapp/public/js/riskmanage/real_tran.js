


function showTheTime(){
    now = new Date;
    var t = " 今天 "+now.getFullYear()+ "-"+(parseInt(now.getMonth())+1) + "-"+now.getDate()+" " + now.getHours() + ":" + showZeroFilled(now.getMinutes()) + ":" + showZeroFilled(now.getSeconds());
    document.getElementById("showTime").innerHTML = t;
    setTimeout("showTheTime()",1000);
}
var failTransCount=-1;
function initTable(){
	showTheTime();
	initGateChannel();
	showRealTrans();
    setInterval(showRealTrans,6000);
}
function showRealTrans(){
	  RiskmanageService.queryRealTran(function(realTran){
	  
		  if(realTran.warMsg!=null&&realTran.warMsg.length>0){
		      document.getElementById("warMsg").innerHTML=realTran.warMsg;
		  }
	       var successCellFuns=[
	       					function(obj){return obj.tseq},
	       					function(obj){return obj.mid},
	       					function(obj){return obj.oid},
	       					function(obj){return obj.sysDate+" "+getStringTime(obj.sysTime)},
	       					function(obj){return div100(obj.amount)}
	       					];
	       var failCellFuns=[
	      				    function(obj){return obj.tseq},
	       					function(obj){return obj.mid},
	       					function(obj){return obj.oid},
	       					function(obj){return obj.sysDate+" "+getStringTime(obj.sysTime)},
	       					function(obj){return div100(obj.amount)},
	       					function(obj){return obj.gate+"/"+h_gate[obj.gate]},
	       					function(obj){return obj.errorCode}
	      				   ];
	       /*
	       var rowCreator=function(options) { //自定义 tr 的创建行为　　 
	　　　　　　　　　　  var row = document.createElement("tr");　
	 					var  spanHour = parseInt((getNowTime()-options.rowData.sysTime % 86400) / 3600 );//时间跨度
	 					if(options.rowData.sysDate==jsToday()&&spanHour<=1){
	 						document.getElementById("warMsg").innerHTML="1小时内有新的失败交易";
	 						row.style.backgroundColor ="silver";
	 					}
	　　　　　　　　　　　 return row;　　　 
	　　　　 };*/
	      	document.getElementById("successCount").innerHTML=realTran.lastSuccesTranList.length;
	      	document.getElementById("failCount").innerHTML=realTran.failTranList.length;
	      	dwr.util.removeAllRows("successTransList");
	      	dwr.util.removeAllRows("failTransList");
	        dwr.util.addRows("successTransList", realTran.lastSuccesTranList, successCellFuns);
	        dwr.util.addRows("failTransList", realTran.failTranList, failCellFuns);
       
	   });			
}
function showZeroFilled(inValue){
    if (inValue > 9) {
        return "" + inValue;
    }
    return "0" + inValue;
}
function getNowTime(){
	var date =new Date();
	var nowTime=date.getHours()*3600+date.getMinutes()*60+date.getSeconds();
	return nowTime;
}