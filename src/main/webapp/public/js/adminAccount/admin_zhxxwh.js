var zhCache={};
//根据用户查询信息
var currPage=1;
function search(pageNo){
	  $("gbzfbut").disabled = true;
	  $("gbzh").disabled = true;
	  var uid=$("mid").value.trim();
	  var mstate=$("mstate").value.trim();
	  AdminZHService.queryZHXX(pageNo,uid,mstate,function callback(pageObj){
		$("zhxxwhTable").style.display="";
		var count=0;
		var cellFuns=[	
		              function(obj){
			        	   count++;
			        	   zhCache[count]=obj;
			        	   if(obj.state== 2||obj.state==0 )  return "";
			        	   if(obj.uid== obj.aid )  return "<input type='radio' onclick='canEditBut("+obj.state+","+obj.payFlag+")' name='zhCount' value='"+count+"'/>";;
			            },
		              	
						function(obj){ return obj.uid;},
						function(obj){ return obj.aid;},
						function(obj){ return obj.aname;},
						
						function(obj){ return obj.initDate;},
						function(obj){ return z_state[obj.state];},
						function(obj){ return z_payFlag[obj.payFlag];},
						
						function(obj){ return div100(obj.transLimit);},
						function(obj){ return div100(obj.monthLimit);},
						function(obj){ return obj.accMonthCount;},
						function(obj){ return div100(obj.accMonthAmt);},
						
						function(obj){ return obj.czFeeMode;},
						function(obj){ return obj.tixianFeeMode;},
						function(obj){ return obj.daifaFeeMode;},
						function(obj){ return obj.daifuFeeMode;}
						];
	    	currPage=pageObj.pageNumber; 
			paginationTable(pageObj,"resultList",cellFuns,"","search");
		}
	);
}
//校验状态
function canEditBut(state,payFlag){
	if(state==1){
		$("gbzh").disabled = false;
		if(payFlag==1){
			$("gbzfbut").disabled = false;
		}else{
			$("gbzfbut").disabled = true;
		}
	}else{
		$("gbzfbut").disabled = true;
		$("gbzh").disabled = true;
	}
}
//关闭账户和账户支付功能
function editZH(flag){
	 var countArr= getValuesByName("zhCount");
	 var zh = zhCache[countArr[0]];
	 var aid=zh.aid;
	 var state=zh.state;
	 var payFlag=zh.payFlag;
	 var allBalance=zh.allBalance;	
	 if(flag==1){
		 if(!confirm("确定关闭账户余额支付功能？"))return;
		 AdminZHService.closeYE(aid,call);
	 }
	 if(flag==2){
		if(allBalance>0){alert("操作失败，余额不为0，不可以关闭");return;}
		if(!confirm("确定关闭账户？"))return;
		AdminZHService.closeZH(aid,call);
	 }
}

function call(msg){
	alert(msg);
    search(1);
}
function pwdReset(){
	var mid=$("mid").value;
	var pwd=$("pwd").value;
	var vnpwd=$("vnpwd").value;
	if(mid==""){
		alert("请填写商户号!");
		return;
	}
	if(pwd==""){
		alert("请填写重置密码!");
		return;
	}
	if(pwd.length<6||pwd.length>15){
		alert("密码长度在6-15位！");
		return false;
	}
	if(pwd!=vnpwd){
		alert("两次密码不一致！");
		return false;
	}
	AdminZHService.resetPassWord(mid,hex_md5(pwd),function(msg){
		if(msg=="ok"){
			alert("重置密码成功！");
			$("pwd").value="";
			$("mid").value="";
			$("vnpwd").value="";
			return;
		}
		alert(msg);
	});
	
	
}

function init(){
    initMinfos();
    dwr.util.addOptions("mstate", m_mstate);
}  
//商户权限修改中商户号改变时
	function midsChange(){
		 if(document.getElementById("mid")) document.getElementById("mid").value= $("smid").value;
	}