function queryGR(pageNo){
	var uid=$("uid").value.trim();
	var idNo=$("id_no").value.trim();
    AdminZHService.queryGR(pageNo,uid,idNo,callBack);
}
var currPage=1;
var callBack = function(pageObj){
	$("grxxwhTable").style.display="";
	   var cellFuncs = [
	                    function(obj) { return "<a href=\"#\" onclick=query4Detail('"+obj.uid+"'); class='box_detail'>"+obj.uid+"</a>"; },
	                    function(obj) { return obj.name; },
	                    function(obj) { return obj.gender==0?"男":"女"; },
	                    function(obj) { return getNormalTime(obj.sysDate+""); },
	                    function(obj) { return obj.idVerify==0?"未校验":"已校验"; },
	                    function(obj) { return obj.bankAcctVerfy==0?"未校验":"已校验"; },
	                    function(obj) { return z_tate[obj.tate]; },
	                    function(obj) { return obj.tel; },
	                    function(obj) { return obj.addr; },
	                    function(obj) { return obj.welMsg; },
	                ]	
	                currPage=pageObj.pageNumber; 
	   			paginationTable(pageObj,"resultList",cellFuncs,"","queryGR");
  }

// 查询详细信息
function query4Detail(uid) {
	AdminZHService.queryGRByUid(uid,function(obj){              
       $("v_uid").innerHTML = obj.uid;
       $("v_name").innerHTML = obj.name;
       $("v_gender").innerHTML = obj.gender==0?"男":"女";
       $("v_sys_date").innerHTML = getNormalTime(obj.sysDate+"");
       $("v_country_code").innerHTML = obj.countryCode;
       $("v_profe").innerHTML = obj.profe;
       
       $("v_id_type").innerHTML = z_id_type[obj.idType];
       $("v_addr").innerHTML = obj.addr;
       $("v_tel").innerHTML = obj.tel;
       $("v_id_no").innerHTML = obj.idNo;
       $("v_id_exp_date").innerHTML = obj.idExpDate;
       $("v_bank_acc").innerHTML = obj.bankAcc;
       
       $("v_gate_id").innerHTML = obj.gateId;
       $("v_id_verify").innerHTML = obj.idVerify==0?"未校验":"已校验";
       $("v_bank_acct_verfy").innerHTML = obj.bankAcctVerfy==0?"未校验":"已校验";
       $("v_tate").innerHTML = z_tate[obj.tate];
       $("v_wel_msg").innerHTML = obj.welMsg;
       

      jQuery("#grxxDetail").wBox({title:"个人信息详细资料",show:true});//显示box
    });
}