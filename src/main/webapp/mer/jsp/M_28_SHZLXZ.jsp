<%@ page language="java" pageEncoding="UTF-8" %>
<% String path = request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
<%
response.setHeader("Pragma","No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", 0);
int rand = new java.util.Random().nextInt(10000);
%>
         <title>余额查询</title>
        <meta http-equiv="pragma" content="no-cache"/>
        <meta http-equiv="cache-control" content="no-cache"/>
        <meta http-equiv="Expires" content="0"/>  
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="../../dwr/engine.js"></script>
        <script type="text/javascript" src="../../dwr/util.js"></script>
        <script type="text/javascript" src="../../dwr/interface/MerMerchantService.js?<%=rand%>"></script>
        <script type="text/javascript">
        function downLoadManual(){
         	dwr.engine.setAsync(false);
        	MerMerchantService.downLoadUserManual(function(data){
        				dwr.engine.openInDownload(data);
        	});
        }
        </script>
        
    </head>
    <body>
    <div class="style">
    <table class="tableBorder" >
    <thead><tr><td class="title" colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;商户资料下载</td></tr></thead>
    <tbody>
		<tr><td align="center"><input type="button" value="操作员手册下载" style="width: 200px;height: 30px;" onclick="downLoadManual();"/></td></tr>
     
    </tbody>
</table>
</div>
</body>
</html>














