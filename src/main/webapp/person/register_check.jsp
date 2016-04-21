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
	<script type="text/javascript" src="../public/js/person/register_check.js?<%=rand%>"></script>
	<script type="text/javascript" src='../dwr/interface/RegisterService.js?<%=rand%>'></script>
	<script type='text/javascript' src='<%=path %>/dwr/engine.js?<%=rand %>'></script>
	<script type='text/javascript' src='<%=path %>/dwr/util.js'></script>
	<script type="text/javascript" src='<%=path %>/public/js/md5.js?<%=rand %>'></script>
	<script type="text/javascript" src='<%=path %>/public/js/ryt_util.js?<%=rand %>'></script>
</head>
<body>
    <form name="form1" method="post" action="Register_MobileInfo.aspx" id="form1" onsubmit="return submitForm(this);">
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
            <h3>
                手机注册</h3>
            <div id="mobile_register">
                <ul>
                    <li>第1步 填写账户名 </li>
                    <li class="mr02">第2步 设置账户密码 </li>
                    <li>第3步 注册完成 </li>
                </ul>
            </div>
            <div id="mobile_register_02">
                <div class="clear" style="height: 20px">
                </div>
                <div class="TabContent_WtApply">
                    <h1>
                        设置账户密码</h1>
                    <p class="ts01">
                        特别提醒：为了保障您的账户安全，请谨慎设置并牢记密码！</p>
                    <ul>
                        <li class="TabContent_WtApply_left"> 融易通账户： </li>
                        <li class="TabContent_WtApply_right">&nbsp;<span id="lblMobile"><%=session.getAttribute("tel") %></span>
                       <div id="check_lblMobile" class="note"></div>
                        </li></ul>
                    <ul>
                        <li class="TabContent_WtApply_left">登录密码： </li>
                        <li class="TabContent_WtApply_right">
                            <input name="loginPwd" type="password" maxlength="20" id="loginPwd" onpaste="return   false" onclick="show(1)" onblur="check(1)"/>
                            <div id="check_txtloginpwd" class="note">
                            </div>
                        </li>
                    </ul>
                    
                    <ul>
                        <li class="TabContent_WtApply_left">确认登录密码： </li>
                        <li class="TabContent_WtApply_right">
                            <input name="loginPwd2" type="password" maxlength="20" id="loginPwd2" onpaste="return   false" onclick="show(2)" onblur="check(2)"/>
                            <div id="check_txtloginpwd2" class="note">
                            </div>
                        </li>
                    </ul>
                    <ul>
                        <li class="TabContent_WtApply_left">交易密码： </li>
                        <li class="TabContent_WtApply_right">
                            <input name="payPwd" type="password" maxlength="20" id="payPwd" onpaste="return   false" onclick="show(3)" onblur="check(3)"/> <span class="ts03">您在提取现金、网上付款时必须输入“交易密码”。</span>
                            <div id="check_txtpaypwd" class="note">
                            </div>
                        </li>
                    </ul>
                    <ul>
                        <li class="TabContent_WtApply_left">确认交易密码： </li>
                        <li class="TabContent_WtApply_right">
                            <input name="payPwd2" type="password" maxlength="20" id="payPwd2" onpaste="return   false" onclick="show(4)" onblur="check(4)"/>
                            <div id="check_txtpaypwd2" class="note">
                            </div>
                        </li>
                    </ul>
                </div>
                <div class="clear" style="height: 20px">
                </div>
                <div class="TabContent_WtApply">
                    <h1>
                        设置个人信息</h1>
                        <ul>
                        <li class="TabContent_WtApply_left">姓名： </li>
                        <li class="TabContent_WtApply_right">
                            <input name="name" type="text" maxlength="20" id="name" style="width:230px;" onclick="show(6)" onblur="check(6)"/>
                            <div id="check_name" class="note">
                            </div>
                        </li>
                    </ul>
                    <ul>
                        <li class="TabContent_WtApply_left">性别： </li>
                        <li class="TabContent_WtApply_right">
                         <select name="sex" id="sex" style="width:234px;">
								<option value="0">男</option>
								<option value="1">女</option>
						</select>
                        </li>
                    </ul>
                    
                    <ul>
                        <li class="TabContent_WtApply_left">身份证号： </li>
                        <li class="TabContent_WtApply_right">
                            <input name="idNo" type="text" maxlength="20" id="idNo" style="width:230px;" onclick="show(5)" onblur="check(5)"/>
                            <div id="check_idNo" class="note">
                            </div>
                        </li>
                    </ul>
                    
                </div>
                <div class="clear" style="height: 20px">
                </div>
                <div class="TabContent_WtApply">
                    <h1>
                        填写您的联系方式</h1>
                    
                    <ul>
                    <li class="TabContent_WtApply_left">联系电话：</li>
                    <li class="TabContent_WtApply_right">
                        <input name="tel" type="text" maxlength="25" id="tel" style="width:228px;" onclick="show(12)" onblur="check(12)"/>
                        <div id="check_tel" class="note">
                        </div>
                    </li>
                </ul>
                </div>
             
                <div class="TabContent_WtApply">
                    <ul>
                        <li class="TabContent_WtApply_left">&nbsp;</li>
                        <li class="TabContent_WtApply_right">
                        <input type="button" name="btnsubmit" value="提交" id="btnsubmit" class="btn01" onclick="registerSuccess();" />
                        <input type="hidden" name="HfMobile" id="HfMobile" value="13671165926" />
                        </li></ul> 
                </div>
            </div>
            
            <div class="clear" style="height: 20px">
            </div>
        </div>
        <div class="clear" style="height: 20px">
        </div>
        <iframe id="Checkframe" name="Checkframe" width="0" height="0"></iframe>
        <div>
            
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td style="border-bottom-width: 3px;border-bottom-style: solid;border-bottom-color: #CCCCCC;">&nbsp;</td>
  </tr>
</table>
<table width="980" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td height="25" align="center" valign="middle" ><a href="http://www.ips.com/About/AboutUs.aspx" target="_blank" style="FONT-SIZE: 12px; color:#3d82c3 ;font-family: Arial, 宋体;">关于环迅支付</a>　<a href="http://www.ips.com/News/index.aspx" target="_blank" style="FONT-SIZE: 12px; color:#3d82c3 ;font-family: Arial, 宋体;">新闻中心</a>　<a href="http://www.ips.com/Cooper/Organization.aspx" target="_blank" style="FONT-SIZE: 12px; color:#3d82c3 ;font-family: Arial, 宋体;">合作伙伴</a>　<a href="http://www.ips.com/About/CallCenter.aspx" target="_blank" style="FONT-SIZE: 12px; color:#3d82c3 ;font-family: Arial, 宋体;">客服中心</a>　<a href='http://www.ips.com/Experience/Stake.aspx' target="_blank" style="FONT-SIZE: 12px; color:#3d82c3 ;font-family: Arial, 宋体;">用户体验</a>　<a href='http://www.ips.com/Job/JobList.aspx' target="_blank" style="FONT-SIZE: 12px; color:#3d82c3 ;font-family: Arial, 宋体;">诚聘英才</a>　<a href="http://www.ips.com/About/ContactUs.aspx" target="_blank" style="FONT-SIZE: 12px; color:#3d82c3 ;font-family: Arial, 宋体;">联系我们</a>　<a href="http://www.ips.com/About/SiteMap.aspx" target="_blank" style="FONT-SIZE: 12px; color:#3d82c3 ;font-family: Arial, 宋体;">网站地图</a></td>
  </tr>
</table>
<table width="980" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td height="25" align="center" valign="bottom" style="FONT-SIZE: 12px; color:#999 ;font-family: Arial, 宋体;">Copyright 2000 - 2012 IPS. All Rights Reserved　融易通信息科技有限公司 版权所有 </td>
  </tr>
  <tr>
    <td height="25" align="center" valign="top" style="FONT-SIZE: 12px; color:#999 ;font-family: Arial, 宋体;"><strong>24小时客服热线： 010-888888</strong></td>
  </tr>
</table>
        </div>

</form>

</body>
</html>