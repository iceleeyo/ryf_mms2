<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>单笔付款到个人银行账户</title>
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
        <script type="text/javascript" src='../../dwr/interface/DfB2CSingleService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/MerchantService.js?<%=rand%>'></script> 
        <script type="text/javascript" src="../../public/js/merchant/mer_dbfkdgryhzh.js?<%=rand%>"></script>
        <script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
        <script type="text/javascript" src="../../dwr/interface/PageService.js?<%=rand%>"></script>
 		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
 		<script type='text/javascript' src='../../public/js/ryt_util.js?<%=rand%>'></script>
 		<script type="text/javascript" src="../../public/js/md5.js"></script>
    </head>
    <body onload="init();">
         <table   align="left"  class="tableBorder" id="table1" >
            <tr><td class="title" colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;单笔付款到个人银行账户&nbsp;&nbsp;</td></tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">付款账户号：</td>
                <td align="left"  >
                <input type="hidden"  name="mid" id="mid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}" />
	             	 <select style="width: 180px" id="aid" name="aid">
	             		<!--  <option value="">请选择...</option> -->
	                 </select>  
	                 <input type="hidden" id="oid"/>
             </td>	
            </tr>
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">收款人账号：</td>
                <td align="left"  >
	             	<input type="text" id="to_acc_no" name="to_acc_no" size="24" maxlength="19"/>  
             </td>
            </tr>
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">收款人户名：</td>
                <td align="left"  >
	             	<input type="text" id="to_acc_name" name="to_acc_name" size="24" maxlength="50"/> 
             </td>
            </tr>
            
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">收款银行：</td>
                <td align="left"  >
	             	 <select style="width: 180px" id="to_bk_no" name="to_bk_no">
	             		 <option value="">请选择...</option>
	                 </select>  
             </td>
            </tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">收款银行联行行号：</td>
                <td align="left"  >  
                  <input id="to_openbk_no" name="to_openbk_no" style="width: 175px" />&nbsp;&nbsp;&nbsp;&nbsp;
                  <input id="to_openbk_name" name="to_openbk_name" type="hidden" />
                  <input type="button" value="检索" onclick="SerchBankNoInfo();"  class="button"/> 
             </td>
            </tr>
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">开户所在省份：</td>
                <td align="left"  >
	                <select id="to_prov_id" style="width: 180px">
	                	<option value="">请选择...</option>
	                </select>
               </td>
            </tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">卡/折标志：</td>
                <td align="left"  >
               <select id="card_flag" style="width: 180px">
               		<option value="">请选择...</option>
               		<option value="0">卡</option>
               		<option value="1">折</option>
               </select>
               </td>
            </tr>
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">交易金额：</td>
                <td align="left"  >
	             	 <input type="text" id="trans_amt1" name="trans_amt1" size="24" maxlength="11"/>元
             </td>
            </tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">用途：</td>
                <td align="left"  >
                <textarea rows="3" cols="30" id="priv" ></textarea> 
             </td>
             
            </tr>
            <tr>
            <td colspan="2" align="center">&nbsp;&nbsp;&nbsp;
            <input  type="button" class="button" value = "提 交" onclick="addSinglePay();" />
             	   </td>
            </tr>
           
        </table>
        
         <table width="100%"  align="left" style="display: none;"  class="tableBorder" id="table2">
            <tr><td class="title" colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;单笔付款到个人银行账户&nbsp;&nbsp;</td></tr>
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">系统订单号：</td>
                <td align="left" id="a_oid" >
             </td>	
            </tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">转出账户号：</td>
                <td align="left" id="a_aid" >
             </td>	
            </tr>
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">收款人账号：</td>
                <td align="left"   id="a_to_acc_no">
             </td>
            </tr>
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">收款人户名：</td>
                <td align="left" id="a_to_acc_name" >
             </td>
            </tr>
            
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">收款银行：</td>
                <td align="left"  id="a_bkno">
             </td>
            </tr>
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">收款银行联行行号：</td>
                <td align="left"  id="a_to_openbk_no">
             </td>
            </tr>
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">交易金额：</td>
                <td align="left"   id=a_trans_amt><!-- transamt -->
             </td>
            </tr>
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">手续费金额：</td>
                <td align="left"   id="a_fee"> <!--fee_  -->
             </td>
            </tr>
             <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">付款金额：</td>
                <td align="left"   id="a_pay_amt"><!-- a_pay_amt -->
             </td>
            </tr>
            <tr>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="40%">用途：</td>
                <td align="left" id="a_priv"></td><!-- priv -->
             </tr>
            <tr>
                <td colspan="2" align="center">
                <input type="button" class="button" value='确定提交' onclick="confirmSinglePay();" /> 
                  <input type="button" class="button" value='返回' onclick="backout();" />
                </td> 
            </tr>
        </table>
        <input type="hidden" id="a_bk_no" /><!-- 收款银行 -->
        <input type="hidden" id="a_to_bk_noh"/><!-- 隐藏域保存行号 -->
       	<input type="hidden" id="a_to_prov_idh"/><!-- 隐藏域保存行号 -->
       	<input type="hidden" id="a_card_flagh"/><!-- 隐藏域保存行号 -->
       	<input type="hidden" id="a_feeh"/><!--隐藏域保存手续费  -->
      	<input type="hidden" id="a_trans_amth"/><!--隐藏域保存交易金额  -->
       	<input type="hidden" id="a_pay_amth"/><!--隐藏域保存实际支付金额  -->
       	<input type="hidden" id="all_balanceh"/><!--隐藏域保存账户余额  -->
        
        
         <table id="table3"  class="tableBorder" style="display: none;width: 400px;height: 100px;">
				<tbody>
					<tr>
						<td class="th1" align="right" width="30%">
							&nbsp; 交易金额：
						</td>
						<td width="70%" align="left">
							<span id="sumAmt"></span>
							<input type="hidden" id="hidden_type"  value=""/>
						</td></tr>
						<tr>
						<td class="th1" align="right" width="30%">
							&nbsp; 手续费：
						</td>
						<td width="70%" align="left">
							<span id="fee_amt"></span>
							<input type="hidden" id="hidden_type"  value=""/>
						</td></tr>
						<tr>
						<td class="th1" align="right" width="30%">
							&nbsp; 实际支付金额：
						</td>
						<td width="70%" align="left">
							<span id="sum_amt"></span>
							<input type="hidden" id="hidden_type"  value=""/>
						</td></tr>
						<tr>
							<td class="th1" align="right" width="30%">
							&nbsp; 账户余额：
							</td>
							<td width="70%" align="left">
								<span id="balance"></span>
								<input type="hidden" id="hidden_type"  value=""/>
							</td>
						</tr>
						<tr>
						<td class="th1" align="right" width="30%">
							&nbsp; 支付密码：
						</td>
						<td width="70%" align="left">
							<input type="password" id="pwd"  size="20"/>
							<input type="hidden" id="hidden_type"  value=""/>
						</td>
						
					</tr>
					<tr>
					    
						<td colspan="2" align="center">
							<input type="button" value="账户金额支付" onclick="edit();" class="button"/>&nbsp;&nbsp;
							<input type="button" value="返 回"  class="wBox_close button"/>
						</td>
					</tr>
				</tbody>
			</table> 
        <table id="serchBankNo" class="tableBorder" style="display:none; width: 700px; height: 300px; margin-top: 0px;">
			<tr>
				<td class="th1" bgcolor="#D9DFEE" align="right" width="15%">开户行:</td>
				<td align="left">
				<select style="width: 150px" id="gate">
						<option value="">全部...</option>
				</select>&nbsp;&nbsp; 
				<input type="text" id="bk_name" style="width: 150px" value="请输入开户行关键字检索联行行号" onfocus="if(value==defaultValue){value='';this.style.color='#000';}"
				   onblur="if(!value){value=defaultValue;this.style.color='#999';}"style="color:#999999" /> <input type="button" value="检索"onclick="SerchBankNo(1);" class="button" />
			   </td>
			</tr>
			<tr>
				<td colspan="2" valign="top">
					<table class="tablelist tablelist2" id="serach">
						<thead>
							<tr style="height: 20px">
								<th width="40px"></th>
								<th align="center">联行行号</th>
								<th align="center">联行名称</th>
							</tr>
						</thead>
						<tbody id="resultListbankno"></tbody>
					</table></td>
			</tr>
			<tr>
				<td colspan="2" align="left" style="height: 50px">
					说明：选择开户行所属行、输入关键字查询&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<input value="确定" type="button" class="button"onclick="confirmbankNo(1);" />
					<input value="返回" type="button"class="wBox_close button" /></td>
			</tr>
		</table>
    </body>
</html>

           
           
    
