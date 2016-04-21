package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.psbc.payment.client.SignatureService;
import com.rongyifu.mms.bean.GateRoute;
import com.rongyifu.mms.common.RypCommon;
import com.rongyifu.mms.dao.BankQueryDao;
import com.rongyifu.mms.utils.LogUtil;

/**
 * 邮政银行-vas
 * 
 * @author m
 * 
 */
public class CheckDataPSBCImp implements SettltData {
	BankQueryDao bkquery = new BankQueryDao();

	// 交易码|清算日期|商户交易时间|订单号|交易流水号|商户号|终端号|交易金额|手续费金额|结算金额|交易相应码|备注1|备注2|
	@Override
	public List<SBean> getCheckData(String bank, String fileContent)
			throws Exception {
		// TotalCount=10
		// IPER|20120612|20101020000000|64564341|10000034|9999998881000000500||1,00.00|0.36|1.44|00000000|||
		// IPER|20120612|20101020000000|20110701092129|10000040|9999998881000000500||0.01|0.36|1.44|00000000|||
		List<SBean> sbeanList = new ArrayList<SBean>();
		String[] datas = fileContent.split("\n");

		for (int i = 0; i < datas.length; i++) {// 数据从第二行开始
			String[] dataArr = datas[i].split("\\|");
			if (dataArr.length == 14) {
				SBean bean = new SBean();
				bean.setGate(bank);
				bean.setBkFee(dataArr[8].replaceAll(",", ""));// 手续费金额
				bean.setMerOid(dataArr[3]);
				bean.setAmt(dataArr[7].replaceAll(",", ""));
				bean.setDate(dataArr[2].substring(0, 8));
				sbeanList.add(bean);
			}
		}
		return sbeanList;
	}

	@Override
	public List<SBean> getCheckData(String bank, Map<String, String> m)
			throws Exception {
		String date = m.get("date");
		String merNo = m.get("merNo");
		String fileType = m.get("fileType");
		StringBuffer palin = new StringBuffer();
		palin.append("MercCode=");
		palin.append(merNo);
		palin.append("|OSttDate=");
		palin.append(date);
		palin.append("|SetFType=");
		palin.append(fileType);
		String Signature = sign(palin.toString());
		StringBuffer requestUrl = new StringBuffer();
		GateRoute gr = bkquery.queryBankInfoByGid(Integer.parseInt(bank));
		String bkRequestUrl = gr.getRequestUrl();
		requestUrl.append(bkRequestUrl);
		requestUrl.append("?transName=IDFR&Plain=");
		requestUrl.append(palin);
		requestUrl.append("&Signature=");
		requestUrl.append(Signature);
		String resultStr = "";
		try {
			resultStr = new String(RypCommon.httpRequest(requestUrl.toString())
					.getBytes("GBK"), "UTF-8");
		} catch (Exception e) {
			LogUtil.printErrorLog("CheckDataPSBCImp", "getCheckData",
					e.getMessage());
		}
		if (!resultStr.equals("")) {
			return packageData(bank, resultStr);
		}
		return null;
	}

	public static String sign(String palin) {
		return SignatureService.sign(palin);
	}

	private List<SBean> packageData(String bank, String fileData) {
		List<SBean> sbeanList = new ArrayList<SBean>();
		if (fileData.indexOf("ErrorCode") >= 0
				|| fileData.indexOf("ErrorMsg") >= 0)
			return sbeanList;
		String[] datas = fileData.split("\n");
		for (int i = 1; i < datas.length; i++) {
			String[] dataArr = datas[i].split("\\|");
			if (dataArr.length == 14) {
				SBean bean = new SBean();
				bean.setGate(bank);
				bean.setTseq(dataArr[3]);
				bean.setAmt(dataArr[7].replaceAll(",", ""));
				bean.setDate(dataArr[2].substring(0, 8));
				sbeanList.add(bean);
			}
		}
		return sbeanList;
	}
}
