var currPage=1;
var tType={};
var flag=true;
function init(){
	PageParam.initMerInfo('add',function(l){	
	     dwr.util.addOptions("trade_type",l[2]);
	     dwr.util.addOptions("mstate",m_mstate);
	     tType=l[2];
	});
	
//	search(1);
	flag=false;
	
}

function search(pageNo){
	var uid=$("uid").value.trim();
	var mstate=$("mstate").value.trim();
	
	  if(flag){
		  //alert(uid);
		  AdminZHService.queryFXPZ(pageNo,null,0,null,callback);
	  }else{
		  //alert(uid);
		  AdminZHService.queryFXPZ(pageNo,uid,null,mstate,callback);
	  }
}
function callback(pageObj){
	
	$("fxpzTable").style.display="";  
	var cellFuns=[	
	            
        	function(obj){
        		return obj.uid==obj.aid?"<input type=\"button\" value=\"配置\" onclick=\"query4One('"+obj.aid+"')\"/>":"";},
			function(obj){ return obj.uid;},
			function(obj){ return obj.name;},
			function(obj){ return tType[obj.merTradeType];},
			function(obj){ return obj.aid;}, 
			function(obj){ return z_state[obj.state];},
			function(obj){ return obj.accMonthCount;},
			function(obj){ return div100(obj.accMonthAmt);},
			
			function(obj){ return obj.dkMonthCount;},
			function(obj){ return div100(obj.dkMonthAmt);}
					];
	currPage=pageObj.pageNumber; 
	paginationTable(pageObj,"resultList",cellFuns,"","search");
	}
function query4One(aid){
	var uid=$("uid").value;
	AdminZHService.getOneAid(uid,aid,callBack4One);
}
function callBack4One(alist){
	map=alist[0];
	jQuery("#configTable").wBox({title:"&nbsp;&nbsp;账户风险配置",show:true});
	dwr.util.setValues({a_uid:map.uid,  a_trade:merTradeType[map.mer_trade_type],  a_aid:map.aid,  a_aname:map.aname,
		a_mstate:map.state,
		//trans_limit:div100(map.trans_limit),  month_limit:div100(map.month_limit),
		acc_month_count:map.acc_month_count,  acc_month_amt:div100(map.acc_month_amt),
		dk_month_count:map.dk_month_count,dk_month_amt:div100(map.dk_month_amt)
	//	cz_fee_mode:map.cz_fee_mode,  tixian_fee_mode:map.tixian_fee_mode,
		//daifa_fee_mode:map.daifa_fee_mode,  daifu_fee_mode:map.daifu_fee_mode
	});
	
}

function query4OneSettlement(aid){
	AdminZHService.getOneAid(null,aid,function(alist){
		map=alist[0];
		dwr.util.setValues({b_uid:map.uid,b_daifu_fee_mode:map.daifu_fee_mode});
		jQuery("#settlementTable").wBox({title:"&nbsp;&nbsp;结算账户风险配置",show:true});
		});
}
function edit4Settlement(){
	var aid=$("b_uid").value;
	var daifuFeeMode = $("b_daifu_fee_mode").value ;
	if(daifuFeeMode==''){
		  alert("不能为空");
		  return;
	  }
	if(!checkFeeModel(daifuFeeMode)){
		alert("您输入的代付计费公式有问请参照计费公式说明重新输入！");
		return false;
	}
	AdminZHService.eidtConfigSettlement(aid,daifuFeeMode,callback4Edit);
}

function edit() {
	
	var aid=$("a_aid").value;
	var accMonthCount = $("acc_month_count").value ;
	var accMonthAmt = $("acc_month_amt").value ;
	var dkMonthCount = $("dk_month_count").value ;
	var dkMonthAmt = $("dk_month_amt").value ;
	var state=$("a_mstate").value;
	var pattern=/^(([1-9]\d*)|0)(\.\d{1,2})?$/;
	if(dkMonthCount==''||dkMonthAmt==''||accMonthCount==''||accMonthAmt==''){
		  alert("不能为空");
		  return;
	  }
	 
	if(!isInteger(accMonthCount))
	{
	    alert("同一收款卡号月累计成功次数必须为正整数，请重新输入！");
	    return false;
	}
	if(!pattern.exec(accMonthAmt))
	{
	    alert("同一收款卡号月累计成功金额格式不对，请重新输入！");
	    return false;
	}
	if(!isInteger(dkMonthCount))
	{
	    alert("同一扣款卡号月累计成功次数必须为正整数，请重新输入！");
	    return false;
	}
	if(!pattern.exec(dkMonthAmt))
	{
	    alert("同一扣款卡号月累计成功金额格式不对，请重新输入！");
	    return false;
	} 
		AdminZHService.eidtConfig(aid,state,accMonthCount,accMonthAmt*100,
				dkMonthCount,dkMonthAmt*100,callback4Edit);
	
}
function callback4Edit(msg) {
		if(msg=='ok'){
			alert("修改成功!");
			jQuery("#wBox_close").click();     
			window.location.href = "A_96_ZHFXPZ.jsp";
		}else{
			alert(msg);
		}
}

