var currPage=1;
function searchOpersInfo(pageNo){
	   var mid = document.getElementById("mid").value.trim();
	   var operName = document.getElementById("oper_name").value.trim();
	   if(mid == '' && operName == ''){alert("至少输入一个查询条件");return false;}
    if(mid != '' && !isFigure(mid)){
          alert("商户号只能为整数"); 
          return false;
    }
    MerchantService.getOpers4Object(mid,operName,pageNo,callback);
   function callback(operInfoList){
    	document.getElementById("body4List").style.display="";
    	if(operInfoList.length==0){
    		document.getElementById("operInfoBody").appendChild(creatNoRecordTr(8));
    		return false;
    	}
    	var cellFuncs = [
    	                 function(OperInfo) { return OperInfo.mid; },
    	                 function(OperInfo) { return OperInfo.operId; },
    	                 function(OperInfo) { return OperInfo.operName; },
    	                 function(OperInfo) { return OperInfo.operTel; },
    	                 function(OperInfo) { return OperInfo.operEmail; },
    	                 function(OperInfo) { return OperInfo.regDate; },
    	                 function(OperInfo) { return OperInfo.state==0 ? '正常' : '关闭'; },
    	                 function(OperInfo) { return "<input type=\"button\" value=\"修改资料\" onclick=\"query4One('" +OperInfo.mid +"','"+OperInfo.operId + "')\"/>"; }          
    	                 ]; 
    	                 currPage=operInfoList.pageNumber;    //设置当前页      
    	paginationTable(operInfoList,"operInfoBody",cellFuncs,"","searchOpersInfo");
    }
 }
	
	function query4One(mid,operId){
	   MerchantService.getOneOpersObject(mid,operId,callback4One);
	}
	
var callback4One = function(Page){
   document.getElementById("queryTJ").style.display="none";
   document.getElementById("body4List").style.display="none";
   document.getElementById("body4One").style.display=""; 
   OperInfo = Page[0];
   dwr.util.setValues({v_minfo_id : OperInfo.mid, 
	                   v_oper_id : OperInfo.operId,
	                   v_oper_name :OperInfo.operName,
	                   v_oper_tel : OperInfo.operTel,
	                   v_oper_email : OperInfo.operEmail,
	                   v_state : OperInfo.state});
};

		
function edit() {
		var minfo_id = document.getElementById("v_minfo_id").value.trim();
		var oper_id = document.getElementById("v_oper_id").value.trim();
		var oper_name = document.getElementById("v_oper_name").value.trim();
		var oper_tel = document.getElementById("v_oper_tel").value.trim();
		var state = document.getElementById("v_state").value.trim();
		var oper_email = document.getElementById("v_oper_email").value.trim();
		if (checkMsg(oper_id,oper_name,oper_tel,oper_email,null,null)) {
		    MerchantService.editOperInfo(state, oper_email, oper_tel ,oper_name , oper_id ,minfo_id,0,callback4Edit);
	    }
}

function callback4Edit(msg) {
		if(msg=='ok'){
			alert("修改成功！");
			shouView("edit");
		}else{
			alert(msg);
		}
}
function shouView(flag){
   if(flag=="edit"){
  		 searchOpersInfo(currPage);
   };	  	 
  document.getElementById("body4One").style.display="none"; 
  document.getElementById("queryTJ").style.display="";
  document.getElementById("body4List").style.display="";
}		