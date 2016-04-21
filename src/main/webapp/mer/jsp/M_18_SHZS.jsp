<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>商户证书</title>
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
        <script type='text/javascript' src='../../dwr/engine.js'></script>
        <script type='text/javascript' src='../../dwr/util.js'></script>
        <script type='text/javascript' src='../../dwr/interface/MerMerchantService.js?<%=rand %>'></script>
        
        <script type='text/javascript'>

        function updateMerRsaFile(){
            var id = dwr.util.getValue('mid');
            var uploadFile = dwr.util.getValue('rsafile');
            if ($('rsafile').value == '') {
			    alert("请先选择文件!");
			    return ;
			}
            if(chkSuffix($('rsafile').value)==false){
                alert("文件格式错误!仅支持txt、der、crt、key、cer、pfx");
                return;
            }
            MerMerchantService.addMerRsaFile(id,uploadFile,function(msg){
                if('ok' == msg){
                    alert('导入成功');
                    //$('sxbut').disabled = "none";
                }else{
                    $('rsabut').disabled = '';
                    alert(msg);
                }
            });
        }
        //新增文件格式验证
        function chkSuffix(file){
            var reg=/(.txt|.der|.crt|.key|.cer|.pfx)$/;
            return reg.test(file);
        }
        
        </script>

    </head>
    <body>
     <div class="style">
         <table width="100%"  align="left"  class="tableBorder" >
            <tr><td class="title" colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;商户RSA公钥导入&nbsp;&nbsp;</td></tr>
            <tr style="height: 30px">
             <td class="th1" align="right" width="30%" >商户RSA公钥文件：</td>
                <td align="left" >
                    <input type="hidden" id="mid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}" />
                    <input type="file" id="rsafile" name="rsafile" style="height: 20px"   size="30px" accept=".txt,.der,.crt,.key,.cer,.pfx"/>
                     <font color="red">*</font>
                </td>
            </tr>
            
            <!-- 
             <tr style="height: 30px">
             <td class="th1" align="right" width="30%" >商户证明文件：</td>
                <td align="left" >
                    <input type="file" id="merfile" name="merfile" style="height: 20px"   size="30px"/>
                </td>
            </tr>
             -->
            
            <tr style="height: 50px">
            <td colspan="2" style="margin-left: 100px" >
                <input onclick="updateMerRsaFile();" id = "rsabut" style="height: 40px;width: 200px;margin-left: 300px" type="button" value = " 导入商户RSA公钥 " />
            </td>
            </tr>
        </table>
       
        </div>
    </body>
</html>
