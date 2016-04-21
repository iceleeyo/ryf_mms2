<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>手工代付查询</title>
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
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type="text/javascript" src="../../dwr/interface/SgDfQueryService.js"></script>
        <script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
        <script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
        <script type="text/javascript" src="../../public/js/adminAccount/admin_sgdfquery.js?<%=rand%>"></script>
    </head>
    
    <body onload="initB2EGate()">
    <div class="style">
        <form name="MERTLOG"  method="post" action="">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="4">&nbsp;&nbsp;&nbsp;&nbsp;手工代付查询&nbsp;&nbsp;</td></tr>
             <tr>
             <input type="hidden"  name="uid" id="uid"  />
                <%-- <input type="hidden"  name="mid" id="mid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}" /> --%>
               <td class="th1" align="right" width="15%">商户号：</td>
                <td align="left" width="35%">
                <input type="text" id="mid" name="mid" style="width: 150px;"  onkeyup="checkMidInput(this);"/>
                  <!-- <select style="width: 150px" id="smid" name="smid"  onchange="initMidInput(this.value);query4ZH();">
                     <option value="">请选择...</option>
                   </select> -->
                 </td><td class="th1" bgcolor="#D9DFEE" align="right" width="15%">批次号：</td><td align="left"  width="35%">
         <input type="text" id="trans_flow" name="trans_flow"   size="20" maxlength="20"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
               <!-- <input class="button" type="button" value = " 查  询 " onclick="queryDaiFaJingBan()"/> -->
               </td>
               
            </tr>
            <tr><td class="th1" bgcolor="#D9DFEE" align="right" >交易类型：</td><td ><select style="width: 150px" id="ptype" name="ptype">
                  <option value="0">全部</option>
                    <option value="12">对公代付</option>
					<option value="11">对私代付</option>
                </select>  
                </td>
                <td class="th1" align="right" >代付流水号：</td>
                <td><input type="text" id="tseq" name="tseq"   size="20"/></td>
            </tr>
            <tr>
            <td class="th1" bgcolor="#D9DFEE" align="right" >商户状态：</td>
             <td align="left" width="35%">
                     <select style="width: 150px" id="mstate" name="mstate">
                    <option value="">全部...</option>
                </select>
                </td>
               <td  class="th1" align="right">系统日期： </td>
               <td>
             <input id="bdate" size="15px"
                    name="bdate" class="Wdate" type="text" onfocus="ryt_area_date('bdate','edate',0,30,0)" />
                    &nbsp;至&nbsp;
                    <input id="edate" size="15px" name="edate" class="Wdate" type="text" disabled="disabled" />
               </td>
            </tr>
            <tr>
	            <td  class="th1" align="right">处理状态：</td>
	            <td colspan="3">
	            	<select style="width: 150px" id="againPayStatus" name="againPayStatus">
	            		<option selected="selected">全部</option>
	            		<option value="2">申请成功</option>
	            		<option value="5">申请撤销</option>
	            		<option value="3">审核成功</option>
	            		<option value="4">审核撤销</option>
	            	</select>
	            </td>
            </tr>
            <tr>
            	<td colspan="4" align="center">
            		<input class="button" type="button" value = " 查  询 " onclick="queryData(1)"/>
            		<input class="button" type="button" style="width:100px;" value = "下 载Excel" onclick="downSGDFData()"/>
            	</td>
            </tr>
        </table>
       </form>
       <table  class="tablelist tablelist2" id="fxpzTable" >
           <thead>
           <tr>
             <th>代付流水号</th><th>交易类型</th><th>批次号</th><!-- <th>支付银行</th> --><th>商户号</th><th>商户简称</th><th>商户订单号</th>
		    <th>支付渠道</th><th>交易金额(元)</th><th>交易手续费(元)</th><th>付款金额(元)</th><th>收款银行</th><th>收款账户名</th><th>收款账号</th>
		    <th>交易状态</th><th>失败原因</th><th>操作</th>
           </tr>
           </thead>
           <tbody id="resultList">
           </tbody>
       </table>
      </div>   
    </body>
</html>
