<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>单笔付款到企业银行卡(B2B)</title>
<%
		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);
		int rand = new java.util.Random().nextInt(10000);
		%>
			<meta http-equiv="pragma" content="no-cache" />
			<meta http-equiv="cache-control" content="no-cache" />
			<meta http-equiv="Expires" content="0" />
			<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
			<link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css" />
			<link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css" />
			<script type="text/javascript" src="../../dwr/engine.js"></script>
			<script type="text/javascript" src="../../dwr/util.js"></script>
			<script type='text/javascript' src="../../public/js/ryt.js?<%=rand%>"></script>
			<script type='text/javascript' src="../../public/js/money_util.js?<%=rand%>"></script>
			<script type='text/javascript' src="../../public/js/adminAccount/mer_fkdqyyhk.js?<%=rand%>"></script>
			<script type='text/javascript' src="../../public/js/md5.js?<%=rand%>"></script>
			<script type='text/javascript' src="../../public/js/ryt_util.js?<%=rand%>"></script>
			<script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
		    <script type="text/javascript" src="../../dwr/interface/PageService.js?<%=rand%>"></script>
			<script type="text/javascript" src='../../dwr/interface/MerchantService.js'></script>
			<script type="text/javascript" src='../../dwr/interface/DfB2BSingleService.js'></script>
</head>
<!-- hasButtonAuth([103,102]);权限 -->
<body onload=" initAid();">
	<div class="style">
		<div id="mainDiv">
			<table width="100%" align="left" class="tableBorder"
				id="orderInputTable">
				<tr>
					<td class="title" colspan="3">单笔付款到企业银行卡<span
						style=color:red>*</span>(为必填项)</td>
				</tr>
				<tr>
					<td class="th2" align="right">&nbsp; 付款账户：</td>
					<td colspan="2" align="left"><select
						style="width: 180px" class="largeSelect" id="aid" name="aid"></select>
					</td>
				</tr>
				<tr>
					<td class="th2" align="right" width="35%">付款金额：</td>
					<td align="left"><input type="text" name="payAmt"
						id="payAmt" value="" maxlength="11"
						onchange="setChineseMoney(this.value);" class="largeInput" /></td>
					<td align="left"><span class="redPoint">*</span> &nbsp;&nbsp;<span
						id="ChineseMoney" class="redfont"></span></td>
				</tr>
				<tr>
					<td class="th2" align="right">对方企业：</td>
					<td align="left">
						<!--  <select id="cusId" onchange="getUserBkAcc();"  class="largeSelect">
                <option value="">请选择...</option>
               </select> --> <input id="cusIdText" class="largeInput"
						value="" onkeyup="cusTextchange();" /> <input id="cusId"
						type="hidden" class="largeInput" value="0" /></td>
					<td align="left"><span class="redPoint">*</span>&nbsp; &nbsp;
						<a href="#" onclick="initAcc();">选择已有账户</a>
						&nbsp;&nbsp;&nbsp;没有企业客户？<a href="M_42_KHXXWH.jsp">增加</a></td>
				</tr>
				<tr>
					<td class="th2" align="right">对方账号：</td>
					<td align="left">
						<!--<select id="accNo" style="width: 270px;" class="largeSelect">
              <option value="">请选择...</option>
               </select>--> <input id="accNo" class="largeInput" /></td>
					<td align="left" width="50%"><span class="redPoint">*</span>
						&nbsp;&nbsp;没有账号？<a href="M_42_KHXXWH.jsp">增加</a> <input
						id="confirm_transAmt" type="hidden" /> <input
						id="confirm_transAmt_uppercase" type="hidden" /><input
						id="confirm_transFee" type="hidden" /></td>
				</tr>
				<tr>
					<td class="th2" align="right">收款银行：</td>
					<td align="left" colspan="2"><select style="width: 204px;"
						class="largeSelect" id="to_bk_no" name="to_bk_no">
							<option value="">请选择...</option>
					</select></td>
				</tr>
				<tr>
					<td class="th1" bgcolor="#D9DFEE" align="right" width="40%">收款银行联行行号：</td>
					<td align="left"><input id="to_openbk_no" name="to_openbk_no" style="width: 204px;" class="largeSelect" />
					<input type="hidden" name="to_openbk_name" id="to_openbk_name" />
					</td>	
				    <td align="left"><input type="button" value="检索" style="width: 40px"  onclick="SerchBankNoInfo();"
						class="button" /></td>
				</tr>
				<tr>
					<td class="th2" align="right">开户所在省份：</td>
					<td align="left" colspan="2"><select id="to_prov_id"
						style="width: 204px;" class="largeSelect">
					</select></td>
				</tr>
				<tr>
					<td class="th2" align="right">短信通知手机号：</td>
					<td align="left" colspan="2"><input type="text"
						id="payToPhoneNo" maxlength="11" class="largeInput" /></td>
				</tr>
				<tr>
					<td class="th2" align="right">订单描述：</td>
					<td align="left" colspan="2"><textarea rows="3" cols="30"
							id="remark" class="largerArea"></textarea></td>
				</tr>
				<!-- 
             <tr>
                 <td class="th2" align="right">付款银行：</td>
                <td align="left"  id="payBank"></td>
            </tr>
             -->
				<tr>
					<td colspan="3" align="center" style="height: 30px"><input
						class="button" type="button" value="提交订单" onclick="confirmMsg();" />
					</td>
				</tr>
			</table>
			<form action="#" method="get" target="_blank" id="toBkAction">
				<input type="hidden" name="p" id="paramVal" /> <input type="hidden"
					name="k" id="keyVal" />
			</form>
			<div style="display: none;width: 100%" id="confirmPayMsgDiv">
				<table width="100%" align="left" class="tableBorder">
					<tr>
						<td class="title" colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;付款确认&nbsp;&nbsp;</td>
					</tr>
					<tr>
						<td class="th2" align="right" width="35%">订单号：</td>
						<td class="largeTh" align="left" id="confirm_ordId"></td>
					</tr>
					<tr>
						<td class="th2" align="right">付款金额：</td>
						<td align="left" class="largeTh"><span id="confirm_payAmt"
							class="redfont"></span>&nbsp;元</td>
					</tr>
					<!--  
            <tr>
                 <td class="th2" align="right">应付金额：</td>
                <td class="largeTh" align="left" >
                <span id="confirm_transAmt" class="redfont"></span>&nbsp;&nbsp;&nbsp;大写：
                
                <span id="confirm_transAmt_uppercase" class="redfont"></span> </td>
            </tr>-->
					<tr>
						<td class="th2" align="right">对方企业编号：</td>
						<td class="largeTh" align="left" id="confirm_uid"></td>
					</tr>
					<tr>
						<td class="th2" align="right">对方企业客户名称：</td>
						<td class="largeTh" align="left" id="confirm_cusName"></td>
					</tr>

					<tr>
						<td class="th2" align="right">对方银行账号：</td>
						<td class="largeTh" align="left" id="confirm_bkAcc"></td>
					</tr>
					<tr>
						<td class="th2" align="right">收款银行：</td>
						<td class="largeTh" align="left" id="skyh"></td>
					</tr>
					 <tr>
                        <td class="th2" align="right">收款银行联行行号：</td>
                        <td class="largeTh" align="left"  id="confirm_bk_no"></td>
            </tr>

					<tr>
						<td class="th2" align="right">短信通知手机号：</td>
						<td class="largeTh" align="left" id="confirm_myPhoneNo"></td>
					</tr>
					<tr>
						<td class="th2" align="right">订单描述：</td>
						<td class="largeTh" align="left" id="confirm_remark"></td>
						<!--  </tr>
           
             <tr id="fkbank" name="fkbank" style="display: none">
                <td class="th2" align="right">付款银行：</td>
                <td>
                <select  id="selectBank" onchange="bankOnchange();">
               		<option value="">请选择...</option>
               		<option value="20001">中国银行B2B</option>
               	</select>
               </td>
               <!--  <td class="largeTh" align="left"  id="confirm_gate"></td> -->

					</tr>
					<tr align="center">
						<td colspan="2"><input
							id="balance" name="payway" onclick="balance();" class="button" type="button"
							value="确认提交 "  />&nbsp;&nbsp;&nbsp;
							<input class="button" type="button" value=" 返 回 "
							onclick="backModify();" /></td>
					</tr>
				</table>
			</div>
		</div>
		<div style="display: none;width: 560px;height: 170px;" id="operMsgDiv"
			align="center">
			<div style="width: 80%">
				<h2>请在打开的页面付款，付款完成前请不要关闭本页面</h2>
				<input type="hidden" id="ordId" value="" />
				<hr class="hrStyle" />
				<p align="left">
					如果付款成功，请点击按钮 <input type="button" value="已完成付款"
						class="wBox_close button" onclick="queryOrder();" />
				</p>
				<p align="left">
					如果付款不成功，你的银行卡未被扣款，请点击按钮 <input type="button" value="返回付款页面"
						class="wBox_close button" onclick="backPayPage();" />
				</p>
				<p align="left">如果您的银行卡已被扣款，但付款不成功，这可能是网络传输问题造成的，请放心我们会尽快把这笔款项打到你账户。</p>
			</div>
		</div>
		<input type="hidden" name="mid" id="mid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}" />
		<table id="paypwd" class="tableBorder" style="display: none;width: 400px;height: 120px;">
			<tr>
				<td class="th1" align="right">付款金额：</td>
				<td><span id="amt" class="redfont"></span>&nbsp;元</td>
			</tr>
			<tr>
				<td class="th1" align="right">服务费：</td>
				<td><span id="trfee" class="redfont"></span>&nbsp;元</td>
			</tr>
			<tr>
				<td class="th1" align="right">应付金额：</td>
				<td><span id="tramt" class="redfont"></span>&nbsp;元</td>
			</tr>
			<tr>
				<td class="th1" align="right">商户余额：</td>
				<td><span id="accbalance" class="redfont"></span>&nbsp;元</td>
			</tr>
			<tr>
				<td class="th1" align="right">支付密码：</td>
				<td><input id="payPassword" name="payPassword" type="password" onblur="pwd_bak(this.value);" /></td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<input type="button" value="账户余额支付" class="button" onclick="payForBalance();" />&nbsp;&nbsp;
					<input type="button" value="返 回"  class="wBox_close button"/>
				</td>
			</tr>
		</table>
	</div>
	<div style="display: none;width: 500px;height: 130px;" id="offlinepay"
		align="center">
		<div style="width: 80%">

			<table class="tableBorder">
				<tr>
					<td class="th1" align="right">付款金额：</td>
					<td><span id="offamt" class="redfont"></span>&nbsp;元</td>
				</tr>
				<tr>
					<td class="th1" align="right">服务费：</td>
					<td><span id="offtrfee" class="redfont"></span>&nbsp;元</td>
				</tr>
				<tr>
					<td class="th1" align="right">应付金额：</td>
					<td><span id="offtramt" class="redfont"></span>&nbsp;元</td>
				</tr>
				<tr style="display:none;">
					<td class="th1" align="right">商户余额：</td>
					<td><span id="offaccbalance" class="redfont"></span>&nbsp;元</td>
				</tr>
			</table>

			<br />
			<p>
				<input type="button" class="button wBox_close" onclick="offline();"
					value="确定" /> <input class="wBox_close button" type="button"
					value="取消" />
			</p>
		</div>
	</div>
	<div id="comBkMsg"
		style="width: 300px;height: 220px;font-size: 14px;display: none;overflow-x:auto;">
		<ul>
			<li>企业名称：</li>
			<li id="targetComName" class="largerLi" style="font-weight: bold;"><div
					align="center">
					<select id="scusId" onchange="getUserBkAcc();"
						style="width:200px; height: 24px;font-size:14px;">
						<option value="">请选择...</option>
					</select>
				</div></li>
			<li class="largerLi"><hr class="hrStyle" />
			</li>
			<li class="largerLi"><ul id="bkAccOptions"></ul>
			</li>
			<li id="targetComName" class="largerLi">&nbsp;&nbsp;</li>
			<li style="margin-left: 50px;"><input type="button" value="确定"
				class="wBox_close button" onclick="selectAcc();" />
			</li>
		</ul>
		<br />

	</div>
	<br />
	<br />
	<!--  
       <div id="orderResult" style="display:none;" align="center">
       <div class="msgDiv">
	    <br/><br/><br/>
	    <h2 id="order_other">您刚才提交的订单号为：<a href="javascript:;" id="ordId_result"></a> 
	    	<br/>支付状态为：<span style="color:red;" id="stateStr">交易处理中</span><br/>
	    	</h2>
	    	<br/><br/><br/>
	    <p>如果对支付情况还有疑问，请联系我们！</p><br/>
	    </div>
       </div>&nbsp;&nbsp;
   
   </div>
   -->
	<input type="hidden" id="pwdbak" value="" />
	 <table id="serchBankNo" class="tableBorder" style="display:none; width: 700px; height: 300px; margin-top: 0px;">
			<tr>
				<td class="th1" bgcolor="#D9DFEE" align="right" width="15%">开户行:</td>
				<td align="left">
				<select style="width: 150px" id="gate">
						<option value="">全部....</option>
				</select>&nbsp;&nbsp; 
				<input type="text" id="bk_name" style="width: 150px" value="请输入开户行关键字检索联行行号" onfocus="if(value==defaultValue){value='';this.style.color='#000';}"
				   onblur="if(!value){value=defaultValue;this.style.color='#999';}" style="color:#999999" /> <input type="button" value="检索"onclick="SerchBankNo(1);" class="button" />
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