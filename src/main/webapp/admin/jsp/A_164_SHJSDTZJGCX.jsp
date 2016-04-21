<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>商户结算单通知结果查询</title>
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
<script type='text/javascript' src='../../dwr/engine.js?<%=rand%>'></script>
<script type='text/javascript' src='../../dwr/util.js?<%=rand%>'></script>
<script type="text/javascript"
	src='../../dwr/interface/PageService.js?<%=rand%>'></script>
<script type='text/javascript'
	src='../../dwr/interface/RypCommon.js?<%=rand%>'></script>
<script type='text/javascript'
	src='../../dwr/interface/SettlementVerifyService.js?<%=rand%>'></script>
<script type="text/javascript"
	src="../../public/js/settlement/admin_notice_sync_settlement.js?<%=rand%>"></script>
<script type='text/javascript'
	src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>

<script type='text/javascript'
	src='../../dwr/interface/DoSettlementService.js?<%=rand%>'></script>

<script type='text/javascript' src='../../dwr/interface/SettlementNoticeService.js?<%=rand%>'></script>
<script type='text/javascript'>
	
</script>

</head>
<body>

	<div class="style">
		<table class="tableBorder">
			<tr>
				<td class="title" colspan="6">&nbsp;商户结算单通知结果查询&nbsp;&nbsp;</td>
			</tr>
			<tr>
				<td class="th1" bgcolor="#D9DFEE" align="right" width="10%">
					商户号：</td>
				<td align="left" width="30%"><input type="text" id="mid"
					name="mid" size="8px" style="width: 150px"
					onkeyup="checkMidInput(this);" /></td>
				<td class="th1" bgcolor="#D9DFEE" align="right" height="30px">商户名称：</td>
				<td align="left"><input type="text" id="name" name="name" />
				</td>
				<td width="10%" align="right" class="th1">&nbsp;同步状态：</td>
				<td align="left">&nbsp; <select name="syncState" id="syncState">
						<option value="">全部...</option>
						<option value="0">成功</option>
						<option value="1">失败</option>
						<option value="2">处理中</option>
				</select></td>

			</tr>
			<tr>
				<td class="th1" bgcolor="#D9DFEE" align="right" height="30px">结算日期：</td>
				<td align="left"><input id="beginDate" name="beginDate"
					class="Wdate" type="text" value=""
					onfocus="WdatePicker({skin:'ext',minDate:'1998-12-20',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});" />
					至&nbsp;<input id="endDate" name="endDate" class="Wdate" type="text"
					value=""
					onfocus="WdatePicker({skin:'ext',minDate:'#F{$dp.$D(\'beginDate\')}',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});" />&nbsp;
				</td>
				<td colspan="4"></td>
			</tr>

			<tr>
				<td colspan="6" align="center"><input type="button"
					class="button" value="查询" onclick="query(1)" /> <input
					type="hidden" name="search" value="yes" /></td>
			</tr>
		</table>

		<table id="detailResultList" class="tablelist tablelist2"
			style="display: none;">
			<thead>
				<tr valign="middle" class="title2">
					<th>选择</th>
					<th>商户号</th>
					<th>商户名</th>
					<th>结算日期</th>
					<th>批次号</th>
					<th>同步结果</th>
					<th>备注</th>
				</tr>
			</thead>
			<tbody id="bodyTable"></tbody>
		</table>
		<input type="hidden" id="userAuthIndex"
			value="<%=request.getParameter("userAuthIndex")%>" />


	</div>
</body>
</html>
