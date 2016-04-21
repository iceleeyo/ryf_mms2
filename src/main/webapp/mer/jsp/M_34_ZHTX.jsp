<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>账户提现</title>
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
        <script type="text/javascript" src="../../dwr/engine.js?<%=rand %>"></script>
        <script type="text/javascript" src="../../dwr/util.js?<%=rand %>"></script>
        <script type="text/javascript" src='../../dwr/interface/MerZHService.js?<%=rand%>'></script> 
        <script type="text/javascript" src="../../public/js/merchant/mer_zhtx.js?<%=rand%>"></script>
 		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
 		 <script type='text/javascript' src="../../public/js/money_util.js?<%=rand%>"></script>
 		<script type="text/javascript" src="../../public/js/md5.js"></script>
 		<script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
    </head>
    <body onload="init();">
    <div class="style">
        <form name="MERTLOG"  method="post" action="">
         <table width="100%"  align="left"  class="tableBorder" id="table1">
            <tr><td class="title" colspan="2">&nbsp;&nbsp;&nbsp; 账户提现 &nbsp;&nbsp;</td></tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">账户：</td>
                <td align="left"  >
             	 <input type="hidden"  name="mid" id="mid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}" />
             	 <select style="width: 180px" id="aid" name="aid">
             	<!--  <option value="">请选择...</option> -->
                    </select>    &nbsp;&nbsp;&nbsp;<input class="button" type="button" value = "选择" onclick="queryZH();" />
             </td>
                
            </tr>
           
        </table>
       </form>
    
    
       <table width="100%"  align="left"   style="display:none" class="tableBorder" id="table2">
            <tr><td class="title" colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;账户提现&nbsp;&nbsp;</td></tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">账户：</td>
                <td align="left">
                <input type="text" id="a_aid" name="a_aid"  size="20" disabled="disabled"   maxlength="50"/>
                </td>
            </tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">可提现金额：</td>
                <td align="left">
                <input type="text" id="balance" name="balance"  size="20" disabled="disabled"    maxlength="20"/>元
                </td>
            </tr>
            
            
           <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">提现金额：</td>
                <td align="left"  >
             	 <input type="text" id="payAmt" name="payAmt" maxlength="14" size="20"/>元&nbsp;&nbsp;<span id="fee"></span>
             	 </td> 
            </tr>
           
            
            <tr>
                <td colspan="2" align="center"><input type="button" class="button" value='确   定 ' onclick="show_surePwd();" /> 
                <input type="button" class="button" value='返  回 ' onclick="fanhui2();" /></td> 
            </tr>
            
            
            
        </table>
      </div> 
           <%@include file="TXSure.jsp" %>
    </body>
    
</html>

           
           
    
