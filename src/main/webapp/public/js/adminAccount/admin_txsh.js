var zt = 0;
var w_state={};

function init(){
	 PageParam.initAdminStatePtype(function(list){
		  w_state=list[2];
		  dwr.util.addOptions("mstate",m_mstate); 
	 });
	
	/*AdminZHService.checkButtonAuth(98,function(flag){
	if(flag){
		 $("searchDJBDD").disabled="";
		 $("downloadSuccessMsg").disabled="";
	 }else{
		 $("searchDJBDD").disabled="disabled";
		 $("downloadSuccessMsg").disabled="disabled";
	 }
	});*/
	initMinfos();
//	search(1,21);
//	AdminZHService.checkButtonAuth(75, function(flag){
//	if(flag){
//		 $("searchDTXDD").disabled="";
//	 }else{
//		 $("searchDTXDD").disabled="disabled";
//	 }
//	});
}
//查询
function search(pageNo,state){
	zt = state;
	var uid=$("mid").value.trim();
	var mstate=$("mstate").value.trim();
	 AdminZHService.queryTXCL_SH(pageNo,uid,state,mstate,callback);
}


//查询
function search2(pageNo){
	var uid=$("mid").value.trim();
	var mstate=$("mstate").value.trim();
	 AdminZHService.queryTXCL_SH(pageNo,uid,zt,mstate,callback);
}



var query_cash = {};

var currPage=1;
function callback(pageObj){
	$("txclTable").style.display="";
	   var cellFuncs = [
	                    function(obj) {
						    if(obj.ptype == 1&&obj.state==21){
						        return "<input type=\"checkbox\"  name=\"toCheck\" id=\"toCheck\" value=\""+ obj.oid +"\">";
						     }else{
						          return "";
						     }
					 	},
	                    function(obj) { return obj.oid; },
	                    function(obj) { return getNormalTime(obj.initTime+""); },
	                    function(obj) { return obj.uid; },
	                    function(obj) { return obj.aid; },
	                    function(obj) { return obj.aname; },
	                    function(obj) { return w_state[obj.state]; },
	                    
	                    function(obj) { return obj.name; },
	                    function(obj) { return obj.bankName; },
	                    function(obj) { return obj.bankAcct; },
	                    function(obj) { return div100(obj.transAmt); },
	                ]	
	              //把查询出来的对象缓存起来
	                for(var i=0;i<pageObj.pageItems.length;i++){
	              	  var o = pageObj.pageItems[i];
	              	  query_cash[o.oid] = o;
	                }
	                str ="<span>&nbsp;<input type='button' id='chooseAll' name='chooseAll' value='全 选' onclick=\"checkAll('toCheck');;\">&nbsp;&nbsp;&nbsp;"
	                	+"<input type='button' id='manage' name='manage' value='提现经办' onclick=\"solve('manage');\">&nbsp;&nbsp;&nbsp;"
	   					+"</span>";
	                
	                str2 ="<span>&nbsp;<input type='button' id='chooseAll' name='chooseAll' value='全 选' onclick=\"checkAll('toCheck');;\">&nbsp;&nbsp;&nbsp;"
	                	+"<input type='button' id='success' name='success' value='审核成功' onclick=\"do_confirm('1','manage');\">&nbsp;&nbsp;&nbsp;"
	                	+"<input type='button' id='fail' name='fail' value='审核失败' onclick=\"do_confirm('2','fail');\">&nbsp;&nbsp;&nbsp;"
	                /*+"<input type='text' id='option' name='option' value='审核意见' \">&nbsp;&nbsp;&nbsp;";*/
	                
//	                var str3 = zt==0 ? str : str2
	                
	                
	                currPage=pageObj.pageNumber; 
	   			paginationTable(pageObj,"resultList",cellFuncs,str2,"search2");
}

//下载
function downloadTXCL(){
	var uid=$("mid").value;
	var mstate=$("mstate").value;
    dwr.engine.setAsync(false);//把ajax调用设置为同步
    AdminZHService.downloadTXCL(uid,mstate,
    		function(data){
    	         dwr.engine.openInDownload(data,function(){alert(1);});
    	    }
    );
}


var array=null;
function do_confirm(index2,flag){
	$("flag").value=index2+"";
	$("flag1").value=flag;
	array= new Array();
    var chkArray = document.getElementsByName("toCheck");
    var index = 0;
    for (var i = 0 ; i < chkArray.length; i++){
    if(chkArray[i].checked){
      var Log = query_cash[chkArray[i].value];
         array[index] = Log;
         index ++;
    	}
    }
	  if (index == 0){
	      alert("请至少选中一条记录!");
	      return false;  
	  }else if(index2=="2" && index>1){
		  alert("请选择一条进行经办失败处理!");return false;
	  }
	jQuery("#hlogDetail_TX").wBox({title:"提现审核意见",show:true});//显示box
	
}

//提现经办，提现成功，提现失败
function solve(flag){
		var flag=$("flag").value;
		var flag1=$("flag1").value;
		if($("option").value.replace(/(\s*$)/g, "")==null||$("option").value.replace(/(\s*$)/g, "")==""){
			alert("审核意见不能为空！");return;
		}
	  if(flag1=='manage'){
		  if (confirm("确定审核?")){
			  AdminZHService.updateTXCL(array,$("option").value.replace(/(\s*$)/g, ""),callback4Solve);
			  jQuery("#back").click();
			  
		  }
	  }
	/*  if(flag1=='success'){
		  if(confirm("确定审核？")){
			  
			  AdminZHService.updateSuccessTXCL(array,$("option").value,callback4Solve2);
		  }
	  }*/
	  if(flag1=='fail'){
		  if (confirm("确定提现失败?")){
			  AdminZHService.updateFailTXCL(array,$("option").value.replace(/(\s*$)/g, ""),callback4Solve2);
			  jQuery("#back").click();
			  
		  }
	  }
}
function callback4Solve(msg){
	alert(msg);
	search(1,21);
}
function callback4Solve2(msg){
	alert(msg);
	search(1,21);
}
/***
 * 点击弹出审核框审核按钮
 */
function doAction(){
	var option=$("option").value;
	var con=$("suc").value;
	var array = new Array();
    var chkArray = document.getElementsByName("toCheck");
    var index = 0;
    for (var i = 0 ; i < chkArray.length; i++){
    if(chkArray[i].checked){
      var Log = query_cash[chkArray[i].value];
         array[index] = Log;
         index ++;
    	}
    }
	if(jQuery("#suc").attr("checked")){
		alert("suc");
		con=1;
	}else if(jQuery("#fail").attr("checked")){
		alert("fail");
		con=0;
	}else{
		alert("选中成功或失败！");
		return;
	}
	 AdminZHService.updateSuccessTXCL(array,callback4Solve2);
	
	
}

//========================yyf
//查询线下充值审核
function searchXXCZSH(pageNo){
	zt = 1;
	var uid=$("mid").value.trim();
	var mstate=$("mstate").value.trim();
	if(uid==undefined){
		uid=="";
	}
	 AdminZHService.queryXXCZSH(pageNo,uid,mstate,callbackXXCZSH);
}

//查询
function searchXXCZSH2(pageNo){
	var uid=$("mid").value.trim();
	 AdminZHService.queryXXCZSH(pageNo,uid,zt,callbackXXCZSH);
}
function callbackXXCZSH(pageObj){
	$("txclTable").style.display="";
	   var cellFuncs = [
	                    function(obj) {
						    if(obj.ptype == 0&&obj.state==32){
						        return "<input type=\"checkbox\"  name=\"toCheck\" id=\"toCheck\" value=\""+ obj.oid +"\">";
						     }else{
						          return "";
						     }
					 	},
	                    function(obj) { return obj.oid; },
	                    function(obj) { return getNormalTime(obj.initTime+""); },
	                    function(obj) { return obj.uid; },
	                    function(obj) { return obj.aid; },
	                    function(obj) { return obj.aname; },
	                    function(obj) { return w_state[obj.state]; },
	                    function(obj) { return div100(obj.payAmt); },
	                ];
	              //把查询出来的对象缓存起来
	                for(var i=0;i<pageObj.pageItems.length;i++){
	              	  var o = pageObj.pageItems[i];
	              	  query_cash[o.oid] = o;
	                }
	                str2 ="<span>&nbsp;<input type='button' id='chooseAll' name='chooseAll' value='全 选' onclick=\"checkAll('toCheck');;\">&nbsp;&nbsp;&nbsp;"
	                	+"<input type='button' id='success' name='success' value='审核成功' onclick=\"do_confirm_xxczsh('1','manage');\">&nbsp;&nbsp;&nbsp;"
	                	+"<input type='button' id='fail' name='fail' value='审核失败' onclick=\"do_confirm_xxczsh('2','fail');\">&nbsp;&nbsp;&nbsp;";
	                currPage=pageObj.pageNumber; 
	   			paginationTable(pageObj,"resultList",cellFuncs,str2,"searchXXCZSH2");
}

//线下充值审核数据下载
function downloadXXCZSH(){
	var uid=$("mid").value;
	var mstate=$("mstate").value;
  dwr.engine.setAsync(false);//把ajax调用设置为同步
  AdminZHService.downloadXXCZSH(uid,mstate,
  		function(data){
  	         dwr.engine.openInDownload(data,function(){alert(1);});
  	    }
  );
}
function do_confirm_xxczsh(index2,flag){
	$("flag").value=index2+"";
	$("flag1").value=flag;
	array= new Array();
    var chkArray = document.getElementsByName("toCheck");
    var index = 0;
    for (var i = 0 ; i < chkArray.length; i++){
    if(chkArray[i].checked){
      var Log = query_cash[chkArray[i].value];
         array[index] = Log;
         index ++;
    	}
    }
	  if (index == 0){
	      alert("请至少选中一条记录!");
	      return false;  
	  }else if(index2=="2" && index>1){
		  alert("请选择一条进行审核失败处理!");return false;
	  }
	jQuery("#hlogDetail_TX").wBox({title:"线下充值审核意见",show:true});//显示box
}
//提现经办，提现成功，提现失败
function solve_xxczsh(flag){
		var flag=$("flag").value;
		var flag1=$("flag1").value;
		var option=$("option").value.replace(/(\s*$)/g, "");
		if(option==""){
			alert("审核意见不能为空！");return;
		}
	  if(flag1=='manage'){
		  if (confirm("确定审核成功?")){
		  AdminZHService.updateXXCZSH(array,$("option").value.replace(/(\s*$)/g, ""),callback4Solve_xxczsh);
		  jQuery("#back").click();
		  }
	  }
	  if(flag1=='fail'){
		  if (confirm("确定审核失败?")){
		  AdminZHService.updateFailXXCZ(array,$("option").value.replace(/(\s*$)/g, ""),callback4Solve_xxczsh);
		  jQuery("#back").click();
		  }
	  }
}
function callback4Solve_xxczsh(msg){
	alert(msg);
	searchXXCZSH(1);
}