<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>商户IP配置</title>
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
        <script type='text/javascript' src='../../dwr/engine.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../dwr/util.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/SysManageService.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>' ></script>
		<script type="text/javascript" src='../../public/js/sysmanage/MerIPconfig.js?<%=rand%>'></script> 

    </head>
    <body onload="init()">
    
     <div class="style">
        <form name="MERHLOG"  method="post" action="">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="4">&nbsp;&nbsp;&nbsp;&nbsp;商户IP配置&nbsp;&nbsp;</td></tr>
            <tr>
            
            <td class="th1" align="right" width="11%">商户号：</td>
                <td align="left" width="30%">
                <input type="text" id="mid" name="mid" style="width: 150px"/>
                 </td>
            <td class="th1" align="right" width="11%">类型：</td>
                <td align="left" width="30%">
                <select id="type">
                  <option value="">全部....</option>
                  <option value="1">代付代扣白名单</option>
                  <option value="2">新代付代扣白名单</option>
                </select>
                 </td>
                </tr>
            <tr>
            <td colspan="6" align="center" style="height: 30px">
                <input class="button" type="button" value = " 查 询 " onclick="queryMerIP(1);" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                 <input class="button" type="button" value = "新增" onclick="addMerIP();" />&nbsp;&nbsp;&nbsp;&nbsp; 
                 <input class="button" type="button" value = "刷新" onclick="reflushMerIP();" /><font color="red">(修改之后请刷新)</font>
            </td>
            </tr>
        </table>
       </form>
    
       <table  class="tablelist tablelist2" id="merIPtable" style="display: none;">
           <thead>
           <tr>
            <th>商户号</th>
            <th>IP地址</th>
            <th>类型</th>
            <th>操作</th>
           </tr>
           </thead>
           <tbody id="resultList"></tbody>
       </table>
       
        <table id="alterMeripConfigTable" class="tableBorder"
			style="display:none; width: 400px; height: 150px; margin-top: 0px;">
			 <tr>
          	  <td class="th1" bgcolor="#D9DFEE" align="right"  width="35%">商户号:</td>
                <td align="left"   ><input type="text" id="m_mid" maxlength="20" size="26"/>&nbsp;&nbsp;<font color="red">*</font>
               </td>
               </tr>
               
                <tr>
          	  <td class="th1" bgcolor="#D9DFEE" align="right"  width="35%">ip地址:</td>
                <td align="left"   ><input type="text" id="ip"  size="26" />&nbsp;&nbsp;<font color="red">*</font>
               </td>
               </tr>
               <tr>
          	  <td class="th1" bgcolor="#D9DFEE" align="right"  width="35%">类型:</td>
                <td align="left"   >
                <select id="t_type">
                    <option value="">全部...</option>
                    <option value="1">代付代扣白名单</option>
                    <option value="2">新代付代扣白名单</option>
                </select>&nbsp;&nbsp;<font color="red">*</font>
               </td>
               </tr>
              <tr>
				<td colspan="4" align="center" height="20px;">
					<input value="新增" type="button" class="button" onclick="add();" />
					<input value="返回" type="button" class="wBox_close button" />
				</td>
			</tr>
		</table>
		
		
		 <table id="alterupdateMeripTable" class="tableBorder"
			style="display:none; width: 400px; height: 150px; margin-top: 0px;">
			 <tr>
          	  <td class="th1" bgcolor="#D9DFEE" align="right"  width="35%">商户号:</td>
                <td align="left"   ><input type="text" id="m_mid1"  size="26"/>&nbsp;&nbsp;<font color="red">*</font>
               </td>
               </tr>
               
                <tr>
          	  <td class="th1" bgcolor="#D9DFEE" align="right"  width="35%">ip地址:</td>
                <td align="left"   ><input type="text" id="ip1"  size="26" />&nbsp;&nbsp;<font color="red">*</font>
               </td>
               </tr>
               <tr>
          	  <td class="th1" bgcolor="#D9DFEE" align="right"  width="35%">类型:</td>
                <td align="left"   >
                <select id="t_type1">
                    <option value="">全部...</option>
                    <option value="1">代付代扣白名单</option>
                    <option value="2">新代付代扣白名单</option>
                </select>&nbsp;&nbsp;<font color="red">*</font>
                 <input type="hidden" id="id" size="26"/>
               </td>
               </tr>
              <tr>
				<td colspan="4" align="center" height="20px;">
					<input value="修改" type="button" class="button" onclick="edit(1);" />
					<input value="返回" type="button" class="wBox_close button" />
				</td>
			</tr>
		</table>
        </div>
	</body>
</html>
