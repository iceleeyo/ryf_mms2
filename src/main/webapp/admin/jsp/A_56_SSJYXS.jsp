<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="../../error.jsp" %>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%> 
<%@ page session="false" %>

<html>
<head>

<title>风险管理</title>
<%
response.setHeader("Pragma","No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", 0);
int rand = new java.util.Random().nextInt(10000);
%>
        <meta http-equiv="pragma" content="no-cache"/>
        <meta http-equiv="cache-control" content="no-cache"/>
        <meta http-equiv="Expires" content="0"/>  
        <META   http-equiv="refresh"   content="30" > 
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css"/>
        <script type='text/javascript' src='../../dwr/engine.js'></script>
		<script type='text/javascript' src='../../dwr/util.js'></script>
		<script type='text/javascript' src='../../dwr/interface/RiskmanageService.js?<%=rand%>'></script>
		<script type="text/javascript" src="../../dwr/interface/PageService.js?<%=rand %>"></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
		<script type="text/javascript" src='../../public/js/riskmanage/real_tran.js?<%=rand%>'></script> 
</head>
  <body  onload="initTable();">
  <div class="style">
 <br/>
 <div style="font-weight: bolder;"><span id="showTime"></span>&nbsp;&nbsp;&nbsp;&nbsp;
 成功交易<span id="successCount" style="color: green"></span>笔，失败交易<span id="failCount" style="color: red"></span>笔</div>
 <p align='center' id="warMsg" style="color: red;font-size: 20px;font-weight: bolder;"></p>

 <table width="50%" border="0" align="left" cellpadding="0" cellspacing="0" class="tablelist" >
  <tr >
        <td align="left" valign="top" width="50%">
            <table width="100%" border="0" cellpadding="0" cellspacing="0"><tr><td class="title" height="25px" align="center" colspan="5">最近10笔成功交易</td></tr></table>
        </td>
       
        <td align="center"  width="2%" style="border-bottom-color: white;border-top-color: white;"></td>
        <td align="left" valign="top">
            <table width="100%" border="0" cellpadding="0" cellspacing="0"><tr><td class="title" height="25px" align="center" colspan="6">失败交易</td></tr></table>
        </td>
  </tr>
  
  <tr >
        <td align="left" valign="top" >
             <table width="100%" border="0" cellpadding="0" cellspacing="0"  >
             <tr  valign="middle" class="title2"><th>系统流水号</th><th>商户号</th><th>商户订单号</th><th>交易时间</th><th>交易金额(元)</th></tr>
             <tbody id="successTransList"></tbody>
             </table>
             <!-- /marquee> -->  
        </td>
        <td align="center"  width="1%" valign="top" style="border-bottom-color: white;"></td>
        
        <td align="right" valign="top" >
          <table width="100%" border="0" cellpadding="0" cellspacing="0">
           <tr valign="middle" class="title2"><th>系统流水号</th><th>商户号</th><th>商户订单号</th><th>交易时间</th><th>交易金额(元)</th><th>网关号/银行</th><th>失败原因</th></tr>
            <tbody id="failTransList"></tbody>
          </table>
        </td>
  </tr>
</table>
</div>
</body>
</html>