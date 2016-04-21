<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>支付密码重置</title>
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
        <script type="text/javascript" src="../../public/js/adminAccount/admin_zhxxwh.js?<%=rand%>"></script>
 		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
 		<script type='text/javascript' src='../../public/js/md5.js'></script>
 		<script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
 		<script type="text/javascript" src='../../dwr/interface/MerchantService.js'></script>
 		<script type='text/javascript' src='../../public/js/ryt_util.js?<%=rand%>'></script>
    </head>
    <body onload="init();">
    <div class="style">
        <form name="MERTLOG"  method="post" action="">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;支付密码重置&nbsp;&nbsp;</td></tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">商户号：</td>
                <td align="left"  >
               <input type="text" id="mid" name="mid" style="width: 150px;"  onkeyup="checkMidInput(this);"/>
                 <!--  <select style="width: 150px" id="smid" name="smid" onchange="midsChange();">
                     <option value="">全部...</option>
                   </select> -->
               </td>
               
            </tr>
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">密码：</td>
                <td align="left"  >
               <input type="password" id="pwd" name="pwd"  size="22" />  
               </td>
            </tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">确认密码：</td>
                <td align="left"  >
               <input type="password" id="vnpwd" name="vnpwd"  size="22" />  
               </td>
            </tr>
             <tr>
             <td colspan="2" align="center">
             <input class="button" type="button" value = "确定" onclick="pwdReset();" />
             <input class="button" type="button" value = "重置" onclick="reset();" />
             </td>
              </tr>
        </table>
       </form>
    
       <table id="zhxxwhTable" style="display:none;" class="tablelist tablelist2" >
           <thead>
	           <tr>
		             <th>选择</th><th>用户ID</th><th>账户ID</th><th>账户名称</th><th>注册日期</th><th>状态</th><th>余额支付功能</th>
		             <th>单笔限额</th><th >月交易限制金额</th><th>同一收款卡号月累计成功次数</th><th>同一收款卡号月累计成功金额</th>
		    <th>充值计费公式</th><th>提现计费公式</th><th>代发计费公式</th><th>代付计费公式</th>
	           </tr>
           </thead>
           <tbody id="resultList">
           </tbody>
             <tr style="margin-top: 10px" >
            <td colspan="15" align="left"><input id="gbzfbut" disabled="disabled" type="button" value= "关 闭账户余额支付功能" size="100" onclick="editZH(1);"/> 
            <input id="gbzh" disabled="disabled" type="button" value= " 关 闭  账 户 " onclick="editZH(2);"/> </td>
       		</tr>
       </table>
     
      </div>   
    </body>
</html>

           
           
    
