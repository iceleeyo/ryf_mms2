<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>提现处理</title>
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
        <script type="text/javascript" src='../../dwr/interface/AdminZHService.js?<%=rand%>'></script> 
        <script type="text/javascript" src="../../public/js/adminAccount/admin_txcl.js?<%=rand%>"></script>
        <script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
 		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
    </head>
    <body onload="init();">
    <div class="style">
    <div class="style">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="8">&nbsp;&nbsp;&nbsp;&nbsp;提现处理(如果无法进行查询，请联系其他操作员，进行按钮权限分配)&nbsp;&nbsp;</td></tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="10%">商户号：</td>
                <td align="left" colspan="2"  >
              <input type="text" id="mid" name="mid" style="width: 150px;" onkeyup="checkMidInput(this);"/>
                  <!-- <select style="width: 150px" id="smid" name="smid" onchange="initMidInput(this.value)">
                     <option value="">全部...</option>
                   </select> -->
                   </td>
                    <td class="th1" bgcolor="#D9DFEE" align="right" width="10%">商户状态：</td>
                    <td colspan="4"> <select style="width: 150px" id="mstate" name="mstate">
                     <option value="">全部...</option>
                   </select>
          &nbsp;&nbsp;&nbsp; <input type="button" id="searchDJBDD" class="button" style="width: 180px" value = "查询待提现经办订单 "  disabled="disabled"  onclick="search(1,21);"/>
                   
                   </td>
                   
 
              <!--  <input type="button" id="searchDTXDD" class="button" style="width: 180px" value = "查询待提现完成订单 "  disabled="disabled"  onclick="search(1,22);" />
               &nbsp;&nbsp;&nbsp; -->
              <!--  <input type="button" id="downloadSuccessMsg" class="button" style="width: 180px" value= "下载当天提现经办成功的记录" disabled="disabled" onclick="downloadTXCL();"/> --> 
             
            </tr>
        </table> <%@include file="TXJBOption.jsp" %>
</div>    <br/><br />
    
       <table id="txclTable" style="display:none;" class="tablelist tablelist2" >
           <thead>
           <tr>
           <th>选择</th>
            <th>系统订单号</th><th>订单时间</th><th>用户</th><th>帐户</th><th>账户名称</th><th>状态</th>
            <!--个人用户有 --><th>开户银行名称</th><th>开户银行</th><th>开户银行卡号</th><th>提现金额(元)</th> 
           </tr>
           </thead>
           <tbody id="resultList">
           </tbody>
       </table>
         
      
      </div>   
    </body>
</html>

           
           
    
