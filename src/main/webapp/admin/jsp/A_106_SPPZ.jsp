<%@ page language="java" pageEncoding="UTF-8" %>
<% String path = request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>审批凭证</title>
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
        <script type="text/javascript" src="../../dwr/engine.js?<%=rand %>"></script>
        <script type="text/javascript" src="../../dwr/util.js?<%=rand %>"></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/MerZHService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/AdminZHService.js?<%=rand%>'></script> 
        <script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
        <script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
        <script type="text/javascript" src="../../public/js/adminAccount/admin_spxxscpz.js?<%=rand%>"></script>
 		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>

    </head>
    <body onload="init();">
    <div class="style">
        <form name="MERTLOG"  method="post" action="">
         <input type="hidden" id="trans_flow" />
         <input type="hidden" id="oid" />
         <input type="hidden" id="aid" />
         <input type="hidden" id="state" />
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="4">&nbsp;&nbsp;&nbsp;&nbsp;线下充值凭证审批&nbsp;&nbsp;
            </td></tr>           
            <tr>
               <td class="th1" align="right" width="10%">商户号：</td>
                <td align="left" width="30%">
                <input type="text" id="mid" name="mid" style="width: 150px;" onchange="query4ZH();" onkeyup="checkMidInput(this);"/>
                  <!-- <select style="width: 150px" id="smid" name="smid"  onchange="initMidInput(this.value);query4ZH();">
                     <option value="">请选择...</option>
                   </select> -->
                 </td>
                 
                <td class="th1" bgcolor="#D9DFEE" align="right" width="15%">交易类型：</td>
                <td align="left" width="35%">
                <select style="width: 150px" id="ptype" name="ptype">
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
                 
               <td class="th1" bgcolor="#D9DFEE" align="right" width="15%">商户状态：</td>
               
               <td><select style="width: 150px" id="mstate" name="mstate">
                    <option value="">全部...</option>
                </select></td>            
            </tr>
            <tr>
            <td colspan="4" align="center" style="height: 30px">
                <input type="hidden"  name="queryType" id="queryType" value="MERTLOG" />
                <input class="button" type="button" value = " 查 询 " onclick="queryMX_A_SPPZ(1);" />
            </td>
            </tr>
        </table>
              <%@include file="show_Pic.jsp" %>
       </form>

       <table  class="tablelist tablelist2" id="mxTable" style="display: none;">
           <thead>
           <tr>
             <th>账户号</th><th>账户名称</th><th>交易总金额(元)</th><th>交易手续费(元)</th>
             <th>结算总金额(元)</th><th>批次号</th><th>订单数量</th><th>付款凭证</th><th>系统日期</th>
             <th>交易类型</th><th>操作</th>
           </tr>
           </thead>
           <tbody id="resultList">
           </tbody>
       </table>
        </div>
    </body>
</html>
