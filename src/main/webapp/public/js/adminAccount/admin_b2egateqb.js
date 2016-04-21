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
	var gid=$("gid").value;
	var name =$("name").value.trim();
	var ncUrl =$("nc_url").value.trim();
	var provId =$("prov_id").value;
	var accNo =$("acc_no").value.trim();
	var accName =$("acc_name").value.trim();
	var termid =$("termid").value.trim();
	var corpNo =$("corp_no").value.trim();
	var userNo =$("user_no").value.trim();
	if(gid=='') {
        alert("请填写网关号！"); 
        return false; 
    }
	if(provId=='') {
        alert("请选择开户银行省份！"); 
        return false; 
    }
	AdminZHService.addB2EGate(gid,name,ncUrl,provId,accNo,accName,
			termid,corpNo,userNo,function(msg){
		alert(msg);
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
	AdminZHService.queryB2EGate(pageNo,function(pageObj){
		   var cellFuncs = [
		                    function(obj) { return obj.name; },
		                    function(obj) { return obj.bkNo; },
		                    function(obj) { return (obj.gid=="40003"?cmb_b2e_area[obj.provId]:prov[obj.provId]); },
		                    function(obj) { return obj.accNo; },
		                    function(obj) { return obj.accName; },
		                    function(obj){
		                    	return   "<input type=\"button\" value=\"余额查询\" onclick=\"queryB2EGateBalance('"+obj.gid+"')\"/>" 
		                    	;}
		                    
		                    ]	
		                currPage=pageObj.pageNumber; 
		   			paginationTable(pageObj,"resultList",cellFuncs,"","queryB2EGate");
	});
}

function queryB2EGateBalance(gid){
	loadingMessage("正在查询，请稍候......");	
	BalanceQueryService.queryBalance(gid,function(blStr){
		alert("可用余额："+blStr);
	})
}

