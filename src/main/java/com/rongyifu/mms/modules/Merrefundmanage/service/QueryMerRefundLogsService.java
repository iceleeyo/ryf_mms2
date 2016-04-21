package com.rongyifu.mms.modules.Merrefundmanage.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.directwebremoting.io.FileTransfer;

import com.rongyifu.mms.bean.RefundLog;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.ParamCache;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.modules.Merrefundmanage.dao.QueryMerRefundLogsDao;
import com.rongyifu.mms.service.DownloadFile;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.RYFMapUtil;

public class QueryMerRefundLogsService {
	private QueryMerRefundLogsDao queryrefundlog = new QueryMerRefundLogsDao();

	/**
	 * 退款查询
	 * 
	 * @return CurrentPage<RefundLog>
	 */
	public CurrentPage<RefundLog> queryRefundLogs(Integer pageNo, String mid,
			String stat, String tseq, Integer dateState, Integer bdate,
			Integer edate, Integer gate, String orgid, Integer vstate,
			Integer authorType, String begintrantAmt, String endtrantAmt) {
		int pageSize = ParamCache.getIntParamByName("pageSize");
		return queryrefundlog.queryRefundLogs(pageNo, pageSize, mid, stat,
				tseq, dateState, bdate, edate, gate, orgid, vstate, authorType,
				null, null, begintrantAmt, endtrantAmt);
	}

	/**
	 * 获得下载的List<RefundLog>
	 * 
	 * @return
	 */
	private List<RefundLog> getRefundList(String mid, String stat, String tseq,
			Integer dateState, Integer bdate, Integer edate, Integer gate,
			String orgid, Integer vstate, Integer gateRouteId,
			String begintrantAmt, String endtrantAmt) {
		CurrentPage<RefundLog> refundLogPage = queryrefundlog.queryRefundLogs(
				1, -1, mid, stat, tseq, dateState, bdate, edate, gate, orgid,
				vstate, gateRouteId, null, null, begintrantAmt, endtrantAmt);
		return refundLogPage.getPageItems();
	}

	/**
	 * 退款报表下载（查询）
	 * 
	 * @return
	 * @throws Exception
	 */
	public FileTransfer downloadReturn(String mid, String stat, String tseq,
			Integer dateState, Integer bdate, Integer edate, Integer gate,
			String orgid, Integer vstate, Integer authorType,
			String begintrantAmt, String endtrantAmt) throws Exception {
		String sessionMid = queryrefundlog.getLoginUser().getMid();

		List<RefundLog> resultlist = getRefundList(mid, stat, tseq, dateState,
				bdate, edate, gate, orgid, vstate, authorType, begintrantAmt,
				endtrantAmt);
		Map<Integer, String> gates = RYFMapUtil.getGateMap();
		Map<Integer, String> mstat = AppParam.h_mer_refund_tstat;
		String[] title = { "序号", "商户号", "退款流水号", "原电银流水号", "原商户订单号", "原银行流水号",
				"原交易日期", "原交易金额（元）", "原交易银行", "退款金额（元）", "退还商户手续费(元)", "经办日期",
				"退款确认日期", "退款状态 ", !"1".equals(sessionMid) ? "" : "原订单优惠金额(元)" };
		ArrayList<String[]> list = new ArrayList<String[]>();
		list.add(title);
		long refAmount = 0;
		long refMerFee = 0;
		for (int j = 0; j < resultlist.size(); j++) {
			RefundLog refundLog = resultlist.get(j);
			String[] strArr = {
					String.valueOf(j + 1),
					String.valueOf(refundLog.getMid()),
					String.valueOf(refundLog.getId()),
					String.valueOf(refundLog.getTseq()),
					refundLog.getOrg_oid(),
					refundLog.getOrgBkSeq(),
					String.valueOf(refundLog.getOrg_mdate()),
					Ryt.div100(refundLog.getOrgAmt()),
					gates.get(refundLog.getGate()),
					Ryt.div100(refundLog.getRef_amt()),
					Ryt.div100(refundLog.getMerFee()),
					changeToString(refundLog.getPro_date()),
					changeToString(refundLog.getReq_date()),
					mstat.get(refundLog.getStat()),
					!"1".equals(sessionMid) ? "" : Ryt.div100(refundLog
							.getPreAmt())

			};
			refAmount += refundLog.getRef_amt();
			refMerFee += refundLog.getMerFee();
			// preAmt+=refundLog.getPreAmt();
			list.add(strArr);
		}
		String[] nullstr = { "", "", "", "", "", "", "", "", "", "", "", "",
				"", "", "" };
		list.add(nullstr);
		String[] str = { "总计:" + resultlist.size() + "条记录", "", "", "", "", "",
				"", "", "", Ryt.div100(refAmount), Ryt.div100(refMerFee), "",
				"", "", "" };
		list.add(str);
		String filename = "MERREFUNDQUERY_" + DateUtil.getIntDateTime()
				+ ".xlsx";
		String name = "退款报表";
		return new DownloadFile().downloadXLSXFileBase(list, filename, name);

	}
	
	/**
	 * 根据Id查询RefundLog
	 * @param id
	 * @return RefundLog
	 */
	public RefundLog queryRefundLogById(Integer id) {
		return queryrefundlog.queryRefundLogById(id);
	}

	/**
	 * 把null和0转换成空格
	 * 
	 * @param obj
	 * @return
	 */
	private String changeToString(Object obj) {
		if (obj == null || obj.toString().equals("0")) {
			return "";
		} else {
			return obj.toString();
		}
	}

}
