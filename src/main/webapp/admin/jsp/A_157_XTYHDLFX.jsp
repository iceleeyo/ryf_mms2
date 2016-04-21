<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>系统用户登录分析</title>
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
<script type="text/javascript" src='../../dwr/interface/UserLoginAnalysisService.js?<%=rand %>'></script>
<script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand %>'></script>
<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
<script type='text/javascript' src='../../public/js/merchant/admin_user_login_analysis.js?<%=rand%>'></script>
</head>
<body>
	<div class="style">
		<form id="queryForm">
			<table class="tableBorder">
				<tbody>
					<tr>
						<td class="title" colspan="4">&nbsp;&nbsp; 用户登录分析
						</td>
					</tr>
					<tr>
						<td class="th1" bgcolor="#D9DFEE" align="right" width="15%">
							统计时间段：
						</td>
						<td align="left" width="35%">
							<input id="bdate" size="15px" name="bdate" class="Wdate" type="text" onfocus="ryt_area_date('bdate','edate',0,6,0)" /> &nbsp;至&nbsp;
							<input id="edate" size="15px" name="edate" class="Wdate" type="text" disabled="disabled" /><font color="red">*</font>
						</td>
						<td class="th1" bgcolor="#D9DFEE" align="right" width="15%"></td>
						<td width="35%"></td>
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
				<th>统计时间段</th>
				<th>登录成功（次数）</th>
				<th>登录失败（次数）</th>
				<th>成功退出（次数）</th>
			</tr>
			<tbody id="resultList">
			</tbody>
		</table>
	</div> 
</body>
</html>