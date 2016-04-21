<%@ page language="java" pageEncoding="UTF-8" %>
<% String path = request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>备份金银行管理-生成存管数据</title>
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
        <script type="text/javascript" src="../../dwr/engine.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../dwr/util.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../dwr/interface/PageService.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../dwr/interface/PrepPayService.js?<%=rand%>"></script>
		<script type="text/javascript" src="../../public/datePicker/WdatePicker.js?<%=rand%>"></script>
		<script type="text/javascript" src="../../public/js/ryt.js?<%=rand%>"></script>

    <script type="text/javascript">

    function genData(){
        var o = $("liqDate");
        if(!o||o.value==''){
            alert("请选择清算日期");
            return;
        }
        PrepPayService.genCGData(o.value,callback);
    }
    function callback(msg){
        if(msg=='ok'){
        	$("liqDate").value='';
        	alert("生成成功");
        }else{
        	alert(msg);
        }
        
    }
    
    
    </script>
    </head>
    <body >
    <div class="style">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="8">&nbsp;&nbsp;&nbsp;&nbsp;生成存管数据（生成存管数据后，合作银行余额（7.3）数据在 备付金管理/银行余额查询中查询）</td></tr>
            <tr>
                <td class="th1" align="right" width="30%">清算日期：</td>
                <td align="left" >
                   <input id="liqDate" name="liqDate" type="text" class="Wdate"
                        onfocus="WdatePicker({skin:'ext',minDate:'2011-01-01',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});"/>
                <font color="red">*</font>
                
                
                &nbsp;
                <input class="button" type="button" style="width: 150px" value = " 生成今天的存管数据 " onclick="genData()" />
                
                </td>
            </tr>
           
        </table>
        
       <table  class="tablelist tablelist2" style="margin-top: 40px">
        <tr>
             <td colspan="6" align="left"> 
             
             数据生成的日期：&nbsp;&nbsp;&nbsp;
             <input id="sysDate" name="sysDate" type="text" class="Wdate"
                        onfocus="WdatePicker({skin:'ext',minDate:'2011-01-01',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});"/>
                <font color="red">*</font>
                
                &nbsp;&nbsp;&nbsp;
                 <input class="button" type="button" style="width: 100px" value = " 查询" onclick="" />
               
                </td>
           </tr>
           <tr>
             <th>机构编号 </th><th>资金账户</th><th>资金余额</th><th>币种</th> 
             <th>清算日期</th><th>客户资金详细</th>
           </tr>
           <tbody id="resultList"></tbody>
       </table>
       
   
        </div>
    </body>
</html>
