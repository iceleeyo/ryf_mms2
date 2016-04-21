<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>划款结果处理</title>
<%
response.setHeader("Pragma","No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", 0);
int rand = new java.util.Random().nextInt(10000);
%>
        <meta http-equiv="pragma" content="no-cache" />
        <meta http-equiv="cache-control" content="no-cache"/>
        <meta http-equiv="Expires" content="0"/>  
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css"/> 
        <link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css" />
        <script type="text/javascript" src="../../dwr/engine.js?<%=rand %>"></script>
        <script type="text/javascript" src="../../dwr/util.js?<%=rand %>"></script>
        <script type="text/javascript" src="../../public/js/ryt.js?<%=rand %>"></script>
        <script type="text/javascript" src='../../dwr/interface/HKResultHandleService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
        <script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
        <script type="text/javascript" src="../../public/js/adminAccount/admin_synczdjs.js?<%=rand%>"></script>
    </head>
    
    <body onload="initB2EGate()">
    <div class="style">
        <form name="MERTLOG"  method="post" action="">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="4">&nbsp;&nbsp;&nbsp;&nbsp;划款结果处理&nbsp;&nbsp;</td></tr>
             <tr>
	            	 <input type="hidden"  name="uid" id="uid"  />
	                <%-- <input type="hidden"  name="mid" id="mid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}" /> --%>
	               <td class="th1" align="right" width="15%">商户号：</td>
	                <td align="left" width="35%">
	                		<input type="text" id="mid" name="mid" style="width: 150px;"  onkeyup="checkMidInput(this);"/>
	                 </td>
	                 <td class="th1" bgcolor="#D9DFEE" align="right" width="15%">划款日期：</td>
	                 <td align="left"  width="35%">
		          			<input id="bdate" size="15px"  name="bdate" class="Wdate" type="text" onfocus="ryt_area_date('bdate','edate',0,30,0)" />
		                    &nbsp;至&nbsp;
		                    <input id="edate" size="15px" name="edate" class="Wdate" type="text" disabled="disabled" />
	               </td>
            </tr>
            <tr>
	               <td class="th1" align="right" >结算批次：</td>
	                <td>
	                	<input type="text" id="liqBatch" name="liqBatch"   size="20"/>
	                </td>
	          	  <td class="th1" bgcolor="#D9DFEE" align="right" >划款状态：</td>
	            	<td >
		            	<select style="width: 150px" id="state" name="state">
		                  <option value="0">全部</option>
		                  <option value="4">请求银行失败</option>
		                  <option value="1">处理中</option>
		                  <option value="3">交易失败</option>
		                </select>  
	               </td>
            </tr>
            <tr>
            	<td colspan="4" align="center">
            		<input class="button" type="button" value = " 查  询 " onclick="queryDataforAutoSettlement(1)"/>
            		<input class="button" type="button" style="width:100px;" value = " 下 载"  onclick="downAutoSettlementData()" />
            	</td>
            </tr>
        </table>
       </form>
       <table  class="tablelist tablelist2" id="fxpzTable" >
           <thead>
           <tr>
              <th>全选</th><th>电银流水号</th><th>商户号</th><th>结算批次号</th><th>结算确认日期</th><th>商户名称</th><th>开户银行名称</th>
              <th>开户账户名称</th><th>开户账户号</th><th>划款状态</th><th>划款金额</th><th>交易银行</th><th>支付渠道</th>
           </tr>
           </thead>
           <tbody id="resultList">
           </tbody>
       </table>
<%--           <%@include file="SGDFOption.jsp" %> --%>
      </div>   
    </body>
</html>

           
           
    
