<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>自动结算--银行余额查询</title>
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
        
		<script type="text/javascript" src='../../dwr/engine.js'></script>
		<script type="text/javascript" src='../../dwr/util.js'></script>
		<script type="text/javascript" src="../../dwr/interface/PageService.js?<%=rand%>"></script>
		<script type="text/javascript" src='../../dwr/interface/AutoSettlementService.js?<%=rand%>'></script>
		<script type="text/javascript" src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
		<script type="text/javascript" src='../../public/js/ryt.js?<%=rand%>'></script>
		<script type="text/javascript" src='../../public/js/bankinfo/bankinfo.js?<%=rand%>'></script>
        
</head>
<body>
<div class="style">
	    <table width="100%"  class="tableBorder" >
                <tbody>
                    <tr><td class="title" colspan="4">&nbsp;&nbsp; 银行余额查询</td></tr>
                    <tr>
                        <td class="th1" align="right">&nbsp;账号：</td>
                        <td align="left" width="60%" > 	
			                  <input type="text" id="acc_no" name="acc_no"  maxlength="21" size="24px" /> &nbsp;<font color="red">*</font>
			                  &nbsp;&nbsp;&nbsp;<input type="button" value="查  询" class="button" onclick="queryBalance();"/>
                        </td>
                    </tr>
                </tbody>
    	</table>
    <div id="balanceList" style="display:none">
    <table class="tablelist tablelist2" >
        <thead><tr><th>户名</th><th>账号</th><th>余额(元)</th><th>可用余额(元)</th><th>开户行</th><th>账户类型</th><th>开户日期</th></tr></thead>
        <tbody id="balanceTable"></tbody>
    </table>
    </div>
</div>
</body>
</html>
