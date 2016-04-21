<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>单笔查询</title>
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
        <script type='text/javascript'	src='../../dwr/engine.js'></script>
        <script type='text/javascript'	src='../../dwr/util.js'></script>
        <script type="text/javascript" src="../../dwr/interface/PageService.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../dwr/interface/MerTransactionService.js?<%=rand%>"></script>
		<script type='text/javascript' src='../../public/datePicker/WdatePicker.js'></script>
		<script type='text/javascript' src='../../public/js/ryt.js'></script> 
 		<script type="text/javascript" src='../../public/js/transaction/mer_jsp_querySpecialDeal.js?<%=rand%>'></script>
</head>
<body >

 <div class="style">
 <table width="100%"  align="left"  class="tableBorder">
    <tr><td class="title" colspan="4">&nbsp;&nbsp;&nbsp;&nbsp;单笔查询</td></tr>
      <tr>
        <td width="100%" bgcolor="#C0C9E0"  height="25px" colspan="4">
        &nbsp;&nbsp;&nbsp;&nbsp;
        <input type="radio" name="choose" id="codeId" value="codeId" checked="checked" onclick=" changeSearch('meroid');" />商户订单号
          &nbsp;&nbsp;
        <input type="radio" name="choose" id="systemId" value="systemId"  onclick=" changeSearch('bytseq');"/>电银流水号
        </td>
    </tr>
  </table>
  
  
  <div id="meroid" >
      <table class="tableBorder" id="topMenuTable">
         <tr>
            <td class="th1" width="100px" align="right" height="30px">商户订单号： </td>
            <td width="200px">
               <input type="text" name="oid" id="oid" size="20" maxlength="30" /><font color="red">*必填项</font>
               <input type="hidden"  id="mid"  value="${sessionScope.SESSION_LOGGED_ON_USER.mid}"/>
            </td>
            <td class="th1" width="100px" align="right" height="30px">商户日期：</td>
            <td >
                 <input id="c_mdate" name="mdate" class="Wdate"  onfocus="WdatePicker({skin:'ext',minDate:'1998-12-20',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});"/>
             </td>
          </tr>
          
          <tr>
            <td colspan="4" align="left" height="30px" style="padding-left: 100px;" >&nbsp;&nbsp; &nbsp;&nbsp; 
		<input type="button" class="button" value="查  询" onclick=" queryOne('meroid'); "/></td>
         </tr> 
      </table>
   </div> 
   <div id="bytseq" style="display:none;" >
        <table  class="tableBorder" id="topMenuTable">
          <tr>
             <td class="th1" align="right" width="150px" height="30px">电银流水号：</td>
             <td><input type="text" name="tseq" id="tseq"/><font color="red">*必填项</font>
             <input type="button"  value="查  询" onclick=" queryOne('bytseq'); " style="margin-left:  10px;width: 100px"/>
             </td>
          </tr>
        </table>
   </div>

     <table id="showTable"  class="tableBorder" style="display: none; margin-top: 10px"><!-- display: none; -->
             <tr  style="font-size:12px">
               <td align="left" class="th1" width="10%" style="font-weight: bold;">商户号：</td>
               <td align="left"  width="15%" id="v_mid"> </td>
               <td align="left" class="th1" width="10%" style="font-weight: bold;">商户订单号： </td>
               <td align="left"  width="15%" id="v_oid" > </td>
               <td align="left" class="th1" width="10%" style="font-weight: bold;">商户日期：</td>
               <td align="left"  width="15%" id="v_mdate"> </td>
             </tr> 
             <tr   style="font-size:12px">
             <td align="left" class="th1" style="font-weight: bold;">电银流水号：</td>
               <td align="left" id="v_tseq" > </td>
               <td align="left" class="th1" style="font-weight: bold;">交易金额：</td>
               <td align="left" id="v_amount" > </td>
               <td align="left" class="th1" style="font-weight: bold;">系统手续费：</td>
               <td align="left" id="v_fee_amt" > </td>
               
             </tr>
             <tr  style="font-size:12px">
               <td align="left" class="th1" style="font-weight: bold;">交易类型：</td>
               <td align="left" id="v_type" > </td>
                <td align="left"  class="th1"style="font-weight: bold;">交易状态：</td>
               <td align="left" id="v_tstat" > </td>
               <td align="left" class="th1" style="font-weight: bold;">支付网关：</td>
               <td align="left" id="v_gateName" > </td>
               
             </tr>
             <tr   style="font-size:12px">
               <td align="left" class="th1" style="font-weight: bold;">初始的系统日期：</td>
               <td align="left" id="v_init_sys_time" > </td>
               <td align="left" class="th1" style="font-weight: bold;">系统日期：</td>
               <td align="left" id="v_sys_date" > </td>
               <td align="left" class="th1" style="font-weight: bold;">清算批次号：</td>
               <td align="left" id="v_batch" > </td>
             </tr>
             <tr   style="font-size:12px">
               <td align="left" class="th1" style="font-weight: bold;">银行应答标志：</td>
               <td align="left" id="v_bk_flag" > </td>
               <td align="left" class="th1" style="font-weight: bold;">总申请退款金额：</td>
               <td align="left" id="v_refund_amt" > </td>
               <td align="left" class="th1"  style="font-weight: bold;">发送银行时间:</td>
               <td align="left" id="v_bk_send" > </td>
             </tr>
             <tr   style="font-size:12px">  
               <td align="left" class="th1" style="font-weight: bold;">银行响应时间:</td>
               <td align="left" id="v_bk_recv" > </td>
               <td align="left" class="th1" style="font-weight: bold;">银行对帐标志：</td>
               <td align="left" id="v_bk_chk" ></td>
               <td align="left" class="th1" style="font-weight: bold;">银行日期：</td>
               <td align="left" id="v_bk_date" ></td>
             </tr>
             <tr   style="font-size:12px">  
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
