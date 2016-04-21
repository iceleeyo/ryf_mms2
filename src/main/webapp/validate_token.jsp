<%@ page language="java"  pageEncoding="UTF-8"%>
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
<link rel="stylesheet" type="text/css" href="public/css/denglu.css"/>
<script type='text/javascript' src='<%=path %>/dwr/engine.js'></script>
<script type='text/javascript' src='<%=path %>/dwr/util.js'></script>
<script type='text/javascript' src='<%=path %>/dwr/interface/LoginService.js?<%=rand %>'></script>
<script type='text/javascript' src='<%=path %>/public/js/ryt_util.js'></script>
<script type='text/javascript' src='<%=path %>/public/js/md5.js'></script>
<script type='text/javascript' src='<%=path %>/public/js/jquery-1.6.min.js'></script>
<title>校验安全令牌</title>
<script type="text/javascript"> 
    function submitForm(){
        var dynamicCode = document.getElementById("dynamicCode").value;
        if(dynamicCode==''){
            alert("动态密码不能为空!");
            return false;
        }
        
		LoginService.validateToken(dynamicCode);
    }
  
	jQuery.noConflict();
	jQuery(document).ready(function() {
		jQuery("#dynamicCode").keydown(function(e) {
			var curKey = e.which;
			if (curKey == 13) {
				submitForm();
				return false;
			}
		});
	});
</script>
<link type="image/x-icon" href="/mms/favicon.ico?" rel="shortcut icon"></link>
<link rel="icon" href="/mms/favicon.ico" type="image/x-icon"></link>
<%

response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", 0);

%>
</head>
<body>

<%
HttpSession sessionobj = request.getSession();
if(null==sessionobj || sessionobj.getAttribute(com.rongyifu.mms.web.WebConstants.SESSION_LOGGED_ON_USER)==null){
    response.sendRedirect(path+"/login.jsp");
}else{ 
%>
<div id="wrap">
    <div class="denglubody">
        <div class="denglushuru">
           <form action="#" method="post"  name="userLoginForm" id="userLoginForm" autocomplete="off">
            <input name="role" type="hidden" value="mer" />
                <ul class="denglu_list">
                    <li>您的账户绑定了动态令牌,请验证</li>
                    <li><span>动态密码：</span><input class="denglu" maxlength="15" type="text" name="dynamicCode" id="dynamicCode" value=""/></li>
                    <li><p style="color: red" id="logmsg"></p></li>
                </ul>
                <input type="button" value="提 交" onclick="submitForm()" id="validate"  style="cursor:pointer;margin-top: 5px;margin-left: 100px;width: 80px;height: 30px" onfocus="this.blur()"/>
            </form>
        </div>
    </div>
    </div>
    <%} %>
</body>
</html>