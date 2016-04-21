
function queryMinfos(pageNo) {
	var mid = $("mno").value;
	var name = $("name").value;
	var prov = $("prov").value;
	var liqPeriod = $("liqPeriod").value;
	var liq_type = $("liq_type").value;
	var stat_flag = $("stat_flag").value;
	if (mid != '') {
		mid = mid.trim();
		if (!isNumber(mid)) {
			alert("商户号必须是数字！");
			return false;
		}
	}
	MerchantService.getMinfos(mid, name, prov, liqPeriod, liq_type, stat_flag,pageNo, callBack);
}
var callBack = function(minfoList) {	
	$("xiangxi").style.display = "none";
	$("contactMsg").style.display = "none";
	$("minfoList").style.display = '';
	if(minfoList.length==0){
		 document.getElementById("minfoList").appendChild(creatNoRecordTr(9));
		  return false;
	}
  var cellFuncs = [
             function(obj) { return obj.id; },
             function(obj) { return obj.name; },
             function(obj) { return prov[obj.provId]; },
             function(obj) { return obj.expDate == 0 ? '' : obj.expDate; },
             function(obj) { return obj.bankName; },
             function(obj) { return obj.bankAcct; },
             function(obj) { return obj.openDate;},
             function(obj) { return obj.mstate == 0 ? "正常" : "关闭"; },
             function(obj) { return obj.liqObj == 0 ? "银行账户": obj.liqObj == 1? "电银账户":"代理商"; },
             function(obj) { return obj.liqState == 0 ? "正常" : "冻结"; },
             function(obj) { return obj.manLiq == 0 ? "关闭" : "开启"; },
             function(obj) { return "<input type=\"button\" value=\"详细\"  onclick=\"query4OneRLog('"+ obj.id + "');\" >";}	                  
         ]
  paginationTable(minfoList,"minfoListBody",cellFuncs,"","queryMinfos");
}
var liqMap = {};
function query4OneRLog(mid) {
	var mtye=["企业","个人","集团"];
	$("minfoList").style.display = 'none';
	$("xiangxi").style.display = "";
	$("contactMsg").style.display = "";
	MerchantService.getOneMinfo(mid, function(d) {
		Minfo = d;
		dwr.util.setValues( {
			v_mid : Minfo.id,
			v_name : Minfo.name,
			v_abbrev : Minfo.abbrev,
			v_prov_id : prov[Minfo.provId],
			v_signatory : Minfo.signatory,
			v_begin_date : Minfo.beginDate,
			v_exp_date : Minfo.expDate
		});
		dwr.util.setValues( {
			v_mer_chk_flag : Minfo.merChkFlag == 1 ? '允许查当天交易' : (Minfo.merChkFlag==2 ? "允许查当天和历史交易" : '不允许'),
			v_liq_type : liqType[Minfo.liqType],
			v_liq_period : liqPeriod[Minfo.liqPeriod],
			v_trans_limit : div100(Minfo.transLimit) == 0? '' : div100(Minfo.transLimit),
			v_liq_limit : Minfo.liqLimit
		});
		dwr.util.setValues( {
			v_bank_prov_id : prov[Minfo.bankProvId],
			v_bank_name : Minfo.bankName,
			v_bank_branch : Minfo.bankBranch,
			v_bank_acct : Minfo.bankAcct,
			v_bank_acct_name : Minfo.bankAcctName
		});
		dwr.util.setValues( {
			v_open_date : Minfo.openDate,
			v_begin_fee : div100(Minfo.beginFee),
			v_annual_fee : div100(Minfo.annualFee),
			v_caution_money : div100(Minfo.cautionMoney),
			v_fax_no : Minfo.faxNo,
			v_corp_code : Minfo.corpCode
		});
		dwr.util.setValues( {
			v_reg_code : Minfo.regCode,
			v_addr : Minfo.addr,
			v_zip : Minfo.zip,
			v_mdesc : Minfo.mdesc,
			v_isPtop : Minfo.isPtop,
			v_userId : Minfo.userId,
			merType : mtye[Minfo.merType],
			v_mstate : Minfo.mstate==0 ? '正常' : '关闭',
			v_refund_flag : Minfo.refundFlag == 0 ? '不允许' : "允许",
			v_web_url : Minfo.webUrl,
			merTradeType :map_merTradeType[Minfo.merTradeType],
			refundFee : Minfo.refundFee==1?"退还":"不退还" , 
			category :mer_category_map[Minfo.category],
			codeExpDate: Minfo.codeExpDate,
			v_corp_name: Minfo.corpName,
			v_id_type:map_idType[Minfo.idType],
			v_id_no:Minfo.idNo,
			v_open_bk_no:Minfo.openBkNo,
			v_last_update: getNormalTime(Minfo.lastUpdate+""),
			liq_obj: Minfo.liqObj == 0 ? "银行账户": Minfo.liqObj == 1? "电银账户":"代理商",
			liq_state: Minfo.liqState==0?"正常" :"冻结",
			man_liq: Minfo.manLiq==0?"关闭" :"开启",
			v_gateId: h_gate2[Minfo.gateId]
		});
		dwr.util.setValues( {
           // v_default_fee_model : Minfo.defaultFeeModel,
			contact0 : Minfo.contact0,
			tel0 : Minfo.tel0,
			email0 : Minfo.email0,
			phone0 : Minfo.cell0,
			
			contact1 :Minfo.contact1,
			tel1 : Minfo.tel1,
			email1 :Minfo.email1,
			phone1 : Minfo.cell1,
			
			contact2 :Minfo.contact2,
			tel2 : Minfo.tel2,
			email2 :Minfo.email2,
			phone2 : Minfo.cell2,
			
			contact3 :Minfo.contact3,
			tel3 : Minfo.tel3,
			email3 :Minfo.email3,
			phone3 : Minfo.cell3,
			
			contact4 :Minfo.contact4,
			tel4 : Minfo.tel4,
			email4 : Minfo.email4,
			phone4 : Minfo.cell4,
			
			contact5 :Minfo.contact5,
			tel5 : Minfo.tel5,
			email5 :Minfo.email5,
			phone5 : Minfo.cell5
		});
	});
}

var prov = {};
var liqPeriod={};
var liqType = {};
var map_merTradeType={};
var map_idType={};
var a = [{name:'',text:'全部'}];
function queryBegin() {
	PageParam.initQueryMinfo(function(d){
		 prov = d[0];
	     dwr.util.removeAllOptions("prov");
	     dwr.util.addOptions("prov", a,'name','text',{escapeHtml:false});
	     dwr.util.addOptions("prov", d[0]);
	     liqPeriod = d[1];
         dwr.util.removeAllOptions("liqPeriod");
         dwr.util.addOptions("liqPeriod", a,'name','text',{escapeHtml:false});
         dwr.util.addOptions("liqPeriod", d[1]);
         liqType = d[2];
         dwr.util.removeAllOptions("liq_type");
         dwr.util.addOptions("liq_type", a,'name','text',{escapeHtml:false});
         dwr.util.addOptions("liq_type", d[2]);
         map_merTradeType=d[3];
         map_idType=d[4];
	})
	var obj=[11,12];
    initGateChannel2(obj);
	
}
function showView() {
	document.getElementById("minfoList").style.display = '';
	document.getElementById("xiangxi").style.display = "none";
	document.getElementById("contactMsg").style.display = "none";
}			