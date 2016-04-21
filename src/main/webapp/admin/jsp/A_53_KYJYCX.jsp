<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
        <title>可疑交易查询</title>
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
		<script type='text/javascript' src='../../dwr/engine.js'></script>
		<script type='text/javascript' src='../../dwr/util.js'></script>
		<script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../dwr/interface/RiskmanageService.js?<%=rand%>'></script>
		<script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
		<script type="text/javascript" src='../../public/js/ryt.js?<%=rand%>'></script>
      
		<script type="text/javascript" src="../../public/datePicker/WdatePicker.js?<%=rand%>"></script>
		<script type='text/javascript'  src='../../public/js/riskmanage/search_risk.js?<%=rand%>'></script>
</head>
    <body onload="initAdminQuery()">
    
     <div class="style">
     <table class="tableBorder" width="100%" >
            <tr>
                <td class="title" colspan="2" >&nbsp;&nbsp;可疑交易查询</td>
            </tr>
            <tbody id="chaxun">
            <tr>
               <td class="th1" align="right" width="30%">订单状态： </td>
               <td align="left">
                <select id="transtate" name="transtate">
                 <option value="2" >支付成功</option>
                 <option value="3" >支付失败</option>
                </select>                
               </td>
               </tr>
               <tr>
               <td  class="th1" align="right" width="30%" >交易类型：</td>
					<td align="left"><select id="tradetype" name="tradetype">
							<option value="0">全部....</option>
							<option value="3">信用卡支付</option>
							<option value="11">对私代付</option>
							<option value="12">对公代付</option>
							<option value="15">对私代扣</option>
					</select>
					</td>
				</tr>
               <tr> 
                <td class="th1" align="right" >交易日期：</td>
                <td align="left">
                <!-- 
                <input type="text" name="begindate" id="begindate" class="Wdate" onfocus="WdatePicker({skin:'ext',minDate:'1998-12-20',maxDate:'%y-%M-{%d-1}',dateFmt:'yyyyMMdd',readOnly:'true'});"/>
                &nbsp;至&nbsp;
                <input type="text" name="enddate" id="enddate" class="Wdate"  onfocus="WdatePicker({skin:'ext',minDate:'#F{$dp.$D(\'begindate\')}',maxDate:'%y-%M-{%d}',dateFmt:'yyyyMMdd',readOnly:'true'});"/>
                 -->
                    <input id="begindate" size="15px"
                    name="begindate" class="Wdate" type="text" onfocus="ryt_area_date('begindate','enddate',0,90,0)" />
                    &nbsp;至&nbsp;
                    <input id="enddate" size="15px" name="enddate" class="Wdate" type="text" disabled="disabled" /><font color="red">*</font>
                </td>            
            </tr>
            <tr>
             <td class="th1" align="right" >其他条件：</td>
             <td align="left" >
              <select  id="other_id" name="other_id">
                <option value="all" >不做为查询条件...</option>
                <option value="card" >同一卡号支付超过 </option>
                <option value="id" >同一证件号支付超过 </option>
                <option value="phone" >同一手机号支付超过 </option>
              </select>
             <input type="text" id="other_id_num" name="other_id_num" value="3" size="3" maxlength="3"/>次。
             <input type="checkbox" id="has_num" name="left_card" value = "-1"  />
                                单笔金额超过
                <input type="text" id="amount_num" name="amount_num" value="5000" size="8" maxlength="8"/>元。
              &nbsp;&nbsp;
              <!-- 
                <input type="checkbox" id="has_card" name="left_card" value = "-1" onchange="choose('left_card');" >
                                卡号前
                <input type="text" id="left_card_num" name="left_card_num" value="8" size="2" maxlength="2">位相同,支付超过       
                <input type="text" id="left_card_times" name="left_card_times" value="3" size="3" maxlength="3">笔。        
                 --></td>
            </tr>
            <tr>
                <td align="center" width="90%" height="30px" colspan="2">
                <input id="checksubmit" name="checksubmit" type="button" value="查 询 " onclick="searchRiskLog(1);" class="button"/>
                
                &nbsp;&nbsp;&nbsp;&nbsp;
                 <input id="downsubmit" name="checksubmit" disabled="disabled" type="button" class="button" value="下载 XLS" onclick="downData();"/>
               
                </td>
            </tr>
        </tbody>
    </table>
      
    <table class="tablelist tablelist2" id="resultList" style="display: none;">
		<thead>
	     <tr>
	     <th>电银流水号</th><th>商户号</th><th>商户简称</th>
	     <th>交易金额(元)</th><th>交易日期</th><th>卡号</th><th>交易类型</th><th>证件号</th><th>手机号</th><th>交易状态</th></tr></thead>
	     <tbody id="mlogBody"></tbody>
    </table>
           <table id="logDetail"  class="tablelist tablelist2"  style="display: none">
           <tr><td class="title" colspan="6" align="center">商户订单详细资料</td></tr>
           <tr >
               <td align="left" class="th1" >商户日期：</td>
               <td align="left" width="15%" id="v_mdate"></td>
               <td align="left" class="th1" >商户号：</td>
               <td align="left" width="15%" id="v_mid"></td>
               <td align="left" class="th1" >商户简称：</td>
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
           
            <tr >
               <td align="center"  colspan="6"><input type="button" value=" 返回  " onclick="back()" class="button"/></td>
              
           </tr>
        </table></div>
</body>
</html>
