<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>代付确认</title>
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
        <link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="../../dwr/engine.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../dwr/util.js?<%=rand%>"></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/js/base64.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/js/merchant/mer_dfqr.js?<%=rand%>'></script>
		<script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script> 
		<script type="text/javascript" src="../../dwr/interface/DfqrService.js"></script>
    </head>
    <body onload="init();">
    <div class="style">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="4">&nbsp;&nbsp;&nbsp;&nbsp;代付确认</td></tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="11%"> 代付批次号：</td>
                <td align="left" width="20%">
                	<input type="text" id="batchNo" name="batchNo" maxlength="20"/>
                </td>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="11%">交易类型：</td>
                <td align="left" width="20%">
                <select style="width: 150px" id="type" name="type" >
                    <option value="-1">全部...</option>
                    <option value="11">对私代付</option>
                    <option value="12">对公代付</option>
                </select>
                </td>
            </tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right">电银流水号：</td>
                <td align="left"><input type="text" id="tseq" name="tseq" id="tseq" maxlength="20"/></td>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="11%"> 
                <select style="width: 150px" id="dateType" name="dateType">
                    <option value="0">申请代付日期</option>
                    <option value="1">确认代付日期</option>
                    <option value="2">撤销代付日期</option>
                </select>：</td>
                <td align="left" width="20%">
               		<input id="bdate" size="15px"
                    name="bdate" class="Wdate" type="text" onfocus="ryt_area_date('bdate','edate',0,30,0)" />
                    &nbsp;至&nbsp;
                    <input id="edate" size="15px" name="edate" class="Wdate" type="text" disabled="disabled" /><font color="red">*</font>
                </td>
            </tr>
            
            <tr>
            <td class="th1" bgcolor="#D9DFEE" align="right">代付状态： </td>
                <td align="left"><select style="width: 150px" id="state" name="state" >
                    <option value="-1">全部...</option>
                    <option value="0">账户申请代付</option>
                    <option value="1">账户确认代付</option>
                    <option value="2">账户代付成功</option>
                    <option value="3">账户代付失败</option>
                    <option value="5">账户撤销代付</option>
                </select></td>
            <td class="th1" bgcolor="#D9DFEE" align="right" width="11%"></td>
             <td align="left" width="20%"> </td>
            </tr>
            <tr>
            <td colspan="4" align="center" style="height: 30px">
                <input type="hidden"  name="mid" id="mid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}" />
                <input class="button" type="button" value = " 查 询 " onclick="queryDFQR(1);" />
                <input class="button" type="button" value= " 下载XLS " onclick = "downqueryDaiFuQueRen();"/>
            </td>
            </tr>
        </table>
    
       <table  class="tablelist tablelist2"  id="dateTable" style="display:none;">
           <thead>
           <tr>
             <th>全选</th><th>电银流水号</th><th>账户号</th>
             <th>商户订单号</th><th>批次号</th>
             <th>交易金额(元)</th><th>系统手续费(元)</th>
             <th>付款金额（元）</th><th>收款银行</th>
             <th>收款户名</th><th>收款账号</th>
             <th>开户行所在省份</th><th>代付状态</th>
             <th>交易类型</th><th>代付日期</th>
             <th>用途</th>
           </tr>
           </thead>
           <tbody id="resultList"></tbody>
       </table>
       
      </div>   
    </body>
</html>
