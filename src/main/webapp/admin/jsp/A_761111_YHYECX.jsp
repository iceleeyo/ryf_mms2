<%@ page language="java" pageEncoding="UTF-8" %>
<% String path = request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>备份金银行管理</title>
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
		function initOption(){
			PrepPayService.getBkNoMap(function(bkNoMap){
				dwr.util.addOptions("cooperateBank",bkNoMap);
			});
		}
		function queryBankBalanceLog(pageNo){
		      var bkNo= $("cooperateBank").value;
		       var beginDate= $("bdate").value;
		       var endDate = $("edate").value;
		       if(beginDate==""||endDate==""){
		           alert("请选择要查询的起始日期！");
		           return;
		       }
		     PrepPayService.queryBankBalance( pageNo, bkNo, beginDate,endDate,callbackFun);
			 function callbackFun(pageObj){ 
	    	   $("balanceLogTable").style.display="";
	    	   dwr.util.removeAllRows("balanceLogList");
		             var cellFuncs = [
		                         function(obj) { return obj.id; },
		                         function(obj) { return obj.name; },
		                         function(obj) { return obj.liqDate; },
		                         function(obj) { return div100(obj.bfBl); },
		                         function(obj) { return div100(obj.balance); }
		                     ]
		              paginationTable(pageObj,"balanceLogList",cellFuncs,"","queryMerToday");
	          }
		}
		
		</script>

    </head>
    <body onload="initOption();">
    <div class="style">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="8">&nbsp;&nbsp;&nbsp;&nbsp;备付金银行余额查询</td></tr>
            <tr>
                <td class="th1" align="right" width="20%">银行：</td>
                <td align="left" >
                  <select style="width: 150px" id="cooperateBank" name="cooperateBank">
                     <option value="">全部...</option>
                   </select>
                 </td>
                 <td class="th1" align="right" width="20%">日期：</td>
                <td align="left" >
                    <input id="bdate" size="15px"
                    name="bdate" class="Wdate" type="text" onfocus="ryt_area_date('bdate','edate',0,30,0)" />
                    &nbsp;至&nbsp;
                    <input id="edate" size="15px" name="edate" class="Wdate" type="text" disabled="disabled" /><font color="red">*</font>
                </td>
             </tr>
            <tr>
            <td colspan="8" align="center" style="height: 30px">
                <input class="button" type="button" value = " 查 询 " onclick="queryBankBalanceLog(1);" />
            </td>
            </tr>
        </table>

       <table  class="tablelist tablelist2" id="balanceLogTable" style="display:none;" >
           <thead>
           <tr>
             <th>流水号</th><th>银行</th><th>系统日期</th>
             <th>商户余额</th><th>银行余额</th>
           </tr>
           </thead>
           <tbody id="balanceLogList"></tbody>
       </table>
        </div>
    </body>
</html>
