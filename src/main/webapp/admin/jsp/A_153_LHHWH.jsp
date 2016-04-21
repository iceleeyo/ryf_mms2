<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>联行行号维护</title>
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
        <script type="text/javascript" src="../../dwr/interface/BankInfoService.js"></script>
        <script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
        <script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
        <script type="text/javascript" src="../../public/js/sysmanage/bankInfoManage.js?<%=rand%>"></script>
    </head>
    
    <body>
    <div class="style">
		<!-- 搜索 -->
        <form id="queryForm"  method="post" action="">
         <table width="100%"  align="left"  class="tableBorder">
            <tr>
            	<td class="title" style="text-align: left;" colspan="7">&nbsp;&nbsp;&nbsp;&nbsp;联行行号维护&nbsp;&nbsp;</td>
            </tr>
            <tr>
            	<td class="th1" align="right">联行行号：</td>
            	<td>
            		<input type="text" name="bkNo" maxlength="20"/>
				</td>
            	<td class="th1" align="right">银行名称：</td>
            	<td>
            		<input type="text" name="bkName" maxlength="20"/>
				</td>
            	<td class="th1" align="right">开户行：</td>
            	<td><input type="text" id="gateName" name="gateName" onblur="gateRouteList()"/>
            		<select id="gateId" name="gid">
            		</select>
				</td>
            	<td align="center">
            		<input class="button" type="button" value = "查询" onclick="queryForPage(1)"/>
            		<input class="button" type="button" value = "新增" onclick="toAdd()"/>
<!--             		<input class="button" type="button" value = "生效" onclick=""/> -->
            	</td>
            </tr>
        </table>
       </form>
       <table class="tablelist tablelist2" id="customTable" style="display: none;" >
           <thead>
           <tr>
             <th>开户行</th><th>联行行号</th><th>联行名称</th><th style="width: 200px;">操作</th>
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
