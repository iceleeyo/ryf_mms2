<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>数据分析查询</title>
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
  	   <script type='text/javascript' src='../../dwr/engine.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../dwr/util.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/TransactionService.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>' ></script>
		<script type="text/javascript" src='../../public/js/transaction/admin_queryDataAnalysis.js?<%=rand%>'></script>
</head>
<body onload="initOptions();">
<div class="style">
     <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="6">&nbsp;&nbsp;&nbsp;&nbsp;数据分析查询</td></tr>
            <tr>
            <td class="th1" align="right" width="10%">查询类型：</td>
                <td align="left" width="30%">
               <select id="queryType" onchange="changeQuery(this.value);" style="width: 150px">
                	<option value="1">历史明细交易</option><option value="2">退款交易</option>
                </select>
                 </td>
               <td class="th1"  align="right" width="10%" >状态：</td>
                <td align="left" width="20%">
                <select id="state" name="state" style="width: 150px">
						<option value="">全部...</option>
					</select>
                </td>
                 <td class="th1" align="right" width="10%">商户号：</td>
                <td align="left" width="27%">
                <input type="text" id="mid" name="mid"  style="width:150px;" onkeyup="checkMidInput(this);"/>
                 <!-- <select style="width: 130px" id="smid" name="smid" onchange="initMidInput(this.value)">
                     <option value="">全部...</option>
                   </select> --> 
                 </td>
            </tr>
            <tr>
             <td class="th1" align="right" id="queryDate"> 交易系统日期： </td>
                <td align="left" >
                    <input id="bdate" size="15px"
                    name="bdate" class="Wdate" type="text" onfocus="ryt_area_date('bdate','edate',0,30,0)" />
                    &nbsp;至&nbsp;
                    <input id="edate" size="15px" name="edate" class="Wdate" type="text" disabled="disabled" /><font color="red">*</font>
                </td>
            <td class="th1" align="right">商户所属行业：</td>
                <td  width="20%"><select id="merTradeType" name="merTradeType" style="width: 150px">
						<option value="">全部...</option>
					</select>
				</td>
                <td class="th1" align="right">商户状态：</td>
                <td align="left">
                 <select style="width: 150px" id="mstate" name="mstate">
                    <option value="">全部...</option>
                </select></td>
            </tr>
            <tr>
            <td colspan="6" align="center" style="height: 30px">
                <input class="button" type="button" value = " 查 询 " onclick="queryAnalysisData(1);" />&nbsp; 
            </td>
            </tr>
        </table>
     <table width="100%" class="tablelist tablelist2" id="transTable" style="display:none;">
        <thead>
           <tr>
             <th>电银流水号</th><th>商户号</th><th>商户简称</th>
             <th>所属行业</th><th>商户订单号</th>
             <th>交易金额</th><th>交易状态</th>
             <th>交易类型</th><th>交易银行</th><th>支付渠道</th>
             <th>系统手续费</th><th>银行手续费</th>
             <th>系统时间</th>
           </tr>
           </thead>
        <tbody id="transBody"></tbody>   
     </table>
    <table width="100%" class="tablelist tablelist2" id="refundTable" style="display:none;">
       <thead> <tr valign="middle" class="title2">
            <th>原电银流水号</th>
            <th>商户号</th>
            <th>商户简称</th>
            <th>所属行业</th>
            <th>原商户订单号</th>
            <th>交易银行</th>
            <th>支付渠道</th>
            <th>退款金额</th>
            <th>退款状态</th> 
            <th>经办日期</th>
        </tr></thead>
        <tbody id="refundBody"></tbody>   
     </table>
</div>
</body>
</html>
