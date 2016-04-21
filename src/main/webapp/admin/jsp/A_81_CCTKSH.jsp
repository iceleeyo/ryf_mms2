<%@ page language="java" pageEncoding="UTF-8" %>
<% String path = request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>备份金银行管理</title>
		 <%
		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);
		int rand = new java.util.Random().nextInt(10000);
		%>
        <meta http-equiv="pragma" content="no-cache"/>
        <meta http-equiv="cache-control" content="no-cache"/>
        <meta http-equiv="Expires" content="0"/>  
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css"/>
        <link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="../../dwr/engine.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../dwr/util.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../dwr/interface/PageService.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../dwr/interface/PrepPayService.js?<%=rand%>"></script>
		<script type="text/javascript" src="../../public/datePicker/WdatePicker.js?<%=rand%>"></script>
		<script type="text/javascript" src="../../public/js/ryt.js?<%=rand%>"></script>
		<script type="text/javascript">
		 	//初始化options
			window.onload= function initOptions(){
            	 dwr.util.addOptions("authType", h_author_type);
            	 dwr.util.addOptions("transState",trans_state_map);
   				 PageService.getMinfosMap(function(m){m_minfos=m;});
			}
			//查询差错退款
			function queryErrorRefund(pageNo){
			   var authType=$("authType").value;
			   var transState=$("transState").value;
			    var oldTseq=$("oldTseq").value;
			   var bdate=$("bdate").value;
			   var edate=$("edate").value;
			   if(bdate==""||edate==""){
			       alert("请选择起始日期和结束日期！");
			      return false;
			   }
			   if(pageNo==-1){
			   	   //下载
			   		dwr.engine.setAsync(false);
			 	    PrepPayService.downloadVerifyRefund(authType, transState, bdate,edate,oldTseq,
			 	    					function(data){dwr.engine.openInDownload(data);});
			   }else{
			        PrepPayService.queryErrorRefundPay(pageNo,authType, transState, bdate,edate,oldTseq,callbackFun);
			   }
			}
			//回调函数
			function callbackFun(pageObj){ 
	    	   document.getElementById("errorRefundTable").style.display="";
	    	   dwr.util.removeAllRows("errorRefundList");
		             var cellFuncs = [
		                         function(obj) {  
		                        	 if(obj.trState==1){
		                        	 	return "<input name='refundCheck' type='checkbox' value='"+obj.id+"'/>"
		                        	 } 
		                        	 return "";
		                         },
		                         function(obj) { return obj.tseq; },
		                         function(obj) { return obj.mid; },
		                         function(obj) { return m_minfos[obj.mid]; },
		                         function(obj) { return obj.bkSeq; },
		                         function(obj) { return h_author_type[obj.authType]; },
		                         function(obj) { return h_gate[obj.gateId]; },
		                         function(obj) { return div100(obj.trAmt); },
		                         function(obj) { return div100(obj.bkRefFee); },
		                         function(obj) { return trans_state_map[obj.trState]; },
		                         function(obj) { return obj.addDate; },
		                         function(obj) { return obj.operDate==0?"":obj.operDate; }
		                     ];
				var str="<span class='pageSum'>&nbsp;申请退款总金额：<font color='red'><b>" + div100(pageObj.sumResult.amtSum) +"</b></font>  元</span><span class='pageSum'> ";
	              	str+="&nbsp;&nbsp;银行退回手续费：<font color='red'><b>"+ div100(pageObj.sumResult.bkRefFee)+"</b></font>  元</span>";	
		            str+="&nbsp;&nbsp;<input value='全选' type='button' onclick='checkReverseByName(\"refundCheck\")'/>&nbsp;&nbsp;";
		            str+="&nbsp;&nbsp;<input value='审核成功' type='button' onclick='verifyRefund(2)'/>&nbsp;&nbsp;&nbsp;&nbsp;";
		            str+="&nbsp;&nbsp;<input value='审核失败' type='button' onclick='verifyRefund(3)'/>";
		                    
		       paginationTable(pageObj,"errorRefundList",cellFuncs,str,"queryErrorRefund");
	          }
	          //审核成功、失败
	          function verifyRefund(handleType){
	          		var idArr=getValuesByName("refundCheck");
	          		 if(idArr.length<1){
	          		 	alert("请选择至少一条记录！");
	          		 	return false;
	          		 }
	          		 if(!confirm("你确认提交操作？"))return;
	          		 PrepPayService.verifyRefundPay(handleType,idArr,function(backData){
	          		 			if(backData=="ok"){
	          		 			   alert("操作成功！");
	          		 			   queryErrorRefund(1);
	          		 			}else{
	          		 			   alert(backData);
	          		 			}
	          		 });
	          }
		</script>

    </head>
    <body >
    <div class="style">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="8">&nbsp;&nbsp;&nbsp;&nbsp;差错退款审核</td></tr>
            <tr>
                <td class="th1" align="right" width="17%">差错退款经办日期：</td>
                <td align="left" width="30%" >
                    <input id="bdate" size="15px"
                    name="bdate" class="Wdate" type="text" onfocus="ryt_area_date('bdate','edate',0,30,0)" />
                    &nbsp;至&nbsp;
                    <input id="edate" size="15px" name="edate" class="Wdate" type="text" disabled="disabled" /><font color="red">*</font>
                </td>
            	<td class="th1" align="right" width="17%">发起类型：</td>
                <td align="left"><span id="span_authType" class="span_val"></span>
                	<select style="width: 150px" id="authType" name="authType">
                    	<option value="">全部...</option>
                	</select>
                </td>
            </tr>
                <tr>
                <td class="th1" align="right">退款状态：</td>
                <td align="left" >
                <select style="width: 150px" id="transState" name="transState">
                    	<option value="">全部...</option>
                	</select>
                </td>
            	<td class="th1" align="right" >原电银流水号：</td>
                <td align="left"><input name="oldTseq" id="oldTseq" /></td>
            </tr>
            <tr>
            <td colspan="8" align="center" style="height: 30px">
                <input class="button" type="button" value = "查 询 " onclick="queryErrorRefund(1);" />&nbsp;&nbsp;&nbsp;&nbsp;
                <input class="button"  type="button" value = "下 载 " onclick="queryErrorRefund(-1);" />
            </td>
            </tr>
        </table>

       <table  class="tablelist tablelist2" id="errorRefundTable" style="display:none;" >
           <thead>
           <tr>
              <th sort="false">选择</th><th>原电银流水号</th><th>商户号</th><th>商户简称</th>
             <th>原银行流水号</th><th>发起类型</th>
             <th>原交易银行</th><th>退款金额</th>
             <th>退还银行手续费</th><th>退款状态</th><th>退款申请日期</th>
             <th>退款经办日期</th>
           </tr>
           </thead>
           <tbody id="errorRefundList"></tbody>
       </table>
        </div>
    </body>
</html>
