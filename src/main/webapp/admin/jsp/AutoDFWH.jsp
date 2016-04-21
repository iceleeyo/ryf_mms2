<%@ page language="java" import="java.util.*" pageEncoding="Utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
  
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  <body>
   <table id="AutoDfWH" class="tableBorder" style="display:none; width: 400px; height: 400px; margin-top: 0px;">
			 <tr>
          	  <td class="th1" bgcolor="#D9DFEE" align="right"  width="35%">商户号:</td>
                <td align="left"   ><input type="text" id="m_mid"  size="26" disabled="disabled"/>
               </td>
               </tr>
               
                <tr>
          	  <td class="th1" bgcolor="#D9DFEE" align="right"  width="35%">商户名称:</td>
                <td align="left"   ><input type="text" id="m_name"  size="26"  disabled="disabled"/>
               </td>
               </tr>
               <tr>
          	   <td class="th1" bgcolor="#D9DFEE" align="right"  width="35%">所属行业:</td>
                <td align="left"   ><input type="text" id="mer_trade_type" size="26" disabled="disabled"/>
               </td>
               </tr>
               <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right"  width="35%">账户号:</td>
                <td align="left"   ><input type="text" id="aid"  size="26" disabled="disabled"/>
               </td>
               </tr>
               <tr>
               <td class="th1" bgcolor="#D9DFEE" align="right"  width="35%">自动代付状态:</td>
                <td align="left">
                <select id="AutoDfState">
                    <option value="0">关闭</option>
                    <option value="1">开启</option>
                </select>     
               </td>
               </tr>
                <tr>
               <td class="th1" bgcolor="#D9DFEE" align="right"  width="35%">开户银行省份:</td>
                <td align="left"   >
                <select id="pbkProvId">
                    <option value=" ">请选择...</option>
                </select>&nbsp;&nbsp;<font color="red">*</font> 
               </td>
               </tr>
               <tr>
          	   <td class="th1" bgcolor="#D9DFEE" align="right"  width="35%">开户银行名:</td>
                <td align="left"   ><input type="text" id="pbkName" size="26"/>&nbsp;&nbsp;<font color="red">*</font>
               </td>
               </tr>
                <tr>
          	   <td class="th1" bgcolor="#D9DFEE" align="right"  width="35%">开户银行支行名:</td>
                <td align="left"   ><input type="text" id="pbkBranch" size="26"/>&nbsp;&nbsp;<font color="red">*</font>
               </td>
               </tr>
                <tr>
          	   <td class="th1" bgcolor="#D9DFEE" align="right"  width="35%">银行帐号:</td>
                <td align="left"   ><input type="text" id="pbkAccNo" size="26"/>&nbsp;&nbsp;<font color="red">*</font>
               </td>
               </tr>
               <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right"  width="35%">开户账号名:</td>
                <td align="left"   ><input type="text" id="pbkAccName" size="26"/>&nbsp;&nbsp;<font color="red">*</font>
               </td>
               </tr>
                <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right"  width="35%">开户银行行号:</td>
                <td align="left"   ><input type="text" id="pbkNo" size="26"/>&nbsp;&nbsp;<font color="red">*</font>
               </td>
               </tr>
              <tr>
				<td colspan="4" align="center" height="20px;">
					<input value="提交" type="button" class="button" onclick="edit(1);" />
					<input value="返回" type="button" class="wBox_close button" />
				</td>
			</tr>
		</table>
  </body>
</html>
