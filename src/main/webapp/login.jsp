<%@ page language="java" pageEncoding="UTF-8" %>
<%@ page import="com.rongyifu.mms.web.WebConstants" %>
<%@ page import="com.rongyifu.mms.web.LoginCheck" %>
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
<script type='text/javascript' src='<%=path %>/public/js/ryt_util.js'></script>
<script type='text/javascript' src='<%=path %>/public/js/ajaxLoad.js'></script>
<script type='text/javascript' src='<%=path %>/public/js/jquery-1.6.min.js?<%=rand %>'></script>
<script type='text/javascript' src='<%=path %>/public/js/ocx/PassGuardCtrl.js?<%=rand %>'></script>
<title>电银信息支付管理平台-商户登录</title>
<script type="text/javascript">
	jQuery.noConflict();
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
        var mid = $("mid").value;
        var uid = $("uid").value;
        var ckpwd = $("ckpwd").value;
        var role = "mer";
        if(mid==''){
            $("logmsg").innerHTML="请输入商户号!";
            return false;
        }
        if(uid==''){
            $("logmsg").innerHTML="请输入操作员号!";
            return false;       
        }
        if(!(isNumber(mid))){
            $("logmsg").innerHTML="商户号只能是数字!";
            return false;
        }
        if(!(isNumber(uid))){
            $("logmsg").innerHTML="操作员号只能是数字!";
            return false;
        }
        if (pgeditor.pwdLength() == 0) {
			$("logmsg").innerHTML="密码不能为空!";
            return false;
		}
        if (pgeditor.pwdValid() == 1) {
			$("logmsg").innerHTML="密码不符合要求!";
            return false;
		}
        if(ckpwd==''){
            $("logmsg").innerHTML="请输入验证码!";
            return false;   
        }
       
        //设置32位长度随机因子，用于AES密钥转换
		jQuery.ajax({
			url : "./srand_num.jsp?" + get_time(),
			type : "GET",
			async : false,
			success : function(srand_num) {
				pgeditor.pwdSetSk(srand_num); //设置32长度随机因子
			}
		});
		//获得密文
		var pwdResult = pgeditor.pwdResult();
        useLoadingImage("public/images/wbox/loading.gif");
        LoginService.merUserLogin(mid,uid, pwdResult, role, ckpwd);
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

	function checkInput(obj){
  		if(obj){
	  		var v = obj.value;
	  	if(v!='' && !isFigure(v)) 
	  		obj.value='';
		}
	}
  
 	function isFigure(data) {
	    var reg = /^\d*$/;
	    return reg.test(data);
	}
	function flushImg(){
		document.getElementById("ImgPwd").src="<%=path %>/CheckCode?temp="+ (new Date().getTime().toString(36));
	}
	
	function get_time() {
		return new Date().getTime();
	}
	var pgeditor = new jQuery.pge({
		pgePath : "https://www.chinaebi.cn:4179/rytpay/",//控件文件目录
		pgeId : "_ocx_password",//控件ID
		pgeEdittype : 0,//控件类型,0星号,1明文
		pgeEreg1 : "[\\s\\S]*",//输入过程中字符类型限制
		pgeEreg2 : "[\\s\\S]{6,15}", //输入完毕后字符类型判断条件
		pgeMaxlength : 15,//允许最大输入长度
		pgeTabindex : 3,//tab键顺序，控件在当前表单中的tabindex顺序
		pgeClass : "ocx_style",//控件css样式，可以设置控件框高度、长度
		tabCallback : "ckpwd",//火狐控件Tab键回调，设置要跳转到的ID
		pgeOnkeydown : "submitForm()"//光标在密码框内按回车键后要调用的函数，比如：提交表单的函数
	});
</script>
<link type="image/x-icon" href="/mms/favicon.ico?" rel="shortcut icon"/>
<link rel="icon" href="/mms/favicon.ico?" type="image/x-icon"/>
</head>
<body onload="pgeditor.pgInitialize();">

<%
try {
	String mbsUrl = LoginCheck.getMbsUrl();
	if(mbsUrl != null && mbsUrl.trim().length() > 10)
		response.sendRedirect(mbsUrl);

    session.removeAttribute(WebConstants.SESSION_LOGGED_ON_USER);
    session.removeAttribute(WebConstants.SESSION_LOGGED_ON_MID);
    session.invalidate();
} catch(Exception e){
}
%>

<div id="wrap">
    <div class="denglubody">
   		 <div id="versionFont" >4.7</div>
        <div class="denglushuru" >
           <form action="<%=path%>/UserLogin" method="post"  name="userLoginForm" id="userLoginForm" autocomplete="off" >
            <input name="role" type="hidden" value="mer" />
                <ul class="denglu_list" >
                    <li>
                    	<span>商户号：</span>
                    	<input tabindex=1 class="denglu" type="text" maxlength="20" name="mid"  onkeyup="checkInput(this);" id="mid" value="<%=request.getAttribute(WebConstants.SESSION_LOGGED_ON_MID)!=null ? request.getAttribute(WebConstants.SESSION_LOGGED_ON_MID) : "" %>"/>
                    </li>
                    <li>
                    	<span>操作员：</span>
                    	<input tabindex=2 class="denglu" maxlength="9" type="text" name="uid" onkeyup="checkInput(this);" id="uid" value="<%=request.getAttribute("uid")!=null ? request.getAttribute("uid") : "" %>"/>
                    </li>
                    <li>
                    	<span>密&nbsp;&nbsp;码：</span>
                    	<script type="text/javascript">pgeditor.generate();</script>
                    </li>
                    <li class="yanzheng">
                    	<span>验证码：</span> 
                    	<input type="text" name="ckpwd" type="text" id="ckpwd"  value="" onkeyup="checkInput(this);" maxlength="4" tabindex=4 />
                    	<span class="yanzhengma">
                    		<img id="ImgPwd" name="ImgPwd" src="./CheckCode" />
                      		<a href="#"  onclick="flushImg();">刷新</a>
                      	</span>
                   </li>
                   <li>
				   	<table>
				   		<tr>
				   			<td style="color:red;width:70px; " id="logmsg"></td>
							<td style="width:70px; " >
								<a href="cus_login.jsp">不是商户？</a>
							</td>
							<td>
								<img  src="public/images/denglu_but.png"   onclick="submitForm()" id = "login" onfocus="this.blur()" style="cursor: pointer;"/>
							</td>
						</tr>
					</table>
				</li>
                </ul>
                
            </form>
        </div>
    </div>
    </div>
     <script type="text/javascript">document.getElementById("mid").focus();//定位焦点。</script>
</body>
</html>