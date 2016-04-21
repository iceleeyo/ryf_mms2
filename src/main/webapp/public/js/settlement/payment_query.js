
var liqList = {};
var pay_state_map = {0:"银行处理中",1:"成功", 2:"失败", 3:"可疑" ,4:"记录不存在"};
var trans_type_map={0:"系统内手工转账",1:"系统内自动转账",2:"对外支付",3:"其他"};

// 划款交易查询列表
function queryPayment(pageNo) {
	var startDate = $("sys_date_begin").value;
	var endDate = $("sys_date_end").value;
//	var accNo = $("acc_no").value;
	if (startDate == null || startDate == '') {
		alert('请选择开始日期');
		return null
	}
	if (endDate == null || endDate == '') {
		alert('请选择结束日期');
		return null
	}
	AutoSettlementService.querySlogPage(pageNo,startDate,endDate,function(pageObj) {
						$("minfoList").style.display ="";
						if (pageObj == null) {
							document.getElementById("minfoList").appendChild(creatNoRecordTr(8));
							return;
						}
						var cellFuncs = [
								function(obj) {return obj.bankSeq;},
								function(obj) {return obj.bankDate;},
								function(obj) {return div100(obj.liqAmt);},
								function(obj) {return obj.payState==0?"正常":"已经被抹账";},
								function(obj) {return obj.payAcname;},
								function(obj) {return obj.balance;},
								function(obj) {	return obj.rcvAcname;},
								function(obj) {
									if(obj.bankSeq=="00000000")return "";
									return "<input type=\"button\" value=\"详细\"  onclick=\"querySlog('"+ obj.bankSeq + "');\" >";
								} ];
						paginationTable(pageObj, "minfoListBody", cellFuncs,"", "queryPayment");
			});
}

// 划款交易查询明细
function querySlog(bankSeq) {
	AutoSettlementService.querySlog(bankSeq, function(obj) {
		   if(obj==null){
			   alert("系统没有该条记录的详情！");
			   return;
			}
			$("id").innerHTML = obj.id;
			$("pay_acname").innerHTML = obj.payAcname;
			$("mid").innerHTML = obj.mid;
			$("bank_date").innerHTML = obj.bankDate;
			$("pay_acno").innerHTML = obj.payAcno;
			$("rcv_acname").innerHTML = obj.rcvAcname;
			$("bank_time").innerHTML=getStringTime(obj.bankTime);
			$("liq_amt").innerHTML = div100(obj.liqAmt);
			$("rcv_acno").innerHTML = obj.rcvAcno;
			$("pay_state").innerHTML = pay_state_map[obj.payState];
			//$("balance").innerHTML = div100(obj.balance);
			$("rcv_addr").innerHTML = obj.rcvAddr;
			$("trans_type").innerHTML =trans_type_map[obj.transType];
			//$("us_balance").innerHTML = div100(obj.usBalance);
			$("rcv_lhh").innerHTML = obj.rcvLhh;
			$("batch").innerHTML = obj.batch;
			$("sure_date").innerHTML=obj.sureDate;
			$("rcv_bank_name").innerHTML = obj.rcvBankName;
			$("bank_seq").innerHTML = obj.bankSeq;
			$("sure_time").innerHTML = getStringTime(obj.sureTime);
		    jQuery("#slogDetail").wBox({title:"结算划款详细信息",show:true});//显示box
		});
}

// 单笔划款查询
function getSinglePayInfo() {
	var seqType= $("seqType").value;
	var webSerialNo = $("webSerialNo").value;
	if (webSerialNo == null || webSerialNo == '') {
		alert('请输入流水号！');
		return false;
	}
	AutoSettlementService.querySinglePay(seqType,webSerialNo, function(obj) {
		$("perInfo").style.display = '';
		$("oldSerialNo").innerHTML = obj.oldSerialNo;
		$("receAccNo").innerHTML = obj.receAccNo;
		$("state").innerHTML =obj.state.trim()==""?"":pay_state_map[obj.state];
		$("receBankNo").innerHTML = obj.receBankNo;
		$("errMsg").innerHTML = obj.errMsg;
		$("money").innerHTML = obj.money;
		$("payAccNo").innerHTML = obj.payAccNo;
		$("rypSerialNo").innerHTML = obj.rypSerialNo;
		$("payBankNo").innerHTML = obj.payBankNo;
	});
}

