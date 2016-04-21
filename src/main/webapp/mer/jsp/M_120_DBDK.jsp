<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
<title>单笔代扣</title>
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
		<link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css" />
		<link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css" />
		<script type="text/javascript" src="../../dwr/engine.js"></script>
		<script type="text/javascript" src="../../dwr/util.js"></script>
		<script type='text/javascript' src="../../public/js/ryt.js?<%=rand%>"></script>
		<script type='text/javascript' src="../../public/js/money_util.js?<%=rand%>"></script>
		<script type='text/javascript' src="../../public/js/ryt_util.js?<%=rand%>"></script>
		<script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand%>'></script>
		<script type="text/javascript" src="../../public/js/merchant/mer_dbdk.js?<%=rand%>"></script>
		<script type="text/javascript" src='../../dwr/interface/DaiKouService.js?<%=rand%>'></script>
		</head>
<body onload="init();">
<div class="style">
		<div id="mainDiv">
			<table width="100%" align="left" class="tableBorder" id="orderInputTable">
				<tr>
					<td class="title" colspan="3"> 单笔代扣 <span
						style=color:red>*</span>(为必填项)</td>
				</tr>
				<tr>
					<td class="th2" align="right">&nbsp; 扣款人账号：</td>
					<td  align="left" style="width: 204px"> 
						<input id="dk_acc_no" name="dk_acc_no" maxlength="19" class="largeInput" value="" />
					</td>
					<td align="left">
					<span class="redPoint">*</span>
					</td>
				</tr>
				
				<tr>
					<td class="th2" align="right">扣款人户名：</td>
					<td align="left">
						 <input id="dk_acc_name" name="dk_acc_name" maxlength="4" class="largeInput" value="" />
					</td>
					<td align="left">
					<span class="redPoint">*</span>
					</td>
				</tr>
				
				<tr>
					<td class="th2" align="right">扣款人开户行号：</td>
					<td align="left">
						 <input id="dk_bank_no" name="dk_bank_no" maxlength="13" class="largeInput" value="" />
					</td>
					<td align="left">
					<span class="redPoint">*</span>
					</td>
				</tr>
				
				<tr>
					<td class="th2" align="right">扣款人证件类型：</td>
					<td align="left">
					<select style="width: 204px;" class="largeSelect" id="dk_ID_type" name="dk_ID_type">
							<option value="">请选择...</option>
					</select>
					</td>
					<td align="left">
					<span class="redPoint">*</span>
					</td>
				</tr>
				
				<tr>
					<td class="th2" align="right">扣款人证件号码：</td>
					<td align="left">
						 <input id="dk_ID_no" name="dk_ID_no" maxlength="18" class="largeInput" value="" />
					</td> 
					<td align="left">
					<span class="redPoint">*</span> 实际以代扣银行支持证件为准
					</td>
				</tr>
				
				<tr>
					<td class="th2" align="right">卡/折标志：</td>
					<td align="left" >
					<select style="width: 204px;" class="largeSelect" id="dk_KZ_type" name="dk_KZ_type">
							<option value="">请选择...</option>
							<option value="0">卡</option>
							<option value="1">折</option>
					</select>
					</td>
					<td align="left">
					<span class="redPoint">*</span>
					</td>
				</tr>
				
				<tr>
					<td class="th2" align="right">交易金额：</td>
					<td align="left">
						 <input id="dk_amt" name="dk_amt" maxlength="15" class="largeInput" value="" onchange="setChineseMoney(this.value);"/>
					</td> 
					<td align="left">
					<span class="redPoint">*</span> &nbsp;&nbsp;<span id="ChineseMoney" class="redfont"></span>
					</td>
				</tr>
				 
				<tr>
					<td class="th2" align="right">用途：</td>
					<td align="left" colspan="2">
					<textarea rows="3" cols="30" id="remark" name="remark" class="largerArea"></textarea>
					</td>
				</tr>
				 
				<tr>
					<td colspan="3" align="center" style="height: 30px">
					<input class="button" type="button" value="提交订单" onclick="saveOrderInfo();" />
					</td>
				</tr>
			</table>
			<input type="hidden" id="confirmIDType" value=""/>
			<input type="hidden" id="confirmKZType" value=""/> 
			<input type="hidden" id="confirmBankNo" value=""/> 
			<div style="display: none;width: 100%" id="confirmPayMsgDiv">
				<table width="100%" align="left" class="tableBorder">
					<tr>
						<td class="title" colspan="2"> 付款确认 </td>
					</tr>
					<tr>
						<td class="th2" align="right" width="35%">订单号：</td>
						<td class="largeTh" align="left" id="confirm_ordId"></td>
					</tr>
					<tr>
						<td class="th2" align="right">交款金额：</td>
						<td align="left" class="largeTh"><span id="confirm_payAmt"
							class="redfont"></span> 元</td>
					</tr>
					<!--  
					<tr>
						<td class="th2" align="right">手续费：</td>
						<td align="left" class="largeTh"><span id="confirm_feeAmt"
							class="redfont"></span> 元</td>
					</tr>
					 -->
					<tr>
						<td class="th2" align="right">扣款人账号：</td>
						<td class="largeTh" align="left" id="confirm_accNo"></td>
					</tr>
					<tr>
						<td class="th2" align="right">扣款人户名：</td>
						<td class="largeTh" align="left" id="confirm_accName"></td>
					</tr>

					<tr>
						<td class="th2" align="right">扣款人开户银行：</td>
						<td class="largeTh" align="left" id="confirm_bankNo"></td>
					</tr>
					
					<tr>
						<td class="th2" align="right">扣款人证件类型：</td>
						<td class="largeTh" align="left" id="confirm_ID_Type"></td>
					</tr>
					
					<tr>
						<td class="th2" align="right">扣款人证件号码：</td>
						<td class="largeTh" align="left" id="confirm_ID_No"></td>
					</tr>
					
					<tr>
						<td class="th2" align="right">卡/折标志：</td>
						<td class="largeTh" align="left" id="confirm_KZ_Type"></td>
					</tr>

					<tr>
						<td class="th2" align="right">订单备注：</td>
						<td class="largeTh" align="left" id="confirm_remark"></td>
					</tr>
					<tr align="center">
						<td colspan="2">
						<input id="confirmButton" name="confirmButton" style="height: 28px;" type="button" value=" 确 认 提 交 " onclick="confirmSubmit();" /> &nbsp;
					    <input style="height: 28px;" type="button" value=" 返 回 " onclick="back();" />
						</td>
					</tr>
				</table>
			</div>
		</div>
	</div>
</body>
</html>