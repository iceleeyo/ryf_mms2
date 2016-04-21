<%@ page language="java" pageEncoding="UTF-8" %>
<%@page import="com.rongyifu.mms.web.WebConstants"%>
<%@page import="com.rongyifu.mms.bean.LoginUser"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>对账结果处理</title>
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
    <script type="text/javascript" src="../../dwr/engine.js"></script>
    <script type="text/javascript" src="../../dwr/util.js"></script>  
    <script type="text/javascript" src="../../dwr/interface/RypCommon.js?<%=rand%>"></script>
    <script type="text/javascript" src="../../dwr/interface/PageService.js?<%=rand%>"></script>
    <script type="text/javascript" src="../../dwr/interface/SettlementService.js?<%=rand%>"></script>
    <script type="text/javascript" src='../../dwr/interface/DownloadFileService.js?<%=rand%>'></script>
    <script type="text/javascript" src="../../public/js/settlement/admin_settle_result.js?<%=rand%>"></script>
	<script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
	<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script> 
    </head>
  <body onload="init()">
  
  
   <div class="style">
    <form method="post" action="../../downSettleResult" onsubmit=" checkDate()">
    <table class="tableBorder" >
    <tr><td class="title" colspan="4">&nbsp;&nbsp;对账结果处理</td></tr>
    <tr>
        <td class="th1" align="right" width="15%">交易日期：</td>
        <td align="left" width="35%">
            <input id="date_begin" name="date_begin"  class="Wdate" type="text" onfocus="ryt_area_date('date_begin','date_end',0,30,-1)"/>&nbsp;&nbsp;至&nbsp;
            <input id="date_end" name="date_end"  class="Wdate" type="text" disabled="disabled"/>
            <font color="red">*</font>
        </td>
        <td class="th1" align="right" width="15%">&nbsp;错误类型：</td>
        <td align="left" width="35%">
            <select id="error_type" name="error_type"> 
            <option value="-1">全部</option>
            <option value="0">失败交易</option>
            <option value="1">可疑交易</option>
            </select>
        </td>  
    </tr>
    <tr>       
        <td class="th1" align="right" width="15%">支付渠道：</td>
        <td align="left" width="35%">
        	<select id="gateRoute">
        		<option value="0">--全部--</option>
        	</select>
        </td>
        <td class="th1" align="right" width="15%">对账日期：</td>
        <td align="left" width="35%">
            <input id="check_date" name="check_date"  class="Wdate" type="text" onfocus="WdatePicker({skin:'ext',minDate:'1998-12-20',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});"/>
        </td>
     </tr>
     <tr><td align="center" colspan="4">
     <input id="search" name="search" class="button" type="button" onclick="queryBaseSettleResult(1)" value=" 查 询 "/>&nbsp;&nbsp;
     <input id="download" name="download" class="button" type="button" value="下载XLS报表" onclick="downloadResult()"/> 
     </td></tr>
    </table>
    </form>
    <br/>
    <div id="settleResultList" style="display: none">
    <form name="checkForm" id="checkForm">
    <table class="tablelist tablelist2" width="100%"  cellpadding="0" cellspacing="0">
        <thead><tr><th>选择</th><th>商户号</th><th>商户简称</th><th>电银流水号</th><th>交易日期</th><th>交易金额</th><th>交易银行</th>
        <th>银行订单号</th><th>银行金额</th><th>错误类型</th><th>处理状态</th><th>对账日期</th><th>处理说明</th><th>支付渠道</th></tr></thead>
        <tbody id="settleResultTable"></tbody>
    </table>
    <table class="tableBorder" width="80%" align="center" bgcolor="#ffffff" border="1" cellpadding="0" cellspacing="1">
        <tbody id="pageTable"></tbody>
    </table>
    <% LoginUser loginuser = (LoginUser) session.getAttribute(WebConstants.SESSION_LOGGED_ON_USER); %>
    <input type="hidden" name="loginmid" id="loginmid" value="<%=loginuser.getMid() %>"/>
    <input type="hidden" name="loginuid" id="loginuid" value="<%=loginuser.getOperId() %>"/>
    </form>
    </div> 
  </div>   
    
  </body>
</html>
