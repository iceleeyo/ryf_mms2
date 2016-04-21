<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>账户信息维护</title>
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
        <script type="text/javascript" src="../../dwr/engine.js?<%=rand %>"></script>
        <script type="text/javascript" src="../../dwr/util.js?<%=rand %>"></script>
        <script type="text/javascript" src='../../dwr/interface/MerZHService.js?<%=rand%>'></script> 
        <script type="text/javascript" src="../../public/js/merchant/mer_zhxxwh.js?<%=rand%>"></script>
        <link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css"/>
 		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
 		<script type="text/javascript" src="../../public/js/md5.js"></script>
    </head>
    <body onload="initZHXX();">
    <div class="style">
     <input type="hidden"  name="mid" id="mid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}" />
		<table class="tablelist tablelist2">
			<thead>
				<tr>
					<th>选择</th>
					<th>商户号</th>
					<th>账户ID</th>
					<th>账户名称</th>
					<th>注册日期</th>
					<th>状态</th>
					<th>余额支付功能</th>
					<th>单笔限额</th>
					<th>月交易限制金额</th>
					<th>同一收款卡号月累计成功次数</th>
					<th>同一收款卡号月累计成功金额</th>
					<th>充值计费公式</th>
					<th>提现计费公式</th>
					<th>代发计费公式</th>
					<th>代付计费公式</th>
				</tr>
			</thead>
			<tbody id="resultList">
			</tbody>
		</table>
		<p style="margin-top: 10px">
            <input id="kqzfbut" disabled="disabled" type="button" value= "开启余额支付功能" size="100" onclick="editPF(1);"/> 
            <input id="gbzfbut" disabled="disabled" type="button" value= "关 闭余额支付功能" size="100" onclick="editPF(2);"/> 
            <input id="gbzh" disabled="disabled" type="button" value= " 关 闭  账 户 " onclick="editPF(3);"/> 
       
       </p>
       
       <table id="body4One"  class="tableBorder" style="display: none;width: 300px;height: 100px;">
				<tbody>
					
						<tr>
						<td class="th1" align="right" width="50%">
							&nbsp; 请输入您的支付密码：
						</td>
						<td width="50%" align="left">
							<input type="password" id="pwd"  size="20"/>
							<input type="hidden" id="hidden_type"  value=""/>
						</td>
					</tr>
					
					<tr>
						<td align="center"   colspan="2">
							
							没有维护支付密码？<a href="M_35_ZFMMWH.jsp">支付密码维护</a>
						</td>
					</tr>
					
					<tr>
						<td colspan="2" align="center">
							<input type="button" value="确  定" onclick="edit();" class="button"/>&nbsp;&nbsp;
							<input type="button" value="返 回"  class="wBox_close button"/>
						</td>
					</tr>
				</tbody>
			</table> 
           
      </div>  
      
       
    </body>
</html>

           
           
    
