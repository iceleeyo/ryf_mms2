<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>客户信息维护</title>
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
        <script type="text/javascript" src="../../public/js/merchant/mer_khxxwh.js?<%=rand%>"></script>
        <script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
 		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
    </head>
    <body  onload="init();">
    <div class="style">
        <form name="MERTLOG"  method="post" action="">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="6">&nbsp;&nbsp; 客户信息维护&nbsp;&nbsp; <span style=color:red>*</span>(为必填项)</td></tr>
            <tr>
            
            	<td class="th1" bgcolor="#D9DFEE" align="right" width="10%" >客户编号：</td>
                <td align="left" width="28%" >  C<input type="text" id="cid" name="cid" value="" size="20" maxlength="10" />
                <input type="hidden"  name="mid" id="mid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}" />
                <span style=color:red>*</span>(唯一,最多10位字符数字组成)</td>
               
                <td class="th1" bgcolor="#D9DFEE" align="right" width="10%" >客户类型：</td>
                <td align="left"   >
                 <select id="ctype" name="ctype" style="width: 150px" onchange="search4TradeType();"> 
               		 <option value=""> 请选择...</option>
                 </select>  <span style=color:red>*</span>   </td>
                <td class="th1" bgcolor="#D9DFEE" align="right"  width="10%">所属行业：</td>
                <td align="left"   >
               <select id="trade_type" name="trade_type" style="width: 150px" >
               <option value=""> 请选择...</option>
               </select> 
               <span style=color:red>*</span> 
               </td>
            </tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right"  >客户/账户名称：</td>
                <td align="left" colspan="5" >
                <input type="text"  name="cname" id="cname" value="" size="30" maxlength="50"/><span style=color:red>*</span> （填写开户银行账户名称,最多包含50个字符）
               </td>
            </tr>
         <!--     <tr >
                <td colspan="6" bgcolor="#C0C9E0" >银行账户信息添加&nbsp;&nbsp;(可不填，或者3项都添加)</td>
            </tr>--> 
            
             <tr>
            
            	<td class="th1" bgcolor="#D9DFEE" align="right"  >开户银行：</td>
                <td align="left"  >
                  <select id="bk_name" name="bk_name" style="width: 150px"> 
                	<option value=""> 请选择...</option>
               	  </select> <span style=color:red>*</span>   </td>
               
                <td class="th1" bgcolor="#D9DFEE" align="right"  >开户银行省份：</td>
                <td align="left"   >
                 <select id="prov_id" name="prov_id" style="width: 150px"> 
                	<option value=""> 请选择...</option>
                 </select><span style=color:red>*</span> </td>
                  
                <td class="th1" bgcolor="#D9DFEE" align="right"  >开户银行帐号：</td>
                <td align="left"   ><input type="text" id="acc_no" name="acc_no" size="20" maxlength="30"/><span style=color:red>*</span> 
               <!-- <input type="button" onclick="addMore();" value = " +增加多个 " />--> 
               </td>
            </tr>
         <!--    <tr >
                <td colspan="6" bgcolor="#C0C9E0" >联系人信息添加&nbsp;&nbsp;(可不填，或者3项都添加)</td>
            </tr>
            
             <tr>
            
            	<td class="th1" bgcolor="#D9DFEE" align="right"  >联系人姓名：</td>
                <td align="left"  >  <input type="text" id="contact" name="contact"  /><span style=color:red>*</span> </td>
               
                <td class="th1" bgcolor="#D9DFEE" align="right"  >职位：</td>
                <td align="left"   ><input type="text" id="position" name="position"  /><span style=color:red>*</span> </td>
                  
                <td class="th1" bgcolor="#D9DFEE" align="right"  >手机号：</td>
                <td align="left"   ><input type="text"  id="cell" name="cell"  /><span style=color:red>*</span> 
               </td>
               
            </tr>
            --> 
            
            <tr>
            <td colspan="6" align="center" style="height: 30px">
                <input class="button" type="button" value = " 增 加 " onclick="addCusInfos();" />
            </td>
            </tr>
            
        </table>
       </form> &nbsp;
        <br/>    
       <table  class="tablelist tablelist2" >
           
           <tr><td colspan="7" align="center">
    		<input  type="button" class="button" value= " 增加银行账户"  onclick="addUserBkAcc(1);"/>
    		<!-- <input  type="button" class="button" value= " 增加联系人"  onclick="addUserBkAcc(2);"/>  -->        
            客户编号：<input  type="text" id="c_cid" name="c_cid"/> <input  type="button" class="button" value= " 查  询"  onclick="search();"/></td>
           </tr>
           <tr>
             <th>客户编号</th><th>类型</th><th>客户/账户名称</th><th>所属行业</th><th>银行账户信息</th>
           </tr>
           
           <tbody id="resultList">
           </tbody>
       </table>
       <!-- 添加开户银行信息表 -->
        <table id="insertUserBkAccTable" class="tableBorder"
			style="display: none; width: 600px; height: 220px; margin-top: 0px;">
			<tr>
				<td class="th1" align="right">&nbsp; 开户银行账户名称：</td>
				<td  align="left"><input type="text" id="i_acc_name"   size="20" value="" disabled="disabled" />
				</td>
			</tr>
			<tr>
				<td align="right" class="th1">开户银行：</td>
				<td align="left" width="">
					<select style="width: 150px" id="i_bk_name"  >
						<option value="">请选择...</option>
					</select>
				</td>
			</tr>
			<tr>
				<td align="right" class="th1">开户银行省份：</td>
				<td align="left">
					<select style="width: 150px" id="i_prov_id"  >
						<option value="">请选择...</option>
					</select>
				</td>
			</tr>
			<tr>
				<td class="th1" align="right">&nbsp; 开户银行账号：</td>
				<td  align="left"><input type="text" id="i_acc_no"   size="20"  maxlength="30" />
				<input type="hidden"  name="i_uid" id="i_uid" value="" />
				</td>
			</tr>
			
			<tr>
				<td colspan="4" align="center" height="20px;">
					<input value="添加" type="button" class="button" onclick="insert4UserBkAcc();" />
					<input value="返回" type="button" class="wBox_close button" />
				</td>
			</tr>
		</table>
		<!--添加联系人信息表 -->
		<table id="insertCusContactInfosTable" class="tableBorder"
			style="display: none; width: 600px; height: 220px; margin-top: 0px;">
			<tr>
				<td class="th1" align="right">&nbsp; 用户ID：</td>
				<td  align="left"><input type="text" id="ii_uid"   size="20" disabled="disabled" />
				</td>
			</tr>
			<tr>
				<td align="right" class="th1">联系人姓名：</td>
				<td  align="left"><input type="text" id="i_contact"  size="20"  /></td>
			</tr>
			<tr>
				<td align="right" class="th1">职位：</td>
				<td  align="left"><input type="text" id="i_position"   size="20"  /></td>
			</tr>
			<tr>
				<td class="th1" align="right">&nbsp; 手机号：</td>
				<td  align="left"><input type="text" id="i_cell"   size="20"  /></td>
			</tr>
			
			<tr>
				<td colspan="4" align="center" height="20px;">
					<input value="添加" type="button" class="button" onclick="insert4CusContactInfos();" />
					<input value="返回" type="button" class="wBox_close button" />
				</td>
			</tr>
		</table>
		<!--修改联系人信息表 -->
		<table id="alterCusContactInfosTable" class="tableBorder"
			style="display: none; width: 600px; height: 220px; margin-top: 0px;">
			<tr>
				<td class="th1" align="right">&nbsp; 用户ID：</td>
				<td  align="left"><input type="text" id="a_uid"   size="20" disabled="disabled" />
				<input type="hidden"    id="a_id" value="" /></td>
			</tr>
			<tr>
				<td align="right" class="th1">联系人姓名：</td>
				<td  align="left"><input type="text" id="a_contact"   size="20"  /></td>
			</tr>
			<tr>
				<td align="right" class="th1">职位：</td>
				<td  align="left"><input type="text" id="a_position"   size="20"  /></td>
			</tr>
			<tr>
				<td class="th1" align="right">&nbsp; 手机号：</td>
				<td  align="left"><input type="text" id="a_cell"   size="20"  /></td>
			</tr>
			
			<tr>
				<td colspan="4" align="center" height="20px;">
					<input value="修改" type="button" class="button" onclick="edit4CusContactInfos();" />
					<input value="返回" type="button" class="wBox_close button" />
				</td>
			</tr>
		</table>
       <!-- 修改开户银行信息表 -->
       <table id="alterUserBkAccTable" class="tableBorder"
			style="display: none; width: 600px; height: 220px; margin-top: 0px;">
			<tr>
				<td class="th1" align="right">&nbsp; 开户银行账户名称：</td>
				<td  align="left"><input type="text" id="a_acc_name"   size="20" value="" disabled="disabled" />
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
				<input type="hidden"  name="uid" id="uid" value="" />
				<input type="hidden"  name="b_acc_no" id="b_acc_no" value="" /></td>
			</tr>
			
			<tr>
				<td colspan="4" align="center" height="20px;">
					<input value="修改" type="button" class="button" onclick="edit4UserBkAcc();" />
					<input value="返回" type="button" class="wBox_close button" />
				</td>
			</tr>
		</table>
       
      </div>  
      
       
    </body>
</html>

           
           
    
