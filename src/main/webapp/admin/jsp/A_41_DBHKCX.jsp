<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title></title>
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
        <link href="../../public/css/head.css?<%=rand %>" rel="stylesheet" type="text/css"/>
        <script type='text/javascript' src='../../dwr/engine.js'></script>
        <script type='text/javascript' src='../../dwr/util.js'></script>
		<script type="text/javascript" src='../../dwr/interface/AutoSettlementService.js?<%=rand %>'></script>
		<script type='text/javascript' src='../../public/datePicker/WdatePicker.js'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
		<script type="text/javascript" src='../../public/js/settlement/payment_query.js?<%=rand%>'></script>
        
</head>
<body>
<div class="style">

    <table class="tableBorder" >
            <tr><td class="title" colspan="2">&nbsp;单笔划款结果查询</td></tr>
            <tr>
                <td class="th1" align="right" width="20%">
                		<select id="seqType">
	                		<option value="2">银行流水号</option>
	                		<option value="1">电银流水号</option>
                		</select>&nbsp;</td>
                <td>
                      <input type="text" id="webSerialNo" name="webSerialNo"  size="22px"/>&nbsp;<font color="red">*</font>&nbsp;&nbsp;

                      <input type="button" class="button" value="查  询 " onclick="getSinglePayInfo();"></input>
                </td>
            </tr>
    </table>
	<table width="100%" class="tableBorder" style="display:none;" id="perInfo">
		<tr>
			<td class="title" colspan="4">
				&nbsp;&nbsp;&nbsp;&nbsp;单笔划款信息&nbsp;&nbsp;
			</td>
		</tr>
		<tr style="height: 30px">
			<td class="th1" align="right" width="15%">原流水号：&nbsp;</td>
			<td width="22%" align="left" id="oldSerialNo"></td>
			<td class="th1" align="right" width="15%">收款人账号：&nbsp;</td>
			<td width="22%" align="left"  id="receAccNo"></td>
		</tr>
		<tr style="height: 30px">
			<td class="th1" align="right" width="15%">交易状态：&nbsp;</td>
			<td width="22%" align="left"  id="state"></td>
			<td class="th1" align="right" width="15%">收款方行号：&nbsp;</td>
			<td width="22%" align="left"  id="receBankNo"></td>
		</tr>
		<tr style="height: 30px">
			<td class="th1" align="right" width="15%">交易信息：&nbsp;</td>
			<td width="22%" align="left"  id="errMsg"></td>
			<td class="th1" align="right" width="15%">金额：&nbsp;</td>
			<td width="22%" align="left"  id="money"></td>
		</tr>
		<tr style="height: 30px">
			<td class="th1" align="right" width="15%">付款人账号：&nbsp;</td>
			<td width="22%" align="left"  id="payAccNo"></td>
			<td class="th1" align="right" width="15%">电银流水号：&nbsp;</td>
			<td width="22%" align="left"  id="rypSerialNo"></td>
		</tr>
		
		<tr style="height: 30px">
			<td class="th1" align="right" width="15%">付款行号：&nbsp;</td>
			<td width="22%" align="left"  id="payBankNo"></td>
			<td class="th1" align="right" width="15%"></td>
			<td width="22%" align="left"  id=""></td>
		</tr>
	</table>
</div>
</body>
</html>
