<%@ page language="java" 
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
 <table id="hlogDetail_"  class="tableBorder detailBox3" style="display: none;height:10%">
  <tr><td class="th1" colspan="2">用户名</td><td colspan="2" id="trans_uid"></td><td class="th1" colspan="2">账户号</td><td colspan="2" id="trans_aid"> b</td><td class="th1" colspan="1">账户名</td><td colspan="1" id="trans_aname">b</td></tr></tr>
				  <tr><td class="th1" colspan="2">订单号</td><td colspan="2" id="trans_oid">b</td><td class="th1" colspan="2" >批次号</td><td colspan="2" id="trans_flow">b</td><td class="th1" colspan="1" >订单时间</td><td colspan="1" id="sys_date">b</td></tr>
				  <tr><td class="th1" colspan="2">交易金额</td><td colspan="2" id="trans_amt">b</td><td class="th1" colspan="2" >手续费</td><td colspan="2" id="trans_fee">b</td><td class="th1" colspan="1">交易类型</td><td colspan="1" id="trans_ptype">b</td></tr>
				 <!--  <tr><td class="th1" colspan="2">交易类型</td><td colspan="8" id="trans_ptype">b</td></tr> -->
				  <tr><td colspan="10">	<input type="hidden" id="h_oid"><input type="hidden" id="h_trans_flow">
				  <table  class="tableBorder " height:10%">
				             	
           
           <thead>
          
           <tr>
             <th bgcolor="#2B8AD0"  style="color: #fff">系统订单号</th><th bgcolor="#2B8AD0"  style="color: #fff">交易状态</th><th bgcolor="#2B8AD0"  style="color: #fff">交易类型</th>
             <th bgcolor="#2B8AD0"  style="color: #fff">收款方银行行号</th><th bgcolor="#2B8AD0"  style="color: #fff">收款方银行</th><th bgcolor="#2B8AD0"  style="color: #fff">收款账户名</th><th bgcolor="#2B8AD0"  style="color: #fff">收款账号</th><th bgcolor="#2B8AD0"  style="color: #fff">审核意见</th>
             <!-- 
             <th>融易付流水号</th>
              -->
           </tr>
           </thead>
           <tbody id="resultList_" align="center">
           </tbody>
	       </table></td>
	       </tr>
       </table>
  	<input type="hidden" id="show_aid">
  	<input type="hidden" id="show_oid">
  	<input type="hidden" id="show_transflow">

  </body>
</html>