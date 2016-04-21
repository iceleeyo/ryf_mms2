<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>手动导入订单信息</title>
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
        <script type="text/javascript" src="../../dwr/interface/UploadHlogService.js"></script>
        <script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
        <script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
        <script type="text/javascript" src="../../public/js/transaction/admin_import_order.js?<%=rand%>"></script>
    </head>
    
    <body>
    <div class="style">
		<!-- 搜索 -->
        <form id="queryForm"  method="post" action="">
         <table width="100%"  align="left"  class="tableBorder">
            <tr>
            	<td class="title" style="text-align: left;" colspan="5">&nbsp;&nbsp;&nbsp;&nbsp;手动导入订单信息&nbsp;&nbsp;</td>
            </tr>
            <tr>
            	<td class="th1" align="right">业务类型：</td>
            	<td>
					<select id="type" name="type" style="width: 200px;">
						<option value="0">团购业务</option>
						<option value="1">现金业务</option>
						<option value="2">融易通机票业务</option>
						<option value="3">增值数据同步文件</option>
					</select>&nbsp;<font color="red">*</font>
				</td>
            	<td class="th1" align="right">上传订单信息：</td>
            	<td>
					<input id="file" type="file" accept=".txt,.xls,.xlsx,.csv" />&nbsp;<font color="red">*</font>
				</td>
            	<td align="center">
            		<input class="button" type="button" value = "上传订单" onclick="importOrder()"/>
            	</td>
            </tr>
        </table>
       </form>
       </div>
    </body>
</html>
