<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>失败交易备份查询</title>
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
        <link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css"/>
        <script type='text/javascript' src='../../dwr/engine.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../dwr/util.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/TransactionService.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>' ></script>
		<script type="text/javascript" src='../../public/js/transaction/admin_transactionForQueryDetail.js?<%=rand%>'></script> 

    </head>
    <body onload="blogInit();">
    
     <div class="style">
        <form name="MERHLOG"  method="post" action="">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="6">&nbsp;&nbsp;&nbsp;&nbsp;失败交易备份查询&nbsp;&nbsp;(<font color="red">*</font>银行名称M:信用卡支付,W:wap支付,C:充值卡支付,I:语音/快捷支付,其余:网银支付)</td></tr>
            <tr>
            
            <td class="th1" align="right" width="10%">商户号：</td>
                <td align="left" width="25%">
                <input type="text" id="mid" name="mid"  style="width:150px" onkeyup="checkMidInput(this);"/>
               <!-- <select style="width: 140px" id="smid" name="smid" onchange="initMidInput(this.value)">
                     <option value="">全部...</option>
                   </select> -->
                 </td>
             <td class="th1"  align="right" width="10%">
                     <select style="width: 80px" id="date" name="date">
                        <option value="sys_date">系统日期</option>
                        <option value="mdate">商户日期</option>
                    </select>
                
                </td>
                <td align="left" width="32%">
                    <input id="bdate" size="15px"
                    name="bdate" class="Wdate" type="text" onfocus="ryt_area_date('bdate','edate',0,30,0)" />
                    &nbsp;至&nbsp;
                    <input id="edate" size="15px" name="edate" class="Wdate" type="text" disabled="disabled" /><font color="red">*</font>
                </td>
                <td class="th1"  align="right" width="10%" >交易类型：</td>
                <td align="left" width="*%" >
                    <select style="width: 150px" id="type" name="type">
                        <option value="">全部...</option>
                    </select>
                </td>
            </tr>
            <tr>
             <td class="th1"  align="right" > 商户订单号：</td>
                <td align="left" >
                <input type="text" name="oid" id="oid"/>
                </td>
              <td class="th1" bgcolor="#D9DFEE" align="right" >电银流水号：</td>
                <td align="left"  >
                <input type="text" name="tseq" id="tseq"/>
                </td>
              <td class="th1" bgcolor="#D9DFEE" align="right">商户状态：</td>
                <td align="left" >
                <select style="width: 150px" id="mstate" name="mstate">
                    <option value="">全部...</option>
                </select></td>
                </tr>
                
            <tr>
            <td colspan="6" align="center" style="height: 30px">
                <input type="hidden"  name="queryType" id="queryType" value="MERHLOG" />
                <input class="button" type="button" value = " 查 询 " onclick="queryMerBlog(1);" />&nbsp; 
                <!-- 
                <input style="width: 90px;height: 25px;margin-right: 10px" type="button" value = " 下载TXT "  onclick="query('txt', '');"/>
                <input class="button" type="button" value= "下载XLS " onclick = "downloadDetail();"/>
                 -->
            </td>
            </tr>
        </table>
       </form>
    
       <table  class="tablelist tablelist2" id="merHlogTable" style="display: none;">
           <thead>
           <tr>
             <th>电银流水号</th><th>商户号</th><th>商户简称</th>
             <th>商户订单号</th><th>商户日期</th>
             <th>交易金额(元)</th><th>交易状态</th><!-- <th>对账状态</th> -->
             <th>交易类型</th><th>交易银行</th><th>支付渠道</th>
             <th>系统手续费(元)</th>
             <th>系统时间</th><th>银行流水号</th><th sort="false">失败原因</th>
           </tr>
           </thead>
           <tbody id="resultList"></tbody>
       </table>
		
		  <table id="hlogDetail"   class="tableBorder detailBox" style="display: none;">
			<tbody>
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
				<tr  >
					<td align="left" class="th1" >初始的系统日期：</td>
					<td align="left" id="v_init_sys_time"></td>
					<td align="left" class="th1" >电银流水号：</td>
					<td align="left" id="v_tseq"></td>
					<td align="left" class="th1" >清算批次号：</td>
					<td align="left" id="v_batch"></td>
				</tr>
				<tr  >
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
				<tr  >
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
					<td align="left" class="th1" >银行返回代码：</td>
					<td align="left" id="v_bk_resp">&nbsp;</td>
					<td align="left" class="th1" >失败原因：</td>
					<td align="left" id="v_error_msg">&nbsp;</td>
					<td align="left" class="th1" >&nbsp;</td>
					<td align="left" id="">&nbsp;</td>
				</tr>
				<tr>
					<td colspan="6" height="30px" align="center">
					<input type="button" class="wBox_close button" value=" 返回  " /><br/></td>
				</tr>
			</tbody>
		</table>
        </div>
	</body>
</html>
