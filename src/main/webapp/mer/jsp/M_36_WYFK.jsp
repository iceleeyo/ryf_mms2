<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>付款到电银账户</title>
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
        <script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
        <script type="text/javascript" src="../../public/js/merchant/mer_wyfk.js?<%=rand%>"></script>
 		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
 		<script type="text/javascript" src="../../public/js/md5.js"></script>
    </head>
    <body onload=init();>
    <div class="style" >
         <table width="100%"  align="left"  class="tableBorder" id="table1" >
            <tr><td class="title" colspan="8">&nbsp;&nbsp;&nbsp;&nbsp;付款到电银账户&nbsp;&nbsp;</td></tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">转出账户号：</td>
                <td align="left"  >
                <input type="hidden"  name="mid" id="mid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}" />
	             	 <select style="width: 180px" id="aid" name="aid">
	             		 <option value="">请选择...</option>
	                 </select>  
	                 <span style=color:red>*</span>(转出账户显示的是状态为正常的账户) 
             </td>
            </tr>
            
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">对方电银用户：</td>
                <td align="left"  >
             	  <input type="text" id="toUid" name="toUid" size="24" maxlength="11"/> 
             	  <span style=color:red>*</span>(个人账户填写手机号，企业账户填写商户号)
             </td>
             
            </tr>
            <tr>
            <td colspan="2" align="center">&nbsp;&nbsp;&nbsp;
            <input  type="button" class="button" value = " 确 定  " onclick="queryFK();" />
             	   </td>
            </tr>
           
        </table>
     
      <table  class="tablelist tablelist2" id="mxTable" style="display:;">
           <tr><th style="background-color: rgb(34,182,193)" align="left"  colspan="18" >&nbsp;&nbsp;待付款订单</th></tr>
           <tr>
             <th>账户号</th><th>账户名称</th><th>系统订单号</th><th>交易金额</th><th>结算金额</th>
             <th>交易状态</th>
             <th>交易类型</th><th>交易对方</th><th>订单时间</th><th>操作</th>
           </tr>
           
           <tbody id="resultList">
           </tbody>
       </table>
      
      
    
       <table width="100%"  align="left"  style="display: none;" class="tableBorder" id="table2">
            <tr><td class="title" colspan="8">&nbsp;&nbsp;&nbsp;&nbsp;我要付款&nbsp;&nbsp;</td></tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">转出账户：</td>
                <td align="left">
                <input type="text" id="a_aid"    size="24" disabled="disabled"   maxlength="50"/>
                </td>
                
            </tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">可用金额：</td>
                <td align="left">
                <input type="text" id="balance"    size="24" disabled="disabled"   maxlength="20"/>元
                </td> 
            </tr>
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">付款金额：</td>
                <td align="left"  >
             	 <input type="text" id="payAmt"  maxlength="11" size="24"/>元<span style=color:red>*</span>&nbsp;&nbsp;
             	 </td> 
             	 
            </tr>
            
           <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">对方融易付账户号：</td>
                <td align="left"  >
                <select style="width: 180px" id="toAid" name="toAid">
                </select> 
                <span style=color:red>*</span>(对方账户显示的是状态为正常的账户)
                </td> 
            </tr>
            
            <tr>
                <td colspan="2" align="center">
                <input type="button" class="button" value=' 确   定 '  onclick="queryFK2();"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                 <input type="button" class="button" value=' 返   回 '  onclick="fanHui();"/>
                </td> 
            </tr>
            
        </table>
    
       <table width="100%"  align="left" style="display: none"  class="tableBorder" id="table3">
            <tr><td class="title" colspan="8">&nbsp;&nbsp;&nbsp;&nbsp;我要付款&nbsp;&nbsp;</td></tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">转出账户：</td>
                <td align="left"  id="b_aid" ></td>
                
            </tr>
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">付款金额：</td>
                <td align="left" id="b_payAmt" > 
             	 123  元</td> 
            </tr>
            
           <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">对方融易付用户：</td>
                <td align="left"  id="b_toUid"> </td> 
            </tr>
            
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">对方融易付账户号：</td>
                <td align="left" id="b_toAid"> </td> 
            </tr>
          
            <tr>
                <td colspan="2" align="center">
                <input type="button" class="button" value='确 定 支 付' onclick="qdzf();" /> 
                <input type="button" class="button" value=' 返   回 '  onclick="fanHui2();"/>
                </td> 
            </tr>
        </table>
        
         <table id="table4"  class="tableBorder" style="display: none;width: 300px;height: 100px;">
				<tbody>
					
						<tr>
						<td class="th1" align="right" width="30%">
							&nbsp; 密码：
						</td>
						<td width="70%" align="left">
							<input type="password" id="pwd"  size="20"/>
							<input type="hidden" id="hidden_type"  value=""/>
						</td>
						
						
					</tr>
					<tr>
					<td colspan="2" align="center"><span style=color:red>*</span>(该账户的资金将实时转入对方账户)</td>
					</tr>
					<tr>
					    
						<td colspan="2" align="center">
							<input type="button" value="确  定" onclick="edit();" class="button"/>&nbsp;&nbsp;
							<input type="button" value="返 回"  class="wBox_close button"/>
						</td>
					</tr>
				</tbody>
			</table> 
      </div>   
    </body>
</html>

           
           
    
