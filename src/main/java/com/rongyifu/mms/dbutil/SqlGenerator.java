package com.rongyifu.mms.dbutil;

import com.rongyifu.mms.bean.LoginUser;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dbutil.sqlbean.TlogBean;
import com.rongyifu.mms.utils.LogUtil;

public class SqlGenerator {

	public static void main(String args[]) {

	}
	
	public void test(){
		TlogBean bean = new TlogBean();
		bean.setTseq(123L);
		bean.setMdate(20131011);
		bean.setVersion(10);
		bean.setBk_seq1("BK98138817");
//		bean.setMid("211");
		try {
			String sql2 = generateInsertSql(bean);
			LogUtil.printInfoLog("SqlGenerator", "insertTest", sql2);
			
			Map<String, Object> whereMap = new HashMap<String, Object>();
			whereMap.put("mid", "211");
			whereMap.put("oid", "RYF121'");
			String sql3 = generateUpdateSql(bean, whereMap);
			LogUtil.printInfoLog("SqlGenerator", "updateTest1", sql3);
			
			String sql4 = generateUpdateSql(bean, " mid = 764 ");
			LogUtil.printInfoLog("SqlGenerator", "updateTest2", sql4);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String generateUpdateSql(Object obj, Map<String, Object> whereMap) throws Exception {
		if(!(obj instanceof ISqlBean))
			throw new Exception("参数错误，必须实现ISqlBean接口！");
		
		if(whereMap == null || whereMap.size() == 0)
			throw new Exception("update语句没有where条件，请检查sql是否正确！");
		
		ISqlBean bean = (ISqlBean) obj;
		String updateKv = "";
		StringBuffer sql = new StringBuffer();

		Map<String, String> params = handleParams(obj);
		Iterator<String> iterator = params.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			String value = params.get(key);

			updateKv += key + "=" + value + ",";
		}

		sql.append("UPDATE " + bean.getTableName() + " SET ");
		if (!"".equals(updateKv.trim())) {
			sql.append(updateKv.substring(0, updateKv.length() - 1));
		}
		sql.append(" WHERE 1=1 " + handleWhereConditions(whereMap));

		return sql.toString();
	}
	
	public static String generateUpdateSql(Object obj, String whereConditions) throws Exception {
		if(!(obj instanceof ISqlBean))
			throw new Exception("参数错误，必须实现ISqlBean接口！");
		
		if(Ryt.empty(whereConditions))
			throw new Exception("update语句没有where条件，请检查sql是否正确！");
		
		ISqlBean bean = (ISqlBean) obj;
		String updateKv = "";
		StringBuffer sql = new StringBuffer();

		Map<String, String> params = handleParams(obj);
		Iterator<String> iterator = params.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			String value = params.get(key);

			updateKv += key + "=" + value + ",";
		}

		sql.append("UPDATE " + bean.getTableName() + " SET ");
		if (!"".equals(updateKv.trim())) {
			sql.append(updateKv.substring(0, updateKv.length() - 1));
		}
		sql.append(" WHERE " + whereConditions);

		return sql.toString();
	}
	
	public static String generateInsertSql(Object obj) throws Exception {
		if(!(obj instanceof ISqlBean))
			throw new Exception("参数错误，必须实现ISqlBean接口！");
		
		ISqlBean bean = (ISqlBean) obj;
		String sqlFields = "";
		String sqlValue = "";
		StringBuffer sql = new StringBuffer();

		Map<String, String> params = handleParams(obj);
		Iterator<String> iterator = params.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			String value = params.get(key);

			sqlFields += key + ",";
			sqlValue += value + ",";
		}

		sql.append("INSERT INTO " + bean.getTableName());
		if (!"".equals(sqlFields.trim())) {
			sql.append("(" + sqlFields.substring(0, sqlFields.length() - 1)
					+ ")");
		}
		sql.append(" VALUES(");
		if (!"".equals(sqlValue.trim())) {
			sql.append(sqlValue.substring(0, sqlValue.length() - 1));
		}
		sql.append(");");

		return sql.toString();
	}

	private static Map<String, String> handleParams(Object obj) throws Exception {
		Map<String, String> params = new HashMap<String, String>();

		// 获得对象的类型
		Class<? extends Object> classType = obj.getClass();
		// 获得对象的所有属性
		Field[] fields = classType.getDeclaredFields();
		
		for (int i = 0; i < fields.length; i++) {
			// 获取数组中对应的属性
			Field field = fields[i];
			String fieldName = field.getName();
			
			String stringLetter = fieldName.substring(0, 1).toUpperCase();
			// 获得相应属性的getXXX
			String getName = "get" + stringLetter + fieldName.substring(1);
			// 获取相应的方法
			Method getMethod = classType.getMethod(getName, new Class[] {});
			// 调用源对象的getXXX（）方法
			Object value = getMethod.invoke(obj, new Object[] {});

			if (value == null)
				continue;

			String paramValue;
			if (value instanceof String)
				paramValue = Ryt.addQuotes((String) value);
			else
				paramValue = String.valueOf(value);

			params.put(fieldName, paramValue);
		}

		return params;
	}
	
	private static String handleWhereConditions(Map<String, Object> map) throws Exception{
		String whereConditions = "";
		
		Iterator<String> iterator = map.keySet().iterator();
		while(iterator.hasNext()){
			String key = iterator.next();
			Object value = map.get(key);
			
			if(value instanceof String)
				whereConditions += " AND " + key + "=" + Ryt.addQuotes((String) value);
			else
				whereConditions += " AND " + key + "=" + String.valueOf(value);
		};
		
		return whereConditions;
	}

    /**
     * 通过Bean.get函数获取敏感信息时特殊处理
     * 参数以|noDec 结尾的 不做解密处理，否者需要解密
     * @param sd-> xxx|noDec or xxx|  or xxx
     * @return
     */
    public static String handleGetFuncSD(String sd){
        String result="";      
        try {
            if( null ==sd){
                result="";
            }else{
                String[] v=sd.split("\\|");
                int vlen=v.length;
                if(vlen==2 && "noDec".equals(v[1])){
                    result= v[0];
                }else{
                    result=Ryt.desDec(v[0]);
                }
                
            }
            
        } catch (Exception e) {
            LogUtil.printErrorLog("SqlGenerator", "handleGetFuncSD", "param:"+sd, e);
        }finally{
            return result;
        }
        
    }
    
}
