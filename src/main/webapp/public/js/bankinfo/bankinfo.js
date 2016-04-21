var acc_type_map={1:"基本户" , 2:"一般户", 3:"专用户",4:"临时户"};
// 查询用户信息
var bkAccount={};
function getAccInfo() {
	var acc_no = document.getElementById("acc_no").value;
	if (acc_no == '' || acc_no == null) {
		alert('请输入账号');
		return null;
	}
	AutoSettlementService.getAccInfo(acc_no, function(holdBkList) {
		       var holdBk=holdBkList[0];
				if(holdBk==null){
					alert('请求异常,请联系系统管理员!');
					return;
				}
				if(holdBk.errorMsg!=null){// 错误信息
					alert(holdBk.errorMsg);
					return;
				}
				bkAccount=holdBk;
				document.getElementById("acc_name").innerHTML = holdBk.accName;
				document.getElementById("currency").innerHTML = holdBk.currency;
				document.getElementById("oper_date").innerHTML = holdBk.operDate;
				document.getElementById("acc_type").innerHTML = acc_type_map[holdBk.accType];
				document.getElementById("bk_name").innerHTML = holdBk.bkName;	
				document.getElementById("addInfo").disabled = false;							
			});
}

// 添加存管银行账号信息
function addHoldBkMsg() {
	var accNo = document.getElementById("acc_no").value;
	var bkNo = document.getElementById("bk_no").value;
	if (accNo =="") {
		alert('银行账号不能为空！请填写后再提交。');
		return false;
	}
	if(bkNo==""){
		alert("银行行号不能为空！请填写后再提交。");
		return false;
	}
	bkAccount.bkNo=bkNo;
	AutoSettlementService.insertInfo(bkAccount,function(msg) {
				if (msg == 'ok') {
					alert('增加成功!');
					document.getElementById("acc_no").value = "";
					document.getElementById("acc_name").innerHTML = "";
					document.getElementById("currency").innerHTML = "";
					document.getElementById("oper_date").innerHTML = "";
					document.getElementById("acc_type").innerHTML = "";
					document.getElementById("bk_name").innerHTML = "";
					document.getElementById("addInfo").disabled = true;
				} else {
					alert(msg);
				}
	});
}
// 获得账号下拉列表
var acc = {};
function init() {
	AutoSettlementService.AccNoMatchName(function(map) {
		acc = map;
		dwr.util.addOptions("old_no", acc);
	});
}

// 获得账号信息
function getLocalAccInfo(s) {
	AutoSettlementService.getLocalAccInfo(s, function(bkacc) {
					if(bkacc==null){alert("此账号信息异常！");return;}
					document.getElementById("acc_name").innerHTML = bkacc.accName;
					document.getElementById("currency").innerHTML = bkacc.currency;
					document.getElementById("oper_date").innerHTML = bkacc.operDate;
					document.getElementById("acc_type").innerHTML = acc_type_map[bkacc.accType];
					document.getElementById("bk_name").innerHTML = bkacc.bkName;
					document.getElementById("bk_type").value = bkacc.bkType;
				});
}

function alterAccNo() {

	var oldNo = document.getElementById("old_no").value;
	var newNo = document.getElementById("new_no").value;
	var bkType = document.getElementById("bk_type").value;

	if (oldNo == '' || oldNo == null) {
		alert('原账号不能为空,请选择');
		return null;
	}
	if (newNo == '' || newNo == null) {
		alert('新账号不能为空,请填写');
		return null;
	}
	if (bkType == '' || bkType == null) {
		alert('银行类型不能为空,请填写');
		return null;
	}

	AutoSettlementService.alterAccNo(oldNo, newNo, bkType, function(msg) {

		if (msg == 'ok') {
			alert("修改成功");
			window.location.reload();
		} else
			alert(msg);
	});
}

// 余额查询
function queryBalance() {
	var accNo = dwr.util.getValue("acc_no");
	if (accNo == null || accNo == '') {
		alert('请输入账号');
		return null;
	}
	AutoSettlementService.getAccInfo(accNo, function(balanceList) {
		if(balanceList==null){
			alert('请求异常,请联系系统管理员!');
			return;
		}
		if(balanceList[0].errorMsg!=null){
			alert(balanceList[0].errorMsg);
			return;
		}
		dwr.util.removeAllRows("balanceTable");
		document.getElementById("balanceList").style.display = '';
		var cellFuncs = [ function(obj) {return obj.accName;},
		                  function(obj) {return obj.accNo;}, 
		                  function(obj) {return div100(obj.bkBl);},
		                  function(obj) {return div100(obj.bfBl);},
		                  function(obj) {return obj.bkName;}, 
		                  function(obj) {return acc_type_map[obj.accType];},
		                  function(obj) {return obj.operDate;} 
		                  ];
		
		dwr.util.addRows("balanceTable", balanceList, cellFuncs);
		});
}
//合作银行账号录入  
function addCooperateBk(){
	//var accNo2 = document.getElementById("accNo2").value;
	var bkNo2 = document.getElementById("bkNo2").value;
	var bkAbbv2 = document.getElementById("bkAbbv2").value;

	if(bkNo2==""){
		alert("银行行号不能为空！");
		return false;
	}
	if(bkAbbv2==""){
		alert("银行简称不能为空！");
		return false;
	}
	AutoSettlementService.addCooperateBkNo(bkNo2,bkAbbv2,function(data){
		if(data=="ok"){
			alert("合作银行行号录入 成功！");
		}else{
			alert(data);
		}
	});
}




