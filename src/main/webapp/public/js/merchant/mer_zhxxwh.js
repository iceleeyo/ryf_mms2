//账户信息维护
function add(){
	
	var aname=$("aname").value.trim();
	var mid=$("mid").value;
	var aid=$("userno").innerHTML;
	if(aid==""){alert("账户号不能为空,不能增加！");return ;}
	if(aname==""){alert("账户名称不能为空！");return ;}
	//if(!isWordOrNumber(aid*1)){alert("账户号格式不正确！");return ;}
	MerZHService.add(aid,aname,mid,callBack);
}
function callBack(msg){
	if(msg=='ok'){
		alert("增加成功！");
		search();
		var ids = new Array("aid","aname");
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
//账户信息
function initZHXX(){
	search();
	//生成账户号
	//createNumber();
}

function createNumber(){
	var mid=$("mid").value;//得到商户号
	MerZHService.createNumber(mid,function callback(userNo){
		$("userno").innerHTML=userNo;
		
	});
	
}
var zhCache={};
function search(){
	  var mid=$("mid").value;
	  MerZHService.queryZHYE(mid,function callback(alist){
		if(alist.length==0){
			alert("当前没有账户录入");
			 dwr.util.removeAllRows("resultList");
			return ;
		}
		var count=0;
		var cellFuns=[	
		              function(obj){
			        	   count++;
			        	   zhCache[count]=obj;
			        	   if(obj.state== 2||obj.state==0 )  return "";
			        	   if(obj.uid== obj.aid )  return "<input type='radio' onclick='canEditBut("+obj.state+","+obj.payFlag+")' name='zhCount' value='"+count+"'/>";
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
		dwr.util.removeAllRows("resultList");
	    dwr.util.addRows("resultList",alist,cellFuns,{escapeHtml:false});
		}
	);
}


function canEditBut(state,payFlag){
	if(state==1){
		$("gbzh").disabled = false;
		if(payFlag==1){
			$("gbzfbut").disabled = false;
			$("kqzfbut").disabled = true;
		}else{
			$("gbzfbut").disabled = true;
			$("kqzfbut").disabled = false;
		}
	}else{
		$("kqzfbut").disabled = true;
		$("gbzfbut").disabled = true;
		$("gbzh").disabled = true;
	}
}



//开启，关闭余额支付功能
function editPF(flag){
	 var mid=$("mid").value;
	 var countArr= getValuesByName("zhCount");
	 if(countArr.length==0){
		 alert("请选择一条记录！");
		 return ;
	 }
	 var zh = zhCache[countArr[0]];
	 var state=zh.state;
	 var payFlag=zh.payFlag;
	 var allBalance=zh.allBalance;
	 if(flag==1){
		 if(state==0||state==2){ alert("操作失败，状态必须为正常");return;}
		 if(payFlag==1){alert( "操作失败，余额支付功能已经是正常状态");return;}
		 $("hidden_type").value ='A';
		 jQuery("#body4One").wBox({title:"&nbsp;&nbsp;请输入密码",show:true});
		 
	 }
	 if(flag==2){
		 if(state==0||state==2){ alert("操作失败，状态必须为正常");return;}
		 if(payFlag==0){alert("操作失败，余额支付功能已经是关闭状态");return;}
		 $("hidden_type").value ='B';
		 jQuery("#body4One").wBox({title:"&nbsp;&nbsp;请输入密码",show:true});
	 }
	 if(flag==3){
			if(state==2) {alert("操作失败，已经为关闭状态");return;}
			if(allBalance>0){alert("操作失败，余额不为0，不可以关闭");return;}
			 $("hidden_type").value ='C';
			jQuery("#body4One").wBox({title:"&nbsp;&nbsp;请输入密码",show:true});
	 }
}


function edit(){
	 var mid=$("mid").value;
	 var pwd=$("pwd").value;
	 var hidden_type=$("hidden_type").value;
	 var countArr= getValuesByName("zhCount");
	 var zh = zhCache[countArr[0]];
	 var aid=zh.aid;
	 var state=zh.state;
	 var payFlag=zh.payFlag;
	 var allBalance=zh.allBalance;
	 if(hidden_type=='A') {MerZHService.openPF(mid,aid,hex_md5(pwd),call);}
	 if(hidden_type=='B') {MerZHService.closePF(mid,aid,hex_md5(pwd),call);}
	 if(hidden_type=='C') {MerZHService.closeZH(mid,aid,hex_md5(pwd),call);}
	 
}

function call(msg){
	alert(msg);
	jQuery("#wBox_close").click();
    search();
}

//账户余额查询
function initZHYECX(){
	queryZHYE();
}

function queryZHYE(){
	var mid=$("mid").value;
	MerZHService.queryJSZHYE(mid,function callback(alist){
		if(alist.length==0){
			alert("当前没有账户录入");
			 dwr.util.removeAllRows("resultList");
			return ;
		}
		var cellFuns=[
						function(obj){ return obj.uid;},
						function(obj){ return obj.aid;},
						function(obj){ return obj.aname;},
						function(obj){ return obj.initDate;},
						function(obj){ return z_state[obj.state];},
						function(obj){ return div100(obj.allBalance+obj.balance);},
						function(obj){ return div100(obj.balance);}];
		dwr.util.removeAllRows("resultList");
	    dwr.util.addRows("resultList",alist,cellFuns,{escapeHtml:false});
		}
	);
}
function initZHYECXS(){
	queryJSZHYE();
}
function queryJYZHYE(){
	var mid=$("mid").value;
	MerZHService.queryJYZHYE(mid,function callback(alist){
		if(alist.length==0){
			alert("当前没有账户录入");
			 dwr.util.removeAllRows("resultList");
			return ;
		}
		var cellFuns=[
						function(obj){ return obj.uid;},
						function(obj){ return obj.aid;},
						function(obj){ return obj.aname;},
						function(obj){ return obj.initDate;},
						function(obj){ return z_state[obj.state];},
						function(obj){ return div100(obj.allBalance);},
						function(obj){ return div100(obj.balance);}
						];
		dwr.util.removeAllRows("resultList");
	    dwr.util.addRows("resultList",alist,cellFuns,{escapeHtml:false});
		}
	);
}
//查询结算账户余额 yang.yaofeng 2013-04-25
function queryJSZHYE(){
	var mid=$("mid").value;
	MerZHService.queryJSZHYE(mid,function callback(alist){
		if(alist.length==0){
			alert("当前没有账户录入");
			 dwr.util.removeAllRows("resultList");
			return ;
		}
		var cellFuns=[
						function(obj){ return obj.uid;},
						function(obj){ return obj.aid;},
						function(obj){ return obj.aname;},
						function(obj){ return obj.initDate;},
						function(obj){ return z_state[obj.state];},
						function(obj){ return div100(obj.allBalance);},
						function(obj){ return div100(obj.balance);}
						];
		dwr.util.removeAllRows("resultList");
	    dwr.util.addRows("resultList",alist,cellFuns,{escapeHtml:false});
		}
	);
}