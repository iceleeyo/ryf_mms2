<%@ page language="java"  pageEncoding="UTF-8"%>
<%@page import="com.rongyifu.mms.web.WebConstants"%>
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
<meta http-equiv="pragma" content="no-cache"/>
<meta http-equiv="cache-control" content="no-cache"/>
<meta http-equiv="Expires" content="0"/> 
<meta http-equiv="windows-target" content="_top" />
<link rel="stylesheet" type="text/css" href="<%=path %>/public/css/denglu.css?<%=rand %>"/>
<script type='text/javascript' src='<%=path %>/dwr/engine.js?<%=rand %>'></script>
<script type='text/javascript' src='<%=path %>/dwr/util.js'></script>
<script type='text/javascript' src='<%=path %>/dwr/interface/LoginService.js?<%=rand %>'></script>
<script type='text/javascript' src='<%=path %>/public/js/softkeyboard.js'></script>
<script type='text/javascript' src='<%=path %>/public/js/ryt_util.js'></script>
<script type='text/javascript' src='<%=path %>/public/js/md5.js'></script>
<script type='text/javascript' src='<%=path %>/public/js/ajaxLoad.js'></script>
<title>电银信息支付管理平台-商户登录</title>
<script type="text/javascript"> 
    if (top.location != self.location) {
        top.location = self.location;
    }
    function isNumber(oNum) {
        if (!oNum)
            return false;
        var strP = /^\d+(\.\d+)?$/;
        if (!strP.test(oNum))
            return false;
        try {
            if (parseFloat(oNum) != oNum)
                return false;
        } catch (ex) {
            return false;
        }
        return true;
    }
    function submitForm(){
        var cusName = $("cusName").value;
        var ckpwd = $("ckpwd").value;
        
        if(cusName==''){
            $("logmsg").innerHTML="请输入企业全名!";
            return false;
        }
        
        if(ckpwd==''){
            $("logmsg").innerHTML="请输入验证码!";
            return false;   
        }
        
        if(!(isNumber(ckpwd))){
            $("logmsg").innerHTML="验证码只能是数字!";
            return false;
        }
       //$("userLoginForm").submit();
       LoginService.cusUserLogin(cusName,ckpwd);
       useLoadingImage("public/images/wbox/loading.gif");
    }
    //给登陆按钮绑定回车
   if (document.addEventListener){
       document.addEventListener("keypress", fireFoxHandler, true);
   } else {
       document.attachEvent("onkeypress",ieHandler);
   }
   function fireFoxHandler(evt){
       if(evt.keyCode==13){
           var evt = document.createEvent("MouseEvents");
            evt.initEvent("click",true,true);//针对图片不能绑定直接click
            document.getElementById("login").dispatchEvent(evt);
           // document.getElementById("login").click();
          return false;
       }
  }
  function ieHandler(evt){
      if(evt.keyCode==13){
         document.getElementById("login").click();
         return false;
      }
  }

 
  
   
function flushImg(){
	document.getElementById("ImgPwd").src="<%=path %>/CheckCode?temp="+ (new Date().getTime().toString(36));
}

</script>
<link type="image/x-icon" href="/mms/favicon.ico?" rel="shortcut icon"/>
<link rel="icon" href="/mms/favicon.ico?" type="image/x-icon"/>
</head>
<body>

<%
try {
    session.removeAttribute(WebConstants.SESSION_LOGGED_ON_CUS_NAME);
    session.invalidate();
} catch(Exception e){
}
%>

<div id="wrap">
    <div class="denglubody">
   		 <div id="versionFont" >4.3</div>
        <div class="denglushuru" >
           <form action="<%=path%>/cus/index.jsp" method="post" autocomplete="off" id="userLoginForm">
           <br/>
                <ul class="denglu_list" >
                    <li><span>企业全名：</span>
                    <input class="denglu" type="text" maxlength="50" id="cusName" name = "cusName"/></li>
                    
                    <li class="yanzheng"><span>验证码：</span> 
                    <input type="text" name="ckpwd" type="text" id="ckpwd"  value="" maxlength="4"/>
                    <span class="yanzhengma"><img id="ImgPwd" name="ImgPwd" src="./CheckCode" />
                      <a href="#"  onclick="flushImg();">刷新</a></span>
                   </li>
                    <li>
	<table><tr><td style="color:red;width:70px; " id="logmsg"></td>
	
	<td style="width:70px; " >
	<a href="login.jsp">是商户？</a>
	</td>
	
<td>


<img  src="public/images/denglu_but.png"   onclick="submitForm()" id = "login" onfocus="this.blur()" style="cursor: pointer;"/></td></tr></table>
</li>
                </ul>
                
            </form>
        </div>
    </div>
    </div>
     <script type="text/javascript">$("cusName").focus();//定位焦点。</script>
</body>
</html>