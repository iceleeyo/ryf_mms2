<%@ page language="java" pageEncoding="UTF-8" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    
    <title>交易明细查询</title>
     <%
		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);
		int rand = new java.util.Random().nextInt(10000);
		%>
	<meta http-equiv="pragma" content="no-cache"/>
	<meta http-equiv="cache-control" content="no-cache"/>
	<meta http-equiv="expires" content="0"/>    
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3"/>
	<meta http-equiv="description" content="This is my page"/>
	<link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="../../dwr/engine.js?<%=rand %>"></script>
    <script type="text/javascript" src="../../dwr/util.js?<%=rand %>"></script>
    <script type="text/javascript" src="../../dwr/interface/PageService.js?<%=rand%>"></script>
    <script type="text/javascript" src='../../dwr/interface/CompanyService.js?<%=rand%>'></script> 
    <script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
    <script type="text/javascript" src="../../public/js/company/jymxcx.js?<%=rand%>"></script>
 	<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
  </head>
  
  <body>
  <div class="style">
    <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="6">&nbsp;&nbsp;&nbsp;&nbsp;企业支付交易查询&nbsp;&nbsp;
            </td></tr>
            
            <tr>
                <td class="th1" align="right" width="15%">付款方企业全称：</td>
                <td align="left" width="35%">
               		<input type="text" id="acc_name" name="acc_name" disabled="disabled" size="20" maxlength="11" value="${SESSION_LOGGED_ON_CUS_NAME}"/>
                不正确？<a href="<%=path%>/cus_login.jsp"><b>请重新登录</b></a>
               
                
                </td>
                 
                <td class="th1" bgcolor="#D9DFEE" align="right" width="15%">订单号：</td>
                <td align="left" width="35%">
               <input type="text" id="oid" name="oid" size="20"  maxlength="20"/>
                </td>
            </tr>
            <tr>
            <td colspan="6" align="center" style="height: 30px">
                <input class="button" type="button" value = " 查 询 " onclick="queryMX();" />
            </td>
            </tr>
        </table>
        <table class="tableBorder" id="xiangxi" style="display:none ;">
				<tr><td class="title" colspan="6">&nbsp;&nbsp;&nbsp;&nbsp;订单详细资料</td></tr>
				<tr>
                    <td class="th1" align="right" width="11%">&nbsp;我方企业全称：</td>
                    <td width="22%" align="left" id="a_acc_name" ></td>
                    <td class="th1" align="right" width="11%">&nbsp;订单号：</td>
                    <td width="22%" align="left" id="a_oid" ></td>
                    <td class="th1" align="right" width="11%">&nbsp;订单日期：</td>
                    <td width="22%" align="left" id="a_init_time" ></td>
                </tr>
                <tr>
                    
                    <td class="th1" align="right" >&nbsp;电银流水号：</td>
                    <td  align="left" id="a_tseq" ></td>
                    <td class="th1" align="right" >&nbsp;订单金额：</td>
                    <td  align="left" id="a_trans_amt" ></td>
                    <td class="th1" align="right" >&nbsp;系统手续费：</td>
                    <td  align="left" id="a_trans_fee" ></td>
                </tr>
                <tr>
                    <td class="th1" align="right" >&nbsp;交易类型：</td>
                    <td  align="left" id="a_ptype" ></td>
                    <td class="th1" align="right" >&nbsp;订单状态：</td>
                    <td  align="left" id="a_state" ></td>
                     <td class="th1" align="right" >&nbsp;对方企业客户名称：</td>
                    <td  align="left" id="a_to_acc_name" ></td>
                </tr>
                <tr>
                    <td class="th1" align="right" >&nbsp;对方收款账号：</td>
                    <td  align="left" id="a_to_acc_no" ></td>
                    <td class="th1" align="right" >通知付款方手机号：</td>
                    <td  align="left" id="a_sms_mobiles" ></td>
                    <td class="th1" align="right" ></td>
                    <td  align="left" id="" ></td>
                </tr>
                <tr >
                   <td class="th1" align="right" >&nbsp;订单描述：</td>
                    <td  colspan="6" align="left" id="a_remark" ></td>
                </tr>
		</table>
		
		<table id="showTable"  class="tableBorder" style="display:none; margin-top: 40px">
             <tr  >
               <td align="left" class="th1" width="10%" style="font-weight: bold;">商户号：</td>
               <td align="left"  width="15%" id="v_mid"> </td>
               <td align="left" class="th1" width="10%" style="font-weight: bold;">商户订单号： </td>
               <td align="left"  width="15%" id="v_oid" > </td>
               <td align="left" class="th1" width="10%" style="font-weight: bold;">商户日期：</td>
               <td align="left"  width="15%" id="v_mdate"> </td>
             </tr> 
             <tr  >
             <td align="left" class="th1" style="font-weight: bold;">电银流水号：</td>
               <td align="left" id="v_tseq" > </td>
               <td align="left" class="th1" style="font-weight: bold;">交易金额：</td>
               <td align="left" id="v_amount" > </td>
               <td align="left" class="th1" style="font-weight: bold;">系统手续费：</td>
               <td align="left" id="v_fee_amt" > </td>
               
             </tr>
             <tr >
               <td align="left" class="th1" style="font-weight: bold;">交易类型：</td>
               <td align="left" id="v_type" > </td>
                <td align="left"  class="th1"style="font-weight: bold;">交易状态：</td>
               <td align="left" id="v_tstat" > </td>
               <td align="left" class="th1" style="font-weight: bold;">支付网关：</td>
               <td align="left" id="v_gateName" > </td>
               
             </tr>
             <tr>
               <td align="left" class="th1" style="font-weight: bold;">初始的系统日期：</td>
               <td align="left" id="v_init_sys_time" > </td>
               <td align="left" class="th1" style="font-weight: bold;">系统日期：</td>
               <td align="left" id="v_sys_date" > </td>
               <td align="left" class="th1" style="font-weight: bold;">清算批次号：</td>
               <td align="left" id="v_batch" > </td>
             </tr>
             <tr >
               <td align="left" class="th1" style="font-weight: bold;">银行应答标志：</td>
               <td align="left" id="v_bk_flag" > </td>
               <td align="left" class="th1" style="font-weight: bold;">总申请退款金额：</td>
               <td align="left" id="v_refund_amt" > </td>
               <td align="left" class="th1"  style="font-weight: bold;">发送银行时间:</td>
               <td align="left" id="v_bk_send" > </td>
             </tr>
             <tr >  
               <td align="left" class="th1" style="font-weight: bold;">银行响应时间:</td>
               <td align="left" id="v_bk_recv" > </td>
               <td align="left" class="th1" style="font-weight: bold;">银行对帐标志：</td>
               <td align="left" id="v_bk_chk" ></td>
               <td align="left" class="th1" style="font-weight: bold;">银行日期：</td>
               <td align="left" id="v_bk_date" ></td>
             </tr>
             <tr >  
               <td align="left" class="th1" style="font-weight: bold;">银行流水1：</td>
               <td align="left" id="v_bk_seq1" ></td>
               <td align="left" class="th1" style="font-weight: bold;">银行流水2：</td>
               <td align="left" id="v_bk_seq2" ></td>
               <td align="left" class="th1" style="font-weight: bold;">银行返回代码：</td>
               <td align="left" id="v_bk_resp" ></td>
             </tr>
           </table>
        </div>
  </body>
</html>
