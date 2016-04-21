<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" errorPage="../../error.jsp"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
    <head>
        <title>手工调账请求</title>
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
        <script type='text/javascript' src='../../dwr/engine.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../dwr/util.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js'></script>
       <script type='text/javascript' src='../../dwr/interface/RypCommon.js?<%=rand%>'></script>
       <script type='text/javascript' src='../../dwr/interface/SettlementService.js?<%=rand%>'></script>       
	   <script type="text/javascript" src="../../public/js/settlement/admin_adjust_account_submit.js?<%=rand%>"></script>
 	   <script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
<script type="text/javascript">
       var loginMid=${sessionScope.SESSION_LOGGED_ON_USER.mid};
        var loginUid=${sessionScope.SESSION_LOGGED_ON_USER.operId};
</script>
    </head>

    <body onload="initMinfos();">
    
      <div class="style">
    <table class="tableBorder" width="100%">
            <tr><td class="title" colspan="2">&nbsp;手工调账请求</td></tr>
            <tr>
                <td class="th1" align="right" width="20%">商户号：&nbsp;</td>
                <td width="*%">
                      <input type="text" id="mid" name="mid"  style="width:150px" size="8px" onkeyup="checkMidInput(this);hideTable();" />&nbsp;
                       <!-- <select style="width: 150px" id="smid" name="smid" onchange="initMidInput(this.value);hideTable();" >
                          <option value="">全部...</option>
                      </select> &nbsp; &nbsp;  -->
                      <input type="hidden" name="action" value="yes">
                      <input type="button" class="button" value="确 认 " onclick="getAccount();">
                </td>
            </tr>
        </table>
  <table class="tableBorder" style="display:none;" id="adjustAccount">
    <tbody>
            <tr><td class="title" colspan="2">&nbsp;手工调账</td></tr>
        
            <tr>
                <td class="th1" align="right" width="20%">&nbsp;商户：</td>
                <td  align="left" id="minfo"></td>
            </tr>
            <tr>
                <td class="th1" align="right" width="20%">&nbsp;现有余额(元)：</td>
                <td  align="left" id="bAcount">&nbsp;</td>
            </tr>
             <tr>
                <td class="th1" align="right" width="20%">&nbsp;操作：</td>
                <td  align="left" >
                   <select id ="edit_type_id">
                    <option value="1"> 增 加 </option>
                    <option value="2"> 减 少 </option>
                   </select>
                </td>
            </tr>
            <tr>
                <td class="th1" align="right" width="20%">&nbsp;调账金额(元)：</td>
                <td  align="left" ><input type="text" id="edit_acc_id" maxlength="10">&nbsp;<font color="red">*</font>(必须包含两位小数)</td>
            </tr>
            
             <tr>
                <td class="th1" align="right" width="20%">&nbsp;调账原因：</td>
                <td  align="left" >
                <textarea rows="5" cols="25"   id="reason"></textarea>&nbsp;<font color="red">*</font></td>
            </tr>
            
            <tr>
                <td class="th1" align="center" colspan="2">
                <input type="submit" value=" 提交请求 " class="button" onclick="submitAdjust()"></td>
            </tr>
  		  </tbody>
        </table>
        </div>
    </body>
</html>
