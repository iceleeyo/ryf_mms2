 var prov = {};
function init(){
	dwr.util.addOptions("mer_trade_type", merTradeType);
	PageParam.initMerInfo('add', function(l) {
		dwr.util.removeAllOptions("pbkProvId");
		dwr.util.addOptions("pbkProvId", {"" : "请选择…"});
		dwr.util.addOptions("pbkProvId", l[0]);
		
	});
	 PageParam.initQueryMinfo(function(data){
	        prov = data[0];
			map_merTradeType=data[3];
			map_idType=data[4]; 
   });
	 var obj=[11];
     initGateChannel3(obj);
}
function queryAutoDf(PageNo){
	var mid =$("mid").value;
	var mstate=$("mstate").value;
	 
	AutoDFInfoPreserveService.queryAutoDf(PageNo,mid,mstate,callBack2);	
}

var callBack2 = function(pageObj){
	   $("queryAutoDfTable").style.display="";
	   dwr.util.removeAllRows("resultList");
	   if(pageObj==null){document.getElementById("resultList").appendChild(creatNoRecordTr(13));return;}
   var cellFuncs = [
                    function(obj) { return obj.id; },
                    function(obj) { return obj.name; },
                    function(obj) { return obj.mstate==0?"正常":"关闭"; },
                    function(obj) { return obj.id;},
                    function(obj) { return obj.autoDfState==0?"关闭":"开启";; },
                    function(obj) { return prov[obj.pbkProvId]; },
                    function(obj) { return obj.pbkName; },
                    function(obj) { return obj.pbkBranch; },
                    function(obj) { return obj.pbkNo; },
                    function(obj) { return obj.pbkAccNo; },
                    function(obj) { return obj.pbkAccName; },
                    function(obj) { return h_gate3[obj.pbkGateId]; },
                    function(obj) { return obj.mstate==0?"<input type=\"button\" value=\"配置\" onclick=\"editAuto('"+obj.id+"')\"/>":""; }
                ];
     paginationTable(pageObj,"resultList",cellFuncs,"","queryAutoDf");
 };
function editAuto(id){	
	jQuery("#wBox_close").click();
	AutoDFInfoPreserveService.queryByidAutoDf(id,function(Auto){
		 mid=Auto.id;
		 dwr.util.setValues({
				m_mid:Auto.id,
				m_name:Auto.name,
				mer_trade_type:merTradeType[Auto.merTradeType],
				aid:Auto.id,
				pbk_name:Auto.pbkName,
				pbk_branch:Auto.pbkBranch,
				pbk_acc_no:Auto.pbkAccNo,
				pbk_acc_name:Auto.pbkAccName,
				pbk_no:Auto.pbkNo,
				 });		     
			 jQuery("#AutoDfWH").wBox({title:"&nbsp;&nbsp;商户自动代付信息配置",show:true});
			  $("pbkProvId").value=Auto.pbkProvId;
			  $("auto_df_state").value=Auto.autoDfState;
			  $("gateId").value=Auto.pbkGateId;
			 
	 });	
	
}

function edit(){
	if(checkMinfo()){
		var minfo={};
		var mid=$("m_mid").value.trim();
		minfo.pbkGateId=$("gateId").value.trim();
		minfo.pbkProvId=$("pbkProvId").value.trim();
		minfo.autoDfState=$("auto_df_state").value;
		minfo.pbkName=$("pbk_name").value;
		minfo.pbkBranch=$("pbk_branch").value;
		minfo.pbkAccNo=$("pbk_acc_no").value;
		minfo.pbkAccName=$("pbk_acc_name").value;
		minfo.pbkNo=$("pbk_no").value;
		AutoDFInfoPreserveService.updateMerAutoDf(minfo,mid,function(msg){
			alert(msg);
			jQuery("#wBox_close").click();
			queryAutoDf(1);
	});
}
}

function checkMinfo(){
	var pbkProvId=$("pbkProvId").value;
	var pbkName=$("pbk_name").value;
	var pbkBranch=$("pbk_branch").value;
	var pbkAccNo=$("pbk_acc_no").value;
	var pbkAccName=$("pbk_acc_name").value;
	var pbkNo=$("pbk_no").value;
	var gateId=$("gateId").value;
	 if(pbkProvId==''){
			alert("请选择开户省份");
			return false; 
		}
		if(pbkName==''){
			alert("请输入开户银行名");
			return false; 
		}
		if(pbkBranch==''){
			alert("请输入开户银行支行名");
			return false; 
		}
		if(pbkAccNo==''){
			alert("请输入银行账号");
			return false; 
		}
		if(pbkAccName==''){
			alert("请输入开户帐号名");
			return false; 
		}
		if(pbkNo==''){
			alert("请输入开户银行行号");
			return false; 
		}
		if(gateId==0){
			alert("请选择结算银行");
			return false; 
		}
		return true;
}
var autoDfState;
var pbkProvId;
var pbkname;
var pbkBranch;
var pbkAccNo;
var pbkAccName;
var gateId;
//检索银行行号
function SerchBankNoInfo() {
	autoDfState=$("auto_df_state").value;
	pbkProvId=$("pbkProvId").value;
	pbkname=$("pbk_name").value;
	pbkBranch=$("pbk_branch").value;
	pbkAccNo=$("pbk_acc_no").value;
	pbkAccName=$("pbk_acc_name").value;
	gateId=$("gateId").value;	
	    jQuery("#wBox_close").click();
		jQuery("#serchBankNo").wBox({title : "&nbsp;&nbsp;联行号查询",show : true});

}

function SerchBankNo(PageNo) {
	var gate = $("gate").value.trim();
	var bkname = $("bk_name").value.trim();
	AutoDFInfoPreserveService.queryBKNo(PageNo, gate, bkname, callBack3);

}
var callBack3 = function(pageObj) {
	$("serach").style.display = "";
	dwr.util.removeAllRows("resultListbankno");
	if (pageObj == null) {
		document.getElementById("resultListbankno").appendChild(creatNoRecordTr(13));
		return;
	}
	var cellFuncs = [
			function(obj) {
				return "<input type='radio' onclick='canEditBut(" + obj.bkNo+ ")' name='bankno' value='" + obj.bkNo + "'/>";}, 
				function(obj) {return obj.bkNo;},
				function(obj) {return obj.bkName;
			} ];
	paginationTable(pageObj, "resultListbankno", cellFuncs, "", "SerchBankNo");
}

function confirmbankNo() {
	var countArr = getValuesByName("bankno");
	if (countArr.length == 0) {
		alert("请选择一条记录！");
		return false;
	}
	dwr.util.setValues({
		pbk_no : countArr
	});
	jQuery("#wBox_close").click();
	 dwr.util.setValues({
			pbk_name:pbkname,
			pbk_branch:pbkBranch,
			pbk_acc_no:pbkAccNo,
			pbk_acc_name:pbkAccName,
			 });		     
		 jQuery("#AutoDfWH").wBox({title:"&nbsp;&nbsp;商户自动代付信息配置",show:true});
		  $("pbkProvId").value=pbkProvId;
		  $("auto_df_state").value=autoDfState;
		  $("gateId").value=gateId;

}

