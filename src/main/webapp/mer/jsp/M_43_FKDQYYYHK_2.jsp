<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>银行账户维护</title>
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
    </head>
    <body  >
    <div class="style">
        <form name="MERTLOG"  method="post" action="M_43_FKDQYYYHK_3.jsp">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="8">&nbsp;&nbsp;&nbsp;&nbsp;付款.dddd...&nbsp;&nbsp; <span style=color:red>*</span>(为必填项)</td></tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">订单号：</td>
                <td align="left"  >
                 13209876787878
               </td>
                
            </tr>
            
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">付款金额：</td>
                <td align="left"  >
                <font color="red"> 10000</font>
               </td>
                
            </tr>
            
            
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">手续费：</td>
                <td align="left"  >
                <font color="red">10</font>
               </td>
                
            </tr>
            
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">应付金额：</td>
                <td align="left"  >
                 10000
                 <font color="red">九百九十九元</font>
               </td>
                
            </tr>
            
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">对方企业客户名称：</td>
                <td align="left"  >
                北京融易通技术有限公司
               </td>
            </tr>
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">对方银行名：</td>
                <td align="left"  >交通银行
               </td>
            </tr>
            
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">对方银行账号：</td>
                <td align="left"  >
               595423676943727835
               </td>
            </tr>
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">对方开户银行省份：</td>
                <td align="left"  >
               北京
               </td>
            </tr>
            
          
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">  我方手机号：</td>
                <td align="left"  >
                13436612929,15610310198
                
               </td>
            </tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">对方手机号：</td>
                <td align="left"  >
               13436612929,15610310198
               </td>
            </tr>
            
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">订单描述：</td>
                <td align="left"  >
                在山东分舵是否
                
               </td>
            </tr>
            
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">付款银行：</td>
                <td align="left"  >
                <img src="<%=request.getContextPath() %>/images/banklogo/20000.png" alt="" /><br/>
                
               </td>
            </tr>
            
            
            <tr>
            <td colspan="8" align="center" style="height: 30px">
                <input class="button" type="submit" value = " 去网银页面支付 "  />
            </td>
            </tr>
        </table>
       </form>
    
       
      </div>  
      
       
    </body>
</html>

           
           
    
