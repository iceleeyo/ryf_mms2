package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cmb.netpayment.Settle;

import com.rongyifu.mms.exception.CMBException;

/**
 * 招商银行：适用于B2C、WAP
 *
 */
public class CheckDataCMBImp2 implements SettltData {
	
	/**
	 * txt文件对账
	 */
	@Override
	public List<SBean> getCheckData(String bank,String fileContent) throws Exception {
		List<SBean> res = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");
//		20120807,20120807,0.10,0000008250,0
//		20120807,20120807,0.16,0000008117,0
//		20120807,20120807,0.10,0000008094,0
		int lineCount = 0;
		for (String line : datas) {
			String[] val = line.split(",");
			if (val != null && val.length != 5 && lineCount < datas.length) {
				return res;
			}
			SBean bean = new SBean();
			bean.setGate(bank);
			bean.setTseq(Long.parseLong(val[3]) + "");
			bean.setDate(val[1]);
			bean.setAmt(val[2]);
			res.add(bean);
			lineCount = lineCount + 1;
		}

		return res;
	}

	/**
	 * @param beginDate 查询起始日期
	 * @param endDate 查询结束日期
	 * @param operId 操作员号
	 * @param password 登陆CMB后台的密码
	 */
	@Override
	public List<SBean> getCheckData(String bank,Map<String, String> m) throws Exception {
//		String beginDate = m.get("beginDate");
//		String endDate = m.get("endDate");
//		String operId = m.get("operId");
//		String password = m.get("pwd");

		ArrayList<SBean> succDataList = new ArrayList<SBean>();
		Settle settle = new Settle();
		int iRet = settle.SetOptions("payment.ebank.cmbchina.com");
		if (iRet == 0) {// SetOptions ok
			iRet = settle.LoginC(m.get("bkNo"),m.get("merchantNo") + m.get("operId"), m.get("pwd"));
			if (iRet == 0) {// LoginC ok;
				StringBuffer strbuf = new StringBuffer();// 接收查询出的数据
				iRet = settle.QuerySettledOrder(m.get("beginDate"), m.get("endDate"), strbuf);
				if (iRet == 0) {// 查询成功
					if (strbuf.length() != 0) {// 查询的记录不为空再进行组装
						packageData(bank,strbuf, succDataList);
					}
				} else {
					throw new CMBException(settle.GetLastErr(iRet));
				}

			} else {
				throw new CMBException(settle.GetLastErr(iRet));
			}
		} else {
			throw new CMBException(settle.GetLastErr(iRet));
		}
		settle.Logout();

		return succDataList;
	}

	/**
	 * 按照RYT对账数据要求组装招行返回的数据 {融易通流水号,银行流水号,银行记录中的商户日期,交易金额,交易日期,交易时间,银行手续费}
	 * @author cody 2010-5-20
	 * @param stb CMB返回的数据
	 * @param aList 组装好的RYT对账数据
	 */
	private void packageData(String bank,StringBuffer stb, ArrayList<SBean> aList) {
		String[] srcData = stb.toString().split("\n");// CMB返回数据以\n分隔
		// CMB返回数据结构 交易日期\n处理日期\n金额\n订单号\n订单状态
		int rowCount = srcData.length / 5;
		for (int i = 0; i < rowCount; i++) {
			if (srcData[i * 5 + 4].equals("0")) {// 判断该条订单的状态 0 为已结算

				SBean obj = new SBean();
				obj.setGate(bank);
				// String[] dataArray = new String[6];
				// dataArray[0] = Integer.parseInt(srcData[i*5 + 3]) + ""; //CMB返回的订单号为6位不足会在前面补零
				// dataArray[1] = srcData[i*5 + 3];
				// dataArray[2] = srcData[i*5 + 1];
				// dataArray[3] = srcData[i*5 + 2];
				// dataArray[4] = srcData[i*5];
				// dataArray[5] = "--/--/--";

				obj.setAmt(srcData[i * 5 + 2]);
				// obj.setBkFee(null);
				// obj.setDate(srcData[i*5 + 1]);
				obj.setTseq(Integer.parseInt(srcData[i * 5 + 3]) + "");
				obj.setDate(srcData[i * 5]);

				aList.add(obj);
			}
		}
	}
}
