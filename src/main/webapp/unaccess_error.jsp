<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<%String path = request.getContextPath();%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>禁止访问</title>
<script type="text/javascript">
if (top.location != self.location) {
        top.location = self.location;
}
</script>
<head>
<link href="<%=path%>/css/ys.css" rel="stylesheet" type="text/css">
</head>
<body style="overflow-y:hidden;overflow-x:hidden">
  <table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" height="100%">
    <tr><td height="63" valign="middle">
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
          <td width="304"><img src="<%=path%>/images/main-logo.gif" width="304" height="63"></td>
          <td width="100%" background="<%=path%>/images/main-top-bg.gif">&nbsp;</td>
          <td width="584"><img src="<%=path%>/images/main-top-right.gif" width="584" height="63"></td>
          </tr>
        </table>
        </td>
    </tr>
    <tr><td height="38"  align="left" valign="bottom">   
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
        <td><img src="<%=path%>/images/nav-01.gif" width="38" height="38"></td>
        <td width="100%" background="<%=path%>/images/nav-03.gif"></td>
        <td><img src="<%=path%>/images/nav-02.gif" width="38" height="38"></td>
        </tr>
        </table>
        </td>
    </tr>
    <tr><td background="<%=path%>/images/03.gif"  height="1"></td>
    </tr>
    <tr><td>
        <table background="<%= path%>/images/welcome-bg.jpg" align="center">
          <tr>
          <tr><td valign="top" align="center" background="<%= path%>/images/error.jpg" width="600" height="450" style="margin: 100 auto;text-align: center; font: bold 18px 宋体;color: #0066CC;vertical-align: middle">
               Sorry,禁止访问！<br/>
                 <!-- <a style="font: bold 16px 宋体" href="javascript:history.go(-2)" >点击此处返回</a> --> 
               </td>
         </tr>
         </tr>
        </table>
        </td>
    </tr>
    <tr bgcolor="#CCCCCC">
        <td height="25" valign="bottom" background="<%=path%>/images/bottom-bg.gif">
        <table align="center">
            <tr align="center"><td> Copyright © 2010 上海电银信息技术有限公司    All rights reserved.</td> </tr>
        </table>
        </td>
    </tr>
  </table>
</body>
</html>
