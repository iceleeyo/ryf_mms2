<%@ page language="java" pageEncoding="UTF-8" %>
<% String path = request.getContextPath();%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>操作员权限查询</title>
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
        <link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css"/>
        <link href="../../public/css/zTreeStyle/zTreeStyle.css" rel="stylesheet" type="text/css"/>  
        <script type='text/javascript' src='../../dwr/engine.js'></script>
        <script type='text/javascript' src='../../dwr/util.js'></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../dwr/interface/MerMerchantService.js?<%=rand%>'></script>     
        <script type="text/javascript" src='../../public/js/ryt.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../public/js/jquery.ztree.all-3.3.min.js'></script>
	    <script type="text/javascript" src='../../public/js/merchant/mer_menu.js?<%=rand %>'></script>
<script type="text/javascript" defer="defer">
jQuery(function(){
		setting.callback={//ztree中加上回调函数
			beforeCheck:function(){return false;}
		};
});
</script>
    </head>

    <body onload="showOper()">
    <div class="style">
<table class="tableBorder" >
		<tbody>
			<tr>
				<td class="title" colspan="4">&nbsp; 操作员权限查询</td>
			</tr>
		
			<tr>		
				<td class="th1" align="right" width="20%">&nbsp;操作员：</td>
				<td  align="left" id="show_opers_id" >&nbsp;
					<select name="oper_id" id="oper_id" onchange="enableButton(1);">
					<option value="-1">请选择操作员</option>
					</select> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  <input id="queryMenuAuthButtn" class="button" type="button" value="查  询"  disabled="disabled" />
				</td>
			</tr>
		</tbody>
		</table>
	<input type="hidden" id="mid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid }"/>
    <input type="hidden" id="oid" value="${sessionScope.SESSION_LOGGED_ON_USER.operId }"/>
 <form name="menu_form" id="menu_form" method="post">
<table width="100%" id="oper_auth" class="tableBorder" style="display: none;">
  <tr id="title" align="center" style="font-size: 16px;" class="notChangeTr"><td>查询操作员   <span id="oper_name" style="color: red;"></span> 权限</td></tr>
 <tr align="left" id="foot" class="notChangeTr"><td>
    	    	<ul class="ztree" style="width:230px; overflow:auto;">
					<li><span class="button pIcon01_ico_open"></span><span>操作员菜单权限查询</span><li>
				</ul>
    	    	<ul id="tree" class="ztree" style="width:230px; overflow:auto;"></ul>
</td></tr>
</table>
 </form></div>
    </body>
</html>