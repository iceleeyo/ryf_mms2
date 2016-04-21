var prov = {};
var query_cash = {};
var t_state={0:'申请代付',1:'代付确认',2:'代付成功',3:'代付失败',4:'请求银行失败',5:'代付撤销'};
function init() {
	PageService.getProvMap(function(pro) {
		prov = pro;
	});
	 var obj=[11,12];
     initGateChannel3(obj);
}

// 查询代付确认数据
function queryDFQR(pagNo) {
	var batchNo = $("batchNo").value.trim();
	var type = $("type").value;
	var tseq = $("tseq").value.trim();
	var dateType = $("dateType").value;
	var bdate = $("bdate").value;
	var edate = $("edate").value;
	var state = $("state").value;
	if (bdate == "" || edate == "") {
		alert("请选择日期");
		return;
	}
	DfqrService.queryDFQRinfo(
					pagNo,
					batchNo,
					tseq,
					type,
					dateType,
					state,
					bdate,
					edate,
					function(data) {
						$("dateTable").style.display = "";
						var cellFuns = [
								function(obj) {
									return (obj.tstat == 0 ? "<input type=\"checkbox\"  name=\"toCheck\" id=\"toCheck\" value=\""
											+ obj.oid + "\">"
											: "");
								},
								function(obj) { // 电银流水号
									return obj.tseq_str;
								},
								function(obj) { // 账户名
									return "结算账户";
								},
								function(obj) { // 商户订单号
									return obj.oid;
								},
								function(obj) { // 交易批次号
									return obj.p8;
								},
								function(obj) { // 交易金额
									 return div100(obj.amount);
								},
								function(obj) { // 系统手续费
									return div100(obj.fee_amt);
								},
								function(obj) { // 付款金额
									return div100(obj.pay_amt);
								},
								function(obj) { // 收款银行
									return h_gate3[obj.gate];
								},
								function(obj) { // 收款户名
									return obj.p2;
								},
								function(obj) { // 收款账号
									return obj.p1PT;
								},
								function(obj) { // 开户所在省份 
									return prov[obj.p10];
								},
								function(obj) { // 代付状态
									return t_state[obj.tstat];
								}, function(obj) { // 交易类型
									return obj.type == 11 ? "对私代付" : "对公代付";
								}, function(obj) { // 代付日期
									return obj.bk_date == 0 ? "" : obj.bk_date;
								}, function(obj) { // 用途
									return handleBase64Decode(obj.p7);
								} ];
						for ( var i = 0; i < data.pageItems.length; i++) {
							var o = data.pageItems[i];
							query_cash[o.oid] = o;

						}
						var str = "<span style='float:left;'>&nbsp;<input type='button' id='chooseAll' name='chooseAll' value='全 选' onclick=\"checkAll('toCheck');;\">&nbsp;&nbsp;&nbsp;";
						str += " 交易总金额:<font color='red'><b><span id='totalAmt'></span>元</font></b>";
						str += " 系统手续费总金额:<font color='red'><b><span id='totalTransFee'></span>元</font></b>";
						str += " 付款金额:<font color='red'><b><span id='totalPayAmt'></span>元</font></b>";
						str += "<input type='button' value = '代付确认' onclick='DFHandle(1);'/>&nbsp;&nbsp;&nbsp;&nbsp;";
						str += "<input type='button' value = '代付撤销' onclick='DFHandle(2);'/>&nbsp;&nbsp;&nbsp;&nbsp;";
						str += "</span>";
						paginationTable(data, "resultList", cellFuns, str, "queryDFQR");
						if (data.pageItems.length != 0) {
							document.getElementById("totalPayAmt").innerHTML = div100(data.sumResult["payAmtSum"]);
							document.getElementById("totalTransFee").innerHTML = div100(data.sumResult["sysAmtFeeSum"]);
							document.getElementById("totalAmt").innerHTML = div100(data.sumResult["amtSum"]);
						}
					});
}

function DFHandle(flag) {
	var array = new Array();
	var chkArray = document.getElementsByName("toCheck");
	var index = 0;
	for ( var i = 0; i < chkArray.length; i++) {
		if (chkArray[i].checked) {
			var val = query_cash[chkArray[i].value];
			array[index] = val;
			index++;
		}
	}
	if (index == 0) {
		alert("请至少选中一条记录!");
		return false;

	} else {
		if (flag == 1) {
			if (confirm("确认代付?")) {
				// 代付确认
				DfqrService.DFconfirm(array, function(flag) {
					alert(flag);
					queryDFQR(1);
				});
			}
		} else {
			if (confirm("确定撤销代付?")) {
				// 代付撤销
				DfqrService.DfCancel(array, function(ret) {
					if (ret.flag == "0")
						alert("撤销成功!");
					else
						alert("撤销失败:" + ret.msg);
					queryDFQR(1);
				});
			}
		}
	}
}

function downqueryDaiFuQueRen() {
	var batchNo = $("batchNo").value.trim();
	var type = $("type").value;
	var tseq = $("tseq").value.trim();
	var dateType = $("dateType").value;
	var bdate = $("bdate").value;
	var edate = $("edate").value;
	var state = $("state").value;
	if (bdate == "" || edate == "") {
		alert("请选择日期");
		return;
	}
	DfqrService.downqueryDaiFuQueRen(1, batchNo, tseq, type, dateType, state,
			bdate, edate, {
				callback : function(data) {
					dwr.engine.openInDownload(data);
				},
				async : false
			});
}

function handleBase64Decode(str){
	try{
		if(str != '')
			return utf8to16(base64decode(str));
	}catch(e){
		
	}
	return str;
}
