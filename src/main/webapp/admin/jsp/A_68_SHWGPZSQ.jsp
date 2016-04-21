<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>商户网关配置申请</title>
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
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand %>'></script>
		<script type="text/javascript" src='../../dwr/interface/MerchantService.js?<%=rand %>'></script>
		<script type="text/javascript" src='../../dwr/interface/CommonService.js?<%=rand%>'></script>
		<script type="text/javascript" src="../../public/datePicker/WdatePicker.js?<%=rand %>"></script>
		<script type="text/javascript" src="../../public/js/GreyFrame.js?<%=rand%>"></script>
        <script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../public/js/merchant/admin_minfo_gate_edit.js?<%=rand%>'></script>
	</head>

	<body onload="initParam();"  >
		<div class="style">
		<table class="tableBorder" >
		<tbody>
		<tr >
		<td colspan="5" class="title"><span style="float:left;">&nbsp; &nbsp;商户网关配置申请</span></td>
		</tr> 

		<tr>
	        <td class="th1" align="right" width="20%" style="height: 30px">商户号：</td>
			<td align="left" > 
				<input type="text" id="mid" name="mid" size="8px" onkeyup="checkMidInput(this);"/>
			</td>
	        <td class="th1" align="right" width="20%" style="height: 30px">网关类型：</td>
			<td align="left" > 
				<select id="trans_mode">
				</select>
			</td>
			<td>
			<input type="button" value="查  询" onclick="queryGate_init();" class="button"/>
			</span>
			</td>      	
		</tr>
		</tbody>
		</table>
		<form name='notSupportGatesForm'>
		<table class="tableBorder" id="gateRouteTable" style="display:none;" >
		<tbody>
		  <tr><td id="gateRouteTitle" style="background-color:#c0c9e0;"> </td></tr>
		  <tr><td id="gateRouteCell"> </td></tr>
		</tbody>
		<tfoot><tr><td>
			<input type="button" value="全选" onclick="checkAll('rytGate')" class="button" />
	        <input type="button" value="反选" onclick="checkReverseByName('rytGate')" class="button"/>
	        <input type="button" value="添加至商户"  onclick="AddToMer(this.form)" style="width: 100px;margin-left: 30px;height:25px "/>
		</td></tr></tfoot>
		</table>
		</form>


			<table class="tablelist tablelist2" id = "openGatesTable" style="display: none">

				<tr>
					<td bgcolor="#c0c9e0" colspan="11"><span style="float:left;">&nbsp; &nbsp;商户已开通的支付网关</span></td>
				</tr> 

			<tr>
				<td  height="30px" colspan="11">
					<span style="float:left;">商户手续费配置：
					<select id="selected_gates_trans_model_id">
					<option value="-1">所有支付网关</option>
					<option value="1">个人网银支付</option>
					<option value="2">消费充值卡支付</option>
					<option value="3">信用卡支付</option>
					<option value="5">增值业务</option>
					<option value="6">语音支付</option>
					<option value="7">企业网银支付</option>
					<option value="8">手机WAP支付</option>
					<option value="10">POS支付网关</option>
					<option value="11">对私代付</option>
					<option value="12">对公代付</option>
					<option value="13">B2B充值</option>
					<option value="14">线下充值</option>
					<option value="15">对私代扣</option>
					<option value="16">对公提现</option>
					<option value="17">对私提现</option>
					<option value="18">快捷支付</option>
					</select>

					使用计费公式 ：
					<input type="text" id="mer_calc_model_id" value=""  />
					<input type="hidden" id="selected_mer_id" value=""  />
					<input type="button" value="检查"  style="width: 50px;margin-left: 5px" onclick="checkModel(this)"/>
					<input type="button" value="修改" style="width: 80px;margin-left: 5px" onclick="editMerFeeModel()" id="ensure" disabled=disabled/>
					<a href="feemodel_details.html" target="INLINEIFRAME"><font color="blue">查看公式详情</font> </a>
					</span>
					<span style="float:center;">显示：
						<select onchange="showMerFeeCalaModels()" id="show_mer_fee_model_id">
						<option value="-1">所有支付网关</option>
						<option value="1">个人网银支付</option>
						<option value="2">消费充值卡支付</option>
						<option value="3">信用卡支付</option>
						<option value="5">增值业务</option>
						<option value="6">语音支付</option>
						<option value="7">企业网银支付</option>
						<option value="8">手机WAP支付</option>
						<option value="10">POS支付网关</option>
						<option value="11">对私代付</option>
						<option value="12">对公代付</option>
						<option value="13">B2B充值</option>
						<option value="14">线下充值</option>
						<option value="15">对私代扣</option>
						<option value="16">对公提现</option>
						<option value="17">对私提现</option>
						<option value="18">快捷支付</option>
						</select>
					</span>&nbsp;&nbsp;&nbsp;&nbsp;
					<span style="float:center;">状态：
						<select onchange="showMerFeeCalaModels()" id="status1">
						<option value="-1">全部</option>
						<option value="1">开启</option>
						<option value="0">关闭</option>
						</select>
					</span>
				</td>

			</tr> 
			<tr>
				<th width="5%"><input id="toggleAll" type="checkbox" style="vertical-align: middle;" onclick="toggleAll(this);"/>全选</th>
				<th width="5%">商户号</th>
				<th  width="5%">网关号</th>
				<th  width="10%">网关简称</th> 
				<th  width="10%">银行网关号</th>
				<th  width="10%">交易方式</th>
				<th  width="15%">银行手续费</th>
				<th  width="15%">商户手续费</th>
				<th  width="10%">正在使用</th>
				<th  width="5%">状态</th>
				<!-- 
				<th  width="5%">关闭的原因</th>
				-->
				<th>操作</th>
			</tr>
			<tbody id="FeeModelTable"></tbody>
			</table>
		</div>
	</body>
</html>
