// 用正则表达式将前后空格，用空字符串替代。
	String.prototype.trim = function(){
	    return this.replace(/(^\s*)|(\s*$)/g, "");
	}

	function edit_pass(mid) {
		var oper_id = document.getElementById("oper_id").value;
		var opass = document.getElementById("opass").value;
		var npass = document.getElementById("npass").value;
		var vnpass = document.getElementById("vnpass").value;
		if(npass.trim()==''){
			alert("密码不能为空或空格！");
			return false;
		}
		if(npass.length<8||npass.length>15){
			alert("密码长度在8-15位！");
			return false;
		}
		if(vnpass==''){
			alert("请确定新密码！");
			return false;
		}
		if(npass!=vnpass){
			alert("两次密码不一致！");
			return false;
		}
		if(!isCharAndNum(npass)){
			alert("新密码中必须包含字母、数字和特殊字符！");
			return false;
		}
		if(opass==npass){
			alert("新密码不能和原密码一致！");
			return false;
		}
		MerchantService.editPass(mid,oper_id,hex_md5(opass),hex_md5(npass) ,callback);
	}
	
	function callback(msg) {
		alert(msg);
		document.getElementById("opass").value = "";
        document.getElementById("npass").value = "";
       document.getElementById("vnpass").value = "";
	}