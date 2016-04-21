<%@ page language="java" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
<%
response.setHeader("Pragma","No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", 0);
int rand = new java.util.Random().nextInt(10000);
%>  
	<meta http-equiv="pragma" content="no-cache"/>
	<meta http-equiv="cache-control" content="no-cache"/>
	<meta http-equiv="expires" content="0"/>    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3"/>
	<link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css"/>   	
	<script type='text/javascript' src='../../dwr/engine.js'></script>
    <script type='text/javascript' src='../../dwr/util.js'></script>
    <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
      
    <script type="text/javascript" src='../../dwr/interface/SysManageService.js?<%=rand%>'></script> 
	<script type="text/javascript" src="../../public/datePicker/WdatePicker.js?<%=rand%>"></script>
	<script type="text/javascript" src="../../public/js/ryt.js?<%=rand%>" ></script>
	<script type="text/javascript" src="../../public/js/sysmanage/editMessage.js?<%=rand%>" ></script>
	<script type="text/javascript">
	  var operId=${sessionScope.SESSION_LOGGED_ON_USER.operId};//El表达式获取的值
	</script>
  </head>
  <body>
   <div class="style">
	   <table class="tableBorder" >
	      <tbody>
	          <tr>
	                <td class="title" colspan="4">&nbsp;&nbsp;通知信息修改</td>
	          </tr>
	          <tr>
	              <td class="th1" align="right">有效日期:</td>
	              <td colspan="3" >
	             <input id="v_sys_date_begin" name="v_sys_date_begin" class="Wdate" type="text" onfocus="WdatePicker({skin:'ext',maxDate:'#F{$dp.$D(\'v_sys_date_end\')||\'20201001\'}',dateFmt:'yyyyMMdd',readOnly:'true'});"/>&nbsp;&nbsp;&nbsp;&nbsp;至
	             <input id="v_sys_date_end" name="v_sys_date_end"  class="Wdate" type="text" onfocus="WdatePicker({skin:'ext',minDate:'#F{$dp.$D(\'v_sys_date_begin\')}',maxDate:'20201001',dateFmt:'yyyyMMdd',readOnly:'true'});"/>
	             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	             <input type="button" value=" 查 询 "  onclick="queryMsg();" class="button"/>
	             </td>
	          </tr>
	      </tbody>
	   </table>
	   <br/>
	   <table class="tablelist tablelist2"  id="detailNotice" style="display: none;">
			<tr>
			    <th>通知开始日</th>
				<th>通知结束日</th>
				<th>通知标题</th>
			    <th>修改编辑</th>
			    <th>详细信息</th>
			    <th>删除信息</th>
			</tr>
			<tbody id="biaotou"></tbody>								
	  </table>
	   <table class="tableBorder" id="editORdetailNotice" style="display: none">
			<tbody >
	           <tr>
	         	   <td class="th1" align="right">信息标题 :</td>
	               <td colspan="3" ><input type="hidden" id = "noticeid"/> <input type= "text" id="msgtitle" size="80" maxlength="64"  />  </td>
	           </tr>
	      	   <tr>
	              <td class="th1" align="right">信息内容 :</td>
	              <td colspan="3" ><textarea rows="10" cols="108" id="msgMeat"   onkeydown="return isMaxLen(this,'intro_info');" onblur="return isMaxLen(this,'intro_info');" onkeyup="return isMaxLen(this,'intro_info');" name="corp_intro" maxlength="512"></textarea><br/>
	                 最多可输入512个字，还能输入<input readonly="readonly" size="3" id="intro_info" value="512" name="intro" style="BACKGROUND-COLOR: Transparent; border:0px; color:#ff0000"/>个字   
	              </td>
        	  </tr>
	           <tr>
	              <td class="th1" align="right">有效日期:</td>
	              <td colspan="3" >
	              		<input name="sys_date_begin" id="sys_date_begin" value="" class="Wdate" type="text" onfocus="WdatePicker({skin:'ext',maxDate:'#F{$dp.$D(\'sys_date_end\')||\'20201001\'}',dateFmt:'yyyyMMdd',readOnly:'true'});"/>&nbsp;&nbsp;&nbsp;&nbsp;至
	                    <input name="sys_date_end"  id="sys_date_end" value="" class="Wdate" type="text" onfocus="WdatePicker({skin:'ext',minDate:'#F{$dp.$D(\'sys_date_begin\')}',maxDate:'20201001',dateFmt:'yyyyMMdd',readOnly:'true'});"/>
	             		<font color="red">*</font>有效日期必须填写
	             </td>
	           </tr>
	           <tr>
              	  <td colspan="4"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type="button" value="修改" id ="edit"   onclick="addMsg();" class="button"/> 
              	 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" value="返回"   onclick="back();" class="button" /> </td>
               </tr>
		    </tbody>								
	  </table>
	  </div>
  </body>
</html>
