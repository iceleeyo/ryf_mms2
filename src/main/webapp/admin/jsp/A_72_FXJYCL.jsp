<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>风险交易处理</title>
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
        <script type="text/javascript" src="../../dwr/util.js?<%=rand%>?<%=rand%>"></script>
        <script type="text/javascript" src="../../dwr/interface/PageService.js?<%=rand%>"></script>
        <script type='text/javascript' src="../../dwr/interface/RiskmanageService.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../public/datePicker/WdatePicker.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../public/js/ryt.js?<%=rand%>"></script>
		<script type="text/javascript" src="../../public/js/riskmanage/confirm_risk.js?<%=rand%>" ></script>     
      
    </head>
    <body onload="initParams();">
    
    <div class="style">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" align="center">风险交易处理</td></tr>
             <tr>
                <td align="center" height="35px">
                      风险状态&nbsp;&nbsp;<select  id="rstate" name="rstate">
                  <option value=" 2 or rstate = 3 or rstate = 4 ">全部</option>
                    <option value="2">风险交易冻结</option>
                    <option value="3">风险交易解冻</option>
                    <option value="4">风险交易确认</option>
                </select>
                &nbsp;&nbsp;录入日期：&nbsp;
                
                <input id="sys_date_begin" size='12' name="sys_date_begin" class="Wdate" type="text" size='10' maxlength="9" style="margin:1px 4px;" onfocus="ryt_area_date('sys_date_begin','sys_date_end',0,60,0)" />
                        至
                    <input id="sys_date_end" size='12' name="sys_date_end" class="Wdate" type="text" size='10' maxlength="9" style="margin:1px 4px;" disabled="disabled" /><font color="red" style="padding-left:4px;">*</font>
                
                &nbsp;&nbsp;
                
                电银流水号： &nbsp;<input type="text"  id="tseq"  name="tseq" class="input"/>
                &nbsp;&nbsp;
                 <input type="button" value= "查 询" name="submitQuery" onclick="queryRisklog();" class="button" />
                </td>
            </tr>
        </table>
       
    <table id= "resultList" class="tablelist tablelist2" style="display:none;">
           <thead>
             <tr>
              <th>选择</th>
              <th>电银流水号</th>
              <th>风险状态</th>
              <th>录入日期</th>
              <th>风险金额</th>
              <th>冻结原因</th>
              <th>确认/解冻原因</th>            
           </tr>
           </thead>
           <tbody id="resultListTB"></tbody>
       </table>
           
        <table id="sub_but_id" style="display: none;" >
            <tr><td height="20px">
				<input type="button" value="风险确认" onclick="editRiskLog('c')" class="button"/>
                 </td>
                <td height="20px"> 
                <input type="button" value="解冻处理" onclick="editRiskLog('s')" class="button"/> 
                </td>
            </tr>
        </table>
        <table id="logDetail"  class="tableBorder"  style="display: none;padding-top: 2px;" >
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
       
        <table id="editRiskRemarksTB_C" style="display: none" class="tableBorder" >
            <tr>
                <td valign="top" align="right">备注:&nbsp;&nbsp; 
                </td>
                <td><textarea rows="5" cols="40" id="edit_remarks_c"></textarea><font color="red" style="padding-left:4px;">*</font></td>
            </tr>
            <tr>
                <td valign="top">&nbsp;&nbsp; <input type="hidden" id="select_edit_c_id"/></td>
                <td height="30px"><input type="button" value="确认风险交易确认" onclick="editRiskLogC()"/>&nbsp;&nbsp;&nbsp;<input type="button" value="返回" class="button" onclick="goBack('C')"/></td>
            </tr>
        </table>
        
        <table id="editRiskRemarksTB_S" style="display: none" class="tableBorder">
            <tr><td valign="top" height="20px" align="right">解冻金额：<br /></td><td valign="top"> 
            <input type="text" value="" id="edit_remarks_s_amount" maxlength="9"/><font color="red" style="padding-left:4px;">*</font>必须为含有两的位小数：如200.00<br /></td></tr>
            <tr>
                <td valign="top" align="right">备注:&nbsp;&nbsp; 
                <br /></td>
                <td><textarea rows="4" cols="40" id="edit_remarks_s"></textarea><font color="red" style="padding-left:4px;">*</font><br /></td>
            </tr>
            <tr>
                <td valign="top">&nbsp;&nbsp; <input type="hidden" id="select_edit_s_id"/> <br /></td>
                <td height="25px"><input type="button" value="  确认风险交易解冻 " onclick="editRiskLogS()"/>&nbsp;&nbsp; <input type="button" value="返回" class="button" onclick="goBack('S')"/><br /></td>
            </tr>
        </table>
        <br />
      </div>
    </body>
</html>
