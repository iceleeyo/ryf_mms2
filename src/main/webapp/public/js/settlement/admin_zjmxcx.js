//资金明细查询
var gid,jd,isChk,bdate,edate;
function query(pageNo){
	gid = jQuery("#gateRoute").val();
	jd = jQuery("#jd").val();
	bdate = jQuery("#bDate").val();
	edate = jQuery("#eDate").val();
	if(bdate == ""||edate == ""){
		alert("请输入查询日期！");
		return;
	}
	TrDetailsService.query(pageNo,gid, jd, bdate, edate, callback);
}
function callback(pageObj){
    document.getElementById("listTable").style.display = '';
	var cellFuncs = [  
                     function(obj) {  
	                 	 	var dateStr = obj.trDate!=null?(obj.trDate+""):"";
	                 	 	if(dateStr&&dateStr.length!=8){
	                	 		dateStr = "00000000".substring(0,8-dateStr.length)+dateStr;
	                	 	}
	                	 	return dateStr?(dateStr.substring(0,4)+"-"+dateStr.substring(4,6)+"-"+dateStr.substring(6,8)):"";
                    	 },
                     function(obj) { 
                    	 	var dateStr = obj.trTime!=null?(obj.trTime+""):"";
                    	 	if(dateStr&&dateStr.length!=6){
                    	 		dateStr = "000000".substring(0,6-dateStr.length)+dateStr;
                    	 	}
                    	 	return dateStr?(dateStr.substring(0,2)+":"+dateStr.substring(2,4)+":"+dateStr.substring(4,6)):"";},									 
                     function(obj) { return div100(obj.rcvamt); },
                     function(obj) { return div100(obj.payamt); },
                     function(obj) { return div100(obj.feeAmt); }, 
                     function(obj) { return obj.oppAcno;}, 
                     function(obj) { return obj.oppAcname;}, 
                     function(obj) { return div100(obj.balance);},
                     function(obj) { return obj.bankName; },
                     function(obj) { return obj.summary;},
                     function(obj) { return obj.bkSerialNo;},  
                     function(obj) { return obj.postscript;}
            ];
	str="收入："+div100(pageObj.sumResult.rcvamt)+" 元	支出："+div100(pageObj.sumResult.payamt)+" 元		手续费总金额："+ div100(pageObj.sumResult.feeAmt)+" 元";
	paginationTable(pageObj,"resultList",cellFuncs,str,"query");
}

function download(){
	gid = jQuery("#gateRoute").val();
	jd = jQuery("#jd").val();
	bdate = jQuery("#bDate").val();
	edate = jQuery("#eDate").val();
	if(bdate == ""||edate == ""){
		alert("请输入查询日期！");
		return;
	}
    dwr.engine.setAsync(false);//把ajax调用设置为同步
    TrDetailsService.download(gid, jd, bdate, edate,function(data){
    		dwr.engine.openInDownload(data);
	});
}