package com.rongyifu.mms.utils;

/**
 ** 发送邮件需要使用的基本信息   
 */
import java.util.Properties;

public class MailSenderInfo {
	// 发送邮件的服务器的IP和端口
	private String mailServerHost;
	private String mailServerPort = "25";
	// 邮件发送者的地址
	private String fromAddress;
	// 邮件接收者的地址
	private String toAddress;
	// 登陆邮件发送服务器的用户名和密码
	private String userName;
	private String password;
	// 是否需要身份验证
	private boolean validate = false;
	// 邮件主题
	private String subject;
	// 邮件的文本内容
	private String content;
	// 邮件附件的文件名
	private String[] attachFileNames;

	/**
	 * 获得邮件会话属性
	 */
	public Properties getProperties() {
		Properties p = new Properties();
		p.put("mail.smtp.host", this.mailServerHost);
		p.put("mail.smtp.port", this.mailServerPort);
		p.put("mail.smtp.auth", validate ? "true" : "false");
		return p;
	}

	public String getMailServerHost() {
		return mailServerHost;
	}

	public void setMailServerHost(String mailServerHost) {
		this.mailServerHost = mailServerHost;
	}

	public String getMailServerPort() {
		return mailServerPort;
	}

	public void setMailServerPort(String mailServerPort) {
		this.mailServerPort = mailServerPort;
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	public String[] getAttachFileNames() {
		return attachFileNames;
	}

	public void setAttachFileNames(String[] fileNames) {
		this.attachFileNames = fileNames;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String textContent) {
		this.content = textContent;
	}

//	public static void main(String[] args) {
//		// 这个类主要是设置邮件
//		MailSenderInfo mailInfo = new MailSenderInfo();
//		mailInfo.setMailServerHost("smtp.163.com");
//		mailInfo.setMailServerPort("25");
//		mailInfo.setValidate(true);
//		mailInfo.setUserName("84_tan@163.com");
//		mailInfo.setPassword("841230");// 您的邮箱密码
//		mailInfo.setFromAddress("84_tan@163.com");
//		mailInfo.setToAddress("tan.yanbing@rytong.net");
//		mailInfo.setSubject("设置邮箱标题");
//		mailInfo.setContent("设置邮箱内容");
//
//		// 这个类主要来发送邮件
//		SimpleMailSender sms = new SimpleMailSender();
//		// sms.sendTextMail(mailInfo);//发送文体格式
//		sms.sendHtmlMail(mailInfo);// 发送html格式
//	}
	
	
	@SuppressWarnings("static-access")
	public static void sendmail(String Subject,String Content,String toAddress) {
		// 这个类主要是设置邮件
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost("smtp.163.com");
		mailInfo.setMailServerPort("25");
		mailInfo.setValidate(true);
		mailInfo.setUserName("rytong@163.com");
		mailInfo.setPassword("(rytong)");// 您的邮箱密码
		mailInfo.setFromAddress("rytong@163.com");
		mailInfo.setToAddress(toAddress);
		mailInfo.setSubject(Subject);
		mailInfo.setContent(Content);

		// 这个类主要来发送邮件
		SimpleMailSender sms = new SimpleMailSender();
		// sms.sendTextMail(mailInfo);//发送文体格式
		sms.sendHtmlMail(mailInfo);// 发送html格式
	}
	
	public static void main(String[] aa){
		
		sendmail("test","你好","yuanzy@lightpole.net");
		
	}
}