<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
           <title>余额查询</title>
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
        <script type='text/javascript' src='../../dwr/engine.js'></script>
        <script type='text/javascript' src='../../dwr/util.js'></script>
	    <script type='text/javascript' src='../../dwr/interface/SettlementService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type="text/javascript" src="../../public/js/settlement/admin_search_balance.js?<%=rand%>"></script>
    </head>
    <body onload="initMinfos();">
    
     <div class="style">
    <table class="tableBorder" >
                <tbody>
                    <tr><td class="title" colspan="2">&nbsp;&nbsp; 余额查询</td></tr>
                    <tr>
                        <td class="th1" align="right">&nbsp;商户号：</td>
                        <td align="left" width="60%"> 	
			                  <input type="text" id="mid" name="mid"  size="8px" style="width:150px" onkeyup="checkMidInput(this);"/> &nbsp;
			                   <!-- <select style="width: 150px" id="smid" name="smid" onchange="initMidInput(this.value)">
			                         <option value="">请选择...</option>
			                  </select> --> 
                        </td>
                    </tr>
                    <tr>
	                    <td colspan="2" align="center">
	                   		 <input type="button" value="查询" class="button" onclick="queryBaseBalance()"/>
	            		</td>
                    </tr>
                </tbody>
    </table>
    <div id="balanceList" style="display:none">
    <table class="tablelist tablelist2" >
        <thead><tr><th>用户ID</th><th>账户号</th><th>账户名称</th><th>注册日期</th><th>状态</th><th>余额(元)</th><th>可用余额(元)</th></tr></thead>
        <tbody id="resultList">
        </tbody>
    </table>
    </div>
    
    </div>
</body>
</html>
