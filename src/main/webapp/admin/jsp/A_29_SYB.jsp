<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>收益表</title>
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
		<script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../dwr/interface/RypCommon.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../dwr/interface/SettlementService.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/datePicker/WdatePicker.js'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
	    <script type="text/javascript" src="../../public/js/settlement/admin_incomeSheet.js?<%=rand%>" ></script>
</head>
<body onload="init1();">

 <div class="style">

<table class="tableBorder" >
  <tr><td class="title" colspan="6">&nbsp;&nbsp;  收益表&nbsp;&nbsp;<!-- (<font color="red">*</font>银行名称M:信用卡支付,W:wap支付,C:充值卡支付,I:语音/快捷支付,其余:网银支付) --></td></tr>
  <tr><td class="th1" bgcolor="#D9DFEE" align="right" width="10%" >商户号：</td>
      <td align="left" width="25%">
      		 <input type="text" id="mid" name="mid" style="width: 150px"  size="8px" onkeyup="checkMidInput(this);" />&nbsp;
              <!-- <select style="width: 150px" id="smid" name="smid" onchange="initMidInput(this.value)" >
                  <option value="">全部...</option>
             </select> &nbsp; &nbsp; -->        
       </td>
       <!-- 
      <td class="th1" bgcolor="#D9DFEE" align="right" width="10%">银行：</td>
      <td align="left" width="50%">
        <select style="width:150px" id="gate" name="gate" >
        </select>
      </td>
       -->
       
        <td class="th1" bgcolor="#D9DFEE" align="right"  width="10%">结算日期：</td>
       <td align="left" width="30%" >
          <input id="date_begin" name="date_begin" class="Wdate" type="text" onfocus="WdatePicker({skin:'ext',minDate:'1998-12-20',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});" />
                至<input id="date_end" name="date_end"  class="Wdate" type="text" onfocus="WdatePicker({skin:'ext',minDate:'#F{$dp.$D(\'date_begin\')}',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});"/>
       <font color="red">*</font></td>
       <td class="th1" bgcolor="#D9DFEE" align="right" width="10%">商户状态：</td>
                <td align="left" width="20%">
                <select style="width: 150px" id="mstate" name="mstate">
                    <option value="">全部...</option>
                </select>
                </td>         
  </tr>
    <!-- 
  <tr>
  
  <td class="th1" bgcolor="#D9DFEE" align="right">交易类型:</td>
      <td align="left">
           <select id="type" name="type" >
           </select>
       </td>
       <td class="th1" bgcolor="#D9DFEE" align="right" >查询日期：</td>
       <td align="left">
          <input id="date_begin" name="date_begin" class="Wdate" type="text" onfocus="WdatePicker({skin:'ext',minDate:'1998-12-20',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});" />
                至<input id="date_end" name="date_end"  class="Wdate" type="text" onfocus="WdatePicker({skin:'ext',minDate:'#F{$dp.$D(\'date_begin\')}',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});"/>
       <font color="red">*</font></td>
  </tr>
   -->
  <tr><td colspan="6" align="center" height="30px"><input type="button" class='button'  value="查询" onclick="queryBaseIncome();"/>
  
  
  <input type='button' name='download' id='download' class='button' value='导出Excel' onclick="downLoadInCome();"></input>
  
  </td></tr>
</table>

<br/>
<div id="IncomeList" style="display:none">
    <table class="tablelist tablelist2">
      <thead>
        <tr><th>结算日期</th><th>商户号</th><th>商户简称</th>
        <!-- 
        <th>交易银行</th><th>交易类型</th>
         -->
         <th>交易金额</th><th>退款金额</th>
        
        <!-- <th>支付结算金额</th>
         -->
        <th>系统手续费</th><th>系统退回手续费</th><th>银行手续费</th><th>银行退回手续费</th><th>收益金额</th></tr>
      </thead>
      <tbody id="IncomeTable"></tbody>
    </table>
</div>
</div>
  </body>
</html>