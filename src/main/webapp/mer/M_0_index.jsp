<%@ page contentType="text/html; charset=utf-8" language="java"%>
<%@ page import="com.rongyifu.mms.bean.LoginUser"%>
<%@ page import="com.rongyifu.mms.web.WebConstants"%>
<%@ page import="com.rongyifu.mms.web.LoginCheck"%>
<%@ page language="java" import="com.rongyifu.mms.common.Ryt"%>
<%
	String path = request.getContextPath();
    response.setHeader("Pragma","No-cache");
    response.setHeader("Cache-Control","no-cache");
    response.setDateHeader("Expires", 0);
%>
<html>
<head>
<style type="text/css">
<!--
body {
	margin-left: 0px;
	margin-top: 0px;
	margin-right: 0px;
	margin-bottom: 0px;
}
-->
</style>
<link type="image/x-icon" href="/mms/favicon.ico?" rel="shortcut icon">
<link rel="icon" href="/mms/favicon.ico?" type="image/x-icon">
<link rel="stylesheet" type="text/css" href="<%=path%>/public/css/head.css?"/>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="Expires" content="0"> 

<script type="text/javascript" src="<%=path%>/dwr/engine.js"></script>
<script type="text/javascript" src="<%=path%>/dwr/util.js"></script>
<script type="text/javascript" src="<%=path%>/dwr/interface/MenuService.js?v=2.9"></script>
<script type='text/javascript' src='<%=path %>/public/js/ajaxLoad.js'></script>
 
<script language="javascript">

function logOut(){
	//alert(2);
    location.reload();
}
//点击一级导航栏显示二级导航菜单
  function switchmodTag(menuId,thisName ,authIndex,authStr) {
  		var menus=document.getElementsByName("menu");
	  	for(var i=0;i<menus.length;i++){
	  			menus[i].className="";
	  	}
  	  	document.getElementById(menuId).className="upa";
	    MenuService.getMerLeftMenu(
	    		authIndex,
	    	    function(mstr){
	               $("left_menu_bottom").innerHTML = mstr ; 
	               turnMenu();
	             }
	   );
  }


 function turnMenu()
  {
      var oDiv=document.getElementById('left_menu_bottom');
      var aLi=oDiv.getElementsByTagName('h2');
      var aDiv=oDiv.getElementsByTagName('ul');
      aDiv[0].className='show';
      var i=0;
      for(i=0;i<aLi.length;i++)
      {
          aLi[i].onmousedown=function ()
          {
              var j=0;
              
              for(i=0;i<aLi.length;i++)
              {

                  aDiv[i].className='';
                  
                  if(aLi[i]==this)
                  {
                      j=i;
                  }
              }
              aDiv[j].className='show';
          };
      }
  };
  //显示一级导航栏
function showFirstMenu(){
	 useLoadingImage("../public/images/wbox/loading.gif");
	 MenuService.getLoginUserFirstMenu(function(menuList){
	 	if(menuList.lenght!=0)
			 dwr.util.addOptions("navigator",menuList,{escapeHtml:false});
			 showMMSNotice();
	 });
}
//显示通知信息
 function showMMSNotice(){
 	MenuService.queryMMSNotice(function(noticeList){
 		if(noticeList.length>0){
 			dwr.util.addOptions("nav2Menu",noticeList,{escapeHtml:false});
 			alert("您有"+noticeList.length+"条可查看的系统通知！");
 		}
 	});
 }
 window.onload=function(){
 	showFirstMenu();
 	
 };
 function logOutSys(mid, uid){
 	if(confirm("确定退出吗？"))
 		location.href="<%=path%>/logout.jsp?mid=" + mid + "&uid=" + uid;
 }
</script>
<title>电银信息在线支付平台</title>

</head>
<body style="overflow:auto;border: none; ">
<%
	LoginUser user = LoginCheck.checkLegality(session);  
    if(user==null){ 
    	response.sendRedirect( path + "/login.jsp");
    	return;
    }
%>
  
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" height="100%">
  <tr  id="rows1">
	<td width="100%" height="113">
		<div class="head" style="width:100%;" id="head">
        <img class="bj" src="<%=path%>/public/images/head.jpg" width="990" height="81"/>
        <div class="header">
            <p class="head_nav"> <a href="">&nbsp;　</a><a href="">　&nbsp;</a> </p>
            <p class="head_p">欢迎您&nbsp;<span><b><%=user.getOperName() %></b></span>&nbsp;&nbsp;
            <span id="loginedHistory">
            <%=user.getLogined().equals("0") ? "您是第一次登录" : "上次登录:"+user.getLogined()%>
            <%=Ryt.empty(user.getLastLoginIp()) ? "" : "上次登录IP:"+user.getLastLoginIp() + " "%>
            </span>            
              &nbsp;&nbsp;&nbsp;&nbsp;
             <a href="javascript:void(0);" onclick="logOutSys('<%=user.getMid()%>','<%=user.getOperId()%>')"><b>退出</b></a>
            </p>
        </div>
        <ul class="nav" id="navigator">
        </ul>
    </div>
	</td>
  </tr>
  
  <tr><td background="<%=path%>/images/03.gif"  height="1"></td></tr>
  
  <tr>
    <td valign="top">
    <table width="100%" height="100%" border="0" align="center" cellpadding="0" cellspacing="0">
		<tr>
			<td width="1" valign="top" class="admin01">    
		    	<div class="left" id="left">
					<div class="left_top">
					    <h4>商户：${sessionScope.SESSION_LOGGED_ON_USER.abbrev}</h4>
						<h4>商户号：<span id="number">${sessionScope.SESSION_LOGGED_ON_USER.mid}</span></h4>
						<h4>操作员号：<span id="number">${sessionScope.SESSION_LOGGED_ON_USER.operId}</span></h4>
						<!-- <a href="1_25_search_chang_oper_pass.jsp" target='content2' class="mima" onfocus="this.blur()"></a>
						 -->
					</div>
					<div class="left_bottom" id="left_menu_bottom">
					    <h2 class="nav_2"><label style="color: red">查看系统通知</label></h2>
					    <ul class="show" id="nav2Menu">
				            
                        </ul>
						<!--
						<h2 class="nav_2">交易管理</h2>
						<ul class="nav_3">
							<li><a href="1_0_index1.jsp" target='content2'>当天交易查询</a></li>
							<li><a href="#">明细查询</a></li>
							<li><a href="#">单笔查询</a></li>
							<li><a href="#">汇总查询</a></li>
							<li><a href="#">商户原始日志</a></li>
							<li><a href="#">银行交易单笔查询</a></li>
						</ul>
						 -->
					</div>
				</div>
			</td>
			<td width="99%" valign="top" class="admin01">
				<iframe id="content2" name="content2" frameborder="0"  src="<%=path%>/welcome.jsp" width="100%" height="100%" scrolling="auto"></iframe>
			</td>
		</tr>
 	</table>
 	</td>
 </tr>
 
 <tr bgcolor="#CCCCCC">
    <td height="25" valign="bottom" background="<%=path%>/images/bottom-bg.gif">
		<table align="center" width="100%">
	 		<tr align="center"><td align="center" style="text-align:center;"> Copyright © 2010   上海电银信息技术有限公司   All rights reserved.</td> </tr>
		</table>
	</td>
  </tr>
</table>
</body>
</html>
