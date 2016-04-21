<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>银企直连余额查询</title>
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
<link href="../../public/css/head.css?<%=rand%>" rel="stylesheet"
	type="text/css" />
<link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet"
	type="text/css" />
<script type="text/javascript" src="../../dwr/engine.js?<%=rand %>"></script>
<script type="text/javascript" src="../../dwr/util.js?<%=rand %>"></script>
<script type="text/javascript"
	src='../../dwr/interface/AdminZHService.js?<%=rand%>'></script>
<script type="text/javascript"
	src="../../public/js/adminAccount/admin_b2egateqb.js?<%=rand%>"></script>
<script type="text/javascript"
	src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
<script type="text/javascript"
	src="../../dwr/interface/DaiFuService.js?<%=rand%>'>"></script>
<script type="text/javascript"
	src="../../dwr/interface/BalanceQueryService.js?<%=rand%>'>"></script>
</head>
<body onload="init()">
	<div class="style">
		<table class="tablelist tablelist2" id="yqzlwgwhTable">
			<thead>
				<tr>
					<th>网关名称
						<th>银行行号</th>
					<th>开户省份</th>
						<th>开户帐号</th>
					<th>开户名称</th>
					<th>操作</th>
				</tr>
			</thead>
			<tbody id="resultList">
			</tbody>
		</table>
	</div>
</body>
</html>




