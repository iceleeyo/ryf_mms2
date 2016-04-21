var trueimage=" <img src='../images/person/01123313.gif'> ";
var falesimage=" <img src='../images/person/01123314.gif'> ";


function registerSuccess(){
	var loginPwd=document.getElementById("loginPwd").value;
	var loginPwd2=document.getElementById("loginPwd2").value;
	var payPwd=document.getElementById("payPwd").value;
	var payPwd2=document.getElementById("payPwd2").value;
	
	var name=document.getElementById("name").value;
	var sex=document.getElementById("sex").value;
	var idNo=document.getElementById("idNo").value;
	
	var tel=document.getElementById("tel").value;
	
	if(validateLoginPwd(loginPwd)==1){document.getElementById("check_txtloginpwd").innerHTML=falesimage+"登录密码应由6－20个英文字母、数字或字符组成。";return false;}
	if(validateLoginPwd2(loginPwd2)==1){document.getElementById("check_txtloginpwd2").innerHTML=falesimage+"请再输入一遍您上面输入的登录密码。";return false;}
	if(validateLoginPwd2(loginPwd2)==2){document.getElementById("check_txtloginpwd2").innerHTML=falesimage+'两次输入交易密码不一致，请重新填写。';return false;}
	if(validatePayPwd(payPwd)==1){document.getElementById("check_txtpaypwd").innerHTML=falesimage+"交易密码应由6－20个英文字母、数字或字符组成。";return false;}
	if(validatePayPwd(payPwd)==2){document.getElementById("check_txtpaypwd").innerHTML=falesimage+'交易密码和登录密码不能相同，请重新设置交易密码。';return false;}
	if(validatePayPwd2(payPwd2)==1){document.getElementById("check_txtpaypwd2").innerHTML=falesimage+"请再输入一遍您上面输入的交易密码";return false;}
	if(validatePayPwd2(payPwd2)==2){document.getElementById("check_txtpaypwd2").innerHTML=falesimage+'两次输入交易密码不一致，请重新填写。';return false;}
	if(validateName(name)==1){document.getElementById("check_name").innerHTML=falesimage+"请重新输入，姓名格式不正确！";return false;}
	if(validateIdNo(idNo)==1){document.getElementById("check_idNo").innerHTML=falesimage+"请输入正确的身份证号码！";return false;}
	if(validatePhone(tel)==1){document.getElementById("check_tel").innerHTML=falesimage+"联系电话的长度必须在7-25位以内，如021-62407610 或 (021)62407610。";return false;}
	
	RegisterService.registerSuccess(hex_md5(loginPwd),hex_md5(payPwd),name,sex,idNo,tel);
}
function show(flag){
	if(flag==1){document.getElementById("check_txtloginpwd").innerHTML="由6－20个英文字母、数字或字符组成。为了您的账户安全，建议使用大小写字母与数字混和设置登录密码。";}
	if(flag==2){document.getElementById("check_txtloginpwd2").innerHTML="请再输入一遍您上面输入的登录密码。";}
	if(flag==3){document.getElementById("check_txtpaypwd").innerHTML="您在提取现金、网上付款时必须输入“交易密码。";}
	if(flag==4){document.getElementById("check_txtpaypwd2").innerHTML="请再输入一遍您上面输入的交易密码。";}
	if(flag==5){document.getElementById("check_idNo").innerHTML="请输入您的身份证号码。";}
	if(flag==6){document.getElementById("check_name").innerHTML="请输入您的姓名，长度在2到15之间。";}
	if(flag==12){document.getElementById("check_tel").innerHTML="请填写您的联系电话。";}
}
function check(flag){
	if(flag==1){
		var loginPwd=document.getElementById("loginPwd").value;
		if(validateLoginPwd(loginPwd)==1){document.getElementById("check_txtloginpwd").innerHTML=falesimage+"登录密码应由6－20个英文字母、数字或字符组成。";
		}else {document.getElementById("check_txtloginpwd").innerHTML=trueimage;}
	}
	if(flag==2){
		var loginPwd2=document.getElementById("loginPwd2").value;
		if(validateLoginPwd2(loginPwd2)==1){document.getElementById("check_txtloginpwd2").innerHTML=falesimage+"请再输入一遍您上面输入的登录密码。";
		}else if(validateLoginPwd2(loginPwd2)==2){document.getElementById("check_txtloginpwd2").innerHTML=falesimage+'两次输入交易密码不一致，请重新填写。';
		}else{document.getElementById("check_txtloginpwd2").innerHTML=trueimage;}
	}
	if(flag==3){
		var payPwd=document.getElementById("payPwd").value;
		if(validatePayPwd(payPwd)==1){document.getElementById("check_txtpaypwd").innerHTML=falesimage+"交易密码应由6－20个英文字母、数字或字符组成。";
		}else if(validatePayPwd(payPwd)==2){document.getElementById("check_txtpaypwd").innerHTML=falesimage+'交易密码和登录密码不能相同，请重新设置交易密码。';
		}else{document.getElementById("check_txtpaypwd").innerHTML=trueimage;}
	}
	if(flag==4){
		var payPwd2=document.getElementById("payPwd2").value;
		if(validatePayPwd2(payPwd2)==1){document.getElementById("check_txtpaypwd2").innerHTML=falesimage+"请再输入一遍您上面输入的交易密码";
		}else if(validatePayPwd2(payPwd2)==2){document.getElementById("check_txtpaypwd2").innerHTML=falesimage+'两次输入交易密码不一致，请重新填写。';
		}else{document.getElementById("check_txtpaypwd2").innerHTML=trueimage;}
	}
	if(flag==5){
		var idNo=document.getElementById("idNo").value;
		if(validateIdNo(idNo)==1){document.getElementById("check_idNo").innerHTML=falesimage+"请输入正确的身份证号码！";
		}else{document.getElementById("check_idNo").innerHTML=trueimage;}
	}
	if(flag==6){
		var name=document.getElementById("name").value;
		if(validateName(name)==1){document.getElementById("check_name").innerHTML=falesimage+"请重新输入，姓名格式不正确！";
		}else{document.getElementById("check_name").innerHTML=trueimage;}
	}
	if(flag==12){
		var tel=document.getElementById("tel").value;
		if(validatePhone(tel)==1){document.getElementById("check_tel").innerHTML=falesimage+"联系电话的长度必须在7-25位以内，如021-62407610 或 (021)62407610。";
		}else {document.getElementById("check_tel").innerHTML=trueimage;}
	}
}


//验证登录密码
function validateLoginPwd(obj){
if(obj != document.getElementById("loginPwd2").value)
{
document.getElementById("loginPwd2").value="";
document.getElementById("check_txtloginpwd2").innerHTML="";
}
var patn =   /^[^\s]{6,20}$/;
if(!patn.test(obj))return 1;
return 0;
}
//验证登录密码一致
function validateLoginPwd2(obj){
if(obj=="" )return 1;
if(document.getElementById("loginPwd").value!=obj) return 2;
return 0;
}
//验证交易密码
function validatePayPwd(obj){
if(obj != document.getElementById("payPwd2").value)
{
document.getElementById("payPwd2").value="";
document.getElementById("check_txtpaypwd2").innerHTML="";
}
var patn =   /^[^\s]{6,20}$/;
if(!patn.test(obj))return 1;
else if(obj=="" || document.getElementById("loginPwd").value == obj)return 2;
return 0;
}
//验证交易密码一致
function validatePayPwd2(obj){
if(obj=="")return 1;
if(document.getElementById("payPwd").value!=obj)return 2;
return 0;
}
//电话号码/^(0[0-9]{2,5}\-)?([2-9][0-9]{6,7})+(\-[0-9]{1,4})?$/
function validatePhone(obj){
	var patn =/[0-9()*-]{7,25}$/;
	if(!patn.test(obj))return 1;
	return 0;
	}
//身份证号验证
function validateIdNo(obj){
	var patn =/^(\d{15}|\d{17}[\dxX])$/;
	if(!patn.test(obj))return 1;
	return 0;
	}
//姓名验证
function validateName(obj){
	var patn =   /^[\w\u4e00-\u9fa5]+[\.\w\u4e00-\u9fa5]{1,15}$/;
	if(!patn.test(obj))return 1;
	return 0;
	}