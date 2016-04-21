<%@ page language="java" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>退款运行经办</title>
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
        <link href="../../public/css/head.css?<%=rand %>" rel="stylesheet" type="text/css"/>
		<script type='text/javascript' src='../../dwr/engine.js'></script>
		<script type='text/javascript' src='../../dwr/util.js?<%=rand %>'></script>
		<script type='text/javascript' src='../../dwr/interface/PageService.js?<%=rand %>'></script>
		<script type='text/javascript' src='../../dwr/interface/RefundmentService.js?<%=rand %>'></script>
		<script type="text/javascript" src='../../dwr/interface/DownloadFileService.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../dwr/interface/DoSettlementService.js?<%=rand %>'></script>
		<script type="text/javascript" src="../../public/js/refundment/admin_motion_handle.js?<%=rand%>"></script>
		<script type='text/javascript' src='../../public/datePicker/WdatePicker.js'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand %>'></script>
       
		<script type='text/javascript'>
		function checkBkNo(){
            DoSettlementService.checkBkNo(function(msg){alert(msg);});
        }
        </script>
</head>
<body onload="init()">
<div class="style">
<input value="${sessionScope.SESSION_LOGGED_ON_USER.operName}" name="operName" id="operName" type="hidden"/>
   <!-- <input type="button" value = "退还手续费" name="submitQuery" class="button" onclick="refundFeeAmt();" style="width:100px;height:28px;"/> -->
    <table class="tableBorder"  id="queryPanelTable" >
    <tr><td class="title" colspan="6"> &nbsp;&nbsp;&nbsp;&nbsp;退款经办 </td> </tr>
    <tr>
     <td class="th1" align="right" width="20%">&nbsp;商户号：</td>
     <td align="left"  colspan="5">
            <input type="text" id="mid" name="mid"   style="width: 150px;" onkeyup="checkMidInput(this);"/>
            <!--  <select style="width: 145px;" id="smid" name="smid" onchange="initMidInput(this.value)">
               <option value="">全部...</option>
            </select> --> 
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="下  载"  name="downloadXlsFile"  onclick="queryRefundMotions(-1);" class="button"/><span style="color:red">（根据商户号下载 “当天经办成功”的记录）</span>
       </td>
  
      </tr>
      <tr>
          <td class="th1" align="right" width="10%">&nbsp;退款状态：</td>
         <td align="left"><select id="stat"><option value="1">商户已提交</option><option value="2">退款完成</option></select></td>
         <td class="th1" align="right" width="10%">&nbsp;退款确认日期：</td>
         <td align="left" >
              <input id="date_begin" name="date_begin" class="Wdate" type="text" onfocus="ryt_area_date('date_begin','date_end',0,30,0)" />
                                          至<input id="date_end" name="date_end"  class="Wdate" type="text" disabled="disabled" />
         </td>
        <td class="th1" bgcolor="#D9DFEE" align="right" width="11%">商户状态：</td>
                <td align="left" width="20%">
                <select style="width: 150px" id="mstate" name="mstate">
                    <option value="">全部...</option>
                </select>
                </td>
      </tr>
      <tr align="center"><td colspan="6" height="30px;"> 
      <input type="button" value = "查 询" name="submitQuery" class="button" onclick="queryRefundMotions(1);"/>&nbsp;&nbsp;&nbsp;&nbsp;

       <!-- 
       <input type="button"  style="height: 24px;width: 100px;margin-left: 30px" value="检查" onclick="checkBkNo()"/>
    <font color="red">&nbsp;&nbsp;&nbsp;*</font>
    备付金管理上线后，会产生商户流水记录，点击检查按钮，系统检查相关配置是否完成
         -->
      
      <input type="hidden" value = "${sessionScope.SESSION_LOGGED_ON_USER.mid}" name="loginMid" id="loginMid" />
      <input type="hidden" value = "${sessionScope.SESSION_LOGGED_ON_USER.operId}" name="loginUid" id="loginUid" />
      </td></tr>
      
      
	</table>
     <form name="verifyRefundForm" id="verifyRefundForm" onsubmit=" checkSubmit();" method="post">
     <table width="100%"  class="tablelist tablelist2" id="detailResultList" style="display: none;">
     <thead><tr valign="middle" class="title2">
            <th align="center"  >选择</th>
            <th>原电银流水号</th>
            <th>商户号</th>
            <th>商户简称</th>
            <th>原商户订单号</th>
            <th>原支付渠道</th>
            <th>银行</th>
            <th>退款金额</th>
            <th>退回商户手续费</th>
            <th>退款状态</th> 
            <th>系统日期</th>
             <th>退款确认日期</th>
            <th>申请退款原因</th>
            <th  >撤销退款的原因</th>
        </tr></thead>
        <tbody id="resultList" ></tbody>   
     </table>
     <!-- 
      <table width="100%"  class="tableborder" id="detail4One" style="display: none;">
        <tr>
                <td  colspan="16" height="8px" style="font-weight:bolder" class="title">&nbsp;&nbsp;&nbsp;&nbsp;退款订单信息详情</td>
            </tr>
            <tr >
                <td class="th1" align="right" width="10%"  >&nbsp;商户号：</td>
                <td width="20%" align="left" id="v_mid"></td>
                <td class="th1" align="right" width="10%"> &nbsp;商户简称：</td>
                <td width="20%" align="left" id="v_name"></td>
                <td class="th1" align="right" >&nbsp;原电银流水号：</td>
                <td  align="left" id="v_tseq"  > </td>
            </tr>
            <tr>
                <td class="th1" align="right" >&nbsp;原订单号：</td>
                <td  align="left" id="v_org_oid" > </td>
                <td class="th1" align="right" >&nbsp;退款单号：</td>
                <td  align="left" id="v_oid"  > </td>
                <td class="th1" align="right"  >&nbsp;原商户日期：</td>
                <td   align="left" id="v_org_mdate" > </td>
            </tr>
            <tr>
                <td class="th1" align="right"  > &nbsp;原交易金额：</td> 
                <td align="left" id="v_amount" > </td>
                <td class="th1" align="right"  >&nbsp;原系统手续费：</td>
                <td align="left" id="v_fee_amt" > </td>
                <td class="th1" align="right" >&nbsp;银行：</td>
                <td  align="left" id="v_gate" > </td>
            </tr>
            <tr>
                <td class="th1" align="right" width="10%">&nbsp;退款流水号：</td>
                <td width="20%" align="left" id="v_id"  > </td>
                <td class="th1" align="right" width="10%">&nbsp;结算批次号：</td>
                <td width="20%" align="left" id="v_batch"  > </td>
                <td class="th1" align="right"  >&nbsp;本次退款金额：</td>
                <td align="left" id="v_ref_amt" > </td>
            </tr>
            <tr>
                <td class="th1" align="right"  >&nbsp;总申请退款金额：</td>
                <td align="left" id="v_refund_amt" > </td>
                <td class="th1" align="right"  >&nbsp;已退款总金额：</td>
                <td align="left" id="v_ref_amt_sum" > </td>
                <td class="th1" align="right"  >&nbsp;原系统时间：</td>
                <td align="left" id="v_sys_date" > </td>
            </tr>
            <tr>
                <td class="th1" align="right"  >&nbsp;退款申请日期：</td>
                <td align="left" id="v_mdate" > </td>
                <td class="th1" align="right"  >&nbsp;退款确认日期：</td>
                <td align="left" id="v_req_date" > </td>
                <td class="th1" align="right"  >&nbsp;退款经办日期：</td>
                <td   align="left" id="v_pro_date" > </td>
            </tr>
            <tr>
                <td class="th1" align="right"  >&nbsp;退款审核日期：</td>
                <td   align="left" id="v_ref_date" > </td>
                <td class="th1" align="right"  >&nbsp;退款状态：</td>
                <td   align="left" id="v_stat"  > </td>
                <td   align="left"  > </td>
                <td   align="left"   > </td>
            </tr>
            <tr>
                <td class="th1" align="right"  >&nbsp;退款申请原因：</td>
                <td   align="left" id="v_refund_reason" colspan="5" > </td>
            </tr>
            <tr>
                <td class="th1" align="right"  >&nbsp;退款撤销原因：</td>
                <td   align="left" id="v_etro_reason" colspan="5" > </td>
            </tr>

            <tr>
                <td class="th1" align="right"  >&nbsp; 操作失败原因：</td>
                <td   align="left" id="v_reason" colspan="5" > </td>
            </tr>
            <tr>
                <td colspan="6" align="center">
                    <input type="button" value="返回" class="button" onclick="view();">
                </td>
            </tr>
        
        
        </tbody>   
     </table> -->
    </form>
            
</div>
</body>
</html>
