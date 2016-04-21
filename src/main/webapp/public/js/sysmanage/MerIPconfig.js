function init() {
	queryMerIP(1);
}
function reflushMerIP(){
	SysManageService.reflushMerIP(function callback(msg){if(msg==true) alert('刷新成功');else alert('刷新失败');});	
}
function queryMerIP(pageNo) {

	var mid = $("mid").value;
	var type = $("type").value;

	SysManageService.queryMerIP(pageNo, mid, type, callBack2);
}
var callBack2 = function(pageObj) {
	$("merIPtable").style.display = "";
	dwr.util.removeAllRows("resultList");
	var cellFuncs = [ function(obj) {return obj.mid;}, 
	                  function(obj) {return obj.ip;},
	                  function(obj) {return obj.type==1?"代付代扣白名单":obj.type==2?"新代付代扣白名单":"";}, 
	                  function(obj) {return "<a href=\"#\" onclick=\"editone("+ obj.id +")\" >修改</a>"
	                  +"&nbsp;&nbsp;&nbsp;&nbsp;<a href=\"#\" onclick=\"deleteone("+ obj.id +")\" >删除</a>"
	                	  ;}
	                ];
	paginationTable(pageObj, "resultList", cellFuncs, "", "queryMerIP");
};

//商户Ip配置新增
function addMerIP(){
	 jQuery("#alterMeripConfigTable").wBox({title:"&nbsp;&nbsp;商户IP配置新增",show:true});
}

function add(){
	 var mid=$("m_mid").value;
	 var ip=$("ip").value;
	 var type=$("t_type").value;
	if(mid==''){
		alert("请输入商户号");
		return false; 
	}
	if(ip==''){
		alert("请输入ip地址");
		return false; 
	}
	if(examine1(ip)){
		alert("请输入正确的ip地址");
		return false;
	}
	
	if(type==''){
		alert("请选择类型");
		return false; 
	}
	
	SysManageService.addMerIPConfig(mid,ip,type ,function(msg){
		alert(msg);
		jQuery("#wBox_close").click();
		queryMerIP(1);
	});
}

function editone(id){
	SysManageService.queryMerconfigByid(id,function(VisitIpConfig){
		dwr.util.setValues({
			m_mid1:VisitIpConfig.mid,
			ip1:VisitIpConfig.ip,
			id:VisitIpConfig.id
			 });
		 jQuery("#alterupdateMeripTable").wBox({title:"&nbsp;&nbsp;商户IP配置修改",show:true});
		 $("t_type1").value=VisitIpConfig.type;
		
	})
	
}

function edit(){
	 var id=$("id").value;
	 var mid=$("m_mid1").value;
	 var ip=$("ip1").value;
	 var type=$("t_type1").value;
	 if(mid==''){
		alert("请输入商户号");
		return false; 
	}
	if(ip==''){
		alert("请输入ip地址");
		return false; 
	}
	if(type==''){
		alert("请选择类型");
		return false; 
	}
	
	SysManageService.updateMerIPConfig(id,mid,ip,type ,function(msg){
		alert(msg);
		jQuery("#wBox_close").click();
		queryMerIP(1);
	});	
}

function deleteone(id){
	if(confirm("确定删除?")){
		SysManageService.deleteMerIpConfig(id,function(msg){
			alert(msg);	
			queryMerIP(1);
		})
	}
	
}


