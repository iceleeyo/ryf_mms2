var trueimage=" <img src='../images/person/01123313.gif'> ";
var falesimage=" <img src='../images/person/01123314.gif'> ";

function flushImg(){
	document.getElementById("img_check").src="../CheckCode?temp="+ (new Date().getTime().toString(36));
}
 function checkInput(obj){
	if(obj){
		var v = obj.value;
		if(v!='' && !isFigure(v)) obj.value='';
		}
}
function changeMsg()
{	
	document.getElementById("check_txtmobile").innerHTML="请输入您的手机号";
}

function checkTelIn(){
	 var txtMobile = $("txtMobile").value.trim();
	 if(!isMobel(txtMobile)){document.getElementById("check_txtmobile").innerHTML=falesimage+'手机号码必须以“13”、“15”、“18”开头，并为11位纯数字。';return ;}
	 RegisterService.checkTelIn(txtMobile,function(msg){
  	   if(msg == 'ok'){
  		 document.getElementById("check_txtmobile").innerHTML=trueimage+"恭喜，当前账号还没有被注册!";
             return false;
         }else{
        document.getElementById("check_txtmobile").innerHTML=falesimage+"对不起，您输入的账号已经被注册";
         }
     }); 
	 
  }

function checkTel(){
	 var phoneNo = $("txtMobile").value.trim();
	 if(isMobel(phoneNo)){
	 document.getElementById("check_txtmobile").innerHTML=trueimage;
	 }
	 else{
		 document.getElementById("check_txtmobile").innerHTML=falesimage+'手机号码必须以“13”、“15”、“18”开头，并为11位纯数字。';
	 }
}
function confirm(){
	var txtMobile=document.getElementById("txtMobile").value.trim();
	var txtCheckcode=document.getElementById("txtCheckcode").value;
	if(txtMobile==""){alert("手机号不能为空");return false;}
	if(!isMobel(txtMobile)){alert("手机号格式不正确");return false;}
	if(!document.getElementById("readed").checked){
		alert("您需要接受服务条款后才能注册！");
        document.getElementById("readed").focus();
        return false;
	}

	RegisterService.confirm(txtMobile,txtCheckcode);
}

function validateMobile(obj){
	var str = obj.value;
	var patn = /^1(3|5|8)\d{9}$/;
	if(!patn.test(str))return 1;
	return 0;
	}


	  
