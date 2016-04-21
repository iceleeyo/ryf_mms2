<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>账户风险配置</title>
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
        <script type="text/javascript" src="../../public/js/adminAccount/admin_zhfxpz.js?<%=rand%>"></script>
        <script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
 		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
    </head>
    <body onload="init()">
    <div class="style">
        <form name="MERTLOG"  method="post" action="">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="8">&nbsp;&nbsp;&nbsp;&nbsp;账户代发风险配置&nbsp;&nbsp;</td></tr>
             <tr>
                 <td class="th1" bgcolor="#D9DFEE" align="right" width="20%">商户号：</td>
                
                <td align="left" colspan="3" >
                <input type="hidden"  name="mid" id="mid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}" />
               <input type="text" id="uid" name="uid"  size="20" />  &nbsp;&nbsp;&nbsp;&nbsp;</td>
               
               <td class="th1" bgcolor="#D9DFEE" align="right" width="20%" >商户状态：</td>
               <td>
                <select style="width: 150px" id="mstate" name="mstate">
                     <option value="">全部...</option>
                   </select> 
               </td>
               </tr>
               <tr align="center">
               <td colspan="8"><input class="button" type="button" value = " 查  询 " onclick="search(1);" /></td>
               
            </tr>
        </table>
       </form>
       
    	
       <table  class="tablelist tablelist2" id="fxpzTable" style="display: none;">
          <!--  <tr ><th id="head" style="background-color: rgb(34,182,193);display: none;" align="left" colspan="20">&nbsp;待配置账户</th></tr> -->
           <tr>
              <th>操作</th><th>商户号</th><th>商户名称</th><th>所属行业</th><th>账户号</th><th>账户状态</th>
		    <th>同一收款卡号月累计成功次数</th><th>同一收款卡号月累计成功金额(元)</th>
		    <th>同一扣款卡号月累计成功次数</th><th>同一扣款卡号月累计成功金额(元)</th>
           </tr>
           <tbody id="resultList">
           </tbody>
       </table>
       
       
       
        <table id="configTable" class="tableBorder" style="display: none; width: 600px; height: 220px; margin-top: 0px;">
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">用户：</td>
                <td align="left">
                	<input type="text" disabled="disabled" size="20" id="a_uid"    />
                </td>
            </tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">所属行业：</td>
               <td align="left">
                	<input type="text" disabled="disabled" size="20" id="a_trade"    />
                </td>
            </tr>
            
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">帐户：</td>
                <td align="left">
                	<input type="text" disabled="disabled" size="20" id="a_aid"    />
                </td>
                
               
            </tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">账户名称：</td>
                <td align="left">
                	<input type="text" disabled="disabled" size="20" id="a_aname"    />
                </td>
            </tr>
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">用户状态：</td>
                <td align="left"  >
               	<select id="a_mstate">
               		<option value="1">正常</option>
               		<option value="2">关闭</option>
               	</select>
               </td>
            </tr>
<!--             
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">单笔交易限额：</td>
                <td align="left"  >
               <input type="text"   id="trans_limit" size="20" maxlength="11"/>
               </td>
            </tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">月交易限制金额：</td>
                <td align="left"  >
               <input type="text"    id="month_limit" size="20" maxlength="11"/>
               </td>
            </tr>
	 -->		
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">同一收款卡号月累计成功次数：</td>
                <td align="left"  >
               <input type="text"    id="acc_month_count" size="20" maxlength="11" />
               </td>
            </tr>
            
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">同一收款卡号月累计成功金额：</td>
                <td align="left"  >
               <input type="text"   id="acc_month_amt" size="20" maxlength="11"/>
               </td>
            </tr>
            
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">同一扣款卡号月累计成功次数：</td>
                <td align="left"  >
               <input type="text"    id="dk_month_count" size="20" maxlength="11" />
               </td>
            </tr>
            
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">同一扣款卡号月累计成功金额：</td>
                <td align="left"  >
               <input type="text"   id="dk_month_amt" size="20" maxlength="11"/>
               </td>
            </tr>
            
            <!-- 
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">充值计费公式：</td>
                <td align="left"  >
               <input type="text"   id="cz_fee_mode" size="20" maxlength=""/>
               &nbsp;<font color="red">*</font>
                <a href="feemodel_details.html" target="feeFrame"/><font color="blue">查看公式详情</font>
               </td>
            </tr>
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">提现计费公式：</td>
                <td align="left"  >
               <input type="text"   id="tixian_fee_mode" size="20" maxlength=""/>
               &nbsp;<font color="red">*</font>
                <a href="feemodel_details.html" target="feeFrame"/><font color="blue">查看公式详情</font>
               </td>
            </tr>
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">对私计费公式：</td>
                <td align="left"  >
               <input type="text"    id="daifa_fee_mode" size="20" maxlength=""/>
               &nbsp;<font color="red">*</font>
                <a href="feemodel_details.html" target="feeFrame"/><font color="blue">查看公式详情</font>
               </td>
            </tr>
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">对公计费公式：</td>
                <td align="left"  >
               <input type="text"   id="daifu_fee_mode" size="20" maxlength=""/>
               &nbsp;<font color="red">*</font>
                <a href="feemodel_details.html" target="feeFrame"/><font color="blue">查看公式详情</font>
               </td>
            </tr>
             -->
            <tr>
				<td colspan="4" align="center" height="20px;">
					<input value="修改" type="button" class="button" onclick="edit();" />
					<input value="返回" type="button" class="wBox_close button" />
				</td>
			</tr>
            
        </table>
        <table id="settlementTable" class="tableBorder" style="display: none; width: 600px; height: 220px; margin-top: 0px;">
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">用户：</td>
                <td align="left">
                	<input type="text" disabled="disabled" size="20" id="b_uid"   />
                </td>
            </tr>
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">代付计费公式：</td>
                <td align="left"  >
               <input type="text"   id="b_daifu_fee_mode" size="20" maxlength=""/>
               &nbsp;<font color="red">*</font>
                <a href="feemodel_details.html" target="feeFrame"/><font color="blue">查看公式详情</font>
               </td>
            </tr>
            <tr>
				<td colspan="4" align="center" height="20px;">
					<input value="修改" type="button" class="button" onclick="edit4Settlement();" />
					<input value="返回" type="button" class="wBox_close button" />
				</td>
			</tr>
            
        </table>
      </div>   
    </body>
</html>

           
           
    
