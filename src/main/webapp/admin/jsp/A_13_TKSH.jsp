<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
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
        <script type='text/javascript' src='../../dwr/engine.js'></script>
        <script type='text/javascript' src='../../dwr/util.js'></script>
        <script type='text/javascript' src='../../dwr/interface/RefundmentService.js?<%=rand %>'></script>
        <script type='text/javascript' src='../../dwr/interface/PageService.js?<%=rand %>'></script>
		<script type="text/javascript" src="../../public/js/refundment/admin_motion_verify.js?<%=rand%>"></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/datePicker/WdatePicker.js'></script>
        
</head>

<body onload="init()">
<div class="style">
<table class="tableBorder"  id="queryPanelTable">
  <tr>
    <td class="title" colspan="6">
          &nbsp;&nbsp;&nbsp;&nbsp;退款运行审核&nbsp;&nbsp;(<font color="red">*</font>银行名称M:信用卡支付,W:wap支付,C:充值卡支付,I:语音/快捷支付,其余:网银支付)
        </td>
    </tr>
    <tr>
     <td class="th1" align="right" width="10%">&nbsp;商户号：</td>
     <td align="left" >
        <input type="text" id="mid" name="mid"   size="8px" style="width:150px" onkeyup="checkMidInput(this);"/>
             <!-- <select style="width: 150px" id="smid" name="smid" onchange="initMidInput(this.value)">
               <option value="">全部...</option>
             </select> -->
             
    </td>
    <td class="th1" align="right" width="10%">&nbsp;退款经办日期：</td>
     <td align="left">
     <input id="date_begin" name="date_begin" class="Wdate" type="text" onfocus="ryt_area_date('date_begin','date_end',0,30,0)" />
                                          至<input id="date_end" name="date_end"  class="Wdate" type="text" disabled="disabled" />
     </td> 
      <td class="th1" align="right"  width="10%">支付渠道：</td>
         <td align="left">
           <select  id="gateRouteId" name="gateRouteId" >
           <option value="">全部...</option>
           </select>&nbsp;
         </td>     
      </tr>
      <tr>
      <td class="th1" align="right" >退款状态：</td>
           <td align="left"  >
             <select style="width:120px" id="stat" name="stat" >
              <option value="">全部...</option>
             </select>
           </td>  
               <td class="th1" align="right"  >原电银流水号：</td>
              <td align="left">
                  <input type="text" name="tseq" id="tseq" size="20" onkeyup="inputFigure(this)"/>
              </td> 
           <td class="th1" align="right"  >审核状态：</td>
           <td><select id="vstate" name="vstate" style="width:80px"><option value="0">未审核</option><option value="1">已审核</option></select></td>     
        </tr>
        <tr>
         <td class="th1" align="right" >商户状态：</td>
          <td align="left">
                <select style="width: 150px" id="mstate" name="mstate">
                    <option value="">全部...</option>
                </select>
           </td>
           <td class="th1" align="right"></td>
            <td align="left"></td>
        <td class="th1" align="right"></td>
         <td align="left"></td>
        </tr>
       <tr>
	     <td align="center" colspan="6">
	       <input type="button" value = "查 询" name="submitQuery" class="button" onclick="queryRefundMotions(1);"/>
	        <input type="button" value = "下 载" name="downLoadVerify" class="button" onclick="queryRefundMotions(-1);"/>
	    </td>         
      </tr>
    </table>
                
     <form name="verifyRefundForm" id="verifyRefundForm"  method="post">
     <table width="100%" class="tablelist tablelist2">
       <thead> <tr valign="middle" class="title2">
            <th align="center" sort="false">选择</th>
            <th>原电银流水号</th>
            <th>商户号</th>
            <th>商户简称</th>
            <th>原商户订单号</th>
            <th>原支付渠道</th>
            <th>原支付银行</th>
            <th>退款金额</th>
            <th>退回商户手续费</th>
            <th>退款状态</th> 
            <th>审核状态</th> 
            <th>系统日期</th>
            <th>经办日期</th>
            <!-- <th width="300px">操作失败的原因</th> -->
        </tr></thead>
        <tbody id="resultList"></tbody>   
     </table>
    </form>
            
</div>
</body>
</html>
