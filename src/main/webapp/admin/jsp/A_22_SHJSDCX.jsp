<%@ page language="java" pageEncoding="UTF-8" %>
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
        <script type="text/javascript" src="../../dwr/interface/PageService.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../dwr/interface/RypCommon.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../dwr/interface/SearchFeeLiqBathService.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../dwr/interface/SettlementService.js?<%=rand%>"></script>
 		<script type="text/javascript" src="../../public/js/settlement/search_settlement.js?<%=rand%>"></script>
		<script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script> 
	</head>
	<body onload="initParams();">
	 <div class="style">
	<table class="tableBorder" >
      <tr><td class="title" colspan="6">&nbsp;&nbsp;&nbsp; 商户结算单查询</td></tr>
		<tr>
 		  <td class="th1" bgcolor="#D9DFEE" align="right" width="10%" height="30px">商户类型：</td>
          <td align="left"  width="30%">&nbsp;
				<select name="merType" id="merType">
					<option value="">全部...</option>
					<option value="0">RYF商户</option>
					<option value="1">VAS商户</option>
					<option value="2">POS商户</option>
				</select>
			</td>
 		  <td class="th1" bgcolor="#D9DFEE" align="right" width="10%" height="30px">商户号：</td>
          <td align="left"  width="20%">&nbsp; <input type="text" id="mid" name="mid"  style="width:150px"  size="8px" /></td>
          <td width="10%" align="right" class="th1">&nbsp;结算渠道：</td>
			<td align="left">&nbsp;
				<select name="liqGid" id="liqGid" >	   
					<option value="1">手工结算 -结算到电银账户</option>
					<option value="2">手工结算 -结算到银行卡</option>
					<option value="3">自动结算</option>
					<option value="4">自动代付</option>
					<option value="5">账户系统-企业</option>
					<option value="6">账户系统-个人</option>
				</select>
			</td>
		</tr>
		<tr>
			<td width="10%" align="right" class="th1">&nbsp;结算日期：</td>
			<td align="left">&nbsp;
				<input name="bdate" id="bdate" class="Wdate" type="text" onfocus="ryt_area_date('bdate','edate',0,30,0)"/>
			    至&nbsp;<input name="edate" id="edate" class="Wdate" type="text"/>
			<font color="red">*</font></td>
			<td width="10%" align="right" class="th1">支付渠道：</td>
			<td align="left">&nbsp;
			        <select style="width: 150px" id="gateRouteId"
						name="gateRouteId">
							<option value="">全部...</option>
					</select>
		     </td>
			<td width="10%" align="right" class="th1"></td>
			<td align="left"></td>
		</tr>
		
		<tr>
		  <td colspan="6" align="center" >
			<input type="hidden" name="radio_batch" id="radio_batch" value="nodata"/>
	        <input type="hidden" name="radio_gate" id="radio_gate" value="nodata"/>
			<input type="button" class="button" value=" 查 询 " onclick="queryBaseSettlement(1)"/>
			<input type="button" class="button" value=" 打 印 " onclick="printTable();"/>
		</td>
		
		</tr>
</table>
<div id="DivOne" style="display: none">
<form name="FormOne" id="FormOne">
<table class="tablelist tablelist2">
	<thead><tr>
	<th>选择</th>
	<th>商户号</th><th>商户类别</th><th>结算类型</th><th>批次号</th><th>初始日期</th><th>截止日期</th><th>制表日期</th><th>商户名称</th>
	<th>账户名</th><th>账户号</th><th>交易金额</th><th>交易笔数</th><th>退款金额</th><th>退款笔数</th><th>手工调增金额</th>
	<th>手工调增笔数</th><th>手工调减金额</th><th>手工调减笔数</th><th>系统手续费</th><th>退回商户手续费</th><th>应结算金额</th><th>支付渠道</th>
	</tr></thead>
    <tbody id="TableOne"></tbody>
</table>
<table class="tableBorder" >
     <tbody id="pageTableOne"></tbody>
</table>
</form>
</div>
<div id="DivTen" style="display: none">
<form name="FormTen" id="FormTen">
<table class="tablelist tablelist2">
	<thead><tr>
	<th>选择</th>
	<th>商户号</th><th>商户类别</th><th>结算类型</th><th>批次号</th><th>初始日期</th><th>截止日期</th><th>制表日期</th><th>商户名称</th>
	<th>账户名</th><th>账户号</th><th>交易金额</th><th>交易笔数</th><th>退款金额</th><th>退款笔数</th><th>手工调增金额</th>
	<th>手工调增笔数</th><th>手工调减金额</th><th>手工调减笔数</th><th>系统手续费</th><th>退回商户手续费</th><th>应结算金额</th>
	</tr></thead>
    <tbody id="TableTen"></tbody>
</table>
<table class="tableBorder" >
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
<table class="tableBorder" >
     <tbody id="pageTableTwo"></tbody>
</table>
</form>
</div>

<div id="DivThree" style="display: none">
<table class="tablelist tablelist2">
    <thead><tr><th>商户号</th><th>商户简称</th><th>商户交易日期</th><th>订单号</th><th>交易金额</th><th>系统手续费</th>
            <th>交易类型</th><th>系统日期</th><th>交易流水号</th><th>银行</th></tr>
    </thead>
    <tbody id="TableThree"></tbody>
</table>
<table class="tablelist tablelist2">
     <tbody id="pageTableThree"></tbody>
</table>
</div>

</div>
</body>
</html>
