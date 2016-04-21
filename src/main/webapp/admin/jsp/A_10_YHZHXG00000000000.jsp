<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
        <title></title>
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
        <script type='text/javascript' src='../../dwr/util.js'></script>
        <script type="text/javascript" src='../../dwr/interface/AutoSettlementService.js?<%=rand %>'></script>
		<script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
		<script type="text/javascript" src='../../public/js/bankinfo/bankinfo.js?<%=rand%>'></script>
        
</head>
<body onload="init();">
		<div class="style">
			<form action="" method="post">
				<table width="100%" align="left" class="tableBorder">
					<tr>
						<td class="title" colspan="2">
							&nbsp;&nbsp;&nbsp;&nbsp;银行账号修改&nbsp;&nbsp;
						</td>
					</tr>
					<tr >
						
						<td class="th1" align="right" width="30%">
							原账号：
						</td>
						<td align="left">
							<select style="width: 173px" id="old_no" onchange="getLocalAccInfo(this.value);">
								<option value="">全部...</option>
							</select>
							&nbsp;<font color="red">*</font>
							<!-- &nbsp;<font id="number"></font> -->
						</td>
					</tr>
					<tr >

						<td class="th1" align="right" width="30%">
							新账号：
						</td>
						<td align="left">
							<input type="text" id="new_no" name="new_no" style="height: 20px" size="25px" maxlength="21"/>
							<font color="red">*</font>
						</td>
					</tr>
					<tr >
						<td class="th1" align="right" width="30%">
							银行类型：
						</td>
						<td align="left">
							<select id="bk_type" onchange="check_bk_type(document.getElementById('old_no').value);">
								<option value="">请选择</option>
								<option value="0">合作银行</option>
								<option value="1">存管银行</option>
							</select>
							<font color="red">*</font>
						</td>
					</tr>

					<tr >
						<td class="th1" align="right" width="30%">
							户名：
						</td>
						<td align="left" id="acc_name">

						</td>
					</tr>
					<tr >
						<td class="th1" align="right" width="30%">
							币种：
						</td>
						<td align="left" id="currency">

						</td>
					</tr>
					<tr >
						<td class="th1" align="right" width="30%">
							开户日期：
						</td>
						<td align="left" id="oper_date">

						</td>
					</tr>
					<tr >
						<td class="th1" align="right" width="30%">
							账户类型：
						</td>
						<td align="left" id="acc_type">

						</td>
					</tr>
					<tr >
						<td class="th1" align="right" width="30%">
							开户行：
						</td>
						<td align="left" id="bk_name">

						</td>
					</tr>
					<tr >

						<td align="center"  colspan=2>将去掉该页面，在银行账户例如页面实现
							<input type="button" name="" value="修 改" onclick="alterAccNo();" class="button"/>
						</td>

					</tr>
				</table>
			</form>
		</div>
</body>
</html>
