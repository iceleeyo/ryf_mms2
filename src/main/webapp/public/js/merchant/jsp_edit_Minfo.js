  var prov = {};
  var map_merTradeType={} ;
  var map_idType = {};
  function init(mid){
       PageParam.initQueryMinfo(function(data){
	        prov = data[0];
			map_merTradeType=data[3];
			map_idType=data[4]; 
      });
		query4OneRLog(mid);
    }
  function query4OneRLog(mid) {

	    MerchantService.getOneMinfo(mid, function(Minfo) {
				dwr.util.setValues( {
					v_mid : Minfo.id,
					v_name : Minfo.name,
					v_abbrev : Minfo.abbrev,
					v_prov_id : prov[Minfo.provId],
					v_signatory : Minfo.signatory,
					v_begin_date : Minfo.beginDate,
					v_exp_date : Minfo.expDate,
				
					v_mer_chk_flag : Minfo.merChkFlag == 0 ? '不允许':'允许查询当天交易',
					v_liq_type : h_liq_type[Minfo.liqType],
					v_liq_period : h_liq_period[Minfo.liqPeriod],
					v_trans_limit : div100(Minfo.transLimit),
					v_liq_limit : Minfo.liqLimit,
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
					v_corp_code : Minfo.corpCode,
					v_reg_code : Minfo.regCode,
					v_addr : Minfo.addr,
					v_zip : Minfo.zip,
					v_mdesc : Minfo.mdesc,
					v_mstate : h_stat_flag[Minfo.mstate],
					v_refund_flag : Minfo.refundFlag == 0 ? '不允许' : "允许",
					//v_default_fee_model : Minfo.defaultFeeModel,
					v_web_url : Minfo.webUrl,
					codeExpDate: Minfo.codeExpDate,
					v_refund_fee:Minfo.refundFee==1?"退还":"不退还",
					category : Minfo.category==0?"普通商户":"特殊商户",
					v_corp_name: Minfo.corpName,
					v_mer_trade_type : map_merTradeType[Minfo.merTradeType],
					v_id_type:map_idType[Minfo.idType],
					v_id_no:Minfo.idNo,
					v_open_bk_no:Minfo.openBkNo,
					v_last_update: getNormalTime(Minfo.lastUpdate+"")
				});
				dwr.util.setValues( {
                  v_accSuccCount : Minfo.accSuccCount,
                  v_accFailCount : Minfo.accFailCount,
                  v_phoneSuccCount : Minfo.phoneSuccCount,
                  v_phoneFailCount : Minfo.phoneFailCount,
                  v_idSuccCount : Minfo.idSuccCount,
                  v_idFailCount : Minfo.idFailCount
              });     	
       
	         dwr.util.setValues( {
	             v_accSuccCount : Minfo.accSuccCount,
	             v_accFailCount : Minfo.accFailCount,
	             v_phoneSuccCount : Minfo.phoneSuccCount,
	             v_phoneFailCount : Minfo.phoneFailCount,
	             v_idSuccCount : Minfo.idSuccCount,
	             v_idFailCount : Minfo.idFailCount
	         });         
	         dwr.util.setValues( {                 
	             tel0 : Minfo.tel0,
	             tel1 : Minfo.tel1,
	             tel2 : Minfo.tel2,
	             tel3 : Minfo.tel3,
	             tel4 : Minfo.tel4,
	             tel5 : Minfo.tel5,
	             contact0 : Minfo.contact0,
	             contact1 : Minfo.contact1,
	             contact2 : Minfo.contact2,
	             contact3 : Minfo.contact3,
	             contact4 : Minfo.contact4,
	             contact5 : Minfo.contact5,
	             email0 : Minfo.email0,
	             email1 : Minfo.email1,
	             email2 : Minfo.email2,
	             email3 : Minfo.email3,
	             email4 : Minfo.email4,
	             email5 : Minfo.email5,
	             phone5:Minfo.cell5,
	             phone4:Minfo.cell4,
	             phone3:Minfo.cell3,
	             phone2:Minfo.cell2,
	             phone1:Minfo.cell1,
	             phone0:Minfo.cell0
	         });
     });
 }             