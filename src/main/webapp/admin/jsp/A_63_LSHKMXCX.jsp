<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>划款明细查询</title>
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
        <script type='text/javascript' src='../../dwr/engine.js'></script>
        <script type='text/javascript' src='../../dwr/util.js'></script>
		<script type="text/javascript" src='../../dwr/interface/AutoSettlementService.js?<%=rand %>'></script>
		<script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand %>'></script>
		<script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
		<script type="text/javascript" src='../../public/js/settlement/payment_query.js?<%=rand%>'></script>		
        
</head>
<body>
<div class="style">
	<table class="tableBorder" >
		<tr><td class="title" colspan="2">&nbsp;划款明细查询</td></tr>
		<tr>
          <td class="th1" align="right" width="40%">划款确认日期：</td>
           <td align="left" width="*%">
               <input id="sys_date_begin" name="sys_date_begin"  value=""  class="Wdate" type="text" 
               			onfocus="historyAndToday('sys_date_begin','sys_date_end',0,30);" />&nbsp;至
               <input id="sys_date_end" class="Wdate" type="text"/>
               <font color="red">*</font>&nbsp;&nbsp;&nbsp;
           </td>
           <!-- 
           <td class="th1" align="right" width="15%">账号：</td>
           <td align="left" width="">
               <input id="acc_no" name="acc_no"  value=""  type="text" size="24"/>
              
           </td> 
           -->
		</tr>
		<tr align="center">
			<td colspan="2">
				<input value="查  询" type="button" onclick="queryPayment('1');" class="button" />
			</td>
		</tr>
	</table>
	<table class="tablelist tablelist2" style="display: none;" id = "minfoList">
			<thead><tr>
				<th>银行流水号</th>
				<th>银行日期</th>
				<th>交易金额(元)</th>
				<th>状态</th>
				<th>户名</th>
				<th>余额(元)</th>
				<th>对方户名</th>
				<th>详细信息</th>
			</tr></thead>
			<tbody id="minfoListBody">
			</tbody>
	</table>
	<table class="tableBorder detailBox" id="slogDetail" style="display: none;" >
			<tbody>
				<tr>
                    <td class="th1" align="right" width="11%">&nbsp;电银流水号：</td>
                    <td width="22%" align="left" id="id" ></td>
                    <td class="th1" align="right" width="11%">&nbsp;付款方户名：</td>
                    <td width="22%" align="left" id="pay_acname" ></td>
                    <td class="th1" align="right" width="11%">&nbsp;商户号：</td>
                    <td width="22%" align="left" id="mid" ></td>
                </tr>
				<tr>
                    <td class="th1" align="right" width="11%">&nbsp;银行日期：</td>
                    <td width="22%" align="left" id="bank_date" ></td>
                    <td class="th1" align="right" width="11%">&nbsp;付款方账号：</td>
                    <td width="22%" align="left" id="pay_acno" ></td>
                    <td class="th1" align="right" width="11%">&nbsp;对方户名：</td>
                    <td width="22%" align="left" id="rcv_acname" ></td>
                </tr>
				<tr>
                    <td class="th1" align="right" width="11%">银行响应时间：</td>
                    <td width="22%" align="left" id="bank_time" ></td>
                    <td class="th1" align="right" width="11%">&nbsp;交易金额：</td>
                    <td width="22%" align="left" id="liq_amt" ></td>
                    <td class="th1" align="right" width="11%">&nbsp;对方账号：</td>
                    <td width="22%" align="left" id="rcv_acno" ></td>
                </tr>
				<tr>
                    <td class="th1" align="right" width="11%">&nbsp;状态：</td>
                    <td width="22%" align="left" id="pay_state" ></td>
                    <td class="th1" align="right" width="11%">&nbsp;银行流水号：</td>
                    <td width="22%" align="left" id="bank_seq" ></td>
                    <td class="th1" align="right" width="11%">&nbsp;对方地址：</td>
                    <td width="22%" align="left" id="rcv_addr" ></td>
                </tr>
				<tr>
                    <td class="th1" align="right" width="11%">&nbsp;业务类型：</td>
                    <td width="22%" align="left" id="trans_type" ></td>
                    <td class="th1" align="right" width="11%">&nbsp;系统时间：</td>
                    <td width="22%" align="left" id="sure_time" ></td>
                    <td class="th1" align="right" width="11%">对方开户行行号：</td>
                    <td width="22%" align="left" id="rcv_lhh" ></td>
                </tr>
				<tr>
                    <td class="th1" align="right" width="11%">&nbsp;批次号：</td>
                    <td width="22%" align="left" id="batch" ></td>
                    <td class="th1" align="right" width="11%">&nbsp;系统日期：</td>
                    <td width="22%" align="left" id="sure_date" ></td>
                    <td class="th1" align="right" width="11%">对方开户行行名：</td>
                    <td width="22%" align="left" id="rcv_bank_name" ></td>
                </tr>

				<tr>
					<td colspan="6" align="center" height="25px">
						<input type="button" class="wBox_close button" value=" 返 回 "></input>
					</td>
				</tr>
			</tbody>
	</table>		
</div>
</body>
</html>
