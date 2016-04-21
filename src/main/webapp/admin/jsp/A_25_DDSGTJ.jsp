<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>掉单手工提交</title>
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
        <script type='text/javascript' src='../../dwr/interface/PageService.js?<%=rand%>'></script>
	    <script type='text/javascript' src='../../dwr/interface/RypCommon.js?<%=rand%>'></script>
	    <script type='text/javascript' src='../../dwr/interface/SettlementService.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script> 
 		<script type="text/javascript" src="../../public/js/settlement/admin_submit_lost_order.js?<%=rand%>"></script>    

    </head>
    
    <body onload="initLostOrder();">
    <div class="style">
    <form>
    <table class="tableBorder" >
     <tbody>
        <tr><td class="title" colspan="4"> &nbsp; 掉单手工提交&nbsp;(带<font color="red">*</font>为必填项)</td></tr>
        <tr>
            <td class="th1" align="right" width="20%">商户号：</td>
            <td align="left" width="30%">
            <input type="hidden" id="lostOrderTseq"  value='' />
           	<input type="text" id="mid" name="mid"  size="8px" style="width:150px"  onkeyup="checkMidInput(this);"/>
                    &nbsp;<!-- <select style="width: 150px" id="smid" name="smid" onchange="initMidInput(this.value)">
                     <option value="">全部...</option>
                    </select> --> 
            <font color="red">*</font>&nbsp;</td>
            <td class="th1" align="right" width="20%">订单号：</td>
            <td align="left" width="30%"><input type="text" id="oid" name="oid" size="20"/></td>          
       </tr>
       <tr>
        <!-- 
        <td class="th1" align="right" width="20%">订单生成日期：</td>
        <td align="left" width="30%">
        <input name="btdate" id="btdate" class="Wdate" type="text"  onfocus="WdatePicker({skin:'ext',minDate:'1997-01-01',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});"/>&nbsp;<font color="red">*</font></td>
         -->
        <td class="th1" align="right" width="20%">电银流水号：</td>
        <td align="left" width="30%"><input type="text" id="tseq" name="tseq" size="20"/></td>
         
         <td class="th1" align="right" width="20%"></td>
        <td align="left" width="30%">
        </td>
        
       
       </tr>
       <tr><td  colspan="4" align="center" ><input type="button" class="button" value="查询" onclick="queryBaseLostOrder(1)"/>
       </td></tr>
    </tbody>
   </table>
   </form>
   
 <div id="lostOrderList" style="display: none">
 <form name="form2" method="post" action="">
   <table class="tablelist tablelist2">
    <thead><tr>
    <th>选择</th><th>商户号</th><th>商户简称</th>
    <th>电银流水号</th><th>订单号</th>
    <th>交易金额(元)</th><th>订单生成时间</th>
    <th>支付渠道</th><th>交易状态</th></tr></thead>
    
    
    <tbody id="lostOrderTable"></tbody>
    </table>
    <table class="tableBorder" >
    <tbody id="pageTable"></tbody>
    </table>
 </form>
 </div>
 
  <div id="lostOrderList2" style="display: none" >
    <p align="center">掉单手工确认&nbsp;(带<font color="red">*</font>为必填项,&nbsp;&nbsp;银行名称M:信用卡支付,W:wap支付,C:充值卡支付,I:语音/快捷支付,其余:网银支付)</p>
    <table class="tablelist tablelist2">
    <thead><tr><th>电银信息系统流水号</th><th>商户号</th><th>商户简称</th><th>订单号</th><th>交易金额(元)</th><th>订单生成时间</th><th>支付渠道</th><th>交易状态</th><th>交易银行</th></tr></thead>
    <tr>
        <td id="tseq_"></td>
        <td id="mid_"></td>
        <td id="name_"></td>
        <td id="oid_"></td>
        <td id="amount_"></td>
        <td id="sys_date_"></td>
        <td id="gid_"></td>
        <td>待支付 <input type="hidden" value="" id="bkgate_"/></td>
        <td id="gate_"></td>
       
    </tr>
    </table>
    <table class="tableBorder" id="confirmLostOrderTable">
        <tr><td class="title" colspan="8"> &nbsp; 此订单经人工到银行查询确认&nbsp;</td></tr>
        <tr><td class="th1" align="right" width="40%">交易银行名称：</td>
            <td align="left" >
            <select id="gate__" name="gate"> </select>&nbsp;<font color="red">*</font></td>
        </tr>
        <tr><td class="th1" align="right" width="40%">银行交易流水号：</td>
            <td align="left" ><input type="text" id="bank_code" name="bank_code" value="" size="20" maxlength="20"/>&nbsp;<font color="red">*</font></td>          
        </tr>
        <tr><td class="th1" align="right" width="40%">银行交易金额：</td>
            <td align="left" ><input type="text" id="bank_amount" name="bank_amount" value="" size="20" maxlength="20"/>&nbsp;<font color="red">*</font></td>
       </tr>
       <tr><td colspan="8" align="center">
       <input type="button" id="ConfirmSuccess" name="ConfirmSuccess" value="确认支付成功" onclick="checkSubmitData('success')"/>&nbsp;&nbsp;&nbsp;
       <!-- 
       <input type="button" id="ConfirmFailure" name="ConfirmFailure" value="确认支付失败" onclick="checkSubmitData('failure')"/>&nbsp;&nbsp;&nbsp;
        -->
       <input type="button" value="返  回" onclick="viewList()"/>
       </td></tr>
    </table>
 </div>
 
 </div>
</body>
</html>
