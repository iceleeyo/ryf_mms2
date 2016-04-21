<%@ page language="java" pageEncoding="UTF-8" %>
<% String path = request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>商户资金归集申请</title>
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
				PrepPayService.getBkNoMap(function(data){
	            		dwr.util.addOptions("bkNo", data);
	            });
			}
			var merBalance,bkBalance;
			function queryBankBalance(){
				var bkNo=$("bkNo").value;
				if(bkNo==""){
				  alert("请选择银行！");
				  return;
				}
				PrepPayService.queryBkBalance(bkNo,function(obj){
					merBalance=obj.bfBl;
					bkBalance=obj.bkBl;
					$("merBalance").innerHTML=div100(merBalance);
					$("bkBalance").innerHTML=div100(bkBalance);
				});
			}
			function bunchMerFundReq(){
			    var trAmt=$("trAmt").value;
			    var remark=$("remark").value;
			    var bkNo=$("bkNo").value;
			    if(trAmt==0){
			         alert("归集金额不能为0！");
			         return false;
			    }
			    if(!isTwoPointNum(trAmt)){
					  alert("归集金额必须是含有两位小数的正数！");
					  return false;
				 }
			     if(trAmt>merBalance/100){
			        alert("归集金额不能大于商户余额！");
			        return false;
			     }
			     if(remark==""){
			        alert("请填写归集原因！");
			        return false;
			     }
			     var cgOrder={};
			      cgOrder.bkNo=bkNo;
			      cgOrder.trAmt=trAmt*100;
			      cgOrder.remark=remark;
			     PrepPayService.bunchMerFundReq(cgOrder,function(backData){
			     	if(backData=="ok"){
			     		alert("商户资金归集申请成功！");
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
            <tr><td class="title" colspan="8">&nbsp;&nbsp;&nbsp;&nbsp;商户资金归集申请</td></tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">银行：</td>
                <td align="left" width="*%">
                <select style="width: 150px" id="bkNo" name="bkNo">
                    <option value="">全部...</option>
                </select>&nbsp;&nbsp;&nbsp;<input class="button" type="button" value = " 查 询 " onclick="queryBankBalance();" />
                </td>
            </tr>
            <tr>
                <td class="th1"  align="right" >商户余额(元)：</td>
                <td align="left" id="merBalance"></td>
            </tr>
            <tr>
                <td class="th1"  align="right" >银行余额(元)：</td>
                <td align="left" id="bkBalance"></td>
            </tr>
            <tr>
                <td class="th1"  align="right" >归集金额(元):</td>
                <td align="left" ><input id="trAmt" name="trAmt" type="text" />
                	<font color="red">*</font>(必须为含有两位小数的数字,如1.00)</td>
            </tr>
            <tr>
                <td class="th1"  align="right" >归集原因:</td>
                <td align="left"><textarea rows="3" cols="30" id="remark"></textarea> <font color="red">*</font></td>
            </tr>
            <tr>
            <td colspan="8" align="center" style="height: 30px">
                <input class="button" type="button" value = "提 交 " onclick="bunchMerFundReq();" />
            </td>
            </tr>
        </table>

        </div>
    </body>
</html>
