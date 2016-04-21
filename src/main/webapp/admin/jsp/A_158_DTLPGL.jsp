<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>动态令牌管理</title>
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
<link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="../../dwr/engine.js?<%=rand %>"></script>
<script type="text/javascript" src="../../dwr/util.js?<%=rand %>"></script>
<script type="text/javascript" src='../../dwr/interface/MerchantService.js'></script>
<script type="text/javascript" src='../../dwr/interface/TokenManagementService.js'></script>
<script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
<script type='text/javascript' src='../../public/js/merchant/admin_token_manage.js?<%=rand%>'></script>
</head>
<body onload="init()">
	<div class="style">
		<form id="queryForm">
		<table class="tableBorder">
			<tbody>
				<tr>
					<td class="title" colspan="4">&nbsp;&nbsp; 动态令牌管理
					</td>
				</tr>
				<tr>
					<td class="th1" align="right" width="20%" style="height: 30px;">&nbsp;令牌序列号：</td>
					<td  align="left">&nbsp;
						<input type="text" name="tokenSn" maxlength="20" style="height: 20px;"/>
		            </td>  
					<td class="th1" align="right" width="20%">操作员号：</td>
					<td  align="left">&nbsp;
						<input type="text" name="operId" maxlength="20" style="height: 20px;"/>
					</td>
				</tr>
				<tr>
					<td class="th1" align="right" width="20%" style="height: 30px;">&nbsp; 令牌状态：</td>
					<td align="left">&nbsp;
						<select name="status">
							<option value="">全部</option>
							<option value="1">已启用</option>
							<option value="0">已禁用</option>
						</select>
					</td>
					<td class="th1"></td>
					<td></td>
				</tr>
				<tr>
					<td colspan="4"  align="center">
						<input type="button" class= "button" value="查询" onclick = "toQuery();" />&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="button" class= "button" value="新增绑定" onclick = "toBind();" />
					</td>
					
					
				</tr>
			</tbody>
		</table>
		</form>
		<table style="display: none;" class="tablelist tablelist2" id="operlogTable">
			<tr>
				<th>系统名称</th><th>令牌序列号</th><th>商户</th><th>操作员号</th><th>操作员名</th><th>绑定时间</th><th>禁用时间</th><th>令牌状态</th><th>操作</th>
			</tr>
			<tbody id="resultList">
			</tbody>
		</table>
	</div> 
	<form id="addForm">
		<input type="hidden" id="id"/>
		<table id="addToken" style="display: none;width: 250px;height: 100px;">
	     	<tr>
	     		<td align="right">令牌序列号：</td>
	     		<td><input id="tokenSn" type="text" style="width:120px;"/>&nbsp;<font color="red">*</font></td>
	     	</tr>
	     	<tr>
	    	   <td align="right"><span style="letter-spacing: 3px;">系统名称</span>：</td>
	    	   <td align="left">
	    	   		<select id="system">
	    	   			<option value="">请选择...</option>
	    	   		</select>&nbsp;<font color="red">*</font>
		       </td>
	    	</tr>
	     	<tr>
	     		<td align="right"><span style="letter-spacing: 8px;">商户号</span>：</td>
	     		<td><input id="mid" type="text" style="width:120px;" onchange="showOper();"/>&nbsp;<font color="red">*</font></td>
	     	</tr>
	     	<tr>
	     		<td align="right"><span style="letter-spacing: 3px;">操作员名</span>：</td>
	     		<td>
	    	   		<select id="operId">
	    	   			<option value="">请选择...</option>
	    	   		</select>&nbsp;<font color="red">*</font>
	   	   		</td>
	     	</tr>
	     	 <tr>
	     		<td align="center" >
	     			<input id="subBtn" value="确 定" type="button" class="button"/>
	     		</td>
	     		<td align="center">
	     			<input value="取 消" type="button" id="wBox_close" class="wBox_close button" />
	     		</td>
	     	</tr>
	    </table>
   	</form>
</body>
</html>




