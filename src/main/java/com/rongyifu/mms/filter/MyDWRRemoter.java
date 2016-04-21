package com.rongyifu.mms.filter;

import javax.servlet.http.HttpSession;

import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.extend.Calls;
import org.directwebremoting.extend.Replies;
import org.directwebremoting.impl.DefaultRemoter;
import org.directwebremoting.proxy.dwr.Util;

/**
 * js 过滤器
 */
public class MyDWRRemoter extends DefaultRemoter {
	@Override
	public Replies execute(Calls calls) {
		WebContext webContext=WebContextFactory.get();
		
		String uri = webContext.getHttpServletRequest().getRequestURI();
		if(uri.indexOf("__System.pageLoaded")>-1||uri.indexOf("LoginService.adminLogin")>-1
							||uri.indexOf("LoginService.merUserLogin")>-1){
			return super.execute(calls);
		}
		HttpSession session = webContext.getSession(false);
//		String page=webContext.getCurrentPage();
		if (session == null) {
			logOut();
			return super.execute(new Calls());
		}
		return super.execute(calls);
	}

	private void logOut() {
		WebContext wct = WebContextFactory.get();
		Util utilThis = new Util(wct.getScriptSession());
		utilThis.addScript(new ScriptBuffer("logOut()"));
	}

}
