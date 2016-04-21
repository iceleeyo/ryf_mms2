
//添加操作员
function add(action) {
	var oper_id = document.getElementById("oper_id").value.trim();
	var v_oper_pass = document.getElementById("v_oper_pass").value.trim();
	var oper_pass = document.getElementById("oper_pass").value.trim();
	var oper_name = document.getElementById("oper_name").value.trim();
	var oper_tel = document.getElementById("oper_tel").value.trim();
	var state = document.getElementById("state").value.trim();
	var oper_email = document.getElementById("oper_email").value.trim();
    var mid = document.getElementById("mid").value.trim();
    var oper_mid = document.getElementById("oper_mid").value.trim();
    
    if(mid == '' ){
        alert("请输入商户号或选择商户"); 
        return false;
    }
	if (checkMsg(oper_id,oper_name,oper_tel,oper_email,oper_pass,v_oper_pass)){
      if(!isFigure(mid)){
         alert("商户号只能为整数"); 
         document.all.mid.value = '';
         document.all.mid.focus();
         return false;
      }
      MerchantService.addOperInfo(action,state, oper_email, oper_tel, oper_name,hex_md5(oper_pass),oper_id, mid,oper_mid,callback);
    }
}

function callback(msg) {
	if(msg=='ok'){
		alert("增加成功！");
		var ids = new Array("oper_id","v_oper_pass","oper_pass","oper_name","oper_tel","oper_email");
		for(var i = 0 ; i< ids.length ; i++){
			var obj = document.getElementById(ids[i]);
			if(obj!=null){
				obj.value="";
			}
		}
	}else {
		alert (msg);		
	}
}
//检验输入是否合法
function checkMsg (oper_id,oper_name,oper_tel,oper_email,oper_pass,v_oper_pass) {
	if (oper_name == '') {
		alert("请填写操作员姓名");
		return false;
	}
	if (oper_id == '') {
		alert("请填写操作员号！");
		return false;
	}
	if (!isNumber(oper_id)) {
		alert("操作员号请用数字！");
		return false;
	}
	
	if (oper_id.length > 20) {
		alert("操作员号不能超过20位！");
		return false;
	}
	
	if (oper_name.length > 20) {alert("操作员姓名不能超过20个字！");return false;}
	if (oper_tel.length > 40) {alert("电话号码不能超过40位！");return false;}
	if (oper_email.length > 40) {alert("Email地址长度不能超过40位！");return false;}
	if (oper_email != '' && !isEmail(oper_email)) {alert("请输入正确的Email地址！"); return false;}
	if (!isFigure(oper_tel)) {alert("电话号码只能是数字！"); return false;}	
	if (oper_pass != null || v_oper_pass != null){
		if (oper_pass.trim() == '') {
			alert("密码不能为空或空格！");
			return false;
		}
		if (v_oper_pass == '') {
			alert("请确定密码！");
			return false;
		}
		if (oper_pass.length <8|| oper_pass.length > 15) {
			alert("密码为8-15位长度！");
			return false;
		}
		if (oper_pass != v_oper_pass) {
			alert("两次密码不一致！");
			return false;
		}
	}
	return true;
}
