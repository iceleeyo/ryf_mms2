package com.rongyifu.mms.modules.liqmanage.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.directwebremoting.io.FileTransfer;

import com.rongyifu.mms.bean.TransferMoney;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.modules.liqmanage.dao.QuerytransferDao;
import com.rongyifu.mms.service.DownloadFile;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.RYFMapUtil;

public class QuerytransferService {
	private QuerytransferDao dao = new QuerytransferDao();

	/*
	 * 划款结果查询
	 */

	public CurrentPage<TransferMoney> querytransfer(Integer pageNo, String mid,
			Integer bdate, Integer edate, String batchNo, Integer tstat,Integer type,Integer gateRouteId) {
		return dao.querytransfer(pageNo, new AppParam().getPageSize(), mid,bdate, edate, batchNo, tstat,type,gateRouteId);

	}

	/*
	 * 划款结果下载
	 */

	public FileTransfer downloadtransfer(String mid, Integer bdate,
			Integer edate, String batchNo, Integer tstat,Integer type,Integer gateRouteId) {
		CurrentPage<TransferMoney> TransferMoneyListPage = dao.querytransfer(1,-1, mid, bdate, edate, batchNo, tstat,type,gateRouteId);
		List<TransferMoney> TransferMoneyList = TransferMoneyListPage.getPageItems();
		ArrayList<String[]> list = new ArrayList<String[]>();
		Map<Integer, String> gates = RYFMapUtil.getGateMap();
		Map<Integer, String> gateRouteMap = RYFMapUtil.getGateRouteMap();
		String totleAmount = Ryt.div100(TransferMoneyListPage.getSumResult().get(AppParam.AMT_SUM).toString());
		String totleSysAmtFee = Ryt.div100(TransferMoneyListPage.getSumResult().get(AppParam.SYS_AMT_FEE_SUM).toString());
		list.add("序号,电银流水号,联行行号,商户号,结算批次号,结算确认日期,商户名称,开户银行名称,开户银行支行名,开户账户名称,开户账户号,划款状态,划款金额,划款手续费,代付类型,交易银行,支付渠道,失败原因".split(","));

		int i = 0;
		String gateRoute = "";
		for (TransferMoney h : TransferMoneyList) {
			if (h.getGid() != null && !String.valueOf(h.getGid()).equals("")) {
				gateRoute = gateRouteMap.get(h.getGid());
			} else {
				gateRoute = "";
			}
			String[] str = {
					(i + 1) + "",
					h.getTseq(),
					h.getBankNo().toString(),
					h.getMid(),
					h.getBatch(),
					h.getLiqDate() + "",
					h.getName(),
					h.getBankName(),
					h.getBankBranch(),
					h.getBankAcctName(),
					h.getBankAcct(),
					AppParam.tlog_tstat.get(Integer.parseInt(h.getTstat() + "")),
					Ryt.div100(h.getAmount()+""),
					Ryt.div100(h.getFeeAmt()+""),
					h.getType()==11?"对私代付":"对公代付",
					gates.get(h.getGate()),
					gateRoute,
					h.getErrorMsg() == null ? h.getErrorCode() : h.getErrorMsg() };
			i += 1;
			list.add(str);
		}
		String[] str = { "总计:" + i + "条记录", "", "", "", "", "", "", "", "", "","","", totleAmount, totleSysAmtFee,"", "", "","" };
		list.add(str);
		String filename = "MERHLOG_" + DateUtil.today() + ".xlsx";
		String name = "划款结果查询";
		try {
			return new DownloadFile().downloadXLSXFileBase(list, filename, name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
