package com.rongyifu.mms.filter;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import com.rongyifu.mms.bean.LoginUser;
import com.rongyifu.mms.merchant.MenuService;
import com.rongyifu.mms.utils.HTMLPage;
import com.rongyifu.mms.web.LoginCheck;

public class UserFilter implements Filter {
	/**
	 * 重定向的URL
	 */
	private String redirectURL = null;
	private static final String MERT = "M";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String contextPath = filterConfig.getServletContext().getContextPath();
		redirectURL = contextPath+"/login.jsp";
	}

	/**
	 * 使用过滤器中实现权限控制
	 */
	@Override
	public void doFilter(ServletRequest sRequest, ServletResponse sResponse, FilterChain filterChain)
					throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) sRequest;
		HttpServletResponse response = (HttpServletResponse) sResponse;
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		
		HttpSession session = request.getSession();
		String reqUrl = request.getRequestURI();
		LoginUser lu = LoginCheck.checkLegality(session);
		// 如果会话中的用户为空,页面重新定向到登录页面
		if (session == null || lu == null) {
			if (reqUrl.indexOf("login.jsp") == -1) {
				response.sendRedirect(redirectURL);
//				response.getWriter().print(HTMLPage.noAuthHtmlPage());
			} else {
				filterChain.doFilter(sRequest, sResponse);
			}
		} else {
			try {
				// 会话中存在用户，这里需要验证用户是否存在当前页面的权限
				if (validateUrl(lu, reqUrl)) {
					filterChain.doFilter(sRequest, sResponse);
				} else {
//					response.sendRedirect("/mms/sorry.jsp");
					response.getWriter().print(HTMLPage.noAuthHtmlPage());
				}
			} catch (Throwable e) {
				e.printStackTrace();
//				response.sendRedirect("/mms/sorry.jsp");
				response.getWriter().print(HTMLPage.noAuthHtmlPage());
//				return;
			}
		}
	}

	@Override
	public void destroy() {
	}

	public boolean validateUrl(LoginUser user, String reqUrl) {
		if ("1".equals(user.getMid())) return false;
		if (reqUrl.indexOf("admin") != -1) return false;
		String[] temp1 = reqUrl.split("/");
		String reqPath = temp1[temp1.length - 1];
		String[] r = reqPath.split("_");
		String t = r[0];
		String id = r[1];
		if (!t.equals(MERT)) return false;
		return MenuService.hasThisAuth(user.getAuth(), Integer.parseInt(id));

	}
}