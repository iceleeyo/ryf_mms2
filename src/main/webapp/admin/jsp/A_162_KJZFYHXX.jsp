<%@ page language="java" pageEncoding="UTF-8" %>
<% String path = request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>快捷支付信息查询</title>
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
        <script type="text/javascript" src='../../dwr/interface/QkRiskService.js?<%=rand%>'></script> 
		<script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
		<script type="text/javascript" src='../../public/js/adminAccount/admin_qk_info.js?<%=rand%>'></script>

    </head>
    <body onload="init();">
    <div class="style">
        <form name="MERTLOG"  method="post" action="">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="6">&nbsp;&nbsp;&nbsp;&nbsp;快捷支付信息查询&nbsp;&nbsp;</td></tr>
            
            <tr>
                <td class="th1" align="right" width="10%" >商户号：</td>
                <td align="left" width="30%">
                <input type="text" id="mid" name="mid" style="width:150px;" size="20px" maxlength="20" />

                 </td>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="11%">手机号：</td>
                <td align="left" width="20%">
                <input type="text" id="phone_no" name="phone_no" size="20px" maxlength="20" style="width:150px;"/>
                </td>
                
                <td class="th1" bgcolor="#D9DFEE" align="right" width="11%">卡号：</td>
                <td align="left" width="20%">
                <input type="text" id="card_no" name="card_no" size="30px" maxlength="30" style="width:150px;"/>
                </td>
            </tr>
            
           <tr >
                   <!--  <td align="right"  class="th1">&nbsp; <b>日期：</b></td>
                    <td align="left">
                            <input id="beginDate" name="beginDate" class="Wdate" type="text" value="" onfocus="WdatePicker({skin:'ext',minDate:'2001-12-20',maxDate:'%y-%M-{%d-1}',dateFmt:'yyyyMMdd',readOnly:'true'});" /> <font color="red">*</font>&nbsp;  至&nbsp;&nbsp;
                            <input id="endDate" name="endDate" class="Wdate" type="text" value="" onfocus="WdatePicker({skin:'ext',minDate:'#F{$dp.$D(\'beginDate\')}',maxDate:'%y-%M-{%d-1}',dateFmt:'yyyyMMdd',readOnly:'true'});" />&nbsp; <font color="red">*</font>
                    </td> -->
                <td class="th1" align="right" >系统日期：</td>
           		<td align="left" width="30%">
               			<input id="beginDate" name="beginDate"  value=""  class="Wdate" type="text" onfocus="ryt_area_date('beginDate','endDate',0,90,0)"/>&nbsp;至
               			<input id="endDate" name="endDate"  value="" class="Wdate" type="text" disabled="disabled"/>
               			<font color="red">*</font>
           		</td>
                <td class="th1" align="right" width="10%" >数据来源：</td>
                <td align="left" width="30%">
                <input type="text" id="abbrev" name="abbrev" style="width:150px;" size="20px" maxlength="20" />

                 </td>
                 
                  <td class="th1" bgcolor="#D9DFEE" align="right">是否解绑：</td>
                <td align="left">
                	<select id="type" name = "type">
                		<option value="0">未解绑</option>
                		<option value="1">已解绑</option>
                	</select>
                </td>
                
                <!--  <td class="th1" align="right" ></td>
                <td align="left" >
                 </td> -->
               </tr>
          
            <tr>
            <td colspan="6" align="center" style="height: 30px">
                <input type="hidden"  name="queryType" id="queryType" value="MERTLOG" />
                <input class="button" type="button"value = "查 询" onclick="queryQkCardInfos(1);" />
               
                <input class="button" type="button" value= "下载XLS" onclick = "downloadQk();"/>
            </td>
            </tr>
        </table>
       </form>

       <table  class="tablelist tablelist2" id="merTodayTable" style="display:none;" >
           <thead>
           <tr>
             <th>商户号</th><th>用户平台ID</th><th>姓名</th>
             <th>身份证号</th><th>开户银行</th>
             <th>银行卡号</th><th>手机号</th>
             <th>开通快捷日期</th><th>数据来源</th>
             <th>卡绑定来源</th>
           </tr>
           </thead>
           <tbody id="resultList"></tbody>
       </table>
       
		<%@include file="qkDetailTable.jsp" %>
       
        </div>
    </body>
</html>
