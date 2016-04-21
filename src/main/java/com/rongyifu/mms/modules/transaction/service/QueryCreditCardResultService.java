package com.rongyifu.mms.modules.transaction.service;

import java.util.ArrayList;
import java.util.List;

import org.directwebremoting.io.FileTransfer;

import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.modules.transaction.dao.QueryCreditCardResultDao;
import com.rongyifu.mms.service.DownloadFile;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.RYFMapUtil;

public class QueryCreditCardResultService {
	private QueryCreditCardResultDao queryCreditCardResultDao=new QueryCreditCardResultDao();
	/**
	 * 手机支付 查询（信用卡支付）
     * 新增查询cardVal加密
	 * @param pageNo
	 * @param mid
	 * @param bdate
	 * @param edate
	 * @param tstat 交易状态
	 * @param tseq 电银流水号
	 * @param cardType 卡/证件的类型
	 * @param cardVal 卡/证的值
	 * @return
	 */
	public CurrentPage<OrderInfo> queryCreditCardResult(int pageNo, String mid, int bdate, int edate, Integer tstat, String tseq,
					int cardType, String cardVal, long payAmount,Integer mstate,String begintrantAmt,String endtrantAmt) {

		return queryCreditCardResultDao.queryMlogList(pageNo,new AppParam().getPageSize(), mid, bdate, edate, tstat, tseq,
				cardType, cardVal, payAmount,mstate,begintrantAmt,endtrantAmt);
	}
	
	//手机支付（信用卡支付）下载
		public FileTransfer downloadCreditCardPay(String mid, int bdate, int edate, Integer tstat, String tseq,
				int cardType, String cardVal, long payAmount,Integer mstate,String begintrantAmt,String endtrantAmt) throws Exception {
			 CurrentPage<OrderInfo> mlogListPage =queryCreditCardResultDao.queryMlogList(1,-1, mid, bdate, edate, tstat, tseq, cardType, cardVal, 
					 payAmount,mstate,begintrantAmt,endtrantAmt);
			 List<OrderInfo> mlogList=mlogListPage.getPageItems();
			   ArrayList<String[]> list = new ArrayList<String[]>();
				String[] title = {"序号","电银流水号","系统日期","商户号","商户简称","交易金额","交易状态","卡号","手机号","身份证号"};
				list.add(title);
				long countAmount=0;
				for (int i = 0; i < mlogList.size(); i++) {
					OrderInfo mlog=mlogList.get(i);
					String[] strArr={
							String.valueOf(i+1),
							String.valueOf(mlog.getTseq()),
							String.valueOf(mlog.getSysDate()),
							String.valueOf(mlog.getMid()),
							RYFMapUtil.getInstance().getMerMap().get(mlog.getMid()),
							Ryt.div100(mlog.getPayAmount()),
							AppParam.tlog_tstat.get((int)mlog.getTstat()),
							mlog.getPayCard(),
							mlog.getMobileNo(),
							mlog.getPayId()
						};
					countAmount+=mlog.getPayAmount();
					list.add(strArr);
				}
				String[] str = { "总计:" +mlogList.size() + "条记录", "", "", "", "",Ryt.div100(countAmount),"","",""};
				list.add(str);
				String filename = "CreditCardPay_" + DateUtil.today() + ".xlsx";
				String name = "信用卡支付";
				return new DownloadFile().downloadXLSXFileBase(list, filename, name);
		}

}
