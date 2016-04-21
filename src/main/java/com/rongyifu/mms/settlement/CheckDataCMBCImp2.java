package com.rongyifu.mms.settlement;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.utils.MD5;

/**
 * 民生银行对账文件数据获取（wap）
 * @author zhang.chaochao
 *
 */
public class CheckDataCMBCImp2 implements SettltData{

	/**
	 * 对账文件内容获取（B2C对账）
	 * @author 张超超
	 * @date 2014-5-21
	 * @modify
	 * @param bank 银行
	 * @param fileContent 文件内容
	 * @return List<SBean>
	 * @throws Exception
	 */

	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
//		中国民生银行网上支付商户对帐文件 
//		商户名称,上海电银信息技术有限公司,对帐日期,20140827 
//		消费笔数,35,消费金额,2473.12 
//		撤销笔数,0,撤销金额,0.00 
//		退单笔数,0,退单金额,0.00 
//		累计笔数,35,汇总金额,2473.12 
//		交易明细 
//		订单号,交易金额,交易日期,交易时间,交易类型,
//		'TC140827230516988567,225.00,20140827,230838,消费,
//		'TC140827225702539672,215.00,20140827,220811,消费,
//		'MV140827201146429140,98.00,20140827,200832,消费,
//		'MV140827183011966937,0.01,20140827,180846,消费,
//		'MV140827183155101810,0.01,20140827,180829,消费,
//		'TC140827184009307647,315.00,20140827,180818,消费,
//		'MV140827184610594879,80.00,20140827,180817,消费,
//		'MV140827175403970701,96.00,20140827,170858,消费,
//		'MV140827172447709988,18.00,20140827,170853,消费,
//		'MV140827173807341110,0.01,20140827,170846,消费,
//		'MV140827170700906331,20.00,20140827,170843,消费,
//		'MV140827171655888491,0.01,20140827,170842,消费,
//		'MV140827170342831281,20.00,20140827,170840,消费,
//		'MV140827174521457184,0.01,20140827,170822,消费,
//		'MV140827174815864207,0.01,20140827,170817,消费,
//		'MV140827171253442004,0.01,20140827,170816,消费,
//		'MV140827173623790742,0.01,20140827,170805,消费,
//		'MV140827161747531496,22.00,20140827,160850,消费,
//		'MV140827163849130214,60.00,20140827,160831,消费,
//		'MV140827165321695644,29.00,20140827,160827,消费,
//		'MV140827152430933865,72.00,20140827,150814,消费,
//		'MV140827140423992378,20.00,20140827,140855,消费,
//		'MV140827141024513909,10.00,20140827,140820,消费,
//		'MV140827132155902015,0.01,20140827,130829,消费,
//		'MV140827132030423857,0.01,20140827,130827,消费,
//		'MV140827130221453356,48.00,20140827,130814,消费,
//		'MV140827125731746355,192.00,20140827,120858,消费,
//		'TC140827122614990769,225.00,20140827,120851,消费,
//		'MV140827122859228773,0.01,20140827,120847,消费,
//		'TC140827124528694559,175.00,20140827,120842,消费,
//		'MV140827120519482146,0.01,20140827,120825,消费,
//		'MV140827113618642903,114.00,20140827,110822,消费,
//		'MV140827100744520303,84.00,20140827,100835,消费,
//		'TC140827100251308355,315.00,20140827,100810,消费,
//		'MV140828010531263717,20.00,20140828,010845,消费,
//		注：系统支持两年以内订单流水查询。



		List<SBean> res = new ArrayList<SBean>();
		String filedata=new String(fileContent.getBytes(),"gbk");
		filedata = filedata.replaceAll(" ", "");
//		System.out.println("filedate=" + filedata);
		String[] datas = filedata.split("\n");
		int lineCount = 0;
		for (String line : datas) {
			lineCount++;
			if(lineCount < 9 || Ryt.empty(line)){
				continue;
			}
			
			String[] value = line.split("\\,");
			if (value.length == 5) {
				// 过滤收入金额为0的数据
				String amt = value[1];
				if (amtIsZero(amt)){
					continue;
				}
				SBean bean = new SBean();
				bean.setGate(bank);
				bean.setMerOid(value[0].trim().replace("'", ""));
				bean.setAmt(value[1].trim());
				bean.setDate(value[2].trim());
				res.add(bean);
			}
		}
		return res;
	}

	/**
	 * 判断金额是否为0
	 * 
	 * @param amt
	 * @return
	 */
	private boolean amtIsZero(String amt) {
		if (Ryt.empty(amt))
			return true;
		try {
			double transAmt = Double.parseDouble(amt.replaceAll(",", "").trim());
			if (transAmt == 0D)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public List<SBean> getCheckData(String bank, Map<String, String> m)
			throws Exception {
		return null;
	}
}
