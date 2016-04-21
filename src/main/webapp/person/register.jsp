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
	<script type="text/javascript" src="../public/js/person/register.js?<%=rand%>"></script>
	<script type="text/javascript" src='<%=path %>/public/js/ryt_util.js?<%=rand %>'></script>
	
</head>
<body>
    <form name="form1" method="post" action="register.jsp"  id="form1">
	<div id="top">
		<ul>
		<li><img src="" /></li>
		<li>
			<div align="left">
			<br/>
			<br/>
				<a target="_blank" href="">融易通支付首页</a>
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
                <div class="clear" style="height: 20px;">
                </div>
                <div class="TabContent_WtApply">
                    <ul>
                        <li class="TabContent_WtApply_left">手机号码：</li>
                        <li class="TabContent_WtApply_right">
                            <input name="txtMobile" type="text" maxlength="11" id="txtMobile" onclick="changeMsg()" onblur="checkTel()"/>
                            <input type="button" value="检查账户名是否可用" onclick="checkTelIn();" class="blueButtonCss"
                                style="width: 139px" />
                            <br />
                            <div id="check_txtmobile" class="note">
                            </div>
                           </li>
                    </ul>
                    <div class="clear">
                    </div>
                    <ul>
                        <li class="TabContent_WtApply_left">&nbsp;</li>
                        <li class="TabContent_WtApply_right"><span class="ts04">我们将向此号码发送确认信息，请仔细核对填写的手机号码是否正确</span>
                        </li>
                    </ul>
                    <ul>
                        <li class="TabContent_WtApply_left">验证码：</li>
                        <li class="TabContent_WtApply_right" style="height: 35px;">&nbsp;<input name="txtCheckcode" type="text" maxlength="4" id="txtCheckcode" style="width:57px;" />
                            <img id="img_check" src="<%=path %>/CheckCode" align="middle" style="border-width:0px;" />
                            &nbsp;&nbsp;验证码看不清，<a href="#"  onclick="flushImg();" class="a02">换一张</a>&nbsp;<br />
                            
                            <span id="check_txtCheckcode" class="note" style="color:Black;"></span></li></ul>
                    <ul>
                        <li class="TabContent_WtApply_left">&nbsp;</li>
                        <li class="TabContent_WtApply_right">
                            <input name="readed" type="checkbox" id="readed" />
                            <a href="register_agreement.jsp" target="_blank" class="a02">我已阅读并接受“服务条款”</a>
                        </li>
                    </ul>
                    <ul>
                        <li class="TabContent_WtApply_left">&nbsp;</li>
                        <li class="TabContent_WtApply_right" style="height: 35px;">&nbsp;<input type="button" name="btnregister" value="确认注册" onclick="confirm()" class="btn01"/></li></ul>
                    <ul>
                        <li class="TabContent_WtApply_left">&nbsp;</li>
                    </ul>
                </div>
                <div class="clear" style="height: 20px;">
                </div>
                <iframe id="Checkframe" name="Checkframe" width="0" height="0"></iframe>
            </div>
            </div>
            
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
    <td height="25" align="center" valign="top" style="FONT-SIZE: 12px; color:#999 ;font-family: Arial, 宋体;"><strong>24小时客服热线： </strong> </td>
  </tr>
</table>
        </form>

</body>
</html>