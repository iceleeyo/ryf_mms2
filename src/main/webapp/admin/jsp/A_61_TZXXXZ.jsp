<%@ page language="java" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
	<script type='text/javascript' src='../../dwr/engine.js?<%=rand%>'></script>
    <script type='text/javascript' src='../../dwr/util.js?<%=rand%>'></script>
    <script type="text/javascript" src='../../dwr/interface/SysManageService.js?<%=rand%>'></script>
    <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
    <script type="text/javascript" src='../../public/js/ryt.js?v=<%=rand%>'></script>
    <script type="text/javascript" src="../../public/datePicker/WdatePicker.js?<%=rand%>"></script>
	<script type="text/javascript">
	 function addMsg(){

	    var msgtitle = document.getElementById("msgtitle").value.trim();
	    var msgMeat = document.getElementById("msgMeat").value.trim();
	    var operId = document.getElementById("operId").value.trim();
	    
	    if(msgtitle == ''){alert("请输入通知消息标题"); return false;}
	    if(!checkIllegalChar(msgtitle)) return false;
	    if(!checkIllegalChar(msgMeat))return false;	     
	    if(msgMeat  == ''){alert("请输入通知消息内容");return false;} 
	    var beginDate = document.getElementById("sys_date_begin").value.trim();
	    var endDate = document.getElementById("sys_date_end").value.trim();
	    if(beginDate == ''){alert("请输入有效起始时间");return false;}
	    if(endDate == ''){alert("请输入有效终止时间");return false;}
	    //var mid= document.getElementById("mid").value.trim();
	    var notice = new Object();
	    notice.beginDate = beginDate ;
	    notice.endDate = endDate ;
	    notice.title = msgtitle;
	    notice.content = msgMeat;
	    notice.operId = operId;
	    notice.mid = 1;
	    SysManageService.addOrEditMessage(notice,'add',callBack);
	 }
	 var callBack = function(msg){
	   if(msg == 'ok'){
	       alert("增加成功");
	       document.getElementById("msgtitle").value = "";
	       document.getElementById("msgMeat").value = "";
	       document.getElementById("sys_date_begin").value = "";
	       document.getElementById("sys_date_end").value = "";
	   }else{
	      alert("增加失败");
	   }
	 }
	
	function isMaxLen(o,info){  
		var Restlen = 0;
		var curlen= o.value.length;
		var nMaxLen=o.getAttribute? parseInt(o.getAttribute("maxlength")):"";
			
		if(o.getAttribute && o.value.length>nMaxLen){
			o.value=o.value.substring(0,nMaxLen)
		}else{
			Restlen=nMaxLen - curlen;
		
			var rest=document.getElementById(info);
			document.getElementById(info).value=Restlen;
		}
		return Restlen;
	} 
	/*
	function showChooseMid(){
	   var showToMid=document.getElementById("showToMid").value
	   if(showToMid==2){

	     PageService.getMinfosMap(function(minfos){
	        var minfoArr=new Array();
    	      for(var s in minfos){
    	          minfoArr[s].mid=s;
    	          minfoArr[s].abbrev=minfos[s];
    	      }
    	      alert(minfoArr.length);
    	  });
	     jQuery("#minfoBox").wBox({noTitle:true,show:true})
	   }
	} */
	</script>
  </head>
  
  <body>
  <table id="minfoBox"  class="tableBorder" style="display:none;width:700px;"><tr><td>dddd</td></tr></table>
		<input name="operId" id="operId" value="${sessionScope.SESSION_LOGGED_ON_USER.operId}" type="hidden"/>
   <div class="style">
    <table class="tableBorder" >
      <tbody>
          <tr>
                <td class="title" colspan="4">&nbsp;&nbsp; 通知信息新增</td>
          </tr>
          <tr>
         	   <td class="th1" align="right">信息标题 :</td>
               <td colspan="3" ><input type= "text" id="msgtitle" size="80" maxlength="64"  /></td>
          </tr>
      	  <tr>
              <td class="th1" align="right">信息内容：</td>
              <td colspan="3" ><textarea rows="10" cols="108" id="msgMeat" onkeydown="return isMaxLen(this,'intro_info');" onblur="return isMaxLen(this,'intro_info');" onkeyup="return isMaxLen(this,'intro_info');" name="corp_intro" maxlength="512"></textarea><br/>
                 最多可输入512个字，还能输入<input readonly="readonly" size="3" id="intro_info" value="512" name="intro" style="BACKGROUND-COLOR: Transparent; border:0px; color:#ff0000"/>个字   
              </td>
          </tr>
          <tr>
              <td class="th1" align="right">有效日期：</td>
              <td colspan="3" >
              		<input id="sys_date_begin" name="sys_date_begin" id="sys_date_begin"  class="Wdate" type="text" onfocus="WdatePicker({skin:'ext',minDate:'%y-%M-%d',maxDate:'#F{$dp.$D(\'sys_date_end\')||\'20201001\'}',dateFmt:'yyyyMMdd',readOnly:'true'});"/>&nbsp;&nbsp;&nbsp;&nbsp;至
                    <input id="sys_date_end" name="sys_date_end"  id="sys_date_end" class="Wdate" type="text" onfocus="WdatePicker({skin:'ext',minDate:'#F{$dp.$D(\'sys_date_begin\')||\'%y-%M-%d\'}',maxDate:'20201001',dateFmt:'yyyyMMdd',readOnly:'true'});"/>
                    <font color="red">*</font>有效日期必须填写
             </td>
          </tr>
         <!-- 
          <tr>
             <td class="th1" align="right">指定：</td>
              <td>
                 <select name="showToMid" id="showToMid" onchange="showChooseMid();"> 
                  <option value="0">商户和管理员</option>
                  <option value="1">只管理员</option>
                </select>
              </td>
          </tr> -->
          <tr>
              <td colspan="4"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" value="发布新通知"  onclick="addMsg();" class="button"/> </td>
          </tr>
      </tbody>
    </table>
    </div>
  </body>
</html>
