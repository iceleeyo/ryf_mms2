<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>代付银行账户余额报警设置管理</title>
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
        <link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css"/>
        <link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css"/>
        <script type='text/javascript' src='../../dwr/engine.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../dwr/util.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/SysManageService.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>' ></script>
		<script type='text/javascript' src='../../public/js/ryt_util.js?<%=rand%>' ></script>
		<script type="text/javascript">
		var gids={};
		var emailReg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
		var phoneReg=/^0?(13[0-9]|15[012356789]|18[0236789]|14[57])[0-9]{8}$/;
			
		function init(){
			SysManageService.initDFYEBJ(function(data){
				 gids=data;
				 dwr.util.addOptions("gid", data);
			});	
		}

		var cellFuncs = [
		      			function(obj) { return obj.name; },
		      			function(obj) { return obj.accNo; },
		      			function(obj) { return div100(obj.alarmAmt); },
		      			function(obj) { return obj.alarmPhone; },
		      			function(obj) { return obj.alarmMail; },
		      			function(obj) { return obj.alarmStatus==0?"关闭":"开启"; },
		      			function(obj) { return "<button onclick='edit("+obj.gid+");'>配置</button> <button onclick='openOrClose("+obj.gid+","+obj.alarmStatus+");'>"+(obj.alarmStatus==0?"开启":"关闭")+"</button>"; }
		      		];
		
		function queryBankInfo(pageNo){
			var gid=$("gid").value;
			SysManageService.queryDFBankInfo(gid,pageNo,callback);
			}
			
		function callback(b2egates){
				$("bankInfo").style.display="";
				dwr.util.removeAllRows("resultList");
				if(b2egates.length<=0){
					alert("商户不存在!");
					return;
				}
		paginationTable(b2egates, "resultList", cellFuncs, "", "queryBankInfo");
		}
		
		function  edit(gid){
			if(gid!="")
			 	jQuery("#editPanel").wBox({title:"代付网关报警配置",show:true});
			 else{
			 	alert("网关号为空！");
			 	return;
			 }
			 SysManageService.queryDFBankInfoByGid(gid,function(data){
			 	$("update_gid").value=gid;
			 	$("edit_alarmAmt").value=div100(data.alarmAmt);
				$("edit_alarmPhone").value=data.alarmPhone;
				$("edit_alarmEmail").value=data.alarmMail;
				$("edit_alarmState").value=data.alarmStatus;
			 });
		}
		
		function updateConf(){
			var amt=$("edit_alarmAmt").value;
			var phones=$("edit_alarmPhone").value.trim();
			var emails=$("edit_alarmEmail").value.trim();
			var state=$("edit_alarmState").value;
			var gid=$("update_gid").value;
			if(gid==""){
				alert("信息异常！");
				return;
			}
			
			if(!isMoney(amt)){
				alert("金额格式错误！");
				return;
			}
			
			if(phones!=""){
				var phone=phones.split(",");
				for(var i=0;i<phone.length;i++){
					if(!phoneReg.test(phone[i])){
						alert("手机号格式错误！");
						return;
					}
				} 
			}
			
			if(emails!=""){
				var email=emails.split(",");
				for(var i=0;i<email.length;i++){
					if(!emailReg.test(email[i])){
						alert("邮箱格式错误!");
						return;
					}
				}
			}
			
			if(state==1 && phones=="" && emails==""){
				 alert("请至少输入一个正确的手机号或邮箱!");
				 return;
			}
			
			SysManageService.updateConf(gid,amt,phones,emails,state,function(flag){
				if(flag=="success")
					alert("修改成功!");
				else
					alert("修改失败！");
				jQuery("#editPanel").click();
				queryBankInfo(1);
			});
		}
		
		function openOrClose(gid,status){
			if(gid=="" && status==""){
					alert("信息异常！");
					return;
			}
			
			if (window.confirm("您将" + (status == 0 ? "开启" : "关闭")+ "报警功能，是否继续？")) {
					if (status == 0) {
						SysManageService.isEmptyPhoneAndEmail(gid,function(fl) {
									if (fl != "pass") {
										alert("手机号和邮箱均为空，无法直接开启，请在配置中开启！");
										return;
									}else{
						SysManageService.updateStateByGid(gid, (status == 0 ? "1": "0"), function(flag) {
						if (flag == "success")
							alert((status == 0 ? "开启" : "关闭") + "成功!");
						else
							alert((status == 0 ? "开启" : "关闭") + "失败！");
						queryBankInfo(1);
						});
									}
								});

					}else{
					
					SysManageService.updateStateByGid(gid, (status == 0 ? "1": "0"), function(flag) {
						if (flag == "success")
							alert((status == 0 ? "开启" : "关闭") + "成功!");
						else
							alert((status == 0 ? "开启" : "关闭") + "失败！");
						queryBankInfo(1);
						});
					}
				}
			}
		</script>
    </head>
<body onload="init();">
  <div class="style">
		<table width="100%" align="left" class="tableBorder">
			<tr>
				<td class="title" colspan="6">代付银行账户余额报警配置</td>
			</tr>
		
			<tr>
				<td class="th1" align="right" width="11%">选择银行：</td>
				<td colspan="5" align="left" width="30%">
					<select id="gid">
						<option value="">请选择...</option>
					</select>
				</td>
			</tr>
			
			<tr>
				<td colspan="6" align="center" style="height: 30px">
				<input class="button" type="button" value=" 查 询 " onclick="queryBankInfo(1);" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</td>
			</tr>
		</table>



		<table class="tablelist tablelist2" id="bankInfo"	style="display: none;">
			<thead>
				<tr>
					<th>代付银行</th>
					<th>代付银行账号</th>
					<th>报警金额</th>
					<th>接收报警手机号</th>
					<th>接收报警邮箱</th>
					<th>报警状态</th>
					<th>操作</th>
				</tr>
			</thead>
			<tbody id="resultList"></tbody>
		</table>

		<table id="editPanel" class="tableBorder" style="display:none; width: 400px; height: 150px; margin-top: 0px;">
			<tr>
				<td class="th1" bgcolor="#D9DFEE" align="right" width="35%">报警金额:</td>
				<td align="left"><input type="text" id="edit_alarmAmt" size="22" /> &nbsp;元 <font
					color="red">*</font></td>
			</tr>

			<tr>
				<td class="th1" bgcolor="#D9DFEE" align="right" width="35%">接收报警手机号:</td>
				<td align="left">
					<input type="text" id="edit_alarmPhone" size="22" />
					<font color="red">*</font>
				</td>
			</tr>
			
			<tr>
				<td class="th1" bgcolor="#D9DFEE" align="right" width="35%">接收报警邮箱:</td>
				<td align="left">
					<input type="text" id="edit_alarmEmail" size="22" /><font color="red">*</font> 
				</td>
			</tr>
			
			<tr>
				<td class="th1" bgcolor="#D9DFEE" align="right" width="35%">开启/关闭:</td>
				<td align="left">
					<select id="edit_alarmState">
   						<option value="1">开启</option>
						<option value="0">关闭</option>
					</select>
					<input type="hidden" id="update_gid" value=""/>
					<font color="red">*</font> 
				</td>
			</tr>
			
			<tr>
				<td colspan="4" align="center" height="20px;">
				<input value="修改"	type="button" onclick="updateConf();" class="wBox_close button"  /> 
				<input value="返回" type="button" class="wBox_close button" /></td>
			</tr>
			
		</table>
	</div>
</body>
</html>
