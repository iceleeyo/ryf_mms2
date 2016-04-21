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
	 var obj=[11,12]; 
	 initGateChannel3(obj);
}

jQuery(function(){
	jQuery(":checkbox[name='check']").live('click', function() {
		var status = this.checked?"true":"false";
		var checkboxes=document.getElementsByName("check");
		for(var i = 0;i<checkboxes.length;i++){
			var status1 = checkboxes[i].checked?"true":"false";
			if(status == status1){
				continue;
			}else{
				if(!this.checked){
					jQuery("#toggleAll").attr("checked",this.checked);
				}
				return;
			}
		}
		jQuery("#toggleAll").attr("checked",this.checked);
	});
});
var mid=0;
function showMerMsg(){
   mid=document.getElementById("mid").value;
   if(mid==""){
	   alert("请输入商户号！");
	   $("minfoMsg").style.display="none";
	   return false;
   }
	   MerchantService.getImportentMsgByMid(mid, function(minfo) {
		if (minfo == null) {
			alert("商户不存在!");
			$("mid").value = '';
			$("minfoMsg").style.display = 'none';
			return false;
		} else {
			document.getElementById("minfoMsg").style.display = "";
			//初始化网关列表
			jQuery("#gateId").empty();
			dwr.util.addOptions("gateId",{'':'全部...'});
			jQuery("#gate").empty();
			dwr.util.addOptions("gate",{'':'全部...'});
			var new_gate={};
			selGate(minfo.merType,new_gate);
			dwr.util.addOptions("gate", new_gate);
			dwr.util.addOptions("gateId", new_gate);
//			initGateChannel3(obj);
			
			dwr.util.setValues({
				id : mid,
				name : minfo.name,
				abbrev : minfo.abbrev,
				category : minfo.category,
				code_exp_date : minfo.codeExpDate,
				begin_date : minfo.beginDate,
				exp_date : minfo.expDate,
				corp_code : minfo.corpCode,
				reg_code : minfo.regCode,
				liq_limit : minfo.liqLimit,
				bank_name : minfo.bankName,
				bank_acct : minfo.bankAcct,
				bank_branch : minfo.bankBranch,
				bank_prov_id : minfo.bankProvId,
				open_date : minfo.openDate,
				bank_acct_name : minfo.bankAcctName,
				corp_name : minfo.corpName,
				id_type : minfo.idType,
				id_no : minfo.idNo,
				open_bk_no : minfo.openBkNo,
				liq_obj : minfo.liqObj,
				liq_state : minfo.liqState,
				man_liq : minfo.manLiq,
				dlsCode : minfo.dlsCode,
				gateId : h_gate3[minfo.gateId],
				upmpMid:minfo.upmpMid,
				is_sgdf:minfo.isSgdf
			});
		}
	});
}

function isDlsCodeShow(o){
	if(o=='3'){
		$('dlsSpan').style.display='';
	}else{
		$('dlsSpan').style.display='none';
	};
}

function toggleAll(o){
	var status = o.checked;
	var checkboxes=document.getElementsByName("check");
	for(var i = 0;i<checkboxes.length;i++){
		checkboxes[i].checked=status;
	}
}

function setCondition(){
	dwr.util.setValue("hmid",dwr.util.getValue("mid").trim());
	dwr.util.setValue("hmname",dwr.util.getValue("mname").trim());
	dwr.util.setValue("hstatus",dwr.util.getValue("status").trim());
	$('toggleAll').checked = false;
}

//分页查询修改商户信息申请
function queryCheckPage(pageNo){
	var mid=dwr.util.getValue("hmid").trim();
	var mname=dwr.util.getValue("hmname").trim();
	var status=dwr.util.getValue("hstatus").trim();
	jQuery("#toggleAll").attr("checked",false);
	MerchantService.queryCheckPage(pageNo,mid,mname,status,callBack2);
}

//查询的回调函数
var callBack2 = function(pageObj){ 
	   document.getElementById("minfoMsg").style.display = "none";
	   $("checkTable").style.display="";
	   dwr.util.removeAllRows("resultList");
	   if(pageObj==null){
 	   document.getElementById("resultList").appendChild(creatNoRecordTr(13));
		   return;
	   }  
     var cellFuncs = [
                      function(obj) { return '<input align="left" name="check" type="checkbox" value='+obj.id+' />' ; },
                      function(obj) { return obj.mid; },
                      function(obj) { return obj.name; },
                      function(obj) { return getbankProvNameById(obj.provId); },
                      function(obj) { return obj.beginDate+'-'+obj.expDate; },
                      function(obj) { return obj.bankName; },
                      function(obj) { return obj.bankAcct; },
                      function(obj) { return obj.openDate;},
                      function(obj) { return obj.liqObj==0?'银行账户':obj.liqObj==1?'电银账户':'代理商'; },
                      function(obj) { return obj.liqState==0?'正常':'冻结'; },
                      function(obj) { return obj.manLiq==0?'关闭':'开启'; },
                      function(obj) { return obj.applyOperName; },
                      function(obj) { return obj.applyTimeStr; },
                      function(obj) { return obj.checkOperName; },
                      function(obj) { return obj.checkTimeStr; },
                      function(obj) { return obj.status==0?'未审核':obj.status==1?'审核通过':'审核失败'; },
                      function(obj) { return obj.status==0?'<a href="#" onclick="showCheckDetails('+obj.id+')">详细</a>&nbsp;&nbsp;<a href="#" onclick="doCheck(1,'+obj.id+',true);">审核通过</a>&nbsp;&nbsp;<a href="#" onclick="doCheck(2,'+obj.id+',true)">审核失败</a>':'<a href="#" onclick="showCheckDetails('+obj.id+')">详细</a>';}
                  ];
     		var status=dwr.util.getValue("hstatus").trim();
     		var str = '';
     		if(status=='0'){
     			str = '<input type="button"value="审核通过" onclick="doBatchCheck(1);" class="button" /><input type="button" value="审核失败" onclick="doBatchCheck(2);" class="button" />';
     		}
     		//ryt.js
           	paginationTable(pageObj,"resultList",cellFuncs,str,"queryCheckPage");
           	
   };

/**
 * @param mid
 * 获取详细的审核信息
 */
function showCheckDetails(id){
	MerchantService.showCheckDetails(id, function(minfoPair) {
		if(null==minfoPair){
			alert("记录不存在!");
		}else{
			var currentInfo= minfoPair.currentInfo;
			var applyInfo= minfoPair.applyInfo;
			document.getElementById("minfoMsg").style.display = "";
			//初始化网关列表 依据商户类型 初始化对公 对私网关列表
			jQuery("#gateId").empty();
			dwr.util.addOptions("gate",{'':'全部...'});
			var new_gate={};
			selGate(currentInfo.merType,new_gate);
			dwr.util.addOptions("gateId", new_gate);
			//当前值
			dwr.util.setValues({
				id : id,
				name : currentInfo.name,
				abbrev : currentInfo.abbrev,
				category : currentInfo.category,
				code_exp_date : currentInfo.codeExpDate,
				begin_date : currentInfo.beginDate,
				exp_date : currentInfo.expDate,
				corp_code : currentInfo.corpCode,
				reg_code : currentInfo.regCode,
				bank_name : currentInfo.bankName,
				bank_acct : currentInfo.bankAcct,
				bank_branch : currentInfo.bankBranch,
				bank_prov_id : currentInfo.bankProvId,
				open_date : currentInfo.openDate,
				bank_acct_name : currentInfo.bankAcctName,
				corp_name : currentInfo.corpName,
				id_type : currentInfo.idType,
				id_no : currentInfo.idNo,
				open_bk_no : currentInfo.openBkNo,
				open_bk_name : currentInfo.openBkName,
				liq_obj : currentInfo.liqObj,
				liq_state : currentInfo.liqState,
				man_liq : currentInfo.manLiq,
				gateId : h_gate3[currentInfo.gateId],
				upmpMid:currentInfo.upmpMid,
				is_sgdf : currentInfo.isSgdf,
				liqLimit : currentInfo.liqLimit
			});
			//申请值
			dwr.util.setValues({
				cname : (applyInfo.name==currentInfo.name?'':applyInfo.name),
				cabbrev : (applyInfo.abbrev==currentInfo.abbrev?'':applyInfo.abbrev),
				ccategory : (applyInfo.category==currentInfo.category?'':applyInfo.category==0?'RYF商户':applyInfo.category==1?'VAS商户':'POS商户'),
				ccode_exp_date : (applyInfo.codeExpDate==currentInfo.codeExpDate?'':applyInfo.codeExpDate?applyInfo.codeExpDate:'设置为空'),//组织结构代码有效期 可以为空
				ccontract_date : ((applyInfo.beginDate+''+applyInfo.expDate)==(currentInfo.beginDate+''+currentInfo.expDate)?'':(applyInfo.beginDate+'-'+applyInfo.expDate)),
				ccorp_code : (applyInfo.corpCode==currentInfo.corpCode?'':applyInfo.corpCode),
				creg_code : (applyInfo.regCode==currentInfo.regCode?'':applyInfo.regCode?applyInfo.regCode:'设置为空'),//工商登记号 可以为空
				cbank_name : (applyInfo.bankName==currentInfo.bankName?'':applyInfo.bankName),
				cbank_acct : (applyInfo.bankAcct==currentInfo.bankAcct?'':applyInfo.bankAcct),
				cbank_branch : (applyInfo.bankBranch==currentInfo.bankBranch?'':applyInfo.bankBranch),
				cbank_prov_id : (applyInfo.bankProvId==currentInfo.bankProvId?'':getbankProvNameById(applyInfo.bankProvId)),
				copen_date : (applyInfo.openDate==currentInfo.openDate?'':applyInfo.openDate),
				cbank_acct_name : (applyInfo.bankAcctName==currentInfo.bankAcctName?'':applyInfo.bankAcctName),
				ccorp_name : (applyInfo.corpName==currentInfo.corpName?'':applyInfo.corpName),
				cid_type_no : ((applyInfo.idType+''+applyInfo.idNo)==(currentInfo.idType+''+currentInfo.idNo)?'':(getidTypeNameById(applyInfo.idType)+' '+applyInfo.idNo)),
				copen_bk_no : (applyInfo.openBkNo==currentInfo.openBkNo?'':applyInfo.openBkNo+'-'+applyInfo.openBkName),
				cliq_obj : (applyInfo.liqObj==currentInfo.liqObj?'':applyInfo.liqObj==0?'银行账户':applyInfo.liqObj==1?'电银账户':'代理商'),
				cliq_state : (applyInfo.liqState==currentInfo.liqState?'':applyInfo.liqState==0?'正常':'冻结'),
				cman_liq : (applyInfo.manLiq==currentInfo.manLiq?'':applyInfo.manLiq==0?'关闭':'开启'),
				cgateId : (((''+applyInfo.gateId).substring(2) == (currentInfo.gateId + "").substring(2))?'':h_gate3[applyInfo.gateId]),
				cupmpMid:(applyInfo.upmpMid==currentInfo.upmpMid?'':applyInfo.upmpMid),
				cis_sgdf : (applyInfo.isSgdf == currentInfo.isSgdf  ? '' : (applyInfo.isSgdf == 0 ?'否':'是')),
				curLiqLimit:applyInfo.liqLimit == currentInfo.liqLimit  ? '':applyInfo.liqLimit,
				applyOperName:applyInfo.applyOperName,
				applyTime:applyInfo.applyTimeStr,
				checkOperName:applyInfo.checkOperName,
				checkTime:applyInfo.checkTimeStr
			});
			if(applyInfo.status==0){
				$('btn1').style.display='';
				$('btn2').style.display='';
			}else{
				$('btn1').style.display='none';
				$('btn2').style.display='none';
			}
		}
	});
}

/**
 * @param status
 * @param id
 * @param isEffectNow
 * 审核操作
 */
function doCheck(status,id,isEffectNow){
	var minfo={};
	if(!id){
		id=$('id').value;
	}
	minfo['id']=id;
	minfo['status']=status;
	if(status==1&&(isEffectNow || $('isEffectNow').checked)){
		if(isEffectNow&&!confirm("变更将会立即生效，如需设置生效时间，请取消后点'详细'")){
			return;
		}
		minfo['isEffectNow']=1;
	}else{
		var effectiveTime=$('effectiveTime').value;
		if(!effectiveTime&&status==1){
			alert("请选择生效时间或勾选立即生效复选框。");
			return;
		}
		minfo['effectiveTime']=effectiveTime;
	}
	if(status==1&&!confirm('确认通过该申请？')){
		return;
	}else if(status==2&&!confirm("确认不通过该审申请？")){
		return;
	}
	MerchantService.doCheck(minfo,function(msg){
		alert(msg);
		queryCheckPage(1);
	});
}

/**
 * 批量审核
 * 批量的申请id,成功或失败
 */
function doBatchCheck(status){
	var ids= new Array();
	var checkboxes=document.getElementsByName("check");
	for(var i = 0;i<checkboxes.length;i++){
		if(checkboxes[i].checked){
			ids.push(checkboxes[i].value);
		}
	}
	if(ids.length==0){
		alert("请选择要审核的申请");
		return;
	}
	if(!confirm("确认提交审核？")){
		return;
	}
	MerchantService.doBatchCheck(status,ids,function(msg){
		alert(msg);
		queryCheckPage(1);
	});
}

function getbankProvNameById(id){
	var options = document.getElementById("bank_prov_id").options;
	for(var i=0 ;i<options.length;i++){
		var option = options[i];
		if(option.value==id)return option.text;
	}
}

function getidTypeNameById(id){
	var options = document.getElementById("id_type").options;
	for(var i=0 ;i<options.length;i++){
		var option = options[i];
		if(option.value==id)return option.text;
	}
}

//修改商户重要信息申请
function updateMinfoData(){
	if(!confirm("提交申请？")){
		return;
	}
	var noBlankIds={
			"name":"商户全名",
			"abbrev":"商户简称",
		  	"begin_date":"合同开始日期",
		  	"exp_date":"合同结束日期",
		  	"reg_code":"工商登记号",
		  	"bank_name":"开户银行名",
		  	"bank_acct":"开户账号名",
		  	"bank_branch":"开户银行支行名",
		  	"bank_prov_id":"开户银行省份",
		  	"open_date":"开户日期",
		  	"bank_acct_name":"开户账号名",
		  	"corp_name":"法人姓名",
		  	"id_no":"证件号码",
		  	"open_bk_no":"开户银行行号" ,
		  	"gateId":"结算银行",
		  	"is_sgdf":"是否支持手工代付"
	};//不允许为空的Id
    if(!judgeBlankByIds(noBlankIds))return;
    if(!isFigure($("liq_limit").value)){alert("请填写正确的结算满足额度！");return false;};//商户结算满足额度，开通费，年费，保证金 不能为小数，只能为整数
    var minfo= wrapObj("minfoForm");
    MerchantService.applyUppdateMerImportantInfo(minfo,function(msg){
        if(msg=="ok"){
        	alert("修改成功！");
        }else{
           alert(msg);
        }
    });
}
// 检索银行行号
function SerchBankNoInfo() {
		jQuery("#serchBankNo").wBox({title : "&nbsp;&nbsp;联行号查询",show : true});
}

/**
 * @param pageNo
 * 分页查询
 */
function SerchBankNo(pageNo) {
	var gate = $("gate").value.trim();
	var bkname = $("bk_name").value.trim();
	MerchantService.queryBKNo(pageNo,gate,bkname,callBack1);

}
/**
 * @param pageObj
 * @returns
 * 分页查询回调函数
 */
var callBack1 = function(pageObj) {
	$("serach").style.display = "";
	dwr.util.removeAllRows("resultListbankno");
	if (pageObj == null) {
		document.getElementById("resultListbankno").appendChild(creatNoRecordTr(13));
		return;
	}
	var cellFuncs = [
			function(obj) {return "<input type='radio' onclick='canEditBut(" + obj.bkNo+ ")' gid='"+obj.gid+"' name='bankno' value='" + obj.bkNo + "'/>";}, 
			function(obj) {return obj.bkNo;},
			function(obj) {return obj.bkName;
			} ];
	paginationTable(pageObj, "resultListbankno", cellFuncs, "", "SerchBankNo");
};

function confirmbankNo() {
	var countArr = getValuesByName("bankno");
	if (countArr.length == 0) {
		alert("请选择一条记录！");
		return false;
	}
	dwr.util.setValues({
		open_bk_no : countArr
	});
	var gid = jQuery(jQuery("#resultListbankno :radio:checked")[0]).attr("gid");
	jQuery("#gateId").val("71"+gid);
	if("" == jQuery("#gateId").val()){
		jQuery("#gateId").val("72"+gid);
	}
	jQuery("#wBox_close").click();

}
//修改基本信息
function editBase(){
	var noBlankIds={
		/*  "prov_id":"所在省份",*/	 	
		  "liqPeriod":"结算周期", 	 		
		  "begin_fee":"商户开通费", 
		  "annual_fee":"商户年费",
		  "caution_money":"商户保证金", 	 
		  "signatory":"签约人",
		  "merTradeType":"商户所属行业 "
	};//不允许为空的Id
    if(!judgeBlankByIds(noBlankIds))return;
    //if(!isFigure($("liq_limit").value)){alert("请填写正确的结算满足额度！");return false;};//商户结算满足额度，开通费，年费，保证金 不能为小数，只能为整数
    if (!isFigure($("begin_fee").value)) {alert("请填写正确的商户开通费！");return false;}
    if (!isFigure($("annual_fee").value)) {alert("请填写正确的商户年费！");return false;}
    if (!isFigure($("caution_money").value)) {alert("请填写正确的商户保证金！");return false;}
	var minfo =  wrapObj("basicMinfoForm");
		minfo.id=mid;
		MerchantService.editMinfos(minfo,function(msg){
			alert(msg);
		});	
}
//修改联系人的信息
function editMinfoLXR(){	
	var minfo =getLxrMinfo("editedMefId");
	//检查输入数据的合法性
	if(!checkRelationPerson(minfo))return;
	MerchantService.editMinfoContact(minfo,function(msg){
		if(msg=='ok'){
			alert('修改成功');
		}else{
			alert(msg);
		}
	});
}

//=====================================================商户新增
//页面载入时初始化
function initadd() {

	PageParam.initMerInfo('add', function(l) {
		dwr.util.removeAllOptions("prov_id");
		dwr.util.addOptions("prov_id", {
			"" : "请选择…"
		});
		dwr.util.addOptions("prov_id", l[0]);

		dwr.util.removeAllOptions("bank_prov_id");
		dwr.util.addOptions("bank_prov_id", {
			"" : "请选择…"
		});
		dwr.util.addOptions("bank_prov_id", l[0]);

		dwr.util.removeAllOptions("liqPeriod");
		dwr.util.addOptions("liqPeriod", {
			"" : "请选择…"
		});
		dwr.util.addOptions("liqPeriod", l[1]);

		dwr.util.addOptions("merTradeType", l[2]);
	});
	PageService.getIdType(function(mapData) {
		dwr.util.addOptions("id_type", mapData);
	});
	 PageParam.initMerInfo('add', function(l) {
		dwr.util.removeAllOptions("pbkProvId");
		dwr.util.addOptions("pbkProvId", {"" : "请选择…"});
		dwr.util.addOptions("pbkProvId", l[0]);

	});
	PageParam.initQueryMinfo(function(data) {
		prov = data[0];
		map_merTradeType = data[3];
		map_idType = data[4];
	});
	  var obj=[11];
      initGateChannel3(obj);
}
// 增加新商户
function adminAddMinfo(){
	
	dwr.util.setEscapeHtml(false);
	if(checkMinfo()){
		//var minfo = getMinfoObject();
		var minfo=wrapObj("addMinfoForm");
		minfo.transLimit=minfo.transLimit*100;
		minfo.beginFee=minfo.beginFee*100;
		minfo.annualFee=minfo.annualFee*100;
		minfo.cautionMoney=minfo.cautionMoney*100;
		minfo.gateId=$("gateId").value.trim();
		MerchantService.addMinfos(minfo,"",function(mid){
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
  		MerchantService.editMinfoContact(minfo);
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
	minfo.cell0=dwr.util.getValue("cell0").trim();
	
	minfo.tel1=dwr.util.getValue("tel1").trim();
	minfo.contact1=dwr.util.getValue("contact1").trim();
	minfo.email1=dwr.util.getValue("email1").trim();
	minfo.cell1=dwr.util.getValue("cell1").trim();
	
	minfo.tel2=dwr.util.getValue("tel2").trim();
	minfo.contact2=dwr.util.getValue("contact2").trim();
	minfo.email2=dwr.util.getValue("email2").trim();
	minfo.cell2=dwr.util.getValue("cell2").trim();
	
	minfo.tel3=dwr.util.getValue("tel3").trim();
	minfo.contact3=dwr.util.getValue("contact3").trim();
	minfo.email3=dwr.util.getValue("email3").trim();
	minfo.cell3=dwr.util.getValue("cell3").trim();
	
	minfo.tel4=dwr.util.getValue("tel4").trim();
	minfo.contact4=dwr.util.getValue("contact4").trim();
	minfo.email4=dwr.util.getValue("email4").trim();
	minfo.cell4=dwr.util.getValue("cell4").trim();
	
	minfo.tel5=dwr.util.getValue("tel5").trim();
	minfo.contact5=dwr.util.getValue("contact5").trim();
	minfo.email5=dwr.util.getValue("email5").trim();
	minfo.cell5=dwr.util.getValue("cell5").trim();
	if(mid_id!=""){
		minfo.id = $(mid_id).value;
	}
	return minfo;
}





//校验商户信息
function checkMinfo(){
	var name = $("name").value.trim();
	var abbrev = $("abbrev").value.trim();
//	var liqType = $("liq_type").value.trim();
	var beginDate = $("begin_date").value.trim();
	var expDate = $("exp_date").value.trim();
	var signatory = $("signatory").value.trim();
//	var merChkFlag = $("mer_chk_flag").value.trim();
	var provId = $("prov_id").value.trim();
//	var areaId = $("exp_date").value.trim();
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
//	var corpCode = $("corp_code").value.trim();
//	var addr = $("addr").value.trim();
    var regCode = $("reg_code").value.trim();
	var zip = $("zip").value.trim();
//	var webUrl = $("web_url").value.trim();
	//var mstate = $("mstate").value.trim();
	var mdesc = $("mdesc").value.trim();
//	var refundFlag = $("refund_flag").value.trim();
	var merTradeType = $("merTradeType").value.trim();
	var idNo = $("id_no").value.trim();
	var openBkNo = $("open_bk_no").value.trim();
	var corpName = $("corp_name").value.trim();
//	var liqObj=$("liqObj").value.trim();
	var gate=$("gateId").value.trim();
	var contact0=$("contact0").value.trim();
	var tel0=$("tel0").value.trim();
	var cell0=$("cell0").value.trim();
	var email0=$("email0").value.trim();
	
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
	if (regCode==""){alert("请输入工商登记号");return false;}
	if (zip != '' && !isFigure(zip)) {alert("请输入正确的邮政编码！");return false;}
	if(merTradeType==""){alert("请选择商户所属行业！");return false;}
	if(idNo==""){alert("请填写证件号码！");return false;}
	if(openBkNo==""){alert("请填写开户银行行号！");return false;}
	if(corpName==""){alert("请填写法人姓名！");return false;}
	if(corpName.length >30){alert("法人名字不要超过30个字");return false;}
	if(!examine(corpName)){alert("请不要输入特殊字符");return false;}
	if(gate==0){alert("请选择结算银行！");return false;}
	if(contact0==""){alert("请填写主联系人姓名！"); return false;}
	if(tel0==""){alert("请填写主联系人电话！"); return false;}
	if(cell0==""){alert("请填写主联系人手机！"); return false;}
	if(email0==""){alert("请填写主联系人邮箱！"); return false;}
	var minfo=getLxrMinfo("");
	//var minfo =getMinfoObject();//
	//检查输入数据的合法性
	if(!checkRelationPerson(minfo))return;
	return true;
}
//检查联系人输入的合法性
function checkRelationPerson(minfo){
	var roleArr=["主联系人","财务联系人","技术联系人","运行联系人","市场联系人","销售联系人"];
	var emailObj=function(i){return eval("(minfo.email"+i+")");};
	var phoneObj=function(i){return eval("(minfo.cell"+i+")");};
	var telObj=function(i){return eval("(minfo.tel"+i+")");};
	var contactObj=function(i){return eval("(minfo.contact"+i+")");};
	for(var i=0;i<roleArr.length;i++){
		if(i == 0){//主联系人不能为空
			if (contactObj(i) == '' || contactObj(i).length>4 ||contactObj(i).length<2) {
				alert(roleArr[i]+"的名称输入有误，请输入正确的名称地址！");
				return false;
			}
			if (telObj(i) == '' || !isTel(telObj(i))) {			
				alert(roleArr[i]+"的固定电话输入有误，请输入正确的固定电话号码！");
				return false;
			}
			if (phoneObj(i) == '' || !isMobel(phoneObj(i))) {
				alert(roleArr[i]+"的手机号码输入有误，请输入正确的手机号码！");
				return false;
			}
			if (emailObj(i) == '' || !isEmail(emailObj(i))) {
				alert(roleArr[i]+"的EMail地址输入有误，请输入正确的Email地址！");
				return false;
			}
		}else{
			if (contactObj(i) != '' && (contactObj(i).length>4 ||contactObj(i).length<2)) {
				alert(roleArr[i]+"的名称输入有误，请输入正确的名称地址！");
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
			if (emailObj(i) != '' && !isEmail(emailObj(i))) {
				alert(roleArr[i]+"的EMail地址输入有误，请输入正确的Email地址！");
				return false;
			}
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
		MerchantService.getOneMinfo(mid,
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
							//liq_limit : m.liqLimit, //这个字段不要除100
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
							merType:m.merType,
							mstate : m.mstate,
							refund_flag : m.refundFlag,
							web_url : m.webUrl, 
							//default_fee_model : m.defaultFeeModel,
							merTradeType:m.merTradeType,
				            //default_fee_model : m.defaultFeeModel,
				            refundFee:m.refundFee,
				            gate_id : m.gateId
						},{ escapeHtml:true });
						//set value end
					}//else end
			
		});//fun end
	};
	//联系人信息
	if(aType=='c'){
		$("editBaseMerForm").style.display = 'none';
		$("eiditMinfoClass").style.display = 'none';
		MerchantService.getOneMinfo(mid,
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
							cell0 : m.cell0,
							
							contact1 :m.contact1,
							tel1 : m.tel1,
							email1 :m.email1,
							cell1 : m.cell1,
							
							contact2 :m.contact2,
							tel2 : m.tel2,
							email2 :m.email2,
							cell2 : m.cell2,
							
							
							contact3 :m.contact3,
							tel3 : m.tel3,
							email3 :m.email3,
							cell3 : m.cell3,
							
							contact4 :m.contact4,
							tel4 : m.tel4,
							email4 : m.email4,
							cell4 : m.cell4,
							
							contact5 :m.contact5,
							tel5 : m.tel5,
							email5 :m.email5,
							cell5 : m.cell5
						});
						//set value end
					}
		});
	}
}
//显示输入代理商号
function showdlscode(val){
	if(val=="3"){
		$("dlscdsp").innerHTML="<input type=\"text\" size=\"4\" id=\"dlsCode\" name=\"dlsCode\" maxlength=\"20\"/>";
	}else{
		$("dlscdsp").innerHTML="";
	}
}


/****
 * 依据商户类型 填充new_gate 网关列表
 * @param merType
 * @param new_gate
 */
function selGate(merType,new_gate){
	var condition="71";
	if(merType!=1){//merType==1 个人
		condition="72";
	}
	for(var properties in h_gate3){
		if(""+properties.indexOf(condition)!=-1){
			new_gate[properties]=h_gate3[properties];
		}
	}
	return;
}