<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>操作员管理</title>
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
	    <script type="text/javascript" src='../../dwr/interface/MerMerchantService.js?<%=rand%>'></script> 
		<script type="text/javascript" src="../../public/datePicker/WdatePicker.js?<%=rand%>"></script>
		<script type="text/javascript" src="../../public/js/merchant/mer_jsp_queryMessage.js?<%=rand%>"></script>
		
		
		<script  type="text/javascript">
		
		
		
		</script>
		
		
  </head>
  
  <body>
  
   <div class="style">
    <table class="tableBorder" >
	      <tbody>
	          <tr>
	                <td class="title" colspan="2">&nbsp;&nbsp; 通知信息查询</td>
	          </tr>
	          <tr>
	              <td class="th1" align="right">选择发布期 ：</td>
	              <td  height="30px">
	              		<input id="v_sys_date_begin" name="v_sys_date_begin" class="Wdate" type="text" onfocus="WdatePicker({skin:'ext',maxDate:'#F{$dp.$D(\'v_sys_date_end\')||\'20201001\'}',dateFmt:'yyyyMMdd',readOnly:'true'});"/>&nbsp;&nbsp;&nbsp;&nbsp;至
                        <input id="v_sys_date_end" name="v_sys_date_end"  class="Wdate" type="text" onfocus="WdatePicker({skin:'ext',minDate:'#F{$dp.$D(\'v_sys_date_begin\')}',maxDate:'20201001',dateFmt:'yyyyMMdd',readOnly:'true'});"/> &nbsp;&nbsp; 
	              <input type="button" value="查询"  onclick=" queryMsg();" class="button"/> </td>
	          </tr>
	      </tbody>
	   </table>
	   <br/>
	   <table class="tablelist tablelist2"  id="noticeTable" style="display:none ">
			<thead>
			<tr>
			    <th>通知开始日</th>
				<th>通知结束日</th>
				<th>通知标题</th>
			    <th>详细信息</th>
			</tr>
			</thead>
			<tbody id="biaotou"></tbody>							
	  </table>
	  <table class="tableBorder" id="editORdetailNotice" style="display:none ">
			<tbody  >
	           <tr>
	         	   <td class="th1" align="right">信息标题 :</td>
	               <td colspan="3" ><input type="hidden" id = "noticeid"/> <input type= "text" id="msgtitle" size="80" maxlength="64" readonly="readonly" style="border:none;"/>  </td>
	           </tr>
	      	   <tr>
	              <td class="th1" align="right">信息内容 :</td>
	              <td colspan="3" ><textarea rows="15" cols="108" id="msgMeat" style="overflow-y:auto;border:none;"  readonly="readonly"></textarea>  </td>
	           </tr>
	           <tr>
              	  <td colspan="4" align="center">
              	  <input type="button" value="返回"  class="button" onclick="back();" /> </td>
               </tr>
		    </tbody>								
	  </table>
	  </div>
  </body>
</html>
