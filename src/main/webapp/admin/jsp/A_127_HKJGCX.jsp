<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    
    <title>划款结果查询</title>
    
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
        <script type='text/javascript' src='../../dwr/engine.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../dwr/util.js?<%=rand%>'></script>
       <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
       <script type='text/javascript' src='../../dwr/interface/RypCommon.js?<%=rand%>'></script>
       <script type='text/javascript' src='../../dwr/interface/QuerytransferService.js?<%=rand%>'></script>
	   <script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
	   <script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
	   <script type='text/javascript' src='../../public/js/settlement/adjust_transfer_query.js?<%=rand%>'></script>
  </head>
  
  <body onload="init()">

	<div class="style">
		<form name="MERHLOG" method="post" action="">
			<table width="100%" align="left" class="tableBorder">
				<tr>
					<td class="title" colspan="6">&nbsp;&nbsp;&nbsp;&nbsp;划款结果查询&nbsp;&nbsp;</td>
				</tr>
				<tr>

					<td class="th1" align="right" width="10%">商户号：</td>
					<td align="left" width="20%"><input type="text" id="mid" name="mid" style="width: 150px" size="8px"/></td>



					<td class="th1" align="right" align="right" >结算确认日期：</td>
					<td align="left"><input id="bdate" size="15px" name="bdate" class="Wdate" type="text" onfocus="ryt_area_date('bdate','edate',0,30,0)" /> &nbsp;至&nbsp;
						<input id="edate" size="15px" name="edate" class="Wdate" type="text" disabled="disabled" /><font color="red">*</font></td>
					<td class="th1" align="right" align="right">结算批次：</td>
					<td><input type="text" id="batchNo" name="batchNo" />
					</td>
				</tr>
			     <tr>

					<td class="th1" align="right" width="10%">划款状态：</td>
					<td align="left" width="20%">
					   <select style="width: 150px" id="tstat" name="tstat">
							<option value="">全部...</option>
					    </select>
                    </td>
					<td class="th1" align="right" align="right">代付类型：</td>
					<td>
					<select style="width: 150px" id="type" name="type">
					       <option value="">全部...</option>
							<option value="11">对私代付</option>
							<option value="12">对公代付</option>
					    </select>
					</td>
					<td class="th1" align="right">支付渠道：</td>
					<td align="left"><select style="width: 150px" id="gateRouteId"
						name="gateRouteId">
							<option value="">全部...</option>
					</select></td>
				</tr>
				<tr>
					<td colspan="6" align="center" style="height: 30px">
						<input class="button" type="button" value=" 查 询 " onclick="querytransfer(1);" />&nbsp; 
						<input class="button" type="button" value="下载XLS" onclick="downloadtransfer();" /></td>
				</tr>
			</table>
		</form>

		<table class="tablelist tablelist2" id="queryTransferTable"
			style="display: none;">
			<thead>
				<tr>
					<th>电银流水号</th>
					<th>联行行号</th>
					<th>商户号</th>
					<th>结算批次号</th>
					<th>结算确认日期</th>
					<th>商户名称</th>
					<th>开户银行名称</th>
					<th>开户银行支行名</th>
					<th>开户账户名称</th>
					<th>开户账户号</th>
					<th>划款状态</th>
					<th>划款金额</th>
					<th>划款手续费</th>
					<th>代付类型</th>
					<th>交易银行</th>
					<th>支付渠道</th>
					<th>失败原因</th>
				</tr>
			</thead>
			<tbody id="resultList"></tbody>
		</table>
	</div>
</body>
</html>

