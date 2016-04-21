package com.rongyifu.mms.utils;

import java.text.NumberFormat;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

/**
 * 该类用于计算手续费
 * reckon(x1,x2)
 * x1 传规定格式的计算公式
 * X2 传交易金额
 * X2可以是int float double 或String 类型的数字
 * 按公式定义的固定公式填写
 * @author Administrator
 *
 */
public class ChargeMode {
	
	/**
	 * @param designModel vas的手续费计算公式
	 * @param amount  金额
	 * @param defAmout 默认值
	 * @return
	 * @throws Exception 
	 */
	public static String reckon(String designModel ,String amount,String defAmout) {
		
		if(designModel ==null){
			return "0.00";
		}
		if(designModel.startsWith("X")) {
			designModel = designModel.substring(1);
//			double a = new Double(amount.trim());
//			double b = new Double(diffAmout.trim());
//			String m = designModel.replaceFirst("X","");
//			return new Double(reckon(m ,a-b)) + new Double( diffAmout) + "";
		}
		
		if(designModel.equals("FROMBKFILE")){
			return defAmout == null ?  "0.00" : defAmout;
		}
		
		
		
		return  reckon(designModel ,amount);
	}
	/**
	 * @param designModel,计算公式
	 * @param amount 金额
	 * @return 带两位小数的字符型数字
	 */
	public static String reckon(String designModel ,String amount) {
		return design(designModel ,amount);
	}
	/**
	 * @param designModel,计算公式
	 * @param amount 金额
	 * @return 带两位小数的字符型数字
	 */
	public static String reckon(String designModel ,int amount) {
		return design(designModel ,String.valueOf(amount));
	}
	
	/**
	 * @param designModel,计算公式
	 * @param amount 金额
	 * @return 带两位小数的字符型数字
	 */
	public static String reckon(String designModel ,double amount) {
		return design(designModel ,String.valueOf(amount));
	}
	/**
	 * @param designModel,计算公式
	 * @param amount 金额
	 * @return 带两位小数的字符型数字
	 */
	public static String reckon(String designModel ,float amount) {
		return design(designModel ,String.valueOf(amount));
	}
	
	/**************************************************************************/
	
	// 固定交易费率，无上下限
	// AMT*R，其中R代表费率
	// 如费率为0.008,则计费公式为AMT*0.008
	private static String amt(String r, String amount) {
		double a = new Double(amount.trim());
		double b = new Double(r.trim());
		return amontConverter(a * b);
	}
	
	//固定交易费率，有下限	MAX(X1,AMT*R) ，其中X1代表下限,R代表费率	
	//如费率为0.005，下限为0.2，则计费公式为MAX(0.2,AMT*0.005)	
	private static String max(String x1, String x2, String amount) {
		double xx = new Double(x1.trim());
		double rr = new Double(design(x2.trim() ,amount.trim()));
		return amontConverter(rr < xx ? xx : rr);
	}
	
	// 固定交易费率，有上限
	// MIN(X1,AMT*R)，其中X1代表上限，R代表费率;
	// 如费率为0.005，上限为50，则计费公式为MIN(50,AMT*0.005)
	private static String min(String x1, String x2, String amount) {
		
		double xx = new Double(x1.trim());
		double rr = new Double(design(x2.trim() ,amount.trim()));
		return amontConverter(rr > xx ? xx : rr);
	}
	
	// 固定交易费率，有下限和上限
	// MTM(x1,x2,AMT*R)，其中x1,x2分别代表下限和上限，可以为计费公式，R代表费率
	// 如费率为0.006，下限为0.8，上限为500，则计费公式为MTM(0.8,500,AMT*0.006)
	private static String mtm(String x1, String x2, String x3, String amount) {
		double xx1 = new Double(x1.trim());
		double xx2 = new Double(x2.trim());
		double xx3 = new Double(design(x3.trim() ,amount.trim()));
		
		double res = xx3;
		if(xx3 > xx2 ) res = xx2;
		if(xx3 < xx1 ) res = xx1;
		return amontConverter(res);
	}
	
	// 单笔固定费用
	// SGL(X) 其中X代表单笔固定手续费
	// 如单笔固定为2.5元，则计费公式为SGL(2.5)
	private static String sgl(String x) {
		double a = new Double(x.trim());
		return amontConverter(a);
	}
	
	// 单笔浮动费率
	// FLO(x1,R1,x2,R2,R3)，其中x1,x2…..代表分界档，R1,R2,R3…..代表分界档内的计费费率。其中不包含上分界档
	// 如计费费率为:
	// <500,费率为0.08
	// 500-5000费率为0.06(包含500)
	// 5000-50000费率为0.05(包含5000)
	// 50000-500000费率为0.03(包含50000)
	// >=5000000,费率为0.01
	// 则计费公式为：FLO(500,0.08,5000, 0.06,50000, 0.05,500000, 0.03, 0.01)
	private static String flo(String[] args,String amount) {
		
		double amounted = new Double(amount.trim());//交易金额
		
		double lamount = new Double(args[args.length - 3].trim());//最大金额 
		double lafee = new Double(args[args.length - 1].trim());//最大金额时手续费
		if(amounted >= lamount){//最大金额 new Double(args[args.length - 3])
			return amontConverter(amounted * lafee );
		}
		double res = 0;//最后手续费
		for (int i = 0; i < args.length - 1; i++) {
			if (i % 2 == 0) {
				double famount =  new Double(args[i].trim());
				if(amounted < famount){
					res = new Double(args[i+1].trim()) * amounted;
					break;
				}
			} 
		}
		return amontConverter(res);
	}
	
	// 分段固定手续费
	//	FIX(x1,R1,x2,R2,R3)，其中x1,x2…..
	//其中x1,x2…..代表分界档，R1,R2,R3…..代表分界档内的固定手续费。其中不包含上分界档 
	//如计费费率为:
	// <20000,手续费为2
	// 20000-50000,手续费为3
	// 50000-100000,手续费为5
	// >=100000,费率为10
	// 则计费公式为：FIX(20000,2,50000,3,100000,5,10)
	private static String fix(String[] args,String amount) {
		double amounted = new Double(amount.trim());//交易金额
		double lastfee = new Double(args[args.length - 1].trim());//最大金额时手续费
		double lastamount = new Double(args[args.length - 3].trim());//最大金额
		double res = 0;//最后手续费
		if(amounted >= lastamount){
			return amontConverter(lastfee);
		}
		for (int i = 0; i < args.length - 1; i++) {
			if (i % 2 == 0) {
				double famount =  new Double(args[i].trim());
				double fee = new Double(args[i+1].trim());
				if(amounted < famount){
					res = fee;
					break;
				}
			} 
		}
		return amontConverter(res);
	}
	
	// 增量计费 INC(X,N)，最低手续费为X，每增加N的金额，手续费就增加X 
	//如：最低手续费为2.5元，交易金额每增加20000元，手续费就增加2.5元，
	//则计费公式为INC(2.5，20000) 
	private static String inc(String x,String n ,String amount) {
		double a = new Double(x.trim());
		double b = new Double(n.trim());
		double c = new Double(amount.trim());//交易金额
		double res = ((int)(c/b))*a;
		return amontConverter(res < a ? a : res);
	}
	
	// 最低额计费 IFBIG(x1,x2)，x1为最低计费额，x2为其它组合公式
	// 只有在该额度以上才能计费，否则为零；在大于该额度以上，计费公式为X2。
	//如：只有在5000(含5000)以上才能计费，计费的规则为：充值奖励基数为5000元奖励5元,
	//每递增1000元多奖励1元.即每次充值满5000返5元,每次充值满6000返6元,
	//依此类推,不足1000的尾数部分不计入统计，则计费公式为IFBIG(5000,INT(1,1000))
	private static String ifBig(String x1,String x2 ,String amount) {
		double xx1 = new Double(x1.trim());
		double amt = new Double(amount.trim());//交易金额
		double res = 0;
		if(amt < xx1){
			return amontConverter(res);
		}
		return design(x2.trim() ,amount.trim());
	}
	
	//银行手续费计费格式
	//x1,最小 x2，最大，x3 费率，v4 第三位是多少时进一
	private static String bkfee(String x1, String x2, String x3 ,String x4,String amount){
		double xx1 = new Double(x1);
		double xx2 = new Double(x2);
		int i = Integer.parseInt(x4.trim());
		String AMT = Digit.mul(x3, amount);
		
		if(AMT.contains(".")){
			String zs = AMT.split("\\.")[0];
			String xs = AMT.split("\\.")[1];
			if(xs.length()>=3){
				String d1 = xs.substring(0,1);
				String d2 = xs.substring(1,2);
				String d12 = d1+d2;
				String d3 = xs.substring(2,3);
				int ii = Integer.parseInt(d3);
				if(ii >= i ) {
					int d1int = Integer.parseInt(d1);
					int d2int = Integer.parseInt(d2);
					if(d1int == 0){
						int a = (Integer.parseInt(d2) + 1 );
						if(a<10)  {
							d12 = "0"+a;
						}else{
							d12 = ""+a;
						}
					}else
					if(d1int == 9 && d2int == 9 ){
						d12 = "00";
						zs = (Integer.parseInt(zs)+1)+"";
					}else{
						d12 = (Integer.parseInt(d12)+1)+"";
					}
				}
				AMT = zs + "."+ d12;
			}
		}
		double amt = new Double(AMT);
		if(amt<xx1) return xx1+"";
		if(amt>xx2) return xx2+"";
		return AMT + ""; 
	
	}
	
	//环迅计费公式固定为IPSMAX(X1,AMT*R)
	private static String ipsmax(String x1, String model, String amount){
		double xx1 = new Double(x1);
		String s = Digit.mul(model.split("\\*")[1].trim(), amount);
		if(s.contains(".")){
			String xsd = s.split("\\.")[1];
			if(xsd.length()>=3 && "0".equals(String.valueOf(xsd.charAt(2)))){
				String res = s.split("\\.")[0]+"."+xsd.charAt(0)+xsd.charAt(1);
				double amt = new Double(res);
				return xx1 > amt ? xx1 + "" : amt + "";
			}
		}
		double amt = new Double(Digit.mul(model.split("\\*")[1].trim(), amount,2));
		return xx1 > amt ? xx1 + "" : amt + ""; 
	
	}
	
	private static String amontConverter(double amount) {
		double a = new Double(amount+0.0000001d);
		NumberFormat nbf = NumberFormat.getInstance();
		nbf.setMinimumFractionDigits(2);
		nbf.setMaximumFractionDigits(2);
		String res = nbf.format(a);
		return res.replaceAll(",", "");
	}

	private static String design(String designModel ,String amount) {
		double result = 0;
		if(null == designModel || designModel.trim().equals("")){
			return amontConverter(result);
		} 
		String m = designModel.trim().toUpperCase();
		//AMT*R  amt(String r, String amount)
		if(m.startsWith("AMT")){
			String r = m.split("\\*")[1];
			return amt(r.trim(),amount);
		}
		//MAX(X1,AMT*R) ，max(String x1, String x2, String amount)	
		if(m.startsWith("MAX")){
			String r = m.substring(m.indexOf("(")+1, m.lastIndexOf(")"));
			String[] args = r.trim().split(",",2);
			return max(args[0],args[1],amount);
		}
		//MAX(X1,AMT*R) ，max(String x1, String x2, String amount)	
		if(m.startsWith("MIN")){
			String r = m.substring(m.indexOf("(")+1, m.lastIndexOf(")"));
			String[] args = r.trim().split(",",2);
			return min(args[0],args[1],amount);
		}
		//MTM(x1,x2,AMT*R)  ,mtm(String x1, String x2, String x3, String amount)
		if(m.startsWith("MTM")){
			String r = m.substring(m.indexOf("(")+1, m.lastIndexOf(")"));
			String[] args = r.split(",");
			return mtm(args[0],args[1],args[2],amount);
		}
		//SGL(X) sgl(String x)
		if(m.startsWith("SGL")){
			String r = m.substring(m.indexOf("(")+1, m.lastIndexOf(")"));
			return sgl(r);
		}
		//FLO(x1,R1,x2,R2,R3)  flo(String[] args,String amount)
		if(m.startsWith("FLO")){
			String r = m.substring(m.indexOf("(")+1, m.lastIndexOf(")"));
			String[] args = r.split(",");
			return flo(args,amount);
		}
		//FIX(20000,2,50000,3,100000,5,10)  fix(String[] args,String amount)
		if(m.startsWith("FIX")){
			String r = m.substring(m.indexOf("(")+1, m.lastIndexOf(")"));
			String[] args = r.split(",");
			return fix(args,amount);
		}
		//INC(X,N)  inc(String x,String n ,String amount)
		if(m.startsWith("INC")){
			String r = m.substring(m.indexOf("(")+1, m.lastIndexOf(")"));
			String[] args = r.split(",");
			return inc(args[0], args[1] , amount);
		}
		//IFBIG(x1,x2) ifBig(String x,String method ,String amount)
		if(m.startsWith("IFBIG")){
			String r = m.substring(m.indexOf("(")+1, m.lastIndexOf(")"));
			String[] args = r.split(",",2);
			return ifBig(args[0], args[1] , amount);
		}
		if(m.startsWith("IPSMAX")){
			String r = m.substring(m.indexOf("(")+1, m.lastIndexOf(")"));
			String[] args = r.trim().split(",",2);
			return ipsmax(args[0],args[1],amount);
		}
		
		
		//BKFEE(v1,v2,v3,v4)
		if(m.startsWith("BKFEE")){
			String r = m.substring(m.indexOf("(")+1, m.lastIndexOf(")"));
			String[] args = r.trim().split(",");
			return bkfee(args[0],args[1],args[2],args[3],amount);
		}
		
		
		
		
		
		return amontConverter(result);
	}

}
