<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>商户重要信息修改申请</title>
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
              <tr><td class="title" colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;商户重要信息修改(带<font color="red">*</font>的为必填项)
                </td></tr>
                 <tr>
                   <td class="th1" align="right" width="20%" style="height: 30px">&nbsp;商户号：</td>
                   <td>&nbsp;&nbsp;<input type="text" id="mid" name="mid"    size="8px" onkeyup="checkMidInput(this);"/>
                   &nbsp;&nbsp;<font color="red">*</font>
                   &nbsp;&nbsp;&nbsp;&nbsp;<input type="button" value="查询" class="button" onclick="showMerMsg();"/></td>
                 </tr>
             </tbody>
       </table>
       <form action="#" id="minfoForm">
      <table class="tableBorder"  id="minfoMsg" style="display:none;" >
		<tr><td class="title" colspan="4"></td></tr>
		<tr><td class="th1" align="right" width="20%">商户全名：</td><td>
		          <input type="hidden" value="" id="id" name="id" maxlength="40"/>
		     &nbsp;<input name="name" id="name" style="width: 250px;" type="text"/>
                        <font color="red">*</font>&nbsp;&nbsp;</td>
            <td class="th1" align="right">商户简称：</td><td >
		     &nbsp;<input name="abbrev" id="abbrev" type="text" maxlength="16"/>
                        <font color="red">*</font></td></tr>
		<tr>
		<td class="th1" align="right">组织结构代码：</td>
		<td >&nbsp;<input name="corp_code" id="corp_code" maxlength="9"/></td>
		<td class="th1" align="right">组织结构代码有效期至：</td>
		<td >&nbsp;<input id="code_exp_date" class="Wdate" type="text"
                            onfocus="WdatePicker({skin:'ext',minDate:'2010-01-01',maxDate:'2099-12-31',dateFmt:'yyyyMMdd',readOnly:'true'});" />
        </td>
        </tr>
		<tr>
		<td class="th1" align="right">法人姓名：</td>
        <td align="left"> <input id="corp_name"  type="text" maxlength="30"/>&nbsp;<font color="red">*</font></td>
        <td class="th1" align="right">商户类别：</td><td>
		   <select style="width: 80px" id="category" onchange="isDlsCodeShow(this.value);">
		   <option value="0">RYF商户</option>
		   <option value="1">VAS商户</option>
		   <option value="2">POS商户</option>
		   <option value="3">POS代理商</option>
		   </select>
		   &nbsp;&nbsp;&nbsp;&nbsp;<span id="dlsSpan" style="display: none">代理商渠道号：<input style="width: 100px;" id="dlsCode" type="text"/></span>
		</td>
		</tr>
	   <tr>
		<td class="th1" align="right">&nbsp;合同日期：</td>
        <td align="left">
            <input id="begin_date" class="Wdate" type="text"
                   onfocus="WdatePicker({skin:'ext',minDate:'2010-01-01',maxDate:'2099-12-31',dateFmt:'yyyyMMdd',readOnly:'true'});" />   至
            <input id="exp_date" class="Wdate" type="text"
                    onfocus="WdatePicker({skin:'ext',minDate:'#F{$dp.$D(\'begin_date\')}',maxDate:'2099-12-31',dateFmt:'yyyyMMdd',readOnly:'true'});" />
            <font color="red">*</font>
           </td>
         <td class="th1" align="right">工商登记号：</td>
         <td align="left"><input type="text" id="reg_code" value="" maxlength="30"/>&nbsp;<font color="red">*</font></td>
		</tr>
		<tr>
		<td class="th1" align="right">开户银行名：</td>
        <td align="left"><input type="text" id="bank_name" value="" size="25" maxlength="50"/>&nbsp;<font color="red">*</font></td>
        <td class="th1" align="right" nowrap="nowrap">
                        开户银行支行名：
                    </td>
                    <td align="left">
                        <input type="text" id="bank_branch" value="" size="45"
                            maxlength="60"/>&nbsp;<font color="red">*</font>
             </td>
		</tr>
		<tr>
           <td class="th1" align="right" nowrap="nowrap">开户银行省份：</td>
            <td align="left"> <select id="bank_prov_id"></select>&nbsp;<font color="red">*</font></td>
            <td class="th1" align="right">&nbsp;开户日期：</td>
            <td align="left" nowrap="nowrap">
               <input id="open_date" class="Wdate" type="text"
                      onfocus="WdatePicker({skin:'ext',minDate:'1998-12-20',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});" />
               <font color="red">*</font>
            </td>
         </tr>
         <tr>
             <td class="th1" align="right">
                        &nbsp;开户账号名：
                    </td>
                    <td align="left">
                        <input type="text" id="bank_acct_name" value="" size="40"
                            maxlength="50"/>
                        <font color="red">*</font>
              </td>
              <td class="th1" align="right">
                        &nbsp;银行账号：
                    </td>
                    <td width="30%" align="left">
                        <input type="text" id="bank_acct" value="" size="20"
                            maxlength="32"/>
                        <font color="red">*</font>
               </td>
                 
         </tr>
		<tr>  
		<td class="th1" align="right">&nbsp; 证件：</td>
             <td align="left">
                 <select id="id_type" name="id_type"></select>号码
                        <input id="id_no" name="id_no" maxlength="30"/>&nbsp;<font color="red">*</font>
         </td>
         <td class="th1" align="right">&nbsp; 开户银行行号：</td>
             <td align="left">
                 <input id="open_bk_no" name="open_bk_no" maxlength="14" />&nbsp;<font color="red">*</font>
                 <input type="button" value="检索" onclick="SerchBankNoInfo();"  class="button"/>
         </td></tr>
				<tr>
					<td class="th1" align="right">结算对象：</td>
					<td align="left">
					<select style="width: 150px" id="liq_obj">
							<option value="0">银行账户</option>
							<option value="1">电银账户</option>
							<option value="2">代理商</option>
					</select>
					</td>
					<td class="th1" align="right">结算状态：</td>
					<td align="left">
					<select style="width: 150px" id="liq_state">
							<option value="0">正常</option>
							<option value="1">冻结</option>
					</select>
					</td>
				</tr>
				<tr>
					<td class="th1" align="right">手工结算：</td>
					<td align="left">
					<select style="width: 150px" id="man_liq">
							<option value="0">关闭</option>
							<option value="1">开启</option>
					</select>
					</td>
					<td class="th1" align="right">结算银行：</td>
						<td   align="left">	
						<select style="width: 150px" id="gateId">
						<option value="">全部....</option>
						</select> <font color="red">*</font>
						</td>	
				</tr>
				
				<tr>
					<td class="th1" align="right">银联(UPMP)商户号：</td>
					<td align="left">
					<input type="text" name="upmpMid" id="upmpMid" maxlength="15"  />
					</td>
					<td class="th1" align="right">结算满足额度(元)：</td>
                    <td align="left">
                        <input type="text" id="liq_limit" value="" maxlength="7" size="10"/>（0代表无限额,只能是整数）
                    </td>
				</tr>

				<tr>
					<td class="th1" align="right">是否手工代付：</td>
					<td align="left" colspan="3">
						<select style="width:150px" id="is_sgdf">
						<option selected="selected" value="0">否</option>
						<option value="1">是</option>
						</select>
					</td>
				</tr>
				<tr><td colspan="4" align ="center"><input type="button" value="申请修改" onclick="updateMinfoData();"  class="button"/></td></tr>
	  </table>
	  </form>

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
								<th sort="false" align="center">联行行号</th>
								<th sort="false" align="center">联行名称</th>
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
	</div>
  </body>
</html>
