function init(){
    dwr.util.addOptions("tstat", {0:"成功",1:"失败",2:"待处理",'':"全部..."});
    dwr.util.addOptions("type", pos_type);
}


function onlyNu(o){
	if(!isNumber(o.value)){
		o.value="";
	}
}
 // 查询
 function queryMerToday(pageNo){
	 var mid = $("mid").value;
	 var oid = $("oid").value;
	// var bkseq = $("bkseq").value;
	 var tstat = $("tstat").value;
	// var bankCardNo = $("bankCardNo").value;
	 //var tseq = $("tseq").value;
	 var type=$("type").value;//交易类型
	 var begintrantAmt=$("begintrantAmt").value;//开始金额
	 var endtrantAmt=$("endtrantAmt").value;//结束金额
	 var innerTermId=$("innerTermId").value;//电银终端号
	 var terminalNumber=$("terminalNumber").value;//终端号
	 var beginDate=$("beginDate").value;
	 var endDate=$("endDate").value;
	 //pageNo,pageSize,mid,tseq,bkseq,tstat,oid,bankCardNo
	// QueryPosMerTodayService.queryPosMerToday(pageNo,15,mid,tseq,bkseq,tstat,oid,bankCardNo,callBack2);
	 if (begintrantAmt !=    "") {
		   	begintrantAmt = begintrantAmt.trim();
		       if (!isPlusInteger(begintrantAmt)) {
		           alert("金额必须是正整数!");
		           $("begintrantAmt").value = '';
		           $("begintrantAmt").focus();
		           return false;
		        }
		   }
		   if (endtrantAmt !=    "") {
		   	endtrantAmt = endtrantAmt.trim();
		       if (!isPlusInteger(endtrantAmt)) {
		           alert("金额必须是正整数!");
		           $("endtrantAmt").value = '';
		           $("endtrantAmt").focus();
		           return false;
		        }
		   }
		   if(beginDate=='') {
               alert("请选择起始日期！"); 
               return false; 
           }
		   
           if(endDate==''){
                alert("请选择结束日期！"); 
                return false; 
           }
	 QueryPosMerTodayService.queryMerHlogs(pageNo,15,mid,oid,innerTermId,type,tstat,terminalNumber,begintrantAmt,endtrantAmt,beginDate,endDate,callBack2);	

 }
 //回调函数
 var callBack2 = function(pageObj){
	  $("merTodayTable").style.display="";
	   dwr.util.removeAllRows("resultList");
	   if(pageObj==null){document.getElementById("resultList").appendChild(creatNoRecordTr(17));return;}
	   var cellFuncs = [
                function(obj) { return obj.tseq; },
                function(obj) { return obj.xpepMercode; },
                function(obj) { return obj.innerTermId;},
                //function(obj) { return obj.abbrev; 
                	
               // },
                function(obj) { return obj.oid; },
                function(obj) { return pos_type[obj.xpepTrademsgType]; },//交易类型xpepTrademsgType
                function(obj) { return div100(obj.amount); },
                function(obj) { 
                	if(obj.xpepTradeResp=="00") {
               		 return "成功";
               	 } else if (obj.xpepTradeResp=="A6" || obj.xpepTradeResp=="N1") {
               		 return "待处理";
               	 } else {
               		 return "失败";
               	 }
                },
               // function(obj) { return subBankNo(obj.xpepOutcard);},
                function(obj) { return obj.sysDate+" "+getStringTime(obj.sysTime);},
                function(obj) { return obj.xpeDeductTrace == null ? addNumber(obj.xpepTrace):obj.xpeDeductTrace;},//电银流水号  xpeDeductTrace 
                function(obj) { return obj.terminalNumber!=null?obj.terminalNumber:obj.innerTermId;},
                //function(obj) { return obj.xpepTermId;},手机电话apn
                function(obj) { return fontString(obj.xpeDeductRefer);},//银联参考号    xpeDeductRefer
                function(obj) { return obj.bankFeeModel;},//系统手续费
                function(obj) { return obj.tradeName;}//描述
                //function(obj) { return obj.bankFeeModel;},
                //function(obj) { return obj.xpepAuthCode;},
                //function(obj) { return createTip(8,obj.errorCode);}
              ];
              str ="<span class='pageSum'>&nbsp;交易总金额：<font color='red'><b>" + div100(pageObj.sumResult["amtSum"])+"</b></font>  元 </span><span class='pageSum'>(成功：<font color='red'><b>" +div100(pageObj.sumResult["success_num"])+ "</b></font> 元，失败：<font color='red'><b>"+div100(pageObj.sumResult["fail_num"])+"</b></font> 元，待处理：<font color='red'><b>"+div100(pageObj.sumResult["no_manage_sum"])+"</b></font> 元)</span> <span class='pageSum'>&nbsp;&nbsp;系统手续费总金额：<font color='red'><b>"+ pageObj.sumResult["sysAmtFeeSum"]+"</b></font>  元</span>";
       paginationTable(pageObj,"resultList",cellFuncs,str,"queryMerToday");
   };
   
   function subBankNo(str) {
	   	if(null != str && ''!= str){
	   		var index = str.length-4;
	   		var strs1 = str.substring(0, 6);
	       	var strs = str.substring(index, str.length);
	       	return strs1 + "******" +strs;
	   	}
	   	return '';
	   }
	   
	   
	   
	   function addNumber(number){
		var b = "";
		var len = (number + "").length;
		for (var i = 0; i < 6-len; i ++) {
			b +="0";
		}
		b += number;
		return b;
	}


	   function fontString(str) {
	   	if(null != str && ''!= str){
	   		var index = str.length-(str.length-6);
	   		var strs1 = str.substring(0, index);
	       	var strs = str.substring(index, str.length);
	       	return strs1+"<font color='red'>"+strs+"</font>";
	   	}
	   	return '';
	   }
 