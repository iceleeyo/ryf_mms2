<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" errorPage="../../error.jsp" isErrorPage="false"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
    <head>
    <title>资金明细查询</title>
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
        <script type='text/javascript' src='../../dwr/interface/TrDetailsService.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../public/datePicker/WdatePicker.js'></script>
       	<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>"'></script>
       	<script type='text/javascript' src='../../public/js/settlement/admin_zjmxcx.js?<%=rand%>"'></script>
    </head>
    <body>
     <div class="style">
<!--      搜索 -->
         <table class="tableBorder" width="80%" align="center"  id="begin_settle">
	       <tbody>
			<tr>
				<td colspan="8" align="left" class="title" height="25">
				<b><font color="#ffffff">&nbsp;&nbsp; 资金明细查询</font> </b>
				</td>
			</tr>
	      	<tr>
	      		<td class="th1" align="right" bgcolor="#d9dfee">资金下载渠道：</td>
	      		<td>
	      			<select id="gateRoute">
	      				<option value="">盛京银行</option>
	      			</select>
      			</td>
				<td class="th1" align="right" bgcolor="#d9dfee">收支类型：</td>
				<td align="left"> 
	      			<select id="jd">
	      				<option value="">--全部--</option>
	      				<option value="0">收入</option>
	      				<option value="1">支出</option>
	      			</select>
				</td>
				<td class="th1" align="right" bgcolor="#d9dfee">交易日期：</td>
				<td align="left"> 
					<input realvalue="" readonly="" id="bDate" size="15px" name="bDate" class="Wdate" onfocus="ryt_area_date('bDate','eDate',0,30,0)" type="text"/>
                    &nbsp;至&nbsp;
                    <input id="eDate" size="15px" name="eDate" class="Wdate" type="text"/>&nbsp;<font color="red">*</font>
				</td>
		   </tr>
		   <tr>
		   		<td colspan="8" align="center">
		   			<input onclick="query(1)" type="button" value="&nbsp;查询&nbsp;"/>&nbsp;&nbsp;&nbsp;&nbsp;
		   			<input onclick="download();" type="button" value="&nbsp;下载&nbsp;"/>
		   		</td>
   			</tr>
		</tbody>
	</table>
<!-- 	列表 -->
    <table id="listTable" align="left" class="tablelist tablelist2" style="display: none;">
     <thead>
         <tr>
            <th>交易日期</th>
            <th>交易时间</th>
            <th>收入</th>
            <th>支出</th>
            <th>手续费总额</th>
            <th>对方帐号</th>
            <th>对方名称</th>
            <th>账户余额</th>
            <th>交易行所</th>
            <th>摘要</th>
            <th>流水号</th>
            <th>备注</th>
        </tr>
      </thead>
      <tbody id="resultList"></tbody>
      </table>
    
</div>
</body>
</html>
