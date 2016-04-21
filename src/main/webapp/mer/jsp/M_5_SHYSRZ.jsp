<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
    <title>商户原始日志</title>
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
	 <script type="text/javascript" src='../../public/js/transaction/mer_jsp_queryMerchantOrigLog.js?<%=rand%>'></script>
	<script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
	<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>

</head>
<body onload="init();">
<div class="style">
 <table width="100%"  align="left"  class="tableBorder">
       <tr><td class="title" colspan="4">&nbsp;&nbsp;&nbsp;&nbsp;商户原始日志 </td></tr>
       <tr>
          <td class="th1" align="right" width="20%">系统日期：</td>
           <td align="left" width="30%">
               <input id="sys_date_begin" name="sys_date_begin"  value=""  class="Wdate" type="text" onfocus="ryt_area_date('sys_date_begin','sys_date_end',0,30,0)"/>&nbsp;至
               <input id="sys_date_end" name="sys_date_end"  value="" class="Wdate" type="text" disabled="disabled"/>
               <font color="red">*</font>
           </td>
           <td class="th1" align="right">   订单号：</td>
           <td align="left">
             <input type="text" name="oid" id="oid" size="20" maxlength="30" />
           </td>
       </tr>
       <tr><td colspan="4" align="center" height="30px">
           <input type="hidden"  name="mid" id="mid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}" />
           <input type="button" onclick="queryElog(1);" class="button" value="查  询"/>
       </td></tr>
   </table>
    <table  class="tablelist tablelist2" id="elogTable" style="display:none;">
      <thead>
         <tr  valign="middle" class="title2">
           <th>商户号</th>
           <!--<th>商户简称</th> -->
           <th>商户日期</th>
           <th>订单号</th>
           <th>原始交易金额</th>
           <th>交易类型</th>
           <th>系统日期</th>
         </tr>
      </thead> 
      <tbody id="resultList"></tbody>
     </table>
   
 </div>
 
</body>
</html>