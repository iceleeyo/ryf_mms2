<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>商户结算单通知配置</title>
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
        <link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css" />
        <script type='text/javascript' src='../../dwr/engine.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../dwr/util.js?<%=rand%>'></script>
       <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
       <script type='text/javascript' src='../../dwr/interface/RypCommon.js?<%=rand%>'></script>
       <script type='text/javascript' src='../../dwr/interface/SettlementNoticeService.js?<%=rand%>'></script>
       <script type="text/javascript" src="../../public/js/settlement/admin_notice_settlement.js?<%=rand%>"></script>
	   <script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
	   <script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
	   <script type="text/javascript" src="../../public/js/GreyFrame.js?<%=rand%>"></script>
	   <script type='text/javascript' src='../../dwr/interface/DoSettlementService.js?<%=rand %>'></script>
       
	   
	   <script type='text/javascript'>
       
        </script>
        
    </head>
    <body >
    
    <div class="style">
    <table class="tableBorder" >
        <tr>
          <td class="title" colspan="6">&nbsp;商户结算单通知配置&nbsp;&nbsp;</td>
        </tr>
        <tr>
         <td class="th1" bgcolor="#D9DFEE" align="right" width="10%"> 商户号：</td>
         <td align="left" width="30%">
             	<input type="text" id="mid" name="mid"  size="8px" style="width: 150px" onkeyup="checkMidInput(this);"/>
                   
         </td>
          <td class="th1" bgcolor="#D9DFEE" align="right"  height="30px">商户名称：</td>
          <td align="left" ><input type="text" id="name" name="name"/></td>
          <td colspan="2"></td>
       </tr>
       <tr>
        <td class="th1" bgcolor="#D9DFEE" align="right" height="30px">新增日期：</td>
         <td align="left" colspan="3">
          <input id="beginDate" name="beginDate" class="Wdate" type="text" value=""
                        onfocus="WdatePicker({skin:'ext',minDate:'1998-12-20',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});" />
                            至&nbsp;<input id="endDate" name="endDate"  class="Wdate" type="text" value=""
                        onfocus="WdatePicker({skin:'ext',minDate:'#F{$dp.$D(\'beginDate\')}',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});"/>&nbsp; 
                       <!--  <input id="beginDate" name="beginDate"  value=""  class="Wdate" type="text" 	onfocus="ryt_area_date('beginDate','endDate',0,30,0)"/>&nbsp;至
               				<input id="endDate" name="endDate"  value="" class="Wdate" type="text" 	disabled="disabled"/> -->
         </td>
         <td colspan="4"></td>
       </tr>
       <tr>
         <td colspan="6" align="center">
                    <input type="button"  class="button" value="查询" onclick="query(1)"/>
                    <input type="button"  class="button" value="新增" onclick="toAdd()"/>
                    <input type="hidden" name="search" value="yes"/>
          </td>
      </tr>
     </table>

    <table id="detailResultList" class="tablelist tablelist2"  style="display: none;" >
     <thead>
        <tr valign="middle" class="title2">
         <th>商户号</th>
          <th>商户名</th><th>推送IP</th><th>新增时间</th><th>操作</th></tr></thead>
       <tbody id="bodyTable"></tbody>
    </table>
		<input type= "hidden" id = "userAuthIndex" value="<%=request.getParameter("userAuthIndex") %>"/>

   
   </div>
   <!--       wbox弹出层		  -->
			<table id="pupUpTable" class="tableBorder" style="display:none; width: 100%; height: auto; margin-top: 0px;white-space: nowrap;"></table>
  </body>
</html>
