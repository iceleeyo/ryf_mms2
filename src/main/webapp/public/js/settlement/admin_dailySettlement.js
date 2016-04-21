function init(){
	initMinfos();
	dwr.util.addOptions("mstate", m_mstate);
}
//根据条件查询(第一次查�?
function queryBaseDailySettlement(pageNo) {
 var mid = document.getElementById("mid").value.trim();
 var date_b = document.getElementById("date_begin").value.trim();
 var date_e = document.getElementById("date_end").value.trim();
 var mstate=document.getElementById("mstate").value.trim();
 
 if (mid != '' && !isFigure(mid)){
	 alert("商户号只能是整数!");
     return false;
 }
 if (date_b == ''){
     alert("请选择查询起始日期!");
     return false;
 }   
 if (date_e==''){
     alert("请选择查询结束日期!");
     return false;
 }
  SettlementService.getDailySheet(pageNo, mid, date_b, date_e,mstate, callBackDailySettlementList);
  //提取用户列表的回设函数：balanceList中放的是minfo对象
   function callBackDailySettlementList(DailySettlementList){
	   document.getElementById("DailySettlementList").style.display = '';
 		    var liqAmt = 0;
            var incomeAmt=0; 
 		    var cellFuncs = [
 		    				 function(DailySheet) { return DailySheet.liqDate;},
		                     function(DailySheet) { return DailySheet.mid;},
		                     function(DailySheet) { return DailySheet.abbrev; },
		                     function(DailySheet) { return div100(DailySheet.netPay); },
		                     function(DailySheet) { return div100(DailySheet.wapPay); },
		                     function(DailySheet) { return div100(DailySheet.creditcardPay); },
		                     function(DailySheet) { return div100(DailySheet.debitcardPay); },
		                     function(DailySheet) { return div100(DailySheet.btobPay);},
		                     function(DailySheet) { return div100(DailySheet.manualAdd); },
		                     function(DailySheet) { return div100(DailySheet.refund); },
		                     function(DailySheet) { return div100(DailySheet.manualSub); },
		                     function(DailySheet) { return div100(DailySheet.feeAmt); },
		                     function(DailySheet) { return  div100(DailySheet.refFee); },
		                     function(DailySheet) { 
		                    //计算结算金额，全额不需要减手续�?
							         //if(DailySheet.liqType == 1){
							           //  liqAmt = DailySheet.debitcardPay + DailySheet.netPay + DailySheet.wapPay + DailySheet.creditcardPay + DailySheet.btobPay + DailySheet.manualAdd - DailySheet.refund - DailySheet.manualSub;
							           //  incomeAmt = -1*DailySheet.bankFee;
							         //}else{
//							             liqAmt = DailySheet.debitcardPay + DailySheet.netPay + DailySheet.wapPay + DailySheet.creditcardPay + DailySheet.btobPay + DailySheet.manualAdd - DailySheet.refund - DailySheet.manualSub - DailySheet.feeAmt+DailySheet.refFee;
//							             incomeAmt = DailySheet.feeAmt - DailySheet.bankFee-DailySheet.refFee+DailySheet.bkRefFee;
//							         }
		                    	 
		                    	 
		                    	 liqAmt = DailySheet.debitcardPay + DailySheet.netPay + DailySheet.wapPay + DailySheet.creditcardPay + DailySheet.btobPay + DailySheet.manualAdd - DailySheet.refund - DailySheet.manualSub - DailySheet.feeAmt+DailySheet.refFee;
					             incomeAmt = DailySheet.feeAmt - DailySheet.bankFee-DailySheet.refFee+DailySheet.bkRefFee;
							         //}
		                    	 
		                    	 
		                       return  div100(liqAmt); 
		                     },
		                      function(DailySheet) { return  div100(DailySheet.bankFee); },
		                      function(DailySheet) { return  div100(DailySheet.bkRefFee); },
		                      function(DailySheet) { return  div100(incomeAmt); }
		                 ]

		    var pageUrl = "&mid=" + mid + "&date_begin=" + date_b + "&date_end=" + date_e;
 			var str = "<span  style='float:left'>&nbsp;&nbsp;&nbsp;<input type='button' name='download' id='download' value=' 导出Excel ' onClick=\"downLoadDailySettlement()\"></span>";
		      paginationTable(DailySettlementList,"DailySettlementTable",cellFuncs,str,"queryBaseDailySettlement");
   }
}
function downLoadDailySettlement(){
	//"结算日期, 商户�? 商户简�?,网上支付 ,WAP支付 ,信用卡支�?,消费充�?卡支�?,语音支付,调增 ,退�?,调减 ,系统手续�?结算金额,银行手续�?收益金额 ";
	 var mid = document.getElementById("mid").value.trim();
	 var date_b = document.getElementById("date_begin").value.trim();
	 var date_e = document.getElementById("date_end").value.trim();
	 
	 if (mid != '' && !isFigure(mid)){
		 alert("商户号只能是整数!");
	     return false;
	 }
	 if (date_b == ''){
	     alert("请选择查询起始日期!");
	     return false;
	 }   
	 if (date_e==''){
	     alert("请选择查询结束日期!");
	     return false;
	 }
     var p={};
	    p.mid=mid;
	    p.date_begin=date_b;
	    p.date_end=date_e;
	dwr.engine.setAsync(false);//把ajax调用设置为同�?
	SettlementService.downloadSettleDaily(p,function(data){dwr.engine.openInDownload(data);})
}