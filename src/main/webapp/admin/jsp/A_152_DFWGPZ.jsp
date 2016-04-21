<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>代付网关维护</title>
<%
response.setHeader("Pragma","No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", 0);
int rand = new java.util.Random().nextInt(10000);
%>
        <meta http-equiv="pragma" content="no-cache" />
        <meta http-equiv="cache-control" content="no-cache"/>
        <meta http-equiv="Expires" content="0"/>  
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css"/> 
        <link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css" />
        <script type="text/javascript" src="../../dwr/engine.js?<%=rand %>"></script>
        <script type="text/javascript" src="../../dwr/util.js?<%=rand %>"></script>
        <script type="text/javascript" src="../../public/js/ryt.js?<%=rand %>"></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type="text/javascript" src="../../dwr/interface/DfGateRouteService.js"></script>
        <script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
        <script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
        <script type="text/javascript" src="../../public/js/sysmanage/dfGateRoute.js?<%=rand%>"></script>
    </head>
    
    <body>
    <div class="style">
       <table class="tablelist tablelist2" id="defualtTable" >
            <tr>
            	<td class="title" style="text-align: left;" colspan="4">&nbsp;&nbsp;&nbsp;&nbsp;默认代付渠道维护（未明确代付渠道，默认走此配置）&nbsp;&nbsp;</td>
            </tr>
            <tr>
            	<td colspan="4" style="height: 10px;"></td>
            </tr>
           <tr>
             <th>交易类型</th><th>代付渠道</th><th>交易额度</th><th>操作</th>
           </tr>
           <tbody id="defaultTbody">
           </tbody>
       </table>
		<!-- 搜索 -->
        <form id="queryForm"  method="post" action="">
         <table width="100%"  align="left"  class="tableBorder">
            <tr>
            	<td class="title" style="text-align: left;" colspan="6">&nbsp;&nbsp;&nbsp;&nbsp;代付网关维护&nbsp;&nbsp;</td>
            </tr>
            <tr>
            	<td class="th1" align="right">交易类型：</td>
            	<td>
					<select id="type" name="type" style="width: 200px;">
						<option value="">全部</option>
					</select>
				</td>
            	<td class="th1" align="right">代付渠道：</td>
            	<td>
					<select id="gid" name="gid" style="width: 200px;">
						<option value="">全部</option>
					</select>
				</td>
            	<td class="th1" align="right" style="width: 100px;"></td>
            	<td style="width: 200px;"></td>
            </tr>
            <tr>
            	<td colspan="6" align="center">
            		<input class="button" type="button" value = "查询" onclick="queryCustom(1);"/>
            		<input class="button" type="button" value = "新增" onclick="toAdd();"/>
            		<input class="button" type="button" value = "刷新" onclick="refresh();"/>&nbsp;<font color="red">(新增或修改之后请刷新)</font>
            	</td>
            </tr>
        </table>
       </form>
       <table class="tablelist tablelist2" id="customTable" >
           <thead>
           <tr>
             <th>交易类型</th><th>代付网关</th><th>代付渠道</th><th>交易额度</th><th style="width: 200px;">操作</th>
           </tr>
           </thead>
           <tbody id="customTbody">
           </tbody>
       </table>
      </div>  
<!--       wbox弹出层		  -->
			<table id="pupUpTable" class="tableBorder" style="display:none; width: 100%; height: auto; margin-top: 0px;white-space: nowrap;"></table>
    </body>
</html>
