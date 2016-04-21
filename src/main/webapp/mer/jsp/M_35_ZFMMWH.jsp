<%@ page language="java" pageEncoding="UTF-8" %>
<%@page import="com.rongyifu.mms.bean.LoginUser"%>
<%@page import="com.rongyifu.mms.web.WebConstants"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>支付密码维护</title>
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
        <link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css"/> 
        <script type="text/javascript" src="../../dwr/engine.js?<%=rand %>"></script>
        <script type="text/javascript" src="../../dwr/util.js?<%=rand %>"></script>
        <script type="text/javascript" src='../../dwr/interface/MerZHService.js?<%=rand%>'></script> 
        <script type="text/javascript" src="../../public/js/merchant/mer_zfmmwh.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../public/js/md5.js"></script>
 		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
    </head>
    <body onload="init();">

     <div class="style">
		
		<%
		    LoginUser user = (LoginUser)session.getAttribute(WebConstants.SESSION_LOGGED_ON_USER);  
            if(user==null) response.sendRedirect(request.getContextPath()+"/login.jsp");
			String mid = user.getMid()+"";
			String uid = user.getOperId()+"";
		%>
		<table class="tableBorder" id="addTable" style="display:none" >
		<tbody>
			<tr>
				<td colspan="2" align="left" height="25" class="title">
							<b><font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;支付密码维护 </font> </b>
						</td>
					</tr>
			
					<tr>
						<td width="30%" align="right" class="th1">
							&nbsp;商户：
						</td>
						<td width="70%" align="left">
							&nbsp;<%=mid %>
							
						</td>
					</tr>
					<tr>
						<td width="30%" align="right" class="th1">
							&nbsp;操作员号：
						</td>
						<td width="70%" align="left">
							&nbsp;<%=uid %>
							<input type="hidden" id="oper_id" value="<%=uid %>" />
						</td>
					</tr>
					<tr>
						<td width="30%" align="right" class="th1">
							&nbsp; 支付密码：
						</td>
						<td width="70%" align="left">
							<input type="password" id="pass" value="" size="30" maxlength="15"/>（6-15位长度）
						</td>
					</tr>
						
					<tr>
						<td width="30%" align="right" class="th1">
							&nbsp; 确定支付密码：
						</td>
						<td width="70%" align="left">
							<input type="password" id="vpass" value="" size="30" maxlength="15"/>
						</td>
					</tr>
				
					<tr>
						<td colspan="2" height="30px" >
							<input type="button" style="width: 100px;height: 25px;margin-left: 400px" value="提  交" onclick="add_pass('<%=mid %>')"/>
						</td>
					</tr>
				</tbody>
			</table>
		<table class="tableBorder" id="editTable" style="display:none">
		<tbody>
			<tr>
				<td colspan="2" align="left" height="25" class="title">
							<b><font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;支付密码修改 </font> </b>
						</td>
					</tr>
			
					<tr>
						<td width="30%" align="right" class="th1">
							&nbsp;商户：
						</td>
						<td width="70%" align="left">
							&nbsp;&nbsp;<%=mid %>
							
						</td>
					</tr>
					<tr>
						<td width="30%" align="right" class="th1">
							&nbsp;操作员号：
						</td>
						<td width="70%" align="left">
							&nbsp;
							<%=uid %>
							<input type="hidden" id="a_oper_id" value="<%=uid %>" %/>
						</td>
					</tr>
					<tr>
						<td width="30%" align="right" class="th1">
							&nbsp; 原支付密码：
						</td>
						<td width="70%" align="left">
							&nbsp;
							<input type="password" id="a_opass" value="" size="30" maxlength="15"/>（6-15位长度）
						</td>
					
					</tr>
					<tr>
						<td width="30%" align="right" class="th1">
							&nbsp; 新支付密码：
						</td>
						<td width="70%" align="left">
							&nbsp;
							<input type="password" id="a_npass" value="" size="30" maxlength="15"/>（6-15位长度）
						</td>
					</tr>
						
					<tr>
						<td width="30%" align="right" class="th1">
							&nbsp; 确定新支付密码：
						</td>
						<td width="70%" align="left">
							&nbsp;
							<input type="password" id="a_vnpass" value="" size="30" maxlength="15"/>
						</td>
					</tr>
				
					<tr>
						<td colspan="2" height="30px" >
							<input type="button" style="width: 100px;height: 25px;margin-left: 400px" value="提  交" onclick=" return edit_pass('<%=mid %>')"/>
						</td>
					</tr>
				</tbody>
			</table>
			
    
    
       
      </div>   
    </body>
</html>

           
           
    
