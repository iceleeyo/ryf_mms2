<%@ page language="java" pageEncoding="UTF-8" %>
<% String path = request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>单笔划款结果查询</title>
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
		<script type="text/javascript" src="../../dwr/engine.js?<%=rand %>"></script>
        <script type="text/javascript" src="../../dwr/util.js?<%=rand %>"></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/AdminZHService.js?<%=rand%>'></script> 
        <script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
        <script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
        <script type="text/javascript" src="../../public/js/adminAccount/admin_dbhkjgcx.js?<%=rand%>"></script>
 		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
    </head>
    <body onload="init();">
    <div class="style">
        <form name="MERTLOG"  method="post" action="">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="4">&nbsp;&nbsp;&nbsp;&nbsp;单笔划款结果查询&nbsp;&nbsp;
            </td></tr>
            
            <tr>
            	<td class="th1" align="right" width="10%">商户号：</td>
                <td align="left" width="25%">
                <input type="text" id="mid" name="mid" style="width: 50px;" maxlength="6" onchange="query4ZH();" onkeyup="checkMidInput(this);"/>
                  <select style="width: 150px" id="smid" name="smid"  onchange="initMidInput(this.value);query4ZH();">
                     <option value="">请选择...</option>
                   </select>
                 </td>
                 
                <td class="th1" align="right" width="15%">账户号：</td>
                <td align="left" width="35%">
               <select style="width: 150px" id="aid" name="aid">
                    <option value="">请选择...</option>           
                </select>
                 </td>
            </tr>
            
            <tr>
                <td class="th1" align="right" width="15%">交易日期：</td>
                <td align="left" >
                    <input id="bdate" size="15px"
                    name="bdate" class="Wdate" type="text" onfocus="ryt_area_date('bdate','edate',0,30,0)" />
                    &nbsp;至&nbsp;
                    <input id="edate" size="15px" name="edate" class="Wdate" type="text" disabled="disabled" /><font color="red">*</font>
                </td>
                 
                 <td class="th1" bgcolor="#D9DFEE" align="right" width="15%">状态：</td>
                <td align="left" width="35%">
	                <select style="width: 150px" id="state" name="state">
	                    <option value="">请选择...</option>             
	                </select>
                </td>
            </tr>
            
            <tr>     
                <td class="th1" bgcolor="#D9DFEE" align="right" width="15%">银行卡号：</td>
                <td align="left"><input type="text"  name="to_acc_no" id="to_acc_no"  maxlength="32" size="20"/></td>
                 <td class="th1" bgcolor="#D9DFEE" align="right"> </td>
                 <td></td>
            </tr>
            <tr>
                 <td  class="th1" align="right" width="15%">其他条件：</td>
             <td align="left" colspan="6">
              <select  style="width: 230px" id="other_id" name="other_id" >
                <option value="all" >不做为查询条件...</option>
                <option value="acc_month_count" >同一收款卡号月累计成功次数超出 </option>
                <option value="acc_month_amt" >同一收款卡号月累计成功金额超出</option>
                <option value="trans_limit" >单笔代发金额超出</option>
              </select>
                <input type="text" id="amount_num" name="amount_num" value="" size="20" maxlength="11"/>
              &nbsp;&nbsp;
              </td>
              </tr>
            
            
            <tr>
            <td colspan="6" align="center" style="height: 30px">
                <input class="button" type="button" value = " 查 询 " onclick="querySingleTransMoney(1);" />
            </td>
            </tr>
        </table>
       </form>

       <table  class="tablelist tablelist2" id="dbhkTable" style="display: none;">
           <thead>
           <tr>
             <th>融易付流水号</th><th>账户号</th><th>账户名称</th><th>系统订单号</th><th>交易金额</th>
             <th>交易状态</th><th>交易对方</th><th>订单时间</th>
             <th>收款账户名</th><th>收款帐号</th><th>收款开户银行</th>
           </tr>
           </thead>
           <tbody id="resultList">
           </tbody>
       </table>
       

        </div>
    </body>
</html>
