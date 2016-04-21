<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" errorPage="../../error.jsp"%>
<%@page import="com.rongyifu.mms.utils.DateUtil"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>操作员日志查询</title>
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
		<script type='text/javascript' src='../../dwr/interface/MerchantService.js?<%=rand %>'></script>
         <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand %>'></script>
		<script type="text/javascript" src="../../public/datePicker/WdatePicker.js?<%=rand %>"></script>
         <script type="text/javascript" src='../../public/js/ryt.js?<%=rand %>'></script>
		 <script type="text/javascript" src='../../public/js/merchant/admin_search_oper_log.js?<%=rand%>'></script>
	</head>
	<body onload="initMinfos();">
	
	 <div class="style">
	<form action="./search_oper_log.jsp" method="get">
	<table class="tableBorder">
		<tbody>
			<tr>
				<td class="title" colspan="4">&nbsp;&nbsp; 操作员日志查询
				</td>
			</tr>
		
			
			<tr>
				<td class="th1" align="right" width="20%" style="height: 30px;">&nbsp;商户号：</td>
				<td  align="left" >
					<input type="text" id="mid" name="mid"   size="8px" onkeyup="checkMidInput(this);"/>
                  &nbsp;&nbsp;<!-- <select style="width: 150px" id="smid" name="smid" onchange="initMidInput(this.value)">
                     <option value="">全部...</option>
                   </select> -->
	            </td>  
						
				<td class="th1" align="right" width="20%">
					&nbsp;操作员名：
				</td>
				<td  align="left">
					<input type="text" name="oper_name" id="oper_name" maxlength="20"/>
				</td>
			</tr>
			<tr>
				<td class="th1" align="right" width="20%" style="height: 30px;">&nbsp; 系统日期：</td>
				<td align="left" colspan="3">
                    <% int today = DateUtil.today();%>
					<input id="statd" value="<%=today %>" name="statd"  class="Wdate" type="text"  onfocus="ryt_area_date('statd','endd',3,7,0)"/>
					&nbsp;&nbsp;
					至&nbsp;&nbsp;<input id="endd" value="<%=today %>" name="endd"  class="Wdate" type="text" />
                    <font color="red">*</font>
				</td>
			</tr>
			<tr>
				<td colspan="4"  align="center">
					<input type="button" class= "button" value="查询" onclick = "seachOperInfos(1);" />&nbsp;&nbsp;&nbsp;&nbsp;
					<input type="button" class= "button" value="下载" onclick = "downloadOperLog();" />
				</td>
				
				
			</tr>
		</tbody>
	</table>
	</form>
	<table class="tablelist tablelist2" style="display: none;" id="operlogTable">
		<tr>
			<th>商户号</th><th>商户简称</th><th>操作员号</th><th>操作员名</th><th>系统日期</th><th>系统时间</th><th>操作员IP</th><th>操作</th><th>操作结果</th>
		</tr>
		<tbody id="operlogBody">
		</tbody>
	</table>
	
	</div>
</body>
</html>
