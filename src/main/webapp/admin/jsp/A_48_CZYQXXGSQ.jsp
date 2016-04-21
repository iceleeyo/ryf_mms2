<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" errorPage="../../error.jsp"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<% String path = request.getContextPath();%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>操作员权限修改</title>
 <%
response.setHeader("Pragma","No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", 0);
int rand = new java.util.Random().nextInt(10000);
%>
        <meta http-equiv="pragma" content="no-cache"/>
        <meta http-equiv="cache-control" content="no-cache"/>
        <meta http-equiv="Expires" content="0"/>  
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <link href="../../public/css/head.css?<%=rand %>" rel="stylesheet" type="text/css"/>  
        <link href="../../public/css/zTreeStyle/zTreeStyle.css?<%=rand %>" rel="stylesheet" type="text/css"/>  
        <link href="../../public/css/tab.css?<%=rand %>" rel="stylesheet" type="text/css"/>  
		<script type='text/javascript' src='../../dwr/engine.js'></script>
		<script type='text/javascript' src='../../dwr/util.js'></script>
		<script type="text/javascript" src='../../dwr/interface/MerchantService.js'></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand %>'></script>
 		<script type="text/javascript" src='../../public/js/ryt.js?<%=rand %>'></script>
 		<script type="text/javascript" src='../../public/js/jquery.ztree.all-3.3.min.js'></script>
	    <script type="text/javascript" src='../../public/js/merchant/admin_menu.js?<%=rand %>'></script>
	  	<script type=text/javascript src="../../public/js/jquery.idTabs.min.js?<%=rand %>"></script>
        <script type="text/javascript">
			jQuery.noConflict();
	(function($) { 
		 $(function(){$("#usual1 ul").idTabs();});
	})(jQuery);
		
	</script>
	</head>
	<body>
	
	

	<div class="style">	
	<div id="tab1" name="tab1">
	<!--  操作员权限修改 begin-->
	<table class="tableBorder" >
		<tbody id="tab1">
			<tr>
				<td class="title" colspan="2">&nbsp; 操作员权限修改申请</td>
			</tr>
			<tr>
				<td class="th1" align="right" width="20%">&nbsp;商户号：</td>
				<td><input id="mid" style="height: 20px;" value="" type="text" onchange="showOper();"/></td>
			</tr>
			<tr>
				<td class="th1" align="right" width="20%">&nbsp;操作员：</td>
				<td  align="left" id="show_opers_id" >&nbsp;
						<select name="oper_id" id="oper_id" onchange="enableButton();">
							<option value="">请选择...</option>
						</select>
					<input id="queryMenuAuthButtn" class="button" type="button" value="查询权限"  disabled="disabled"/>
				</td>
			</tr>
			
		</tbody>
		<!--  操作员权限修改   end-->
		</table>
		</div>
	<input type="hidden" id="mid" value=""/>
    <input type="hidden" id="oid" value=""/>
 <form name="menu_form" id="menu_form" method="post">
 <table class="tableBorder" style="display: none;" id="authTable">
   <tr id="title" align="center" style="font-size: 16px;" class="notChangeTr"><td>为操作员号为( <span id="operId" style="color: red;"></span> )的  <span id="oper_name" style="color: red;"></span> 分配权限</td></tr>
   <tr><td>
    	<table style="display: none;border: 0px;" id="oper_auth" width="100%">
    	    <tr id="foot"><td>
    	    	<ul class="ztree" style="width:230px; overflow:auto;">
					<li><span class="button pIcon01_ico_open"></span><span>系统菜单权限分配</span></li>
				</ul>
    	    	<ul id="tree" class="ztree" style="width:230px; overflow:auto;"></ul>
			</td>
			<td valign="top" id="authbtnid">
				<ul class="ztree" style="width:230px; overflow:auto;">
					<li><span class="button pIcon01_ico_open"></span><span>按钮权限分配</span></li>
				</ul>
				<ul id="buttonAuth_tree" class="ztree" style="width:230px; overflow:auto;"></ul>
			</td>
			</tr>
    	</table>
   </td></tr>
   <tr><td>&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" class="button" value="配置申请" id="confirmButton"/></td></tr>
 </table>


 </form>&nbsp;&nbsp;
 <br />
</div>
	</body>
</html>
