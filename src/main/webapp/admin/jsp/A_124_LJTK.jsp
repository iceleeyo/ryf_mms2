<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>联机退款管理</title>
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
 <script type='text/javascript' src='../../dwr/util.js?<%=rand %>'></script>
 <script type='text/javascript' src='../../dwr/interface/PageService.js?<%=rand %>'></script>
 <script type='text/javascript' src='../../dwr/interface/OnlineRefundService.js?<%=rand %>'></script>
 <script type="text/javascript" src='../../dwr/interface/DownloadFileService.js?<%=rand%>'></script>
 <script type='text/javascript' src='../../dwr/interface/DoSettlementService.js?<%=rand %>'></script>
 <script type="text/javascript" src="../../public/js/refundment/admin_lainji_refund.js?<%=rand%>"></script>
 <script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
 <script type='text/javascript' src='../../public/datePicker/WdatePicker.js'></script>

</head>

<body onload="Onit()">

	<table class="tableBorder" id="LjRefund">
		<tr>
			<td class="title" colspan="6">&nbsp;&nbsp;&nbsp;&nbsp;联机退款</td>
		</tr>
		<tr>
			<td class="th1" align="right" width="10%">&nbsp;商户号：</td>
			<td align="left"><input type="text" id="mid" name="mid" style="width: 150px;" /></td>
			<td class="th1" align="right" width="10%">&nbsp;退款确认日期：</td>
			<td align="left">
			<input id="date_begin" name="date_begin" class="Wdate" type="text" onfocus="ryt_area_date('date_begin','date_end',0,30,0)" /> 至<input
				id="date_end" name="date_end" class="Wdate" type="text" disabled="disabled" /></td>
			<td class="th1" bgcolor="#D9DFEE" align="right" width="11%">商户状态：</td>
			<td align="left" width="20%">
			    <select style="width: 150px" id="mstate" name="mstate">
					<option value="">全部...</option>
			    </select></td>
		</tr>
		<tr>
		<td class="th1" align="right" width="10%">&nbsp;联机退款状态：</td>
		 <td><select style="width: 150px" id="state" name="state">
					<option value="0">待处理</option>
					<option value="1">处理中</option>
					<option value="2">联机退款成功</option>
					<option value="3">联机退款失败</option>
					<option value="4">请求银行失败</option>
			    </select></td>
	  
	   <td class="th1" align="right" width="10%">原支付渠道：</td>
	   <td align="left"><input type="text" id="gateName" name="gateName" onblur="gateRouteIdList()"/>
	   		<select style="width: 150px" id="gateRouteId" name="gateRouteId">
            	<option value="">全部...</option>
            </select></td>
             <td class="th1" align="right" width="10%">交易流水号:</td>
	   <td align="left"><input type="text" id="tseq" name="tseq" style="width: 150px;" /></td>
		</tr>
		<tr>
		<td colspan="6" align="center"><input type="button" value = "查询" name="submitQuery" class="button" onclick="queryLJRefundMotions(1);"/>&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="button" value = "下载" name="submitQuery" class="button" onclick="downOnlineRefundMotions();"/><font color="red">（只能下载当天联机退款成功的）</font></td>
		 
		</tr>
	</table>
 <form name="verifyLJRefundForm" id="verifyLJRefundForm" onsubmit=" checkSubmit();" method="post">
     <table width="100%"  class="tablelist tablelist2" id="detailResultList" style="display: none;">
     <thead><tr valign="middle" class="title2">
            <th align="center">选择</th>
            <th>退款流水号</th>
            <th>原电银流水号</th>
            <th>联机退款批次号</th>
            <th>商户号</th>
            <th>商户简称</th>
            <th>原商户订单号</th>
            <th>原支付渠道</th>
            <th>银行</th>
            <th>退款金额</th>
            <th>退回商户手续费</th>
            <th>退款状态</th> 
            <th>退款失败原因</th> 
            <th>系统日期</th>
            <th>退款确认日期</th>
            <th>申请退款原因</th>
            <th>撤销退款的原因</th>
            <th>操作</th>
        </tr></thead>
        <tbody id="resultList" ></tbody>   
     </table>
</form>
</body>
</html>
