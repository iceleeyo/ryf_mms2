<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" errorPage="../../error.jsp"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
    <head>
        <title>手工调账</title>
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
    </head>

    <body onload="initMinfos();">
     <table class="tableBorder" width="100%" id="verify_table"  >
            <tr><td class="title" colspan="2">&nbsp;请输入手机验证码</td></tr>
            <tr>
                <td class="th1" align="right" width="20%">手机验证码：&nbsp;</td>
                <td width="*%">
                      <input type="text" id="verifyCode" maxlength="6" name="verifyCode"  size="8px" />&nbsp;
                      <input type="button"  name="getVefCode" id="getVefCode" value="点此发送验证码" onclick="getvfCode(0);">
                     <input type="button" class="button" value="确 认 " onclick="verify_Code();" >
                </td>
            </tr>
        </table>
      <div class="style">
     <!-- 
      <table class="tableBorder"  width="100%" id="selectmid_table">
   
     -->
     <table class="tableBorder" width="100%" id="selectmid_table" style="display:none;">
            <tr><td class="title" colspan="2">&nbsp;选择商户</td></tr>
            <tr>
                <td class="th1" align="right" width="20%">商户号：&nbsp;</td>
                <td width="*%">
                      <input type="text" id="mid" name="mid"  size="8px" onkeyup="checkMidInput(this);hideTable();" />&nbsp;
                      <select style="width: 150px" id="smid" name="smid" onchange="initMidInput(this.value);hideTable();" >
                          <option value="">全部...</option>
                      </select> &nbsp; &nbsp;
                      <input type="hidden" name="action" value="yes">
                      <input type="button" class="button" value="确 认 " onclick="getAccounts();">
                </td>
            </tr>
        </table>
        <table  class="tableBorder"  id="selec_tab" style="display: none">
        	<tr><td class="title" colspan="2"><input type="radio" checked="checked" onclick="tabChange(0);" name="rdbtn"/>记流水改余额&nbsp;&nbsp;<input type="radio" name="rdbtn" onclick="tabChange(1);"/>修改商户余额</td></tr>
        </table>
  <table class="tableBorder" style="display:none;" id="adjustAccount">
    <tbody>
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
                    <option value="0"> 增 加 </option>
                    <option value="1"> 减 少 </option>
                   </select>
                </td>
            </tr>
            
            <tr>
                <td class="th1" align="right" width="20%">&nbsp;调账金额(元)：</td>
                <td  align="left" ><input type="text" id="edit_amt" maxlength="10">&nbsp;<font color="red">*</font>(必须包含两位小数)</td>
            </tr>

            <tr>
                <td class="th1" align="right" width="20%">&nbsp;调账手续费：</td>
                <td  align="left" ><input type="text" id="edit_fee" maxlength="10"/>&nbsp;<font color="red">*</font>(必须包含两位小数)</td>
            </tr>
            
             <tr>
                <td class="th1" align="right" width="20%">&nbsp;表名：</td>
                <td  align="left" ><input type="text" id="edit_tbname" maxlength="10"/>&nbsp;<font color="red">*</font></td>
            </tr>
            
             <tr>
                <td class="th1" align="right" width="20%">&nbsp;流水号：</td>
                <td  align="left" ><input type="text" id="edit_tseq" maxlength="10"/>&nbsp;<font color="red">*</font></td>
            </tr>
            
             <tr>
                <td class="th1" align="right" width="20%">&nbsp;简短说明：</td>
                <td  align="left" ><input type="text" id="edit_remrk" maxlength="30"/></td>
            </tr>
            
            <tr>
                <td class="th1" align="center" colspan="2">
             手机验证码：  <input type="text" id="verifyCode_submit" maxlength="6" name="verifyCode_submit"  size="8px" />&nbsp;
                      <input type="button"  name="getVefCode_" id="getVefCode_" value="点此发送验证码" onclick="getvfCode(1);">
                      <input type="button" id="subbt_SGDZ" disabled="disabled" value=" 提交请求 " class="button" onclick="submitSGDZ()"></td>
            </tr>
  		  </tbody>
        </table>
        
        
        
 <table class="tableBorder" style="display:none;" id="adjustAccounts">
    <tbody>
            <tr>
                <td class="th1" align="right" width="20%">&nbsp;商户：</td>
                <td  align="left" id="minfo_"></td>
            </tr>
            <tr>
                <td class="th1" align="right" width="20%">&nbsp;现有余额(元)：</td>
                <td  align="left" id="bAcount_">&nbsp;</td>
            </tr>
            
             <tr>
                <td class="th1" align="right" width="20%">&nbsp;操作：</td>
                <td  align="left" >
                   <select id ="edit_type_ids">
	                    <option value="0"> 增 加 </option>
	                    <option value="1"> 减 少 </option>
                   </select>
                </td>
            </tr>
            
            <tr>
                <td class="th1" align="right" width="20%">&nbsp;调账金额(元)：</td>
                <td  align="left" ><input type="text" id="edit_amts" maxlength="10">&nbsp;<font color="red">*</font>(必须包含两位小数)</td>
            </tr>

            <tr>
                <td class="th1" align="center" colspan="2">
             手机验证码：<input type="text" id="verifyCode_submits" maxlength="6" name="verifyCode_submit"  size="8px" />&nbsp;
                      <input type="button"  name="getVefCodes" id="getVefCodes" value="点此发送验证码" onclick="getvfCode(2);">
                      <input type="button" id="subbt_SGDZS" disabled="disabled" value=" 提交请求 " class="button" onclick="submitSHYE()"></td>
            </tr>
  		  </tbody>
        </table>
        
        </div>
    </body>
</html>
