package com.rongyifu.mms.utils;

import java.io.File;
import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import common.Logger;

/**
 * XML解析工具类
 */
public final class XmlUtils {

	private Document root;
	
	private Logger logger = Logger.getLogger(getClass());
	
	public XmlUtils(Document doc){
		this.root = doc;
	}
	
	public XmlUtils(File f){
		try {
			SAXReader saxReader = new SAXReader();
			root = saxReader.read(f);
		} catch (DocumentException e) {
			logger.error("从文件["+f.getName()+"]创建dom对象失败", e);
			throw new RuntimeException();
		}
	}
	
	public XmlUtils(InputStream in){
		try {
			SAXReader saxReader = new SAXReader();
			root = saxReader.read(in);
		} catch (DocumentException e) {
			logger.error("从InputStream创建dom对象失败", e);
			throw new RuntimeException();
		}
	}
	
	public XmlUtils(String xmlStr){
		try {
			root = DocumentHelper.parseText(xmlStr);
		} catch (DocumentException e) {
			logger.error("由字符串["+xmlStr+"]创建dom对象失败", e);
			throw new RuntimeException();
		}
	}
	
	public static void main(String[] args) {
		String xml="<?xml version=\"1.0\" encoding=\"utf-8\"?><res><status><value>0</value><msg>test1</msg></status><transResult><accountId>AID001</accountId><orderId>OID43211</orderId><transAmt>0.01</transAmt><transType>C1</transType><tseq>20150127104249100002</tseq><transStatus>W</transStatus><errorMsg></errorMsg><sysDate>20150127</sysDate><sysTime>104249</sysTime><merPriv>merPriv</merPriv><chkValue>663432E7420B20C4FCAB7B5A2C4550A9</chkValue></transResult></res>";
		XmlUtils utils = new XmlUtils(xml);
		System.out.println(utils.getSingleNodeText("msg"));
		System.out.println(utils.getNodeTextByPath("//res/transResult/orderId"));
	}
	
	/**
	* 根据节点名称获取节点的text 当包含多个匹配的节点名称时 返回第一个的text 未找到匹配的节点 返回空字符串""
	*/ 
	public String getSingleNodeText(String tagName){
		Node n = root.selectSingleNode("//"+tagName);
		if(n == null){
			return "";
		}
		return n.getText(); 
	}
	
	/**
	* @title: getNodeTextByPath
	* @description: 以xPath的方式获取节点的text 如果xPath未到达叶节点 返回的是空字符串 当表达式匹配多个叶节点时 仅返回第一个节点的text
	* @author li.zhenxing
	* @date 2015-1-28
	* @param path eg://res/transResult/orderId
	* @return
	*/ 
	public String getNodeTextByPath(String path){
		if(path == null || "".equals(path)){
			return "";
		}
		if(!path.startsWith("//")&&!path.startsWith("/")){
			path  = "//" + path;
		}
		Node n = root.selectSingleNode(path);
		if(n == null){
			return "";
		}
		return n.getText();
	}
	
	/**
	* 根据节点名称获取节点的text 当包含多个匹配的节点名称时 返回第一个的text 未找到匹配的节点 返回空字符串""
	*/ 
	public static String getSingleNodeText(String tagName,Node node){
		Node n = node.selectSingleNode("//"+tagName);
		if(n == null){
			return "";
		}
		return n.getText(); 
	}
	
	/**
	* @title: getNodeTextByPath
	* @description: 以xPath的方式获取节点的text 如果xPath未到达叶节点 返回的是空字符串 当表达式匹配多个叶节点时 仅返回第一个节点的text
	* @author li.zhenxing
	* @date 2015-1-28
	* @param path /paretnElement
	* @return
	*/ 
	public static String getNodeTextByPath(String path,Node node){
		if(path == null || "".equals(path)){
			return "";
		}
		if(!path.startsWith("//")&&!path.startsWith("/")){
			path  = "//" + path;
		}
		Node n = node.selectSingleNode(path);
		if(n == null){
			return "";
		}
		return n.getText();
	}
}
