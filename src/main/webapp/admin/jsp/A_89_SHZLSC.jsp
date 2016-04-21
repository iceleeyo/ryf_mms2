<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>操作手册上传</title>
		<link href="../../public/css/head.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="../../dwr/engine.js"></script>
        <script type="text/javascript" src="../../dwr/util.js"></script>
        <script type="text/javascript" src="../../dwr/interface/MerchantService.js"></script>
        <script type="text/javascript">
            function uploadOperationFile(){   
			    var uploadFile = dwr.util.getValue("uploadFile"); 
			    var filename=document.getElementById("uploadFile").value;
			    if (filename == '') {
			    	alert("请先选择文件!");
			    	return ;
			    }
                var pointIndex=filename.lastIndexOf(".");
                var fileFormat =filename.substring(pointIndex+1,filename.length);
			    if(chkSuffix(filename)==false){
			    		alert("文件格式错误!仅支持doc、docx");
			    		return;
			    }
			    MerchantService.uploadUserManual(uploadFile,fileFormat,function(data){
			    		alert(data); 
			    });   
			}
          //新增上传文件格式验证
          function chkSuffix(file){
              var reg=/(.doc|.docx)$/;
              return reg.test(file);
          }
        
        </script>
</head>
<body>
<div class="style">
		<table class="tableBorder">
		<tr><td class="title">&nbsp;&nbsp;&nbsp;&nbsp;商户资料上传</td></tr>
		<tr>
			<td align="center" style="height: 25px;">
		  		<input type="file" name="uploadFile" id="uploadFile" accept=".doc,.docx"/>&nbsp;&nbsp;&nbsp;&nbsp;
  				<input type="button" value="操作员手册上传" onclick="uploadOperationFile();" class="button"/>
  				&nbsp;&nbsp;&nbsp;&nbsp;<span style="color:red;">说明：重复上传将覆盖上一次上传的文件；</span>
			</td>
		</tr>
		</table>
</div>
</body>
</html>