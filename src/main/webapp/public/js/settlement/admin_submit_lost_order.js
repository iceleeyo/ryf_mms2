
var m_auth_type = {};
var a = [{name:'',text:'全部'}];
function initLostOrder(){
//	SettlementService.getLostOrde(function(m){
//		m_minfos = m[0];
//		m_gates = m[1];
//		m_auth_type = m[2];
//		dwr.util.addOptions("smid", m[0]);
//	});
	initMinfos();
	initGateChannel();
}
// 根据条件查询(第一次查询)
function queryBaseLostOrder(pageNo) {
	var mid = dwr.util.getValue("mid").trim();
	var oid = dwr.util.getValue("oid").trim();
	var tseq = dwr.util.getValue("tseq").trim();
	//var btdate = dwr.util.getValue("btdate").trim();
	//document.getElementById("lostOrderList").style.display = "";
	//document.getElementById("lostOrderList2").style.display = "none";
	if (mid == '') {
		alert("商户号不能为空。");
		return false;
	}
	if (!isFigure(mid)) {
		alert("商户号只能是整数!");$("mid").value='';
		return false;
	}
	if (tseq != '' && !isFigure(tseq)) {
		alert("电银流水号只能为数字，请重新输入。");
		return false;
	}
//	if (btdate == '') {
//		alert("订单生成日期不能为空。");
//		return false;
//	}
	SettlementService.queryLostOrder(pageNo, mid, oid, tseq, 0, callBackLostOrderList);
}
// 提取用户列表的回设函数
	var callBackLostOrderList = function(lostOrderList) {
		document.getElementById("lostOrderList2").style.display = "none";
		document.getElementById("lostOrderList").style.display = '';
		dwr.util.removeAllRows("lostOrderTable");
		var cellFuncs = [				
						 function(obj) { return  "<input type='radio' id='toValidate"+obj.tseq+"' name='toValidate' onclick = 'setTseq("+obj.tseq+")' >";},			  								 
		                 function(obj) { return  obj.mid;},
		                 function(obj) { return m_minfos[obj.mid]; },
		                 function(obj) { return  obj.tseq; },
		                 function(obj) { return obj.oid; },
		                 function(obj) { return div100(obj.amount); },
		                 function(obj) { return obj.sysDate; },
		                 function(obj) { return gate_route_map[obj.gid]; },
		                 function(obj) { return "待支付"; }
		              ]
		           var   str="&nbsp;&nbsp;<input type='button' id='Validate' name='Validate' value='手工确认' onclick=\"confirm_LostOrder()\">&nbsp;&nbsp;&nbsp;";
		paginationTable(lostOrderList,"lostOrderTable",cellFuncs,str,"queryBaseLostOrder");
	}
function setTseq(t){
	document.getElementById("lostOrderTseq").value = t;
}
// 掉单手工提交--提交
function confirm_LostOrder() {
	var vtseq = document.getElementById("lostOrderTseq").value;
	if (vtseq=='') {
		alert("请至少选择一条记录！");
		return false;
	}
	 //if (confirm("确定手工确认吗?")) 
		SettlementService.queryLostOrderByTseq(vtseq,function(h){
			document.getElementById("tseq_").innerHTML = h.tseq ;
			document.getElementById("mid_").innerHTML = h.mid ;
			document.getElementById("name_").innerHTML = m_minfos[h.mid] ;
			document.getElementById("oid_").innerHTML = h.oid;
			document.getElementById("amount_").innerHTML = div100(h.amount) ;
			document.getElementById("sys_date_").innerHTML = h.sysDate ;
			document.getElementById("gate_").innerHTML = h_gate[h.gate] ;
			document.getElementById("bkgate_").value = h.gate;
			document.getElementById("gid_").innerHTML = gate_route_map[h.gid];			
			document.getElementById("gid_").value = h.gid;			
			dwr.util.removeAllOptions("gate__");
		    dwr.util.addOptions("gate__", a,'name','text',{escapeHtml:false});
		    dwr.util.addOptions("gate__", h_gate);
		})
		document.getElementById("bank_amount").value = '';
		document.getElementById("bank_code").value = '';
	    document.getElementById("lostOrderList").style.display = "none";
		document.getElementById("lostOrderList2").style.display = "";
		
	
}
// 掉单手工提交--确认
function checkSubmitData(action) {
	
	var mid = document.getElementById("mid_").innerHTML;	
	var tseq = document.getElementById("tseq_").innerHTML;
	var lost_gate = document.getElementById("bkgate_").value;
	var lost_amount = document.getElementById("amount_").innerHTML;
	var merDate = document.getElementById("sys_date_").innerHTML;	
	var gate_name = document.getElementById("gate__").value;//网关ID
	var bank_code = document.getElementById("bank_code").value;
	var bank_amount = document.getElementById("bank_amount").value;
	var gid = document.getElementById("gid_").value
	var bank_Amount = parseInt(bank_amount * 100);
	if (gate_name == '') {
		alert("请选择交易银行。");
		return false;
	}
	if (bank_code == '') {
		alert("请输入银行交易流水号。");
		return false;
	}
	if (bank_amount == '') {
		alert("请输入银行交易金额。");
		return false;
	}
	if (!isNumber(bank_amount)) {
		alert("交易金额只能为数字，请重新输入。");
		return false;
	}
	if ((lost_gate != gate_name || parseInt(lost_amount*100) != bank_Amount)) {
		alert("选择银行或交易金额与原订单号不符，请确认后重新提交！")
		return false;
	}	
	if (action == 'success' && confirm("确定支付成功吗?")) {	
		SettlementService.confirmLostOrder(action, tseq, merDate, bank_code,gate_name,mid,lost_amount,gid,callback);		
	}else if (action == 'failure' && confirm("确定支付失败吗?")) {		
		SettlementService.confirmLostOrder(action, tseq, merDate, bank_code, gate_name,mid,lost_amount,gid,callback);
	}
	function callback(msg) {
		if(msg=="ok"){
			alert("操作成功！");
		}else{
			alert(msg);
		}
		queryBaseLostOrder(1); 
	}
}


function viewList(){
	document.getElementById("lostOrderList").style.display = "";
	document.getElementById("lostOrderList2").style.display = "none";
}