<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>退款确认</title>
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
        <script type="text/javascript" src="../../dwr/engine.js?<%=rand %>"></script>
        <script type="text/javascript" src="../../dwr/util.js?<%=rand %>"></script>
        <script type='text/javascript' src='../../dwr/interface/MerRefundmentService.js?<%=rand %>'></script>
        <script type="text/javascript" src='../../dwr/interface/CommonService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand %>'></script>
 		<script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
        <script type="text/javascript" src="../../public/js/refundment/mer_jsp_refund_verify.js?<%=rand%>"></script>
 		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
</head>
<body onload="init();">
 <div class="style">
         <table width="100%"  align="left"  class="tableBorder">
	    <tr>
	        <td class="title" colspan="4">&nbsp;&nbsp;&nbsp;&nbsp;商户退款确认&nbsp;&nbsp;</td>
	    </tr>
     <tr>
         <td class="th1" align="right" width="20%">申请日期：</td>
         <td align="left" width="30%">
           <input id="bdate" name="bdate"  class="Wdate" type="text" 
           onfocus="WdatePicker({skin:'ext',minDate:'2010-01-01',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});"/>&nbsp;&nbsp;至&nbsp;
           <input id="edate" name="edate"  class="Wdate" type="text" 
           onfocus="WdatePicker({skin:'ext',minDate:'#F{$dp.$D(\'bdate\')}',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});"/>
         </td>                
         
         <td class="th1" align="right"  width="10%">银行：</td>
         <td align="left" >
           <select style="width:150px" id="gate" name="gate" >
           <option value="">全部...</option>
           </select>&nbsp;
         </td>
       </tr>
        <tr>
              <td class="th1" align="right"  >原商户订单号：</td>
              <td align="left">
                  <input type="text" name="orgid" id="orgid" size="20" maxlength="30" />
              </td>
               <td class="th1" align="right"  >原电银流水号：</td>
              <td align="left">
                  <input type="text" name="tseq" id="tseq" size="20" onkeyup="inputFigure(this)"/>
              </td> 
        </tr>
	    <tr>
	       <td colspan="4"  height="30px" align="center">
	       
           <input type="hidden"  name="mid" id="mid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}" />
	       <input type="button" class="button" value="查  询" onclick="queryVerifyRefund(1);"/>
	       </td>
	     </tr>
         </table>
         
          <table  class="tablelist tablelist2" id="refundList" style="display:none;">
           <thead>
           <tr>
            <th >选择</th><th>原电银流水号</th><th>商户号</th><th>原商户订单号</th><th>银行</th>
		    <th>申请退款金额</th><th>退款状态</th> 
		    <th>退款申请日期</th><th width="500px">请求退款原因</th>
           </tr>
           </thead>
           <tbody id="resultList"></tbody>
       </table>               
                      
</div>
</body>
</html>