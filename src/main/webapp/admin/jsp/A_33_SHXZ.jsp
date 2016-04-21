<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>商户信息增加</title>
 <%
response.setHeader("Pragma","No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", 0);
int rand = new java.util.Random().nextInt(10000);
%>
        <meta http-equiv="pragma" content="no-cache"/>
        <meta http-equiv="cache-control" content="no-cache"/>
        <meta http-equiv="Expires" content="0"/>  
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <link href="../../public/css/head.css" rel="stylesheet" type="text/css"/>
        <link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css"/>
		<script type='text/javascript' src='../../dwr/engine.js?<%=rand %>'></script>
		<script type='text/javascript' src='../../dwr/util.js?<%=rand %>'></script>
		<script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
	
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../dwr/interface/MerchantService.js?<%=rand %>'></script>
		<script type="text/javascript" src="../../public/datePicker/WdatePicker.js?<%=rand %>"></script>
		<script type="text/javascript" src='../../public/js/ryt.js?<%=rand %>'></script>
		<script type="text/javascript" src='../../public/js/merchant/mms2minfo.js?<%=rand %>'></script>
        <script type="text/javascript" src="../../public/js/GreyFrame.js?<%=rand%>"></script>

	</head>

	<body onload="initadd()">
		<div class="style">
		<form action="#" id="addMinfoForm">
			<table width="100%" class="tableBorder">
				<tbody id="add_minfo_id">
					<tr>
						<td class="title" colspan="6">&nbsp;&nbsp;&nbsp;&nbsp;商户信息增加（带<font color="red">*</font>的为必填项）</td>
					</tr>
					<tr>
						<td colspan="6" bgcolor="#C0C9E0" align="center" > 商户基本信息</td>
					</tr>
					<tr>
						<td class="th1" width="12%" align="right"> 商户名： </td>
						<td align="left" nowrap="nowrap" style="width: 315px">
							<input type="text" id="name" value="" style="width: 300px" maxlength="40"/>
							<font color="red">*</font>
						</td>
						<td class="th1" width="12%" align="right"> 商户简称： </td>
						<td width="19%" align="left">
							<input type="text" id="abbrev" value="" style="width: 180px" maxlength="12"/>
							<font color="red">*</font>
						</td>
						<td class="th1" align="right" width="12%"> 所在省份： </td>
						<td align="left" >
							<select id="prov_id" style="width: 150px"></select>
							<font color="red">*</font>
						</td>
					</tr>
					<tr>
						<td class="th1" align="right"> 合同日期： </td>
						<td align="left">
							<input id="begin_date" class="Wdate" type="text" style="width:139px;"
								onfocus="WdatePicker({skin:'ext',minDate:'2010-01-01',maxDate:'2099-12-31',dateFmt:'yyyyMMdd',readOnly:'true'});" />
							至
							<input id="exp_date" class="Wdate" type="text" style="width:139px;"
								onfocus="WdatePicker({skin:'ext',minDate:'#F{$dp.$D(\'begin_date\')}',maxDate:'2099-12-31',dateFmt:'yyyyMMdd',readOnly:'true'});" />
							<font color="red">*</font>
						</td>
						<td class="th1" align="right"> 结算周期： </td>
						<td align="left">
							<select id="liqPeriod" style="width: 150px"></select>
							<font color="red">*</font>
						</td>
						<td class="th1" align="right"> 结算方式： </td>
						<td align="left">
							<select id="liq_type" style="width: 150px">
								<option value="2"> 净额 </option>
								<option value="1"> 全额 </option>
							</select>
						</td>
					</tr>

					<tr>
						<td class="th1" align="right">结算满足额度(元)： </td>
						<td align="left">
							<input type="text" id="liq_limit" value="" maxlength="7" style="width: 150px" onkeyup= "inputFigure(this)"/>（0代表无限额） 
						</td>
						<td class="th1" align="right" >单笔限制金额(元)： </td>
						<td align="left" >
							<input type="text" id="trans_limit" style="width: 100px" maxlength="7" onkeyup="inputFigure(this)"/>(为空表示无上限) 
						</td>
						<td class="th1" align="right"> 联机查询： </td>
						<td align="left">
							<select id="mer_chk_flag" style="width: 150px">
								<option value="1"> 允许查当天交易 </option>
								<option value="0"> 不允许 </option>
							</select>
						</td>
					</tr>

					<tr>
						<td class="th1" align="right" > 是否允许退款： </td>
						<td align="left">
							<select id="refund_flag" style="width: 150px">
								<option value="1"> 允许 </option>
								<option value="0"> 不允许 </option>
							</select>
						</td>
						<td class="th1" align="right"> 商户开通费(元)： </td>
						<td align="left">
							<input type="text" id="begin_fee" value="" size="20" maxlength="7"/>
							<font color="red">*</font>
						</td>
						<td class="th1" align="right"> 商户年费(元)： </td>
						<td align="left" >
							<input type="text" id="annual_fee" value="" size="20" maxlength="7"/>
							<font color="red">*</font>
						</td>
					</tr>

					<tr>

						<td class="th1" align="right"> 银行账号： </td>
						<td align="left">
							<input type="text" id="bank_acct" value="" size="40" maxlength="40"/>
							<font color="red">*</font>
						</td>
						<td class="th1" align="right"> 开户日期： </td>
						<td align="left"  >
							<input id="open_date" class="Wdate" type="text"
								onfocus="WdatePicker({skin:'ext',minDate:'1998-12-20',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});" />
							<font color="red">*</font>
						</td>

						<td class="th1" align="right"> 开户银行名： </td>
						<td align="left">
							<input type="text" id="bank_name" value="" style="width: 150px" maxlength="50"/>
							<font color="red">*</font>
						</td>
					</tr>

					<tr>
						<td class="th1" align="right"  > 开户银行支行名： </td>
						<td align="left" nowrap="nowrap">
							<input type="text" id="bank_branch" value="" size="40" maxlength="60"/>
							<font color="red">*</font>
						</td>
						<td class="th1" align="right"  > 开户银行省份： </td>
						<td align="left">
							<select id="bank_prov_id" style="width: 150px"></select><font color="red">*</font>
						</td>
						<td class="th1" align="right"> 开户账号名： </td>
						<td align="left">
							<input type="text" id="bank_acct_name" value="" maxlength="50"/><font color="red">*</font>
						</td>
					</tr>

					<tr>
						<td class="th1" align="right"> 商户保证金(元)： </td>
						<td align="left">
							<input type="text" id="caution_money" value="" size="20" maxlength="7"/>
							<font color="red">*</font>
						</td>
						<td class="th1" align="right"> 签约人： </td>
						<td align="left">
							<input type="text" id="signatory" value="" size="20" maxlength="10"/>
							<font color="red">*</font>
						</td>
						<td class="th1" align="right">法人姓名：</td>
       					 <td align="left"> <input id="corp_name" maxlength="20" type="text" /><font color="red">*</font></td>
					</tr>
					<tr>
						<td class="th1" align="right"> 商户地址： </td>
						<td align="left">
							<input type="text" id="addr" value="" maxlength="80" style="width: 300px"/>
						</td>
						<td class="th1" align="right"> 工商登记号： </td>
						<td align="left">
							<input type="text" id="reg_code" value="" maxlength="30"/><font color="red">*</font>
						</td>
						<td class="th1" align="right"> 商户邮编： </td>
						<td align="left">
							<input type="text" id="zip" value="" maxlength="6"/>
						</td>
					</tr>
					<tr>
						<td class="th1" align="right">  网上域名： </td>
						<td align="left">
							<input type="text" id="web_url" value="" maxlength="80" style="width: 300px"/>
						</td>
						<td class="th1" align="right"> 商户传真： </td>
						<td align="left">
							<input type="text" id="fax_no" value="" size="20" maxlength="20"/>
						</td>
						<td height="24px" class="th1" align="right"> 商户所属行业 </td>
						<td>
							<select id="merTradeType" ,name="merTradeType" style="width: 150px">
								<option value=""> 请选择.... </option>
							</select>&nbsp;<font color="red">*</font>
						</td>
					</tr>
					<tr>
					   <td class="th1" align="right">&nbsp; 证件：</td>
			             <td align="left">
			                 <select id="id_type" name="id_type"></select>号码
			                        <input id="id_no" name="id_no" style="width: 125px;" maxlength="30"/>&nbsp;<font color="red">*</font>
			            </td>
			            
			         <td class="th1" align="right">&nbsp; 开户银行行号：</td>
			             <td align="left">
			                 <input id="open_bk_no" name="open_bk_no" maxlength="14" onchange="" />&nbsp;<font color="red">*</font>
			                 <input type="button" value="检索" onclick="SerchBankNoInfo();" class="button" style="width: 40px"/>
			         </td>

						<td class="th1" align="right">商户的类别：</td>
						<td colspan="" align="left">
							<select style="width: 90px" id="category" onchange="showdlscode(this.value);">
							<option value="0">RYF商户</option>
							<option value="1">VAS商户</option>
							<option value="2">POS商户</option>
							<option value="3">POS代理商</option>
							</select>
							<span id="dlscdsp"></span>
						</td>
					</tr>
				<tr>  
					<td class="th1" align="right">退款时退还手续费 ：</td>
					<td colspan="" align="left">
							<select id="refundFee" name="refundFee" style="width: 150px"><option value="0">不退还</option><option value="1">退还</option></select>
							&nbsp;(默认为不退还)
					</td>
			         <td class="th1" align="right"> 组织机构代码： </td>
						<td align="left">
							<input type="text" id="corp_code" value="" size="9" maxlength="9"/>
						</td>
			          <td class="th1" align="right">组织机构代码有效期：</td>
					    <td  align="left"><input name="codeExpDate" id="codeExpDate" class="Wdate"
					  onfocus="WdatePicker({skin:'ext',minDate:'2010-01-01',maxDate:'2099-12-31',dateFmt:'yyyyMMdd',readOnly:'true'});" /></td>
			       </tr>
					<tr>
						<td class="th1" align="right">结算对象：</td>
						<td align="left"><select style="width: 150px" id="liqObj">
								<option value="0">银行账户</option>
								<option value="1">电银账户</option>
								<option value="3">账户系统-企业</option>
								<option value="4">账户系统-个人</option>
						</select></td>

						<td class="th1" align="right">商户类型：</td>
						<td   align="left">
							<select style="width: 150px" id="merType">
							<option value="0">企业</option>
							<option value="1">个人</option>
							<option value="2">集团</option>
							</select>
						</td>
						<td class="th1" align="right">结算状态：</td>
						<td   align="left">	
						<select style="width: 150px" id="liqState">
							<option value="0">正常</option>
							<option value="1">冻结</option>
							</select>
						
						</td>		
					</tr>
					<tr>
					<td class="th1" align="right">结算银行：</td>
						<td   align="left">	
						<select style="width: 150px" id="gateId">
						<option value="0">全部....</option>
						</select> <font color="red">*</font>
						</td>	
						<td class="th1" align="right">商户描述：</td>
						<td align="left">
							<input type="text" id="mdesc" maxlength="80" border: 1" />
						</td>
						<td class="th1" align="right">P2P托管业务：</td>
						<td>
							<select id="isPtop">
								<option value="0">否</option>
								<option value="1">是</option>
							</select>
						</td>	
					</tr>
					<tr>
						<td colspan="6" bgcolor="#C0C9E0" align="center" >商户联系人信息</td>
					</tr>
				</tbody>
			</table>
			<table width="100%" style="margin-top: 0px" class="tableBorder">
				<tbody>
					<tr>
						<td class="th1" align="center" style="border: 0"></td>
						<td class="th1" align="center" style="border: 0"> 联系人姓名 </td>
						<td class="th1" align="center" style="border: 0"> 联系人电话 </td>
						<td class="th1" align="center" style="border: 0"> 联系人手机 </td>
						<td class="th1" align="center" style="border: 0"> 联系人Email </td>
					</tr>
			         <tr>
                       <td class="th1" align="center" >主联系人</td>
                       <td align="left"><input type="text" id="contact0" value='' size="20" maxlength="10"/><font color="red">*</font></td>
                       <td align="left"><input type="text" id="tel0" value='' size="40" maxlength="20"/><font color="red">*</font></td>
                       <td align="left"><input type="text" id="cell0"  size="40" maxlength="20"/><font color="red">*</font></td>
                       <td align="left"><input type="text" id="email0"  size="40" maxlength="40" /><font color="red">*</font></td>
                     </tr>
			         <tr>
                       <td class="th1" align="center" >财务联系人</td>
                       <td align="left"><input type="text" id="contact1" value='' size="20" maxlength="10"/></td>
                       <td align="left"><input type="text" id="tel1" value='' size="40" maxlength="20"/></td>
                       <td align="left"><input type="text" id="cell1"  size="40" maxlength="20"/></td>
                       <td align="left"><input type="text" id="email1"  size="40" maxlength="40" /></td>
                     </tr> 
			         <tr>
                       <td class="th1" align="center" >技术联系人</td>
                       <td align="left"><input type="text" id="contact2" value='' size="20" maxlength="10"/></td>
                       <td align="left"><input type="text" id="tel2" value='' size="40" maxlength="20"/></td>
                       <td align="left"><input type="text" id="cell2"  size="40" maxlength="20"/></td>
                       <td align="left"><input type="text" id="email2"  size="40" maxlength="40" /></td>
                     </tr>
			         <tr>
                       <td class="th1" align="center" >运行联系人</td>
                       <td align="left"><input type="text" id="contact3" value='' size="20" maxlength="10"/></td>
                       <td align="left"><input type="text" id="tel3" value='' size="40" maxlength="20"/></td>
                       <td align="left"><input type="text" id="cell3"  size="40" maxlength="20"/></td>
                       <td align="left"><input type="text" id="email3"  size="40" maxlength="40" /></td>
                     </tr>
			         <tr>
                       <td class="th1" align="center" >市场联系人 </td>
                       <td align="left"><input type="text" id="contact4" value='' size="20" maxlength="10"/></td>
                       <td align="left"><input type="text" id="tel4" value='' size="40" maxlength="20"/></td>
                       <td align="left"><input type="text" id="cell4"  size="40" maxlength="20"/></td>
                       <td align="left"><input type="text" id="email4"  size="40" maxlength="40" /></td>
                     </tr>
                      <tr>
                       <td class="th1" align="center" >销售联系人 </td>
                       <td align="left"><input type="text" id="contact5" value='' size="20" maxlength="10"/></td>
                       <td align="left"><input type="text" id="tel5" value='' size="40" maxlength="20"/></td>
                       <td align="left"><input type="text" id="cell5"  size="40" maxlength="20"/></td>
                       <td align="left"><input type="text" id="email5"  size="40" maxlength="40" /></td>
                     </tr>
					<tr><td colspan="5"  align="center"><input type="button" value="增 加" class="button" onclick = "adminAddMinfo();" /></td></tr>
				</tbody>
			</table>
			</form>
			<form>
			<table id="serchBankNo" class="tableBorder" style="display:none; width: 700px; height: 300px; margin-top: 0px;">
			<tr>
				<td class="th1" bgcolor="#D9DFEE" align="right" width="15%">开户行:</td>
				<td align="left">
				<select style="width: 150px" id="gate">
						<option value="">全部....</option>
				</select>&nbsp;&nbsp; 
				<input type="text" id="bk_name" style="width: 150px" value="请输入开户行关键字检索联行行号" onFocus="if(value==defaultValue){value='';this.style.color='#000'}"
				   onBlur="if(!value){value=defaultValue;this.style.color='#999'}"style="color:#999999" /><input type="button" value="检索"onclick="SerchBankNo(1);" class="button" />
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
		</form>
		</div>
	</body>
</html>
