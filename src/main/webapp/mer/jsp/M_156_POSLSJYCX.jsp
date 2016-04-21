<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>pos历史交易查询</title>
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
        <script type="text/javascript" src="../../dwr/engine.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../dwr/util.js?<%=rand%>"></script>
        <script type="text/javascript" src='../../dwr/interface/CommonService.js?<%=rand%>'></script>
  		<script type="text/javascript" src='../../dwr/interface/QueryPosMerTodayService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script> 
        <script type="text/javascript" src="../../public/js/transaction/mer_queryPosPastTrade.js?<%=rand%>"></script>
        <script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
    </head>
    <body onload="init();">
    <div class="style">
        <form name="queryForm">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="6">&nbsp;POS历史交易查询</td></tr>
            <tr>
                
                <td class="th1" bgcolor="#D9DFEE" align="right">商户订单号： </td>
                <td align="left"> <input type="text" id="oid" name="oid"  maxlength="30"/></td>
               
                 <td class="th1" bgcolor="#D9DFEE" align="right" width="10%">电银终端号：</td>
                  <td align="left" width="20%">
                	<input type="text" id="innerTermId" name="innerTermId" maxlength="30" />
                </td>
                
                 <td class="th1" bgcolor="#D9DFEE" align="right">交易类型：</td>
                <td align="left">
         
                	<select id="type" name = "type">
                		<option value="">全部...</option>
                	</select>
                </td>
            </tr>
            <tr>
             <td class="th1" bgcolor="#D9DFEE" align="right">金额：</td>
                <td align="left"><input type="text" id="begintrantAmt" name="begintrantAmt" value=""/>
                                      到<input type="text" id="endtrantAmt" name="endtrantAmt" value=""/></td>
                                      <td class="th1" align="right">终端编号：</td>
                <td align="left"> 
               		<input type="text" id="terminalNumber" name="terminalNumber" maxlength="30"/>
           		</td> 
                <td class="th1" bgcolor="#D9DFEE" align="right">交易状态：</td>
                <td align="left">
                	<select id="tstat" name = "tstat">
                		
                	</select>
                </td>
           		 
           		
            </tr>
            <tr>
            	<td class="th1" align="right" >系统日期：</td>
           		<td align="left" width="30%">
               			<input id="beginDate" name="beginDate"  value=""  class="Wdate" type="text" onfocus="ryt_area_date('beginDate','endDate',0,'30',0)"/>&nbsp;至
               			<input id="endDate" name="endDate"  value="" class="Wdate" type="text" disabled="disabled"/><font color="red">*</font>
               			
           		</td>
            </tr>
            <tr>
            <td colspan="6" align="center" style="height: 30px">
                <input type="hidden" name="mid" id="mid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}" />
                <input class="button" type="button" value = " 查 询 " onclick="queryMerToday(1);" />
            </td>
            </tr>
        </table>
       </form>
    
       <table  class="tablelist tablelist2"  id="merTodayTable" style="display:none;">
           <thead>
           <tr>
            <th>电银交易编号</th><th>电银商户号</th>
             <th>电银终端号</th>
             <th>商户订单号</th><th>交易类型</th>
              <th>交易金额(元)</th><th>交易状态</th>
      		 <th>系统时间</th>
             <th>电银流水号</th><th>终端编号</th>
             <th>银联参考号</th>
             <th>系统手续费</th>
             <th>描述</th>
           </tr>
           </thead>
           <tbody id="resultList"></tbody>
       </table>
      </div>   
    </body>
</html>
