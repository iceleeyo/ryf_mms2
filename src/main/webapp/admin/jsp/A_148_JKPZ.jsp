<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"
	errorPage="../../error.jsp" isErrorPage="false"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>监控配置</title>
<%
        response.setHeader("Pragma","No-cache");
        response.setHeader("Cache-Control","no-cache");
        response.setDateHeader("Expires", 0);
        int rand = new java.util.Random().nextInt(10000);
        %>
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css" />
<script type='text/javascript' src='../../dwr/engine.js'></script>
<script type='text/javascript' src='../../dwr/util.js'></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand %>'></script>
<script type='text/javascript' src='../../dwr/interface/MonitorConfigService.js?<%=rand%>'></script>
 <script type="text/javascript" src='../../dwr/interface/SysManageService.js?<%=rand%>'></script>
<script type='text/javascript' src='../../public/datePicker/WdatePicker.js'></script>
<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>"'></script>
        <script type="text/javascript" src="../../public/js/GreyFrame.js?<%=rand%>"></script>
<script type='text/javascript' src='../../public/js/sysmanage/admin_jkpz.js?<%=rand%>"'></script>
</head>
<body>
	<div class="style">
		<!--      搜索 -->
		<table class="tableBorder" width="80%" align="center"
			id="begin_settle">
			<tbody>
				<tr>
					<td align="left" class="title" height="25"><b>&nbsp;&nbsp;监控配置&nbsp;
					<font color="red">警告值(N),紧急值(M)</font></b>
					</td>
				</tr>
				<tr>
					<td align="left" height="25" style="padding: 10px;">
						<button onclick="config(0)">交易类型配置</button>
						<button onclick="config(1)">重要商户配置</button>
					</td>
				</tr>
			</tbody>
		</table>
		<!-- 	列表 -->
		<table id="listTable" align="left" class="tablelist tablelist2">
			<thead>
				<tr>
					<th>编号</th>
					<th>交易类型/重要商户</th>
					<th>监控对象</th>
					<th>交易银行</th>
					<th>忙时预警配置</th>
					<th>闲时预警配置</th>
					<!-- <th>当天预警配置</th> -->
					<th>操作</th>
				</tr>
			</thead>
			<tbody id="resultList"></tbody>
		</table>

	</div>
</body>
</html>
