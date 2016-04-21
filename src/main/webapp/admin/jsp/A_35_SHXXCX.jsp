<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>商户信息查询</title>
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
        <script type='text/javascript' src='../../dwr/engine.js'></script>
        <script type='text/javascript' src='../../dwr/util.js'></script>
		<script type='text/javascript' src='../../dwr/interface/PageService.js?<%=rand %>'></script>
		<script type='text/javascript' src='../../dwr/interface/MerchantService.js?<%=rand %>'></script>
		<script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
		 <script type="text/javascript" src='../../public/js/merchant/admin_search_info.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
	</head>
	<body onload="queryBegin();">
	<div class="style">
	
			<table class="tableBorder" >
				<tbody id="queryTiaojian">
					<tr>
						<td class="title" colspan="6">&nbsp;&nbsp;商户信息查询</td>
					</tr>
					<tr>
						<td class="th1" align="right" width="11%">&nbsp;商户号：</td>
						<td align="left" width="22%">
							<input type="text" id="mno" name="mno" size="30" ></input>
						</td>
						<td class="th1" align="right" width="11%">&nbsp;商户名称：</td>
						<td align="left" width="11%">
							<input type="text" name="name" id="name" size="30" maxlength="40"></input>
						</td>
						<td class="th1" align="right" width="11%">所在省份：</td>
                        <td align="left" width="22%">
                            <select name="prov" id="prov">
                                <option value="-1">全部</option>
                            </select>
                        </td>
						
					</tr>
					<tr>
						
						<td class="th1" align="right">结算周期：</td>
						<td align="left">
							<select name="liqPeriod" id="liqPeriod"></select>
						</td>
						<td class="th1" align="right">结算方式：</td>
						<td align="left">
							<select name="liq_type" id="liq_type"></select>
						</td>
						<td class="th1" align="right">状态标志：</td>
						<td align="left">
							<select name="stat_flag" id="stat_flag">
							<option value="">全部</option>
							<option value="0">正常</option>
							<option value="1">关闭</option>
							</select>
						</td>
					</tr>
					<tr>
						<td colspan="6" align="center">
							<input type="button" class= "button" name="submitQuery" value="查询" onclick="queryMinfos(1);" />
						</td>
						
					</tr>
				</tbody>
			</table>
	
		<table class="tablelist tablelist2" style="display: none;" id = "minfoList">
			<thead>
			<tr>
				<th>
					商户号
				</th>
				<th>
					商户名
				</th>
				<th>
					所在省份
				</th>
				<th>
					合同结束日期
				</th>
				<th>
					开户银行名
				</th>
				<th>
					银行帐号
				</th>
				<th>
					开户日期
				</th>
				<th>
					状态标志
				</th>
				<th>
				          结算对象
			    </th>
			    <th>
				          结算状态
			    </th>
			    <th>
				          手工结算
			    </th>
			     <th>
					详情
				</th>
			</tr>
			</thead>
			<tbody id="minfoListBody">
			</tbody>
		</table>
		
		
		<table class="tableBorder" id="xiangxi" style="display: none;">
			<tbody>
				<tr><td class="title" colspan="6">&nbsp;&nbsp;&nbsp;&nbsp;商户信息详情</td></tr>
				<tr>
                    <td class="th1" align="right" width="11%">&nbsp;商户号：</td>
                    <td width="22%" align="left" id="v_mid" ></td>
                    <td class="th1" align="right" width="11%">&nbsp;商户名：</td>
                    <td width="22%" align="left" id="v_name" ></td>
                    <td class="th1" align="right" width="11%">&nbsp;商户简称：</td>
                    <td width="22%" align="left" id="v_abbrev" ></td>
                </tr>
                <tr>
                    
                    <td class="th1" align="right" >&nbsp;所在省份：</td>
                    <td  align="left" id="v_prov_id" ></td>
                    <td class="th1" align="right" >&nbsp;签约人：</td>
                    <td  align="left" id="v_signatory" ></td>
                    <td class="th1" align="right" >&nbsp;合同起始日期：</td>
                    <td  align="left" id="v_begin_date" ></td>
                </tr>
                <tr>
                    <td class="th1" align="right" >&nbsp;合同结束日期：</td>
                    <td  align="left" id="v_exp_date" ></td>
                    <td class="th1" align="right" >&nbsp;联机查询：</td>
                    <td  align="left" id="v_mer_chk_flag" ></td>
                     <td class="th1" align="right" >&nbsp;结算方式：</td>
                    <td  align="left" id="v_liq_type" ></td>
                </tr>
                <tr>
                   
                    <td class="th1" align="right" >&nbsp;结算周期：</td>
                    <td  align="left" id="v_liq_period" ></td>
                    <td class="th1" align="right" >单笔限制金额(元)：</td>
                    <td  align="left" id="v_trans_limit" ></td>
                    <td class="th1" align="right" >结算满足的额度(元)：</td>
                    <td  align="left" id="v_liq_limit" ></td>
                </tr>
                <tr>
                    <td class="th1" align="right" >开户银行省份：</td>
                    <td  align="left" id="v_bank_prov_id" ></td>
                    <td class="th1" align="right" >&nbsp;开户银行名：</td>
                    <td  align="left" id="v_bank_name" ></td>
                     <td class="th1" align="right" >开户银行支行名：</td>
                    <td  align="left" id="v_bank_branch" ></td>
                </tr>
                <tr>
                    <td class="th1" align="right" >开户银行行号：</td>
                    <td  align="left" id="v_open_bk_no" ></td>
                    <td class="th1" align="right" >&nbsp;银行账号：</td>
                    <td  align="left" id="v_bank_acct" ></td>
                    <td class="th1" align="right" >&nbsp;开户账号名：</td>
                    <td  align="left" id="v_bank_acct_name" ></td>
                 
                </tr>
                <tr>
                   <td class="th1" align="right" >&nbsp;开户日期：</td>
                    <td  align="left" id="v_open_date" ></td>
                    <td class="th1" align="right" >&nbsp;商户开通费(元)：</td>
                    <td  align="left" id="v_begin_fee" ></td>
                    <td class="th1" align="right" >&nbsp;商户年费(元)：</td>
                    <td  align="left" id="v_annual_fee" ></td>
                </tr>
                <tr>
                   <td class="th1" align="right" >&nbsp;商户保证金(元)：</td>
                    <td  align="left" id="v_caution_money" ></td>
                    <td class="th1" align="right" >&nbsp;商户传真：</td>
                    <td  align="left" id="v_fax_no" ></td>
                    <td class="th1" align="right" >&nbsp;工商登记号：</td>
                    <td  align="left" id="v_reg_code" ></td>
                </tr>
                <tr>
                    <td class="th1" align="right" >&nbsp;商户地址：</td>
                    <td  align="left" id="v_addr" ></td>
                    <td class="th1" align="right" >&nbsp;商户邮编：</td>
                    <td  align="left" id="v_zip" ></td>
                     <td class="th1" align="right" >&nbsp;商户类别：</td>
                    <td  align="left" id="category" ></td>
                </tr>
                <tr>
                   
                    <td class="th1" align="right" >&nbsp;状态标志：</td>
                    <td  align="left" id="v_mstate" ></td>
                    <td class="th1" align="right" >&nbsp;是否允许退款：</td>
                    <td  align="left" id="v_refund_flag" ></td>
                    <td class="th1" align="right" >&nbsp;网上域名：</td>
                    <td  align="left" id="v_web_url" ></td>
                </tr>
                <tr>
                    <td class="th1" align="right" >法人姓名：</td>
                    <td  align="left" id="v_corp_name" ></td>
                     <td class="th1" align="right" >商户所属行业：</td>
                    <td  align="left" id="merTradeType"></td>
                    <td class="th1" align="right" >退款时退还手续费 ：</td>
                    <td  align="left"  id="refundFee"></td>
                 </tr>
                 <tr>
                    <td class="th1" align="right" >&nbsp;组织机构代码：</td>
                    <td  align="left" id="v_corp_code" ></td>
                    <td class="th1" align="right">组织机构代码有效期：</td>
					 <td  align="left" id="codeExpDate"></td>
					 <td class="th1" align="right">证件：</td>
					 <td  align="left" id="codeExpDate">
					       证件类型：<span id="v_id_type"></span> |号码:<span id="v_id_no"></span>  
					 </td>
                 </tr>
				<tr><td class="th1" align="right" >&nbsp;上次修改时间：</td>
                    <td  align="left" id="v_last_update" ></td> 
					<td class="th1" align="right" >&nbsp;结算对象：</td>
                    <td  align="left" id="liq_obj"></td>
                    <td class="th1" align="right" >商户类型：</td>
                    <td  align="left"  id="merType"></td>
				</tr>
				<tr>
				   <td class="th1" align="right" >手工结算：</td>
                   <td  align="left" id="man_liq"></td>
				   <td class="th1" align="right" >&nbsp;结算状态：</td>
                   <td  align="left" id="liq_state"></td>
				   <td class="th1" align="right" >&nbsp;结算银行：</td>
                   <td  align="left" id="v_gateId"></td>
				</tr>
				<tr>
				   	<td class="th1" align="right" >&nbsp;商户描述：</td>
              		<td align="left" colspan="3" id="v_mdesc"></td>
			   		<td class="th1" align="right">P2P托管业务：</td>
			    	<td>
				   		<select id="v_isPtop" disabled="disabled">
							<option value="0">否</option>
							<option value="1">是</option>
						</select>
					</td>	
				</tr>
				<tr>
					<td class="th1" align="right" >&nbsp;账户系统ID：</td>
					<td align="left" id="v_userId"></td>
					<td colspan="4"></td>
				</tr>
		    </tbody>
		</table>
 <table class="tableBorder" id="contactMsg" style="display: none;">
                <tr>
                <td class="title" align="center" width="150px">联系人</td>
                <td class="title" align="center" width="150px">联系人姓名</td>
                <td class="title" align="center" width="150px">联系人电话</td>
                <td class="title" align="center" width="150px">联系人手机</td>
                <td class="title" align="center" width="150px">联系人Email</td>
                </tr>
                <tr>
                    <td class="th1" align="right" >&nbsp; 主联系人：</td>
                    <td  align="left" id="contact0" ></td>
                    <td  align="left" id="tel0" ></td>
                    <td  align="left" id="phone0" >&nbsp; </td>
                    <td  align="left" id="email0" ></td>
                </tr>
                <tr>
                    <td class="th1" align="right" >&nbsp; 财务联系人：</td>
                    <td  align="left" id="contact1" ></td>
                    <td  align="left" id="tel1" ></td>
                    <td  align="left" id="phone1">&nbsp;</td>
                    <td  align="left" id="email1" ></td>
                </tr>
                <tr>
                    <td class="th1" align="right" >&nbsp; 技术联系人：</td>
                    <td  align="left" id="contact2" ></td>
                    <td  align="left" id="tel2" ></td>
                    <td  align="left" id="phone2" >&nbsp;</td>
                    <td  align="left" id="email2" ></td>
                </tr>
                <tr>    
                    <td class="th1" align="right" >&nbsp; 运行联系人：</td>
                    <td  align="left" id="contact3" ></td>
                    <td  align="left" id="tel3" ></td>
                    <td  align="left" id="phone3">&nbsp; </td>
                    <td  align="left" id="email3" ></td>
                </tr>
                <tr>
                    <td class="th1" align="right" >&nbsp; 市场联系人：</td>
                    <td  align="left" id="contact4" ></td>
                    <td  align="left" id="tel4" ></td>
                    <td  align="left" id="phone4">&nbsp;</td>
                    <td  align="left" id="email4" ></td>
                </tr>  
                <tr> 
                    <td class="th1" align="right" >&nbsp; 销售联系人：</td>
                    <td  align="left" id="contact5" ></td>
                    <td  align="left" id="tel5" ></td>
                    <td  align="left" id="phone5">&nbsp;</td>
                    <td  align="left" id="email5" ></td>
                </tr>
               
				<tr>
					<td colspan="5" align="center">
						<input type="button" class="button" value=" 返 回 " onclick="showView();"></input>
					</td>
				</tr>
		</table>
		
		</div>
	</body>
</html>
