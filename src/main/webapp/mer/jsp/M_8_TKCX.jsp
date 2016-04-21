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
        <script type="text/javascript" src="../../dwr/engine.js"></script>
        <script type="text/javascript" src="../../dwr/util.js"></script>
		<script type="text/javascript" src='../../dwr/interface/QueryMerRefundLogsService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/CommonService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand %>'></script>
        <script type="text/javascript" src="../../public/js/refundment/mer_jsp_refund_query.js?<%=rand%>"></script>
		<script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script> 	
</head>
<body onload="init();">
 <div class="style">
 

     <table width="100%"  align="left"  class="tableBorder">
        <tr>
            <td class="title" colspan="6">&nbsp;&nbsp;&nbsp;&nbsp;退款查询</td>
        </tr>
     <tr>
         <td class="th1" align="right" width="10%">申请日期：</td>
         <td align="left" width="30%">
          <input id="bdate" size="15px"
                    name="bdate" class="Wdate" type="text" onfocus="ryt_area_date('bdate','edate',0,30,0)" />
                    &nbsp;至&nbsp;
                    <input id="edate" size="15px" name="edate" class="Wdate" type="text" disabled="disabled" /><font color="red">*</font>
         </td>                
         
         <td class="th1" align="right"  width="10%">银行：</td>
         <td align="left" width="15%">
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
             <td class="th1" align="right"  >金额：</td>
                <td align="left"> 
               <input type="text" id="begintrantAmt" name="begintrantAmt" value="" size="10px"/>
                                      到 &nbsp;<input type="text" id="endtrantAmt" name="endtrantAmt" value="" size="10px"/></td>
               <td class="th1" align="right"  >原电银流水号：</td>
              <td align="left">
                  <input type="text" name="tseq" id="tseq" size="20" onkeyup="inputFigure(this)"/>
              </td> 
                <td class="th1" align="right"  >原商户订单号：</td>
              <td align="left">
                  <input type="text" name="orgid" id="orgid" size="20" maxlength="30" />
              </td>
             
        </tr>
       
       
        <tr>
           <td colspan="6"  height="30px">
           <input type="hidden"  name="mid" id="mid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}" />
           <input type="hidden"  name="queryType" id="queryType" value="MERREFUNDQUERY" />
           <input type="button" style="width: 100px;height: 25px;margin-left: 400px;margin-right: 20px" value="查  询" onclick="queryVerifyRefund(1);"/>
           <input type="button" style="width: 100px;height: 25px" value="下载XLS报表" onclick="downloadReturnQuery();"/>
           </td>
         </tr>
         </table>
          <table  class="tablelist tablelist2" style="display: none;" id="refundListTable">
           <thead>
           <tr>
            <th>原电银流水号</th><th>商户号</th><th>原商户订单号</th><th>银行</th>
            <th>申请退款金额</th>
            <th>退款申请日期</th><th>退款状态</th> <th>详细</th>
           </tr>
           </thead>
           <tbody id="resultList"></tbody>
       </table>   
       <table class="tableBorder detailBox" id="detail4One" style="display:none;">
            <tr style="height: 20px">
                <td class="th1" align="right" width="15%" >&nbsp;原电银流水号：</td>
                <td  align="left" id="v_tseq"  width="20%"> </td>
                <td class="th1" align="right" width="15%"  >&nbsp;商户号：</td>
                <td width="20%" align="left" id="v_mid"></td>
                <td class="th1" align="right" width="15%">&nbsp;原商户订单号：</td>
                <td  align="left" id="v_org_oid" width="*%" > </td>
            </tr>
            <tr style="height: 20px">
                <td class="th1" align="right" width="10%">&nbsp;退款流水号：</td>
                <td width="20%" align="left" id="v_id"  > </td>
                <td class="th1" align="right" >&nbsp;退款单号：</td>
                <td  align="left" id="v_oid"  > </td>
                <td class="th1" align="right" >&nbsp;银行：</td>
                <td  align="left" id="v_gate" > </td>
            </tr>
            <tr style="height: 20px">
                <td class="th1" align="right"  >&nbsp;退款申请日期：</td>
                <td align="left" id="v_mdate" > </td>
                
                <td class="th1" align="right"  >&nbsp;本次退款金额：</td>
                <td align="left" id="v_ref_amt" > </td>
                
                <td class="th1" align="right"  >&nbsp;退款状态：</td>
                <td   align="left" id="v_stat"  > </td>
            </tr>
            <tr style="height: 20px">
                <td class="th1" align="right">退还商户手续费：</td>
                <td   align="left" id="v_merFee" > </td>
                <td class="th1" align="right"  >原银行流水号：</td>
                <td align="left" id="v_orgBkSeq" > </td>
                <td class="th1" align="right"  >原订单金额：</td>
                <td   align="left" id="v_orgAmt" > </td>
            </tr>
            <tr style="height: 20px">
                
                <td class="th1" align="right"  >&nbsp;退款确认日期：</td>
                <td align="left" id="v_req_date" > </td>
                <td class="th1" align="right"  >&nbsp;退款经办日期：</td>
                <td   align="left" id="v_pro_date" > </td>
                <td class="th1" align="right"  >&nbsp;退款审核日期：</td>
                <td   align="left" id="v_ref_date" > </td>
              </tr>
            <!-- 
            <tr style="height: 20px">
                <td class="th1" align="right"  >&nbsp;授权码：</td>
                <td   align="left" id="auth_no" colspan="5" > </td>
            </tr>   
             -->
            <tr style="height: 20px">
                <td class="th1" align="right"  >&nbsp;退款申请原因：</td>
                <td   align="left" id="v_refund_reason" colspan="5" > </td>
            </tr>
            <tr style="height: 20px">
                <td class="th1" align="right"  >&nbsp;退款撤销原因：</td>
                <td   align="left" id="v_etro_reason" colspan="5" > </td>
            </tr>

            <tr style="height: 20px">
                <td class="th1" align="right"  >操作失败原因：</td>
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