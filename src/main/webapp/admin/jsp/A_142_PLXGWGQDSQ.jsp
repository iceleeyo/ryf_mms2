<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>批量修改网关渠道申请</title>
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
        <link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css"/>
        <script type='text/javascript' src='../../dwr/engine.js'></script>
        <script type='text/javascript' src='../../dwr/util.js'></script>
        <script type="text/javascript" src="../../dwr/interface/PageParam.js?<%=rand %>"></script>
        <script type="text/javascript" src="../../dwr/interface/PageService.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../dwr/interface/SysManageService.js?<%=rand%>"></script> 
		<script type="text/javascript" src="../../public/datePicker/WdatePicker.js?<%=rand %>"></script>
		<script type="text/javascript" src="../../public/js/GreyFrame.js?<%=rand%>"></script>
		<script type="text/javascript" src="../../public/js/ryt.js?<%=rand %>"></script>
        <script type="text/javascript" src="../../public/js/sysmanage/gateRoute.js?<%=rand%>"></script>
  </head>
  <body onload="initOptions();">
		<div class="style">
	   		<table class="tableBorder" >
	              <tr><td class="title" colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;批量网关修改申请
	                </td></tr>
	                 <tr>
	                   <td class="th1" align="right" width="10%" style="height: 20px">&nbsp;交易类型：</td>
	                   <td>
	                   		<select style="width: 120px;height: 20px;" onchange="queryGatesByRoute();" id="trans_mode">
	                   		</select>
                   		</td>
	                 </tr>
	                 <tr>
	                   <td class="th1" align="right" width="10%" style="height: 20px">&nbsp;支付渠道：</td>
	                   <td>
	                   		<select onchange="queryGatesByRoute();" id="gateRouteId" style="width: 120px;height: 20px;">
	                   		
	                   		</select>
                   		</td>
	                 </tr>
	                 <tr>
	                   <td class="th1" align="right" width="10%" style="height: 20px">&nbsp;支付网关：</td>
	                   <td id="gateTable">
                   	   </td>
	                 </tr>
	                 <tr>
	                   <td align="center" colspan="2"><input onclick="applySetRouteOfGate();" type="button" value="申 请"/></td>
	                 </tr>
	      	</table>
		</div>
  </body>
</html>
