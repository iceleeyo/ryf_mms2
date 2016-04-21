<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ page import="com.rongyifu.mms.bean.LoginUser" %>
<%@ page import="com.rongyifu.mms.common.Ryt" %>
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
<meta name="Keywords" content="Travel, Flight, Airline"/>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<meta http-equiv="windows-target" content="_top" />
<link rel="stylesheet" type="text/css" href="public/css/denglu.css?<%=rand %>"/>
<script type='text/javascript' src='<%=path %>/dwr/engine.js?<%=rand %>'></script>
<script type='text/javascript' src='<%=path %>/dwr/util.js?<%=rand %>'></script>
<script type='text/javascript' src='<%=path %>/dwr/interface/LoginService.js?<%=rand %>'></script>
<script type='text/javascript' src='<%=path %>/public/js/ryt_util.js?<%=rand %>'></script>
<script type='text/javascript' src='<%=path %>/public/js/md5.js?<%=rand %>'></script>
<title>电银信息支付管理平台-商户登录</title>
<script type="text/javascript"> 
    function submitForm(){
        var opwd = document.getElementById("opwd").value;
        var npwd = document.getElementById("npwd").value;
        var vpwd = document.getElementById("vpwd").value;
        if(opwd==''){
            alert("请输入原密码!");
            return false;
        }
        if(npwd==''){
            alert("请输入新密码!");
            return false;
        }
        if(vpwd==''){
            alert("请确认新密码!");
            return false;
        }
        if(opwd.length<8|| opwd.length>15){
            alert("原密码的长度为8-15位");
            return false;
        }
        if(npwd.length<8|| npwd.length>15){
            alert("新密码的长度为8-15位");
            return false;
        }
        if(npwd!=vpwd){
            alert("两次密码不一致!");
            return false;
        }
       if(!isCharAndNum(npwd)){
			alert("新密码中必须包含字母、数字和特殊字符！");
			return false;
		}
		var anti_phishing_str = document.getElementById("anti_phishing_str").value;
		
		LoginService.updatePwd(hex_md5(opwd), hex_md5(npwd),hex_md5(vpwd),anti_phishing_str);
      //  document.getElementById("userLoginForm").submit();
    }

	//给登陆按钮绑定回车
	if (document.addEventListener) {
		document.addEventListener("keypress", handler, true);
	} else {
		document.attachEvent("onkeypress", handler);
	}
	function handler(evt) {
		if (evt.keyCode == 13) {
			submitForm();
			return false;
		}
	}
	
	function pageInit(){
		var firstLoginFlag = document.getElementById("firstLoginFlag").value;
		if(firstLoginFlag == "0"){
			var anti_phishing_obj = document.getElementById("anti_phishing_str");
			anti_phishing_obj.removeAttribute("readonly");
		}
	}
</script>
<link type="image/x-icon" href="/mms/favicon.ico?" rel="shortcut icon">
<link rel="icon" href="/mms/favicon.ico" type="image/x-icon">
<%

response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", 0);

%>
</head>
<body onload="pageInit();">

<%
HttpSession sessionobj = request.getSession();
LoginUser loginUser = (LoginUser) sessionobj.getAttribute(com.rongyifu.mms.web.WebConstants.SESSION_LOGGED_ON_USER);
int firstLoginFlag = "0".equals(loginUser.getLogined()) ? 0 : 1; // 0 代表第一次登陆 
String dbAntiPhishingStr = Ryt.empty(loginUser.getAntiPhishingStr()) ? "" : loginUser.getAntiPhishingStr();
if(null==sessionobj || loginUser==null){
    response.sendRedirect(path+"/login.jsp");
}else{ 
%>
<input type="hidden" id="firstLoginFlag" name="firstLoginFlag" value="<%=firstLoginFlag %>" />
<div id="wrap">
    <div class="denglubody">
        <div class="updatepwd">
           <span style="line-height:24px">您的密码已过期，请修改您的密码</span>
           <form action="#" method="post"  name="userLoginForm" id="userLoginForm" autocomplete="off">
            <input name="role" type="hidden" value="mer" />
                <ul class="denglu_list">
                    <li>
                    	<span>原密码：</span>
                    	<input class="denglu" maxlength="15" type="password" name="opwd" id="opwd" value=""/>
                    	<font color="red">&nbsp;*</font>
                    </li>
                    <li>
                    	<span>新密码：</span>
                    	<input class="denglu" type="password" name="npwd"  id="npwd" maxlength="15" />
                    	<font color="red">&nbsp;*</font>
                    </li>
                    <li>
                    	<span>确认：</span>
                    	<input class="denglu" type="password" name="vpwd"  id="vpwd" maxlength="15" />
                    	<font color="red">&nbsp;*</font>
                    </li>
                    <li>
                    	<span>防伪信息：</span>
                    	<input class="denglu" type="text" name="anti_phishing_str" id="anti_phishing_str" maxlength="20" value="<%=dbAntiPhishingStr %>" readonly="readonly" />
                    </li>
                    <li><p style="color: red" id="logmsg"></p></li>
                </ul>
                <span style="line-height:24px"><font color="red">注：密码必须由数字、字母和特殊字符组成</font></span>
                <input type="button" value="修改" onclick="submitForm()"  style="cursor:pointer;margin-top: 5px;margin-left: 150px;width: 80px;height: 30px" onfocus="this.blur()"/>
            </form>
        </div>
    </div>
    </div>
    <%} %>
</body>
</html>