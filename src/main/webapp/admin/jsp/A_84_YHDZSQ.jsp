<%@ page language="java" pageEncoding="UTF-8" %>
<% String path = request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>银行调帐申请</title>
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
			function adjustAccountReq(){
			    var trAmt=$("trAmt").value;
			    var remark=$("remark").value;
			    var bkNo=$("bkNo").value;
			    var trType=$("adjustType").value;
			    var adjustDate=$("adjustDate").value;
			    if(!isTwoPointNum(trAmt)){
					  alert("调帐金额必须是含有两位小数的数字！");
					  return false;
				 }
			     if(trType==3&&trAmt>(bkBalance-merBalance)/100){
			        alert("调减金额不能大于银行和商户的差额！");
			        return false;
			     }
			     if(adjustDate==""){
			        alert("请选择调账日期！");
			        return false;
			     }
			     if(remark==""){
			        alert("请填写调帐原因！");
			        return false;
			     }
			     var cgOrder={};
			      cgOrder.bkNo=bkNo;
			      cgOrder.trDate=adjustDate;
			      cgOrder.trAmt=trAmt*100;
			      cgOrder.trType=trType;
			      cgOrder.remark=remark;
			     PrepPayService.adjustAccountReq(cgOrder,function(backData){
			     	if(backData=="ok"){
			     		alert("银行手工调帐申请成功！");
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
            <tr><td class="title" colspan="8">&nbsp;&nbsp;&nbsp;&nbsp;银行调帐申请</td></tr>
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
                <td class="th1"  align="right" >应收商户手续费调整：</td>
                <td align="left" id="bkBalance">
                	<select id="adjustType"><option value="2">调增</option><option value="3">调减</option></select>
                </td>
            </tr>
            <tr>
                <td class="th1"  align="right" >调账金额(元)：</td>
                <td align="left" ><input id="trAmt" name="trAmt" type="text" />
                	<font color="red">*</font>(必须为含有两位小数的数字,如1.00)</td>
            </tr>
             <tr>
                <td class="th1"  align="right" >调账日期：</td>
                <td align="left" id="trDate"><input name="adjustDate" size="15" id="adjustDate" class="Wdate" type="text" 
                onfocus="WdatePicker({skin:'ext',minDate:'2000-01-01',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});"/>
                <font color="red">*</font> (该调账日期为需要调账的数据日期)</td>
            </tr>
            <tr>
                <td class="th1"  align="right" >调账原因：</td>
                <td align="left"><textarea rows="3" cols="30" id="remark"></textarea> <font color="red">*</font></td>
            </tr>
            <tr>
            <td colspan="8" align="center" style="height: 30px">
                <input class="button" type="button" value = "提 交 " onclick="adjustAccountReq();" />
            </td>
            </tr>
        </table>
        </div>
    </body>
</html>
