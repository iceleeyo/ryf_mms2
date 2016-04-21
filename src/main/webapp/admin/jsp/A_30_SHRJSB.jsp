<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>商户日结表</title>
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
		<script type='text/javascript' src='../../dwr/interface/RypCommon.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../dwr/interface/SettlementService.js?<%=rand%>'></script>
	    <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../dwr/interface/DownloadFileService.js?<%=rand %>'></script>
		<script type='text/javascript' src='../../public/datePicker/WdatePicker.js'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>''></script>
	    <script type="text/javascript" src="../../public/js/settlement/admin_dailySettlement.js?<%=rand%>"></script>
		
</head>
<body onload="init();">
<div class="style">
<table class="tableBorder" >
  <tr><td class="title" colspan="6">&nbsp;&nbsp;商户日结表&nbsp;&nbsp;</td></tr>
  <tr>
      <td class="th1" bgcolor="#D9DFEE" align="right" width="10%" >商户号：</td>
      <td align="left" width="25%">
      			<input type="text" id="mid" name="mid" style="width: 150px" size="8px" onkeyup="checkMidInput(this);"/> &nbsp;
                  <!-- <select style="width: 145px" id="smid" name="smid" onchange="initMidInput(this.value)">
                     <option value="">全部...</option>
                 </select>  -->
         </td>
      <td class="th1" bgcolor="#D9DFEE" align="right" width="10%" >查询日期：</td>
      <td align="left" width="30%"><input id="date_begin" name="date_begin" class="Wdate" type="text" onfocus="WdatePicker({skin:'ext',minDate:'1998-12-20',maxDate:'%y-%M-{%d-1}',dateFmt:'yyyyMMdd',readOnly:'true'});" />
                                          至<input id="date_end" name="date_end"  class="Wdate" type="text" onfocus="WdatePicker({skin:'ext',minDate:'#F{$dp.$D(\'date_begin\')}',maxDate:'%y-%M-{%d-1}',dateFmt:'yyyyMMdd',readOnly:'true'});"/>
                     <font color="red">*</font></td>
               <td class="th1" bgcolor="#D9DFEE" align="right" width="10%">商户状态：</td>
                <td align="left" width="20%">
                <select style="width: 150px" id="mstate" name="mstate">
                    <option value="">全部...</option>
                </select>
                </td>            
                     </tr>
                
      <tr><td colspan="6" align="center"><input type="button" class="button"  value="查询" onclick="queryBaseDailySettlement(1)"/></td></tr>
</table>


<div id="DailySettlementList" style="display:none">
	<table class="tablelist tablelist2">
	  <thead>
	    <tr>
	    <th>结算日期</th><th>商户号</th><th>商户简称</th><th>网上支付</th><th>WAP支付</th><th>信用卡支付</th><th>消费充值卡支付</th>
	    <th>语音/快捷支付</th><th>调增</th><th>退款</th><th>调减</th><th>系统手续费</th><th>系统退回手续费</th><th>结算金额</th>
	    <th>银行手续费</th><th>银行退回手续费</th><th>收益金额</th>
	    </tr>
	  </thead>
	  <tbody id="DailySettlementTable"></tbody>
	</table>
</div>
</div>
</body>
</html>