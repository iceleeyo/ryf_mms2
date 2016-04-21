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
<meta name="Keywords" content="Travel, Flight, Airline"/>
<meta http-equiv="pragma" content="no-cache"/> 
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<meta http-equiv="windows-target" content="_top" />
<link rel="stylesheet" type="text/css" href="<%=path %>/public/css/denglu.css?<%=rand %>"/>
<script type='text/javascript' src='<%=path %>/dwr/engine.js?<%=rand %>'></script>
<script type='text/javascript' src='<%=path %>/dwr/util.js'></script>
<script type='text/javascript' src='<%=path %>/dwr/interface/LoginService.js?<%=rand %>'></script>
<script type='text/javascript' src='<%=path %>/public/js/ryt_util.js?<%=rand %>'></script>
<script type='text/javascript' src='<%=path %>/public/js/ajaxLoad.js?<%=rand %>'></script>
<script type='text/javascript' src='<%=path %>/public/js/jquery-1.6.min.js?<%=rand %>'></script>
<script type='text/javascript' src='<%=path %>/public/js/ocx/PassGuardCtrl.js?<%=rand %>'></script>
<title>电银信息支付管理平台-商户登录</title>
<script type="text/javascript"> 
	jQuery.noConflict();
    if (top.location != self.location) {
        top.location = self.location;
    }
    function submitForm(){
    	var role = $("role").value;
        var uid = $("uid").value;
        var ckpwd = $("ckpwd").value;
        if(uid==''){
       	    $("logmsg").innerHTML="请输入操作员号!";
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
			url : "../srand_num.jsp?" + get_time(),
			type : "GET",
			async : false,
			success : function(srand_num) {
				pgeditor.pwdSetSk(srand_num); //设置32长度随机因子
			}
		});
		
		//获得密文
		var pwdResult = pgeditor.pwdResult();
        useLoadingImage("../public/images/wbox/loading.gif");
        LoginService.adminLogin(uid, pwdResult, role, ckpwd);
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
            evt.initEvent("click",true,true);//ff针对图片不能绑定直接click
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
          if(v!='' && !isFigure(v)) obj.value='';
      }
  }
	function flushImg(){
		document.getElementById("ImgPwd").src="<%=path %>/CheckCode?temp="+ (new Date().getTime().toString(36));
	}
	
	//IP限制
	function checkAccessIp(){
		LoginService.doCheckAddr("admin",function(Res){
			if("error"==Res){
				window.location.href="../unaccess_error.jsp";
			}
		});
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
		pgeTabindex : 2,//tab键顺序，控件在当前表单中的tabindex顺序
		pgeClass : "ocx_style",//控件css样式，可以设置控件框高度、长度
		tabCallback : "ckpwd",//火狐控件Tab键回调，设置要跳转到的ID
		pgeOnkeydown : "submitForm()"//光标在密码框内按回车键后要调用的函数，比如：提交表单的函数
	});
</script>
<link type="image/x-icon" href="<%=path%>/favicon.ico?" rel="shortcut icon"/>
</head>
<body onload="checkAccessIp();pgeditor.pgInitialize();">

<%
try {
	session.removeAttribute(WebConstants.SESSION_LOGGED_ON_USER);
    session.removeAttribute(WebConstants.SESSION_LOGGED_ON_MID);
    session.invalidate();
} catch(Exception e){
}
%>


<div id="wrap">
    <div class="denglubody">
    	<div id="versionFont" >4.7</div>
        <div class="denglushuru">
           <form action="#" method="post"  name="userLoginForm" id="userLoginForm" autocomplete="off" >
            <input id="role" name="role" type="hidden" value="admin" />
            <input id="mid" name="mid" type="hidden" value="1" />
                <ul class="denglu_list" >
                	<li>
                    	<span>操作员：</span>
                    	<input class="denglu" onkeyup="checkInput(this);" maxlength="9" type="text" name="uid"  id="uid" value="<%=request.getAttribute("uid")!=null ? request.getAttribute("uid") : "" %>" tabindex=1 />
                    </li>
                    <li>
                    	<span>密&nbsp;&nbsp;码：</span>
                    	<script type="text/javascript">pgeditor.generate();</script>
                    </li>
                    <li class="yanzheng">
                    	<span>验证码：</span> 
                    	<input type="text" name="ckpwd" type="text" id="ckpwd" onkeyup="checkInput(this);" maxlength="4" tabindex=3 />
                    	<span class="yanzhengma" ><img id="ImgPwd" name="ImgPwd" src="<%=path %>/CheckCode" />
                    	<a href="#"  onclick="flushImg();">刷新</a></span>
                    </li>
                    <li>
	                    <table>
							<tr><td style="color:red;width:135px; " id="logmsg"></td>
							     <td>
							     <img  src="<%=path %>/public/images/denglu_but.png"   onclick="submitForm();" id = "login" onfocus="this.blur()" style="cursor: pointer;"/>
							     </td>
							</tr>
						</table>
					</li>
                </ul>
            </form>
        </div>
    </div>
    </div>
     <script type="text/javascript">document.getElementById("uid").focus();//定位焦点。</script>
</body>
</html>
