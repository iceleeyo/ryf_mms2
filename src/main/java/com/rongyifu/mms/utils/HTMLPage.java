package com.rongyifu.mms.utils;

public class HTMLPage {

	public static String noAuthHtmlPage() {
		StringBuffer page = new StringBuffer();
		page.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		page.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		page.append("<head>");
		page.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
		page.append("<title>操作失败</title>");
		page.append("<style type=\"text/css\">");
		page.append("body,h1,h2,ul,li{margin:0;padding:0;color:#000;}");
		page.append(".error_Head{height:69px;background:url(/mms/public/images/error_head.gif) 0 -501px repeat-x;}");
		page.append(".error_logo{width:965px;margin:0 auto;padding-top:3px;}");
		page.append(".error_center{width:965px;margin:0 auto;}");
		page.append("li{list-style-type:none;}");
		page.append("var{font-style:normal;}");
		page.append(".jiaoyitishi{height:167px;margin-top:16px;background:url(/mms/public/images/error_head.gif) 0 -167px repeat-x;}");
		page.append(".jiaoyitishi_left{height:167px;background:url(/mms/public/images/error_head.gif) 0 0 no-repeat;}");
		page.append(".jiaoyitishi_right{height:167px;background:url(/mms/public/images/error_head.gif) right  -334px no-repeat;}");
		page.append(".jiaoyitishi_right h2{padding-left:16px;font-size:14px;line-height:35px;}");
		page.append(".jiaoyimingxi{margin:33px 0 0 60px;background:url(\"/mms/public/images/dingdanxiqing_bj.gif\") no-repeat fixed 235px 35px;background-repeat:no-repeat;height:48px;padding-left:94px;}");
		page.append(".jiaoyimingxi_error{background-position:0 -48px;}");
		page.append("</style>");
		page.append("</head>");
		page.append("<body>");
		page.append("<div class=\"error_center\">");
		page.append("<div class=\"jiaoyitishi\">");
		page.append("<div class=\"jiaoyitishi_left\">");
		page.append("<div class=\"jiaoyitishi_right\">");
		page.append("<h2>操作失败</h2>");
		page.append("<h3 class=\"jiaoyimingxi jiaoyimingxi_error\\\">您没有权限访问该资源</h3>");
		page.append("</div>");
		page.append("</div>");
		page.append("</div>");
		page.append("</div>");
		page.append("</body>");
		page.append("</html>");

		return page.toString();
	}
	
	public static String htmlPage(String errMsg) {
		StringBuffer page = new StringBuffer();
		page.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		page.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		page.append("<head>");
		page.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
		page.append("<title>操作失败</title>");
		page.append("<style type=\"text/css\">");
		page.append("body,h1,h2,ul,li{margin:0;padding:0;color:#000;}");
		page.append(".error_Head{height:69px;background:url(/mms/public/images/error_head.gif) 0 -501px repeat-x;}");
		page.append(".error_logo{width:965px;margin:0 auto;padding-top:3px;}");
		page.append(".error_center{width:965px;margin:0 auto;}");
		page.append("li{list-style-type:none;}");
		page.append("var{font-style:normal;}");
		page.append(".jiaoyitishi{height:167px;margin-top:16px;background:url(/mms/public/images/error_head.gif) 0 -167px repeat-x;}");
		page.append(".jiaoyitishi_left{height:167px;background:url(/mms/public/images/error_head.gif) 0 0 no-repeat;}");
		page.append(".jiaoyitishi_right{height:167px;background:url(/mms/public/images/error_head.gif) right  -334px no-repeat;}");
		page.append(".jiaoyitishi_right h2{padding-left:16px;font-size:14px;line-height:35px;}");
		page.append(".jiaoyimingxi{margin:33px 0 0 60px;background:url(\"/mms/public/images/dingdanxiqing_bj.gif\") no-repeat fixed 235px 35px;background-repeat:no-repeat;height:48px;padding-left:94px;}");
		page.append(".jiaoyimingxi_error{background-position:0 -48px;}");
		page.append("</style>");
		page.append("</head>");
		page.append("<body>");
		page.append("<div class=\"error_center\">");
		page.append("<div class=\"jiaoyitishi\">");
		page.append("<div class=\"jiaoyitishi_left\">");
		page.append("<div class=\"jiaoyitishi_right\">");
		page.append("<h2>操作失败</h2>");
		page.append("<h3 class=\"jiaoyimingxi jiaoyimingxi_error\\\">"+errMsg+"</h3>");
		page.append("</div>");
		page.append("</div>");
		page.append("</div>");
		page.append("</div>");
		page.append("</body>");
		page.append("</html>");

		return page.toString();
	}

}
