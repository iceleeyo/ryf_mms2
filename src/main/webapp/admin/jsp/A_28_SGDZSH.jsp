<%@ page language="java" pageEncoding="UTF-8" %>
<%@page import="com.rongyifu.mms.web.WebConstants"%>
<%@page import="com.rongyifu.mms.bean.LoginUser"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>手工调账审核</title>
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
        <script type='text/javascript' src='../../dwr/engine.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../dwr/util.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../dwr/interface/RypCommon.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../dwr/interface/SettlementService.js?<%=rand%>'></script>
        <script type="text/javascript" src="../../dwr/interface/PageService.js?<%=rand%>"></script>
		<script type="text/javascript" src="../../public/datePicker/WdatePicker.js?<%=rand%>"></script>
		<script type="text/javascript" src="../../public/js/ryt.js?<%=rand%>" ></script> 
        <script type="text/javascript" src="../../public/js/settlement/admin_adjust_account_audit.js?<%=rand%>" ></script>
        
        <script type='text/javascript' src='../../dwr/interface/DoSettlementService.js?<%=rand %>'></script>
      
        
         <script type='text/javascript'>
        function checkBkNo(){
            DoSettlementService.checkBkNo(function(msg){alert(msg);});
        }
        </script>
        
    </head>
    <body onload="init();">    
     <div class="style">
    <table class="tableBorder" >
     <tbody>
        <tr><td class="title" colspan="6">&nbsp; 手工调账审核</td></tr>
        <tr><td class="th1" align="right" width="10%">商户号：</td>
            <td align="left" width="30%"> <input type="text" id="mid" name="mid" style="width:150px" size="8px" onkeyup="checkMidInput(this);"/>
                   <!-- <select style="width: 150px" id="smid" name="smid" onchange="initMidInput(this.value)">
                     <option value="">全部...</option>
                   </select> --> 
             </td>
            <td class="th1" align="right" width="10%">调账类型：</td>
            <td align="left" width="20%">
                   <select id = "type" name = "type">
                        <option value="0">全部</option>
                        <option value="1">手工增加</option>
                        <option value="2">手工减少</option>
                   </select></td> 
               <td class="th1" bgcolor="#D9DFEE" align="right" width="10%">商户状态：</td>
                <td align="left" width="20%">
                <select style="width: 150px" id="mstate" name="mstate">
                    <option value="">全部...</option>
                </select>
                </td>                
       </tr>
        <tr><td class="th1" align="right" width="10%">调账请求日期：</td>
            <td align="left" width="30%">
              <input name="btdate" size="15" id="btdate" class="Wdate" type="text"  onfocus="WdatePicker({skin:'ext',maxDate:'#F{$dp.$D(\'etdate\')||\'%y-%M-%d\'}',dateFmt:'yyyyMMdd',readOnly:'true'});"/>
                        至<input name="etdate" size="15" id="etdate" class="Wdate" type="text"  onfocus="WdatePicker({skin:'ext',minDate:'#F{$dp.$D(\'btdate\')}',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});"/></td>
            <td class="th1" align="right" width="10%"> 调账状态：</td>
            <td align="left" width="20%"><select id = "state" name = "state">
                        <option value="-1">全部</option>
                        <option value="0">调账提交</option>
                        <option value="1">审核成功</option>
                        <option value="2">审核失败</option>
                   </select></td>
                   
                 <td class="th1" bgcolor="#D9DFEE" align="right" width="11%"></td>
                <td align="left" width="20%">
                </td>      
        </tr>
        <tr><td  colspan="6" align="center" height="30px">
        <input type="button" class='button' value="查询" onclick="queryBaseAuditAccount(1)"/>
       <!-- 
         <input type="button"  style="height: 24px;width: 100px;margin-left: 30px" value="检查" onclick="checkBkNo()"/>
    <font color="red">&nbsp;&nbsp;&nbsp;*</font>
    备付金管理上线后，会产生商户流水记录，点击检查按钮，系统检查相关配置是否完成
             -->
        </td></tr>
    </tbody>
   </table><br/>
<% LoginUser loginuser = (LoginUser)session.getAttribute(WebConstants.SESSION_LOGGED_ON_USER);%>
<input type="hidden" name="loginusermid" id="loginusermid" value="<%=loginuser.getMid() %>"/>
<input type="hidden" name="loginuseroperid" id="loginuseroperid" value="<%=loginuser.getOperId() %>"/>
 <div id="auditaccountList" style="display: none">
 <form name="form2" method="post" action="">
   <table class="tablelist tablelist2" >
    <thead><tr><th sort="false"><input type="checkbox" onclick="allSelected()" id="allSelect" />全选</th>
    <th>商户号</th><th>商户简称</th><th>调账请求操作员ID</th><th>调账请求时间</th>
    <th>调账金额(元)</th><th>调账类型</th><th>调账状态</th><th>调账审核操作员ID</th><th>调账审核时间</th><th>调账原因</th></tr></thead>
    <tbody id="adjustaccountTable"></tbody>
    </table>
    <table class="tableBorder" width="80%" align="center" bgcolor="#ffffff" border="1" cellpadding="0" cellspacing="1">
        <tbody id="pageTable"></tbody>
    </table>
  </form>
 </div>
 
 </div>
</body>
</html>
