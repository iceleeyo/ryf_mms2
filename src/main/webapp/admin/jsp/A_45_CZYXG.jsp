<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>操作员修改</title>
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
		<script type='text/javascript' src='../../dwr/engine.js'></script>
		<script type='text/javascript' src='../../dwr/util.js'></script> 
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand %>'></script>
		<script type='text/javascript' src='../../dwr/interface/MerchantService.js?<%=rand %>'></script>
  		<script type="text/javascript" src='../../public/js/merchant/admin_search_edit_oper.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/js/merchant/admin_add_operator.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../public/js/ryt.js?<%=rand %>'></script>
</head>

<body onload="initMinfos();">

<div class="style">
	<table class="tableBorder" id="queryTJ" >
				<tbody>
					<tr>
						<td class="title" colspan="4">&nbsp;&nbsp; 操作员修改</td>
					</tr>
								
					<tr>
	                    <td class="th1" width="20%" align="right" style="height: 30px;">&nbsp;商户号：</td>
					    <td width="40%" align="left">	<input type="text" id="mid" name="mid"  size="8px"/>
                   &nbsp;<!-- <select style="width: 150px" id="smid" name="smid" onchange="initMidInput(this.value)">
                     <option value="">全部...</option>
                    </select> --></td>
						<td class="th1" align="right" width="15%">&nbsp;操作员名：</td>
						<td  align="left" width="25%">&nbsp;
							<input type="text" name="oper_name" id="oper_name" maxlength="20"/>
						</td>
					</tr>
					<tr>
						<td colspan="4" align="center"><input type="button" class="button" value="查  询" onclick="searchOpersInfo(1)" /></td>
					</tr>
				</tbody>
			</table>
	<br/>		
	<table class="tablelist tablelist2" id="body4List" style="display: none;">
			<thead>
				<tr>
					<th>
						商户号
					</th>
					<th>
						操作员号
					</th>
					<th>
						操作员名
					</th>
					<th>
						操作员电话
					</th>
					<th>
						操作员邮箱
					</th>
					<th>
						注册日期
					</th>
					<th>
						状态标志
					</th>
					<th>
						操作
					</th>
				</tr>
			</thead>
			<tbody  id="operInfoBody"> </tbody>
	</table>
    <table class="tableBorder" id="body4One" style="display: none" >
				<tbody>
					<tr><td class="title" colspan="2">&nbsp; 商户操作员修改</td>
					</tr>
					<tr>
						<td colspan="2" bgcolor="#C0C9E0"></td>
					</tr>
					<tr>
						<td class="th1" align="right" width="30%">
							&nbsp;商户：
						</td>
						<td width="70%" align="left">
							<input type="text" readonly="readonly" id="v_minfo_id" />
						</td>
					</tr>
					<tr>
						<td class="th1" align="right" width="30%">
							&nbsp;操作员号：
						</td>
						<td width="70%" align="left">
							<input type="text" id="v_oper_id" readonly="readonly" />
						</td>
					</tr>
					<tr> 
						<td class="th1" align="right" width="30%">
							&nbsp; 操作员姓名：
						</td>
						<td width="70%" align="left">
							<input type="text" id="v_oper_name"  size="20"/>
							&nbsp;<font color="red">(必填)</font>
						</td>
					</tr>
					<tr>
						<td class="th1" align="right" width="30%">
							&nbsp; 操作员电话：
						</td>
						<td width="70%" align="left">
							<input type="text" id="v_oper_tel"  size="20"/>
						</td>
					</tr>
					<tr>
						<td class="th1" align="right" width="30%">
							&nbsp; 操作员Email：
						</td>
						<td width="70%" align="left">
							<input type="text" id="v_oper_email"  size="20"/>
						</td>
					</tr>
					<tr>
						<td class="th1" align="right" width="30%">
							&nbsp;状态标志：
						</td>
						<td width="70%" align="left">
							<select id="v_state">
								<option value="0">正常</option>
								<option value="1" >关闭</option>
							</select>
						</td>
					</tr>
					<tr>
						<td colspan="2" align="center">
							<input type="button" value="修 改" onclick="edit();" class="button"/>&nbsp;&nbsp;
							<input type="button" value="返 回" onclick="shouView('back')" class="button"/>
						</td>
					</tr>
				</tbody>
			</table>
			</div>
	</body>
</html>
