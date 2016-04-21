<%@ page language="java" import="java.util.*" pageEncoding="utf-8" errorPage="../../error.jsp"%>
<%@ page language="java" import="java.text.*,java.util.*"%>
<%@ page language="java" import="java.sql.*"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>黑名单管理</title>
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
		<script type='text/javascript' src='../../dwr/interface/CommonService.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../dwr/interface/RiskmanageService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../public/js/ryt.js?<%=rand%>'></script>
        <script type="text/javascript" src="../../public/datePicker/WdatePicker.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../public/js/GreyFrame.js?<%=rand%>"></script>
      
 		<script type="text/javascript" src='../../public/js/riskmanage/blacklist_manage.js?<%=rand%>'></script>

</head>
    <body>  <div class="style">
     <table class="tableBorder"  align="center"  cellpadding="0" cellspacing="0">
        <tbody>
            <tr>
                <td class="title" colspan="16">
                   <input type="hidden" name="auth_type" id="auth_type" value="0" />
                    &nbsp;&nbsp;&nbsp;&nbsp;黑名单管理(添加名单时带<font color="red">*</font>为必填项)
                </td>
            </tr>
          
            <tr>
                <td class="th1" align="center" width="100%" height="30px" colspan="4">
                   <select id="fieldType" name="fieldType" style="height: 22px;margin-bottom: 1px;">
                   	<option value="0">全部...</option>
                   	<option value="1">卡号</option>
				    <option value="2">证件号</option>
				    <option value="3">手机号</option>
				    </select>&nbsp;&nbsp;
                <input type="text" name="field" id="field" size="30" maxlength="25" class="input" style="height: 22px;margin-bottom: 1px;">
                <span style="height: 22px;margin-bottom: 1px;"><font color="red" style="padding: 10px;">*</font>录入时间：</span>
                <input type="text" name="date" id="date" class="Wdate"  onfocus="WdatePicker({skin:'ext',minDate:'1998-12-20',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});"  style="height: 22px;margin-bottom: 1px;"/>
                <input type="button" value="确认查询" onclick=" checkData(); " class="button">
                </td>            
            </tr>
            <tr>
                <td align="left" style="padding-left: 183px;" width="100%" height="30px" colspan="4">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <input type="button" id="add" value="添加到黑名单" onClick="addList()" class="button">&nbsp;&nbsp;
                <input type="button" id="add" value="批量导入黑名单" onClick="showFrame()" class="button">&nbsp;&nbsp;
                <input type="button" value="导入模板下载" onClick="downloadTemplate();" class="button">&nbsp;&nbsp;
				<input type="button" id="add" value="刷新" onClick="refreshCardAuth()" class="button"><span style="height: 22px;margin-bottom: 3px;"><font color="red">&nbsp;&nbsp;*&nbsp;&nbsp;</font>对黑名单配置后 必须点击刷新按钮刷新才能生效</span>
                </td>
            </tr>
        </tbody>
    </table>
    <div id="cardList" style="display: none">
    <table class="tablelist tablelist2" width="80%" align="center">
	    <thead>
	    	<tr><th>序号</th><th>卡号/证件号/手机号</th><th>录入时间</th><th>操作</th></tr>
	   	</thead>
    <tbody id="biaotou"></tbody>
    </table>
    </div></div>
</body>
</html>
