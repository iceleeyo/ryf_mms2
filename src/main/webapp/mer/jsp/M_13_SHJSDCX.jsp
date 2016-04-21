<%@ page language="java" pageEncoding="UTF-8" %>
<% String path = request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>商户结算单查询</title>
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
        <script type='text/javascript' src='../../dwr/engine.js'></script>
        <script type='text/javascript' src='../../dwr/util.js'></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/MerSettlementService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../public/js/settlement/mer_search_settlement.js?v=<%=rand%>'></script> 
		<script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>      
    </head>
    <body onload="initParams();">
    <div class="style">
    <table class="tableBorder" >
    <tbody>
    <tr><td class="title" colspan="2"> &nbsp;&nbsp;商户结算单查询</td></tr>
    <tr>
       <td width="30%" align="right" class="th1">&nbsp;结算日期：</td>
       <td align="left">&nbsp;
           <input name="bdate" id="bdate" class="Wdate" type="text" onfocus="ryt_area_date('bdate','edate',0,30,0)"/>
           &nbsp; 至&nbsp;
           <input name="edate" id="edate" class="Wdate" type="text"/>
           <font color="red">*</font>
        </td>
        </tr>
        <tr >
            <td colspan="2" height="30px">
                <input type="hidden" name="radio_batch" id="radio_batch" value="nodata"/>
                <input type="hidden" name="radio_gate" id="radio_gate" value="nodata"/>
                <input type="hidden" id="mid" name="mid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}"/>
                <input type="button" value="查  询" style="width: 100px;margin-left: 400px;height: 25px" onclick="queryBaseSettlement(1)"/>
            </td>
        </tr>
    </tbody>
</table>
 

<div id="DivOne" style="display: none"><!-- style="display: none" -->
<form name="FormOne" id="FormOne">
<table class="tablelist tablelist2"  >
    <thead><tr><th>选择</th><th>商户号</th><th>商户简称</th><th>结算批号</th><th>结算日期</th><th>支付金额</th><th>退款金额</th>
        <th>手工调增</th><th>手工调减</th><th>系统手续费</th><th>退回商户手续费</th><th>清算金额</th></tr>
    </thead>
    <tbody id="TableOne"></tbody>
</table>
<table class="tablelist tablelist2"  width="80%" align="center" bgcolor="#ffffff" border="1" cellpadding="0" cellspacing="1">
     <tbody id="pageTableOne"></tbody>
</table>
</form>
</div>

<div id="DivTwo" style="display: none">
<form name="FormTwo" id="FormTwo">
<table class="tablelist tablelist2" >
    <thead><tr><th>选择</th><th>商户号</th><th>商户简称</th><th >银行网关</th><th>支付金额</th><th >退款金额</th><th>系统手续费</th><th>退回商户手续费</th></tr></thead>
    <tbody id="TableTwo"></tbody>
</table>
</form>
</div>
<div id="DivThree" style="display:none ">
<table class="tablelist tablelist2"  >
    <thead><tr><th>商户号</th><th>商户简称</th><th>商户交易日期</th><th>订单号</th><th>交易金额</th><th>系统手续费</th>
            <th>交易类型</th><th>系统日期</th><th>交易流水号</th><th>银行</th></tr>
    </thead>
    <tbody id="TableThree"></tbody>
</table>
</div>
</div>
</body>
</html>