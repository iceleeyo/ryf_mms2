<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"  errorPage="../../error.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>网关对应商户交易款</title>
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
<script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
<script type="text/javascript" src='../../dwr/interface/SettlementService.js?<%=rand%>'></script>
<script type="text/javascript" src="../../public/js/settlement/admin_merge_amount.js?<%=rand %>"></script>
<script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand %>'></script>
<script type='text/javascript' src='../../public/js/ryt.js?<%=rand %>'></script>

</head>
<body onload="initGateRouteOptions();">
<div class="style">
 <form name="bank_form"  method="post" style="border:solid 1px white;">
    <table class="tableBorder" width="80%" align="center"  cellpadding="0" cellspacing="0">
    <tbody>
        <tr><td class="title" colspan="2">&nbsp;&nbsp; 网关对应商户交易款</td></tr>
        <tr><td width="10%" valign="middle" align="right" class="th1" height="25px">选择银行：
            
        </td>
        <td><select  name="gateRoutes" id="gateRoutes" >
        	<option  value="">请选择&nbsp;</option>
        </select>
        <font color="red">*</font>
<!-- 
            <input type="checkbox" name="pnrType"  value="1" >汇付信用卡&nbsp;
            <input type="checkbox" name="pnrType"  value="4">汇付网银&nbsp;
            <input type="checkbox" name="author_type"  value="2" >环迅&nbsp;
            <input type="checkbox" name="author_type"  value="3" >高阳捷迅&nbsp; -->
        </td>
    </tr>
    <tr>
        <td width="10%" valign="top" align="right" class="th1">系统日期：</td>
        <td width="90%" valign="top" align="left" class="th1">
            <input type="text" id="bdate" name= "bdate" size=20  onfocus="ryt_area_date('bdate','edate',0,31,-1)" class="Wdate"/>&nbsp;&nbsp;至&nbsp;&nbsp;
            <input type="text" id="edate" name= "edate" size=20  class="Wdate" disabled="disabled" />
        <font color="red">*</font>
        </td>
    </tr>
    <tr>
        <td width="10%" valign="top" align="right" class="th1"></td>
        <td width="90%" valign="top" align="left" class="th1">
        <input name="search" id="search" type="hidden" value="yes">
        <input type="button" value="查  看" onclick=" return checkGate(1);" class="button">&nbsp;&nbsp;&nbsp;
        <input type="button" value="下载XLS文件" onclick="return checkGate(-1);" class="button">&nbsp;&nbsp;&nbsp;
        </td>
    </tr>
        </tbody>
    </table>
</form>
<div>&nbsp;</div>
    <table class="tablelist" width="80%"  id="mergeAmountDetail" style="display:none;">
 <thead><tr><th>银行</th><th>商户号</th><th>商户简称</th><th>支付金额</th><th >退款金额</th><th>银行手续费</th><th>银行退回的手续费</th><th>银行划款额</th></tr></thead>
    <tbody  id="mergeAmountBody"> </tbody>
    </table>

</div>
 </body>
</html>
