<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<table id="hlogDetail"  class="tableBorder detailBox" style="display: none;">
          <!--  <tr><td class="title" colspan="6" align="center">商户订单详细资料</td></tr> -->
           <tr>
               <td align="left" class="th1" width="17%" >商户日期：</td>
               <td align="left" width="17%" id="v_mdate"></td>
               <td align="left" class="th1" width="17%" >商户号：</td>
               <td align="left" width="17%" id="v_mid"></td>
               <td align="left" class="th1" width="17%">商户简称：</td>
               <td align="left" width="" id="v_midName"></td>
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
               <td align="left" id="v_bk_resp"></td>
               <td align="left" class="th1" >版本号：</td>
               <td align="left" id="v_version"></td>
               <td align="left" class="th1" >用户的Ip地址:</td>
               <td align="left" id="v_ip"></td>
           </tr>
           <tr >
               <td align="left" class="th1">买方id(代理商id)：</td>
               <td align="left" id="v_bid"></td>
               <td align="left" class="th1" >实际交易金额：</td>
               <td align="left" id="v_pay_amt"></td>
               <td align="left" class="th1" >原始交易数据：</td>
               <td align="left" id="v_org_seq"></td> 
           </tr>
             
            <tr >
               <td align="left" class="th1">退款交易数据：</td>
               <td align="left" id="v_ref_seq"></td>
               <td align="left" class="th1" >商户的私有数据项：</td>
               <td align="left" id="v_mer_priv"></td>
               <td align="left" class="th1" >支付渠道</td>
               <td align="left" id="v_gid"></td>
                
           </tr>
            <tr >
               <td align="left" class="th1" >手机号：</td>
               <td align="left" id="v_mobile_no"></td>
               <td align="left" class="th1" >有效期：</td>
               <td align="left" id="v_trans_period"></td>
               <td align="left" class="th1" >错误代码</td>
               <td align="left" id="v_error_code"></td>
           </tr>
            <tr >
               <td align="left" class="th1">记录支付所用的银行卡号：</td>
               <td align="left" id="v_card_no"></td>
                <td align="left" class="th1">支付的手机号:</td>
               <td align="left" id="v_phone_no"></td>
               <td align="left" class="th1" >操作员号：</td>
               <td align="left" id="v_oper_id"></td>
           </tr>
            <tr >
               <td align="left" class="th1">差价：</td>
               <td align="left" id="v_pre_amt"></td>
               <td align="left" class="th1" >银行手续费公式：</td>
               <td align="left" id="v_bk_fee_model"></td>
               <td align="left" class="th1" >优惠金额：</td>
               <td align="left" id="v_pre_amt1"></td>
           </tr>
           <tr>
               <td align="left" class="th1">分期期数：</td>
               <td align="left" id="v_pro_num"></td>
               <td align="left" class="th1" >交易计划：</td>
               <td align="left" id="v_trade_plan"></td>
               <td align="left" class="th1" >首付金额：</td>
               <td align="left" id="v__down_payamt"></td>
           </tr>
           <tr>
               <td align="left" class="th1">每期付款金额：</td>
               <td align="left" id="v_each_pay_num"></td>
               <td align="left" class="th1" >分期手续费：</td>
               <td align="left" id="v_pro_poundage"></td>
               <td align="left" class="th1" >授权号：</td>
               <td align="left" id="v__Grant_Number"></td>
           </tr>
		<tr>
			<td align="left" class="th1">错误信息描述：</td>
			<td align="left" id="v_err_msg" colspan="5"></td>
		</tr>
		<tr >
               <td align="left" class="th1">后台返回地址：</td>
               <td align="left" id="v_bk_url" colspan="5"></td>
           </tr>
           <tr >
               <td align="left" class="th1">页面返回地址：</td>
               <td align="left" id="v_fg_url" colspan="5"></td>
           </tr>
           
           <tr><td colspan="6" height="30px"  align="center"> <input type="button"  value = "返回" class="wBox_close button"/>
			<!-- <input type="button" onclick="fanhui()" value = "返  回" class="button"/>--><br/></td></tr>
        </table>
</body>
</html>