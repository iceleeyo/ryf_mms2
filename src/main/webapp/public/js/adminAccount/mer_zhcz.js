var account_map = {};
var gate_map = {};
function init() {
	var mid = $("mid").value;
	MerZHService.getSettlementAcc(mid,function(accName){
		if(accName==null||accName==undefined){
			return ;
		}
//		$("aid").value=accName.substring(0,accName.indexOf("["));
		$("aid").innerHTML=accName;
//		$("aids").value=accName.substring(0,accName.indexOf("["));
		$("aids").innerHTML=accName;
	});
//	MerZHService.getZHUid2(mid, function(data) {
//		account_map = data;
//		dwr.util.addOptions("aid", data);
//		dwr.util.addOptions("aids", data);
//		if($("aid").length>1){
//			$("aid")[1].selected="selected";
//		}
//		if($("aids").length>1){
//			$("aids")[1].selected="selected";
//		}
//	});
	var gateMap=new Array();
//	gateMap[0]=20000;
	gateMap[0]=20001;
	addBankGates(gateMap, 1);
}

function confirmMsg() {
	var aid = $("aid").innerHTML;
	var payAmt = $("payAmt").value;
	var gate = getValuesByName("gate")[0];
	if (aid=="暂无账户"||aid == ""||aid==undefined) {
		alert("暂无账户，无法交易！");
		return;
	}
	if (!isMoney(payAmt)) {
		alert("请填写正确的充值金额！");
		return;
	}
	if (payAmt > 20000000) {// 2147483647
		alert("输入的金额不能超过两千万！");
		return;
	}
	if (!gate) {
		alert("请选择要充值的银行！");
		return;
	}
	dwr.engine.setAsync(false);
	MerZHService.getEncryptPayParam(payAmt, gate,
			function(data) {
				$("toBkAction").action = data.actionUrl;
				$("ordId").value = data.ordId;
				$("ordId_result").innerHTML = data.ordId;
				$("confirm_transFee").innerHTML = data.transFee;
				$("confirm_transAmt").innerHTML = data.transAmt;
				$("paramVal").value = data.p;
				$("keyVal").value = data.k;
				jQuery("#confirmPayMsgDiv").wBox( {
					title : "<img src='../../public/images/xitu.gif'>请确认你充值的信息",
					show : true
				});
			});

	$("confirm_name").innerHTML = $("mid").value;
	// /$("confirm_aid").innerHTML=aid;
	$("confirm_payAmt").innerHTML = payAmt;
	$("confirm_bk").innerHTML = "<img src='../../images/banklogo/" + gate+ ".png'/>";
}

function addBankGates(gateMap, columnNum) {
	var bankStr = "<ul>";
	var count = 0;
	for ( var i=0;i<gateMap.length;i++) {
		bankStr += "<li><input  type='radio' value='" + gateMap[i]
				+ "' name='gate'/>"
				+ "<img alt='银行'  src='../../images/banklogo/" + gateMap[i]
				+ ".png'/></li> ";
		if (count % columnNum == columnNum - 1) {
			bankStr += "</ul><ul>";
		}
		count++;
	}
	bankStr += "</ul>";
	$("bank_list").innerHTML = bankStr;
}

function queryOrder() {
	MerZHService.queryOrderState($("ordId").value, function(result) {
		$("stateStr").innerHTML = result;
		$("mainDiv").style.display = "none";
		$("orderResult").style.display = "";
	});
}
//提交订单
function submitOrder() {
	dwr.engine.setAsync(false);
	MerZHService.submitPayOrder($("ordId").value, function(data) {
		if (data == "ok") {
			$("toBkAction").submit();
			jQuery("#wBox_close").click();
			jQuery("#operMsgDiv").wBox( {
				title : "<img src='../../public/images/xitu.gif'>&nbsp;操作信息",
				show : true
			});
		} else {
			alert(data);
		}
	});
}
function payForOffline(){
	var aid = $("aids").innerHTML;//账户
	var payAmt = $("payAmts").value;//金额
	if (aid =="暂无账户" ||aid == "" ||aid==undefined) {
		alert("暂无账户，无法交易！");
		return;
	}
	if (!isMoney(payAmt)) {
		alert("请填写正确的充值金额！");
		return;
	}
	if (payAmt > 20000000) {// 2147483647
		alert("输入的金额不能超过两千万！");
		return;
	}
	MerZHService.offLineForAccount(payAmt,function(data){
		$("order_id").innerHTML = data.ordId;
		$("transFee").innerHTML = "0.00";
		$("tranPayAmt").innerHTML = data.tranPayAmt;
		$("paymoney").innerHTML = data.payAmt;
		$("confirm_names").innerHTML = $("mid").value;
		jQuery("#offlineconfirmMsgDiv").wBox( {
			title : "<img src='../../public/images/xitu.gif'>请确认你充值的信息",
			show : true});
	});
	}
	function submitForOffLine(){
		var aid = $("mid").value;//账户(结算帐户)
		var payAmt = $("paymoney").innerHTML ;//金额
		var orderId=$("order_id").innerHTML;
		MerZHService.payForAccount(aid,payAmt,orderId,function(msg){
			if(msg=="ok"){
//				alert("提交成功，请尽快付款！");
				$("offline").disabled="disabled";
				var targetStr="M_0_success.jsp?oid="+orderId+"&flag=2&uri=M_33_ZHCZ.jsp";
				location.href=targetStr;
				return;
			}
			alert(msg);
	});
}
	function setChineseMoney(val,flag) {
		val=formatNumber(Number(val).toFixed(2));
		if (!isMoney(val)) {
			$("ChineseMoney").innerHTML = "";
			alert("请输入正确的金额！");
			return;
		}
		if(flag==1){
			$("balance_chineseAmt").innerHTML = atoc(val);
		}
		if(flag==2){
			$("offline_chineseAmt").innerHTML = atoc(val);
		}
	}