package com.rongyifu.mms.utils;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
* 由于Java的简单类型不能够精确的对浮点数进行运算，这个工具类提供精
* 确的浮点数运算，包括加减乘除和四舍五入。
*/
public class Digit {
	
	
	   public static double add(double v1,double v2){
	        BigDecimal b1 = new BigDecimal(Double.toString(v1));
	        BigDecimal b2 = new BigDecimal(Double.toString(v2));
	        return b1.add(b2).doubleValue();
	    }
	
	    //默认除法运算精度
	    private static final int DEF_DIV_SCALE = 10;
	    //这个类不能实例化
	    private Digit(){
	    }
	    
	    /**
	     * 提供精确的加法运算。
	     * @param v1 被加数
	     * @param v2 加数
	     * @return 两个参数的和
	     */
	    public static String add(String v1,String v2){
	        BigDecimal b1 = new BigDecimal(v1);
	        BigDecimal b2 = new BigDecimal(v2);
	        return b1.add(b2).toString();
	    }
	    
	    /**
	     * 提供精确的减法运算。
	     * @param v1 被减数
	     * @param v2 减数
	     * @return 两个参数的差
	     */
	    public static String sub(String v1,String v2){
	        BigDecimal b1 = new BigDecimal(v1);
	        BigDecimal b2 = new BigDecimal(v2);
	        return b1.subtract(b2).toString();
	    }
	    
	    /**
	     * 提供精确的乘法运算。
	     * @param v1 被乘数
	     * @param v2 乘数
	     * @return 两个参数的积
	     */
	    public static String mul(String v1,String v2){
	        BigDecimal b1 = new BigDecimal(v1);
	        BigDecimal b2 = new BigDecimal(v2);
	        return b1.multiply(b2).toString();
	    }

	    /**
	     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
	     * 小数点以后10位，以后的数字四舍五入。
	     * @param v1 被除数
	     * @param v2 除数
	     * @return 两个参数的商
	     */
	    public static String div(String v1,String v2){
	        return div(v1,v2,DEF_DIV_SCALE);
	    }

	    /**
	     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
	     * 定精度，以后的数字四舍五入。
	     * @param v1 被除数
	     * @param v2 除数
	     * @param scale 表示表示需要精确到小数点以后几位。
	     * @return 两个参数的商
	     */
	    public static String div(String v1,String v2,int scale){
	        if(scale<0){
	            throw new IllegalArgumentException(
	                "The scale must be a positive integer or zero");
	        }
	        BigDecimal b1 = new BigDecimal(v1);
	        BigDecimal b2 = new BigDecimal(v2);
	        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).toString();
	    }

	    /**
	     * 提供精确的小数位四舍五入处理。
	     * @param v 需要四舍五入的数字
	     * @param scale 小数点后保留几位
	     * @return 四舍五入后的结果
	     */
	    public static String round(String v,int scale){
	        if(scale<0){
	            throw new IllegalArgumentException(
	                "The scale must be a positive integer or zero");
	        }
	        BigDecimal b = new BigDecimal(v);
	        BigDecimal one = new BigDecimal("1");
	        return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).toString();
	    }
	    
	    /**
	     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
	     * 定精度，以后的数字四舍五入。
	     * @param v1 被除数
	     * @param v2 除数
	     * @param scale 表示表示需要精确到小数点以后几位。
	     * @return 两个参数的商
	     */
	    public static double div(double v1,double v2,int scale){
	        if(scale<0){
	            throw new IllegalArgumentException(
	                "The scale must be a positive integer or zero");
	        }
	        BigDecimal b1 = new BigDecimal(Double.toString(v1));
	        BigDecimal b2 = new BigDecimal(Double.toString(v2));
	        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
	    }
	    
	    /**
	     * 提供精确的乘法运算，结果用进一法处理。
	     * @param v1 被乘数
	     * @param v2 乘数
	     * @param scale 小数点后保留几位
	     * @return 两个参数的积进一后的结果
	     */
	    public static String mul(String v1,String v2,int scale){
	    	BigDecimal b1 = new BigDecimal(v1);
	        BigDecimal b2 = new BigDecimal(v2);
	        return b1.multiply(b2).setScale(scale,BigDecimal.ROUND_UP).toString();
	    }
	    
	    /**
	     * 把参数放大一百倍
	     * @param v  被放大的参数
	     * @return   返回参数放大100倍后的结果
	     */
	    public static String hundred(String v){
	    	return mul(v,"100",0);
	    }
	    
	    /**
	     * 把参数缩小一百倍
	     * @param v  被缩小的参数
	     * @return   返回参数缩小100倍后的结果
	     */
	    public static String percent(String v){
	    	return div(v,"100",2);
	    }
	    
	    /*
		 * 数据转换器 从对数据进行/100处理， 并且保留俩位小数amount应该为数据库原始值
		 */
		public static String amontConverter(String amount) {
			double a = new Double(amount);
			int b = 100;
			NumberFormat nbf = NumberFormat.getInstance();
			nbf.setMinimumFractionDigits(2);
			nbf.setMaximumFractionDigits(2);
			String res = nbf.format(a / b);
			return res.replaceAll(",", "");
		}
}
