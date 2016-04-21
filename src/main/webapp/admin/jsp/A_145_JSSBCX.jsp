<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" errorPage="../../error.jsp" isErrorPage="false"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
    <head>
    <title>结算发起</title>
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
        <script type='text/javascript' src='../../dwr/engine.js'></script> 
        <script type='text/javascript' src='../../dwr/util.js'></script>
        <script type='text/javascript' src='../../dwr/interface/LiqService.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../dwr/interface/PageService.js?<%=rand %>'></script>
        <script type='text/javascript' src='../../public/datePicker/WdatePicker.js'></script>
       	<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>"'></script>
       	<script type='text/javascript' src='../../public/js/settlement/admin_jssbcx.js?<%=rand%>"'></script>
    </head>
    <body>
     <div class="style">
         <table class="tableBorder" width="80%" align="center"  id="begin_settle">
	       <tbody>
			<tr>
				<td colspan="4" align="left" class="title" height="25">
				<b><font color="#ffffff">&nbsp;&nbsp; 结算失败查询</font> </b>
				</td>
			</tr>
	      	<tr>
	      		<td class="th1" align="right" bgcolor="#d9dfee">商户类型：</td>
	      		<td>
	      			<select id="category">
	      				<option value="">--全部--</option>
	      				<option value="0">RYF商户</option>
	      				<option value="1">VAS商户</option>
	      				<option value="2">POS商户</option>
	      				<option value="3">POS代理商</option>
	      			</select>
      			</td>
				<td class="th1" align="right" bgcolor="#d9dfee">结算截至日期：</td>
				<td align="left"> 
					<input realvalue="" readonly="" id="bToDate" size="15px" name="bToDate" class="Wdate" onfocus="ryt_area_date('bToDate','eToDate',0,30,0)" type="text">
                    &nbsp;至&nbsp;
                    <input id="eToDate" size="15px" name="eToDate" class="Wdate" type="text">
				</td>
		   </tr>
	      	<tr>
	      		<td class="th1" align="right" bgcolor="#d9dfee">商户号：</td>
	      		<td>
	      			<input value="" id="mid" type="text" style="height: 20px;"/>
      			</td>
				<td class="th1" align="right" bgcolor="#d9dfee">结算发起日期：</td>
				<td align="left"> 
					<input realvalue="" readonly="" id="bLiqDate" size="15px" name="bLiqDate" class="Wdate" onfocus="ryt_area_date('bLiqDate','eLiqDate',0,30,0)" type="text">
                    &nbsp;至&nbsp;
                    <input id="eLiqDate" size="15px" name="eLiqDate" class="Wdate" type="text">&nbsp;<font color="red">*</font>
				</td>
		   </tr>
		   <tr>
		   		<td class="th1" align="right" bgcolor="#d9dfee">失败原因：</td>
	      		<td>
	      			<select style="height: 22px;" id="reason">
	      				<option value="">--请选择--</option>
	      			</select>
      			</td>
		   </tr>
		   <tr>
		   		<td colspan="4" align="center">
		   			<input onclick="queryFailRecord(1)" type="button" value="&nbsp;查询&nbsp;"/>&nbsp;&nbsp;&nbsp;&nbsp;
		   			<input onclick="downLiqFailList();" type="button" value="&nbsp;下载&nbsp;"/>
		   		</td>
   			</tr>
		</tbody>
	</table>
    <table id="failListTable" align="left" class="tablelist tablelist2" style="display: none;">
     <thead>
         <tr>
            <th>商户类型</th>
            <th>商户号</th>
            <th>商户简称</th>
            <th>结算截至日期</th>
            <th>结算发起时间</th>
            <th id="reason">失败原因</th>
        </tr>
      </thead>
      <tbody id="resultList"> </tbody>
      </table>
    
</div>
</body>
</html>
