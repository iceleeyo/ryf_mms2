
var prov={};
function init(){
	var mid=$("mid").value;
	PageParam.initMerInfo('add',function(l){	
		
		 dwr.util.removeAllOptions("prov_id");
	     dwr.util.addOptions("prov_id", {"":"请选择…"});
	     dwr.util.addOptions("prov_id", l[0]);
	     dwr.util.addOptions("a_prov_id", l[0]);
	     prov=l[0];
	     
	     dwr.util.addOptions("bk_name",bk_b2b );
	     dwr.util.addOptions("a_bk_name",bk_b2b );
	});
	MerchantService.getImportentMsgByMid(mid,function(minfo){
	  	dwr.util.setValues({
	  		uid:mid,
		  	acc_name:minfo.name
	  	});
	});
//	PageService.getCompanyBkMap(function(data){
//		dwr.util.addOptions("bk_name",data )
//	});
	
	search();
}

function add(){
	var uid=$("uid").value;
	var accName=$("acc_name").value;
	var bkName=dwr.util.getText("bk_name");
	var gate=$("bk_name").value;
	var provId=$("prov_id").value;
	var accNo=$("acc_no").value;
	if(gate=='') {
        alert("请选择开户银行！"); 
        return false; 
    }
	if(provId=='') {
        alert("请选择开户银行省份！"); 
        return false; 
    }
	if(accNo=='') {
        alert("请填写银行账号！"); 
        return false; 
    }
	if(!isAccNo(accNo)) {
        alert("请填写正确的银行账号！只能包含10-30位数字"); 
        return false; 
    }
	
	MerZHService.addUserBkAcc(uid,accName,bkName,gate,provId,accNo,callback);
}
function callback(msg){
	$("bk_name").value='';
	$("prov_id").value='';
	$("acc_no").value='';
	alert(msg);
	search();
}
function search(){
	MerZHService.getUserBkAcc(1,function(pageObj){
		 $("yhzhwhTable").style.display="";
		   var cellFuncs = [
		                    function(obj) { return obj.accName; },
		                    function(obj) { return bk_b2b[obj.gate]; },
		                    function(obj) { return prov[obj.provId]; },
		                    function(obj) { return obj.accNo; },
		                    function(obj){return "<input type=\"button\" value=\"修改\" onclick=\"edit4One('"+obj.uid+"','"+obj.accNo+"')\"/>" +
		                    		"&nbsp;&nbsp;<input type=\"button\" value=\"删除\" onclick=\"delete4One('"+obj.uid+"','"+obj.accNo+"')\"/>";}
		                ]	
		                currPage=pageObj.pageNumber; 
		   			paginationTable(pageObj,"resultList",cellFuncs,"","search");
	})
}
function edit4One(uid,accNo){
	MerZHService.getOneUserBkAcc(uid,accNo,callback2);
}
function callback2(page){
	
	dwr.engine.setAsync(false);//把ajax调用设置为同步
	UserBkAcc=page[0];
	   dwr.util.setValues({a_acc_name:UserBkAcc.accName,a_acc_no:UserBkAcc.accNo,b_acc_no:UserBkAcc.accNo});
	   jQuery("#alterUserBkAccTable").wBox({title:"&nbsp;&nbsp;银行账户维护修改",show:true});
	   $("a_bk_name").value=UserBkAcc.gate;
	   $("a_prov_id").value=UserBkAcc.provId;
}
function edit(){
	var uid=$("uid").value;
	var aGate =$("a_bk_name").value.trim();
	var aBkName=dwr.util.getText("a_bk_name");
	var aProvId =$("a_prov_id").value.trim();
	var aAccNo =$("a_acc_no").value.trim();
	var bAccNo =$("b_acc_no").value.trim();
	
	if(aGate=='') {
        alert("请选择开户银行！"); 
        return false; 
    }
	if(aProvId=='') {
        alert("请选择开户银行省份！"); 
        return false; 
    }
	if(aAccNo=='') {
        alert("请填写银行账号！"); 
        return false; 
    }
	if(!isAccNo(aAccNo)) {
        alert("请填写正确的银行账号！只能包含10-30位数字"); 
        return false; 
    }
	MerZHService.editOneUserBkAcc(uid,aBkName,aGate,aProvId,aAccNo,bAccNo,callback3);
}

function delete4One(uid,accNo){
	if(!confirm("确定删除?"))return;
	MerZHService.deleteOneUserBkAcc(uid,accNo,callback3);
}
function callback3(msg){
	alert(msg);
	jQuery("#wBox_close").click();
	search();
}


