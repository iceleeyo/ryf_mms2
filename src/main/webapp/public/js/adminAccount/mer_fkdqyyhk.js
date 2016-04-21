var cus_name_map = {};
var prov_Map = {};
// window.onload =''
var paydata = {
	oid : null,
	aid : null,
	toAccNo : null,
	toAccName : null,
	toBkName : null,
	toBkNo : null,
	toProvId : null,
	cardFlag : null,
	payAmt : null,
	transAmt : null,
	transFee : null,
	priv : null,
	gid : null,
	gate : null,
	smsMoble : null,
	cusAid : null
};
function initAcc() {
	DfB2BSingleService.queryMerCustomerInfoList(function(paramList) {
		cus_name_map = paramList[0];
		dwr.util.addOptions("scusId", paramList[0]);
		prov_Map = paramList[1];
	});
	jQuery("#comBkMsg").wBox({
		show : true,
		opacity : 0,
		title : "<img src='../../public/images/xitu.gif'>对方企业银行账户"
	});
};
// 根据客户企业id获得开户行账号
function getUserBkAcc() {
	var cusId = $("scusId").value;
	DfB2BSingleService.queryBkAccByCusId(
					cusId,
					function(bkAccMap) {
						var bkAccStr = "";
						for ( var cc in bkAccMap) {
							bkAccStr += "<li onclick='this.firstChild.checked=true;'><input name='accName' type='radio' value='"
									+ cc + "'/>" + bkAccMap[cc] + "</li>";
						}
						$("bkAccOptions").innerHTML = bkAccStr;
					});
}
var orderId, sendPhoneNo;
function confirmMsg() {
	var aid = $("aid").value;
	var cusId = $("cusId").value;
	var accNo = $("accNo").value;
	var transAmt = $("payAmt").value; /* 交易金额 */
	// var gate=getValuesByName("gate")[0];
	var phoneNo = $("payToPhoneNo").value;
	var cusText = $("cusIdText").value;
	var remark = $("remark").value;
	var sf = $("to_prov_id").value;
	var yh = $("to_bk_no").value;
	var toOpenBkNo = $("to_openbk_no").value;
	var toOpenBkName = $("to_openbk_name").value;
	var bkname = $("to_bk_no").options[$("to_bk_no").selectedIndex].text;
	bkname = toOpenBkName;
	if (aid == "" || aid == undefined) {
		alert("请选择付款账户！");
		return;
	}
	if (!isMoney(transAmt)) {
		alert("请填写正确的付款金额！");
		return;
	}
	if (cusText == "") {
		alert("请输入对方企业名称！");
		return;
	}
	if (accNo == "") {
		alert("请输入付款帐号！");
		return;
	}
	if (yh == "") {
		alert("请选择收款银行！");
		return;
	}
	if (toOpenBkNo == '') {
		alert("请选择收款银行联行行号！");
		return false;
	}
	if (sf == "") {
		alert("请选择开户所在省份！");
		return;
	}
	if (transAmt > 20000000) {// 2147483647
		alert("输入的金额不能超过两千万！");
		return;
	}
	if (phoneNo != "") {
		if (!checkPhone(phoneNo)) {
			alert("请输入正确的手机号！");
			return;
		}
	} else {
		phoneNo = "";
	}
	var arr = new Array();
	if ($("ordId").value != "") {
		arr[0] = $("ordId").value;
	}
	dwr.engine.setAsync(false);
	DfB2BSingleService.checkSinglePay(aid, paydata.oid, transAmt, yh, function(data) {
		if(data.flag != "0"){
			alert(data.msg);
			return;
		}
		
		orderId = data.oid;
		sendPhoneNo = phoneNo;// 去网银支付的短信号码
		$("confirm_ordId").innerHTML = data.oid;// 订单号
		$("confirm_payAmt").innerHTML = Number(transAmt).toFixed(2);// 付款金额
		$("confirm_transFee").value = data.transFee;// 手续费
		$("confirm_transAmt").value = data.payAmt;// 应付金额
		$("confirm_transAmt_uppercase").value = atoc(data.payAmt);// 大写应付金额
		$("confirm_uid").innerHTML = cusId == "" ? "" : cusId;// 对方企业编号
		$("confirm_cusName").innerHTML = $("cusIdText").value;
		$("confirm_bkAcc").innerHTML = accNo;// 对方银行帐号
		$("confirm_myPhoneNo").innerHTML = phoneNo;// 手机号
		$("confirm_remark").innerHTML = remark;
		$("skyh").innerHTML = bkname;
		$("confirm_bk_no").innerHTML = toOpenBkNo;
		$("confirmPayMsgDiv").style.display = "";
		$("orderInputTable").style.display = "none";
		paydata.oid = orderId;
		paydata.gid = data.gid;
		paydata.aid = aid;
		paydata.transAmt = data.transAmt;
		paydata.payAmt = data.payAmt;
		paydata.transFee = data.transFee;
		paydata.toAccName = cusText;
		paydata.toAccNo = accNo;
		paydata.toBkNo = toOpenBkNo;
		paydata.toBkName = bkname;
		paydata.priv = remark;
		paydata.smsMobile = phoneNo;
		paydata.toProvId = sf;
		paydata.cusAid = cusId;
		paydata.gate = yh;
		
		var balance = data.balance;// 用户余额
		var tranAmt = ($("confirm_transAmt").value).replaceAll(",", "");
		var isok = balance - tranAmt;
		if (isok < 0 && $("balance").style.display != "none") {
			$("balance").disabled = "disabled";
		} else {
			$("balance").disabled = "";
		}
	});
}
// 返回修改
function backModify() {
	$("confirmPayMsgDiv").style.display = "none";
	$("orderInputTable").style.display = "";
	$("ordId").value = $("confirm_ordId").innerHTML;
}
function setChineseMoney(val) {
	val = Number(val).toFixed(2);
	if (!isMoney(val)) {
		$("ChineseMoney").innerHTML = "";
		alert("请输入正确的金额！");
		return;
	}
	$("ChineseMoney").innerHTML = atoc(val);
}
function queryOrder() {
	MerZHService.queryOrderState(orderId, function(result) {

		$("ordId_result").innerHTML = orderId;
		$("stateStr").innerHTML = result;
		$("mainDiv").style.display = "none";
		$("orderResult").style.display = "";
	});
}
// 提交支付后--返回支付页面
function backPayPage() {
	$("mainDiv").style.display = "";
	backModify();

}
function balance() {
	var mid = $("mid").value;
	var payamt = $("confirm_payAmt").innerHTML;
	var trfee = $("confirm_transFee").value;
	var amt = $("confirm_transAmt").value;
	var tramt = Number(amt.replaceAll(",", "")).toFixed(2);
	DfB2BSingleService.queryJSZHYE(mid, function(data) {
		var balance = div100(data.balance);
		$("accbalance").innerHTML = balance;
		$("amt").innerHTML = payamt;
		$("trfee").innerHTML = trfee;
		$("tramt").innerHTML = tramt;

	});
	jQuery("#paypwd").wBox({
		title : "<img src='../../public/images/xitu.gif'>请输入您的支付密码",
		show : true
	});
}
// 余额支付
function payForBalance() {
	var paypwd = $("pwdbak").value;// 支付密码
	if (paypwd == "") {
		alert("支付密码不能为空！");
		return;
	}
	if(!window.confirm("确认支付？"))
		return;
	
	var data = [ paydata.aid, paydata.oid, paydata.toAccNo, paydata.toAccName,
			paydata.toBkName, paydata.toBkNo, paydata.toProvId, paydata.payAmt,
			paydata.transAmt, paydata.transFee, paydata.priv, paydata.gid,
			paydata.gate, paydata.smsMobile, paydata.cusAid ];

	DfB2BSingleService.updateSinglePay(data, hex_md5(paypwd), function(ret) {
		var flag = ret.split("\|")[0];
		var msg = ret.split("\|")[1];
		if (flag == "0") {
			$("balance").disabled = "disabled";
			alert(msg);
			location.href = "M_43_FKDQYYHK.jsp";
			return;
		}
		alert(msg);
	});

}

function offlineConfimBtn() {
	var mid = $("mid").value;
	var payamt = $("confirm_payAmt").innerHTML;
	$("offamt").innerHTML = payamt;
	$("offtrfee").innerHTML = $("confirm_transFee").value;
	var amt = $("confirm_transAmt").value;
	$("offtramt").innerHTML = formatNumber(Number(amt.replaceAll(",", ""))
			.toFixed(2));
	MerZHService.queryJSZHYE(mid, function(data) {
		var balance = div100(data[0].balance);
		$("offaccbalance").innerHTML = balance;
	});
	jQuery("#offlinepay").wBox({
		title : "<img src='../../public/images/xitu.gif'>请确认您的支付信息",
		show : true
	});
}

// 单笔到个人银行 线下充值提交
function offline() {
	var amt = $("confirm_transFee").value;
	var fee = amt.replaceAll(",", "");
	var oid = $("confirm_ordId").innerHTML;
	MerZHService.payForOffline(oid, fee, function(msg) {
		if (msg == "ok") {
			$("balance").disabled = "disabled";
			$("lineout").disabled = "disabled";
			var targetStr = "M_0_success.jsp?oid=" + oid
					+ "&flag=2&uri=M_43_FKDQYYHK.jsp";
			location.href = targetStr;
			// alert("操作成功，请尽快付款！");
			return;
		}
		alert(msg);
	});

}
String.prototype.replaceAll = function(s1, s2) {
	return this.replace(new RegExp(s1, "gm"), s2);
};
function selectAcc() {
	var accId = getValuesByName("accName")[0];
	$("accNo").value = (accId == undefined ? "" : accId);
	$("cusId").value = $("scusId").value;
	$("cusIdText").value = $("scusId").options[$("scusId").selectedIndex].text;
	// alert($("scusId").options($("scusId").selectedIndex).text);
}
function initAid() {
	var mid = $("mid").value;
	DfB2BSingleService.getZHUid(mid, function(zh) {
		dwr.util.addOptions("aid", zh);
	});
	PageParam.initMerInfo('add', function(l) {
		dwr.util.removeAllOptions("to_prov_id");
		dwr.util.addOptions("to_prov_id", {
			"" : "请选择..."
		});
		dwr.util.addOptions("to_prov_id", l[0]);
	});
	DfB2BSingleService.getMerGate(mid, function(merGate) {
		dwr.util.removeAllOptions("to_bk_no");
		dwr.util.addOptions("to_bk_no", {
			"" : "请选择..."
		});
		dwr.util.addOptions("to_bk_no", merGate);
	});
	PageParam.initQueryMinfo(function(data) {
		prov = data[0];
		map_merTradeType = data[3];
		map_idType = data[4];
	});
	var obj = [ 12 ];
	initGateChannel3(obj);
}
function cusTextchange() {
	$("cusId").value = "";
}
function pwd_bak(value) {
	$("pwdbak").value = value;
}

function SerchBankNoInfo() {
	var mid = $("mid").value;
	DfB2BSingleService.getMerGate(mid, function(merGates) {
		jQuery("#serchBankNo").wBox({
			title : "&nbsp;&nbsp;联行号查询",
			show : true
		});
		
		dwr.util.removeAllOptions("gate");
		dwr.util.addOptions("gate", {"":"全部..."});
		dwr.util.addOptions("gate", merGates);		
		$("gate").value = $("to_bk_no").value;
	});
}
function SerchBankNo(PageNo) {
	var gate = $("gate").value.trim();
	var bkname = $("bk_name").value.trim();
	MerchantService.queryBKNo(PageNo, gate, bkname, callBack2);

}
var callBack2 = function(pageObj) {
	$("serach").style.display = "";
	dwr.util.removeAllRows("resultListbankno");
	if (pageObj == null) {
		document.getElementById("resultListbankno").appendChild(
				creatNoRecordTr(13));
		return;
	}
	var cellFuncs = [
			function(obj) {
				var objHtml = "<input type='radio' name='bankno' value='" + obj.bkNo + "|" + obj.bkName + "'/>";
				return objHtml;
			}, function(obj) {
				return obj.bkNo;
			}, function(obj) {
				return obj.bkName;
			} ];
	paginationTable(pageObj, "resultListbankno", cellFuncs, "", "SerchBankNo");
};

function confirmbankNo() {
	var countArr = getValuesByName("bankno");
	if (countArr.length == 0) {
		alert("请选择一条记录！");
		return false;
	}
	var bankInfos = countArr[0].split("|");
	var bkNo = bankInfos[0];
	var bkName = bankInfos[1];
	dwr.util.setValues({
		to_openbk_no : bkNo
	});
	dwr.util.setValues({
		to_openbk_name : bkName
	});
	jQuery("#wBox_close").click();

}