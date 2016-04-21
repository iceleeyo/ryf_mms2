package com.rongyifu.mms.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.rongyifu.mms.bean.LoginUser;


public class URLFilter  implements Filter {

	/**
	 * @see Filter#destroy()
	 */
	@Override
	public void destroy() {

	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		// place your code here

		// pass the request along the filter chain
		// chain.doFilter(request, response);
//		HttpServletRequest re = (HttpServletRequest) request;
//		
//		HttpServletResponse rp = (HttpServletResponse) response;
//
//		Iterator values = re.getParameterMap().values().iterator();// 获取所有的表单参数
//
//		while (values.hasNext()) {
//
//			String[] value = (String[]) values.next();
//
//			for (int i = 0; i < value.length; i++) {
//				System.out.println("value==="+value[i]);
//				if (sql_inj(value[i])) {
//				
//					rp.sendRedirect("/mms/sorry.jsp");
//					return;
//				}else if(value[i].equals("=")){
//					rp.sendRedirect("/mms/sorry.jsp");
//					return;
//				}else{
//					chain.doFilter(request, response);
//					return;
//				}
//			}
//		}
		HttpServletRequest re = (HttpServletRequest) request;
		HttpSession session = re.getSession();
		
		LoginUser ll=new LoginUser();
		
		ll.setMid("1");
		ll.setMd5Pwd("e3ceb5881a0a1fdaad01296d7554868d");
		ll.setMtype(0);
		ll.setOperId(2);
		ll.setOperState(0);
		ll.setRole(0);
		ll.setState(0);
		ll.setErrCount(0);
		ll.setExpDate(20200422);
		session.setAttribute("USER", ll);

		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	@Override
	public void init(FilterConfig fConfig) throws ServletException {

	}

	public static boolean sql_inj(String str) {
		//String inj_str = "'|and|exec|insert|select|delete|update|drop|join|where|union|alter|like|modify|cast|rename|count|*|%|chr|mid|master|truncate|char|declare|;|or|-|+|,";
		
		String inj_str = "'|and|exec|insert|select|delete|update|drop|join|where|union|alter|like|modify|cast|rename|count|*|%|chr|mid|master|truncate|char|declare|;|-|+|,";
		String inj_stra[] = inj_str.split("\\|");
		
		for (int i = 0; i < inj_stra.length; i++) {

				if (str.indexOf(" "+inj_stra[i]+" ") >= 0) {
					
					return true;
				}
		}
		
		return false;
	}
}
