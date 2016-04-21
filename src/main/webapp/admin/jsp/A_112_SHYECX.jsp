<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>商户余额查询</title>
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
        <script type="text/javascript" src="../../dwr/engine.js?<%=rand %>"></script>
        <script type="text/javascript" src="../../dwr/util.js?<%=rand %>"></script>
        <script type="text/javascript" src='../../dwr/interface/AdminZHService.js?<%=rand%>'></script> 
        <script type="text/javascript" src="../../public/js/adminAccount/admin_zhjylscx.js?<%=rand%>"></script>
 		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
 		<script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
    </head>
    <body onload="initSH();">
     <div class="style">
    <table class="tableBorder" >
     <tbody>
        <tr><td class="title" colspan="6"> &nbsp; 商户余额查询</td></tr>
        <tr>
        	 <td class="th1" bgcolor="#D9DFEE" align="right" width="20%">商户号：</td>
                <td align="left" colspan="2" >
               <input type="text" id="mid" name="mid" style="width: 150px;"   value="" onkeyup="checkMidInput(this);"/>
                 <!--  <select style="width: 150px" id="smid" name="smid" onchange="midsChange();">
                     <option value="">全部...</option>
                   </select> -->
               </td>  
              <td class="th1" bgcolor="#D9DFEE" align="right" width="20%">商户类型：</td>
               <td align="left" colspan="2" >
              <select style="width: 150px" id="mstate" name="mstate">
                     <option value="">全部...</option>
                   </select> 
               </td>
        </tr>
        <tr><td  colspan="6" align="center" >
        	<input type="button" class="button" value="查询" onclick="querySHYE();"/>
        	</td>
        </tr>
    </tbody>
   </table><br/>
       <table  class="tablelist tablelist2" style="display: none" id="shinfo">
           <thead>
           <tr>
             <th>商户号</th><th>账户号</th><th>账户名称</th><th>注册日期</th>
		    <th>状态</th>
		    <th>余额(元)</th><th >可用余额(元)</th>
           </tr>
           </thead>
           <tbody id="resultList">
           </tbody>
       </table>
      </div>
    </body>
</html>