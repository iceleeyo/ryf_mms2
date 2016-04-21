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
        <script type='text/javascript' src='../../dwr/interface/SettlementService.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../dwr/interface/PageService.js?<%=rand %>'></script>
        <script type='text/javascript' src='../../public/datePicker/WdatePicker.js'></script>
       	<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>"'></script>
		<script type="text/javascript" src="../../public/js/GreyFrame.js?<%=rand%>"></script>
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
    </head>
    <body>
     <div class="style">
         <table class="tableBorder" width="80%" align="center"  id="begin_settle">
	       <tbody>
			<tr>
				<td colspan="2" align="left" class="title" height="25">
				<b><font color="#ffffff">&nbsp;&nbsp; 结算发起</font> </b>
				</td>
			</tr>
		      <tr>
			<td class="th1" width="30%" align="right" bgcolor="#d9dfee">&nbsp;截至日期：</td>
			<td width="70%" align="left">
			&nbsp;&nbsp;<input type="text" id="exp_date" name= "exp_date" size=20 
			onfocus="WdatePicker({skin:'ext',dateFmt:'yyyyMMdd',maxDate:'%y-%M-%d',readOnly:'true'});" class="Wdate"/>
			&nbsp;&nbsp;&nbsp;&nbsp;<input  value="下一步" type="button" class="button" onclick="nextStep()"/>
			</td>
		   </tr>
		</tbody>
	</table>
	<table width="100%" class="table">
		<tr>
			<th colspan="5" style="background-color: #2B8AD0"><b>结算周期内的商户</b></th>
		</tr>
		<tr>
			<th rowspan="6" width="2%" style="background-color: #A0A9D0;">满<br>足<br>结<br>算<br>发<br>起</th>
			<th><input type="checkbox" onclick="toggleAll(this);"/></th>
			<th>商户类型</th>
			<th>商户数目</th>
			<th>操作</th>
		</tr>
		<tr>
			<td><input type="checkbox" name="check"/></td>
			<td>RYF商户</td>
			<td>200</td>
			<td><a>结算</a><a style="padding-left: 10px;">详情</a></td>
		</tr>
		<tr>
			<td><input type="checkbox" name="check"/></td>
			<td>POS商户</td>
			<td>200</td>
			<td><a>结算</a><a style="padding-left: 10px;">详情</a></td>
		</tr>
		<tr>
			<td><input type="checkbox" name="check"/></td>
			<td>VAS商户</td>
			<td>200</td>
			<td><a>结算</a><a style="padding-left: 10px;">详情</a></td>
		</tr>
		<tr>
			<td><input type="checkbox" name="check"/></td>
			<td>代理商</td>
			<td>200</td>
			<td><a>结算</a><a style="padding-left: 10px;" onclick="showDetails()">详情</a></td>
		</tr>
		<tr>
			<td colspan="4" style="text-align: left;padding-left: 20px;"><input type="button" value="批量结算"/></td>
		</tr>
	</table>
	<table width="100%" class="table" style="margin-top:10px;">
		<tr>
			<th colspan="4" style="background-color: #2B8AD0;"><b>非结算周期内的商户</b></th>
		</tr>
		<tr>
			<th width="10%">商户类型</th>
			<th>商户数目</th>
			<th>操作</th>
		</tr>
		<tr>
			<td>RYF商户</td>
			<td>200</td>
			<td><a onclick="showDetails()">详情</a></td>
		</tr>
		<tr>
			<td>POS商户</td>
			<td>200</td>
			<td><a onclick="showDetails()">详情</a></td>
		</tr>
		<tr>
			<td>VAS商户</td>
			<td>200</td>
			<td><a onclick="showDetails()">详情</a></td>
		</tr>
		<tr>
			<td>代理商</td>
			<td>200</td>
			<td><a onclick="showDetails()">详情</a></td>
		</tr>
	</table>
  </div>
</div>
</body>
</html>
