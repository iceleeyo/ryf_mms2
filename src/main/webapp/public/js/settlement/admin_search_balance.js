 document.write("<script type='text/javascript' src='../../public/datePicker/WdatePicker.js'></script>");
 document.write("<script type='text/javascript' src='../../public/js/ryt.js'></script>"); 
function checkData(mid){
	  if(!mid=='' && !isFigure(mid)){
          alert("商户号只允许为整数，请重新输入！");
          document.getElementById("mid").value = '';
      }
  }
	// 根据条件查询(第一次查询)
function queryBaseBalance() {
		var mid=$("mid").value;
		if(mid=='') {
			alert('请选择要查询的商户');
			return;
		}
		SettlementService.searchBalance(mid,function callback(alist){
			if(alist.length==0){
				alert("当前没有账户录入");
				 dwr.util.removeAllRows("resultList");
				return ;
			}
			$("balanceList").style.display="";
			var cellFuns=[
							function(obj){ return obj.uid;},
							function(obj){ return obj.aid;},
							function(obj){ return obj.aname;},
							function(obj){ return obj.initDate;},
							function(obj){ return z_state[obj.state];},
							function(obj){ return div100(obj.allBalance+obj.balance);},
							function(obj){ return div100(obj.balance);}
							];
			dwr.util.removeAllRows("resultList");
		    dwr.util.addRows("resultList",alist,cellFuns,{escapeHtml:false});
			}
		);
}
	 
      