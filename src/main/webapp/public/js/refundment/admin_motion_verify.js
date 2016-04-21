var query_cash = {};
function  init(){
	dwr.util.addOptions("stat", h_mer_refund_tstat_);
	dwr.util.addOptions("mstate", m_mstate);
 	document.getElementById("stat").options[4].selected=true;
 	initGateChannel(); 
 	initMinfos();
     queryRefundMotions(1);
}
 
function queryRefundMotions(pageNo){
     var  mid = $("mid").value;
     var  bdate = $("date_begin").value; 
     var  gateRouteId = $("gateRouteId").value;
     var  edate = $("date_end").value;
     var  stat = $("stat").value;
     var  tseq = $("tseq").value;
     var  vstate = $("vstate").value;
     var mstate= $("mstate").value;
     var dateState=3;
     if(mid !='' && !isFigure(mid)){
         alert("商户号只能是整数!");
         $("mid").value = '';
         return false; 
     }
     if(bdate!=""&&edate==""){
    	 alert("请选择一个时间段!");
    	 return false;
     }
     if(pageNo==-1){
         dwr.engine.setAsync(false);//把ajax调用设置为同步
    	 RefundmentService.downloadRefundVerify(mid,stat,tseq,dateState,bdate,edate,null,null,vstate,gateRouteId,mstate,
    			 function(data){dwr.engine.openInDownload(data);});
     }else{
    	 RefundmentService.queryRefundLogs(pageNo,mid,stat,tseq,dateState,bdate,edate,null,null,vstate,gateRouteId,mstate,null,callBack2);
     }
}

   var cellFuncs = [
                    function(obj) {//为了兼容以前的stat==3的数据
                        return ((obj.stat == 2||obj.stat==3) && obj.vstate==0) ? "<input type='checkbox' name='ref_Id' value='"+ obj.id +"'/>" : "";
                    },
                    function(obj) { return obj.tseq;},
                    function(obj) { return obj.mid;},
                    function(obj) { return m_minfos[obj.mid];},
                    function(obj) { return obj.org_oid; },
                    function(obj) { return gate_route_map[obj.gid]; },
                    function(obj) { return  h_gate[obj.gate];},
                    function(obj) { return  div100(obj.ref_amt); },
                    function(obj) { return div100(obj.merFee); },
                    function(obj) { return  h_mer_refund_tstat[obj.stat]; },
                    function(obj) { return  obj.vstate==0 ? "未审核" : "已审核" ; },
                    function(obj) { return  formatDate(obj.sys_date);},
                    function(obj) { return  formatDate(obj.pro_date);}
   ];
   var callBack2 = function(pageObj){
       //把查询出来的对象缓存起来
	   for(var i=0;i<pageObj.pageItems.length;i++){
	 	  var o = pageObj.pageItems[i];
	 	  query_cash[o.id] = o;
	   }
	   
       str = "<span style='float:left;color:blue'>&nbsp;申请退款总金额：<font color='red'><b>" + div100(pageObj.sumResult.refFeeSum)+"</b></font>  元";
       str+= "&nbsp;&nbsp;&nbsp;&nbsp;";
       str+= "&nbsp;&nbsp;<input type=\"button\" id=\"chooseAll\" name=\"chooseAll\" value=\"全 选\" onclick=\"checkAll('ref_Id');\">";
       str+= "&nbsp;&nbsp;<input type=\"button\" id=\"verifySuccess\" name=\"verifySuccess\" value=\"操作成功\" onclick=\"verifyRefund();\">";
       str +="</span>";
       paginationTable(pageObj,"resultList",cellFuncs,str,"queryRefundMotions");
   };

function verifyRefund() {
	var refLogArray = new Array();
	var chkArray = document.getElementsByName("ref_Id");
	var index = 0;
	for (var i = 0; i < chkArray.length; i++) {
		if (chkArray[i].checked) {
			var RefundLog = query_cash[chkArray[i].value];
			refLogArray[index] = RefundLog;
			index++;
		}
	}

	if (index == 0) {
		alert("请至少选中一条记录!");
		return false;
	}
	
	if (confirm("确定操作成功吗?")) {
		RefundmentService.verifyRefund(refLogArray, callback);
	}
}
 function callback(msg) {
 	alert(msg);
    var page = document.getElementById("pageChoose").value;
    queryRefundMotions(page);
 }
