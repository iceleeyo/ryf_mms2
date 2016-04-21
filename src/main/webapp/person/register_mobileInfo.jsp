<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
    <% String path = request.getContextPath(); %>
<title>Insert title here</title>
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
	<link href="../public/css/person/home.css?<%=rand%>" rel="stylesheet" type="text/css"/> 
	<script type='text/javascript' src='<%=path %>/dwr/engine.js?<%=rand %>'></script>
	<script type='text/javascript' src='<%=path %>/dwr/util.js'></script>
	<script type="text/javascript" src='<%=path %>/public/js/ryt_util.js?<%=rand %>'></script>
	
	<script type="text/javascript" >
	
	</script>

</head>

<body>
<form name="form1" method="post" action="" id="form1">
	 
<div id="top">
	<ul>
	<li><img src="" /></li>
	<li>
		<div align="left">
			<p><a target="_blank" href="#">融易通支付首页</a></p>
				</div>
	</li>
	</ul>
</div>
<div id="top_menu">

</div>
<div id="myips_content">
		<h3>手机注册</h3>
		<div id="mobile_register">
			<ul>
					<li>第1步 填写账户名</li>
					<li>第2步 设置账户密码</li>	
					<li class="mr02">第3步 注册完成</li>					
			</ul>
		</div>
		<div id="mobile_register_02">
		<div class="clear" style="height:20px; "></div>
			
			<div class="TabContent_WtApply_ok02">
			<h1>注册成功，欢迎您成为融易通用户!</h1>
			<ul>
			  <li>>> 您的融易通账户：<font class="fontred"><span id="lblmobile"><%=session.getAttribute("tel") %></span></font> 请务必记住。</li>
			   <li><a href="" class="a02">>> 点此登录您的账户</a></li>
			 </ul> 
		  </div>
			<div class="clear" style="height:20px; "></div>
		</div>
<div class="clear" style="height:20px; "></div>
<div>
	 
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td style="border-bottom-width: 3px;border-bottom-style: solid;border-bottom-color: #CCCCCC;">&nbsp;</td>
  </tr>
</table>
<table width="980" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td height="25" align="center" valign="bottom" style="FONT-SIZE: 12px; color:#999 ;font-family: Arial, 宋体;">Copyright 2000 - 2012 IPS. All Rights Reserved　融易通信息科技有限公司 版权所有 </td>
  </tr>
  <tr>
    <td height="25" align="center" valign="top" style="FONT-SIZE: 12px; color:#999 ;font-family: Arial, 宋体;"><strong>24小时客服热线：010-888888 </strong> </td>
  </tr>
</table>
</div></form>
</body>
</html>