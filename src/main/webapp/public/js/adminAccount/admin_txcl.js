var zt = 0;
var w_state={};
function init(){
	 PageParam.initAdminStatePtype(function(list){
		  w_state=list[2];
		  dwr.util.addOptions("mstate",m_mstate); 
	 });
	
	AdminZHService.checkButtonAuth(98,function(flag){
	if(flag){
		 $("searchDJBDD").disabled="";
//		 $("downloadSuccessMsg").disabled="";
	 }else{
		 $("searchDJBDD").disabled="disabled";
//		 $("downloadSuccessMsg").disabled="disabled";
	 }
	});
	initMinfos();search(1,21);
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
	 AdminZHService.queryTXCL(pageNo,uid,state,mstate,callback);
}


//查询
function search2(pageNo){
	var mstate=$("mstate").value.trim();
	var uid=$("mid").value.trim();
	 AdminZHService.queryTXCL(pageNo,uid,zt,mstate,callback);
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
	                    function(obj) { return div100(obj.transAmt); }
	                ];	
	              //把查询出来的对象缓存起来
	                for(var i=0;i<pageObj.pageItems.length;i++){
	              	  var o = pageObj.pageItems[i];
	              	  query_cash[o.oid] = o;
	                }
	                str ="&nbsp;&nbsp;&nbsp;&nbsp;"
	                	+"<span>&nbsp;<input type='button' id='chooseAll' name='chooseAll' value='全 选' onclick=\"checkAll('toCheck');;\">&nbsp;&nbsp;&nbsp;"
	                	+"<input type='button' id='manage' name='manage' value='提现经办成功' onclick=\"do_confirm('1','success');\">&nbsp;&nbsp;&nbsp;"
	                	+"<input type='button' id='manage' name='manage' value='提现经办失败' onclick=\"do_confirm('2','fail');\">&nbsp;&nbsp;&nbsp;"
	                	/*+"<input type='text' id='option' name='option' value='提现经办失败' \">&nbsp;&nbsp;&nbsp;"*/
	   					+"</span>";
	                
	                str2 ="&nbsp;&nbsp;&nbsp;&nbsp;"
	                	/*+"<span>&nbsp;<input type='button' id='chooseAll' name='chooseAll' value='全 选' onclick=\"checkAll('toCheck');;\">&nbsp;&nbsp;&nbsp;"*/
	                	/*+"<input type='button' id='success' name='success' value='提现完成' onclick=\"solve('success');\">&nbsp;&nbsp;&nbsp;";*/
	                
	                var str3 = zt==21 ? str : str2;
	                
	                
	                currPage=pageObj.pageNumber; 
	   			paginationTable(pageObj,"resultList",cellFuncs,str3,"search2");
}

//下载
function downloadTXCL(){
	var uid=$("mid").value;
    dwr.engine.setAsync(false);//把ajax调用设置为同步
    AdminZHService.downloadTXCL(uid,
    		function(data){
    	         dwr.engine.openInDownload(data,function(){alert(1);});
    	    }
    );
}
/***
 * 提现经办 成功 /失败时触发
 * @param index
 */
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
	jQuery("#hlogDetail_TX").wBox({title:"提现经办意见",show:true});//显示box
	
}

//提现经办，提现成功，提现失败
function solve(){
	var flag=$("flag").value;
	var flag1=$("flag1").value;

	  var option=$("option").value.replace(/(\s*$)/g, "");
	  if(option==""){
		  alert("审核意见不能为空");return;
	  }
	  if(flag1=='manage'){
		  if (confirm("确定进行提现经办?")){
			  AdminZHService.updateTXCL(array,option,callback4Solve);
			  jQuery("#back").click();
		  }
		 /* jQuery("#wBox_close").click();
		  window.location.href="A_95_TXCL.jsp";
		  search2();*/
	  }
	  if(flag1=='success'){
		  if (confirm("确定进行提现经办完成?")){
		  AdminZHService.updateSuccessTXCL(array,option,callback4Solve2);
		  jQuery("#back").click();
		  }
	  }
	  if(flag1=='fail'){
		  if (confirm("确定提现经办失败?")){
		  AdminZHService.updateFailTXCL_JB(array,option,callback4Solve2);
		  jQuery("#back").click();
		  }
		/*  jQuery("#wBox_close").click();
		  window.location.href="A_95_TXCL.jsp";
		  search2();*/
	  }
	  
}
function callback4Solve(msg){
	alert(msg);
	search(1,21);
}
function callback4Solve2(msg){
	alert(msg);
//	  search2();
	search(1,21);
}



