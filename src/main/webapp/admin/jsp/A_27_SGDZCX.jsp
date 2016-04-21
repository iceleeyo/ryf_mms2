<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>手工调账查询</title>
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
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script> 
        <script type="text/javascript" src="../../public/js/settlement/adjust_account_query.js?<%=rand%>" defer="defer"></script>
    </head>
    <body onload="init();">
    
     <div class="style">
    <table class="tableBorder" width="100%"  align="left" >
     <tbody>
        <tr><td class="title" colspan="6"> &nbsp; 手工调账查询</td></tr>
        <tr><td class="th1" align="right" width="10%">商户号：</td>
            <td align="left" width="35%">  
            		<input type="text" id="mid" name="mid"  size="8px" style="width:150px" onkeyup="checkMidInput(this);"/>&nbsp;
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
             <td class="th1" bgcolor="#D9DFEE" align="right"  width="10%">商户状态：</td>
                <td align="left"  width="25%">
                <select style="width:120px" id="mstate" name="mstate">
                    <option value="">全部...</option>
                </select></td>
                      
       </tr>
        <tr><td class="th1" align="right" width="10%">调账请求日期：</td>
            <td align="left" width="35%">
              <input name="btdate" size="15" id="btdate" class="Wdate" type="text"  onfocus="WdatePicker({skin:'ext',maxDate:'#F{$dp.$D(\'etdate\')||\'%y-%M-%d\'}',dateFmt:'yyyyMMdd',readOnly:'true'});"/>
                        至<input name="etdate" size="15" id="etdate" class="Wdate" type="text"  onfocus="WdatePicker({skin:'ext',minDate:'#F{$dp.$D(\'btdate\')}',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});"/></td>
            <td class="th1" align="right" width="10%"> 调账状态：</td>
            <td align="left" width="20%"><select id = "state" name = "state">
                        <option value="-1">全部</option>
                        <option value="0">调账提交</option>
                        <option value="1">审核成功</option>
                        <option value="2">审核失败</option>
                   </select></td>
               <td class="th1" bgcolor="#D9DFEE" align="right"  width="10%"></td>
              <td></td>
        </tr>
        <tr><td  colspan="6" align="center" >
        <input type="button" class="button" value="查询" onclick="queryBaseAdjustAccount(1)"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <input type="button" class="button" value="下载" onclick="downBaseAdjustAccount(-1)"/>
        </td></tr>
    </tbody>
   </table><br/>
   <div id="adjustaccountList" style="display: none"/>
   <table class="tablelist tablelist2" >
    <thead><tr><th>序号</th><th>商户号</th><th>商户简称</th><th>调账请求操作员ID</th><th>调账请求时间</th>
    <th>调账金额(元)</th><th>调账类型</th><th>调账状态</th><th>调账审核操作员ID</th><th>调账审核时间</th><th>调账原因</th></tr></thead>
    <tbody id="adjustaccountTable"></tbody>
    </table>

    </div>
 
</body>
</html>
