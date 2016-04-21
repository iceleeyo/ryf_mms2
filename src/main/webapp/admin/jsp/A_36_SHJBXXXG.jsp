<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>商户基本信息修改</title>
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
        <script type='text/javascript' src='../../dwr/engine.js?<%=rand %>'></script>
        <script type='text/javascript' src='../../dwr/util.js?<%=rand %>'></script>
        <script type="text/javascript" src="../../dwr/interface/MerchantService.js?<%=rand%>"></script>              
        <script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
		<script type="text/javascript" src="../../public/datePicker/WdatePicker.js?<%=rand %>"></script>
		<script type="text/javascript" src='../../public/js/ryt.js?<%=rand %>'></script>
        <script type="text/javascript" src='../../public/js/merchant/mms2minfo.js?<%=rand %>'></script>
	</head>

	<body onload="initadd();initMinfos();">
	
	 <div class="style">
	
	<table class="tableBorder" >
             <tbody id="queryTiaojian">
              <tr><td class="title" colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;商户基本信息修改(带<font color="red">*</font>的为必填项)
                </td></tr>
                 
                 <tr>
                   <td class="th1" align="right" width="20%" style="height: 30px">&nbsp;商户号：</td><td>
                    <input type="text" id="mid" name="mid"   size="8px" onkeyup="checkMidInput(this);"/>
                        <!-- <select style="width: 150px" id="smid" name="smid" onchange="initMidInput(this.value)">
                     <option value="">全部...</option>
                   </select> -->&nbsp;&nbsp;<font color="red">*</font>
                     &nbsp;&nbsp;&nbsp;&nbsp;
                     <input type="button" value="修改基本信息" onclick="editMid('b');"/>&nbsp;&nbsp;&nbsp;&nbsp;
                     <input type="button" value="修改联系人信息" onclick="editMid('c');"/>&nbsp;&nbsp;&nbsp;&nbsp;
					<!-- <input type="button" value="修改商户类别" onclick="editMid('d');"/> -->
                    
                     </td>
                 </tr>
             </tbody>
       </table>

    <table class="tableBorder" cellpadding="0" cellspacing="0" style="margin-bottom: 0px; ">
          <tbody>
              <tr ><td colspan="4"  id="editedMsg" align="center" class="title">请输入您要修改的商户号</td></tr>
               <tr><td colspan="4">  <input type="hidden" value="" id="editedMefId"/></td></tr>
          </tbody>
    </table>	
      <table class="tableBorder" id="editMerInfoLXRTB" style="display: none;margin-top: 0px;padding-top: 0px;" cellpadding="0" cellspacing="0" >
        <tr>
         <td class="th1" align="center"></td>
         <td class="th1" align="center"> 联系人姓名</td>
         <td class="th1" align="center">联系人电话</td>
         <td class="th1" align="center">联系人手机</td>
         <td class="th1" align="center">联系人Email</td> 
        </tr>
        <%
        for(int i = 0;i < 6 ; i++){
        	%>
         <tr>
          <td class="th1" align="center">
          <%=
          i==0 ? "主联系人" : i==1 ? "财务联系人" : i==2 ? "技术联系人" : i==3 ? "运行联系人" : i==4 ? "市场联系人" : "销售联系人"
         
          %></td>
          <td align="left"><input type="text" id="contact<%=i%>" value='' size="20" maxlength="10" /><%= i==0 ? "<font color=\"red\">*</font>":"" %></td>
          <td align="left"><input type="text" id="tel<%=i%>" value='' size="40" maxlength="20"/><%= i==0 ? "<font color=\"red\">*</font>":"" %></td>
          <td align="left"><input type="text" id="cell<%=i%>"  size="40" maxlength="20"/><%= i==0 ? "<font color=\"red\">*</font>":"" %></td>
          <td align="left"><input type="text" id="email<%=i%>"  size="40" maxlength="40"/><%= i==0 ? "<font color=\"red\">*</font>":"" %></td>
         </tr>
        <%
        }
        
        %>
   
      
      <tr><td colspan="6" align="center"><input type="button" value=" 修改 "  onclick="editMinfoLXR()" class="button"/></td></tr>
                        
      </table>
      <form action="#" id="basicMinfoForm">
      <input type="hidden" id="gate_id" value="" />
      
      <table class="tableBorder"  id="eiditMinfoClass" style="display: none;margin-top: 0px;padding-top: 0px;" cellpadding="0" cellspacing="0" >
		<tr><td class="th1" align="right">修改商户类别：</td><td>
			<select style="width: 150px" id="category">
			<option value="0">普通商户</option>
			<option value="1">特殊商户</option>
			<option value="2">支持优惠价商户</option>
			</select>
			</td></tr>
		<tr><td colspan="2" align ="center"><input type="button" value="修改" onclick="editMinfoCategory()"  class="button"/></td></tr>
	  </table>
       
	
		<table class="tableBorder"  id="editBaseMerForm"   style="display: none;margin-top: 0px;padding-top: 0px;" cellpadding="0" cellspacing="0" >
           
    		<tbody>
				
			 <tr>
                    <td class="th1" width="20%" align="right">
                        &nbsp;商户名：
                    </td>
                    <td width="30%" align="left">
                        <input type="text" id="name" value="" size="40" maxlength="40" disabled="disabled"/>
                    </td>
                    <td class="th1" width="20%" align="right">
                        &nbsp;商户简称：
                    </td>
                    <td width="30%" align="left">
                        <input type="text" id="abbrev" value="" size="20" maxlength="16" disabled="disabled"/>
                        <font color="red">*</font>
                    </td>
                </tr>
                <tr>
                    <td class="th1" align="right">
                        &nbsp;所在省份：
                    </td>
                    <td align="left">
                        <select id="prov_id"></select>
                        <!-- <font color="red">*</font> -->
                    </td>
                    <td class="th1" align="right">
                        &nbsp;商户地址：
                    </td>
                    <td align="left">
                        <input type="text" id="addr" value="" size="50" maxlength="80"/>
                    </td>
                </tr>


                <tr>
                    <td class="th1" align="right">
                        &nbsp;结算周期：
                    </td>
                    <td align="left">
                        <select id="liqPeriod"></select>
                        <font color="red">*</font>
                    </td>
                    <td class="th1" align="right">
                        &nbsp;结算方式：
                    </td>
                    <td align="left">
                        <select id="liq_type">
                            <option value="2">
                                净额
                            </option>
                            <option value="1">
                                全额
                            </option>
                        </select>
                    </td>
                </tr>

                <tr>
                    <td class="th1" align="right" >商户类型：</td>
                    <td  align="left" >
                    <select id="merType" name="merType">
                    	<option value="0">企业</option>
                    	<option value="1">个人</option>
                    	<option value="2">集团</option>
                    </select></td>
                    <td class="th1" align="right">
                        &nbsp; 商户邮编：
                    </td>
                    <td align="left">
                        <input type="text" id="zip" value="" maxlength="6"/>
                    </td>
                </tr>
                <tr>
                    <td class="th1" align="right">
                        &nbsp;商户开通费(元)：
                    </td>
                    <td align="left">
                        <input type="text" id="begin_fee" value="" size="20" maxlength="7"/>
                        <font color="red">*</font>
                    </td>
                    <td class="th1" align="right">
                        &nbsp;商户年费(元)：
                    </td>
                    <td align="left" nowrap="nowrap">
                        <input type="text" id="annual_fee" value="" size="20"
                            maxlength="7"/>
                        <font color="red">*</font>
                    </td>
                </tr>
                <tr>
                    <td class="th1" align="right">
                        &nbsp;商户保证金(元)：
                    </td>
                    <td align="left">
                        <input type="text" id="caution_money" value="" size="20"
                            maxlength="7"/>
                        <font color="red">*</font>
                    </td>
                    <td class="th1" align="right">
                        &nbsp;签约人：
                    </td>
                    <td align="left">
                        <input type="text" id="signatory" value="" size="20"
                            maxlength="10"/>
                        <font color="red">*</font>
                    </td>
                </tr>

                <tr>
                    <td class="th1" align="right">
                        &nbsp; 网上域名：
                    </td>
                    <td align="left">
                        <input type="text" id="web_url" value="" size="35" maxlength="80"/>
                    </td>
                    <td class="th1" align="right">&nbsp;商户传真：
                    </td>
                    <td align="left">
                        <input type="text" id="fax_no" value="" size="20" maxlength="20"/>
                    </td>
                </tr>
                <tr><td height="24px" class="th1" align="right"> 商户所属行业： </td>
					<td>
						<select id="merTradeType" name="merTradeType" style="width: 150px">
								<option value="">请选择.... </option>
						</select>&nbsp;<font color="red">*</font>
					</td>
					<!-- 
                    <td class="th1" align="right" nowrap="nowrap">
                        &nbsp; 默认计费公式：
                    </td>
                    <td align="left">
                        <input type="text" id="default_fee_model" value="" size="20" disabled="disabled" /> 如需要修改请进行 手续费维护  操作
                    </td> -->
						<td class="th1" align="right">退款时退还手续费 ：</td>
						<td colspan="" align="left">

							<select id="refundFee" name="refundFee" ><option value="0">不退还</option><option value="1">退还</option></select>
						</td>

                </tr>
				<tr>		

				   <td class="th1" align="right">
                        &nbsp; 商户描述：
                    </td>
                    <td align="left" colspan="3" >
                        <textarea id="mdesc" cols="65" rows="1" ></textarea>
                    </td>
				</tr>
				<tr>
					<td colspan="4" align="center">
						<input type="button" value="修  改" onclick="editBase();" class="button"/>
					</td>					
				</tr>
			</tbody>
		</table>
		</form>
		</div>
	</body>
</html>
