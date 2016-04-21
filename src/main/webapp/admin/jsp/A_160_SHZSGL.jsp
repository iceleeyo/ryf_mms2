<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" errorPage="../../error.jsp"%>
<%@page import="com.rongyifu.mms.utils.DateUtil"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>商户证书管理</title>
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
    <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand %>'></script>
    <script type="text/javascript" src='../../dwr/interface/CertManagerService.js?<%=rand %>'></script>
	<script type="text/javascript" src="../../public/datePicker/WdatePicker.js?<%=rand %>"></script>
	<script type="text/javascript" src="../../public/js/GreyFrame.js?<%=rand%>"></script>
    <script type="text/javascript" src='../../public/js/ryt.js?<%=rand %>'></script>
	<script type="text/javascript" src='../../public/js/merchant/admin_cert_manager.js?<%=rand%>'></script>
	</head>
	<body>
	
	<div class="style">
		<table class="tableBorder">
			<tbody>
				<tr>
					<td class="title" colspan="4">&nbsp;&nbsp; 商户证书管理
					</td>
				</tr>
				<tr>
					<td class="th1" align="right" width="20%" style="height: 30px;">&nbsp;证书标题：</td>
					<td  align="left" >
						<input type="text" id="cert_name" name="cert_name" />&nbsp;&nbsp;
		            </td>	
					<td class="th1" align="right" width="20%">
						&nbsp;证书有效期：
					</td>
					<td  align="left">
						<input id="cert_valid_period" class="Wdate" type="text"
								onfocus="WdatePicker({skin:'ext',minDate:'1998-12-20',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});" />
					</td>
				</tr>
				<tr>
					<td class="th1" align="right" width="20%" style="height: 30px;">&nbsp;商户号：</td>
					<td  align="left" >
						<input type="text" id="mid" name="mid" />
	                  &nbsp;&nbsp;
		            </td>  
							
					<td class="th1" align="right" width="20%">
						&nbsp;
					</td>
					<td  align="left">
						&nbsp;
					</td>
				</tr>
				<tr>
					<td colspan="4"  align="center">
						<input type="button" class= "button" value="查询" onclick = "seachCertInfos(1);" />&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="button" class= "button" value="新增证书" onclick = "showFrame();" />
					</td>
				</tr>
			</tbody>
		</table>
		<table class="tablelist tablelist2" style="display: none;" id="certInfoTable">
			<tr>
				<th>商户号</th>
				<th>证书DN</th>
				<th>算法</th>
				<th>证书起始日期</th>
				<th>证书截止日期</th>
				<th>导入时间</th>
			</tr>
			<tbody id="certInfoBody">
			</tbody>
		</table>	
	</div>
</body>
</html>
