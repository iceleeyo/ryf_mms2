package com.rongyifu.mms.dao;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReverseObject {
	/**
	 * 大写字母替换成_+小写
	 * @param inputStr
	 * @return
	 */
    public  String CharToUnderline (String inputStr){
    	String backStr=inputStr.replaceAll("([A-Z]{1})", "_$1").toLowerCase();
    	return backStr;
    }
    /**
     * 获得数据库表的名字
     * @param objClass
     * @return
     */
    public String getTableName(Class<?> objClass){
    	return CharToUnderline(objClass.getSimpleName()).substring(1);
    }
    public StringBuffer getSqlFacade(Class<?> objClass){
    	StringBuffer strBuff=new StringBuffer("insert into ");
   	      strBuff.append(getTableName(objClass));
   	      strBuff.append(" (");
   	      return strBuff;
    }
    /**
     * 获取对象属性的值
     * @param obj
     * @param field
     * @return
     * @throws Exception
     */
    public Object getMethodVal(Object obj,Field field) throws Exception{
    	PropertyDescriptor pd = new PropertyDescriptor(field.getName(),obj.getClass());
    	Method getMethod = pd.getReadMethod();//获得get方法
    	return getMethod.invoke(obj);//执行get方法返回一个Object
    }
    /**
     * 转换object为sql和field的数组
     * @param obj
     * @return
     * @throws Exception
     */
    public  Object[] reverseObjToSql(Object obj) throws Exception{
    	Class<?> objClass=obj.getClass();
    	Field[] fields =objClass.getDeclaredFields();
    	List<Object> objList = new ArrayList<Object>(); 
    	StringBuffer strBuff=getSqlFacade(objClass);
    		String countParam="";
    		for (Field field:fields) {
    			 Object methodObjVal=getMethodVal(obj, field);
	        	 if(methodObjVal!=null){
	        		 strBuff.append("`").append(CharToUnderline(field.getName())).append("`,");
	        		 countParam+="?,";
	        		 objList.add(methodObjVal);
	        	 }
			}
    	  if(countParam.equals(""))throw new Exception("The object is null!");
    	  String sqlBuff=strBuff.toString();
    	   sqlBuff=sqlBuff.substring(0,strBuff.toString().length()-1) ;
    	   countParam=countParam.substring(0,countParam.length()-1);
    	  String sql=sqlBuff+") values("+countParam+")";
    	  return new Object[]{sql,objList.toArray()};
    }
}
