package com.rongyifu.mms.web;

public class WebConstants {
	
	/** 当前登录用户 */
	public static final String SESSION_LOGGED_ON_USER = "SESSION_LOGGED_ON_USER";

	/** 当前登录用户mid */
	public static final String SESSION_LOGGED_ON_MID = "SESSION_LOGGED_ON_MID";
	
	/**允许用户登录密码连续输入失败的次数 */
	public static final int ALLOW_ERR_LOGIN_PWD_COUNT = 3;
	
	/**用户密码的有效期（天） */
	public static final int USER_PWD_PERIOD = 90;
	
	
	/**非融易付账户登录记录上面名称**/
	public static final String SESSION_LOGGED_ON_CUS_NAME = "SESSION_LOGGED_ON_CUS_NAME";
	
}
