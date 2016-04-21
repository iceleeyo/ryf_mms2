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
		<meta http-equiv="cache-control" content="no-cache"/>
		<meta http-equiv="expires" content="0"/>
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3"/>
		<meta http-equiv="description" content="This is my page"/>
		<link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css"/>
		<script type='text/javascript' src='../../dwr/engine.js?<%=rand%>'></script>
		   <script type="text/javascript" src='../../public/js/ryt.js?<%=rand%>'></script>
		   <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
      
		<script type="text/javascript" src='../../dwr/interface/MerchantService.js?<%=rand %>'></script>
		<script type="text/javascript" >
		function merOpen() {

		    var mid = document.getElementById("mid").value;
		    if(mid=='') {
		        alert("请输入商户号");
		        return;
			}
		    if(!isFigure(mid)){
		    	  alert("输入商户号不正确");
	                return;
			}
			
		    MerchantService.merOpen(mid, function callback(msg) {
		        alert(msg);
		    });
		}
		</script>
		
		
	</head>

	<body>
    <div class="style">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" align="center">商户开启</td></tr>
            <tr>
                <td align="center" height="35px">商户号：
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <input type="text" id="mid" name="mid" style="width: 150px"  />
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                 <input type="button" value = "开 启" name="submitQuery" onclick="merOpen();" />
                </td>
            </tr>
        </table>

        
      </div>
	</body>
</html>
