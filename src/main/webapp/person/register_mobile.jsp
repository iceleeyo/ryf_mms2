<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<% String path = request.getContextPath(); %>
    <head>
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
	<script type="text/javascript" src='../dwr/interface/RegisterService.js?<%=rand%>'></script>
	<script type='text/javascript' src='<%=path %>/dwr/engine.js?<%=rand %>'></script>
	<script type='text/javascript' src='<%=path %>/dwr/util.js'></script>
	<script type='text/javascript' src='<%=path %>/public/js/ryt_util.js'></script>
	<script type="text/javascript">
var falesimage=" <img src='../images/person/01123314.gif'> ";
	function init(){
	now = new Date;
    var t = now.getFullYear()+ "-"+(parseInt(now.getMonth())+1) + "-"+now.getDate()+" " + now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds();
	document.getElementById("lblinfo").innerHTML="融易通于"+t+"向您发送了短信注意查收!";
	document.getElementById("lblMobile").innerHTML=<%=session.getAttribute("tel") %>;
	}
	function nextStep(){
	var txtmobilecode=document.getElementById("txtmobilecode").value.trim();
	if(txtmobilecode==''){document.getElementById("RequiredFieldValidator1").style.display="";return false;}
	if(txtmobilecode != <%=session.getAttribute("yzm")%>){document.getElementById("RequiredFieldValidator1").innerHTML=falesimage+"短信验证码不正确";return false;}
	
	window.location.href='register_check.jsp';
	}
	</script>
</head>
<body onload="init();">
 <form name="form1" method="post"  id="form1">
<div>
</div>
 
<div id="top">
	<ul>
	<li><img src="" /></li>
	<li>
		<div align="left">
			<p><a target="_blank" href="#">融易通支付首页  </a></p>
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
					<li class="mr02">第1步 填写账户名</li>
					<li>第2步 设置账户密码</li>	
					<li>第3步 注册完成</li>					
			</ul>
		</div>
		<div id="mobile_register_02">
		<div class="clear" style="height:20px; "></div>
	<p class="ts01"> 
        <span id="lblinfo"></span></p>
			<div class="TabContent_WtApply">
				<ul>
			 		<li class="TabContent_WtApply_left">手机号码：</li>
			  		<li class="TabContent_WtApply_right" style="height:35px; "><span id="lblMobile"></span> 
                          <input type="hidden" name="hdfMobile" id="hdfMobile" value="" />
                          <input type="hidden" name="hdfCheckcode" id="hdfCheckcode" value="5527" />
                      </li> 
				</ul>
			 	<ul>
					  <li class="TabContent_WtApply_left">短信中的校验码：</li>
					   <li class="TabContent_WtApply_right" style="height:35px; ">
                           <input name="txtmobilecode" type="text" maxlength="6" id="txtmobilecode" style="width:73px;" />
                           <span id="RequiredFieldValidator1" class="note" style="color:Black;display:none;">
                           <img src='../images/person/01123314.gif'> 校验码不能为空！</span>
                           <span id="lblmsg" class="note" style="color:Black;display:none;"></span></li></ul>
			  	 <ul>
			  <li class="TabContent_WtApply_left">&nbsp;</li>
			   <li class="TabContent_WtApply_right" style="height: 27px"><div class="ts04" style="width:300px;">每个手机24小时内最多只允许申请5次短信校验码</div></li> 
			 </ul>
			 
				<ul>
					  <li class="TabContent_WtApply_left">&nbsp;</li>
					  <li class="TabContent_WtApply_right" style="height:25px;padding-top:10px; ">
                          <input type="button" name="btnnext" value="下一步" onclick="nextStep();" id="btnnext" class="btn01" /></li></ul>
			</div>
			<div class="clear" style="height:20px; "></div>
		</div>
<div class="clear" style="height:20px; "></div>
	
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

</form>
</body>
</html>