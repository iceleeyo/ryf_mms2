<%@ page language="java" pageEncoding="UTF-8" %>
<% String path = request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>当天收款交易查询</title>
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
        <link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="../../dwr/engine.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../dwr/util.js?<%=rand%>"></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/QueryMerTodayService.js?<%=rand%>'></script> 
		<script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
		<script type="text/javascript" src='../../public/js/transaction/admin_queryTodayTrade.js?<%=rand%>'></script>

    </head>
    <body onload="init();">
    <div class="style">
        <form name="MERTLOG"  method="post" action="">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="6">&nbsp;&nbsp;&nbsp;&nbsp;当天收款交易查询&nbsp;&nbsp;(<font color="red">*</font>银行名称M:信用卡支付,W:wap支付,C:充值卡支付,I:语音/快捷支付,其余:网银支付)</td></tr>
            
            <tr>
                <td class="th1" align="right" width="10%">商户号：</td>
                <td align="left" width="30%">
                <input type="text" id="mid" name="mid" style="width:150px;"   onkeyup="checkMidInput(this);"/>
                  <!-- <select style="width: 140px" id="smid" name="smid" onchange="initMidInput(this.value)">
                     <option value="">全部...</option>
                   </select> -->
                 </td>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="11%">银行：</td>
                <td align="left" width="20%">
                <select style="width: 150px" id="gate" name="gate">
                    <option value="">全部...</option>
                </select>
                </td>
                
                <td class="th1" bgcolor="#D9DFEE" align="right" width="11%">商户状态：</td>
                <td align="left" width="20%">
                <select style="width: 150px" id="mstate" name="mstate">
                    <option value="">全部...</option>
                </select>
                </td>
            </tr>
            
            
            
            <tr>
   
                <td class="th1" bgcolor="#D9DFEE" align="right" >支付渠道：</td>
                <td align="left" width="30%"><input type="text" id="gateName" name="gateName" onblur="gateRouteIdList()"/>
                <select style="width: 150px" id="gateRouteId" name="gateRouteId" >
                    <option value="">全部...</option>
                </select>
                </td>
                
                <td class="th1" bgcolor="#D9DFEE" align="right" >交易状态：</td>
                <td align="left" width="20%">
                <select style="width: 150px" id="tstat" name="tstat" >
                    <option value="">全部...</option>
                </select>
                </td>
                <td class="th1" bgcolor="#D9DFEE" align="right"  >交易类型：</td>
                <td align="left">
                    <select style="width: 150px" id="type" name="type" onchange="onChangeGate()">
                        <option value="">全部...</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right">电银流水号：</td>
                <td align="left"><input type="text" id="tseq" name="tseq" value=""/></td>
                 <td class="th1" bgcolor="#D9DFEE" align="right">商户订单号： </td>
                <td align="left"> <input type="text" id="oid" name="oid"/></td>
                <td class="th1"  align="right">银行流水号：</td>
                <td align="left"><input type="text" id="bkseq" name="bkseq"  maxlength="25"/></td>
            </tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right">金额：</td>
                <td align="left"><input type="text" id="begintrantAmt" name="begintrantAmt" value=""/>
                                      到<input type="text" id="endtrantAmt" name="endtrantAmt" value=""/></td>
                	<td class="th1"  align="right">接入方式</td>
            <td align="left"><select style="width: 150px" id="p15" name="p15">
            	<option value="">全部...</option>
             	<option value="1">接口</option>
             	<option value="2">web</option>
             	<option value="3">wap</option>
             	<option value="4">控件</option>
             	</select>
            </td>
                <td class="th1"  align="right"></td>
                <td align="left"></td>
            </tr>
            <tr>
            <td colspan="6" align="center" style="height: 30px">
                <input type="hidden"  name="queryType" id="queryType" value="MERTLOG" />
                <input class="button" type="button"value = "查 询" onclick="queryMerToday(1);" />
                <!-- 
                <input style="width: 90px;height: 25px;margin-right: 10px" type="button" value = " 下载TXT "  onclick="query('txt', '');"/>
                 -->
                <input class="button" type="button" value= "下载XLS" onclick = "downloadToday();"/>
            </td>
            </tr>
        </table>
       </form>

       <table  class="tablelist tablelist2" id="merTodayTable" style="display:none;" >
           <thead>
           <tr>
             <th>电银流水号</th><th>商户号</th><th>商户简称</th>
             <th>商户订单号</th><th>商户日期</th>
             <th>交易金额(元)</th><th>交易状态</th>
             <th>交易类型</th><th>交易银行</th><th>支付渠道</th>
             <th>系统手续费(元)</th><th>系统时间</th><th>银行流水号</th><th>接入方式</th><th sort="false">失败原因</th>
           </tr>
           </thead>
           <tbody id="resultList"></tbody>
       </table>
       
		<%@include file="detailTable.jsp" %>
       
        </div>
    </body>
</html>
