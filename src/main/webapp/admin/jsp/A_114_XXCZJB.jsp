<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>线下充值经办</title>
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
        <script type="text/javascript" src="../../dwr/interface/DaiFuService.js"></script>
        <script type="text/javascript" src='../../dwr/interface/MerZHService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/AdminZHService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
        <script type="text/javascript" src="../../public/js/adminAccount/admin_dbjb.js?<%=rand%>"></script>
    </head>
    
    <body onload="initCZJB();">
    <div class="style">
        <form name="MERTLOG"  method="post" action="">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="5">&nbsp;&nbsp;&nbsp;&nbsp;线下充值经办&nbsp;&nbsp;</td></tr>
             <tr>
               <td class="th1" align="right" width="12%">商户号：</td>
                <td align="left" width="28%">
                <input type="text" id="mid" name="mid" style="width: 150px;"   onkeyup="checkMidInput(this);"/>
                 <!--  <select style="width: 150px" id="smid" name="smid"  onchange="initMidInput(this.value);">
                     <option value="">请选择...</option>
                   </select> --> </td>
                    <td class="th1" align="right" width="12%">商户状态：</td> 
                    <td align="left" width="28%">
                <select style="width: 150px" id="mstate" name="mstate">
                     <option value="">全部...</option>
                   </select> </td>
                <td align="left"  width="75%">
         显示最近：&nbsp;
            <select id="num">
			           <option value = "15">15</option>
			           <option value = "30">30</option>
			           <option value = "40">40</option>
			           <option value = "100">100</option>
		           </select>&nbsp;条。
		           
               </td>
               
            </tr>
            <tr>
            	<td colspan="5" align="center">
            		<input class="button" type="button" value = " 查  询 " onclick="queryXXCZJingBan()"/>
            	</td>
            </tr>
        </table>
       </form>
       <table  class="tablelist tablelist2" id="fxpzTable" >
           <thead>
           <tr>
              <th>操作</th><th>系统订单号</th><th>交易类型</th><th>用户</th><th>账户ID</th><th>账户名称</th>
		    <th>充值金额(元)</th>
           </tr>
           </thead>
           <tbody id="resultList">
           </tbody>
       </table>
          <%@include file="XXCZDFJBOption.jsp" %>
      </div>   
    </body>
</html>
