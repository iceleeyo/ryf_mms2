var zh_flag =null;
var w_state={};
var w_ptype={};
var m_oid=null;
var m_oid2=null;
function init(){
	var mid=$("mid").value;
	if(zh_flag==null){
		 MerZHService.getZHUid2(mid,function(zh){
			 dwr.util.addOptions("aid", zh);
			zh_flag=zh;
		 })
	    }
	PageParam.initAdminStatePtype(function(list){
		  w_state=list[0];
		  w_ptype=list[1];
	  })
	MerZHService.queryWaitPay(1,call4FK);
}
var currPage=1;
var call4FK = function(pageObj){
	 $("mxTable").style.display="";
	   var cellFuncs = [
	                    function(obj) { return obj.aid; },
	                    function(obj) { return obj.aname; },
	                    function(obj) { return obj.oid; },
	                    function(obj) { return div100(obj.transAmt); },
	                    function(obj) { return div100(obj.payAmt); },
	                    //function(obj) { return obj.sysDate; },
	                   // function(obj) { return getStringTime(obj.sysTime); },
	                    function(obj) { return w_state[obj.state]; },
	                    function(obj) { return w_ptype[obj.ptype]; },
	                    function(obj) { return obj.toUid+"["+obj.toAid+"]"; },
	                    function(obj) { return getNormalTime(obj.initTime+""); },
	                   // function(obj) { return obj.tseq; },
	                    function(obj){return "<input type=\"button\" value=\"付款\" onclick=\"pay4One('"+obj.oid+"')\"/>" +
                    		"&nbsp;&nbsp;<input type=\"button\" value=\"取消订单\" onclick=\"cancel4One('"+obj.oid+"')\"/>";}
	                ]	
	                currPage=pageObj.pageNumber; 
	   			paginationTable(pageObj,"resultList",cellFuncs,"","queryMX");
  }

function  pay4One(oid){
	m_oid=oid;
	$("hidden_type").value ='B';
	jQuery("#table4").wBox({title:"&nbsp;&nbsp;请输入支付密码",show:true});
}

function cancel4One(oid,oid2){
	m_oid=oid;
	m_oid2=oid2;
	$("hidden_type").value ='C';
	jQuery("#table4").wBox({title:"&nbsp;&nbsp;请输入支付密码",show:true});
}
//查询本账户可用金额，和对方账户
function queryFK(){

	var aid=$("aid").value;
	var toUid=$("toUid").value;
	if(aid=='') {
        alert("请选择转出账户！"); 
        return false; 
    }
    if(toUid==''){
         alert("请选择对方融易付账户！"); 
         return false; 
    }
	
	MerZHService.queryFK(aid,toUid,callBack);
}
function callBack(obj){
	accInfos = obj[0];
	map = obj[1];
	
	if(accInfos.payFlag == 0 ){
		alert("该账户余额支付功能为关闭状态.");
		return;
	}
	if(accInfos.balance == 0 ){
		alert("该账户余额为0.");
		return;
	}else if(map!=null){
		$("table1").style.display="none";
		$("table2").style.display="";
		$("mxTable").style.display="none";
		$("table3").style.display="none";
		$("table4").style.display="none";
		
		$("a_aid").value=accInfos.aid+ "【" + accInfos.aname + "】";
		$("balance").value=div100(accInfos.balance);
		dwr.util.addOptions("toAid", map);
	}else{
		alert("该用户不存在，或者该用户没有可用的账户(不可以自己转给自己)");
	}
}
//查询对方账户的信息
function queryFK2(){
	var aid=$("aid").value;
	var toUid=$("toUid").value;
	var balance=$("balance").value;
	var payAmt=$("payAmt").value.trim();
	var toAid=$("toAid").value;
	var pattern=/^(([1-9]\d*)|0)(\.\d{1,2})?$/;       
	
	if(payAmt==''||payAmt<=0){
        alert("付款金额不能为空，为0或小于0！"); 
        return false; 
    }
	if(toAid==''){
         alert("请选择对方融易付账户！"); 
         return false; 
    }
	if(!pattern.exec(payAmt))
	{
	    alert("付款金额格式不对，请重新输入！");
	    return false;
	}
	if(payAmt*100>balance*100){
        alert("付款金额不能大于可用金额！"); 
        return false; 
    }
	MerZHService.queryToAid(toAid,callBack1);
		
}
function callBack1(obj){
	$("table1").style.display="none";
	$("table2").style.display="none";
	$("table3").style.display="";
	$("mxTable").style.display="none";
	$("table4").style.display="none";
	$("b_aid").innerHTML=$("a_aid").value;
	$("b_payAmt").innerHTML=$("payAmt").value.trim()+"元";
	$("b_toUid").innerHTML=$("toUid").value;
	$("b_toAid").innerHTML=obj.aid+ "【" + obj.aname + "】";
}
//点击确定支付进行订单生成
function qdzf(){
	var aid=$("aid").value;
	var mid=$("mid").value;
	var toAid=$("toAid").value;
	var toUid=$("toUid").value;
	var payAmt=$("payAmt").value.trim();
	MerZHService.genTrOrders(aid,mid,toAid,toUid,payAmt,function(list){
	m_oid=list[0];
	m_oid2=list[1];
	            });
	$("hidden_type").value ='A';
	jQuery("#table4").wBox({title:"&nbsp;&nbsp;请输入支付密码",show:true});
	
}
//输入密码后，执行的确定
function edit(){
	var aid=$("aid").value;
	var toAid=$("toAid").value;
	var toUid=$("toUid").value;
	var payAmt=$("payAmt").value.trim();
	var pwd=$("pwd").value;
	var mid=$("mid").value;
	var hidden_type=$("hidden_type").value;
	if(hidden_type=='A') {MerZHService.qdzf(aid,mid,toAid,toUid,payAmt,hex_md5(pwd),m_oid,m_oid2,call);}
	if(hidden_type=='B') {MerZHService.pay4One(mid,m_oid,hex_md5(pwd),call);}
	if(hidden_type=='C') {MerZHService.cancel4One(mid,m_oid,m_oid2,hex_md5(pwd),call);}
	 
}

function call(msg){
	alert(msg);
	jQuery("#wBox_close").click();
	window.location.href="M_36_WYFK.jsp";
}

function fanHui(){
	$("table1").style.display="";
	$("mxTable").style.display="";
	$("table2").style.display="none";
	$("table3").style.display="none";
	$("table4").style.display="none";
	dwr.util.removeAllOptions("toAid");
	$("payAmt").value="";



}

function fanHui2(){
	$("table1").style.display="none";
	$("mxTable").style.display="none";
	$("table2").style.display="";
	$("table3").style.display="none";
	$("table4").style.display="none";
}
