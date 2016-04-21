<%@ page language="java" pageEncoding="UTF-8" %>
<% String path = request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>信用卡支付查询</title>
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
        <script type="text/javascript" src="../../dwr/engine.js"></script>
        <script type="text/javascript" src="../../dwr/util.js"></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/QueryCreditCardResultService.js?<%=rand%>'></script> 
		<script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
		<script type="text/javascript" src='../../public/js/transaction/admin_queryCreditCardPay.js?<%=rand%>'></script>
    </head>
    <body onload="initOption();">
    <div class="style">
        <form  method="post" action="">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="6">&nbsp;&nbsp;&nbsp;&nbsp;信用卡支付查询</td></tr>
            <tr>
                <td class="th1" align="right" width="10%">商户号：</td>
                <td align="left" width="25%">&nbsp;
                <input type="text" id="mid" name="mid" style="width:150px;"  onkeyup="checkMidInput(this);"/>
                <!-- <select style="width: 130px" id="smid" name="smid" onchange="initMidInput(this.value)">
                     <option value="">全部...</option>
                   </select> -->
                 </td>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="10%">系统日期：</td>
                <td align="left" width="30%">&nbsp;
                          <input id="bdate" size="15px"
                    name="bdate" class="Wdate" type="text" onfocus="ryt_area_date('bdate','edate',0,30,0)" />
                    &nbsp;至&nbsp;
                    <input id="edate" size="15px" name="edate" class="Wdate" type="text" disabled="disabled" /><font color="red">*</font>
                </td>
                
                <td class="th1" bgcolor="#D9DFEE" align="right" width="11%">交易状态：</td>
                <td align="left" width="20%">&nbsp;
                <select id="tstat" name="tstat"><option value="">全部...</option></select>
                </td>
            </tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" >电银流水号：</td>
                <td align="left" width="20%">&nbsp;
                 <input type="text" id="tseq" name="tseq"/>
                </td>
                
                <td class="th1" bgcolor="#D9DFEE" align="right" > 
                <select style="width: 80px" id="cardType" name="cardType">
                        <option value="1">卡号</option>
                        <option value="2">身份证号</option>
                        <option value="3">手机号</option>
                    </select></td>
                <td align="left" width="20%">&nbsp;
                <input name="cardVal" id="cardVal"/>
                </td>
                <td class="th1" bgcolor="#D9DFEE" align="right" >同一卡号累计金额超过 </td>
                <td align="left"><input name="payAmount" id="payAmount" style="width:80px;" maxlength="10"/>元</td>
            </tr>
            <tr>
               <td class="th1" bgcolor="#D9DFEE" align="right" >商户状态：</td>
                <td align="left" width="20%">
                <select style="width: 150px" id="mstate" name="mstate">
                    <option value="">全部...</option>
                </select></td>
              <td class="th1" align="right"  >金额：</td>
              <td align="left"> 
               <input type="text" id="begintrantAmt" name="begintrantAmt" value="" size="15px"/>
                                      到 &nbsp;<input type="text" id="endtrantAmt" name="endtrantAmt" value="" size="15px"/></td>
               <td class="th1" bgcolor="#D9DFEE" align="right" ></td><td></td>
            </tr>
            <tr>
            <td colspan="6" align="center" style="height: 30px">
                <input class="button" type="button" value = " 查 询 " onclick="showCreditCardResult(1);" />
                <input class="button" type="button" value = "下载 " onclick="downloadCardPay();" />
            </td>
            </tr>
        </table>
       </form>
       <table  class="tablelist tablelist2" id="creditCardTable" style="display:none;" >
           <thead>
           <tr>
             <th width="8%">电银流水号</th><th width="10%">系统日期</th><th width="8%">商户号</th>
             <th width="15%">商户简称</th><th width="8%">交易金额</th><th width="10%">交易状态</th>
             <th width="15%">卡号</th><th width="10%">手机号</th><th>身份证号</th>
           </tr>
           </thead>
           <tbody id="resultList"></tbody>
       </table>

        </div>
    </body>
</html>
