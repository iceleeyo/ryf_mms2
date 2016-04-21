<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>商户重要信息修改审核</title>
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
        <link href="../../public/css/head.css?<%=rand %>" rel="stylesheet" type="text/css"/>
        <link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css"/>
        <script type='text/javascript' src='../../dwr/engine.js'></script>
        <script type='text/javascript' src='../../dwr/util.js'></script>
        <script type="text/javascript" src="../../dwr/interface/PageParam.js?<%=rand %>"></script>
        <script type="text/javascript" src="../../dwr/interface/PageService.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../dwr/interface/MerchantService.js?<%=rand%>"></script> 
		<script type="text/javascript" src="../../public/datePicker/WdatePicker.js?<%=rand %>"></script>
		<script type="text/javascript" src="../../public/js/ryt.js?<%=rand %>"></script>
        <script type="text/javascript" src='../../public/js/merchant/mms2minfo.js?<%=rand %>'></script>
  </head>
  <body onload="initImportantOptions();">
  <div class="style">
   <table class="tableBorder" >
             <tbody id="queryTiaojian">
              <tr>
              		<td class="title" colspan="7">&nbsp;&nbsp;&nbsp;&nbsp;商户重要信息修改审核</td>
              </tr>
                 <tr>
                   <td class="th1" align="right" width="20%" style="height: 30px">商户号：</td>
                   <td><input style="width: 150px;height: 20px;" type="text" id="mid" name="mid" size="8px"  onkeyup="checkMidInput(this);"/></td>
                   <td class="th1" align="right" width="20%" style="height: 30px">商户名称：</td>
                   <td><input style="width: 150px;height: 20px;" type="text" id="mname" name="mname" /></td>
                   <td class="th1" align="right" width="20%" style="height: 30px">审核状态：</td>
                   <td>
                   		<select style="width: 150px;height: 20px;" id="status" name="status">
                   			<option value="0">待审核</option>
                   			<option value="1">审核通过</option>
                   			<option value="2">审核失败</option>
                   		</select>
				   </td>
                   <td><input type="button" value="查询" class="button" onclick="setCondition(); queryCheckPage(1);"/></td>
					<%--查询历史记录--%>
                   <input type="hidden" name="hmid"/>
                   <input type="hidden" name="hmname"/>
                   <input type="hidden" name="hstatus"/>
                 </tr>
             </tbody>
      </table>
       <table class="tablelist tablelist2" id="checkTable" style="display:none;">
       		<thead>
	           <tr>
	             <th style="width: 40px;" sort=false><input id="toggleAll" type="checkbox" onclick="toggleAll(this);" />全选</th><th>商户号</th><th>商户全名</th>
	             <th>所在省份</th><th>合同日期</th><th>开户行名称</th><th>银行帐号</th><th>开户日期</th><th>结算对象</th>
	             <th>结算状态</th><th>手工结算</th><th>申请人</th><th>申请时间</th><th>审核人</th><th>审核时间</th><th>审核状态</th><th>操作</th>
	           </tr>
            </thead>
           <tbody id="resultList"></tbody>
       </table>
      
       <form action="#" id="minfoForm">
			<table class="tableBorder" id="minfoMsg" style="display:none;">
				<tr>
					<td class="title"></td>
					<td class="title">&nbsp;&nbsp;当&nbsp;&nbsp;前</td>
					<td class="title">&nbsp;&nbsp;申&nbsp;&nbsp;请</td>
					<td class="title"></td>
					<td class="title">&nbsp;&nbsp;当&nbsp;&nbsp;前</td>
					<td class="title">&nbsp;&nbsp;申&nbsp;&nbsp;请</td>
				</tr>
				<tr>
					<td class="th1" align="right">商户全名：</td>
					<td><input type="hidden" value="" id="id" name="id"
						maxlength="40" /> &nbsp;<input readonly="readonly" 
						name="name" id="name" style="width: 250px;"
						type="text" /> <font color="red">*</font>&nbsp;&nbsp;</td>
					<td class="th3" id="cname" align="left"></td>
					<td class="th1" align="right">商户简称：</td>
					<td>&nbsp;<input name="abbrev" id="abbrev" type="text"
						readonly="readonly" maxlength="16" /> <font color="red">*</font>
					</td>
					<td class="th3" id="cabbrev" align="left"></td>
				</tr>
				<tr>
					<td class="th1" align="right">组织结构代码：</td>
					<td>&nbsp;<input name="corp_code" id="corp_code"
					 readonly="readonly" axlength="9" />
					</td>
					<td class="th3" id="ccorp_code" align="left"></td>
					<td class="th1" align="right">组织结构代码有效期至：</td>
					<td>&nbsp;<input id="code_exp_date" class="Wdate" type="text"
						readonly="readonly" />
					</td>
					<td class="th3" id="ccode_exp_date" align="left"></td>
				</tr>
				<tr>
					<td class="th1" align="right">法人姓名：</td>
					<td align="left"><input id="corp_name" type="text"
						maxlength="30" readonly="readonly" />&nbsp;<font color="red">*</font>
					</td>
					<td class="th3" id="ccorp_name" align="left"></td>
					<td class="th1" align="right">商户类别：</td>
					<td><select disabled="disabled" style="width: 150px;" id="category">
							<option value="0">RYF商户</option>
							<option value="1">VAS商户</option>
							<option value="2">POS商户</option>
					</select></td>
					<td class="th3" id="ccategory" align="left"></td>
				</tr>
				<tr>
					<td class="th1" align="right">&nbsp;合同日期：</td>
					<td align="left"><input id="begin_date" class="Wdate"
						readonly="readonly" type="text"/>
						至 <input readonly="readonly"  id="exp_date" class="Wdate" type="text"/>
						<font color="red">*</font></td>
					<td class="th3" id="ccontract_date" align="left"></td>
					<td class="th1" align="right">工商登记号：</td>
					<td align="left"><input type="text" id="reg_code" value=""
					readonly="readonly" maxlength="30" />
					</td>
					<td class="th3" id="creg_code" align="left"></td>
				</tr>
				<tr>
					<td class="th1" align="right">开户银行名：</td>
					<td align="left"><input type="text" id="bank_name" value=""
						size="25" maxlength="50" readonly="readonly" />&nbsp;<font color="red">*</font>
					</td>
					<td class="th3" id="cbank_name" align="left"></td>
					<td class="th1" align="right" nowrap="nowrap">开户银行支行名：</td>
					<td align="left"><input type="text" id="bank_branch" value=""
						readonly="readonly" size="45" maxlength="60" />&nbsp;<font color="red">*</font></td>
					<td class="th3" id="cbank_branch" align="left"></td>
				</tr>
				<tr>
					<td class="th1" align="right" nowrap="nowrap">开户银行省份：</td>
					<td align="left"><select disabled="disabled" id="bank_prov_id"></select>&nbsp;<font
						color="red">*</font>
					</td>
					<td class="th3" id="cbank_prov_id" align="left"></td>
					<td class="th1" align="right">&nbsp;开户日期：</td>
					<td align="left" nowrap="nowrap"><input id="open_date"
						readonly="readonly" class="Wdate" type="text" />
						<font color="red">*</font></td>
					<td class="th3" id="copen_date" align="left"></td>
				</tr>
				<tr>
					<td class="th1" align="right">&nbsp;开户账号名：</td>
					<td align="left"><input readonly="readonly"  type="text" id="bank_acct_name"
						value="" size="40" maxlength="50" /> <font color="red">*</font></td>
					<td class="th3" id="cbank_acct_name" align="left"></td>
					<td class="th1" align="right">&nbsp;银行账号：</td>
					<td width="30%" align="left"><input readonly="readonly" ype="text" id="bank_acct"
						value="" size="20" maxlength="32" /> <font color="red">*</font></td>
					<td class="th3" id="cbank_acct" align="left"></td>
				</tr>
				<tr>
					<td class="th1" align="right">&nbsp; 证件：</td>
					<td align="left"><select disabled="disabled" id="id_type" name="id_type"></select>&nbsp;号码
						<input id="id_no" readonly="readonly" name="id_no" maxlength="30" />&nbsp;<font
						color="red">*</font></td>
					<td class="th3" id="cid_type_no" align="left"></td>
					<td class="th1" align="right">&nbsp; 开户银行行号：</td>
					<td align="left">
						<input readonly="readonly" id="open_bk_no" name="open_bk_no" maxlength="14" style="width: 100px;" />&nbsp;
						<font color="red">*</font>
						<input readonly="readonly" id="open_bk_name" name="open_bk_name" style="width: 200px;"/>
						<font color="red">*</font>
					</td>
					<td class="th3" id="copen_bk_no" align="left"></td>
				</tr>
				<tr>
					<td class="th1" align="right">结算对象：</td>
					<td align="left"><select disabled="disabled" style="width: 150px" id="liq_obj">
							<option value="0">银行账户</option>
							<option value="1">电银账户</option>
							<option value="2">代理商</option>
					</select></td>
					<td class="th3" id="cliq_obj" align="left"></td>
					<td class="th1" align="right">结算状态：</td>
					<td align="left"><select disabled="disabled" style="width: 150px" id="liq_state">
							<option value="0">正常</option>
							<option value="1">冻结</option>
					</select></td>
					<td class="th3" id="cliq_state" align="left"></td>
				</tr>
				<tr>
					<td class="th1" align="right">手工结算：</td>
					<td align="left"><select disabled="disabled" style="width: 150px" id="man_liq">
							<option value="0">关闭</option>
							<option value="1">开启</option>
					</select></td>
					<td class="th3" id="cman_liq" align="left"></td>
					<td class="th1" align="right">结算银行：</td>
					<td align="left"><select disabled="disabled" style="width: 150px" id="gateId">
							<option value="">全部....</option>
					</select> <font color="red">*</font></td>
					<td class="th3" id="cgateId" align="left"></td>
				</tr>
				
				<tr>
					<td class="th1" align="right">银联(UPMP)商户号：</td>
					<td align="left">
						<input type="text" name="upmpMid" id="upmpMid" maxlength="15"  />
					</td>
					<td class="th3" id="cupmpMid"  align="left"></td>
					<td class="th1" align="right">结算满足额度(元)：</td>
                    <td align="left">
                        <input type="text" readonly="readonly" id="liqLimit" value="" maxlength="7" size="10"/>（0代表无限额,只能是整数）
                    </td>
					<td class="th3" id="curLiqLimit"  align="left"></td>
				</tr>
				
				<tr>
				<td class=th1 align=right>是否手工代付：</td>
				<td align=left>
					<select disabled="disabled" style="width:150px" id=is_sgdf >
						<option>全选</option>
						<option value="0">否</option>
						<option value="1">是</option>
					</select><font color="red">*</font>
				</td>
				<td class="th3" colspan="4" id="cis_sgdf" align="left"></td>
				</tr>
				<tr>
					<td class="th1" align="right">生效时间：</td>
					<td colspan="5">
						<input id="effectiveTime" class="Wdate" type="text" onfocus="WdatePicker({skin:'ext',minDate:'%y-%M-%d',maxDate:'2099-12-31',dateFmt:'yyyyMMddHHmm',readOnly:'true'});" />
						&nbsp;&nbsp;&nbsp;&nbsp;<input style="vertical-align: middle;" id="isEffectNow" type="checkbox"/>&nbsp;&nbsp;立即生效
					</td>
				</tr>
				<tr>
					<td class="th1" align="right">申请：</td>
					<td colspan="2"><span id="applyOperName" style="padding: 0 20px;"></span><span id="applyTime" style="padding: 0 20px;">间</span></td>
					<td class="th1" align="right">审核：</td>
					<td colspan="2"><span id="checkOperName" style="padding: 0 20px;"></span><span id="checkTime" style="padding: 0 20px;"></span></td>
				</tr>
				<tr>
					<td colspan="6" align="center">
						<input id="btn1" type="button" value="审核通过" onclick="doCheck(1,'',false);" class="button" />
						<input id="btn2" type="button" value="审核失败" onclick="doCheck(2,'',false);" class="button" />
						<input type="button" value="关闭" onclick="document.getElementById('minfoMsg').style.display = 'none'" class="button" />
					</td>
				</tr>
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
					   onBlur="if(!value){value=defaultValue;this.style.color='#999'}"style="color:#999999" /> <input type="button" value="检索"onclick="SerchBankNo(1);" class="button" />
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

