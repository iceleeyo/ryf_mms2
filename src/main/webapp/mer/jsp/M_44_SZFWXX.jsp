<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="com.rongyifu.mms.bean.LoginUser" %>
<%@ page import="com.rongyifu.mms.common.Ryt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>设置防伪信息</title>
<%
	response.setHeader("Pragma","No-cache");
	response.setHeader("Cache-Control","no-cache");
	response.setDateHeader("Expires", 0);
	int rand = new java.util.Random().nextInt(10000);
%>
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="../../dwr/engine.js?<%=rand %>"></script>
<script type="text/javascript" src="../../dwr/util.js?<%=rand %>"></script>
<script type="text/javascript" src='../../dwr/interface/AntiPhishingService.js?<%=rand %>'></script>
<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
<script type='text/javascript' src='../../public/js/merchant/common_szfwxx.js?<%=rand%>'></script>
</head>
<%
	HttpSession sessionobj = request.getSession();
	LoginUser loginUser = (LoginUser) sessionobj.getAttribute(com.rongyifu.mms.web.WebConstants.SESSION_LOGGED_ON_USER);
	String antiPhishingStr = loginUser.getAntiPhishingStr();
	antiPhishingStr = Ryt.empty(antiPhishingStr) ? "" : antiPhishingStr;
 %>
<body>
	<div class="style">
		<form id="Form1">
			<table class="tableBorder">
				<tbody>
					<tr>
						<td class="title" colspan="2">&nbsp;&nbsp; 防伪信息设置
						</td>
					</tr>
					<tr>
						<td class="th1" bgcolor="#D9DFEE" align="right" width="30%">
							防伪信息：
						</td>
						<td align="left" width="70%">
							<input type="text" name="anti_phishing" id="anti_phishing" value="<%=antiPhishingStr %>" maxlength="20" />
							最长20个汉字,允许数字,字母
						</td>
					</tr>
					<tr>
						<td colspan="2" align="center">
							<input type="button" class= "button" value="保 存" onclick = "doSave();" />
						</td>
					</tr>
				</tbody>
			</table>
			<span style="line-height:24px">
				<font color="red">注：您登录时，将在页面首页显示预设的防伪信息。若回显信息与设置的不一致，请停止操作，并尽快拨打客服电话：400-700-8010</font>
			</span>
		</form>
	</div> 
</body>
</html>