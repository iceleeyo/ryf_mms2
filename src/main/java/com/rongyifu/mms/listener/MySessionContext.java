package com.rongyifu.mms.listener;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;


public class MySessionContext implements HttpSessionListener {
	
	private static final Map<HttpSession,String> sessionCache = new ConcurrentHashMap<HttpSession,String>();
	
	public void sessionCreated(HttpSessionEvent httpSessionEvent) {
    }

    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
    	sessionCache.remove(httpSessionEvent.getSession());
    }
    
    public static void addSession(HttpSession session,String user){
    	if(sessionCache.containsValue(user)){
    		Iterator<Entry<HttpSession,String>> it = sessionCache.entrySet().iterator();
    		while(it.hasNext()){
    			Entry<HttpSession,String> currentEntry = it.next();
    			HttpSession oldSession = currentEntry.getKey();
    			String oldUser = currentEntry.getValue();
    			if(user.equals(oldUser)){
    				try {
						oldSession.invalidate();
					} catch (Exception e) {
					}
    				it.remove();
    				break;
    			}
    		}
    	}
    	sessionCache.put(session, user);
    }

}