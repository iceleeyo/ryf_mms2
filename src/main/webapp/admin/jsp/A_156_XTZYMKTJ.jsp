<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>系统重要模块统计</title>
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
<link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="../../dwr/engine.js?<%=rand %>"></script>
<script type="text/javascript" src="../../dwr/util.js?<%=rand %>"></script>
<script type="text/javascript" src='../../dwr/interface/SystemModuleStatisticsService.js'></script>
<script type='text/javascript' src='../../public/datePicker/WdatePicker.js'></script>
<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
<script type='text/javascript' src='../../public/js/merchant/admin_system_module_statistics.js?<%=rand%>'></script>
</head>
<body>
	<div class="style">
		<form id="queryForm">
			<table class="tableBorder">
				<tbody>
					<tr>
						<td class="title" colspan="4">&nbsp;&nbsp; 系统使用情况统计
						</td>
					</tr>
					<tr>
						<td class="th1" bgcolor="#D9DFEE" align="right" width="15%">
							系统日期：
						</td>
						<td align="left" width="35%">
							<input id="bdate" size="15px" name="bdate" class="Wdate" type="text" onfocus="ryt_area_date('bdate','edate',0,6,0)" /> &nbsp;至&nbsp;
							<input id="edate" size="15px" name="edate" class="Wdate" type="text" disabled="disabled" /><font color="red">*</font>
						</td>
						<td class="th1" bgcolor="#D9DFEE" align="right" width="15%">
							重要模块：
						</td>
						<td width="35%">
							<select id="moduleId" name="moduleId">
								<option value="-1">全部</option>
								<option value="1">支付渠道维护</option>
								<option value="2">银行网关维护</option>
								<option value="3">数据库维护</option>
								<option value="4">系统参数配置</option>
								<option value="5">商户重要信息修改申请</option>
								<option value="6">手工调账请求</option>
								<option value="7">联机退款</option>
								<option value="8">退款经办</option>
							</select>
						</td>
					</tr>
					<tr>
						<td colspan="4"  align="center">
							<input type="button" class= "button" value="查询" onclick = "doQuery();" />&nbsp;&nbsp;&nbsp;&nbsp;
							<input type="button" class= "button" value="下载报表" onclick = "doDownload();" />
						</td>
					</tr>
				</tbody>
			</table>
		</form>
			<table style="display: none;" class="tablelist tablelist2" id="statisticsTable">
				<tr>
					<th>序列号</th>
					<th>模块</th>
					<th>最后一次操作时间</th>
					<th>操作次数</th>
				</tr>
				<tbody id="resultList">
				</tbody>
			</table>
	</div> 
</body>
</html>