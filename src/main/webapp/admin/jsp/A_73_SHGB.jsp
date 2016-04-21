<%@ page language="java" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>商户关闭</title>
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
        <script type="text/javascript" src="../../dwr/engine.js"></script>
        <script type="text/javascript" src="../../dwr/util.js"></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand%>'></script>
        <script type="text/javascript" src="../../dwr/interface/MerchantService.js?<%=rand%>"></script>  
        <script type="text/javascript" src='../../public/js/ryt.js?<%=rand %>'></script>
        <script type="text/javascript">
         var mer_trade_type={};
         var prov_map={};
         function initParam(){
         	initMinfos();
         	PageParam.initMerInfo("add",function(data){
                   prov_map=data[0];
                   mer_trade_type=data[2];
            });
         }
         function showMinfoMsg(){
            var mid=document.getElementById("mid").value;
            //var smid=document.getElementById("smid").value;
            if(mid==""){
	            alert("商户不存在，请重新输入！");
	            return;
            }
            document.getElementById("minfoMsgTable").style.display="none";
         	MerchantService.getOneMinfo(mid, function(minfo) {
         	if(minfo==null){
         	alert("商户不存在！");
         	return;	
         	}
         	  document.getElementById("minfoMsgTable").style.display="";
         	  dwr.util.setValues( {
         	 		    minfoId:minfo.id,
         	  			id:minfo.id,
         	  			name:minfo.name,
         	  			abbrev:minfo.abbrev,
         	  			merTradeType:mer_trade_type[minfo.merTradeType],
         	  			provId:prov_map[minfo.provId],
         	  		    corpName:minfo.corpName});
         	});
         }
         function closeMinfo(){
            if(!confirm("你确认关闭吗？"))return;
            var mid=document.getElementById("minfoId").value;
            var loginMid=document.getElementById("loginMid").value;
            var loginUid=document.getElementById("loginUid").value;
			MerchantService.closeMinfo(mid,loginMid,loginUid,function(msg){
			     if(msg=="ok"){
			        alert("商户关闭成功！");
			         window.location.href="A_73_SHGB.jsp";
			     }else{
			        alert(msg);
			         window.location.href="A_73_SHGB.jsp";
			     }
			});
         }
        </script>
  </head>
  
  <body  onload="initParam();">
    
    <div class="style">
     <table class="tableBorder">
     	<tr><td colspan="2" class="title" >&nbsp;&nbsp;&nbsp;&nbsp;商户关闭</td></tr>
     	<tr><td class="th1" align="right"> 商户号：</td>
     	   <td> <input type="text" id="mid" name="mid" style="width: 150px;"  onkeyup="checkMidInput(this);"/>
                  <!-- <select style="width: 150px" id="smid" name="smid" onchange="initMidInput(this.value)">
                     <option value="">全部...</option>
                   </select> -->
           </td>
      </tr>
      <tr><td colspan="2" align="center"><input type="button" value="查看" onclick="showMinfoMsg();" class="button"/></td></tr>
     </table>
       <input type="hidden" id="minfoId" value=""/>
       <input type="hidden" id="loginMid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}"/>
       <input type="hidden" id="loginUid" value="${sessionScope.SESSION_LOGGED_ON_USER.operId}"/>
     <table class="tableBorder" id="minfoMsgTable" style="display:none;">
         <tr><td colspan="6" class="title">&nbsp;&nbsp;&nbsp;&nbsp;商户信息</td></tr>
	     <tr>
	     	<td align="right" class="th1" width="15%">商户号：</td><td width="20%" id="id"></td>
	     	<td align="right" class="th1" width="15%">商户全名：</td><td width="20%" id="name"></td>
	     	<td align="right" class="th1" width="15%">商户简称：</td><td id="abbrev"></td>
	     </tr>
	     <tr>
		     <td align="right" class="th1">所属行业：</td><td id="merTradeType"></td>
		     <td align="right" class="th1" >所在省份：</td><td id="provId"></td>
	     	 <td align="right" class="th1" >法人姓名：</td><td id="corpName"></td>
	    </tr>
	     <tr><td colspan="6"  align="center"><input type="button" value="确认关闭" onclick="closeMinfo();" class="button"/></td></tr>
     </table>
    </div>
  </body>
</html>
