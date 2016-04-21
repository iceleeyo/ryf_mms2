function init(){
	MerZHService.getMinfoPayPwd(function(msg){
		if(msg=='ok'){
			$("addTable").style.display='none';
			$("editTable").style.display='';
			}
		else{
			$("addTable").style.display='';
			$("editTable").style.display='none';
		}
	});
}

function add_pass(mid) {
	
	var oper_id = document.getElementById("oper_id").value;
	var pass = document.getElementById("pass").value;
	var vpass = document.getElementById("vpass").value;
	if(pass.trim()==''){
		alert("密码不能为空或空格！");
		return false;
	}
	if(pass.length<6||pass.length>15){
		alert("密码长度在6-15位！");
		return false;
	}
	if(vpass==''){
		alert("请确定新密码！");
		return false;
	}
	if(pass!=vpass){
		alert("两次密码不一致！");
		return false;
	}
	if(!isCharAndNum1(pass)){
		alert("新密码中必须包含字母和数字！");
		return false;
	}
	MerZHService.addMinfoPayPwd(mid,hex_md5(pass),callback);
}

function callback(msg) {
	alert(msg);
	window.location.href="M_35_ZFMMWH.jsp";
	
}
function edit_pass(mid) {
	
	var a_oper_id = document.getElementById("a_oper_id").value;
	var a_opass = document.getElementById("a_opass").value;
	var a_npass = document.getElementById("a_npass").value;
	var a_vnpass = document.getElementById("a_vnpass").value;
	if(a_npass.trim()==''){
		alert("密码不能为空或空格！");
		return false;
	}
	if(a_npass.length<6||a_npass.length>15){
		alert("密码长度在6-15位！");
		return false;
	}
	if(a_vnpass==''){
		alert("请确定新密码！");
		return false;
	}
	if(a_npass!=a_vnpass){
		alert("两次密码不一致！");
		return false;
	}
	if(!isCharAndNum1(a_npass)){
		alert("新密码中必须包含字母和数字！");
		return false;
	}
	if(a_opass==a_npass){
		alert("新密码不能和原密码一致！");
		return false;
	}
	MerZHService.editMinfoPayPwd(mid,hex_md5(a_opass),hex_md5(a_npass) ,callback);
}

