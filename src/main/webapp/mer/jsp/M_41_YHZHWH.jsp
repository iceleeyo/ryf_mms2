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
        <link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css" />
        <script type="text/javascript" src="../../dwr/engine.js?<%=rand %>"></script>
        <script type="text/javascript" src="../../dwr/util.js?<%=rand %>"></script>
        <script type="text/javascript" src='../../dwr/interface/MerZHService.js?<%=rand%>'></script> 
        <script type="text/javascript" src="../../dwr/interface/MerchantService.js?<%=rand%>"></script> 
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type="text/javascript" src="../../public/js/merchant/mer_yhzhwh.js?<%=rand%>"></script>
        <script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
 		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
    </head>
    <body  onload="init()">
    <div class="style">
        <form name="MERTLOG"  method="post" action="">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="2">&nbsp;&nbsp;&nbsp; 银行账户信息维护&nbsp;&nbsp; <span style=color:red>*</span>(为必填项)</td></tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">开户银行账户名称：</td>
                <td align="left"  >
               	 <input type="hidden"  name="mid" id="mid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}" />
               	 <input type="hidden"  name="uid" id="uid" value="" />
               	 <input type="text"  name="acc_name" id="acc_name" value="" size="20" disabled="disabled"/>
               </td>
                
            </tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">开户银行：</td>
                <td align="left"  >
               <select id="bk_name" style="width: 150px">
               <option value="">请选择...</option>
               </select><font class="redPoint">*</font>    &nbsp;&nbsp;&nbsp;&nbsp;目前只显示 B2B交易银行
               </td>
            </tr>
            
            
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">开户银行省份：</td>
                <td align="left"  >
               <select id="prov_id" style="width: 150px">
               </select><font class="redPoint">*</font>
               </td>
            </tr>
            
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">银行账号：</td>
                <td align="left"  >
                <input type="text"  name="acc_no" id="acc_no" value="" size="20" maxlength="30"/><font class="redPoint">*</font>
               </td>
            </tr>
            
            <tr>
            <td colspan="2" align="center" style="height: 30px">
                <input class="button" type="button" value = " 增 加 " onclick="add();" />
            </td>
            </tr>
        </table>
       </form>
    
       <table  class="tablelist tablelist2" id="yhzhwhTable" >
           <thead>
           <tr>
             <th>开户银行账户名称</th><th>开户银行</th><th>开户银行省份</th><th>开户银行账号</th> <th>操作</th>
           </tr>
           </thead>
           <tbody id="resultList">
           </tbody>
       </table>
       <table id="alterUserBkAccTable" class="tableBorder"
			style="display: none; width: 600px; height: 220px; margin-top: 0px;">
			<tr>
				<td class="th1" align="right">&nbsp; 开户银行账户名称：</td>
				<td  align="left"><input type="text" id="a_acc_name" name="a_acc_name" size="20" disabled="disabled" />
				</td>
			</tr>
			<tr>
				<td align="right" class="th1">开户银行：</td>
				<td align="left" width="">
					<select style="width: 150px" id="a_bk_name"  >
						<option value="">请选择...</option>
					</select>
				</td>
			</tr>
			<tr>
				<td align="right" class="th1">开户银行省份：</td>
				<td align="left">
					<select style="width: 150px" id="a_prov_id"  >
						<option value="">请选择...</option>
					</select>
				</td>
			</tr>
			<tr>
				<td class="th1" align="right">&nbsp; 开户银行账号：</td>
				<td  align="left"><input type="text" id="a_acc_no"   size="20"  maxlength="30"/>
				<input type="hidden"  name="b_acc_no" id="b_acc_no" value="" /></td>
			</tr>
			
			<tr>
				<td colspan="4" align="center" height="20px;">
					<input value="修改" type="button" class="button" onclick="edit();" />
					<input value="返回" type="button" class="wBox_close button" />
				</td>
			</tr>
		</table>
       
       
      </div>  
      
       
    </body>
</html>

           
           
    
