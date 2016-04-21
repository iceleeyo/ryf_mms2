<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>银行账号录入</title>
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
		<script type="text/javascript" src="../../dwr/engine.js"></script>
		<script type="text/javascript" src="../../dwr/util.js"></script>
		<script type="text/javascript" src="../../dwr/interface/AutoSettlementService.js?<%=rand%>"></script>
		<script type="text/javascript" src="../../public/js/ryt.js?<%=rand%>"></script>
		<script type="text/javascript" src="../../public/js/bankinfo/bankinfo.js?<%=rand%>"></script>
	
	</head>
	<body>
		<div class="style">
			<form action="info_form" method="post">
				<table width="100%" align="left" class="tableBorder">
					<tr><td class="title" colspan="4">&nbsp;&nbsp;&nbsp;&nbsp;存管银行账号录入&nbsp;&nbsp;</td></tr>
					<tr>
						<td class="th1" align="right" width="15%">账号：</td>
						<td align="left">
							<input type="text" id="acc_no" name="acc_no" maxlength="32" style="width: 220px;"/>
							<font color="red">*</font>&nbsp;&nbsp;
							<input type="button" name="" onclick="getAccInfo();" value="查  询" class="button"/>
						</td>
					 
					<td class="th1" align="right" width="15%">银行行号：</td>
					<td align="left">
					<input id="bk_no" name="bk_no" type="text" maxlength="14" value="301290000007" />（交通银行）
					<font color="red">*</font></td>
			       </tr>
					<tr >
						<td class="th1" align="right" width="15%">银行简称：</td>
						<td align="left">交通银行(存管银行)</td>
					 
						<td class="th1" align="right" width="15%">户名：</td>
						<td align="left" id="acc_name"></td>
					</tr>
					<tr >
						<td class="th1" align="right" width="15%">币种：</td>
						<td align="left" id="currency"></td>
					 
						<td class="th1" align="right" width="15%">开户日期：</td>
						<td align="left" id="oper_date"></td>
					</tr>
					<tr >
						<td class="th1" align="right" width="15%">账户类型：</td>
						<td align="left" id="acc_type"></td>
					 
						<td class="th1" align="right" width="15%">开户行：</td>
						<td align="left" id="bk_name"></td>
					</tr>
					<tr >
						<td align="center" colspan=4>
							<input type="button" id="addInfo"  value="新  增"
								onclick="addHoldBkMsg();" disabled="disabled" class="button"/>
						</td>

					</tr>
				</table>
			</form>
			<table width="100%" align="left" class="tableBorder">
			<tr><td class="title" colspan="4">&nbsp;&nbsp;&nbsp;&nbsp;合作银行账号录入&nbsp;&nbsp;</td></tr>
			<!-- <tr>
				<td class="th1" align="right" width="30%">账号：</td>
				<td align="left"><input id="accNo2" name="accNo2" type="text" maxlength="32" style="width: 220px;"/> <font color="red">*</font></td>
			</tr>
			 
			<tr >
				<td class="th1" align="right" width="20%">合作银行：</td>
				<td align="left"><input id="bkNo2" name="bkNo2" type="text" maxlength="14"/> <font color="red">*</font></td>
				<td class="th1" align="right" width="20%">银行简称：</td>
				<td align="left"><input id="bkAbbv2" name="bkAbbv2" type="text"  maxlength="10" /> <font color="red">*</font></td>
				<td class="th1" align="right" width="20%">银行帐号：</td>
				<td align="left"><input type="text"  maxlength="30" style="width: 220px;"/></td>
				<td class="th1" align="right" width="20%">银行类型：</td>
				<td align="left">合作银行</td>
			</tr>
			-->
			
			
			<tr >
				<td class="th1" align="right" width="20%">合作银行：</td>
				<td align="left">
				<select><!-- BankNoUtil.java 中map -->
				<option >请选择...</option>
				<option >中国建设银行 </option>
				<option >中国光大银行 </option>
				<option >中国工商银行 </option>
				<option >中国民生银行 </option>
				</select>
				<font color="red">*</font></td>
				<td class="th1" align="right" width="20%">银行帐号：</td>
				<td align="left"><input type="text"  maxlength="30" style="width: 220px;"/></td>
			</tr>
			
			<tr>
				<td  colspan="4" align="center">
				   <input type="button"  value="新  增" onclick="addCooperateBk();"  class="button"/>
				</td>
			</tr>
			
			 
			</table>
			
			
			<table  class="tablelist tablelist2" id="balanceLogTable">
           <thead>
           <tr>
             <th>银行行号</th><th>银行简称</th><th>银行账号</th>
             <th>银行类型</th> 
           </tr>
           </thead>
           <tbody id="balanceLogList">
           <tr><td>301290000007</td><td>交通银行</td>
           <td>
            <a href="#" title="修改">6222600910021232112</a><!-- A_10_YHZHXG.jsp 实现功能 -->
           </td>
           
           
           <td>存管银行</td></tr>
           
           <tr><td>105100000017</td><td>中国建设银行</td><td></td><td>合作银行</td></tr>
           <tr><td>303100000006</td><td>中国光大银行</td><td></td><td>合作银行</td></tr>
           </tbody>
       </table>
		</div>
	</body>
</html>
