<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.rongyifu.mms.service.SysManageService"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>通知信息查看</title>
        <meta http-equiv="pragma" content="no-cache"></meta>
        <meta http-equiv="cache-control" content="no-cache"></meta>
        <meta http-equiv="Expires" content="0">  </meta>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
        <link href="../public/css/head.css" rel="stylesheet" type="text/css"></link>
</head>
<body background="../images/welcome-bg.jpg">

<%

String id = request.getParameter("id");
if(id== null||id==""){
	return;
}

com.rongyifu.mms.bean.MMSNotice n = new SysManageService().getMessageById(Integer.parseInt(id));

%>
<br/><br/><br/><br/>
<h3 align="center"><span ><%= n==null ? "" : n.getTitle() %></span></h3><br/><br/>

<h3 align="left"><span  style="width: 800px; margin: 0pt auto; line-height: 24px;"><%= n==null ? "" :  n.getContent()%></span></h3><br/>

</body>
</html>