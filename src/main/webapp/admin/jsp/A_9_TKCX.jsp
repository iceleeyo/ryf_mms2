<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>退款管理</title>
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
        <link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="../../dwr/engine.js?<%=rand %>"></script>
        <script type="text/javascript" src="../../dwr/util.js?<%=rand %>"></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand %>'></script>
        <script type='text/javascript' src='../../dwr/interface/QueryRefundService.js?<%=rand %>'></script>
		<script type="text/javascript" src="../../public/js/refundment/admin_refund_query.js?<%=rand%>"></script>
		<script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
</head>
<body onload="init();">
 <div class="style">
 
 <form name="MERREFUNDQUERY" id="MERREFUNDQUERY"  method="post">
     <table width="100%"  align="left"  class="tableBorder">
        <tr>
            <td class="title" colspan="6">&nbsp;&nbsp;&nbsp;&nbsp;退款查询</td>
        </tr>
     <tr>
         <td class="th1" align="right" width="10%">
         	<select id="dateState">
         		<option value="1">退款申请日期：</option>
         		<option value="2" selected="selected">退款确认日期：</option>
         		<option value="3">退款经办日期：</option>
         		<option  value="4">退款审核日期：</option></select></td>
         <td align="left" width="30%">
          <input id="bdate" size="15px"
                    name="bdate" class="Wdate" type="text" onfocus="ryt_area_date('bdate','edate',0,30,0)"/>
                    &nbsp;至&nbsp;
                    <input id="edate" size="15px" name="edate" class="Wdate" type="text" disabled="disabled" /><font color="red">*</font>
         </td>                
         
         <td class="th1" align="right"  width="7%">银行：</td>
         <td align="left" width="20%">
           <select style="width:150px" id="gate" name="gate" >
           <option value="">全部...</option>
           </select>&nbsp;
         </td>
          <td class="th1" align="right" width="10%" >退款状态：</td>
           <td align="left" width="100px" >
             <select style="width:120px" id="stat" name="stat" >
              <option value="">全部...</option>
             </select>
           </td>  
         
       </tr>
         <tr>
          <td class="th1" align="right" >商户号：</td>
              <td align="left">
              <input type="text" id="mid" name="mid"   size="8px" style="width:150px" onkeyup="checkMidInput(this);"/>
                 <!-- <select style="width: 150px" id="smid" name="smid" onchange="initMidInput(this.value)">
                     <option value="">全部...</option>
                   </select> -->
              </td>
               <td class="th1" align="right" width="10%">原支付渠道：</td>
                <td align="left">
                	<select style="width: 150px" id="gateRouteId" name="gateRouteId">
                    	<option value="">全部...</option>
                	</select>
                </td>
                <!-- 
              <td class="th1" align="right"  >原商户订单号：</td>
              <td align="left">
                  <input type="text" name="orgid" id="orgid" size="20" maxlength="20" />
              </td> 
              -->
               <td class="th1" align="right"  >原电银流水号：</td>
              <td align="left">
                  <input type="text" name="tseq" id="tseq" size="20" onkeyup="inputFigure(this)"/>
              </td>      
        </tr>
				<tr>
					<td class="th1" align="right">金额：</td>
					<td align="left"><input type="text" id="begintrantAmt"name="begintrantAmt" value="" size="15px" /> 到 &nbsp;
					<input type="text" id="endtrantAmt" name="endtrantAmt" value=""size="15px" />
					</td>
					<td class="th1" align="right">商户状态：</td>
					<td align="left"><select style="width: 150px" id="mstate"name="mstate">
							<option value="">全部...</option>
					</select>
					</td>
					<td class="th1" align="right">退款处理类型：</td>
					<td align="left"><select style="width: 150px" id="refundType" name="refundType">
							<option value="">全部...</option>
							<option value="0">人工经办</option>
							<option value="1">联机退款</option>
							<option value="2">联机人工经办</option>
					</select>
					</td>

				</tr>
				<!-- 
       <tr>
	       <td class="th1" align="right" >退款审核日期：</td>
	         <td></td>
	       <td class="th1" align="right"></td>
	        <td></td>
	       <td class="th1" align="right"></td>
	        <td></td>
       </tr>
        -->
        <tr>
           <td colspan="6"  height="30px" align="center">
           <input type="hidden"  name="queryType" id="queryType" value="MERREFUNDQUERY" />
           <input type="button" class="button" value="查  询" onclick="queryVerifyRefund(1);"/>
           <input type="button" class="button" value="下载XLS" onclick="downloadReturn();"/>
           </td>
         </tr>
         
         
         </table>
         </form>
          <table  class="tablelist tablelist2" style="display: none;" id="detailResultList">
           <thead>
           <tr>
            <th>退款流水号</th><th>原电银流水号</th><th>商户号</th><th>商户简称</th><th>原商户订单号</th><th>原支付渠道</th><th>原支付银行</th>
            <th>申请退款金额</th><th>退回商户手续费</th><th>原订单优惠金额</th>
            <th>退款确认日期</th><th>退款状态</th> <th sort="false">详细</th>
           </tr>
           </thead>
           <tbody id="resultList"></tbody>
       </table>  
                    
       <table class="tableBorder detailBox" id="detail4One" style="display: none;">
            <tr style="height: 20px">
                <td class="th1" align="right" width="15%" >原电银流水号：</td>
                <td  align="left" id="v_tseq"  width="20%"> </td>
                <td class="th1" align="right" width="12%"  >商户号：</td>
                <td  align="left" id="v_mid" width="20%"></td>
                <td class="th1" align="right" width="10%">原商户订单号：</td>
                <td  align="left" id="v_org_oid"> </td>
            </tr>
            <tr style="height: 20px">
                <td class="th1" align="right" width="10%">退款流水号：</td>
                <td width="20%" align="left" id="v_id"  > </td>
                <td class="th1" align="right" >退款单号：</td>
                <td  align="left" id="v_oid"  > </td>
                <td class="th1" align="right" >银行：</td>
                <td  align="left" id="v_gate" > </td>
            </tr>
            <tr style="height: 20px">
                <td class="th1" align="right"  >退款申请日期：</td>
                <td align="left" id="v_mdate" > </td>
                <td class="th1" align="right"  >本次退款金额：</td>
                <td align="left" id="v_ref_amt" > </td>
                <td class="th1" align="right"  >退款状态：</td>
                <td   align="left" id="v_stat"  > </td>
            </tr>
           <tr style="height: 20px">
                <td class="th1" align="right"  >原支付渠道：</td>
                <td   align="left" id="v_gid" > </td>
                <td class="th1" align="right"  >原银行流水号：</td>
                <td align="left" id="v_orgBkSeq" > </td>
                <td class="th1" align="right"  >原订单金额：</td>
                <td   align="left" id="v_orgAmt" > </td>
            </tr>
            <tr style="height: 20px">
                <td class="th1" align="right"  >原实际交易金额：</td>
                <td   align="left" id="v_orgPayAmt" > </td>
                <td class="th1" align="right"  >原订单优惠金额：</td>
                <td align="left" id="v_pre_amt1" > </td>
                <td class="th1" align="right"  >原订单差价：</td>
                <td   align="left" id="v_preAmt" > </td>
            </tr>
            <tr style="height: 20px">
                <td class="th1" align="right"  >退款确认日期：</td>
                <td align="left" id="v_req_date" > </td>
                <td class="th1" align="right"  >退款经办日期：</td>
                <td   align="left" id="v_pro_date" > </td>
                <td class="th1" align="right"  >退款审核日期：</td>
                <td   align="left" id="v_ref_date" > </td>
            </tr>
              <tr style="height: 20px">
              <td class="th1" align="right">退款处理类型：</td>
                <td align="left" id="v_refundType"> </td>
                <td class="th1" align="right">授权码：</td>
                <td align="left" id="v_authNo" colspan="3"> </td>
            </tr>
            <tr style="height: 20px">
                <td class="th1" align="right"  >退款申请原因：</td>
                <td   align="left" id="v_refund_reason" colspan="5" > </td>
            </tr>
            <tr style="height: 20px">
                <td class="th1" align="right"  >退款撤销原因：</td>
                <td   align="left" id="v_etro_reason" colspan="5" > </td>
            </tr>

            <tr style="height: 20px">
                <td class="th1" align="right"  >失败原因：</td>
                <td   align="left" id="v_reason" colspan="5" > </td>
            </tr>
            <tr style="height: 30px">
                <td colspan="6" align="center">
                    <input type="button" value="返回" class="wBox_close button"/>
                </td>
            </tr>
    </table>
</div>
</body>
</html>