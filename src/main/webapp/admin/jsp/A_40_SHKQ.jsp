<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>

		<title>商户开启</title>
 <%
response.setHeader("Pragma","No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", 0);
int rand = new java.util.Random().nextInt(10000);
%>
		<meta http-equiv="pragma" content="no-cache" />
		<meta http-equiv="cache-control" content="no-cache" />
		<meta http-equiv="expires" content="0" />
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3"/>
		<meta http-equiv="description" content="This is my page"/>
		<link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css"/>
		<link href="../../public/css/tab.css?<%=rand %>" rel="stylesheet" type="text/css"/>
		<script type='text/javascript' src='../../dwr/engine.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../dwr/util.js?<%=rand%>'></script>
		<script type="text/javascript" src='../../dwr/interface/MerchantService.js?<%=rand %>'></script>
		<script type="text/javascript" src='../../public/js/merchant/admin_open_mer.js?<%=rand%>'></script>
		<script type=text/javascript src='../../public/js/ryt.js?<%=rand %>'></script>
		<script type=text/javascript src='../../public/js/jquery.idTabs.min.js?<%=rand %>'></script>
		
		<script type="text/javascript">
			jQuery.noConflict();
				(function($) { 
					 $(function(){ 
					     $("#usual1 ul").idTabs();
					 });
				})(jQuery);
			
			function merOpen() {
					MerchantService.merOpen($("mid").value, function callback(msg) {
						alert(msg);
					});
				}
			function transAccOpen() {
					var accMid=$("accMid").value;
					if(accMid==''){alert("输入开启商户号！");return;}
					MerchantService.transAccOpen($("accMid").value, function callback(msg) {
						alert(msg);
					});
				}
		</script>
	</head>

	<body>
    <div class="style">    	
		<div id="usual1" class="usual"> 
			<ul>  
			    <li><a  class="selected" href="#tab1" id="tablink1">商户开启</a></li> 
			    <!-- <li><a  href="#tab2" id="tablink2">账户开启</a></li> -->
			</ul> 
		
			<div id="tab1">
		         <table width="100%"  align="left"  class="tableBorder">
		            <tr>
		            	<td class="title" align="center">&nbsp;</td>
		            </tr>
		            <tr>
		            	<td align="center" height="35px">商户号：
			                &nbsp;&nbsp;
			                <input type="text" id="mid" name="mid"  />
			           		&nbsp;&nbsp;&nbsp;&nbsp;
			                <input type="button" value = "开 启" name="submitQuery" onclick="merOpen();" />
		                </td>
		            </tr>
		        </table>
		        <div></div>
			</div>
			
			<!-- <div id="tab2">
				<table width="100%"  align="left"  class="tableBorder">
					<tr>
		            	<td class="title" align="center">&nbsp;</td>
		            </tr>
		            <tr>
		            	<td align="center" height="35px">商户号：
			                &nbsp;&nbsp;
			                <input type="text" id="accMid" name="accMid"  />
			                &nbsp;&nbsp;&nbsp;&nbsp;
			                <input type="button" value = "开启交易账户" name="submitQuery" onclick="transAccOpen();" />
		                </td>
		            </tr>
		        </table>
		        <div></div> 
			</div>-->
        </div>
      </div>
	</body>
</html>
