<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>银企直连网关维护</title>
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
        <script type="text/javascript" src="../../public/js/adminAccount/admin_yqzlwgwh.js?<%=rand%>"></script>
        <script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
 		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
 		<script type="text/javascript" src="../../public/js/ryt_util.js?<%=rand%>"></script>
 		<script type="text/javascript" src="../../dwr/interface/YQGateService.js?<%=rand%>'>"></script>
 		<style type="test/css">
 			#addB2EGateTable td{
 					height:30px;
 			}
 		
 		</style>
    </head>
    <body  onload="init()">
    <div class="style">
     
        <form name="MERTLOG"  method="post" action="">
         <table width="100%"  align="left"  class="tableBorder">
         
            <tr><td class="title" colspan="8">&nbsp;&nbsp; 银企直连网关维护&nbsp;&nbsp; <span style=color:red>*</span>(为必填项)</td></tr>
           	<tr>
           		<td class=th1 bgcolor=#D9DFEE align="right" width="25%">开户账户号：</td>
           		<td alight="left" width="25%"><input type="text" id="account" name="account" size="30"/></td>
           		<td alight="left" width="50%">
           			<input type="button" id="query" name="query" value="查询" class="button" style="margin-left: 8em" onclick="queryClick();" />
           			<input type="button" id="addNewGate" name="addNewGate" value="新增" class="button"  style="margin-left:8em" onclick="addClick();" />
           		</td>
           	</tr>
         
        </table>
       </form>
   
       <table  class="tablelist tablelist2" id="yqzlwgwhTable" >
           <thead>
           <tr>
             <th>网关号</th><th>网关名称</th><th>前置机地址</th><th>银行行号</th><th>开户省份</th> 
             <th>开户帐号</th><th>开户名称</th><th>企业前置机ip</th><th>企业客户编码</th><th>企业操作员代码</th> 
             <th>登录密码</th><th>字符编码</th><th>协议编号</th><th>操作</th>
           </tr>
           </thead>
           <tbody id="resultList">
           </tbody>
       </table>
       <table id="alterB2EGateTable" class="tableBorder"
			style="display:none; width: 600px; height: 220px; margin-top: 0px;">
			 <tr>
          	  <td class="th1" bgcolor="#D9DFEE" align="right"  width="35%">网关号：</td>
                <td align="left"   ><input type="text" id="a_gid"  size="11" maxlength="11" disabled="disabled"/>
               </td>
               </tr>
                <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="10%" >网关名称：</td>
                <td align="left"   ><input type="text" id="a_name"  size="40" maxlength="40"/>  
               </td>
               </tr>
                <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right"  width="10%">前置机地址：</td>
                <td align="left"   ><input type="text" id="a_nc_url" /> 
               </td>
            </tr>
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right"  width="10%">银行行号：</td>
                <td align="left"   ><input type="text" id="a_bk_no"   size="26" maxlength="14"/> 
               </td>
            </tr>
            
             <tr>
              <td class="th1" bgcolor="#D9DFEE" align="right"  >开户省份：</td>
                <td align="left"   >
                 <select id="a_prov_id"   style="width: 193px" > 
                	<option value=""> 请选择...</option>
                 </select><span style=color:red>*</span> </td>
                 </tr>
                  <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right"  >开户帐号：</td>
                <td align="left"   ><input type="text" id="a_acc_no"  size="26" maxlength="40"/>  
               </td></tr>
                <tr>
               <td class="th1" bgcolor="#D9DFEE" align="right"  >开户名称：</td>
               <td align="left"   ><input type="text" id="a_acc_name"   size="26" maxlength="50"/>  </td>
              </tr>
            
              <tr>
               	  <td class="th1" bgcolor="#D9DFEE" align="right"  >企业前置机ip：</td>
                <td align="left"   ><input type="text" id="a_termid"   size="26" maxlength="20"/> 
               </td>
               </tr>
               <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right"  > 企业客户编码：</td>
                <td align="left"   ><input type="text" id="a_corp_no"   size="26" maxlength="20"/> 
               </td>
               </tr>
               <tr>
               <td class="th1" bgcolor="#D9DFEE" align="right"  >企业操作员代码：</td>
                <td align="left"   ><input type="text" id="a_user_no"   size="26" maxlength="20"/>  </td>
            </tr>
             <tr>
               <td class="th1" bgcolor="#D9DFEE" align="right"  >登录密码：</td>
                <td align="left"   ><input type="text" id="a_user_pwd"  size="26" maxlength="30"/>  </td>
            </tr>
             <tr>
               <td class="th1" bgcolor="#D9DFEE" align="right"  >字符编码：</td>
                <td align="left"   ><input type="text" id="a_code_type"  size="26" maxlength="10"/>  </td>
            </tr>
             <tr>
               <td class="th1" bgcolor="#D9DFEE" align="right"  >协议编号：</td>
                <td align="left"   ><input type="text" id="a_busi_no"   size="26" maxlength="20"/>  </td>
            </tr>
			<tr>
				<td colspan="4" align="center" height="20px;">
					<input value="修改" type="button" class="button" onclick="edit();" />
					<input value="返回" type="button" class="wBox_close button" />
				</td>
			</tr>
		</table>
		
		
		 <table id="addB2EGateTable" class="tableBorder"
			style="display:none; width: 600px; height: 220px; margin-top: 0px;">
			
			 <tr><td class="th1" bgcolor="#D9DFEE" align="right"  width="30%">网关号：</td>
			 	 <td align="left"   ><input type="text" id="gid" name="gid" size="26" maxlength="11"/><span style=color:red>*</span> </td>
			 </tr>
			 
             <tr>   <td class="th1" bgcolor="#D9DFEE" align="right" width="30%" >网关名称：</td>
             		<td align="left"   ><input type="text" id="name" name="name" size="26" maxlength="40" /><span style=color:red>*</span>  </td>
             </tr>
             
             <tr> <td class="th1" bgcolor="#D9DFEE" align="right"  width="30%">前置机地址：</td>
             	  <td align="left"   ><input type="text" id="nc_url" name="nc_url" size="26"/> <span style=color:red>*</span>  </td>
             </tr>
             
             <tr> <td class="th1" bgcolor="#D9DFEE" align="right"  width="30%">银行行号：</td>
             	  <td align="left"   ><input type="text" id="bk_no" name="bk_no" size="26" maxlength="14"/> <span style=color:red>*</span>  </td>
             </tr>           		
           	
             <tr><td class="th1" bgcolor="#D9DFEE" align="right" width="30%"  >开户省份：</td>
               	<td align="left"   >
		                <select id="prov_id" name="prov_id" style="width: 193px" > 
		               	<option value=""> 请选择...</option>
		                </select><span style=color:red>*</span> 
                </td>
              </tr>
              
              <tr><td class="th1" bgcolor="#D9DFEE" align="right" width="30%"  >开户帐号：</td>
              	  <td align="left"   ><input type="text" id="acc_no" name="acc_no" size="26" maxlength="29"/>  <span style=color:red>*</span> </td>
              </tr>
              
              <tr><td class="th1" bgcolor="#D9DFEE" align="right"  width="30%" >开户名称：</td>
              	  <td align="left"   ><input type="text" id="acc_name" name="acc_name" size="26" maxlength="50"/> <span style=color:red>*</span>  </td>
              </tr>
              
              <tr><td class="th1" bgcolor="#D9DFEE" align="right"  width="30%" >企业前置机ip：</td>
              	  <td align="left"   ><input type="text" id="termid" name="termid" maxlength="20" size="26"/>  </td>
              </tr>
              
              <tr><td class="th1" bgcolor="#D9DFEE" align="right" width="30%" > 企业客户编码：</td>
              	  <td align="left"   ><input type="text" id="corp_no" name="corp_no" maxlength="20" size="26"/>  </td>
              </tr>
              
              <tr><td class="th1" bgcolor="#D9DFEE" align="right" width="30%" >企业操作员代码：</td>
              	<td align="left"   ><input type="text" id="user_no" name="user_no" maxlength="20" size="26"/>   </td>
              </tr>
              
              <tr><td class="th1" bgcolor="#D9DFEE" align="right" width="30%" >登录密码：</td>
              	<td align="left"   ><input type="text" id="log_pwd" name="log_pwd" maxlength="30" size="26"/>  </td>
              </tr>
              
              <tr><td class="th1" bgcolor="#D9DFEE" align="right" width="30%" >字符编码：</td>
              	<td align="left"   ><input type="text" id="encode" name="encode" maxlength="10" size="26"/>   </td>
              </tr>
            	
              <tr><td class="th1" bgcolor="#D9DFEE" align="right" width="30%" >协议编号：</td>
              	<td align="left"   ><input type="text" id="protocol" name="protocol" maxlength="20" size="26"/>   </td>
              </tr>
            <tr>
	            <td colspan="2" align="center"  >
	                <input class="button" type="button" value = " 增 加 " onclick="addB2EGate();" />
	                <input value="返回" type="button" class="wBox_close button" />
	            </td>
           </tr>
		</table>
      </div>  
    </body>
</html>

           
           
    
