<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>交易报表统计查询</title>
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
<link href="../../public/css/head.css?<%=rand%>" rel="stylesheet"type="text/css" />
<link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet"type="text/css" />
<script type="text/javascript" src="../../dwr/engine.js?<%=rand %>"></script>
<script type="text/javascript" src="../../dwr/util.js?<%=rand %>"></script>
<script type="text/javascript" src="../../public/js/ryt.js?<%=rand %>"></script>
<script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
<script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
<script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
<script type="text/javascript" src='../../dwr/interface/QueryTradeStatisticService.js?<%=rand%>'></script>
<script type="text/javascript" src='../../public/js/transaction/admin_queryTradeStatistics.js?<%=rand%>'></script>
</head>

<body onload="init()">
	<div class="style">
		<form name="WENDSMS" method="post" action="">
			<table width="100%" align="left" class="tableBorder">
				<tr>
					<td class="title" colspan="4">&nbsp;&nbsp;&nbsp;&nbsp;交易报表统计查询&nbsp;&nbsp;</td>
				</tr>
				<tr>
					<td class="th1" bgcolor="#D9DFEE" align="right" width="15%">系统日期：</td>
					<td align="left" width="35%"><input id="bdate" size="15px" name="bdate" class="Wdate" type="text"
						onfocus="ryt_area_date('bdate','edate',0,30,0)" /> &nbsp;至&nbsp;
						<input id="edate" size="15px" name="edate" class="Wdate" type="text" disabled="disabled" /></td>
					<td class="th1" align="right" width="15%">报表类型：</td>
					<td align="left" width="35%">
						<select id="excelType" style="width: 100px;margin-left: 50px;">
							<option value="0">交易总表</option>
							<option value="1">交易分表</option>
						</select>
					</td>
				</tr>
				<tr>
					<td colspan="4" align="center">
						<input class="button" type="button" value=" 查  询  " onclick="querytrans(1)" />
						<input class="button" type="button" value=" 下载Excel " onclick="exportTransExcel()" />
					</td>
				</tr>
			</table>
		</form>
		<table class="tablelist tablelist2" id="smsTable" style="display: none;">
			<thead>
				<tr>
					<th>交易类型</th>
					<th>总笔数</th>
					<th>成功笔数</th>
					<th>成功总金额</th>
					<th>成功率</th>
				</tr>
			</thead>
			<tbody id="resultList">
			</tbody>
		</table>
		
		<table class="tablelist tablelist2" id="smsTable1" style="display: none;">
			<thead>
				<tr>
					<th>交易类型</th>
					<th>交易银行</th>
					<th>总笔数</th>
					<th>成功笔数</th>
					<th>成功金额</th>
					<th>成功率</th>
				</tr>
			</thead>
			<tbody id="resultList1">
			</tbody>
		</table>
	</div>
</body>
</html>




