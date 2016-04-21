<%@ page language="java" pageEncoding="UTF-8" %>
<%@page import="com.rongyifu.mms.utils.DateUtil"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>账户流水查询</title>
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
        <script type='text/javascript' src='../../dwr/interface/PageService.js?<%=rand%>'></script>
	    <script type='text/javascript' src='../../dwr/interface/RypCommon.js?<%=rand%>'></script>
	    <script type='text/javascript' src='../../dwr/interface/SettlementService.js?<%=rand%>'></script>
  		<script type="text/javascript" src="../../public/js/settlement/admin_search_account.js?<%=rand%>"></script>
  		<script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
    </head>
    <body onload="init();">
    
      <div class="style">
    <table class="tableBorder" >
          <tbody>
              <tr><td class="title" colspan="6">&nbsp;&nbsp; 账户流水查询(2.8以前)</td></tr>
              <tr>
                  <td class="th1" align="right" width="10%">&nbsp;商户号：</td>
                  <td align="left" width="30%">&nbsp;     
                  	  <input type="text" id="mid" name="mid"  style="width:150px" size="8px" onkeyup="checkMidInput(this);"/>&nbsp;
                       <!-- <select style="width: 150px" id="smid" name="smid" onchange="initMidInput(this.value)">
                          <option value="">全部...</option>
                      </select> --> 
                    </td>
                  <td class="th1" align="right" width="10%">查询日期：</td>
                  <td align="left" width="37%">
                  <% int today = DateUtil.today();%>
                  <input type="text" value="<%=today %>" name="begin_date" id="begin_date" class="Wdate" onfocus="ryt_area_date('begin_date','end_date',0,15,0)"/>
                                       至 <input type="text" value="<%=today %>" name="end_date" id="end_date" class="Wdate"/>&nbsp;<font color="red">*</font></td>
                    <td class="th1" bgcolor="#D9DFEE" align="right" width="11%">商户状态：</td>
                <td align="left" width="20%">
                <select style="width: 150px" id="mstate" name="mstate">
                    <option value="">全部...</option>
                </select>
                </td>                     
                                       
               </tr>
               <tr align="center">
               <td colspan="6" >
               
                
              
               <input value="查询" type="button" class="button"  onclick="queryBaseAccount(1)"/>&nbsp;&nbsp;&nbsp;&nbsp;
               <input value="下载" type="button" class="button"  onclick="queryBaseAccount(-1)"/></td></tr>
          </tbody>
     </table>

    <div id="accountList" style="display: none">
    <table  class="tablelist tablelist2">
        <thead><tr role='head'><th>序号</th><th>商户号</th><th>商户简称</th><th>结算方式</th><th sort="true">交易日期</th><th  sort="true">交易时间</th><th>操作类型</th>
        <th>操作标识符</th><th>交易金额（元）</th><th>系统手续费(元)</th><th>变动金额（元）</th><th  sort="true">余额（元）</th></tr></thead>
        <tbody id="accountTable"></tbody>
    </table>

    </div> 
         </div>      
    </body>
</html>
