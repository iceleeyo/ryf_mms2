<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" errorPage="../../error.jsp"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
        <link href="../../public/css/head.css?<%=rand %>" rel="stylesheet" type="text/css"/> 
        <link href="../../public/css/zTreeStyle/zTreeStyle.css" rel="stylesheet" type="text/css"/>      
		<script type='text/javascript' src='../../dwr/engine.js'></script>
		<script type='text/javascript' src='../../dwr/util.js'></script>
		<script type="text/javascript" src='../../dwr/interface/MerchantService.js'></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand %>'></script>
        <script type="text/javascript" src='../../public/js/ryt.js?<%=rand %>'></script>
        <script type="text/javascript" src='../../public/js/jquery.ztree.all-3.3.min.js'></script>
        <script type="text/javascript" src='../../public/js/merchant/admin_menu.js?<%=rand %>'></script>
<script type="text/javascript" defer="defer">
jQuery(function(){
		setting.callback={//ztree中加上回调函数
			beforeCheck:function(){return false;}
		};
});
</script>

	</head>
	<body onload="initMinfos();">
	<div class="style">

	<table class="tableBorder" >
		<tbody>
			<tr>
				<td class="title" colspan="4">&nbsp; 操作员权限查询</td>
			</tr>
		
			<tr>		
				<td class="th1" bgcolor="#D9DFEE" align="right" width="20%" height="30px"> 商户号：</td>
	                   <td align="left"  width="30%" style="height: 30px">&nbsp;  
	                  <input type="text" id="mid" name="mid" style="width: 150px" class="mid_input"  onblur="checkMidInput(this);showOper(this.value);"/>
                  &nbsp;&nbsp;<!-- <select style="width: 150px" id="smid" name="smid" onchange="initMidInput(this.value);showOper(this.value);">
                     <option value="">全部...</option>
                   </select> -->&nbsp;<font color="red">*</font>  	
	             	  </td>  
				
				<td class="th1" align="right" width="20%">&nbsp;操作员：</td>
				<td  align="left" id="show_opers_id" >&nbsp;
					<select name="oper_id" id="oper_id" onchange="enableButton();">
					<option value="">请选择...</option>
					</select>
				</td>
			</tr>
			<tr>
				<td colspan="4" align="center"><input type="hidden" name="action" value="yes"/>
				<input type="hidden" name="userAuthIndex" value=""/>
				<input id="queryMenuAuthButtn" class="button" type="button" value="查  询"  disabled="disabled"/><input id="confirmButtons" type="hidden"></input></td>
			</tr>
		</tbody>
		</table>
		
	<input type="hidden" id="mid" value=""></input>
    <input type="hidden" id="oid" value=""></input>
				<table width="100%" id="oper_auth" class="tableBorder"
					style="display: none;">
					<tr id="title" align="center" style="font-size: 16px;"
						class="notChangeTr">
						<td>
							查询商户号为(
							<span id="minfo_abbrev" style="color: red;"></span> )的
							<span id="oper_name" style="color: red;"></span> 权限
						</td>
					</tr>
					<tr align="left" id="foot" class="notChangeTr">
						<td>
							<ul id="tree" class="ztree" style="width: 230px; overflow: auto;"></ul>
						</td>
					</tr>
				</table>
 &nbsp;&nbsp;<br />
</div>
	</body>
</html>
