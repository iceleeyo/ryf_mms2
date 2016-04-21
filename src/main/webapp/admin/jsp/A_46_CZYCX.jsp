<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>商户信息查询</title>
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
        <link href="../../public/css/head.css" rel="stylesheet" type="text/css"/>	
		<script type='text/javascript' src='../../dwr/engine.js?<%=rand %>'></script>
		<script type='text/javascript' src='../../dwr/util.js?<%=rand %>'></script> 
		<script type='text/javascript' src='../../dwr/interface/MerchantService.js?<%=rand %>'></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
		<script type="text/javascript" src='../../public/js/merchant/admin_search_operator.js?<%=rand%>'></script>
	</head>
	<body onload="initMinfos();">
	
	    <div class="style">
	<table class="tableBorder" id="queryTJ" >
		<tbody>
			<tr>
				<td class="title" colspan="4">&nbsp;&nbsp; 操作员查询
				</td>
			</tr>			
			<tr>
                   <td class="th1" width="20%" align="right" style="height: 30px;">&nbsp;商户号：</td>
			    <td width="40%" align="left" id="selectMerTypeId">
                      <input type="text" id="mid" name="mid"   size="8px" onkeyup="checkMidInput(this);"/>
                    &nbsp;&nbsp;<!-- <select style="width: 150px" id="smid" name="smid" onchange="initMidInput(this.value)">
                     <option value="">全部...</option>
                   </select> -->
                       </td>
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

	<table class="tablelist tablelist2" id="body4List" style="display: none;">
		    <tr>
				<th>商户号</th><th>操作员号</th><th>操作员名</th><th>操作员电话</th><th>操作员邮箱</th>
				<th>注册日期</th><th>状态标志</th>
			</tr>
			<tbody id="operInfoBody">
			</tbody>
		</table>
		</div>
	</body>
</html>
