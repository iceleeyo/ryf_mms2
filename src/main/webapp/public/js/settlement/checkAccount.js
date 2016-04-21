jQuery.noConflict();
(function($) { 
	 $(function(){ 
	     $("#usual1 ul").idTabs();
	     $("#usual1 ul a").click(function(){$("#merHlogTable").hide();}) //
	     $("#downLoadTxt").click(downLoadErrorData);
	     $("#downLoadExel").click(downLoadExel);
	 });				 
   function downLoadErrorData(){
  	  var text = "银行记录的订单号  银行交易金额 电银流水号 电银信息交易金额 状态\n\t";
  	  text+=$("#dataBody td").map(function(i){
  	    if(i%5==4)return $(this).text()+"\n";
  	    else return $(this).text();
  	  }).get().join("\t");
  	  dwr.engine.setAsync(false);//把ajax调用设置为同步
  	DownloadFileService.downloadTXTFile(text,jsToday()+"0"+Math.floor(Math.random()*1000)+"ErrorData.txt",
  	        function(data) {dwr.engine.openInDownload(data);}); 
   }
   function downLoadExel(){
     var dataArr=new Array();
     dataArr[0]="银行记录的订单号,银行交易金额,电银流水号 ,电银信息交易金额 ,状态";
     $("#dataBody tr").each(function(i){
        dataArr[i+1]=$(this).children("td").map(function(){
             return  $(this).text();
         }).get().join(",");
     });
     dwr.engine.setAsync(false);//把ajax调用设置为同步
  	DownloadFileService.downloadXLSFile(dataArr,jsToday()+"0"+Math.floor(Math.random()*1000)+"ErrorData.xls","对账失败或可疑数据",
  	        function(data) {dwr.engine.openInDownload(data);});
   } 
})(jQuery);
window.onload=function(){
 var gate_map={10001:"交行B2C(txt)",90000: "招商银行WAP(txt)",90001:"交通银行WAP(txt)",90002:"建设银行WAP(txt)",
	        90003:"工商银行WAP(txt)",90004:"中国银行WAP(txt)",1:"hf(txt)",2:"hx(csv)",3:"gy(txt)",4:"kq(csv)",5:"jh(txt)"};
	dwr.util.addOptions("settleBank",gate_map);
}
 function uploadFiles() {
	 var gateId = dwr.util.getValue('settleBank');
	 if(gateId==''){
	    alert('请选择要对账的银行');
	    return;
	 }
	 var filePath = document.getElementById('uploadFile').value;
	 if (filePath == '') {
	    alert("请选择对账文件!");
	    return ;
	 }
	 var f = filePath.toLocaleLowerCase();
	 if ((gateId==2||gateId==4) && (f.indexOf(".csv") == -1)) {
	    alert("请选择CSV文件!");
	    return;
	 }
	 if (gateId!=2&&gateId!=4&& (f.indexOf(".txt") == -1)) {
	    alert("请选择txt文件!");
	    return;
	 }
	 var file = dwr.util.getValue('uploadFile');
	 DoSettlementService.byUploadFileSettle(gateId,file,callback);
}

  var callback = function(obj){ 
       if(obj==null){alert("对账出现异常！请重试。");return;}
	   if(!obj.success){
	  		 alert(obj.errMsg) ;
	         return;
	   }
	   dwr.util.removeAllRows("dataBody");
	   document.getElementById("total").innerHTML=obj.total;
	   document.getElementById("success").innerHTML=obj.success.length;
	   document.getElementById("suspect").innerHTML=obj.suspect.length;
	   document.getElementById("fail").innerHTML=obj.fail.length;
	   document.getElementById("finish").innerHTML=obj.finish;
	   document.getElementById("exception").innerHTML=obj.exception;
	   cellfuncs1=[
	   		       function(failObj){return failObj.bkSeq;},
	       			function(failObj){return failObj.bkAmount;},
	       			function(failObj){return failObj.tseq;},
	       			function(failObj){return div100(failObj.amount);},
	       			function(failObj){return "<B>失败</B>";}
	   			  ]
	   cellfuncs2=[
	   		        function(suspectObj){return suspectObj.bkSeq;},
	       			function(suspectObj){return suspectObj.bkAmount;},
					function(suspectObj){return '';},
	       			function(suspectObj){return '';},
	       			function(suspectObj){return "可疑";}
	   			  ]
	   document.getElementById("merHlogTable").style.display="";
	   if(obj.suspect.length==0&&obj.fail.length==0){
	       document.getElementById("dataBody").appendChild(creatNoRecordTr(5));
	        return;
	   }
	   if(obj.fail.length!=0){
	   		dwr.util.addRows("dataBody", obj.fail, cellfuncs1,{ escapeHtml:false });
	   }
	   if(obj.suspect.length!=0){		             						
	     	dwr.util.addRows("dataBody", obj.suspect, cellfuncs2);
	   }
  }
	/******接口对账*****/
	
 function checkCMB(){
	var beginDate = document.getElementById("CMBbegin").value;
	var endDate = document.getElementById("CMBend").value;
	var operId = document.getElementById("operId").value;
	var pwd = document.getElementById("pwd").value;
	if(operId == ''){
		alert("操作员号不能为空!");
		return false;
	}
	if(pwd == ''){
		alert("操作员密码不能为空!");
		return false;
	}
	if (beginDate==""||endDate=="") {
	   alert("请选择起始日期和结束日期！");
		return false;
	}
	 DoSettlementService.byBKInterfaceSettle("CMB",{"operId":operId,"pwd":pwd,"beginDate":beginDate,"endDate":endDate},callback) ;
	
}
 		
function checkBkNo(){
 	DoSettlementService.checkBkNo(function(msg){alert(msg)});
}
 		
 		
 		