package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.common.Ryt;

/**
 * 江苏银行 vas
 * 
 * @author Administrator
 * 
 */
public class CheckDataJSBImp implements SettltData {

	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
		List<SBean> sbeanList = new ArrayList<SBean>();

//		对账日期:	2012/10/25	至	2012/10/25	总笔数:	3
//		订单编号	交易类型	商户名称	商户订单号	订单日期	订单交易币种	订单交易金额	订单手续费	付款方账号	付款方户名	收款方账号	收款方户名	清算对账日期	清算对账状态	清算对帐结果	本金结算日期	本金结算标志	订单状态
//		201505061027966768	支付	上海电银信息技术有限公司	PH150506111321835268	201505061113	人民币	50.00	0.00	6221731001003455067	李勇	99010159540005003	上海电银支付清算过渡户	20150507	已对账	正常	20150507	已结算	已支付		
//		201505061027966037	支付	上海电银信息技术有限公司	PH150506110653914846	201505061107	人民币	100.00	0.00	6228760505000966068	石庭松	99010159540005003	上海电银支付清算过渡户	20150507	已对账	正常	20150507	已结算	已支付		
//		201505061027984370	退款	上海电银信息技术有限公司	TT150501062938197055	201505010000	人民币	100.00	0.00	18260188000002780	上海电银信息技术有限公司	6228761205000202932	黄思荣	20150507	已对账	正常	--	未结算	全额退款		
//		201505061027984369	退款	上海电银信息技术有限公司	TT150501070025727732	201505010000	人民币	50.00	0.00	18260188000002780	上海电银信息技术有限公司	6228760405000913691	屠翔翔	20150507	已对账	正常	--	未结算	全额退款		
//		201505061027941240	支付	上海电银信息技术有限公司	PH150506013944907877	201505060140	人民币	100.00	0.00	6228760605000442316	梁涛	99010159540005003	上海电银支付清算过渡户	20150506	已对账	正常	20150507	已结算	已支付		
		
		
		String[] datas = null;
		if(fileContent.indexOf("\n") != -1)
			datas = fileContent.split("\n");
		else if(fileContent.indexOf("\r") != -1)
			datas = fileContent.split("\r");
		
		int i = 0;
		for (String line : datas) {// 数据从第三行开始
			
			if(Ryt.empty(line))
				continue;
			
			if (i >= 2) {
				// 过滤退款数据
				if(line.indexOf("--") != -1)
					continue;
				
				String[] dataArr = line.trim().split("\\s{1,}");
				SBean bean = new SBean();
				bean.setGate(bank);
				bean.setMerOid(dataArr[3].trim());
				bean.setAmt(dataArr[6].replaceAll(",", "").trim());
				bean.setBkFee(dataArr[7].replaceAll(",", "").trim());//订单手续费
				bean.setDate(dataArr[4].substring(0,8));
				sbeanList.add(bean);
			}

			i++;
		}
		return sbeanList;

	}

	@Override
	public List<SBean> getCheckData(String bank, Map<String, String> m)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
