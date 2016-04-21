<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>个人认证处理</title>
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
    </head>
    <body >
    <div class="style">
        <form name="MERTLOG"  method="post" action="">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="8">&nbsp;&nbsp;&nbsp;&nbsp;个人认证处理&nbsp;&nbsp;</td></tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">用户：</td>
                <td align="left"  >
               <input type="text" id="oid" name="oid"  maxlength="20"/>  
               &nbsp;&nbsp;&nbsp;&nbsp;
               
               <input type="button" value = " 查询 " onclick="" />
               </td>
                
            </tr>
             
        </table>
       </form>
    <br/><br />
    <p> 待认证用户</p>
     <p style="margin-top: 10px">
       
           
           
           	显示：
           	<select>
           	<option>所有</option>
           	<option>身份认证</option>
           	<option>银行卡认证</option>
           	</select>
           
           <input type="button" value= "身份认证通过" /> 
           
           <input type="button" value= "随机注入金额" /> 
           
           <input type="button" value= "自动汇款到银行账户" />
           &nbsp;&nbsp;&nbsp;&nbsp;
           <input type="button" value= "下载身份认证报表 " align="right" /> 
           <input type="button" value= "下载银行卡认证报表 " align="right" />
        
       </p>
       <table  class="tablelist tablelist2" >
           <thead>
           <tr>
             <th><input type="checkbox" name="aa"/></th> <th>用户</th><th>姓名</th><th>性别</th><th>身份证号码</th>
		    <th>证件有效期</th><th>开户银行</th>
		    <th>开户银行卡号</th><th>随机注入金额</th><th>认证类型</th> 
           </tr>
           
           </thead>
           <tbody id="resultList">
           
           <tr>
           <td><input type="checkbox" name="aa"/></td>
           <td>15810319128</td><td>张三</td><td>女</td><td>432423198109128765</td>
           <td>20181231</td>
           <td></td><td></td><td></td>
           <td>身份证认证</td>  
           </tr>
           
           <tr>
           <td><input type="checkbox" name="aa"/></td>
           <td>15810319128</td><td>张三</td><td>女</td><td>432423198109128765</td>
           <td>20181231</td>
           <td>交通银行</td><td>6222600910021234567</td><td>0.01</td>
           <td>银行卡认证</td>
           </tr>
            <tr>
           <td><input type="checkbox" name="aa"/></td>
           <td>15810319128</td><td>张三</td><td>女</td><td>432423198109128765</td>
           <td>20181231</td>
           <td></td><td></td><td></td>
           <td>身份证认证</td>
           </tr>
           
           <tr>
           <td><input type="checkbox" name="aa"/></td>
           <td>15810319128</td><td>张三</td><td>女</td><td>432423198109128765</td>
           <td>20181231</td>
           <td>交通银行</td><td>6222600910021234567</td><td>0.07</td>
           <td>银行卡认证</td>
           </tr>
           
           </tbody>
       </table>
      
      </div>   
    </body>
</html>

           
           
    
