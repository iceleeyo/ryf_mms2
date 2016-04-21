<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" errorPage="../../error.jsp" isErrorPage="false"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
    <head>
    <title>结算发起</title>
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
        <script type='text/javascript' src='../../dwr/engine.js'></script> 
        <script type='text/javascript' src='../../dwr/util.js'></script>
        <script type='text/javascript' src='../../dwr/interface/LiqService.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../dwr/interface/PageService.js?<%=rand %>'></script>
        <script type='text/javascript' src='../../public/datePicker/WdatePicker.js'></script>
       	<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>"'></script>
		<script type="text/javascript" src="../../public/js/GreyFrame.js?<%=rand%>"></script>
		<script type="text/javascript" src="../../public/js/settlement/admin_jsfq.js?<%=rand%>"></script>
       	<style type="text/css">
       	.table th {
		    border: 1px solid #C8E4FC;
		    background-color: #A0A9D0;
		    font-size: 12px;
		    color: #fff;
		    padding:3px 0;
		    white-space: nowrap;
		}
		
		.table td {
		    border: 1px solid #99B3E7;
		    text-align: center;
		    font-size: 12px;
		    color: #000306;
		    line-height: 180%;
		    white-space: nowrap;
		}
       	</style>
       	<script type="text/javascript">
       	var authStr = '${sessionScope.SESSION_LOGGED_ON_USER.auth}';
       	</script>
    </head>
    <body>
     <div class="style">
         <table class="tableBorder" width="80%" align="center"  id="begin_settle" >
	       <tbody>
			<tr>
				<td colspan="2" align="left" class="title" height="25">
				<b><font color="#ffffff">&nbsp;&nbsp; 结算发起</font> </b>
				</td>
			</tr>
		      <tr>
			<td class="th1" width="30%" align="right" bgcolor="#d9dfee">&nbsp;截至日期：</td>
			<td width="70%" align="left">
			&nbsp;&nbsp;<input type="text" id="to_date" name= "exp_date" size=20 
			onfocus="WdatePicker({skin:'ext',dateFmt:'yyyyMMdd',maxDate:'%y-%M-%d',readOnly:'true'});" class="Wdate"/>
			&nbsp;&nbsp;&nbsp;&nbsp;<input  value="下一步" type="button" class="button" onclick="showLiqInfoByType();"/>
			</td>
		   </tr>
		</tbody>
	</table>
	<div class="search" style="display: none;">
		<table width="100%" class="table">
			<tr>
				<th colspan="4" style="background-color: #2B8AD0"><b>结算周期内的商户</b></th>
			</tr>
			<tr id="toAppend1">
				<th rowspan="6" width="2%" style="background-color: #A0A9D0;">满<br>足<br>结<br>算<br>发<br>起</th>
				<th style="text-align: left;padding-left: 20px;width: 10%;"><input id="toggleAll" type="checkbox" onclick="toggleAll(this);"/>商户类型</th>
				<th>商户数目</th>
				<th>操作</th>
			</tr>
			<tr>
				<td colspan="4" style="text-align: left;padding-left: 20px;"><input onclick="liqByCategory();" type="button" value="批量结算"/></td>
			</tr>
		</table>
	</div>
	<div class="search" style="display: none;">
		<table width="100%" class="table" style="margin-top:10px;">
			<tr>
				<th colspan="4" style="background-color: #2B8AD0;"><b>非结算周期内的商户</b></th>
			</tr>
			<tr id="toAppend2">
				<th width="10%">商户类型</th>
				<th>商户数目</th>
				<th>操作</th>
			</tr>
		</table>
	</div>
  </div>
</div>
</body>
</html>