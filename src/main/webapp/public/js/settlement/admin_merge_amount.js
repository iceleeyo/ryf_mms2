function initGateRoute(){
	PageService.getGateRouteMap(function(gateRouteMap){
		var gateRouteStr="";
		var count=0;
		   for(var gid in gateRouteMap){
			   gateRouteStr+="&nbsp;<input type='checkbox' name='bank_id'  value='"+gid+"' >"+gateRouteMap[gid]+"&nbsp;&nbsp;";
		       if(count==12)gateRouteStr+="<br/>"
			   count++;
		   }
	  $("gateRoutes").innerHTML=gateRouteStr;
	});
}

function initGateRouteOptions(){
	PageService.getGateRouteMap(function(gateRouteMap){
		  dwr.util.addOptions("gateRoutes",gateRouteMap);
	});
	initMinfos();
}

function checkBank(flag) {
	 var gateRoutArr=getValuesByName("bank_id");
	 var bdate=$("bdate").value;
	 var edate=$("edate").value;
	 if(gateRoutArr.length==0){
		 alert("请选择银行！");
		 return false;
	 }
	 if(bdate==""||edate==""){
		 alert("请选择起始日期！");
		 return false;
	 }

	if (flag == -1) {
		dwr.engine.setAsync(false);// 把ajax调用设置为同步
		SettlementService.downloadBankData(gateRoutArr, bdate, edate, function(data) {
			dwr.engine.openInDownload(data);
		})
	}else{
		SettlementService.getMergeAmountList(gateRoutArr, bdate, edate, callback);
	}

	function callback(mergeDetailList) {
		
		if (mergeDetailList == null) {
			//alert("");
			return;
		}
		
		dwr.util.removeAllRows("mergeAmountBody");
		document.getElementById("mergeAmountDetail").style.display = "";
		
		var totalAmount = 0;
		var totalrefAmt = 0;
		var totalBankFee = 0;
		var countBankAmt = 0;
		var totalBkFee = 0;
		var totalBkFeeReal = 0;
		var cellFuncs = [// 银行, 支付金额, 退款金额, 银行手续费, 银行划款额
				function(obj) {
					return obj.bkName;
				},
				function(obj) {
					totalAmount += Number(div100(obj.payAmt));
					return div100(obj.payAmt);
				},
				function(obj) {
					totalrefAmt += Number(div100(obj.refAmt));
					return div100(obj.refAmt);
				},
				function(obj) {
					totalBankFee += Number(div100(obj.bankFee));
					return div100(obj.bankFee);
				},
				function(obj) {
					totalBkFeeReal += Number(div100(obj.bkFee));
					return div100(obj.bkFee);
				},
				function(obj) {
					totalBkFee += Number(div100(obj.bkFeeReal));
					return div100(obj.bkFeeReal);
				},
				function(obj) {
					countBankAmt += Number(div100(obj.payAmt - obj.refAmt
							- obj.bankFee + obj.bkFeeReal));
					return div100(obj.payAmt - obj.refAmt - obj.bankFee
							+ obj.bkFeeReal);
				} ]
				
		   dwr.util.addRows("mergeAmountBody",mergeDetailList,cellFuncs,null);
		  creatTotleTr(totalAmount, totalrefAmt, totalBankFee, countBankAmt,
				totalBkFee, totalBkFeeReal);
	
	}
	function creatTotleTr(totalAmount, totalrefAmt, totalBankFee, countBankAmt,
			totalBkFee, totalBkFeeReal) {
		var trElement = document.createElement("tr");
		var tdElement = document.createElement("td");
		tdElement.colSpan = 7;
		tdElement.setAttribute("style", "color: blue");
		tdElement.innerHTML = " &nbsp;总计：支付金额 " + totalAmount.toFixed(2)
				+ " 元，退款金额 " + totalrefAmt.toFixed(2) + " 元，银行手续费 "
				+ totalBankFee.toFixed(2) + " 元，应收银行退回手续费 "
				+ totalBkFeeReal.toFixed(2) + " 元 ，实收银行退回手续费"
				+ totalBkFee.toFixed(2) + "元，银行划款额" + countBankAmt.toFixed(2)
				+ "元。";
		trElement.appendChild(tdElement);
		trElement.setAttribute("role", "bottom");
		document.getElementById("mergeAmountBody").appendChild(trElement);
	}

}
//============================================================================================

function checkGate(t) {

	var gateRouteId = document.getElementById("gateRoutes").value;

	if (gateRouteId == null || gateRouteId == '') {
		alert("请选择银行");
		return false;
	}
	var bkName = dwr.util.getText("gateRoutes");
	var bdate = document.getElementById("bdate").value;
	var edate = document.getElementById("edate").value;
	if (bdate == ''||edate == '') {
		alert("请选择起始日期");
		return false;
	}
	if (t == -1) {
		dwr.engine.setAsync(false);// 把ajax调用设置为同步
		SettlementService.downloadBankDataToMer(gateRouteId, bkName,bdate, edate, function(data) {
			dwr.engine.openInDownload(data);
		})
	}else{
		SettlementService.getMergeAmountListToMer(gateRouteId, bkName,bdate, edate, callback);
	}

	function callback(mergeDetailList) {
		dwr.util.removeAllRows("mergeAmountBody");
		document.getElementById("mergeAmountDetail").style.display = "";
		if (mergeDetailList == null) {
			document.getElementById("mergeAmountBody").appendChild(
					creatNoRecordTr(8));
			return;
		}
		var totalAmount = 0;
		var totalrefAmt = 0;
		var totalBankFee = 0;
		var countBankAmt = 0;
		var totalBkFeeReal = 0;
		var cellFuncs = [// 银行, 商户号, 商户简称, 支付金额, 退款金额, 银行手续费, 银行划款额 ,银行退回的手续费
				function(obj) {
					//return h_merge_bk[obj.bankType];
					return obj.bkName;
				},
				function(obj) {
					return obj.mid;
				},
				function(obj) {
					//return obj.abbrev;
					return m_minfos[obj.mid];
				},
				function(obj) {
					totalAmount += Number(div100(obj.payAmt));
					return div100(obj.payAmt);
				},
				function(obj) {
					totalrefAmt += Number(div100(obj.refAmt));
					return div100(obj.refAmt);
				},
				function(obj) {
					totalBankFee += Number(div100(obj.bankFee));
					return div100(obj.bankFee);
				},
				function(obj) {
					totalBkFeeReal += Number(div100(obj.bkFeeReal));
					return div100(obj.bkFeeReal);
				},
				function(obj) {
					countBankAmt += Number(div100(obj.payAmt + obj.bkFeeReal
							- obj.refAmt - obj.bankFee));
					return div100(obj.payAmt + obj.bkFeeReal - obj.refAmt
							- obj.bankFee);
				} ]
		var bankArr = new Array();

		dwr.util.addRows("mergeAmountBody", mergeDetailList, cellFuncs);
		creatTotleTr(totalAmount, totalrefAmt, totalBankFee, countBankAmt,
				totalBkFeeReal);
	}

	function creatTotleTr(totalAmount, totalrefAmt, totalBankFee, countBankAmt,
			totalBkFeeReal) {
		var trElement = document.createElement("tr");
		var tdElement = document.createElement("td");
		tdElement.colSpan = 8;
		tdElement.setAttribute("style", "color: blue");
		tdElement.innerHTML = " &nbsp;总计：支付金额 " + totalAmount.toFixed(2)
				+ " 元，退款金额 " + totalrefAmt.toFixed(2) + " 元，银行手续费 "
				+ totalBankFee.toFixed(2) + " 元 ，银行退回手续费"
				+ totalBkFeeReal.toFixed(2) + " 元，银行划款额"
				+ countBankAmt.toFixed(2) + "元。";
		trElement.appendChild(tdElement);
		trElement.setAttribute("role", "bottom");
		document.getElementById("mergeAmountBody").appendChild(trElement);
	}

}