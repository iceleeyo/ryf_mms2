package com.rongyifu.mms.common;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import com.rongyifu.mms.utils.LogUtil;

public class UploadService {
		
		//所有需要执行的类和方法的map集合
		private static Map<String, String[]> map=new HashMap<String, String[]>();
		
		private static String DEFAULT_UPLOAD_PATH="/opt/data/temp";
		
		
		public String dispatch(HttpServletRequest request){
			String []specificParams={};
			
			// 需要限制的文件后缀名
			String ext="";
			String path=DEFAULT_UPLOAD_PATH;
			// 设置大小限制（单位：字节）
			final int permitedSize = 80 * 1024 * 1024;
			try {
				// 获取句柄
				MultipartRequest multipartRequest = new MultipartRequest(request, DEFAULT_UPLOAD_PATH,
						permitedSize, "UTF-8", new DefaultFileRenamePolicy());
				
				//封装所有请求参数
				Map<String, String> paramMap=constructionParam(multipartRequest);
				
				String recs=paramMap.get("resource");
				
				if(!Ryt.empty(recs)){
					//上传之前的特殊校验  以及获取指定上传路径和后缀名  最终路径:/usr/data/current/
					specificParams=specificHandle(recs,paramMap);
				
				path=specificParams[0];//路径
				ext=specificParams[1];//后缀名
				//如果返回字符不是文件路径，则直接输出错误信息
				if(path.indexOf("/")<0){
					return path;
					}
				}
				boolean flag=uploadFile(multipartRequest,path,ext);
				if (flag)
					return "success";
				else
					return"fail";
			} catch (IOException e) {
				e.printStackTrace();
				return "expErr";
			} 
		}
		public static void main(String[] args) {
			
			System.out.println((".txt".split("\\|")).length);
		}
		
		/**
		 * 上传文件
		 * @param multipartRequest
		 * @param path 需要上传的文件路径
		 * @param exts  允许通过的文件格式 （“.txt|jpg”）
		 * @return
		 */
		public boolean uploadFile(MultipartRequest multipartRequest,String path,String exts){
			try {
				Enumeration files = multipartRequest.getFileNames();       
				// 取得文件详细信息 
				while (files.hasMoreElements()) { 
					   String name =(String)files.nextElement();
				       String fileName = multipartRequest.getFilesystemName(name);
				       if(Ryt.empty(fileName))continue;
				       File currentFile = multipartRequest.getFile(name);
				       	if(!checkExt(exts, fileName)){
				       		currentFile.delete();
				       		return false;
				       	}
				       currentFile.renameTo(new File(path+fileName));
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}  
			
		}
		
		/**
		 * 根据recs执行指定类和方法，如为空则不执行
		 * @param recs 用来获取指定类和方法的key
		 * @param paramMap 所有请求参数的map
		 * @return 返回错误信息和所需文件后缀名 或 路径和所需文件后缀名， 文件后缀名可以用“|”分隔  
		 * 		        例如:params=["usr/data/current/",".txt|.csv"]
		 */
		public String[] specificHandle(String recs ,Map<String, String> paramMap){
			String [] values=map.get(recs);
			
			//无需特殊处理，返回默认路径
			if(null==values) return new String[]{DEFAULT_UPLOAD_PATH,""};
			
			Class<?> [] paramType =new Class[1];
			paramType[0]=Map.class;
			
			try {
				Class<?> objClass=Class.forName(values[0]);
				Method	method=objClass.getMethod(values[1], paramType);
				return (String[]) method.invoke(objClass.newInstance(), paramMap);
			} catch (Exception e) {
				e.printStackTrace();
				return new String[]{"expErr",""};
			}
		}
		
		
		/**
		 * 构建请求参数的map
		 * @param multipartRequest
		 * @return 所有请求参数map
		 */
		public Map<String, String>  constructionParam(MultipartRequest multipartRequest){
			Map<String, String> paramMap=new HashMap<String, String>();
			
			// 取得其它非文件字段
			Enumeration params = multipartRequest.getParameterNames();
			
			while (params.hasMoreElements()) {
			    String key = (String)params.nextElement();
			    String value = multipartRequest.getParameter(key);
			    paramMap.put(key, value);
			}                      
			 
			return paramMap;
		}
		
		/**
		 * 检查文件后缀名
		 * @param exts 允许的文件后缀名"jpg|txt"
		 * @param fileName 文件名
		 * @return
		 */
		public boolean checkExt(String exts,String fileName){
			if(exts.equals("")) return true;
			boolean flag=false;
			String []ext=exts.split("\\|");
			for (String et : ext) {
				if(fileName.indexOf(et)>0){
					flag=true;
					break;
				}
			}
			return flag;
		}
		
		
		static{
			map.put("TBWJSC", new String[]{"com.rongyifu.mms.service.SysManageService","uploadTBfile"});
			
		}
		
}
