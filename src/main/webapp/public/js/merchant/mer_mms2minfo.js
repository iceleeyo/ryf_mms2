
//=============================商户重要信息修改

function initImportantOptions(){
  initMinfos();
  PageService.getProvMap(function(data){
  	     dwr.util.addOptions("bank_prov_id", {"":"请选择…"});
	     dwr.util.addOptions("bank_prov_id", data);
  });
	 PageService.getIdType(function(mapData){
		  dwr.util.addOptions("id_type",mapData);
     });
}
var mid=0;
function showMerMsg(){
   mid=document.getElementById("mid").value;
   if(mid==""){
	   alert("请输入商户号！");
	   $("minfoMsg").style.display="none";
	   return false;
   }
   if($("smid").value==""){
	   alert("商户不存在！");
	   $("minfoMsg").style.display="none";
	   return false;
   }
   document.getElementById("minfoMsg").style.display="";
   MerMerchantService.getImportentMsgByMid(mid,function(minfo){
	  	dwr.util.setValues({
	  	    id:mid,
		  	name:minfo.name,
		  	abbrev:minfo.abbrev,
		  	category:minfo.category,
		  	code_exp_date:minfo.codeExpDate,
		  	begin_date:minfo.beginDate,
		  	exp_date:minfo.expDate,
		  	corp_code:minfo.corpCode,
		  	reg_code:minfo.regCode,
		  	bank_name:minfo.bankName,
		  	bank_acct:minfo.bankAcct,
		  	bank_branch:minfo.bankBranch,
		  	bank_prov_id:minfo.bankProvId,
		  	open_date:minfo.openDate,
		  	bank_acct_name:minfo.bankAcctName,
		  	corp_name:minfo.corpName,
		  	id_type:minfo.idType,
		  	id_no:minfo.idNo,
		  	open_bk_no:minfo.openBkNo
	  	});
  });
}
//修改重要信息
function updateMinfoData(){
	var noBlankIds={
			"name":"商户全名",
			"abbrev":"商户简称",
		  	"begin_date":"合同开始日期",
		  	"exp_date":"合同结束日期",
		  	"bank_name":"开户银行名",
		  	"bank_acct":"开户账号名",
		  	"bank_branch":"开户银行支行名",
		  	"bank_prov_id":"开户银行省份",
		  	"open_date":"开户日期",
		  	"bank_acct_name":"开户账号名",
		  	"corp_name":"法人姓名",
		  	"id_no":"证件号码",
		  	"open_bk_no":"开户银行行号"  
	};//不允许为空的Id
    if(!judgeBlankByIds(noBlankIds))return;
    var minfo= wrapObj("minfoForm");
    MerMerchantService.updateMinfoImportantData(minfo,function(msg){
        if(msg=="ok"){
        	alert("修改成功！")
        }else{
           alert(msg);
        }
    });
}
//修改基本信息
function editBase(){
	var noBlankIds={
		  "prov_id":"所在省份",	 	
		  "liqPeriod":"结算周期", 	 		
		  "begin_fee":"商户开通费", 
		  "annual_fee":"商户年费",
		  "caution_money":"商户保证金", 	 
		  "signatory":"签约人",
		  "merTradeType":"商户所属行业 "
	};//不允许为空的Id
    if(!judgeBlankByIds(noBlankIds))return;
    if (!isNumber($("begin_fee").value)) {alert("请填写正确的商户开通费！");return false;}
    if (!isNumber($("annual_fee").value)) {alert("请填写正确的商户年费！");return false;}
    if (!isNumber($("caution_money").value)) {alert("请填写正确的商户保证金！");return false;}
	var minfo =  wrapObj("basicMinfoForm");
		minfo.id=mid;
		MerMerchantService.editMinfos(minfo,function(msg){
			alert(msg);
		});	
}
//修改联系人的信息
function editMinfoLXR(){	
	var minfo =getLxrMinfo("editedMefId");
	//检查输入数据的合法性
	if(!checkRelationPerson(minfo))return;
	MerMerchantService.editMinfoContact(minfo,function(msg){
		if(msg=='ok'){
			alert('修改成功');
		}else{
			alert(msg);
		}
	});
}

//=====================================================商户新增
//页面载入时初始化
function initadd(){
	PageParam.initMerInfo('add',function(l){	
		 dwr.util.removeAllOptions("prov_id");
	     dwr.util.addOptions("prov_id", {"":"请选择…"});
	     dwr.util.addOptions("prov_id", l[0]);
	     
	     dwr.util.removeAllOptions("bank_prov_id");
	     dwr.util.addOptions("bank_prov_id", {"":"请选择…"});
	     dwr.util.addOptions("bank_prov_id", l[0]);

	     dwr.util.removeAllOptions("liqPeriod");
	     dwr.util.addOptions("liqPeriod", {"":"请选择…"});
	     dwr.util.addOptions("liqPeriod", l[1]);
	     
	     dwr.util.addOptions("merTradeType",l[2]);
	});
	 PageService.getIdType(function(mapData){
		  dwr.util.addOptions("id_type",mapData);
     });
}
//增加新商户
function adminAddMinfo(){
	dwr.util.setEscapeHtml(false);
	if(checkMinfo()){
		//var minfo = getMinfoObject();
		var minfo=wrapObj("addMinfoForm")
		minfo.transLimit=minfo.transLimit*100;
		minfo.beginFee=minfo.beginFee*100;
		minfo.annualFee=minfo.annualFee*100;
		minfo.cautionMoney=minfo.cautionMoney*100;
		MerMerchantService.addMinfos(minfo,"",function(mid){
			if (!isFigure(mid)) {
				alert(mid);
			} else {
				alert("商户(" + mid + ")基本信息增加成功，请进行后续操作!");
		}
	});
	
	}
}
/*
//新增联系人信息
function addMinfo2FXXS(action){
  if(action=='add'){
  	$("add_minfo_fx_id").style.display = "";
  	$("add_minfo_lxr_id").style.display = 'none';
  	var minfo =getLxrMinfo("addedMinfoId");
  	//检查输入数据的合法性
  	if(checkRelationPerson(minfo)){
  		MerMerchantService.editMinfoContact(minfo);
  	}
  	
  }
  $("add_step_msg").innerHTML = "商户风险系数参数";
}
*/
//商户联系人信息Minfo对象
function getLxrMinfo(mid_id){
	var minfo = new Object();
	minfo.tel0=dwr.util.getValue("tel0").trim();
	minfo.contact0=dwr.util.getValue("contact0").trim();
	minfo.email0=dwr.util.getValue("email0").trim();
	minfo.cell0=dwr.util.getValue("phone0").trim();
	
	minfo.tel1=dwr.util.getValue("tel1").trim();
	minfo.contact1=dwr.util.getValue("contact1").trim();
	minfo.email1=dwr.util.getValue("email1").trim();
	minfo.cell1=dwr.util.getValue("phone1").trim();
	
	minfo.tel2=dwr.util.getValue("tel2").trim();
	minfo.contact2=dwr.util.getValue("contact2").trim();
	minfo.email2=dwr.util.getValue("email2").trim();
	minfo.cell2=dwr.util.getValue("phone2").trim();
	
	minfo.tel3=dwr.util.getValue("tel3").trim();
	minfo.contact3=dwr.util.getValue("contact3").trim();
	minfo.email3=dwr.util.getValue("email3").trim();
	minfo.cell3=dwr.util.getValue("phone3").trim();
	
	minfo.tel4=dwr.util.getValue("tel4").trim();
	minfo.contact4=dwr.util.getValue("contact4").trim();
	minfo.email4=dwr.util.getValue("email4").trim();
	minfo.cell4=dwr.util.getValue("phone4").trim();
	
	minfo.tel5=dwr.util.getValue("tel5").trim();
	minfo.contact5=dwr.util.getValue("contact5").trim();
	minfo.email5=dwr.util.getValue("email5").trim();
	minfo.cell5=dwr.util.getValue("phone5").trim();
	if(mid_id!=""){
		minfo.id = $(mid_id).value;
	}
	return minfo;
}





//校验商户信息
function checkMinfo(){
	var name = $("name").value.trim();
	var abbrev = $("abbrev").value.trim();
	var liqType = $("liq_type").value.trim();
	var beginDate = $("begin_date").value.trim();
	var expDate = $("exp_date").value.trim();
	var signatory = $("signatory").value.trim();
	var merChkFlag = $("mer_chk_flag").value.trim();
	var provId = $("prov_id").value.trim();
	var areaId = $("exp_date").value.trim();
	var transLimit = $("trans_limit").value.trim();
	var liqPeriod = $("liqPeriod").value.trim();
	var liqLimit = $("liq_limit").value.trim();
	var bankName = $("bank_name").value.trim();
	var bankAcct = $("bank_acct").value.trim();
	var bankBranch = $("bank_branch").value.trim();
	var bankProvId = $("bank_prov_id").value.trim();
	var openDate = $("open_date").value.trim();
	var bankAcctName = $("bank_acct_name").value.trim();
	var beginFee = $("begin_fee").value.trim();
	var annualFee = $("annual_fee").value.trim();
	var cautionMoney = $("caution_money").value.trim();
	var faxNo = $("fax_no").value.trim();
	var corpCode = $("corp_code").value.trim();
	var addr = $("addr").value.trim();
	var regCode = $("reg_code").value.trim();
	var zip = $("zip").value.trim();
	var webUrl = $("web_url").value.trim();
	//var mstate = $("mstate").value.trim();
	var mdesc = $("mdesc").value.trim();
	var refundFlag = $("refund_flag").value.trim();
	var merTradeType = $("merTradeType").value.trim();
	var idNo = $("id_no").value.trim();
	var openBkNo = $("open_bk_no").value.trim();
	var corpName = $("corp_name").value.trim();
	
	if (name == '') {alert("请填写商户名称！");return false;}
	if (abbrev == '') {alert("请填写商户简称！");return false;}
	if (provId == "") {alert("请选择商户所在省份！");return false;}
	if (signatory == '') {alert("请填写签约人！");return false;}
	if (beginDate == '') {alert("请选择合同起始日期！");return false;}
	if (expDate == '') {alert("请选择合同结束日期！");return false;}
	if (!judgeDate(beginDate, expDate)) {alert("起始日期不得大于结束日期！");return false;}
	if (liqPeriod == "") {alert("请选择结算周期！");return false;}
	if (bankProvId == "") {alert("请选择开户银行省份！");return false;}
	if (bankName == '') {alert("请填写开户银行名！");return false;}
	if (bankBranch == '') {alert("请填写 开户银行支行名！");return false;}
	if (bankAcct == '') {alert("请填写银行账号！");return false;}
	if (openDate == '') {alert("请选择开户日期！");return false;}
	if (beginFee == '' || !isNumber(beginFee)) {alert("请填写正确的开通费！");return false;}
	if (beginFee.indexOf(".") > -1) {alert("开通费不允许有小数！");return false;}
	if (annualFee == '' || !isNumber(annualFee)) {alert("请填写正确的年费！");return false;}
	if (annualFee.indexOf(".") > -1) {alert("年费不允许有小数！");return false;}
	if (cautionMoney == '' || !isNumber(cautionMoney)) {alert("请填写正确的保证金！");return false;}
	if (cautionMoney.indexOf(".") > -1) {alert("保证金不允许有小数！");return false;}
	if (!faxNo == '' && !isFigure(faxNo)) {alert("请填写正确的商户传真号码！");return false;}
	if (bankAcctName == '') {alert("请填写开户帐号名！");return false;}
	if (transLimit != '' && !(isNumber(transLimit))) {alert("单笔限制金额必须是数字！");return false;}
	if (liqLimit != '' && !(isNumber(liqLimit))) {alert("结算满足的额度必须是数字！");return false;}
	if (transLimit.indexOf(".") > -1) {alert("单笔限制金额不允许有小数！");return false;}
	if (liqLimit.indexOf(".") > -1) {alert("结算满足的额度不允许有小数！");return false;}
	if (mdesc.length > 80) {alert("商户描述不能超过80个字");return false;}
	if (!isFigure(bankAcct)) {alert("银行账号只能是数字！");return false;}
	if (zip != '' && !isFigure(zip)) {alert("请输入正确的邮政编码！");return false;}
	if(merTradeType==""){alert("请选择商户所属行业！");return false;}
	if(idNo==""){alert("请填写证件号码！");return false;}
	if(openBkNo==""){alert("请填写开户银行行号！");return false;}
	if(corpName==""){alert("请填写法人姓名！");return false;}
	var minfo=getLxrMinfo("");
	//var minfo =getMinfoObject();//
	//检查输入数据的合法性
	if(!checkRelationPerson(minfo))return;
	return true;
}
//检查联系人输入的合法性
function checkRelationPerson(minfo){
	var roleArr=["主联系人","财务联系人","技术联系人","运行联系人","市场联系人","销售联系人"];
	var emailObj=function(i){return eval("(minfo.email"+i+")")};
	var phoneObj=function(i){return eval("(minfo.cell"+i+")")};
	var telObj=function(i){return eval("(minfo.tel"+i+")")};
	for(var i=0;i<roleArr.length;i++){
		if (emailObj(i) != '' && !isEmail(emailObj(i))) {
			alert(roleArr[i]+"的EMail地址输入有误，请输入正确的Email地址！");
			return false;
		}
		if (telObj(i) != '' && !isTel(telObj(i))) {			
			alert(roleArr[i]+"的固定电话输入有误，请输入正确的固定电话号码！");
			return false;
		}
		if (phoneObj(i) != '' && !isMobel(phoneObj(i))) {
			alert(roleArr[i]+"的手机号码输入有误，请输入正确的手机号码！");
			return false;
		}
	}
   return true;
}
//商户信息
function editMid(aType){
	mid = $("mid").value;
	if(mid==''){alert("请输入您要修改的商户号");return false;}
	if(!isFigure(mid)){alert("商户号格式不正确");return false;}
	//基本信息
	if(aType=='b'){
		$("editMerInfoLXRTB").style.display = 'none';
		$("eiditMinfoClass").style.display = 'none';
		MerMerchantService.getOneMinfo(mid,
				function(m){
					if(m==null){
						alert("商户不存在!"); 
						$("editedMsg").innerHTML = "请输入您要修改的商户号";
						$("editedMefId").value = '';
						$("editBaseMerForm").style.display = 'none';
						//$("editMerInfoTB").innerHTML='';
						return false;
					}else{
						$("editBaseMerForm").style.display = '';
						dwr.util.removeAllRows("editMerInfoTB");
						$("editedMsg").innerHTML = "商户基本信息修改";
						$("editedMefId").value = m.id;
						//set value begin
						dwr.util.setValues({
							name : m.name,
							abbrev : m.abbrev,
							prov_id : m.provId,
							signatory : m.signatory,
							begin_date : m.beginDate,
							exp_date : m.expDate,
							mer_chk_flag : m.merChkFlag,
							liq_type : m.liqType,
							liqPeriod : m.liqPeriod,
							trans_limit : m.transLimit/100,
							liq_limit : m.liqLimit, //这个字段不要除100
							bank_prov : m.bankProvId,
							bank_name : m.bankName,
							bank_branch : m.bankBranch,
							bank_acct : m.bankAcct,
							bank_acct_name : m.bankAcctName,
							open_date : m.openDate,
							begin_fee : m.beginFee/100,
							annual_fee : m.annualFee/100,
							caution_money : m.cautionMoney/100,
							fax_no : m.faxNo,
							corp_code : m.corpCode,
							reg_code : m.regCode,
							addr : m.addr,
							zip : m.zip,
							mdesc : m.mdesc,
							mstate : m.mstate,
							refund_flag : m.refundFlag,
							web_url : m.webUrl, 
							//default_fee_model : m.defaultFeeModel,
							merTradeType:m.merTradeType,
				            //default_fee_model : m.defaultFeeModel,
				            refundFee:m.refundFee
						},{ escapeHtml:true });
						//set value end
					}//else end
			
		});//fun end
	};
	//联系人信息
	if(aType=='c'){
		$("editBaseMerForm").style.display = 'none';
		$("eiditMinfoClass").style.display = 'none';
		MerMerchantService.getOneMinfo(mid,
				function(m){
					if(m==null){
						alert("商户不存在!");
						$("editedMsg").innerHTML = "请输入您要修改的商户号";
						$("editedMefId").value = '';
						return false;
					}else{
						$("editedMsg").innerHTML = "商户联系人信息修改";
						$("editedMefId").value = m.id;
						$("editMerInfoLXRTB").style.display = '';
						dwr.util.setValues({
							contact0 : m.contact0,
							tel0 : m.tel0,
							email0 : m.email0,
							phone0 : m.cell0,
							
							contact1 :m.contact1,
							tel1 : m.tel1,
							email1 :m.email1,
							phone1 : m.cell1,
							
							contact2 :m.contact2,
							tel2 : m.tel2,
							email2 :m.email2,
							phone2 : m.cell2,
							
							
							contact3 :m.contact3,
							tel3 : m.tel3,
							email3 :m.email3,
							phone3 : m.cell3,
							
							contact4 :m.contact4,
							tel4 : m.tel4,
							email4 : m.email4,
							phone4 : m.cell4,
							
							contact5 :m.contact5,
							tel5 : m.tel5,
							email5 :m.email5,
							phone5 : m.cell5
						});
						//set value end
					}
		});
	}
}


