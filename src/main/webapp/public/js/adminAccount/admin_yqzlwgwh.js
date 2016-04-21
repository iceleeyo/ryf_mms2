var prov={};
function init(){
	PageParam.initMerInfo('add',function(l){	
		 dwr.util.removeAllOptions("prov_id");
	     dwr.util.addOptions("prov_id", {"":"请选择…"});
	     dwr.util.addOptions("prov_id", l[0]);
	     dwr.util.addOptions("a_prov_id", l[0]);
	     prov=l[0];
	});
	queryB2EGate(1);
}

function addB2EGate(){
	var b2eGate=new B2eGate();
	var gid=$("gid").value.trim();
	if(!isInteger(gid)){
		alert("网关号输入错误，只能为数字，不能为0开头");
		return;
	}else if(gid.length>6){
		alert("网关号输入错误，最大长度6位的数字");
		return;
	}
	b2eGate.setGid(parseInt(gid));
	var name =$("name").value.trim();
	if(name==''||name.length>40){
		alert("网关名称输入错误，最大长度为40位字符");
		return;
	}
	b2eGate.setName(name);
	
	var ncUrl =$("nc_url").value.trim();
	if(ncUrl==''){
		alert("前置机IP不能为空");
		return;
	}
	if(ncUrl.length > 30){
		alert("前置机IP长度不能大于30位");
		return;
	}
	b2eGate.setNcurl(ncUrl);
	
	var bkNo=$("bk_no").value.trim();
	if(!is_bk(bkNo)){
		alert("行号输入错误，只能为14位数字");
		return;
	}
	b2eGate.setBkno(bkNo);
	
	var provId =$("prov_id").value;
	if(provId==''){
		alert("开户省份不能为空，请选择开户省份");
		return;
	}
	b2eGate.setProvid(provId);
	
	var accNo =$("acc_no").value.trim();
	if(!isAccNo(accNo)){
		alert("账户号输入错误,不能0开头或必须为数字或长度（9-29）");
		return;
	}
	b2eGate.setAccno(accNo);
	
	var accName =$("acc_name").value.trim();
	if(accName==''||accName.length>50){
		alert("账户名称输入错误，不能为空，最大长度只能为50位的字符");
		return;
	}
	b2eGate.setAccname(accName);
	//ryf4.6 经确认前置机ip非必填项
	var termid =$("termid").value.trim();
//	if(termid==''){
//		alert("前置机IP不能为空");
//		return;
//	}
	if(termid!=''){
		if(termid.length>20){
			alert("前置机IP长度不能大于20");
			return;
		}
		
	}
	b2eGate.setTermid(termid);
	
	var corpNo =$("corp_no").value.trim();
	/*if((corpNo==''||corpNo.length>20)){
		alert("企业客户编号输入错误，不能为空，只能为20位字符");
		return;
	}*/
	if(corpNo!=''){
		if(corpNo.length>20){
			alert("企业客户编号长度不能大于20");
			return;
		}
		
	}
	b2eGate.setCorpno(corpNo);
	
	var userNo =$("user_no").value.trim();
	/*if(userNo==''||userNo.length>20){
		alert("操作员编号输入错误，不能为空，只能为20位字符");
		return;
	}*/
	if(userNo!=''){
		if(userNo.length>20){
			alert("操作员编号长度不能大于20");
			return;
		}
		
	}
	b2eGate.setUserno(userNo);
	
	var logPwd=$("log_pwd").value.trim();
	/*if(logPwd==''||logPwd.length>20){
		alert("操作员密码输入错误，不能为空，只能为30位字符");
		return;
	}*/
	if(logPwd!=''){
		if(logPwd.length>30){
			alert("操作员密码长度不能大于30");
			return;
		}
		
	}
	b2eGate.setUserpwd(logPwd);
	
	var encode=$("encode").value.trim();
	/*if(encode==''||encode.length>10){
		alert("字符编码输入错误，不能为空，只能为10位字符");
		return;
	}*/
	if(encode!=''){
		if(encode.length>10){
			alert("字符编码长度不能大于10");
			return;
		}
		
	}
	b2eGate.setEncode(encode);
	
	var protocol=$("protocol").value.trim();
	/*if(protocol==''||protocol.length>10){
		alert("协议编号输入错误，不能为空，只能为20位字符");
		return;
	}*/
	if(encode!=''){
		if(encode.length>20){
			alert("协议编号长度不能大于20");
			return;
		}
		
	}
	b2eGate.setBusino(protocol);
	
	YQGateService.addB2EGate(b2eGate,function(msg){
		alert(msg);
		jQuery("#wBox_close").click();
		var ids = new Array("gid","name","nc_url","prov_id","acc_no","acc_name","termid","corp_no","user_no");
		for(var i = 0 ; i< ids.length ; i++){
			var obj = document.getElementById(ids[i]);
			if(obj!=null){
				obj.value="";
			}
		}
		queryB2EGate(1);
	});
}

function queryB2EGate(pageNo){
	var account=$("account").value.trim();
	
	YQGateService.queryB2EGate(pageNo,account,function(pageObj){
		   var cellFuncs = [
		                    function(obj) { return obj.gid; },
		                    function(obj) { return obj.name; },
		                    function(obj) { return obj.ncUrl; },
		                    function(obj) { return obj.bkNo; },
		                    function(obj) { return (obj.gid=="40003"?cmb_b2e_area[obj.provId]:prov[obj.provId]); },
		                    function(obj) { return obj.accNo; },
		                    function(obj) { return obj.accName; },
		                    function(obj) { return obj.termid; },
		                    function(obj) { return obj.corpNo; },
		                    function(obj) { return obj.userNo; },
		                    function(obj) { return obj.userPwd; },
		                    function(obj) { return obj.codeType; },
		                    function(obj) { return obj.busiNo; },
		                    function(obj){
		                    	return "<input type=\"button\" value=\"修改\" onclick=\"edit4One('"+obj.gid+"')\"/>"
		                    	 /*+"<input type=\"button\" value=\"查询当日余额\" onclick=\"queryB2EGateBalance('"+obj.gid+"')\"/>"*/ 
		                    	;}
		                    
		                    ]	
		                currPage=pageObj.pageNumber; 
		   			paginationTable(pageObj,"resultList",cellFuncs,"","queryB2EGate");
	});
}
function edit4One(gid){
	YQGateService.getOneB2EGate(gid,function(B2EGate){
		 dwr.util.setValues({
			 a_gid:B2EGate.gid,a_name:B2EGate.name,a_nc_url:B2EGate.ncUrl,
			 a_bk_no:B2EGate.bkNo,
			 a_acc_no:B2EGate.accNo,a_acc_name:B2EGate.accName,
			 a_termid:B2EGate.termid,a_corp_no:B2EGate.corpNo,a_user_no:B2EGate.userNo,
			 a_user_pwd:B2EGate.userPwd,a_code_type:B2EGate.codeType,a_busi_no:B2EGate.busiNo
			 });
		 jQuery("#alterB2EGateTable").wBox({title:"&nbsp;&nbsp;银企直连网关维护修改",show:true});
		 if(B2EGate.gid==40003){
			 dwr.util.removeAllOptions("a_prov_id");
			 dwr.util.addOptions("a_prov_id", {"":"请选择…"});
			 dwr.util.addOptions("a_prov_id",cmb_b2e_area);
			 $("a_prov_id").value=B2EGate.provId;
		 }
		 else{
			 $("a_prov_id").value=B2EGate.provId;	 
		 }	 
	});
}
function edit(){
	var b2eGate=new B2eGate();
	var aGid=$("a_gid").value;
	b2eGate.setGid(aGid);
	var aName =$("a_name").value.trim();
	b2eGate.setName(aName);
	var aNcUrl =$("a_nc_url").value.trim();
	b2eGate.setNcurl(aNcUrl);
	var aBkNo =$("a_bk_no").value.trim();
	b2eGate.setBkno(aBkNo);
	var aProvId =$("a_prov_id").value;
	if(aProvId=='') {
        alert("请选择开户银行省份！"); 
        return false; 
    }
	b2eGate.setProvid(aProvId);
	var aAccNo =$("a_acc_no").value.trim();
	b2eGate.setAccno(aAccNo);
	var aAccName =$("a_acc_name").value.trim();
	b2eGate.setAccname(aAccName);
	var aTermid =$("a_termid").value.trim();
	b2eGate.setTermid(aTermid);
	var aCorpNo =$("a_corp_no").value.trim();
	b2eGate.setCorpno(aCorpNo);
	var aUserNo =$("a_user_no").value.trim();
	b2eGate.setUserno(aUserNo);
	var aUserPwd =$("a_user_pwd").value.trim();
	b2eGate.setUserpwd(aUserPwd);
	var aCodeType =$("a_code_type").value.trim();
	b2eGate.setCodetype(aCodeType);
	var aBusiNo =$("a_busi_no").value.trim();
	b2eGate.setBusino(aBusiNo);
	
	YQGateService.editOneB2EGate(b2eGate,function(msg){
		alert(msg);
		jQuery("#wBox_close").click();
		queryB2EGate(1);
	});
}


function queryClick(){
	queryB2EGate(1);
}


function addClick(){
	 jQuery("#addB2EGateTable").wBox({title:"&nbsp;&nbsp;银企直连网关维护修改",show:true});
	 dwr.util.removeAllOptions("prov_id");
	 dwr.util.addOptions("prov_id", {"":"请选择…"});
	 dwr.util.addOptions("prov_id",prov);
}

function B2eGate(){
	this.gid=0;//integer
	this.name="";
	this.ncUrl="";
	this.bkNo="";
	this.provId=0;//integer
	this.accNo="";
	this.accName="";
	this.termid="";
	this.corpNo="";
	this.userNo="";
	this.codeType="";
	this.token="";
	this.userPwd="";
	this.busiNo="";
	this.encode="";
	this.alarmAmt=0;//integer
	this.alarmPhone="";
	this.alarmMail="";
	this.alarmStatus=0;//integer
	this.failCount="";
	this.sucRate="";
	//setParam
	this.setGid=function(object){this.gid=parseInt(object);};//integer
	this.setName=function(object){this.name=object;};
	this.setNcurl=function(object){this.ncUrl=object;};
	this.setBkno=function(object){this.bkNo=object;};
	this.setProvid=function(object){this.provId=parseInt(object);};//integer
	this.setAccno=function(object){this.accNo=object;};
	this.setAccname=function(object){this.accName=object;};
	this.setTermid=function(object){this.termid=object;};
	this.setCorpno=function(object){this.corpNo=object;};
	this.setUserno=function(object){this.userNo=object;};
	this.setCodetype=function(object){this.codeType=object;};
	this.setToken=function(object){this.token=object;};
	this.setUserpwd=function(object){this.userPwd=object;};
	this.setBusino=function(object){this.busiNo=object;};
	this.setEncode=function(object){this.encode=object;};
	this.setAlarmamt=function(object){
		if(object=''){
			object=0;
		}
		this.alarmAmt=object;};//integer
	this.setAlarmphone=function(object){this.alarmPhone=object;};
	this.setAlarmmail=function(object){this.alarmMail=object;};
	this.setAlarmstatus=function(object){
		if(object==''){
			object=0;
		}
		this.alarmStatus=object;};//integer
	this.setFailcount=function(object){this.failCount=object;};
	this.setSucrate=function(object){this.sucRate=object;};
}


function is_bk(is_bk){
	if(!is_bk){
		return false;
	}
	var reg=/^[1-9]{1}[0-9]{1,13}$/;
	if(!reg.test(is_bk)){
		return false;
	}	
	return true;
}