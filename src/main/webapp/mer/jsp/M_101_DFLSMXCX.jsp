<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>出款交易查询</title>
<%
	response.setHeader("Pragma", "No-cache");
	response.setHeader("Cache-Control", "no-cache");
	response.setDateHeader("Expires", 0);
	int rand = new java.util.Random().nextInt(10000);
%>
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="../../public/css/head.css?<%=rand%>" rel="stylesheet"
	type="text/css" />
<link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet"
	type="text/css" />
<script type='text/javascript' src='../../dwr/engine.js?<%=rand%>'></script>
<script type='text/javascript' src='../../dwr/util.js?<%=rand%>'></script>
<script type="text/javascript"
	src='../../dwr/interface/PageService.js?<%=rand%>'></script>
<script type="text/javascript"
	src='../../dwr/interface/TransactionService.js?<%=rand%>'></script>
<script type='text/javascript'
	src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
<script type="text/javascript"
	src='../../public/js/transaction/mer_DaifutransactionForQueryDetail.js?<%=rand%>'></script>

</head>
<body onload="init()">

	<div class="style">
		<form name="MERHLOG" method="post" action="">
			<table width="100%" align="left" class="tableBorder">
				<tr>
					<td class="title" colspan="6">&nbsp;&nbsp;&nbsp;&nbsp;出款交易查询&nbsp;&nbsp;</td>
				</tr>
				<tr>

					<td class="th1" bgcolor="#D9DFEE" align="right" width="11%">商户订单号：</td>
					<td><input type="text" id="oid" name="oid"
						style="width: 150px" size="8px" /></td>
					<td class="th1" align="right" align="right">批次号：</td>
					<td><input type="text" id="batchNo" name="batchNo" maxlength="25" />
					</td>
					<td class="th1" bgcolor="#D9DFEE" align="right" width="11%">电银流水号：</td>
					<td><input type="text" id="tseq" name="tseq"
						style="width: 150px" maxlength="20" size="8px" />
					</td>

				</tr>
				<tr>
				<td class="th1" align="right" align="right">交易类型：</td>
					<td><select style="width: 150px" id="type" name="type">
							<option value="">全部...</option>
					</select>
					</td>
					<td class="th1" bgcolor="#D9DFEE" align="right" width="11%"><select
						style="width: 80px" id="date" name="date">
							<option value="sys_date">系统日期</option>
							<option value="mdate">商户日期</option>
					</select></td>
					<td align="left"><input id="bdate" size="15px" name="bdate"
						class="Wdate" type="text"
						onfocus="ryt_area_date('bdate','edate',0,30,0)" /> &nbsp;至&nbsp;
						<input id="edate" size="15px" name="edate" class="Wdate"
						type="text" disabled="disabled" /><font color="red">*</font></td>
					<td class="th1" bgcolor="#D9DFEE" align="right" width="11%">交易状态：</td>
					<td align="left" width="20%"><select style="width: 150px"
						id="tstat" name="tstat">
							<option value="">全部...</option>
					</select></td>
					
				</tr>
				<tr>
					<td colspan="6" align="center" style="height: 30px"><input
						type="hidden" name="queryType" id="queryType" value="MERHLOG" />
						<input class="button" type="button" value=" 查 询 "
						onclick="querypaymentHlog(1);" />&nbsp; <!-- 
                <input style="width: 90px;height: 25px;margin-right: 10px" type="button" value = " 下载TXT "  onclick="query('txt', '');"/>
                 --> <input class="button" type="button" value="下载XLS "
						onclick="downloadpaymentDetail();" /></td>
				</tr>
			</table>
		</form>

		<table class="tablelist tablelist2" id="merHlogTable"
			style="display: none;">
			<thead>
				<tr>
					<th>电银流水号</th>
					<th>商户号</th>
					<th>商户简称</th>
					<th>账户号</th>
					<th>商户订单号</th>
					<th>批次号</th>
					<%--<th>商户日期</th>
             --%>
					<th>交易金额(元)</th>
					<th>系统手续费(元)</th>
					<th>交易状态</th>
					<!-- <th>对账状态</th> -->
					<th>交易类型</th>
					<th>交易银行</th>
					<th>订单时间</th>
					<th sort="false">失败原因</th>
				</tr>
			</thead>
			<tbody id="resultList"></tbody>
		</table>

		<table id="hlogDetail" class="tableBorder detailBox5"
			style="display: none;">
			<!--  <tr><td class="title" colspan="6" align="center">商户订单详细资料</td></tr> -->
			<tr>
				<td align="left" class="th1">电银流水号：</td>
				<td align="left" id="v_tseq"></td>
				<td align="left" class="th1" width="17%">商户号：</td>
				<td align="left" width="17%" id="v_mid"></td>
				<td align="left" class="th1" width="17%">商户简称：</td>
				<td align="left" width="" id="v_midName"></td>
			</tr>
			<tr>
				<td align="left" class="th1">账户号：</td>
				<td align="left" id="v_aid"></td>
				<td align="left" class="th1">订单号：</td>
				<td align="left" id="v_oid"></td>
				<td align="left" class="th1">批次号：</td>
				<td align="left" id="v_batch"></td>
			</tr>
			<tr>
				<td align="left" class="th1">交易金额：</td>
				<td align="left" id="v_amount"></td>
				<td align="left" class="th1">系统手续费：</td>
				<td align="left" id="v_fee_amt"></td>
				<td align="left" class="th1">交易状态：</td>
				<td align="left" id="v_tstat"></td>
			</tr>

			<tr>
				<td align="left" class="th1">交易类型：</td>
				<td align="left" id="v_type"></td>
				<td align="left" class="th1">支付网关：</td>
				<td align="left" id="v_gateName"></td>
				<td align="left" class="th1">实际支付金额：</td>
				<td align="left" id="v_payamt"></td>
			</tr>

			<tr>
				<td align="left" class="th1">订单时间：</td>
				<td align="left" id="v_sys_time"></td>
				<td align="left" class="th1">对方银行行号：</td>
				<td align="left" id="to_bank_no"></td>
				<td align="left" class="th1">对方银行账户名：</td>
				<td align="left" id="v_to_acc_name"></td>
			</tr>

			<tr>	
				<td align="left" class="th1">对方银行账号：</td>
				<td align="left" id="v_card_no"></td>
				<td align="left" class="th1">失败原因：</td>
				<td align="left" id="v_err_msg" colspan="5"></td>
			</tr>
			<tr>
				<td colspan="6" height="30px" align="center"><input
					type="button" value="返回" class="wBox_close button" /> <!-- <input type="button" onclick="fanhui()" value = "返  回" class="button"/>-->
					<br />
				</td>
			</tr>
		</table>
	</div>
</body>
</html>
