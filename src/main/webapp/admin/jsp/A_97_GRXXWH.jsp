<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>个人信息维护</title>
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
        <script type="text/javascript" src="../../public/js/adminAccount/admin_grxxwh.js?<%=rand%>"></script>
 		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
    </head>
    <body >
    <div class="style">
        <form name="MERTLOG"  method="post" action="">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="8">&nbsp;&nbsp;&nbsp;&nbsp;个人信息维护&nbsp;&nbsp;</td></tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="20%">用户：</td>
                <td align="left"  >
               <input type="text" id="uid" name="uid"  maxlength="20"/>  
               </td>
               <td class="th1" bgcolor="#D9DFEE" align="right" width="20%">身份证号：</td>
                <td align="left"  >
               <input type="text" id="id_no" name="id_no"  maxlength="20"/>  
               </td>
            </tr>
            
            <tr>
            <td colspan="8" align="center" style="height: 30px">
                <input type="button" class="button"   value="查 询" onclick="queryGR(1);"/>
                
            </td>
            </tr>
        </table>
       </form>
    
       <table  id="grxxwhTable" class="tablelist tablelist2" style="display: none;">
           <thead>
           <tr>
            <th>用户</th><th>姓名</th><th>性别</th><th>注册日期</th>
		    
		    <th>身份认证</th><th>银行卡认证</th><th>状态</th><th>联系电话</th><th>住址</th> 
		    
		    <th>预留信息</th>  
           </tr>
           </thead>
           <tbody id="resultList">
           </tbody>
       </table>
       
       <table id="grxxDetail"  class="tableBorder detailBox" style="display: none;">
           <tr>
               <td align="left" class="th1" width="10%" >用户id：</td>
               <td align="left" width="15%" id="v_uid"></td>
               <td align="left" class="th1" width="8%" >姓名：</td>
               <td align="left" width="15%" id="v_name"></td>
               <td align="left" class="th1" width="8%">性别：</td>
               <td align="left" width="15%" id="v_gender"></td>
           </tr>
           <tr >
               <td align="left" class="th1" >注册日期：</td>
               <td align="left" id="v_sys_date"></td>
               <td align="left" class="th1" >国籍：</td>
               <td align="left" id="v_country_code"></td>
               <td align="left" class="th1" >职业：</td>
               <td align="left" id="v_profe"></td>
           </tr>
           <tr >
               <td align="left" class="th1" >证件类型：</td>
               <td align="left" id="v_id_type"></td>
               <td align="left" class="th1" >住址：</td>
               <td align="left" id="v_addr"></td>
               <td align="left" class="th1" >联系电话：</td>
               <td align="left" id="v_tel"></td>
           </tr>
           <tr >
               <td align="left" class="th1" >证件号码：</td>
               <td align="left" id="v_id_no"></td>
               <td align="left" class="th1" >证件有效期：</td>
               <td align="left" id="v_id_exp_date"></td>
               <td align="left" class="th1" >开户银行账号：</td>
               <td align="left" id="v_bank_acc"></td>
           </tr>
           <tr >
               <td align="left" class="th1" >开户银行：</td>
               <td align="left" id="v_gate_id"></td>
               <td align="left" class="th1" >身份证校验：</td>
               <td align="left" id="v_id_verify"></td>
               <td align="left" class="th1" >银行卡校验：</td>
               <td align="left" id="v_bank_acct_verfy"></td>
           </tr>
           <tr >
               <td align="left" class="th1" >状态：</td>
               <td align="left" id="v_tate">
               </td>
               <td align="left" class="th1" >预留信息：</td>
               <td align="left" id="v_wel_msg"></td>
               <td align="left" class="th1" ></td>
               <td align="left" ></td>
           </tr>
          
           <tr><td colspan="6" height="30px"  align="center"> <input type="button"  value = "返回" class="wBox_close button"/>
			<!-- <input type="button" onclick="fanhui()" value = "返  回" class="button"/>--><br/></td></tr>
        </table>
      </div>   
    </body>
    
</html>

           
           
    
