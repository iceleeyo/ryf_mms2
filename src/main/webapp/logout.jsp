<%@ page contentType="text/html; charset=utf-8" %>
<%@page import="com.rongyifu.mms.web.WebConstants"%>
<%@page import="com.rongyifu.mms.dao.MerOperDao"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
        <META http-equiv="Expires" content="0">  
<title></title>
</head>
<body>

<%		
    String contextPath = request.getContextPath();
    String path = contextPath + "/login.jsp";
        
    try {
        session.removeAttribute(WebConstants.SESSION_LOGGED_ON_USER);
        session.removeAttribute(WebConstants.SESSION_LOGGED_ON_MID);
        //session.removeAttribute("URLS");
        session.invalidate();
		String mid =request.getParameter("mid");
		int uid = Integer.parseInt(request.getParameter("uid"));
	    if("1".equals(mid)){
	       path = contextPath + "/admin/login.jsp";
	    }
	    
	    new MerOperDao().saveOperLog(mid,uid,"退出系统","退出系统",request);
    } catch(Exception e){
        response.sendRedirect(path);
        return;
    }
   response.sendRedirect(path);
%>
</body>
</html>
