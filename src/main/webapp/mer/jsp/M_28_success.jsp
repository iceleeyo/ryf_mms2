<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.rongyifu.mms.web.WebConstants"%>
<%@page import="com.rongyifu.mms.bean.LoginUser"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title></title>
<%
	response.setHeader("Pragma", "No-cache");
	response.setHeader("Cache-Control", "no-cache");
	response.setDateHeader("Expires", 0);
	int rand = new java.util.Random().nextInt(10000);
%>
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	    <link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="../../dwr/engine.js"></script>
    <script type="text/javascript" src="../../dwr/util.js"></script>
	<script type="text/javascript" src="../../dwr/interface/MerZHService.js?<%=rand%>"></script> 
    <script type='text/javascript' src="../../public/js/ryt.js?<%=rand%>"></script>
     <script type='text/javascript' src="../../public/js/money_util.js?<%=rand%>"></script>
    <script type='text/javascript' src="../../public/js/money_util.js?<%=rand%>"></script>
    <script type='text/javascript' src="../../public/js/adminAccount/offline_sec.js?<%=rand%>"></script>
     <script type='text/javascript' src="../../public/js/ryt_util.js?<%=rand%>"></script>
<style type="text/css">
.tableBorder1 {
	border-collapse: collapse;
	font-size: 12px;
	margin-top: 8px;
	width: 100%;
}

.tableBorder1 td {
	border-collapse: collapse;
	line-height: 170%;
}

.titleFont {
	background-color: #2B8AD0;
	color: #FFFFFF;
	font-family: "微软雅黑", "宋体", Tahoma, Arial;
	font-size: 12px;
	font-weight: bold;
	height: 22px;
	line-height: 20px;
}
.payamt {
	font-family: "微软雅黑", "宋体", Tahoma, Arial;
	font-size: 12px;
	font-weight: bold;
	color:red;
}
</style>
<script type='text/javascript'>
	function init(){
	var flag=$("flag").value;//1 为批量 2为单笔
	var oid=$("old_oid").value;
	if(flag==2){
		$("ddpch").innerHTML="订单号";
	}
	MerZHService.getOrderInfoByOid(oid,flag,function(data){
		if(data.length<=0){
			return;
		}
		$("mid").innerHTML=data[0].uid;
		$("name").innerHTML=data[0].name;
		$("oid").innerHTML=oid;
		$("payAmt").innerHTML= formatNumber(div100(data[0].payAmt));
		$("transFee").innerHTML=formatNumber(div100(data[0].transFee));
		$("transAmt").innerHTML=formatNumber(div100(data[0].transAmt));
		$("remark").innerHTML=data[0].remark;
	});
	}
	function continuePay(){
	var requri=$("requri").value;
	if(requri!=""&&requri!=null){
		location.href=requri;	
		}
	}
	function lookInfo(){
		location.href="M_39_ZHJYMXCX.jsp";
	}
</script>
</head>
<body onload="init();">
	<div class="style">
		<table class="tableBorder1" border="1">
			<tr>
				<td class="title" colspan="2">&nbsp;&nbsp;生成付款单</td>
			</tr>
			<tr align="center">
				<td align="right">
					<table border="0" align="center" width="85%">
						<tr>
							<td width="2%">&nbsp;</td>
							<td>
								<table class="tableBorder">
									<tr>
										<td class="titleFont" colspan="2" align="left">&nbsp;&nbsp;订单基本信息</td>
									</tr>
									<tr>
										<td align="right">商户号&nbsp;</td>
										<td align="left">&nbsp;<span id="mid"></span></td>
									</tr>
									<tr>
										<td align="right">商户名&nbsp;</td>
										<td align="left">&nbsp;<span id="name"></span></td>
									</tr>
									<tr>
										<td align="right"><span id="ddpch">订单批次号</span>&nbsp;</td>
										<td align="left">&nbsp;<span id="oid" class="redfont"></span></td>
									</tr>
									<tr>
										<td align="right">订单金额合计&nbsp;</td>
										<td align="left">&nbsp;<span id="payAmt" class="redfont"></span> 元</td>
									</tr>
									<tr>
										<td align="right">服务费合计&nbsp;</td>
										<td align="left">&nbsp;<span id="transFee" class="redfont"></span>  元</td>
									</tr>
									<tr>
										<td align="right">付款金额合计&nbsp;</td>
										<td align="left">&nbsp;<span id="transAmt" class="redfont"></span> 元</td>
									</tr>
									<tr>
										<td align="right">订单描述&nbsp;</td>
										<td align="left">&nbsp;<span id="remark"></span></td>
									</tr>

								</table></td>
							<td width="2%">&nbsp;</td>
							<td valign="top" align="left">
								<table class="tableBorder1">
									<tr>
										<td>您已<span class="redfont">成功提交</span>订单，请尽快完成付款！</td>
									</tr>
									<tr>
										<td>付款银行：中国银行</td>
									</tr>
									<tr>
										<td>&nbsp;</td>
									</tr>
									<tr>
										<td><b>付款步骤提示：</b>
										</td>
									</tr>
									<tr>
										<td>1、线下转账的收款账号为：<span class="redfont">6222 0030 3123 0133 139</span>。<input type="hidden" id="old_oid" value="<%=request.getParameter("oid")%>"/></td>
									</tr>
									<tr>
										<td>2、线下转账时请在备注信息内填写:订单批次号,以便款项核实。<input type="hidden" id="flag" value="<%=request.getParameter("flag")%>"/></td>
									</tr>
									<tr>
										<td>3、完成线下转账后，请在后台该订单处上传凭证。</td>
									</tr>
									<tr>
										<td>4、我方管理员将在系统内进行到账审核，审核通过后将付款到指定收款账户。</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td colspan="2" align="right"><input class="button" type="button"  onclick="continuePay();" value="继续下单" /></td>
							<td colspan="2" align="left"><input class="button" type="button" onclick="lookInfo();" value="查看订单状态" /></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
		<br />
	</div>
<input type="hidden" id="requri" value="<%=request.getParameter("uri")%>"/>
</body>
</html>
