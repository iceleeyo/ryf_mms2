<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
<%
response.setHeader("Pragma","No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", 0);
int rand = new java.util.Random().nextInt(10000);
%>
        <title>商户网关交易统计查询</title>
        <meta http-equiv="pragma" content="no-cache"/>
        <meta http-equiv="cache-control" content="no-cache"/>
        <meta http-equiv="Expires" content="0"/>  
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css"/>
        <link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css"/>
        <script type='text/javascript'  src='../../dwr/engine.js'></script>
        <script type='text/javascript'  src='../../dwr/util.js'></script>
        <script type="text/javascript" src="../../dwr/interface/PageService.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../dwr/interface/SettlementService.js?<%=rand%>"></script>
        <script type='text/javascript' src='../../public/datePicker/WdatePicker.js'></script>
 		<script type="text/javascript" src='../../public/js/settlement/admin_gidStatistics.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>

</head>
<body onload="initOption();">

 <div class="style">
 <table width="100%"  align="left"  class="tableBorder">
    <tr><td class="title" colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;商户网关交易统计查询</td></tr>
      <tr>
        <td width="100%" bgcolor="#C0C9E0"  height="25px" colspan="2">
        &nbsp;&nbsp;&nbsp;&nbsp;
        <input type="radio" name="choose" id="systemId" value="systemId"  checked="checked"  onclick=" changeSearch('bygid');"/>按渠道
         &nbsp;&nbsp;
        <input type="radio" name="choose" id="codeId" value="codeId" onclick=" changeSearch('meroid');" />按商户号
          &nbsp;&nbsp;
        
        </td>
    </tr>
    
  </table>
  
  
  <div id="meroid" style="display:none;" >
      <table class="tableBorder" id="topMenuTable">
         <tr>
            <td class="th1" height="30px">商户号： </td>
            <td align="left">
               <input type="text" id="mid" name="mid"  size="15px" onkeyup="checkMidInput(this);"/>
               </td>
          </tr>
          <tr>
          	<td class="th1" align="left" >系统日期：</td>
           			<td align="left" >
               				<input id="beginDate" name="beginDate"  value=""  class="Wdate" type="text" 	onfocus="ryt_area_date('beginDate','endDate',0,92,0)"/>&nbsp;至
               				<input id="endDate" name="endDate"  value="" class="Wdate" type="text" 	disabled="disabled"/>
               				<font color="red">*</font>
           			</td>
          </tr>
          <tr>
				<td colspan="2" align="center">
					<input class="button" type="button" value=" 查  询  " onclick="queryOne(1,'A')" />
					<input class="button" type="button" value=" 下载Excel " onclick="exportTransExcel('A')" />
				</td>
			</tr>
      </table>
   </div> 
   <div id="bygid" >
        <table  class="tableBorder" id="topMenuTable">
          <tr>
            <td class="th1" bgcolor="#D9DFEE" align="left" >支付渠道：</td>
                <td align="left"><input type="text" id="gateName" name="gateName" onblur="gateRouteIdList()"/>
                <select style="width: 150px" id="gateRouteId" name="gateRouteId" >
                    <option value="">全部...</option>
                </select>
                </td>
          </tr>
          <tr>
          	<td class="th1">系统日期：</td>
           			<td align="left" width="">
               				<input id="bDate" name="bDate"  value=""  class="Wdate" type="text" 	onfocus="ryt_area_date('bDate','eDate',0,92,0)"/>&nbsp;至
               				<input id="eDate" name="eDate"  value="" class="Wdate" type="text" 	disabled="disabled"/>
               				<font color="red">*</font>
           			</td>
          </tr>
          <tr>
				<td colspan="2" align="center">
					<input class="button" type="button" value=" 查  询  " onclick="queryOne(1,'B')" />
					<input class="button" type="button" value=" 下载Excel " onclick="exportTransExcel('B')" />
				</td>
			</tr>
        </table>
   </div>
     <table id="showTable"  class="tablelist tablelist2" style="display: none; margin-top: 10px"><!-- display: none; -->
          <thead>
           <tr>
             <th>商户号</th><th>交易总金额</th>
             <th>交易总笔数</th>
           </tr>
           </thead>
           <tbody id="resultList"></tbody>
        </table>
        
      <table id="showTableGid"  class="tablelist tablelist2" style="display: none; margin-top: 10px"><!-- display: none; -->
          <thead>
           <tr>
             <th>渠道</th><th>交易总金额</th>
             <th>交易总笔数</th>
           </tr>
           </thead>
           <tbody id="resultListgid"></tbody>
        </table>
          
  </div>
  
</body>
</html>
