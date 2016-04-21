var bknos = {};
var zh_flag = null;
var oid = {};
var aid = null;
/* 创建paydata 对象 */
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
	gate : null
};
function init() {
	var mid = $("mid").value;
	PageParam.initMerInfo('add', function(l) {
		dwr.util.removeAllOptions("to_prov_id");
		dwr.util.addOptions("to_prov_id", {
			"" : "请选择..."
		});
		dwr.util.addOptions("to_prov_id", l[0]);
	});
	DfB2CSingleService.getMerGate(mid,function(merGates) {
		dwr.util.removeAllOptions("to_bk_no");
		dwr.util.addOptions("to_bk_no", {
			"" : "请选择..."
		});
		dwr.util.addOptions("to_bk_no", merGates);
	});
	if (zh_flag == null) {
		DfB2CSingleService.getZHUid(mid, function(zh) {
			dwr.util.addOptions("aid", zh);
			zh_flag = zh;
		});
	}

	PageParam.initQueryMinfo(function(data) {
		prov = data[0];
		map_merTradeType = data[3];
		map_idType = data[4];
	});
	var obj = [ 11 ];
	initGateChannel3(obj);
}

function addSinglePay() {
	aid = $("aid").value;
	var toAccNo = $("to_acc_no").value;
	var toAccName = $("to_acc_name").value;
	var toBkName = dwr.util.getText("to_bk_no");
	var toBkNo = $("to_bk_no").value;
	var toOpenBkNo = $("to_openbk_no").value;
	var toOpenBkName = $("to_openbk_name").value;
	var toProvId = $("to_prov_id").value;
	var cardFlag = $("card_flag").value;
	var transAmt = $("trans_amt1").value;
	var priv = $("priv").value;
	if (aid == '') {
		alert("请选择转出账户号！");
		return false;
	}
	if (toAccNo == '') {
		alert("请输入收款人账号！");
		return false;
	}
	if (toAccName == '') {
		alert("请输入收款人户名！");
		return false;
	}
	if (toBkNo == '') {
		alert("请选择收款银行！");
		return false;
	}
	if (toOpenBkNo == '') {
		alert("请选择收款银行联行行号！");
		return false;
	}
	if (toProvId == '') {
		alert("请选择开户所在省份！");
		return false;
	}
	if (cardFlag == '') {
		alert("请选择卡折标志！");
		return false;
	}
	if (transAmt == '' || !isMoney(transAmt)) {
		alert("请输入正确的交易金额！");
		return false;
	}
	if (transAmt == 0) {
		alert("交易金额不合法！");
		return false;
	}
	if (priv == '') {
		alert("请输入用途！");
		return false;
	}
	if (!isAccNo(toAccNo)) {
		alert("请输入正确的银行卡号！");
		return false;
	}
	var oid_ = $("oid").value;
	if (oid_ != "") {
		/* data={oid,transAmt,transFee,payAmt,balance} */
		DfB2CSingleService.checkSinglePay(aid, oid, transAmt, toBkNo,
				function(data) {
					if(data.flag != "0"){
						alert(data.msg);
						return;
					}
			
					oid = data.oid;
					var ids = new Array("aid", "to_acc_no", "to_acc_name",
							"to_bk_no", "to_openbk_no", "toProvId", "cardFlag",
							"trans_amt", "priv");
					for ( var i = 0; i < ids.length; i++) {
						var obj = document.getElementById(ids[i]);
						if (obj != null) {
							obj.value = "";
						}
					}
					var fee = data.transFee;
					var trans_amt = data.transAmt;
					var pay_amt = data.payAmt;
					var use_balance = data.balance;
					var gid = data.gid;
					$("table1").style.display = "none";
					$("table2").style.display = "";
					$("a_aid").innerHTML = aid;
					$("a_to_acc_no").innerHTML = toAccNo;
					$("a_to_acc_name").innerHTML = toAccName;
					$("a_bkno").innerHTML = toBkName;
					$("a_to_openbk_no").innerHTML = toOpenBkNo;
					$("a_pay_amt").innerHTML = pay_amt + "元";
					$("a_priv").innerHTML = priv;
					$("a_oid").innerHTML = oid;
					$("a_trans_amt").innerHTML = trans_amt + "元";
					$("a_fee").innerHTML = fee + "元";
					/* 填充弹出确认页隐藏域 */
					$("a_feeh").value = fee;
					$("a_to_bk_noh").value = toBkNo;
					$("a_trans_amth").value = trans_amt;
					$("a_pay_amth").value = pay_amt;
					$("oid").value = oid;
					$("a_card_flagh").value = cardFlag;
					$("a_to_prov_idh").value = toProvId;
					/* 填充填出密码确认页内容 */
					$("sumAmt").innerHTML = trans_amt + "元";
					$("fee_amt").innerHTML = fee + "元";
					$("sum_amt").innerHTML = pay_amt + "元";
					$("balance").innerHTML = use_balance + "元";
					/* oid,aid,toAccNo,toAccName,toBkName,toBkNo,toProvId,cardFlag,payAmt,priv,transAmt,transFee */
					payDataConstructor(oid, aid, toAccNo, toAccName,
							toOpenBkName, toOpenBkNo, toProvId, cardFlag,
							pay_amt, priv, trans_amt, fee, gid, toBkNo);
				});
	} else {
		/* data={oid,transAmt,transFee,payAmt,balance} */
		DfB2CSingleService.checkSinglePay(aid, "", transAmt, toBkNo, function(
				data) {
			if(data.flag != "0"){
				alert(data.msg);
				return;
			}
			
			oid = data.oid;
			var ids = new Array("aid", "to_acc_no", "to_acc_name", "to_bk_no",
					"toProvId", "cardFlag", "trans_amt1", "priv");
			for ( var i = 0; i < ids.length; i++) {
				var obj = document.getElementById(ids[i]);
				if (obj != null) {
					obj.value = "";
				}
			}
			var fee = data.transFee;
			var trans_amt = data.transAmt;
			var pay_amt = data.payAmt;
			var all_balance = data.balance;
			var gid = data.gid;
			/* 填充弹出确认页内容 */
			$("table1").style.display = "none";
			$("table2").style.display = "";
			$("a_aid").innerHTML = aid;
			$("a_to_acc_no").innerHTML = toAccNo;
			$("a_to_acc_name").innerHTML = toAccName;
			$("a_bkno").innerHTML = toBkName;
			$("a_to_openbk_no").innerHTML = toOpenBkNo;
			$("a_priv").innerHTML = priv;
			$("a_pay_amt").innerHTML = pay_amt + "元";
			$("a_oid").innerHTML = oid;
			$("a_trans_amt").innerHTML = trans_amt + "元";
			$("a_fee").innerHTML = fee + "元";
			/* 填充弹出确认页隐藏域 */
			$("a_feeh").value = fee;
			$("a_to_bk_noh").value = toBkNo;
			$("a_trans_amth").value = trans_amt;
			$("a_pay_amth").value = pay_amt;
			$("oid").value = oid;
			$("a_card_flagh").value = cardFlag;
			$("a_to_prov_idh").value = toProvId;
			/* 填充填出密码确认页内容 */
			$("sumAmt").innerHTML = trans_amt + "元";
			$("fee_amt").innerHTML = fee + "元";
			$("sum_amt").innerHTML = pay_amt + "元";
			$("balance").innerHTML = all_balance + "元";
			payDataConstructor(oid, aid, toAccNo, toAccName, toOpenBkName,
					toOpenBkNo, toProvId, cardFlag, pay_amt, priv, trans_amt,
					fee, gid, toBkNo);
		});
	}
}

// 点击返回 返回订单录入页面 原信息保留。
function backout() {
	$("table2").style.display = "none";
	$("table1").style.display = "";
	$("aid").value = $("a_aid").innerHTML;
	$("to_acc_no").value = $("a_to_acc_no").innerHTML;
	$("to_acc_name").value = $("a_to_acc_name").innerHTML;
	$("to_bk_no").value = $("a_to_bk_noh").value;
	$("to_prov_id").value = $("a_to_prov_idh").value;
	$("card_flag").value = $("a_card_flagh").value;
	$("trans_amt1").value = $("a_trans_amth").value;
	$("priv").value = $("a_priv").innerHTML;
	$("to_openbk_no").value = $("a_to_openbk_no").innerHTML; 
}
function confirmSinglePay() {
	jQuery("#table3").wBox({
		title : "&nbsp;&nbsp;请输入支付密码",
		show : true
	});
}
function edit() {
	var pwd = $("pwd").value;
	var transAmt = paydata.transAmt;
	if (window.confirm("确认支付？") && transAmt) {
		var data = [ paydata.aid, paydata.oid, paydata.toAccNo,
				paydata.toAccName, paydata.toBkName, paydata.toBkNo,
				paydata.toProvId, paydata.cardFlag, paydata.payAmt,
				paydata.transAmt, paydata.transFee, paydata.priv, paydata.gid,
				paydata.gate ];
		DfB2CSingleService.updateSinglePay(data, hex_md5(pwd), function(ret) {
			var flag = ret.split("\|")[0];
			var msg = ret.split("\|")[1];
			if (flag != "0") {
				alert(msg);
				return;
			}
			alert(msg);
			jQuery("#wBox_close").click();
			window.location.href = "M_37_DBFKDYHK.jsp";
		});
	}
}

// 生成订单
function genorderOid() {
	var d = new Date();
	var run = Math.floor(Math.random() * (1000000 + 1));
	return d + run;
}
/* payData 为属性赋值 */
function payDataConstructor(oid, aid, toAccNo, toAccName, toBkName, toBkNo,
		toProvId, cardFlag, payAmt, priv, transAmt, transFee, gid, gate) {
	paydata.oid = oid;
	paydata.aid = aid;
	paydata.toAccNo = toAccNo;
	paydata.toAccName = toAccName;
	paydata.toBkName = toBkName;
	paydata.toBkNo = toBkNo;
	paydata.toProvId = toProvId;
	paydata.cardFlag = cardFlag;
	paydata.payAmt = payAmt;
	paydata.priv = priv;
	paydata.transAmt = transAmt;
	paydata.transFee = transFee;
	paydata.gid = gid;
	paydata.gate = gate;
}

function SerchBankNoInfo() {
	var mid = $("mid").value;
	DfB2CSingleService.getMerGate(mid, function(merGates) {
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
				return "<input type='radio' name='bankno' value='" + obj.bkNo + "|" + obj.bkName + "'/>";
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
	var bankInfo = countArr[0].split("|");
	var bkNo = bankInfo[0];
	var bkName = bankInfo[1];
	dwr.util.setValues({
		to_openbk_no : bkNo
	});
	dwr.util.setValues({
		to_openbk_name : bkName
	});
	jQuery("#wBox_close").click();

}