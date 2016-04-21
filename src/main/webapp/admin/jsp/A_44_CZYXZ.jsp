<%@ page language="java" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>商户操作员增加</title>
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
		<script type="text/javascript" src='../../dwr/interface/MerchantService.js?<%=rand %>'></script>
		<script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand %>'></script>
		<script type="text/javascript" src='../../public/js/ryt.js?<%=rand %>'></script>
		<script type="text/javascript" src="../../public/js/md5.js"></script>
        <script type="text/javascript" src="../../public/js/merchant/admin_add_operator.js?<%=rand %>"></script>
	</head>

    <body onload="initMinfos();">
    
     <div class="style">
		<table class="tableBorder" >
			<tbody>
				<tr>
					<td class="title" colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;商户操作员增加(带<font color="red">*</font>的为必填项)
					</td>
				</tr>
		
				<tr>
					<td class="th1" align="right">
					<input type="hidden" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}" name="oper_mid" id="oper_mid"/>&nbsp;商户号：</td>
					<td width="70%" align="left" id="selectMerTypeId">
                     &nbsp;<input type="text" id="mid" name="mid" size="20px" maxlength="20"/>
                 <!--  &nbsp;<select style="width: 150px" id="smid" name="smid" onchange="initMidInput(this.value)">
                     <option value="">全部...</option>
                   </select> -->&nbsp;<font color="red">*</font>
                    </td>
				</tr>
				<tr>
					<td class="th1" align="right">&nbsp;操作员号：</td>
					<td width="70%" align="left">
						&nbsp;<input type="text" id="oper_id" value="" maxlength="9"/>&nbsp;<font color="red">*</font>
					</td>
				</tr>
				<tr>
					<td class="th1" align="right">&nbsp;密码：</td>
					<td width="70%" align="left">
						&nbsp;<input type="password" id="oper_pass" value="" maxlength="15"  size="30"/>&nbsp;<font color="red">*&nbsp;(长度为8-15)</font>
					</td>
				</tr>
				<tr>
					<td class="th1" align="right">&nbsp;密码确定：</td>
					<td width="70%" align="left">
						&nbsp;<input type="password" id="v_oper_pass" value="" maxlength="15" size="30"/>&nbsp;<font color="red">*&nbsp;(长度为8-15)</font>
					</td>
				</tr>

				<tr>
					<td class="th1" align="right">&nbsp; 操作员姓名：</td>
					<td width="70%" align="left">
						&nbsp;<input type="text" id="oper_name" value="" size="20" maxlength="20"/>&nbsp;<font color="red">*</font>
					</td>
				</tr>
				<tr>
					<td class="th1" align="right">&nbsp; 操作员电话：</td>
					<td width="70%" align="left">
					&nbsp;<input type="text" id="oper_tel" value="" size="25" maxlength="40"/>
					</td>
				</tr>
				<tr>
					<td class="th1" align="right">
						&nbsp; 操作员Email：
					</td>
					<td width="70%" align="left">
						&nbsp;<input type="text" id="oper_email" value="" size="30" maxlength="40"/>
					</td>
				</tr>

				<tr>
					<td class="th1" align="right">&nbsp;状态标志：</td>
					<td width="70%" align="left">
						&nbsp;<select id="state">
							<option value="0">
								正常
							</option>
							<option value="1">
								关闭
							</option>
						</select>
					</td>
				</tr>
				<tr>
					<td colspan="2" align="center">
						<input type="button" class="button" value=" 提  交  " onclick= "add('minfo')"/>
					</td>
				</tr>
			</tbody>
		</table>
</div>
	</body>
</html>
