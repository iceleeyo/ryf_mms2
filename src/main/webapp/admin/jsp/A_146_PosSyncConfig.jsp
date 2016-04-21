<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% String path = request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Pos数据同步设置</title>
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
      <link href="<%=path %>/public/css/tab.css?<%=rand %>" rel="stylesheet" type="text/css">
      <style type="text/css">
      	.button{
      		margin-left: 10em;
      		width: 100px;
      		height: 25px;
      	}
    	.config{
    		font-size: 12px ;
			 padding-top: 20px;
		 	 background-color: #f2f0f0;
    	}
    	.config td{
    	 	height:30px;
    	}
    	.file_input{
    		height: 25px;
    	}
    	.input{
    		height:25px;
    	}
      </style>
      <script type="text/javascript" src="../../dwr/engine.js"></script>
      <script type="text/javascript" src="../../dwr/util.js"></script>
      <script type="text/javascript" src="../../dwr/interface/PageService.js?<%=rand %>"></script>
	  <script type="text/javascript" src="../../public/datePicker/WdatePicker.js"></script>
	  <script type="text/javascript" src="../../public/js/ryt.js?<%=rand%>"></script>
	  <SCRIPT type=text/javascript src="<%=path %>/public/js/jquery.idTabs.min.js?<%=rand %>"></SCRIPT>
	  <script type="text/javascript" src="../../dwr/interface/PosSyncConfig.js"></script>
	  <script type="text/javascript">
	  jQuery.noConflict();
	  (function($) { 
	   $(function(){ 
				     $("#usual1 ul").idTabs();
				 });
			})(jQuery);			
			
	  	 	function init(){
				PosSyncConfig.loadConfig(function(param){
					var hh=param[0];
					var MM=param[1]
					var isOpen=param[2];
					jQuery("#hh").val(hh);
					jQuery("#MM").val(MM);
					jQuery("#isOpen").val(isOpen);
				});  	 	
	  	 	}
	  	 	
			function sureEvent(){
				var hh=$("hh").value;
				var MM=$("MM").value;	
				var preTime=""+hh+""+MM;
				var isOpen=$("isOpen").value;
				PosSyncConfig.modifyConfig(""+preTime,isOpen,function(param){
					alert(param);	
					init();			
				});
			
			}
			
			function uploadDz(){
				var uploadF="uploadForm";
				var suffix="_POS_DZ.txt";//文件后缀名
				var file=jQuery("#upFDZ").val()
				if(file.indexOf(suffix)<=-1){
					alert("清算文件格式必须是yyyyMMdd"+suffix);
					return;
				}
				var form=jQuery("#uploadForm");
				ifm=jQuery("#uploadFm");
				form.submit();
				form[0].reset();
				ifm.load(function(){
						var res=jQuery.trim(jQuery(this.contentWindow.document.body).text());
						var msgBox={"success":"上传成功","typeErr":"文件类型选择错误",
								"extErr":"上传文件格式错误","expErr":"系统处理异常"};
						alert(msgBox[res]);
						if("success"==res){
							PosSyncConfig.beginRead(suffix,function(Bool){
							  if(Bool==true){
								location.href=location.href;
							  }
							}); //手工触发 文件读取
						}
					});
			}
			
			function uploadCc(){
				var uploadF="uploadForm";
				var suffix="_POS_CC.txt";//文件后缀名
				var file=jQuery("#upFCC").val()
				if(file.indexOf(suffix)<=-1){
					alert("清算文件格式必须是yyyyMMdd"+suffix);
					return;
				}
				var form=jQuery("#uploadForm");
				ifm=jQuery("#uploadFm");
				form.submit();
				form[0].reset();
				ifm.load(function(){
						var res=jQuery.trim(jQuery(this.contentWindow.document.body).text());
						var msgBox={"success":"上传成功","typeErr":"文件类型选择错误",
								"extErr":"上传文件格式错误","expErr":"系统处理异常"};
						alert(msgBox[res]);
						if("success"==res){
							PosSyncConfig.beginRead(suffix,function(Bool){
							  if(Bool==true){
								location.href=location.href;
							  }
							}); //手工触发 文件读取
						}
					});
			}
			
			
		</script>
</head>
<body onload="init();" >
	<div id="usual1" class="usual" >
		<div>
			<table align="left" width="100%" class="tableBorder" style="margin-bottom: 1.5em;width: 100%">
				<tr ><td   class="title"  colspan="5">&nbsp;&nbsp;&nbsp;POS数据自动同步设置</td></tr>
				<tr height="22px">
				<td align="right" width="150px" class="th1"><b>同步时间：</b></td>
				<td width="200px">
				<select id="hh" class="input" >
					<option selected="selected" value="00">00</option>
					<option value="01">01</option>
					<option value="02">02</option>
					<option value="03">03</option>
					<option value="04">04</option>
					<option value="05">05</option>
					<option value="06">06</option>
					<option value="07">07</option>
					<option value="08">08</option>
					<option value="09">09</option>
					<option value="10">10</option>
					<option value="11">11</option>
					<option value="12">12</option>
					<option value="13">13</option>
					<option value="14">14</option>
					<option value="15">15</option>
					<option value="16">16</option>
					<option value="17">17</option>
					<option value="18">18</option>
					<option value="19">19</option>
					<option value="20">20</option>
					<option value="21">21</option>
					<option value="22">22</option>
					<option value="23">23</option>
				</select>&nbsp;时
				<select id="MM" class="input" ><option value="00" selected="selected">00</option>
						<option value="10">10</option>
						<option value="20">20</option>
						<option value="30">30</option>
						<option value="40">40</option>
						<option value="50">50</option>
			   </select>&nbsp;分
				</td>
				<td align="right" width="150px" class="th1"><b>启用状态：</b></td>
				<td width="200px" align="left">
				<select id="isOpen" class="input" style="width:10em">
					<option value="0" selected="selected">开启</option>
					<option value="1">关闭</option>
				</select>
				</td>
				<td align="left" ><input type="button" class="button"  value="确 定" onclick="sureEvent();"/></td>
				</tr>
				<tr></tr>
			</table>
		</div>	
		   <!-- <div style="PADDING-BOTTOM: 6px;padding-left:1em;padding-top:6px;margin-bottom: -1px; color: #FFFFFF;height: 22px;background-color: #2B8AD0;"  >POS数据手工同步</div> -->
		   	<form action="<%=path %>/upload" id="uploadForm" method="post" enctype="multipart/form-data" target="uploadFm">
			   		<ul>  
					    <li><a  class="selected" href="#tab1" id="tablink1">清算对账</a></li> 
					    <li><a  href="#tab2" id="tablink2">差错对账</a></li> 
					</ul>
					<div id="tab1">
						<table  width="100%" align="left"
							class="tableBorder">
							<tbody>
								<tr id="tr_dz" width="100%">
									<td align="right" class="th1" width="150px"><b>清算对账文件：</b></td>
									<td colspan="4"><input  type="file" class="file_input" id="upFDZ" name="upFDZ" accept="text/plain" />
									</td>
								</tr>
								<tr>
									<td colspan="5"><input  type="button" class="button" onclick="uploadDz();" value="确 认"></td>
								</tr>
							</tbody>
						</table><br/>
					</div>
					
			 		<div id="tab2">
						<table width="100%" align="left"
							class="tableBorder">
							<tbody>
								<tr id="tr_cc" width="100%">
									<td  align="right" class="th1" width="150px"><b>差错处理文件：</b></td>
									<td colspan="4"><input type="file"  class="file_input" id="upFCC" name="upFCC"   accept="text/plain" />
									</td>
								</tr>
								<tr>
									<td colspan="5"><input type="button" class="button" onclick="uploadCc();" value="确 认"></td>
								</tr>
							</tbody>
						</table><br/>
					</div>
			<input type="hidden" name="resource" id="resource" value="TBWJSC"><!-- 同步源 -->
			<input type="hidden" name="fileType" id="fileType" value="3"><!-- 同步文件类型 3 PosDZ文件 -->
		</form>
	<iframe  name="uploadFm" id="uploadFm" style="display:none"></iframe>

	</div>
</body>
</html>
