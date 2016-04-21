<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>历史收款明细查询</title>
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
        <script type='text/javascript' src='../../dwr/engine.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../dwr/util.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/CommonService.js?<%=rand%>'></script>
 		<script type="text/javascript" src='../../dwr/interface/QueryMerMerHlogDetailService.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script> 
  		<script type="text/javascript" src="../../public/js/transaction/mer_jsp_transactionForQueryDetail.js?<%=rand%>"></script>
    </head>
    <body onload="init()" >
    
     <div class="style">
        <form name="MERHLOG"  method="post" action="">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="6">&nbsp;&nbsp;&nbsp;&nbsp;历史收款明细查询&nbsp;&nbsp;(<font color="red">*</font>银行名称M:信用卡支付,W:wap支付,C:充值卡支付,I:语音/快捷支付,其余:网银支付)</td></tr>
            <tr>
             <td class="th1" bgcolor="#D9DFEE" align="right" width="10%">
                     <select style="width: 80px" id="date" name="date">
                        <option value="sys_date">系统日期</option>
                        <option value="mdate">商户日期</option>
                    </select>
                
                </td>
                <td align="left"  width="30%">
                    <input id="bdate" size="13px"
                    name="bdate" class="Wdate" type="text" onfocus="ryt_area_date('bdate','edate',0,15,0)" />
                    &nbsp;至&nbsp;
                    <input id="edate" size="13px" name="edate" class="Wdate" type="text" disabled="disabled" /><font color="red">*</font>
                </td> 
                <td class="th1" bgcolor="#D9DFEE" align="right" width="6%">交易状态：</td>
                <td align="left" width="15%">
                <select style="width: 150px" id="tstat" name="tstat" >
                    <option value="">全部...</option>
                </select>
                </td>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="6%" >交易类型：</td>
                <td align="left" width="20%" >
                    <select style="width: 130px" id="type" name="type" onchange="onChangeGate();">
                        <option value="">全部...</option>
                    </select>
                </td>
            </tr>
            <tr>
            <td class="th1" align="right"  >金额：</td>
                <td align="left"> 
                <input type="text" id="begintrantAmt" name="begintrantAmt" value="" size="10px"/>
                                      到 &nbsp;<input type="text" id="endtrantAmt" name="endtrantAmt" value="" size="10px"/></td>    
                <td class="th1" bgcolor="#D9DFEE" align="right"  width="6%">商户订单号： </td>
                <td align="left"> <input type="text" id="oid" name="oid"  maxlength="30"/></td>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="6%">银行：</td>
                <td align="left"  width="22%" >
                <select style="width: 150px" id="gate" name="gate">
                    <option value="">全部...</option>
                </select>
                </td>
            </tr>
            
            
            <tr>
            <td colspan="6" align="center" style="height: 30px">
                <input type="hidden"  name="mid" id="mid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}" />
                <input type="hidden"  name="queryType" id="queryType" value="MERHLOG" />
                <input class="button" type="button" value = " 查 询 " onclick="queryMerHlog(1);" />
                 &nbsp; &nbsp; 
                <input class="button" type="button" value= " 下载XLS " onclick = "downloadDetail();"/>
            </td>
            </tr>
        </table>
       </form>
    
       <table  class="tablelist tablelist2" id="merHlogTable" style="display:none;">
           <thead>
           <tr>
             <th sort="false"><input type="checkbox" id="toggleAll" onclick="toggleAll(this)"/></th>
             <th>电银流水号</th><th>商户号</th>
             <th>商户订单号</th><th>商户日期</th>
             <th>交易金额(元)</th><th>交易状态</th>
             <th>交易类型</th><th>交易银行</th>
             <th>系统手续费(元)</th><th>系统日期</th>
             <th>操作 </th>
           </tr>
           </thead>
           <tbody id="resultList"></tbody>
       </table>
       
      </div>  
    </body>
</html>
