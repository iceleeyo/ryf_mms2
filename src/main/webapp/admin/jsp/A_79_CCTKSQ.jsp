<%@ page language="java" pageEncoding="UTF-8" %>
<% String path = request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>差错退款申请</title>
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
				 initMinfos();
            	 dwr.util.addOptions("authType", h_author_type);
            	PageService.getGatesMap(function(map){
        			h_gate = map;
        			dwr.util.addOptions("gate", h_gate);
        		});
			}
		    //点击checkbox时查询
			function queryIsExist(){
			    	var mid=document.getElementById("mid").value;
			    	var oldTseq=document.getElementById("tseq").value;
			    	if(mid==""){
			    	  alert("请选择商户！");
			    	  document.getElementById("exist").checked=false;
			    	  return false;
			    	}
			    	if(oldTseq==""){
			    	    alert("请输入原电银流水号！");
			    	    document.getElementById("exist").checked=false;
			    	    return false;
			    	}
			    	if(!isNumber(oldTseq)){
			    	 	alert("电银流水号必须是数字！");
			    	    document.getElementById("exist").checked=false;
			    	    return false;
			    	}
			    	PrepPayService.queryHlogIsExist(mid,oldTseq,callbackFun);
			}
			//查询的 回调函数
			function callbackFun(obj){
				if(obj==null){
				    alert("你选择的此商户的原电银流水号系统中不存在！");
				     document.getElementById("disexist").checked=true;
				     showInputs();
				    return false;
				}else{
					dwr.util.setValues({
						bkSeq:obj.bk_seq1,
						span_bkSeq:obj.bk_seq1,
						trAmt:div100(obj.amount),
						span_trAmt:div100(obj.amount),
						authType:h_author_type[obj.authorType],
						span_authType:h_author_type[obj.authorType],
						gateId:h_gate[obj.gate],
						span_gateId:h_gate[obj.gate],
						span_trDate:obj.sysDate,
						trDate:obj.sysDate
					});
					jQuery(".blank_button").hide();
				}
			}
			//不存在时显示input
			function showInputs(){
				jQuery(".span_val").html("");
				jQuery(".redPoint").show();
				jQuery(".blank_button").show();
			}
			//提交差错退款
			function submitErrorRefund(){
			     var errorAlertObj={"mid":"商户号","tseq":"原电银流水号","bkSeq":"原银行流水号",
			     "trAmt":"原交易金额","authType":"发起类型","gateId":"支付银行","trDate":"原交易日期","remark":"差错退款原因"};
				 if(!judgeBlankByIds(errorAlertObj))return;
				  var cgOrder=wrapObj("refundReqForm");
				  if(!isTwoPointNum(cgOrder.trAmt)){
					  alert("原交易金额必须是含有两位小数的数字！");
					  return;
				  }
				 cgOrder.trAmt= cgOrder.trAmt*100;
			     PrepPayService.submitErrorRefund(cgOrder,function(backData){
			      		if(backData=="ok"){
			      		   alert("差错退款申请成功！");
			      		}else{
			      		   alert(backData);
			      		}
			      });
			     
			}
		</script>

    </head>
    <body >
    <div class="style">
    <form action="#" id="refundReqForm">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="8">&nbsp;&nbsp;&nbsp;&nbsp;差错退款申请</td></tr>
            <tr>
                <td class="th1" align="right" width="40%">商户号：</td>
                <td align="left" >
                <input type="text" id="mid" name="mid" style="width: 50px;" maxlength="6"  onkeyup="checkMidInput(this);"/>
                  <select style="width: 150px" id="smid" name="smid" onchange="initMidInput(this.value)">
                     <option value="">全部...</option>
                   </select> <font color="red">*</font>
                 </td>
            </tr>
            <tr>
            	<td class="th1" align="right" >原电银流水号：</td>
                <td align="left"><input id="tseq" name="tseq" type="text" /> <font color="red">*</font></td>
             </tr>
            <tr>
            	<td class="th1" align="right" >系统中是否存在：</td>
                <td align="left"><input type="radio" name="isExist" id="exist" onclick="return queryIsExist();"/>存在
                			   <input type="radio" name="isExist" id="disexist" onclick="return showInputs();"/>不存在</td>
             </tr>
              <tr>
            	<td class="th1" align="right" >原银行流水号：</td>
                <td align="left"><span id="span_bkSeq" class="span_val"></span>
                		<input id="bkSeq" name="bkSeq" type="text" class="blank_button"/>
                		<span  class='redPoint'>*</span></td>
             </tr>
             <tr>
            	<td class="th1" align="right" >原交易金额：</td>
                <td align="left"><span id="span_trAmt" class="span_val"></span>
                		<input id="trAmt" name="trAmt" type="text" class="blank_button"/>
                		<span  class='redPoint'>*</span>(必须为含有两位小数的数字,如1.00)</td>
             </tr>
             <tr>
            	<td class="th1" align="right" >发起类型：</td>
                <td align="left"><span id="span_authType" class="span_val"></span>
                	<select style="width: 150px" id="authType" name="authType" class="blank_button">
                    	<option value="">全部...</option>
                	</select> <span  class='redPoint'>*</span>
                </td>
             </tr>
             <tr>
            	<td class="th1" align="right" >支付银行：</td>
                <td align="left"><span id="span_gateId" class="span_val"></span>
	                <select style="width: 150px" id="gateId" name="gateId" class="blank_button">
	                    <option value="">全部...</option>
	                </select> <span  class='redPoint'>*</span>
               </td>
             </tr>
              <tr>
            	<td class="th1" align="right" >原交易日期：</td>
                <td align="left" ><span id="span_trDate" class="span_val"></span>
                		<input id="trDate" name="trDate" type="text" class="Wdate blank_button"
                		onfocus="WdatePicker({skin:'ext',minDate:'2000-01-01',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});"/>
                		<span  class='redPoint'>*</span></td>
             </tr>
              <tr>
            	<td class="th1" align="right" > 差错退款原因：</td>
                <td align="left"><textarea rows="3" cols="30" id="remark"></textarea> <font color="red">*</font></td>
             </tr>
            <tr>
            <td colspan="8" align="center" style="height: 30px">
                <input class="button" type="button" value = "提交 " onclick="submitErrorRefund();" />
            </td>
            </tr>
        </table>
        </form>
        </div>
    </body>
</html>
