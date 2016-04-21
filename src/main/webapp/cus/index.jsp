<%@ page contentType="text/html; charset=utf-8" language="java"%>
<%@ page import="com.rongyifu.mms.web.WebConstants"%>
<%
	String path = request.getContextPath();
    response.setHeader("Pragma","No-cache");
    response.setHeader("Cache-Control","no-cache");
    response.setDateHeader("Expires", 0);
%>
<html>
<head>
<style type="text/css">
<!--
body {
	margin-left: 0px;
	margin-top: 0px;
	margin-right: 0px;
	margin-bottom: 0px;
}
-->
</style>
<link type="image/x-icon" href="/mms/favicon.ico?" rel="shortcut icon">
<link rel="icon" href="/mms/favicon.ico?" type="image/x-icon">
<link rel="stylesheet" type="text/css" href="<%=path%>/public/css/head.css?"/>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="Expires" content="0"> 

<script type="text/javascript" src="<%=path%>/dwr/engine.js"></script>
<script type="text/javascript" src="<%=path%>/dwr/util.js"></script>
 
<title>电银信息在线支付平台</title>

</head>
<body style="overflow:auto;border: none; ">
<%
	String sessionName = (String)session.getAttribute(WebConstants.SESSION_LOGGED_ON_CUS_NAME); 
	if(sessionName==null || sessionName.trim().length()==0){
			response.sendRedirect( path + "/cus_login.jsp"); return;
		}
%>
  
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" height="100%">
  <tr  id="rows1">
	<td width="100%" height="113">
		<div class="head" style="width:100%;" id="head">
        <img class="bj" src="<%=path%>/public/images/head.jpg" width="990" height="81"/>
        <div class="header">
            <p class="head_nav"> <a href="">&nbsp;　</a><a href="">　&nbsp;</a> </p>
            <p class="head_p">欢迎您&nbsp;<span><b><%= sessionName %></b></span>&nbsp;&nbsp;
            <span id="loginedHistory">
            </span>
            
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
             <a href="<%=path%>/cus_login.jsp"><b>退出</b></a>
            
            </p>
        </div>
        <ul class="nav" id="navigator">
        <li>
        <a href = "<%=path%>/cus/jsp/query_orders.jsp" id= "menu0" class="" name="menu" style="cursor:pointer;" target="content2">交易查询</a>
        </li>
        <li>
         <a href = "<%=path%>/cus/jsp/pay_to_merchants.jsp" id= "menu0" class="" name="menu" style="cursor:pointer;" target="content2">我要付款</a>
         </li>
        </ul>
    </div>
	</td>
  </tr>
  
  <tr><td background="<%=path%>/images/03.gif"  height="1"></td></tr>
  
  <tr>
    <td valign="top">
    <table width="100%" height="100%" border="0" align="center" cellpadding="0" cellspacing="0">
		<tr>
			<td width="1" valign="top" class="admin01">    
		    	<div class="left" id="left">
					
					<div class="left_bottom" id="left_menu_bottom">
						<h2 class="nav_2"><a href = "<%=path%>/cus/jsp/query_orders.jsp" target="content2">交易查询</a></h2>
						<h2 class="nav_2"><a href="<%=path%>/cus/jsp/pay_to_merchants.jsp" target="content2">我要付款</a></h2>
					
					    <h2 class="nav_2"><a href="<%=path%>/cus_login.jsp" target="content2">退出</a></h2>
					
					</div>
				</div>
			</td>
			<td width="99%" valign="top" class="admin01">
				<iframe id="content2" name="content2" frameborder="0"  src="<%=path%>/welcome.jsp" width="100%" height="100%" scrolling="auto"></iframe>
			</td>
		</tr>
 	</table>
 	</td>
 </tr>
 
 <tr bgcolor="#CCCCCC">
    <td height="25" valign="bottom" background="<%=path%>/images/bottom-bg.gif">
		<table align="center" width="100%">
	 		<tr align="center"><td align="center" style="text-align:center;"> Copyright © 2010   上海电银信息技术有限公司   All rights reserved.</td> </tr>
		</table>
	</td>
  </tr>
</table>
</body>
</html>
