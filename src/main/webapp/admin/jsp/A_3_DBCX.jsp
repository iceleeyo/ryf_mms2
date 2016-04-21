<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
<%
response.setHeader("Pragma","No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", 0);
int rand = new java.util.Random().nextInt(10000);
%>
        <title>单笔查询</title>
        <meta http-equiv="pragma" content="no-cache"/>
        <meta http-equiv="cache-control" content="no-cache"/>
        <meta http-equiv="Expires" content="0"/>  
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css"/>
        <script type='text/javascript'  src='../../dwr/engine.js'></script>
        <script type='text/javascript'  src='../../dwr/util.js'></script>
        <script type="text/javascript" src="../../dwr/interface/PageService.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../dwr/interface/TransactionService.js?<%=rand%>"></script>
        <script type='text/javascript' src='../../public/datePicker/WdatePicker.js'></script>
 		<script type="text/javascript" src='../../public/js/transaction/admin_querySpecialDeal.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>


</head>
<body onload="initOption();">

 <div class="style">
 <table width="100%"  align="left"  class="tableBorder">
    <tr><td class="title" colspan="4">&nbsp;&nbsp;&nbsp;&nbsp;单笔查询</td></tr>
      <tr>
        <td width="100%" bgcolor="#C0C9E0"  height="25px" colspan="4">
        &nbsp;&nbsp;&nbsp;&nbsp;
        <input type="radio" name="choose" id="systemId" value="systemId"  checked="checked"  onclick=" changeSearch('bytseq');"/>电银流水号
         &nbsp;&nbsp;
        <input type="radio" name="choose" id="codeId" value="codeId" onclick=" changeSearch('meroid');" />商户订单号
          &nbsp;&nbsp;
        <input type="radio" name="choose" id="bkseqId" value="bkseqId"  onclick="changeSearch('bybkseq');"/>银行流水号
        </td>
    </tr>
  </table>
  
  
  <div id="meroid" style="display:none;" >
      <table class="tableBorder" id="topMenuTable">
         <tr>
            <td class="th1" width="100px" align="right" height="30px">商户号： </td>
            <td align="left" width="400px">
               <input type="text" id="mid" name="mid"  size="8px" style="width: 150px" onkeyup="checkMidInput(this);"/>
                <!--<select style="width: 150px" id="smid" name="smid" onchange="initMidInput(this.value)">
                     <option value="">全部...</option>
                   </select> --> 
                  <font color="red">*必填项</font>
                 </td>
            <td class="th1" width="100px" align="right" height="30px">商户订单号： </td>
            <td width="20%">
               <input type="text" name="oid" id="oid" size="20" /><font color="red">*必填项</font>
            </td>
            <td class="th1" width="100px"  align="right" height="30px">商户日期：</td>
            <td >
                 <input id="c_mdate" name="mdate" class="Wdate"  onfocus="WdatePicker({skin:'ext',minDate:'1998-12-20',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});"/>
             </td>
          </tr>
          
          <tr>
            <td colspan="6" align="left" height="30px"> 
            <input type="button" style="margin-left: 200px;width: 100px" value="查  询" onclick=" queryOne('meroid'); "/></td>
         </tr> 
      </table>
   </div> 
   <div id="bytseq" >
        <table  class="tableBorder" id="topMenuTable">
          <tr>
             <td class="th1" align="right" width="20%" height="30px">电银流水号：</td>
             <td><input type="text" name="tseq" id="tseq"  /><font color="red">*必填项</font>
             <input type="button"  value="查  询" onclick=" queryOne('bytseq'); " class="button" />
             </td>
          </tr>
        </table>
   </div>
   
    <div id="bybkseq" style="display:none;" >
        <table  class="tableBorder" id="topMenuTable">
          <tr>
             <td class="th1" align="right" width="100px" height="30px">银行流水号：</td>
             <td width="200px"><input type="text" name="bktseq" id="bktseq"/> <font color="red">*必填项</font></td>
              <td class="th1" align="right" width="100px" height="30px">支付渠道：</td>
             <td width="200px">
<select id="gateRouteId" name="gateRouteId"><option value=""> 请选择... </option>
			    </select> <font color="red">*必填项</font>
</td> 
			    <td>
             <input type="button"  value="查  询" onclick=" queryOne('bybkseq'); " style="margin-left:  10px;width: 100px;"/>
             </td>
          </tr>
        </table>
   </div>

     <table id="showTable"  class="tableBorder" style="display: none; margin-top: 10px"><!-- display: none; -->
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
               <td align="left" class="th1" >银行手续费：</td>
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
        </table>
              <table  class="tableBorder" id="footMenuTable" style="display:none;">
		          <tr>
		             <td class="th1" align="right" width="20%" height="30px">更改历史订单支付渠道：</td>
			           <td align="left" width="15%">
		                   <select style="width: 150px" id="gateRouteId2" name="gateRouteId">
		                    </select>
	                    </td>
	                    <td>
	    &nbsp;&nbsp;<input type="button"  value="确    认" onclick="submitChangePayChannel();" class="button" />
	                  </td>
		          </tr>
              </table>
            
          
          
          
  </div>
  
  
  
  
</body>
</html>
