<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
       <title>结算制表</title>
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
        <script type='text/javascript' src='../../dwr/engine.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../dwr/util.js?<%=rand%>'></script>
       <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
	    <script type='text/javascript' src='../../dwr/interface/SettlementTableService.js?<%=rand%>'></script>
	    <script type="text/javascript" src="../../public/js/settlement/admin_download_settlement.js?<%=rand%>"></script>
		<script type='text/javascript' src='../../public/datePicker/WdatePicker.js'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
    </head>
    <body onload="init();">
    <div class="style">
    <table class="tableBorder" >
        <tr><td class="title" colspan="6">&nbsp;结算制表&nbsp;&nbsp;</td></tr>
        <tr>
         <td class="th1" bgcolor="#D9DFEE" align="right" width="10%"> 商户号：</td>
         <td align="left" width="30%">
              	<input type="text" id="mid" name="mid"  size="8px" style="width:150px" onkeyup="checkMidInput(this);"/>
                    &nbsp;<!-- <select style="width: 150px" id="smid" name="smid" onchange="initMidInput(this.value)">
                     <option value="">全部...</option>
                    </select> --> 
         </td>
         <td class="th1" bgcolor="#D9DFEE" align="right" width="10%" >结算状态:</td>
         <td align="left" width="*%">
           <select id="state" name="state" >
             <option value="0">全部</option>
             <option value="1" selected="selected" >已发起</option>
             <option value="2">已制表</option>                            
           </select>
         </td>
          <td class="th1" align="right" >商户状态：</td>
             <td align="left">
                <select style="width: 150px" id="mstate" name="mstate">
                    <option value="">全部...</option>
                </select>
           </td>
       </tr>
       <tr>
        <td class="th1" bgcolor="#D9DFEE" align="right" height="30px">结算发起日期：</td>
         <td align="left">
          <input id="beginDate" name="beginDate" class="Wdate" type="text" value=""
                        onfocus="WdatePicker({skin:'ext',minDate:'1998-12-20',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});" />
                            至<input id="endDate" name="endDate"  class="Wdate" type="text" value=""
                        onfocus="WdatePicker({skin:'ext',minDate:'#F{$dp.$D(\'beginDate\')}',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});"/>
          <font color="red">*</font>
         </td>
         <td class="th1" bgcolor="#D9DFEE" align="right"  height="30px">批次号：</td>
         <td align="left" ><input type="text" id="batch" name="batch"/></td>
          <td class="th1" bgcolor="#D9DFEE" align="right"  height="30px"><!-- 结算对象 --></td>
         <td>
         	 <!-- 
             <select id="jsdx">
         			<option value="0">商户银行卡</option>
         			<option value="1">商户电银账户</option>
         			<option value="2">代理商账户</option>
        	 </select>
        	  -->
         </td>
       </tr>
       <tr>
       <td colspan="6" align="center">
                    <input type="button" name="search" class="button" value="查询"  onclick="queryDownload_settlement(1)"/>
                    <input type="hidden" name="search" value="yes"/>
            </td>
      </tr>
     </table>
     <br/>
     <div id = "resultTable" style="display:none">
       <table class="tablelist tablelist2"  width="80%" align="center" >
        <thead>
         <tr  valign="middle" class="title2">
            <th sort="false"><input type="checkbox" onclick="allSelected()" id="allSelect" disabled="disabled" />全选</th><th>商户号</th>
            <th>商户简称</th><th>支付金额</th><th>退款金额</th><th>交易净额</th><th>系统手续费</th><th>系统退回手续费</th><th>手工调增</th>
            <th>手工调减</th><th>清算金额</th><th>结算批次号</th><th>发起日期</th><th>结算状态</th>
         </tr>
        </thead>
        <tbody id="bodyTable">
        </tbody>
    </table>
     <table class="tableBorder" width="80%" align="center">
        <tbody id="buttomTable"></tbody>
    </table>
     </div>
     
     </div>
     
    </body>
</html>
