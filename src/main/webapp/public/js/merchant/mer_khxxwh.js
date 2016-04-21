var prov={};
var tType={};
//初始化
function init(){
	var mid=$("mid").value;
	PageParam.initMerInfo('add',function(l){	
		
	     dwr.util.addOptions("prov_id", l[0]);
	     dwr.util.addOptions("a_prov_id", l[0]);
	     dwr.util.addOptions("i_prov_id", l[0]);
	     prov=l[0];
	     dwr.util.addOptions("trade_type",l[2]);
	     tType=l[2];
	     
	     
	});
	 dwr.util.addOptions("ctype", z_ctype);
	 dwr.util.addOptions("bk_name",bk_b2b );
     dwr.util.addOptions("a_bk_name",bk_b2b );
     dwr.util.addOptions("i_bk_name",bk_b2b );
     //dwr.util.addOptions("trade_type",merTradeType);
     search();
}
//匹配当ctype为1时，trade_type为其他
function search4TradeType(){
	if($("ctype").value==1)$("trade_type").value=109;
}
//增加信息
function addCusInfos(){
	var cid="C"+$("cid").value.trim();
	var ctype=$("ctype").value;
	var tradeType=$("trade_type").value;
	var cname=$("cname").value.trim();
	var gate=$("bk_name").value;
	var bkName=dwr.util.getText("bk_name");
	var provId=$("prov_id").value;
	var accNo=$("acc_no").value;
//	var contact=$("contact").value;
//	var position=$("position").value;
//	var cell=$("cell").value;
	if($("cid").value.trim()=='') {
        alert("请填写客户编号！"); 
        return false; 
    }
	if(ctype=='') {
        alert("请选择客户类型！"); 
        return false; 
    }
	if(tradeType=='') {
        alert("请选择所属行业！"); 
        return false; 
    }
	if(cname=='') {
        alert("请填写账户/客户名！"); 
        return false; 
    }
	if(gate=='') {
        alert("请选择开户银行！"); 
        return false; 
    }
	if(provId=='') {
        alert("请选择开户银行省份！"); 
        return false; 
    }
	if(accNo=='') {
        alert("请填写开户银行账号！"); 
        return false; 
    }
	if(!isAccNo(accNo)) {
        alert("请填写正确的银行账号！只能包含10-30位数字"); 
        return false; 
    }
//	if(contact=='') {
//        alert("请选择开户银行！"); 
//        return false; 
//    }
//	if(position=='') {
//        alert("请选择开户银行！"); 
//        return false; 
//    }
//	if(cell=='') {
//        alert("请选择开户银行！"); 
//        return false; 
//    }
//	if(!isMobel(cell)) {
//        alert("请填写正确的手机号！"); 
//        return false; 
//    }
	MerZHService.addKeHu(cid,ctype,tradeType,cname,bkName,gate,provId,accNo,call);
}
function call(msg){
	alert(msg);
	var ids = new Array("cid","ctype","trade_type","cname","bk_name","prov_id","acc_no");
	for(var i = 0 ; i< ids.length ; i++){
		var obj = document.getElementById(ids[i]);
		if(obj!=null){
			obj.value="";
		}
	}
	search();
}


var zhCache={}//缓存
//进行大表得查询
function search(){
	var c_cid=$("c_cid").value;
	
	MerZHService.getCusInfos(c_cid,function(list){
		dwr.util.removeAllRows("resultList");
		var count=0;
		   var cellFuncs = [
							function(obj){
									count++;
								   zhCache[count]=obj;
								   return "<input type='radio' onclick='canEditBut("+obj.cid+")' name='zhCount' value='"+count+"'/>"+obj.cid;
							},
		                    function(obj) { return z_ctype[obj.ctype]; },
		                    function(obj) { return obj.cname; },
		                    function(obj) { return tType[obj.tradeType]; },
		                    function(obj) { 
//		                    	if(obj.userBkAccSet==null) 
//		                    		return "";
		                    	
		                    	var str = "";
		                    	for(var i = 0;i< obj.userBkAccSet.length; i++ ){
		                    		var o = obj.userBkAccSet[i];
		                    		str +=  prov[o.provId] +"/"+bk_b2b[o.gate]+"/"+o.accNo
		                    		str += "&nbsp;&nbsp;<input type=\"button\" value=\"修改\"  onclick=\"edit4One('"+o.uid+"','"+o.accNo+"')\"/>"; 
		                    		str += " <br>";}
		                    	return str;},
//		                    function(obj){
//		                    	var str = "";
//		                    	for(var i = 0;i< obj.cusContactInfosSet.length; i++ ){
//		                    		var o = obj.cusContactInfosSet[i];
//		                    		str +=  o.contact+"/"+o.position+"/"+o.cell
//		                    		str +="&nbsp;&nbsp;<input type=\"button\"  value=\"修改\" onclick=\"edit4One2('"+o.id+"')\"/>"; 
//		                    		str += " <br>";}
//		                    	return str;}
		                    	]	
		        	    dwr.util.addRows("resultList",list,cellFuncs,{escapeHtml:false});
	})
}
//修改一条银行信息维护
function edit4One(uid,accNo){
	MerZHService.getOneUserBkAcc(uid,accNo,callback2);
}
function callback2(page){
	
	dwr.engine.setAsync(false);//把ajax调用设置为同步
	 UserBkAcc=page[0];
	   dwr.util.setValues({
		   uid:UserBkAcc.uid,
		   a_acc_name:UserBkAcc.accName,
		   a_acc_no:UserBkAcc.accNo,
		   b_acc_no:UserBkAcc.accNo});
	   jQuery("#alterUserBkAccTable").wBox({title:"&nbsp;&nbsp;银行账户维护修改",show:true});
	   $("a_bk_name").value=UserBkAcc.gate;
	   $("a_prov_id").value=UserBkAcc.provId;
}
//确定修改一条银行信息维护
function edit4UserBkAcc(){
	var uid=$("uid").value;
	var aBkName =dwr.util.getText("a_bk_name");
	var aGate=$("a_bk_name").value.trim();
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
function callback3(msg){
	alert(msg);
	jQuery("#wBox_close").click();
	search();
}
//添加一条银行账户信息,添加一条联系人信息
function addUserBkAcc(flag){
	var zhCount=getValuesByName("zhCount");
	if(zhCount==0){
		alert("请选择一条记录！");
		 return false;
	}
	var zh=zhCache[zhCount[0]];
	var cid_=zh.cid;
	if(flag==1){
	$("i_uid").value=cid_;
	MerZHService.getAccName(cid_,function call(msg){$("i_acc_name").value=msg
	jQuery("#insertUserBkAccTable").wBox({title:"&nbsp;&nbsp;银行账户维护添加",show:true});
	})
	}
	if(flag==2){
	$("ii_uid").value=cid_;	
	jQuery("#insertCusContactInfosTable").wBox({title:"&nbsp;&nbsp;联系人信息添加",show:true});
	}
}
//确定添加一条银行账户信息
function insert4UserBkAcc(){
	var iUid=$("i_uid").value;
	var iAccName=$("i_acc_name").value;
	var iBkName =dwr.util.getText("i_bk_name");
	var iGate =$("i_bk_name").value.trim();
	var iProvId =$("i_prov_id").value.trim();
	var iAccNo =$("i_acc_no").value.trim();
	if(iGate=='') {
        alert("请选择开户银行！"); 
        return false; 
    }
	if(iProvId=='') {
        alert("请选择开户银行省份！"); 
        return false; 
    }
	if(iAccNo=='') {
        alert("请填写银行账号！"); 
        return false; 
    }
	if(!isNumber(iAccNo)) {
        alert("请填写正确的银行账号！只能包含数字"); 
        return false; 
    }
	MerZHService.addUserBkAcc(iUid,iAccName,iBkName,iGate,iProvId,iAccNo,callback3);
}

//function addMore(){
//var cellFuncs = [
//                 function(data) { 
//                	 var str='';
//                	  str+="<tr colspan='8'><td class='th1' bgcolor='#D9DFEE' align='right' >开户银行：</td><td align='left'  > " ;
//                	 str+= "<select id='bk_name1' name='bk_name1' style='width: 150px'>";
//                	 str+= "<option value=''> 请选择...</option> </select></td>";
//                	 return str;
//                 		},
//         		function(data) { 
//            	 var str='';
//            	  str+="<td class='th1' bgcolor='#D9DFEE' align='right' >开户银行：</td><td align='left'  > " ;
//            	 str+= "<select id='bk_name1' name='bk_name1' style='width: 150px'>";
//            	 str+= "<option value=''> 请选择...</option> </select></td>";
//            	 return str;
//             		},
//             	function(data) { 
//                	 var str='';
//                	  str+="<td class='th1' bgcolor='#D9DFEE' align='right' >开户银行：</td><td align='left'  > " ;
//                	 str+= "<select id='bk_name1' name='bk_name1' style='width: 150px'>";
//                	 str+= "<option value=''> 请选择...</option> </select></td></tr>";
//                	 return str;
//                 		},
//                
//               ];
//
//               var count = 1;
//               dwr.util.addRows( "demo1",[''],cellFuncs,{ escapeHtml:false});
//}

//修改一条联系人信息
//function edit4One2(id){
//	MerZHService.getOneCusContactInfos(id,callback4);
//}
//function callback4(page){
//	
//	dwr.engine.setAsync(false);//把ajax调用设置为同步
//	CusContactInfos=page[0];
//	   dwr.util.setValues({a_uid:CusContactInfos.uid,a_contact:CusContactInfos.contact,a_position:CusContactInfos.position,a_cell:CusContactInfos.cell});
//	   dwr.util.setValues({a_id:CusContactInfos.id});
//	   jQuery("#alterCusContactInfosTable").wBox({title:"&nbsp;&nbsp;联系人信息修改",show:true});
//}
//确定修改一条联系人信息
//function edit4CusContactInfos(){
//	var aId=$("a_id").value;
//	var aContact =$("a_contact").value.trim();
//	var aPosition =$("a_position").value.trim();
//	var aCell =$("a_cell").value.trim();
//	if(aContact=='') {
//        alert("请输入联系人姓名！"); 
//        return false; 
//    }
//	if(aPosition=='') {
//        alert("请输入职位！"); 
//        return false; 
//    }
//	if(aCell=='') {
//        alert("请输入手机号！"); 
//        return false; 
//    }
//	if(!isMobel(aCell)) {
//        alert("请填写正确的手机号！"); 
//        return false; 
//    }
//	MerZHService.editOneCusContactInfos(aId,aContact,aPosition,aCell,callback3);
//}


//确定添加一条联系人信息
//function insert4CusContactInfos(){
//	var iiUid=$("ii_uid").value;
//	var iContact =$("i_contact").value.trim();
//	var iPosition =$("i_position").value.trim();
//	var iCell =$("i_cell").value.trim();
//	if(iContact=='') {
//        alert("请输入联系人姓名！"); 
//        return false; 
//    }
//	if(iPosition=='') {
//        alert("请输入职位！"); 
//        return false; 
//    }
//	if(iCell=='') {
//        alert("请输入手机号！"); 
//        return false; 
//    }
//	if(!isMobel(iCell)) {
//        alert("请填写正确的手机号！"); 
//        return false; 
//    }
//	MerZHService.addOneCusContactInfos(iiUid,iContact,iPosition,iCell,callback3);
//}


