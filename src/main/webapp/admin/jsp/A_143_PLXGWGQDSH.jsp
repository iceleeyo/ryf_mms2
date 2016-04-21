<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>批量修改网关渠道审核</title>
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
        <script type="text/javascript" src="../../dwr/interface/PageService.js?<%=rand %>"></script>
        <script type="text/javascript" src="../../dwr/interface/SysManageService.js?<%=rand %>"></script>  
		<script type="text/javascript" src="../../public/datePicker/WdatePicker.js?<%=rand %>"></script>
		<script type="text/javascript" src="../../public/js/ryt.js?<%=rand %>"></script>
        <script type="text/javascript" src="../../public/js/sysmanage/gateRoute.js?<%=rand %>"></script>
  </head>
  <body onload="initOptions();">
  <div class="style">
   <table class="tableBorder">
       <tr>
       		<td class="title" colspan="5">&nbsp;&nbsp;&nbsp;&nbsp;批量网关修改审核
         	</td>
       </tr>
       <tr>
       		<td class="th1" align="right" style="width:10%;">交易类型：</td>
       		<td><select id="trans_mode1"></select></td>
       		<td class="th1" align="right" style="width:10%;">支付网关：</td>
       		<td><select id="gateId"></select></td>
       		<td  style="width:20%;" align="center"><input type="button" value="查 询" onclick="cacheArgs();" /></td>
       </tr>
   </table>
      
   <table id="pageTable" class="tablelist tablelist2" style="display: none;">
   	 <thead>
		<tr>
			<th style="width: 10%;" sort="false"><input style="vertical-align: middle;" id="toggleAll" type="checkbox" onclick="toggleAll(this);" />全选</th>
			<th>交易类型</th>
			<th>支付网关</th>
			<th>申请渠道</th>
			<th>状态</th>
			<th>操作</th>
		</tr>
	 </thead>
     <tbody id="resultList"></tbody>
   </table>
      

	</div>
  </body>
</html>
