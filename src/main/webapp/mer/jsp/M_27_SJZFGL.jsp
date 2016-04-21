<%@ page language="java" pageEncoding="UTF-8" %>
<%@page import="com.rongyifu.mms.bean.LoginUser"%>
<%@page import="com.rongyifu.mms.web.WebConstants"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>手机支付订单管理</title>
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
   		<script type="text/javascript" src='../../dwr/interface/MerTransactionService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>   
        
		<script type="text/javascript" src='../../public/js/transaction/mer_jsp_phone_pay.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/datePicker/WdatePicker.js'></script>
		<script type='text/javascript' src='../../public/js/ryt.js'></script>
    </head>
    <body onload="init()">
    
     <div class="style">
      <table width="100%"  align="left"  class="tableBorder">
        <tr><td class="title" colspan="8"> &nbsp; 手机支付订单生成</td></tr>
        <tr style="height: 25px">
        	<td class="th1" align="right" width="12%">订单号：</td>
        	<td align="left" width="15%">
                <input type="text" id="userOid" name="userOid" size="20" maxlength="30"/>&nbsp;<font color="red">*</font>
            </td>
            <td class="th1" align="right" width="12%">交易金额：</td>
            <td align="left" width="15%">
                <input type="text" id="transAmt" name="transAmt" size="20" maxlength="7"/>&nbsp;<font color="red">*</font>
            </td>           
            <td class="th1" align="right" width="10%">支付手机号：</td>
            <td align="left" width="15%">
            <input type="text" id="phoneNumber" name="phoneNumber" size="20" maxlength="11"/>&nbsp;<font color="red">*</font>
            </td>
            <td class="th1" align="right" width="10%"> 支付时效：</td>
            <td align="left" width="20%">
                <select id = "payTimePeriod" name = "payTimePeriod" style="width: 100px">
                     <option value="30">半小时 </option>
                     <option value="60">1小时  </option>
                     <option value="120">2小时</option>
                     <option value="240">4小时</option>
                     <option value="480">8小时</option>
                </select>&nbsp;<font color="red">*</font>
            </td>
        </tr>
        <tr style="height: 30px;"><td colspan="8" align="center"> 
            <%
            LoginUser user = (LoginUser)session.getAttribute(WebConstants.SESSION_LOGGED_ON_USER);
            %>
            <input type="hidden"  name="mid" id="mid" value="<%=user.getMid()%>" />
            <input type="hidden"  name="uid" id="uid" value="<%=user.getOperId()%>" />
            <input type="button" name="submitButton" id="submitButton" value=" 发送订单支付短信  " onclick="confirmPhonePay()"/>
        </td></tr>
   </table>
    <form name="MERPHONEPAY"  method="post" action="">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="6">&nbsp;&nbsp;&nbsp;&nbsp;手机支付订单查询&nbsp;</td></tr>
        <tr>
            <td class="th1" align="right" width="12%">
            <select id="table" name="table" style="width:auto;" onchange="initDate()">
              <option value="tlog">当天交易</option>
              <option value="hlog">历史交易</option>
            </select>
            </td>
            <td align="left" width="30%">
            <input id="bdate" name="bdate" class="Wdate" type="text" size="12px" disabled="disabled" onfocus="ryt_area_date('bdate','edate',24,30,0)" />
            &nbsp;至&nbsp;
            <input id="edate" name="edate" class="Wdate" type="text" size="12px" disabled="disabled"/>
            </td>
            <td class="th1" align="right" width="10%">交易状态：</td>
            <td align="left" width="20%">
            <select id="tstat" name="tstat" style="width: 150px">
            <option value="">全部...</option>
            </select>
            </td> 
            <td class="th1" align="right" width="10%">操作员：</td>
            <td align="left">
            <select id="operid" name="operid" style="width: 150px">
                <option value="">全部...</option>
            </select>
            </td> 
        </tr>
        <tr>
            <td colspan="8" align="center" style="height: 30px">
                <input type="hidden"  name="queryType" id="queryType" value="MERPHONEPAY" />
                <input  class="button" type="button" value = " 查 询 " onclick="queryPhonePay(1);" />
                <input  class="button" type="button" value= " 下载XLS " onclick = "downloadMobilePay();"/>
            </td>
            </tr>
        </table>
       </form>
     <table  class="tablelist tablelist2"   id="phonePayTable" style="display:none;">
            <thead>
            <tr>
            <th>电银流水号</th><th>商户号</th><th>商户订单号</th><th>交易金额(元)</th><th>交易状态</th>
            <th>系统日期</th><th>系统时间</th><th>手机号</th><th>操作员</th>
            <th>支付时效(分钟)</th>
            </tr></thead>
           <tbody id="resultList"></tbody>
       </table>
   </div>
</body>
</html>
