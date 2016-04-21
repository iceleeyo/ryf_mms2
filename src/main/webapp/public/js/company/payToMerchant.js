  window.onload=function(){
    	initMinfos();
    }
    var bkaccList=new Array();
    //查询账户列表
 	function queryAccOption(){
 	 	var mid=$("mid").value;
// 	 	if(!m_minfos[mid]){
// 	 		alert("您输入的账户号不存在，请核对后重新输入！");
// 	 	 	return ;
// 	 	};
 	 	
 		CompanyService.getBkAccList(mid,function(dataList){
 			if(dataList==null){
 				alert("您输入的账户号不存在，请核对后重新输入！");
 				return ;
 			}
 			bkaccList=dataList;
 	 		//交通银行-尾号为765445
 	 		var bkAccStr="";
 			 for(var i=0;i<dataList.length;i++){
 				var bkAcc=dataList[i];
 				bkAccStr+="<li onclick='this.firstChild.checked=true;'><input name='accName' type='radio' value='"+
 								bkAcc.id+"'/>"+bkAcc.bkName+"-尾号为"+bkAcc.accNo+"</li>";
 	 		 }
 			 $("bkAccOptions").innerHTML=bkAccStr;
 			 $("targetComName").innerHTML=bkAcc.accName;
 			jQuery("#comBkMsg").wBox({show:true,opacity:0,title:"<img src='../../public/images/xitu.gif'>对方企业银行账户"});
 	 	});
 		
 	}
 	//选择账户
 	function selectAcc(){
 		var accId=getValuesByName("accName")[0];
 		if(accId){
 			var bkAccObj=getObjById(bkaccList,"id",accId);
 			$("bkAccNo").innerHTML=bkAccObj.bkName+"-尾号为"+bkAccObj.accNo;
 			$("hidde_bkAccId").value=accId;
 			//hidde_bkAccNo
 	 	}
 	}
 	//检测输入的mid
 	function checkMid(mid){
 		if(mid!=""&&!m_minfos[mid]){
 			alert("您输入的账户号不存在，请核对后重新输入！");
 	 	 	return ;
 		}else{
 			$("comCustomer").innerHTML=m_minfos[mid];
 	 	}
 	 }

 	 var orderId,sendPhoneNo;
 	 function confirmMsg(){
     	    var mid=$("mid").value;
			var bkAccId=$("hidde_bkAccId").value;
			var transAmt=$("transAmt").value;
			var phoneNo=$("payToPhoneNo").value;
			var remark=$("remark").value;
			var comName=$("comName").value;
			if(mid==""){
				alert("请选择要付款的企业客户！");
				return;
			}
			if(bkAccId==""){
				alert("请选择要付款的账户！");
				return;
			}
			if(!isMoney(transAmt)){
				alert("请填写正确的付款金额！");
				return;
			}
			if(transAmt>20000000){//2147483647
				alert("输入的金额不能超过两千万！");
				return;
			}
			if($("isSendSms").checked){
				if(!checkPhone(phoneNo)){
					alert("请输入正确的手机号！");
					return;
				}
			}else{
				phoneNo="";
			}
			   dwr.engine.setAsync(false);
			   CompanyService.getPayToMerParam(comName,mid,bkAccId,transAmt,phoneNo,remark,function(data){
					orderId=data.ordId;
					sendPhoneNo=phoneNo;//去网银支付的短信号码
					$("confirm_ordId").innerHTML=data.ordId;
					$("confirm_payAmt").innerHTML=formatNumber(data.transAmt);//formatNumber(Number(payAmt).toFixed(2));
					$("confirm_transFee").innerHTML=formatNumber(data.transFee);
					$("confirm_transAmt").innerHTML=formatNumber(data.transAmt);
					$("confirm_transAmt_uppercase").innerHTML=atoc(data.transAmt);
					$("confirm_aname").innerHTML=m_minfos[mid];
					//$("confirm_bkName").innerHTML=;
					var accNo=data.userBkAcc.accNo;
					$("confirm_bkAcc").innerHTML=data.userBkAcc.bkName+"-尾号为"+accNo.substring(accNo.length-6,accNo.length);
					$("confirm_myPhoneNo").innerHTML=phoneNo;
					$("confirm_remark").innerHTML=remark;
					$("confirm_gate").innerHTML="&nbsp;<img src='../../images/banklogo/"+data.userBkAcc.gate+".png'/>";
					
					$("toBkAction").action=data.actionUrl;
					$("paramVal").value=data.p;
					$("keyVal").value=data.k;
					
					$("confirmPayMsgDiv").style.display="";
					$("orderInputTable").style.display="none";
				});	
				//jQuery("#confirmPayMsgDiv").wBox({title:"<img src='../../public/images/xitu.gif'>请确认你充值的信息",show:true});
		}
 	//返回修改
     function backModify(){
     	$("confirmPayMsgDiv").style.display="none";
			$("orderInputTable").style.display="";
    }
  	//金额转化成 中文大写
     function setChineseMoney(val){
    	 val=formatNumber(Number(val).toFixed(2));
         if(!isMoney(val)){
         	$("ChineseMoney").innerHTML="";
				alert("请输入正确的金额！");
				return;
         }
     	$("ChineseMoney").innerHTML=atoc(val);
     }
 	 //提交订单
     function submitOrder(){
     	dwr.engine.setAsync(false);
     	CompanyService.submitB2bOrder(orderId,sendPhoneNo,function(data){
     		if(data=="ok"){
     			$("toBkAction").submit();
     			jQuery("#operMsgDiv").wBox({title:"<img src='../../public/images/xitu.gif'>请确认你充值的信息",show:true});
             }else{
					alert(data);
              }
         });
     }
		function queryOrder(){
			CompanyService.queryOrderState(orderId,function(result){
				
				$("ordId_result").innerHTML=orderId;
				$("stateStr").innerHTML=result;
				$("mainDiv").style.display="none";
				$("orderResult").style.display="";
			});
		}
		//提交支付后--返回支付页面
		function backPayPage(){
			$("mainDiv").style.display="";
			backModify();
			
	   }