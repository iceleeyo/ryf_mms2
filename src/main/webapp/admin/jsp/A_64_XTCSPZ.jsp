<%@ page language="java" pageEncoding="UTF-8"%>
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
	<meta http-equiv="expires" content="0"/>    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3"/>
		<title>系统参数配置</title>
		<link href="../../public/css/head.css" rel="stylesheet" type="text/css"/>
		<script type="text/javascript" src="../../dwr/interface/SysManageService.js?<%=rand%>"></script>
		<script type="text/javascript" src="../../dwr/engine.js?<%=rand%>"></script>
		<script type="text/javascript" src="../../dwr/util.js?<%=rand%>"></script>
		<script type="text/javascript" src='../../public/js/sysmanage/globalParamsManage.js?<%=rand%>'></script>
		<script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
		<script type="text/javascript" src='../../public/js/ryt.js?<%=rand%>'></script>
      
	</head>
	<body onload="init()">
		<div class="style">
			<!-- 
		<form action="">
			<table class="tableBorder" >
				<tbody>
					<tr>
						<td class="title" colspan="4">
							系统参数配置（带<font color="red">*</font>的为必填项）
						</td>
					</tr>
					<tr height="35">
						<td class="th1" align="right">
							&nbsp;参数名
						</td>
						<td width="35%" align="left">
							<input type="text" id="parName" name="parName" size="20" onblur="validate()">
							<font color="red">*</font>
							<span id="result"></span>
						</td>
						<td class="th1" align="right">
							&nbsp;参数值
						</td>
						<td width="35%" align="left">
							<input type="text" id="parValue" name="parValue" size="50">
							<font color="red">*</font>
						</td>
					</tr>
					<tr height="35">
						<td class="th1" align="right">
							&nbsp;手工修改
						</td>
						<td width="35%" align="left">
							<select id="parEdit" name="parEdit">
								<option value="-1">
									请选择
								</option>
								<option value="0">
									不可修改
								</option>
								<option value="1">
									只能修改一次
								</option>
								<option value="9">
									可多次修改
								</option>
							</select>
							<font color="red">*</font>
						</td>
						<td class="th1" align="right">
							&nbsp;参数描述
						</td>
						<td width="35%" align="left">
							<input type="text" id="parDesc" name="parDesc" size="50">
						</td>
					</tr>
					<tr>
						<td colspan="4" align="center">
							<input type="button" value="提交 " onclick="add()">
						</td>
					</tr>
				</tbody>
			</table>
		</form>
        <p width="100%">对如下信息进行修改后需点击<input type="button" value=" 刷 新 " onclick="refreshParamList()">才能生效</p>
		 -->
			<table class="tablelist tablelist2" id="body">
				<thead>
					<tr>
						<th>
							参数名
						</th>
						<th>
							参数值
						</th>
						<th>
							参数描述
						</th>
						<th>
							操作
						</th>
					</tr>
				</thead>
				<tbody id="list"></tbody>
			</table>


			<table class="tableBorder" id="editTableId" style="display:none">
				
					<tr>
						<td class="title" colspan="2" align="center">
							系统参数修改
						</td>
						
					</tr>
					<tr>
						<td class="th1" align="right" width="20%">
							&nbsp;参数名
						</td>
						<td align="left" id="v_parName">
						</td>

					</tr>
					<tr>
						<td class="th1" align="right">
							&nbsp;参数值
						</td>
						<td width="35%" align="left">
							<input type="text" id="v_parValue" name="parValue" size="120" maxlength="200"/>
							<font color="red">*</font>
						</td>
					</tr>
					<tr>
						<td class="th1" align="right">
							&nbsp;参数描述
						</td>
						<td align="left">
							<input type="text" id="v_parDesc" name="parValue" size="120"
								maxlength="80"/>
						</td>
					</tr>
					<tr>
						<td colspan="2" align="center">
						      <input id="v_i" type="hidden" />
							<input type="button" value="修改"  onclick="editParam();" class="button"/>
						    <input type="button" value="取消"  onclick="qx();" class="button"/>
						</td>
					</tr>
			</table>
		</div>
	</body>
</html>