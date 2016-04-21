var zh_flag =null;
var w_state={};

function init(){
    initMinfos();
	$("bdate").value = jsToday();
	$("edate").value = jsToday();
PageParam.initAdminStatePtype(function(list){
	  dwr.util.removeAllOptions("state");
	  dwr.util.addOptions("state", {"":"请选择..."});
	  dwr.util.addOptions("state",list[2]);
	  dwr.util.addOptions("mstate",m_mstate);
	  w_state=list[2];
	  
});
}
function query4ZH(){
	var uid=$("mid").value;
	if(uid==null||uid==""){
		 $("aid").value="";
		 $("aidValue").value="";
		return;
	}
		 AdminZHService.getJSZHByUid(uid,function(zh){
			 if(zh[uid]!=undefined){
				 $("aid").value=uid;
				 $("aidValue").value= zh[uid];
			 }else
			 $("aidValue").value="";
			 //zh_flag=zh;
		 });
}
function querySingleTransMoney(pageNo){
	var uid=$("mid").value;
	//var aid=$("aid").value;
	var bdate=$("bdate").value;
	var edate=$("edate").value;
	var state=$("state").value;
	var to_acc_no=$("to_acc_no").value;
	var other_id=$("other_id").value;
	var amount_num=$("amount_num").value;
	var mstate=$("mstate").value;
	var ym="";
	if(bdate=='') {
        alert("请选择起始日期！"); 
        return false; 
    }
    if(edate==''){
         alert("请选择结束日期！"); 
         return false; 
    }
    if(to_acc_no!=''&&!isNumber(to_acc_no)){
        alert("请填写正确的银行卡号！"); 
        return false; 
   }
    if(other_id=="acc_month_amt"||other_id=="trans_limit"){
    	if(!isMoney(amount_num)){
    		alert("请输入正确的金额形式");
    		return false; 
    	}
    }
    if(other_id=="acc_month_amt"){
//    	ym=$("YM").value;
    }
    if(other_id=="acc_month_count"){
    	if(!isInteger(amount_num)){
    		alert("请输入正确的成功次数");
    		return false; 
    	}
    }  
    if(other_id=="acc_month_amt"||other_id=="trans_limit"){amount_num=amount_num*100;}
    AdminZHService.querySingleTransMoney(pageNo,uid,uid,bdate,edate,state,to_acc_no,other_id,amount_num,ym,mstate,callBack);
}
var currPage=1;
var callBack = function(pageObj){
	 $("dbhkTable").style.display="";
	   var cellFuncs = [
	                  /*  function(obj) { return obj.tseq; },*/
	                    function(obj) { return obj.aid; },
	                    function(obj) { return obj.aname; },
	                    function(obj) { return obj.oid; },
	                    function(obj) { return div100(obj.transAmt); },
	                    //function(obj) { return div100(obj.payAmt); },
	                    //function(obj) { return obj.sysDate; },
	                    //function(obj) { return getStringTime(obj.sysTime); },
	                    function(obj) { return w_state[obj.state]; },
	                    //function(obj) { return w_ptype[obj.ptype]; },
	                    function(obj) {var touid=((obj.toUid=="null"||obj.toUid==null)?"":obj.toUid);var toaid=((obj.toAid==null||obj.toAid=="null")?"":obj.toAid);; return (touid==""&&toaid=="")?"":obj.toUid+"["+obj.toAid+"]"; },
	                    function(obj) { return getNormalTime(obj.initTime+""); },
	                    function(obj) { return obj.toAccName; },
	                    function(obj) { return obj.toAccNo; },
	                    function(obj) { return obj.toBkName; },
	                   
	                ]	
	                currPage=pageObj.pageNumber; 
	   			paginationTable(pageObj,"resultList",cellFuncs,"","querySingleTransMoney");
  }
function doCheck(flag){
	$("amount_num").disabled="";
	if(flag=="acc_month_amt"){dwr.util.setValue("unit",'元');/*AdminZHService.createMonth(function(res){$("month").innerHTML=res;});jQuery("#mo").css("display","");jQuery("#ri").css("display","none");jQuery("#month").css("display","");jQuery("#date1").css("display","none");jQuery("#date2").css("display","none");*/}
	if(flag=="trans_limit"){dwr.util.setValue("unit",'元');/*jQuery("#ri").css("display","");jQuery("#mo").css("display","none");jQuery("#month").css("display","none");jQuery("#date1").css("display","");jQuery("#date2").css("display","");*/}
	if(flag=="acc_month_count"){dwr.util.setValue("unit",'次(至少1次)');/*AdminZHService.createMonth(function(res){$("month").innerHTML=res;});jQuery("#mo").css("display","");jQuery("#ri").css("display","none");jQuery("#month").css("display","");jQuery("#date1").css("display","none");jQuery("#date2").css("display","none");*/}
	if(flag=="all"){$("amount_num").disabled="disabled";dwr.util.setValue("unit",'');/*jQuery("#month").css("display","none");jQuery("#ri").css("display","");jQuery("#mo").css("display","none");jQuery("#date1").css("display","");jQuery("#date2").css("display","");*/}
}
