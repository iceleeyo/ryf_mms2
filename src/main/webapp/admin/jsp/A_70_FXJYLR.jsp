<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>风险交易录入</title>
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
        <script type="text/javascript" src="../../dwr/engine.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../dwr/util.js?<%=rand%>"></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
      
        <script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../dwr/interface/RiskmanageService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../public/js/ryt.js?<%=rand%>'></script> 
       	<script type="text/javascript" src='../../public/js/riskmanage/add_risk_log.js?<%=rand%>'></script> 
    </head>

    <body onload="initAdminQuery('tlog')">
    
    <div class="style">
         <table width="100%"  align="left"  class="tableBorder" >
            <tr><td class="title" align="center">风险交易录入</td></tr>
            <tr>
                <td align="center" height="35px">
                <select  id="t" name="t">
	                <option value="tseq">电银流水号</option>
	                <option value="oid">商户订单号</option>
	                <option value="bkseq">银行流水号</option>
                </select>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <input type="text" id="k" name="k"  class="input"/>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                
                 <input type="button" value= "查 询" name="submitQuery" onclick="queryAlog();" class="button"/>
                 <span  style="color:red;display: none;" id="noMsg">没有符合条件的记录！</span>
                </td>
            </tr>
        </table>
       
<!-- style="display: none" -->
    <table id= "resultList" class="tablelist tablelist2" style="display: none">
           <thead>
             <tr>
             <th>电银流水号</th>
             <th>商户订单号</th>
             <th>银行流水号1</th>
              <th>银行流水号2</th>
             <th>商户号</th>
             <th>交易金额(元)</th>
             <th>交易状态</th>
             <th></th>
           </tr>
           </thead>
           <tbody id="resultListTB"></tbody>
       </table>
       <table id="logDetail"  class="tablelist tablelist2"  style="display: none">
           <tr><td class="title" colspan="6" align="center">商户订单详细资料</td></tr>
           <tr >
               <td align="left" class="th1" width="10%" >商户日期：</td>
               <td align="left" width="15%" id="v_mdate"></td>
               <td align="left" class="th1" width="8%" >商户号：</td>
               <td align="left" width="15%" id="v_mid"></td>
               <td align="left" class="th1" width="8%">商户简称：</td>
               <td align="left" width="15%" id="v_midName"></td>
           </tr>
           <tr >
               <td align="left" class="th1" >订单号：</td>
               <td align="left" id="v_oid"></td>
               <td align="left" class="th1" >交易金额：</td>
               <td align="left" id="v_amount"></td>
               <td align="left" class="th1" >交易类型：</td>
               <td align="left" id="v_type"></td>
           </tr>
           <tr >
               <td align="left" class="th1" >支付网关号：</td>
               <td align="left" id="v_gate"></td>
               <td align="left" class="th1" >支付网关：</td>
               <td align="left" id="v_gateName"></td>
               <td align="left" class="th1" >系统日期：</td>
               <td align="left" id="v_sys_date"></td>
           </tr>
           <tr >
               <td align="left" class="th1" >初始的系统日期：</td>
               <td align="left" id="v_init_sys_time"></td>
               <td align="left" class="th1" >电银流水号：</td>
               <td align="left" id="v_tseq"></td>
               <td align="left" class="th1" >清算批次号：</td>
               <td align="left" id="v_batch"></td>
           </tr>
           <tr >
               <td align="left" class="th1" >系统手续费：</td>
               <td align="left" id="v_fee_amt"></td>
               <td align="left" class="th1" >交易状态：</td>
               <td align="left" id="v_tstat"></td>
               <td align="left" class="th1" >银行应答标志：</td>
               <td align="left" id="v_bk_flag"></td>
           </tr>
           <tr >
               <td align="left" class="th1" >总申请退款金额：</td>
               <td align="left" id="v_refund_amt">
               </td>
               <td align="left" class="th1" >系统时间：</td>
               <td align="left" id="v_sys_time"></td>
               <td align="left" class="th1" >发送银行时间:</td>
               <td align="left" id="v_bk_send"></td>
           </tr>
           <tr >
               <td align="left" class="th1" >银行响应时间:</td>
               <td align="left" id="v_bk_recv"></td>
               <td align="left" class="th1" >银行手续费：</td>
               <td align="left" id="v_bank_fee"></td>
               <td align="left" class="th1" >银行对帐标志：</td>
               <td align="left" id="v_bk_chk"></td>
           </tr>
           <tr >
               <td align="left" class="th1" >银行日期：</td>
               <td align="left" id="v_bk_date">&nbsp;</td>
               <td align="left" class="th1" >银行流水1：</td>
               <td align="left" id="v_bk_seq1">&nbsp;</td>
               <td align="left" class="th1" >银行流水2：</td>
               <td align="left" id="v_bk_seq2">&nbsp;</td>
           </tr>
           <tr >
               <td align="left" class="th1">银行返回代码：</td>
               <td align="left" id="v_bk_resp">&nbsp;</td>
               <td align="left" class="th1" >&nbsp;</td>
               <td align="left" id="">&nbsp;</td>
               <td align="left" class="th1" >&nbsp;</td>
               <td align="left" id="">&nbsp;</td>
           </tr>
        </table>
        <br />
        <table id="addRiskRemarksTB" style="display: none">
            <tr><td valign="top">&nbsp;&nbsp; </td></tr>
            <tr>
                <td valign="top">备注录入原因:&nbsp;&nbsp; 
                
                <input type="hidden" id="add_remarks_tseq" />
                <input type="hidden" id="add_remarks_oper_id" />
                
                </td>
                <td><textarea rows="5" cols="40" id="add_remarks"></textarea><font color="red" style="padding-left:4px;">*</font></td>
            </tr>
            <tr>
                <td valign="top">&nbsp;&nbsp; </td>
                <td height="30px"><input type="button" value="确认录入" class="button" onclick="addRiskLog()" id="submit_id" disabled="disabled"/></td>
            </tr>
        </table>
        
      </div>
    </body>
</html>
