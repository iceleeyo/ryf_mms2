<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>支付渠道维护</title>
		<%
		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);
		int rand = new java.util.Random().nextInt(10000);
		%>
        <meta http-equiv="pragma" content="no-cache"></meta>
        <meta http-equiv="cache-control" content="no-cache"></meta>
        <meta http-equiv="Expires" content="0">  </meta>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
        <link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css"/>
        <link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css"></link>
	    <script type='text/javascript' src='../../dwr/engine.js'></script>
	    <script type='text/javascript' src='../../dwr/util.js'></script>
	    <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/SysManageService.js?<%=rand%>'></script>
        <script type="text/javascript" src="../../public/js/GreyFrame.js?<%=rand%>"></script>
		<script type="text/javascript" src="../../public/js/sysmanage/gateRouteManager.js?<%=rand%>"></script> 
        <script type="text/javascript" src="../../public/js/ryt.js?<%=rand%>"></script>
       	<script type="text/javascript">
       	var authStr = '${sessionScope.SESSION_LOGGED_ON_USER.auth}';
       	</script>
</head>
<body onload="initParams();">

 <div class="style"><br />
<table style="border: 0em; width: 100%">
<tr>
<td align="left">
<input id="addBtn" name='' type='button' value='基本信息新增' onclick="showEditNameWin(4);" style="width: 100px;height: 25px"/>&nbsp;&nbsp;&nbsp;
<input name='' type='button' value='基本信息修改' onclick="showEditNameWin(1);" style="width: 100px;height: 25px"/>&nbsp;&nbsp;&nbsp;
<!-- <input id="editReqUrlBut"  type='button' value='修改请求地址' onclick="showEditNameWin(4);" style="width: 100px;height: 25px"/> -->
<!-- 请求地址请不要随便修改&nbsp;&nbsp;&nbsp; -->
<input name='' type='button' value='修改其他信息' onclick="showEditNameWin(2);" style="width: 100px;height: 25px"/>&nbsp;&nbsp;&nbsp;
<input type="button" value=" 刷 新 " style="width: 100px;height: 25px" onclick="reflushGate();"/></td>
<!-- 
<input name='' type='button' value='修改备付金银行行号' onclick="showEditNameWin(3);"/>
 --> 
<td align="right"><input type="text" id="sel_key"  name="sel_key" style="width: 150px;height: 18px"/>
<input type="button" value=" 查 询 " style="width: 80px;height: 25px" onclick="selectByKey();"/></td> 
</tr>


  </table>  
    <table id="gateRouteTable" class="tablelist tablelist2">
     	<tr >
					<td bgcolor="#c0c9e0" colspan="7"><span style="float:left;">&nbsp; &nbsp;支付渠道表</span></td>
		</tr> 
    	<tr>
    		<th>选择</th><th >渠道ID</th><th>渠道名称</th><th>商户号</th><th>对账功能</th>
    		<th nowrap="nowrap">请求地址</th>
    		
    		
    		<th nowrap="nowrap">说明</th>
    		<!-- 
    		<th>收款方账号</th><th>收款方账户名 </th><th>收款方开户行联行号 </th><th>收款方开户行全名</th>
    		<th>备付金银行行号</th>
    		 -->
   		</tr>
    <tbody id="gateRouteList" ></tbody>
    </table>
    &nbsp;
    
    <table id="basicInfoTable"  style="display: none;width: 300px;height: 100px;">
     	<tr>
     		<td align="right">支付渠道ID：</td>
     		<td><input id="gateRouteId" maxlength="11"/><font color="red">&nbsp;&nbsp;*</font></td>
     	</tr>
     	 <tr>
     		<td align="right">支付渠道名：</td>
     		<td><input id="gateRouteName" value="" maxlength="20"/><font color="red">&nbsp;&nbsp;*</font></td>
     	</tr>
     	 <tr>
     		<td align="right">商户号：</td>
     		<td><input id="gateRouteMerNo" value="" maxlength="60"/><font color="red">&nbsp;&nbsp;*</font></td>
     	</tr>
     	 <tr>
     		<td align="right">请求地址：</td>
     		<td><input id="gateRouteReqUrl" value="" maxlength="120"/><font color="red">&nbsp;&nbsp;*</font></td>
     	</tr>
     	 <tr>
     		<td align="right">说明：</td>
     		<td><input id="gateRouteRemark" value="" maxlength="60"/><font color="red">&nbsp;&nbsp;*</font></td>
     	</tr>
     	 <tr>
     		<td><input id="subBtn" value="确认" type="button" class="button"/></td>
     		<td><input  value="返回" type="button" class="wBox_close button" /></td>
     	</tr>
    </table>
    
    <div style="display: none;" id="payGateMsgDiv">
    <form id="payGateMsgForm">
    <table id="payGateMsgTable"   class="tableBorder" style="width: 600px;height:220px;margin-top: 0px;">
    	<tr>
	    	<td align="right" class="th1">name</td>
	    	<td>
	    	<input id="name" value="" disabled="disabled"/>
	    	<input id="gid" type="hidden"  /> </td>
    	</tr>
    	<tr>
	    	<td align="right" class="th1">mer_no</td>
	    	<td><input id="merNo" value="" maxlength="60" /></td>
    	</tr>
    	
    	<tr>
	    	<td align="right" class="th1">remark</td>
	    	<td><input id="remark" value=""  size="60"/></td>
    	</tr>
    	<tr>
	    	<td align="right" class="th1">mer_key</td>
	    	<td><input id="merKey" value="" size="60"/></td>
    	</tr>
    	
    	<tr>
	    	<td align="right" class="th1">mer_key_pwd </td>
	    	<td><input id="merKeyPwd" value="" /></td>
    	</tr>
    	
    	<tr>
	    	<td align="right" class="th1">request_url </td>
	    	<td><input id="requestUrl" value="" size="60" disabled="disabled"/></td>
    	</tr>
    	
    	<tr>
	    	<td align="right" class="th1">p1 </td>
	    	<td><input id="p1" value="" size="60"/></td>
    	</tr>
    	
    	<tr>
	    	<td align="right" class="th1">p2 </td>
	    	<td><input id="p2" value="" size="60"/></td>
    	</tr>
    	
    	<tr>
	    	<td align="right" class="th1">p3 </td>
	    	<td><input id="p3" value="" size="60"/></td>
    	</tr>
    	
    	<tr>
	    	<td align="right" class="th1">p4 </td>
	    	<td><input id="p4" value="" size="60"/></td>
    	</tr>
    	<tr>
	    	<td align="right" class="th1">p5 </td>
	    	<td><input id="p5" value="" size="60"/></td>
    	</tr>
    	
    	<tr>
	    	<td align="right" class="th1">class_name</td>
	    	<td><input id="className" value="" size="60"/></td>
    	</tr>
    	
    	<tr>
    	   <td align="right" class="th1">rec_acc_no</td>
    	   <td><input id="recAccNo" value="" maxlength="40"  size="50"/></td>
    	</tr>
    	<tr>
    	   <td align="right" class="th1">rec_acc_name</td>
    	   <td><input id="recAccName" value="" maxlength="60"  size="50" /></td>
    	</tr>
    	<tr>
    	   <td align="right" class="th1">rec_bank_no</td>
    	   <td><input id="recBankNo" value="" maxlength="14"  /></td>
    	</tr>
    	<tr>
    	   <td align="right" class="th1">rec_bank_name</td>
    	   <td><input id="recBankName" value=""  size="50" /></td>
    	</tr>
    	
    	<tr>
    	   <td align="right" class="th1">bf_bk_no</td>
    	   <td><input id="bfBkNo" value="" maxlength="14"  /></td>
    	</tr>
    	
    	
        <tr>
     		<td colspan="2" align="right" height="20px;">
     		<input value="确认" type="button" class="button" onclick="editGateRouteMsg();"/>
     		<input  value="取消" type="button" class="wBox_close button" />
     		</td>
     	</tr>
    </table>
    
     </form></div>
     <p>&nbsp;</p>
     
     <table id="bfBkNoTable"  style="display: none;width: 300px;height: 100px;">
     	<tr>
     		<td align="right">支付渠道ID：</td>
     		<td><input id="gateRouteId1" disabled="disabled"/></td>
     	</tr>
     	<tr>
    	   <td align="right" >备付金银行：</td>
    	   <td align="left" width="15%">
		       <select style="width: 150px" id="bfBkNo" name="bfBkNo">
		       <option value=""> 请选择...</option>
		       </select>
	       </td>
    	</tr>
     	 <tr>
     		<td align="right"><input id="editInput" value="修  改" type="button" class="button" onclick="eidtBfBkNo();"/></td>
     		<td align="right"><input  value="返  回" type="button" class="wBox_close button" /></td>
     	</tr>
    </table>
    
    
    <table id="RequestUrlTable"  style="display: none;width: 300px;height: 100px;">
     	<tr>
     		<td align="right">支付渠道ID：</td>
     		<td><input id="gateRouteId4" disabled="disabled"/></td>
     	</tr>
     	<tr>
    	   <td align="right" >请求地址：</td>
    	   <td align="left" width="15%">
		      <input id="edit_request_url" value="" size=85 maxlength="120"/> 
	       </td>
    	</tr>
     	 <tr>
     		<td align="center" >
     		<input value="修  改" type="button" class="button" onclick="eidtRequestUrl();"/>
     		</td>
     		<td align="center">
     		
     		<input  value="返  回" type="button" class="wBox_close button" /></td>
     	</tr>
    </table>
    
    
    
    <!-- 
         <table width="100%"  class="tableBorder" id="addRytGateTable" >
         	<tr>
         	     <td class="title" colspan="2">&nbsp;&nbsp;&nbsp;增值业务银行网关增加</td>
         	</tr>
            <tr>
                <td class="th1" align="right" width="35%">&nbsp;网关号：</td>
                <td  align="left">
                    <input id="n_gate" type="text" /> <font color="red">*</font>
               </tr>
             <tr>
               <td class="th1" align="right">&nbsp;网关名称：</td>
               <td  align="left"><input id="gateName" type="text" /> <font color="red">*</font></td>   
             </tr>
             <tr>
                <td class="th1" align="right" width="35%">&nbsp;交易方式：</td>
                <td  align="left">
                     <select id="n_trans_mode" disabled="disabled">
                     	<option value="4"> &nbsp;WAP支付&nbsp; </option>
                     </select>
                </td>  
               </tr>
               <tr>
                <td colspan="4" align="center" height="25px">
                <input type="button" id="submitModelID" value="提 交"  class="button" onclick="addGateRoute()"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                </td>
            </tr>
    </table>
     -->
    </div>
</body>
</html>
