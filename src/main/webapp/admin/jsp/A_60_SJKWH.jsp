<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    
    <title>数据库维护</title>
<%
response.setHeader("Pragma","No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", 0);
int rand = new java.util.Random().nextInt(10000);
%>  
	<meta http-equiv="pragma" content="no-cache"/>
	<meta http-equiv="cache-control" content="no-cache"/>
	<meta http-equiv="expires" content="0"/>    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3"/>
    <link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css"/>
	<script type='text/javascript' src='../../dwr/engine.js?<%=rand%>'></script>
    <script type='text/javascript' src='../../dwr/util.js?<%=rand%>'></script>
    <script type="text/javascript" src='../../dwr/interface/SysManageService.js?<%=rand%>'></script>

       
    <script type="text/javascript">
    function callProcedure(){
    
    if (confirm("不正确的手工调用存储过程会造成数据紊乱,请谨慎操作,是否继续?")){
	     var radios = document.getElementsByName("proce");
	     var count = 0;
	     for (var j=0;j<radios.length;j++){
	        if(radios[j].checked){
	            SysManageService.callProc(radios[j].value,callback);
	            count ++;
	         }
	     }
        if (count == 0)alert("请选择要手工调用的存储过程!");
        }
    }
    function callback(msg){
        if (msg == "ok"){ alert("存储过程调用成功!");}
        else{alert("存储过程调用失败!");}
    }
    </script>
  </head>
  
  <body>
   <div class="style">
   <table class="tablelist tablelist2"  >
        <tbody>
        <tr ><th>选择</th><th>存储过程名</th><th>操作说明</th></tr>
            <tr>
                <td ><input type="radio" value="switchdata" name="proce" checked="checked"/></td>
                <td  class="th1">Switchdata()</td>
                <td   align="left"> 将tlog表中当天以前的数据移至hlog表中</td>
            </tr>
            <tr>
                 <td  ><input type="radio" value="collect" name="proce"/></td>
                <td class="th1" >Collect()</td>
                <td >统计hlog中当前日期前一天的数据。</td>
            </tr>
            <tr>
                <td  ><input type="radio" value="dailyCollect" name="proce"/></td>
                <td class="th1" >dailyCollect()</td>
                <td >根据set_temporary表中的数据对每天的结算数据进行统计。</td>    
             </tr>
             <tr><td colspan="3" >
             <input type="button" value="手工调用" onclick="callProcedure()" class="button"/></td></tr>
        </tbody>
    </table>
    </div>
  </body>
</html>
