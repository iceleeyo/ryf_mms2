var zh_flag =null;
function init(){ 
	var mid=$("mid").value;
	if(zh_flag==null){
		 MerZHService.getZHUid2(mid,function(zh){
			 dwr.util.addOptions("aid", zh);
			zh_flag=zh;
		 });
	    };
}

function queryZH(){

	var aid=$("aid").value;
	if(aid=='') {
        alert("请选择转出账户！"); 
        return; 
    }
	MerZHService.queryZH(aid,callBack);
}


function callBack(obj){
	if(obj.balance==0){
		alert("该账户余额为0.");
		return ;
	}else{
		$("table1").style.display="none";
		$("table2").style.display="";
		
		$("a_aid").value=obj.aid+ "【" + obj.aname + "】";
		$("balance").value=div100(obj.balance);
	}
}

/***
 * 点击确认 显示输入密码框 确认提现
 * @returns {Boolean}
 */
function show_surePwd(){
	var mid=$("mid").value;
	var aid=$("aid").value;
	var balance=$("balance").value;
	var payAmt=$("payAmt").value.trim();
	var pattern=/^(([1-9]\d*)|0)(\.\d{1,2})?$/;       
	
	if(payAmt==''||payAmt<=0){
        alert("提现金额错误，请输入正确的金额！"); 
        return false; 
    }
	if(!isMoney(payAmt))
	{
	    alert("提现金额格式不对，请重新输入！");
	    return false;
	}
	if(payAmt*100>balance*100){
        alert("提现金额不能大于可用金额！"); 
        return false; 
    }
	MerZHService.queryTXFee(aid,payAmt,function(res){
		var d=res.split(",");
		$("sumAmt").innerHTML=payAmt+"元";
		$("fee_amt").innerHTML=""+d[0]+"元";
		$("bankName").innerHTML=d[1];
		$("bankNo").innerHTML=d[2];
	});
	$("table1").style.display="none";
	$("table2").style.display="none";
	jQuery("#table3").wBox({title:"&nbsp;&nbsp;请输入支付密码",show:true});
}

function editTX(){
	var mid=$("mid").value;
//	var aid=$("aid").value;//结算帐户，不需传帐户id
	var balance=$("balance").value;
	var payAmt=$("payAmt").value.trim();
	var pwd=$("pwd").value;
	var pattern=/^(([1-9]\d*)|0)(\.\d{1,2})?$/;       
	if(confirm("确认支付？")){
		MerZHService.editTX_N(mid,mid,payAmt,hex_md5(pwd),callBack1);//结算帐户，不需传帐户id yang.yaofeng
	}
		
}
function callBack1(obj){
	if(obj!="操作成功"){
		alert(obj);
		return;
	}
	alert(obj);
	$("table1").style.display="";
	$("table2").style.display="none";
	$("aid").value="";
	$("payAmt").value="";
	$("fee").innerHTML="";
	window.location.href="M_34_ZHTX.jsp";
}
function fanhui(){
	$("table1").style.display="none";
	$("table2").style.display="";
	$("table3").style.display="none"
	/*$("aid").value="";
	$("payAmt").value="";
	$("fee").innerHTML="";*/
}

function fanhui2(){
	$("table1").style.display="";
	$("table2").style.display="none";
	$("table3").style.display="none"
}