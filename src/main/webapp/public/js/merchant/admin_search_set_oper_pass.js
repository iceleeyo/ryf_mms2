	
	function searchOpersInfo(pageNo){
		 var mid = document.getElementById("mid").value.trim();
		   var operName = document.getElementById("oper_name").value.trim();
		   if(mid == '' && operName == ''){alert("至少输入一个查询条件");return false;}
	     if(mid != '' && !isFigure(mid)){
	           alert("商户号只能为整数"); 
	           return false;
	     }
	     MerchantService.getOpers4Object(mid,operName,pageNo,callback);
	}
	var callback = function(operInfoList){
	      document.getElementById("body4List").style.display="";
	       if(operInfoList.length==0){
	    		document.getElementById("operInfoBody").appendChild(creatNoRecordTr(8));
	          return false;
	       }
	        var cellFuncs = [
                   function(OperInfo) { return OperInfo.mid; },
                   function(OperInfo) { return OperInfo.operId; },
                   function(OperInfo) { return OperInfo.operName; },
                   function(OperInfo) { return OperInfo.operTel; },
                   function(OperInfo) { return OperInfo.operEmail; },
                   function(OperInfo) { return OperInfo.regDate; },
                   function(OperInfo) { return OperInfo.state==0 ? '正常' : '关闭'; },
                   function(OperInfo) { return "<input type=\"button\" value=\"密码重置\" onclick=\"editPass("+OperInfo.mid+"," + OperInfo.operId + ") ;\" />"; }      
               ] 
        paginationTable(operInfoList,"operInfoBody",cellFuncs,"","searchOpersInfo");
	 }
	 function editPass(mid,operId){
	   document.getElementById("queryTJ").style.display="none";
     document.getElementById("body4List").style.display="none";
     document.getElementById("body4One").style.display="";
     document.getElementById("v_npass").value="";
     document.getElementById("v_vnpass").value="";
     dwr.util.setValues({v_mid : mid , v_oper_id : operId});
	 }	 
	 function edit_pass() {
	    var mid = document.getElementById("v_mid").value;
		var oper_id = document.getElementById("v_oper_id").value;
		var npass = document.getElementById("v_npass").value;
		var vnpass = document.getElementById("v_vnpass").value;
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
		MerchantService.setPass(mid,oper_id,hex_md5(npass),callback4Edit);
	}
		
	function callback4Edit(msg) {
		if(msg != undefined){
		   alert(msg);
		}
		document.getElementById("body4One").style.display="none"; 
      document.getElementById("queryTJ").style.display="";
      document.getElementById("body4List").style.display="";
	}	  