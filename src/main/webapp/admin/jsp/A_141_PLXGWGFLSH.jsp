<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>批量修改网关费率审核</title>
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
        <link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css"/>
        <script type='text/javascript' src='../../dwr/engine.js'></script>
        <script type='text/javascript' src='../../dwr/util.js'></script>
        <script type="text/javascript" src="../../dwr/interface/PageParam.js?<%=rand %>"></script>
        <script type="text/javascript" src="../../dwr/interface/PageService.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../dwr/interface/MerchantService.js?<%=rand%>"></script> 
		<script type="text/javascript" src="../../public/datePicker/WdatePicker.js?<%=rand %>"></script>
		<script type="text/javascript" src="../../public/js/ryt.js?<%=rand %>"></script>
        <script type="text/javascript" src='../../public/js/sysmanage/gateFee.js?<%=rand %>'></script>
  </head>
  <body onload="initOptions();">
  <div class="style">
   <table class="tableBorder" >
             <tbody id="queryTiaojian">
              	<tr>
           			<td class="title" colspan="7">&nbsp;网关费率修改申请</td>
                </tr>
                 <tr>
                   <td class="th1" align="right" width="20%" style="height: 30px">商户号：</td>
                   <td>
                   		<input type="text" id="mid" name="mid" size="8px" onkeyup="checkMidInput(this);"/><font color="red">*</font>
                   </td>
                   <td class="th1" align="right" width="20%" style="height: 30px">网关类型：</td>
                   <td>
                   		<select id="trans_mode"></select>
                   </td>
                   <td class="th1" align="right" width="20%" style="height: 30px">状态：</td>
                   <td>
                   		<select>
                   			<option>所有</option>
                   			<option>已启用</option>
                   			<option>未启用</option>
                   		</select>
                   </td>
                   <td>
                   		<input type="button" value="查询" class="button" onclick="showMerMsg();"/>
               	   </td>
                 </tr>
             </tbody>
      </table>
      
      <table class="tablelist tablelist2">
      		<tbody>
	       		<tr>
					<th class="title"><input style="vertical-align: middle;" type="checkbox"></input>&nbsp;全选</th>
					<th>商户号</th>
					<th>商户简称</th>
					<th>网关类型</th>
					<th>现有费率</th>
					<th>申请费率</th>
					<th>状态</th>
					<th>操作</th>
				</tr>
      			<tr></tr>
      		</tbody>
      </table>
	</div>
  </body>
</html>
