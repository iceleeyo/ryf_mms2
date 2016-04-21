<%@ page contentType="text/html; charset=utf-8" language="java"%>
<%@ page import="com.rongyifu.mms.bean.LoginUser"%>
<%@ page import="com.rongyifu.mms.web.*"%>
<%@ page import="com.rongyifu.mms.common.Ryt" %>
<% 
    String path = request.getContextPath();

	LoginUser user = (LoginUser)session.getAttribute(WebConstants.SESSION_LOGGED_ON_USER);  
    if(user==null){ response.sendRedirect( path + "/admin/login.jsp"); return;}
    String antiPhishingStr = "";
    if(!Ryt.empty(user.getAntiPhishingStr())) 
    	antiPhishingStr = "防伪信息：" + user.getAntiPhishingStr();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="Expires" content="0">  
<style>
.welcome-font {
	color: #05326a;
	font-family: "微软雅黑", "宋体", Tahoma, Arial;
	font-size: 14px;
}
</style>

<title>Welcome</title>
</head>
<body background="<%= path%>/images/welcome-bg.jpg">
<table id="welcome_body" width="100%">
    <tr height="100px"><td colspan="2">&nbsp;</td></tr>
    <tr height="220px">
        <td width="80px">&nbsp;</td>
        <td valign="middle"><label class="welcome-font">
               欢迎使用电银信息支付系统商户管理平台<br/><font color="red"><%=antiPhishingStr%></font><br/>  <br/><br/>
               </label>
               </td>
    </tr>
</table>

</body>
</html>