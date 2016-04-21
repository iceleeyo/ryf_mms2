<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    
    <title>自动代付信息维护</title>
    
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
        <link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css"/>
        <script type='text/javascript' src='../../dwr/engine.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../dwr/util.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/AutoDFInfoPreserveService.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>' ></script>
		<script type="text/javascript" src='../../public/js/adminAccount/admin_autodfwh.js?<%=rand%>'></script> 
	    
  </head>
  
  <body onload="init()">

	<div class="style">
		<form name="AUtODF" method="post" action="">
			<table width="100%" align="left" class="tableBorder">
				<tr>
					<td class="title" colspan="6">&nbsp;&nbsp;&nbsp;&nbsp;自动代付信息维护&nbsp;&nbsp;</td>
				</tr>
				<tr>

					<td class="th1" align="right" width="10%">商户号：</td>
					<td align="left" width="25%"><input type="text" id="mid"
						name="mid" style="width: 150px" size="8px" />
					</td>
					<td class="th1" align="right" width="10%">商户状态：</td>
					<td align="left" width="25%"><select style="width: 150px"
						id="mstate" name="mstate">
							<option value="">全部...</option>
							<option value="0">正常</option>
							<option value="1">关闭</option>
					</select></td>
					<td class="th1" align="right" width="10%"></td>
					<td></td>

				</tr>
				<tr>
					<td colspan="6" align="center" style="height: 30px"><input
						class="button" type="button" value=" 查 询 "
						onclick="queryAutoDf(1);" />&nbsp;</td>
				</tr>
			</table>
		</form>

		<table class="tablelist tablelist2" id="queryAutoDfTable"
			style="display: none;">
			<thead>
				<tr>
					<th>商户号</th>
					<th>商户名称</th>
					<th>商户状态</th>
					<th>账户号</th>
					<th>自动代付状态</th>
					<th>开户银行省份</th>
					<th>开户银行名</th>
					<th>开户银行支行名</th>
					<th>开户银行行号</th>
					<th>银行账户</th>
					<th>开户帐号名</th>
					<th>结算银行</th>
					<th>操作</th>
				</tr>
			</thead>
			<tbody id="resultList"></tbody>
		</table>
		<form action="#" id="addMinfoForm">
			<table id="AutoDfWH" class="tableBorder"
				style="display:none; width: 400px; height: 400px; margin-top: 0px;">
				<tr>
					<td class="th1" bgcolor="#D9DFEE" align="right" width="35%">商户号:</td>
					<td align="left"><input type="text" id="m_mid" size="26"
						disabled="disabled" /></td>
				</tr>

				<tr>
					<td class="th1" bgcolor="#D9DFEE" align="right" width="35%">商户名称:</td>
					<td align="left"><input type="text" id="m_name" size="26"
						disabled="disabled" /></td>
				</tr>
				<tr>
					<td class="th1" bgcolor="#D9DFEE" align="right" width="35%">所属行业:</td>
					<td align="left"><input type="text" id="mer_trade_type"
						size="26" disabled="disabled" /></td>
				</tr>
				<tr>
					<td class="th1" bgcolor="#D9DFEE" align="right" width="35%">账户号:</td>
					<td align="left"><input type="text" id="aid" size="26"
						disabled="disabled" /></td>
				</tr>
				<tr>
					<td class="th1" bgcolor="#D9DFEE" align="right" width="35%">自动代付状态:</td>
					<td align="left"><select id="auto_df_state">
							<option value="0">关闭</option>
							<option value="1">开启</option>
					</select></td>
				</tr>
				<tr>
					<td class="th1" bgcolor="#D9DFEE" align="right" width="35%">开户银行省份:</td>
					<td align="left"><select id="pbkProvId">
							<option value=" ">请选择...</option>
					</select>&nbsp;&nbsp;<font color="red">*</font></td>
				</tr>
				<tr>
					<td class="th1" bgcolor="#D9DFEE" align="right" width="35%">开户银行名:</td>
					<td align="left"><input type="text" id="pbk_name" size="26" />&nbsp;&nbsp;<font
						color="red">*</font></td>
				</tr>
				<tr>
					<td class="th1" bgcolor="#D9DFEE" align="right" width="35%">开户银行支行名:</td>
					<td align="left"><input type="text" id="pbk_branch"
						size="26" />&nbsp;&nbsp;<font color="red">*</font></td>
				</tr>
				<tr>
					<td class="th1" bgcolor="#D9DFEE" align="right" width="35%">银行帐号:</td>
					<td align="left"><input type="text" id="pbk_acc_no"
						size="26" />&nbsp;&nbsp;<font color="red">*</font></td>
				</tr>
				<tr>
					<td class="th1" bgcolor="#D9DFEE" align="right" width="35%">开户账号名:</td>
					<td align="left"><input type="text" id="pbk_acc_name"
						size="26" />&nbsp;&nbsp;<font color="red">*</font></td>
				</tr>
				<tr>
					<td class="th1" bgcolor="#D9DFEE" align="right" width="35%">开户银行行号:</td>
					<td align="left" style="white-space:nowrap;overflow:hidden;word-break:break-all;"><input type="text" id="pbk_no" size="26" />&nbsp;&nbsp;<font
						color="red">*</font> <input type="button" value="检索"
						onclick="SerchBankNoInfo();" class="button" style="width: 40px" />
					</td>
				</tr>
				<tr>
					<td class="th1" bgcolor="#D9DFEE" align="right" width="35%">结算银行:</td>
					<td align="left"><select id="gateId">
							<option value="0">全部...</option>
					</select> <font color="red">*</font></td>
				</tr>
				<tr>
					<td colspan="4" align="center" height="20px;"><input
						value="提交" type="button" class="button" onclick="edit(1);" /> <input
						value="返回" type="button" class="wBox_close button" /></td>
				</tr>
			</table>
		</form>
		<table id="serchBankNo" class="tableBorder"
			style="display:none; width: 700px; height: 300px; margin-top: 0px;">
			<tr>
				<td class="th1" bgcolor="#D9DFEE" align="right" width="15%">开户行:</td>
				<td align="left"><select style="width: 150px" id="gate">
						<option value="">全部....</option>
				</select>&nbsp;&nbsp; <input type="text" id="bk_name" style="width: 150px"
					value="请输入开户行关键字检索联行行号"
					onFocus="if(value==defaultValue){value='';this.style.color='#000'}"
					onBlur="if(!value){value=defaultValue;this.style.color='#999'}"
					style="color:#999999" /> <input type="button" value="检索"
					onclick="SerchBankNo(1);" class="button" /></td>
			</tr>
			<tr>
				<td colspan="2" valign="top">
					<table class="tablelist tablelist2" id="serach">
						<thead>
							<tr style="height: 20px">
								<th width="40px"></th>
								<th align="center">联行行号</th>
								<th align="center">联行名称</th>
							</tr>
						</thead>
						<tbody id="resultListbankno"></tbody>
					</table>
				</td>
			</tr>
			<tr>
				<td colspan="2" align="left" style="height: 50px">
					说明：选择开户行所属行、输入关键字查询&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<input value="确定" type="button" class="button"
					onclick="confirmbankNo(1);" /> <input value="返回" type="button"
					class="wBox_close button" />
				</td>
			</tr>
		</table>
	</div>
</body>
</html>

