<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>当天收款交易查询</title>
<%
response.setHeader("Pragma","No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", 0);
int rand = new java.util.Random().nextInt(10000);
%>
        <meta http-equiv="pragma" content="no-cache" />
        <meta http-equiv="cache-control" content="no-cache"/>
        <meta http-equiv="Expires" content="0"/>  
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css"/> 
        <script type="text/javascript" src="../../dwr/engine.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../dwr/util.js?<%=rand%>"></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/TransactionService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/QueryMerMerTodayService.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../public/datePicker/WdatePicker.js'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script> 
        <script type="text/javascript" src="../../public/js/transaction/admin_ddjycx.js?<%=rand%>"></script>
    </head>
    <body onload="init();">
    <div class="style">
        <form name="MERTLOG">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="6">&nbsp;&nbsp;&nbsp;&nbsp;掉单交易查询</td></tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="10%">银行：</td>
                <td align="left" width="20%">
	                <select style="width: 150px" id="gate" name="gate">
	                    <option value="">全部...</option>
	                </select>
                </td>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="10%">交易状态：</td>
                <td align="left" width="25%">
	                <select style="width: 150px" id="tstat" name="tstat" >
	                    <option value="">全部...</option>
	                </select>
                </td>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="10%" >交易类型：</td>
                <td align="left">
                    <select style="width: 150px" id="type" name="type" onchange="onChangeGate();">
                        <option value="">全部...</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right">商户订单号： </td>
                <td align="left"> <input type="text" id="oid" name="oid"  maxlength="30"/></td>
                <td class="th1" bgcolor="#D9DFEE" align="right">电银流水号：</td>
                <td align="left"><input type="text" id="tseq" name="tseq" id="tseq"/></td>
                <td class="th1" align="right">金额：</td>
                <td align="left"> 
                	<input type="text" id="amount" name="amount" value="" size="15px"/>
                </td>
            </tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right">商户号： </td>
                <td align="left"> <input type="text" id="mid" name="mid"  maxlength="30"/></td>
                <td class="th1" bgcolor="#D9DFEE" align="right">交易日期： </td>
            	<td colspan="3">
            		<input id="bdate" size="15px" name="bdate" class="Wdate" type="text" onfocus="ryt_area_date('bdate','edate',0,30,0)"><span style="margin: 10px;">至</span><input id="edate" size="15px" name="edate" class="Wdate" type="text" disabled="disabled">
           		</td>
            </tr>
            <tr>
            <td colspan="6" align="center" style="height: 30px">
                <input class="button" type="button" value = " 查 询 " onclick="queryNotifyFailedOrderRecord(1);" />
                <input class="button" type="button" value= " 下载XLS " onclick = "downloadNotifyFailedRecord();"/>
            </td>
            </tr>
        </table>
       </form>
    
       <table class="tablelist tablelist2"  id="notifyFailedTable">
           <thead>
           <tr>
             <th sort="false"><input type="checkbox" id="toggleAll" onclick="toggleAll(this)"/>全选</th>
             <th>商户号</th><th>商户订单号</th>
             <th>交易金额(元)</th><th>交易状态</th>
             <th>交易类型</th><th>交易银行</th>
             <th>系统手续费(元)</th><th>交易日期</th>
             <th>操作 </th>
           </tr>
           </thead>
           <tbody id="resultList"></tbody>
       </table>
       
      </div>   
    </body>
</html>
