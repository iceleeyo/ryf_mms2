<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>银行网关配置</title>
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
		<script type="text/javascript" src='../../dwr/interface/MerchantService.js?<%=rand %>'></script>
		<script type="text/javascript" src='../../dwr/interface/CommonService.js?<%=rand%>'></script>
		<script type="text/javascript" src="../../public/js/GreyFrame.js?<%=rand%>"></script>
        <script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script> 
        <script type="text/javascript" src='../../public/js/merchant/admin_minfo_gate_edit.js?<%=rand%>'></script>
        
	</head>

	<body onload="initParam();"  >
		<div class="style">
		<table class="tableBorder" >
		<tbody>
		<tr >
		<td colspan="10" class="title"><span style="float:left;">&nbsp; &nbsp;商户网关配置审核</span></td>
		</tr> 
		<tr>
	        <td class="th1" align="right" width="10%" style="height: 30px">商户号：</td>
			<td align="left" > 
				<input type="text" id="mid" name="mid" size="8px"/>
			</td>
	        <td class="th1" align="right" width="10%" style="height: 30px">商户名称：</td>
			<td align="left" > 
				<input type="text" id="mName" name="mName" size="8px"/>
			</td>
	        <td class="th1" align="right" width="10%" style="height: 30px">配置状态：</td>
			<td align="left" > 
				<select id="status">
					<option value="0">待审核</option>
					<option value="1">审核通过</option>
					<option value="2">审核失败</option>
				</select>
			</td>
	        <td class="th1" align="right" width="10%" style="height: 30px">申请类型：</td>
			<td align="left" > 
				<select id="type">
					<option value="1">配置申请</option>
					<option value="0">开关申请</option>
				</select>
			</td>
	        <td class="th1" align="right" width="10%" style="height: 30px">状态：</td>
			<td align="left" > 
				<select id="state">
					<option value="">全部</option>
					<option value="1">开启</option>
					<option value="0">关闭</option>
				</select>
			</td>
		</tr>
		<tr>
			<td align="center" colspan="10"><input type="button" value="查  询" onclick="searchPage();" class="button"/></td>
		</tr>
		</tbody>
		</table>
<%--		style="display:none;"--%>
       <table class="tablelist tablelist2" id="configTable" style="display:none;" >
       		<thead>
	           <tr>
	            <th id="toggleAll1Th" sort="false" ><input id="toggleAll1" type="checkbox" style="vertical-align: middle;" onclick="toggleAll(this);"/>全选</th>
				<th id="shh1">商户号</th>
				<th>网关号</th>
				<th>网关简称</th> 
				<th>银行网关号</th>
				<th>交易方式</th>
				<th>银行手续费</th>
				<th>商户手续费</th>
				<th>渠道</th>
				<th>状态</th>
				<th>生效时间</th>
				<th>申请状态</th>
				<th>操作</th>
	           </tr>
            </thead>
           <tbody id="configList"></tbody>
       </table>
       
       <table class="tablelist tablelist2" id="switchTable" style="display:none;" >
       		<thead>
	           <tr>
	            <th id="toggleAll2Th" sort="false" ><input id="toggleAll2" type="checkbox" style="vertical-align: middle;" onclick="toggleAll(this);"/>全选</th>
				<th id="shh2">商户号</th>
				<th>网关号</th>
				<th>网关简称</th> 
				<th>交易方式</th>
				<th>申请类型</th>
				<th>当前状态</th>
				<th>申请状态</th>
				<th>操作</th>
	           </tr>
            </thead>
           <tbody id="switchList"></tbody>
       </table>
       
		</div>
	</body>
</html>
