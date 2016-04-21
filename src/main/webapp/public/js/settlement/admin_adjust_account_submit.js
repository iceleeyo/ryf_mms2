    function hideTable(){
    	document.getElementById("adjustAccount").style.display="none";
    }
 	function checkParam(){
            var mid = document.getElementById("mid").value;
            if(mid == ''){
                 alert("请输入商户号!");
                 return false;
            }
            if(!mid=='' && !isFigure(mid)){
                alert("商户号只允许为整数，请重新输入！");
                document.getElementById("mid").value = '';
                return false;
            }
            if(m_minfos[mid]==undefined){
            	 alert("商户号不存在，请重新输入！");
            	 document.getElementById("mid").value = '';
            	 return false;
            }
            return true;
     }
 	var mAccount=0;
 	function getAccount(){
 		if(!checkParam())return;
 		document.getElementById("adjustAccount").style.display="";
 		var mid= document.getElementById("mid").value ;
 		 SettlementService.getBalanceById(mid,callback);
 		 function callback(account){
 			mAccount=account;
 			 document.getElementById("minfo").innerHTML=m_minfos[mid];
 			 document.getElementById("bAcount").innerHTML=account;
 		 }
 	}
 	
    function submitAdjust(){
        var editAcc = document.getElementById("edit_acc_id").value;
        var mid=document.getElementById("mid").value;
        if(editAcc==''){
            alert("请输入调账金额!");
            return false;
        }
        var pattern=/^\d{1,}\.\d{2}$/;       
		if(!pattern.exec(editAcc))
		{
		    alert("调账金额格式不对，请重新输入!");
		    return false;
		}
        var operType = document.getElementById("edit_type_id").value;
        var reason = document.getElementById("reason").value;
        
        if(reason==''){
            alert("请输入调账原因!");
            return false;
        }
        if(reason.length > 255){
            alert("调账原因不得超过255个字");
            return false;
        }
        if(reason=="退款手续费退回"||reason=="TKSXFTH"){
        	alert("此原因为系统使用，请更改调账原因！");
        	return false;
        }
        SettlementService.adjustAccount(mid,operType,mAccount,editAcc,reason,loginMid,loginUid ,function(data){
            if(data=='ok'){
                alert("调账请求成功，请等待审核!");
                location.href = "A_26_SGDZQQ.jsp?userAuthIndex=26";
	        }else{
	            alert(data);
	        }
        
        });
    }
    
    /**
     * 页面切换
     * @param flag
     */
    function tabChange(flag){
    	if(flag==0){
	    	$("adjustAccount").style.display="block";
	    	$("adjustAccounts").style.display="none";
	    	resetInput();
    	}
    	if(flag==1){
    		$("adjustAccount").style.display="none";
	    	$("adjustAccounts").style.display="block";
	    	resetInput();
    	}
    }
    
    var wait=60;
    /**
     * 获取手机验证码
     */
    function getvfCode(flag){
    	wait=60;
    	var getvf_span=$("getVefCode");
    	if(flag==1){
    		$("subbt_SGDZ").removeAttribute("disabled");
    		getvf_span=$("getVefCode_");
    	}
    	if(flag==2){
    		$("subbt_SGDZS").removeAttribute("disabled");	
    		getvf_span=$("getVefCodes");
    	}
    	getvf_span.setAttribute("disabled", true);
    	SettlementService.getVfCode();
    	time(getvf_span);
    }
    /**
     * 倒计时
     * @param getvf_span
     */
    function time(getvf_span) {
    	if (wait == 0) {
			getvf_span.removeAttribute("disabled");			
			getvf_span.value="点此发送验证码";
			wait = 60;
		} else {
			getvf_span.setAttribute("disabled", true);
			getvf_span.value=wait+"秒后可以重新发送";
			wait--;
			setTimeout(function() {
				time(getvf_span);
			},
			1000);
		}
	}
    /**
     * 验证手机验证码
     */
    function verify_Code(){
    	var code=$("verifyCode").value;
    	if(code==""||code==undefined){
    		alert("请输出手机验证码！");
    		return;
    	}
    	SettlementService.verifyCode(code,function(data){
    		if(data==1){
    			alert("验证码错误！");
    			return;
    		}
    			$("verify_table").style.display="none";
    			$("selectmid_table").style.display="block";
    	});
    }
    function submitSGDZ(){
    	var code=$("verifyCode_submit").value;
    	var mid=$("mid").value;
    	var type=$("edit_type_id").value;
    	var edit_amt=$("edit_amt").value;
    	var edit_fee=$("edit_fee").value;
    	var edit_tbname=$("edit_tbname").value;
    	var edit_tseq=$("edit_tseq").value;
    	var edit_remrk=$("edit_remrk").value;
    	var pattern=/^\d{1,}\.\d{2}$/;     
    	if(mid==""){
    		alert("请输入商户号！");
    		return;
    	}
		if(edit_amt==""||edit_fee==""||edit_tbname==""||edit_tseq==""){
				alert("请检查信息是否输入完整！");
				return;
		  }
		if(code==""){
			alert("请输入手机验证码！");
			return;
		}
 		if(!pattern.exec(edit_amt))
 		{
 		    alert("调账金额格式不对，请重新输入!");
 		    return;
 		}
 		if(!pattern.exec(edit_fee))
 		{
 		    alert("调账手续费格式不对，请重新输入!");
 		    return;
 		}
 		SettlementService.accountSGDZ(code,mid,type,edit_amt,edit_fee,edit_tbname,edit_tseq,edit_remrk,function(flag){
 			if(flag==0){
 				alert("操作成功！");
 				tabChange(0);
 				return;
 			}else{
 				alert("操作失败！");
 	 			tabChange(0);
 			}
 		});
    } 
    /**
     * 修改商户余额
     */
    function submitSHYE(){
    	var code=$("verifyCode_submits").value;
    	var mid=$("mid").value;
    	var type=$("edit_type_ids").value;
    	var edit_amt=$("edit_amts").value;
    	var pattern=/^\d{1,}\.\d{2}$/;    
    	if(mid==""){
    		alert("请输入商户号！");
    		return;
    	}
		if(edit_amt==""){
				alert("请输入金额！");
				return;
		  }
		if(code==""){
			alert("请输入手机验证码！");
			return;
		}
 		if(!pattern.exec(edit_amt))
 		{
 		    alert("调账金额格式不对，请重新输入!");
 		    return;
 		}
 		SettlementService.accountAmtChange(code,mid,type,edit_amt,function(flag){
 			if(flag==0){
 				alert("操作成功！");
 				tabChange(0);
 				return;
 			}else{
 				alert("操作失败！");
 	 			tabChange(0);
 			}
 		});
    } 
    function resetInput(){
    	 $("verifyCode_submit").value="";
    	 $("edit_amt").value="";
    	 $("edit_fee").value="";
    	 $("edit_tbname").value="";
    	 $("edit_tseq").value="";
    	 $("edit_remrk").value="";
    	 $("verifyCode_submits").value="";
    	 $("edit_amts").value="";
    }
    /**
     * 手工调帐查询商户信息 （A_116_SGDZ.JSP）
     */
    function getAccounts(){
 		if(!checkParam())return;
 		document.getElementById("adjustAccount").style.display="";
 		document.getElementById("selec_tab").style.display="";
 		var mid= document.getElementById("mid").value ;
 		 SettlementService.getBalanceById(mid,callback);
 		 function callback(account){
 			mAccount=account;
 			 document.getElementById("minfo").innerHTML=m_minfos[mid];
 			 document.getElementById("bAcount").innerHTML=account;
 			 document.getElementById("minfo_").innerHTML=m_minfos[mid];
 			 document.getElementById("bAcount_").innerHTML=account;
 		 }
 	}
 	