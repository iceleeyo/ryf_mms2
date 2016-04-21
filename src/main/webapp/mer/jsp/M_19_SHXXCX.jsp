<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>商户信息</title>
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
        <link href="../../public/css/head.css?v=" rel="stylesheet" type="text/css"/>
       
        <script type="text/javascript" src="../../dwr/engine.js"></script>
        <script type="text/javascript" src="../../dwr/util.js"></script>
        <script type="text/javascript" src="../../dwr/interface/PageService.js?<%=rand %>"></script>
		<script type="text/javascript" src="../../dwr/interface/MerMerchantService.js?<%=rand %>"></script>
		<script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
        <script type="text/javascript" src='../../public/js/ryt.js?<%=rand %>'></script>
        <script type="text/javascript" src='../../public/js/merchant/mer_jsp_edit_Minfo.js?<%=rand%>'></script>
    </head>
    <body onload="init('${sessionScope.SESSION_LOGGED_ON_USER.mid}');">
    <div class="style">
        
		<table class="tableBorder">
			<tbody>
				<tr><td class="title" colspan="6">&nbsp;&nbsp;商户信息</td></tr>
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
                     <td class="th1" align="right" >&nbsp;商户类型：</td>
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
                    <td  align="left" id="v_mer_trade_type"></td>
                    <td class="th1" align="right" >退款时退还手续费 ：</td>
                    <td  align="left"  id="v_refund_fee"></td>
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
                    <td class="th1" align="right" >&nbsp;结算状态：</td>
                    <td  align="left" id="v_liqstate" ></td>
					<td class="th1" align="right" >&nbsp;商户描述：</td>
                    <td  align="left" id="v_mdesc"></td>
				</tr>
                </tbody>
                </table>
                
               <table class="tableBorder" >
                <tbody>
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
               
			</tbody>
		</table>
		
		      <table class="tableBorder" >
                <tbody>
                <tr>
                <td class="title" align="center" width="200px">同一卡号日成功交易次数</td>
                <td class="title" align="center" width="200px">同一卡号日失败交易次数</td>
                <td class="title" align="center" width="200px">同一手机号日成功交易次数</td>
                <td class="title" align="center" width="200px">同一手机号日失败交易次数</td>
                <td class="title" align="center" width="200px">同一身份证号日成功交易次数</td>
                <td class="title" align="center" width="200px">同一身份证号日失败交易次数</td>
                </tr>
                <tr>
                    <td  align="center"  id="v_accSuccCount"></td>
                    <td  align="center"  id="v_accFailCount"></td>
                    <td  align="center"  id="v_phoneSuccCount"></td>
                    <td  align="center"  id="v_phoneFailCount"></td>
                    <td  align="center"  id="v_idSuccCount"></td>
                    <td  align="center"  id="v_idFailCount"></td>
                </tr>
            </tbody>
        </table>
		
		
		 </div>
    </body>
</html>
