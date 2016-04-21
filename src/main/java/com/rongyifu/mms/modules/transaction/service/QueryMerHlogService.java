package com.rongyifu.mms.modules.transaction.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.directwebremoting.io.FileTransfer;

import com.rongyifu.mms.bean.OrderInfo;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.common.Constant;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.modules.transaction.dao.QueryMerHlogDao;
import com.rongyifu.mms.service.DownloadFile;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.RYFMapUtil;

public class QueryMerHlogService {
	
	private QueryMerHlogDao querymerhlogdao=new QueryMerHlogDao();
	/**
 	 * 根据条件查询交易明细
 	 * @return CurrentPage
 	 */
 	
	public CurrentPage<OrderInfo> queryHlogDetail(Integer pageNo, Map<String, String> param) {

	return querymerhlogdao.queryHlogDetail(pageNo,new AppParam().getPageSize(), param);


}
	
	//历史明细查询下载
		public FileTransfer downloadDetail(Map<String, String> param) throws Exception {
		  CurrentPage<OrderInfo> hlogListPage=querymerhlogdao.queryHlogDetail(1,-1, param );
		  	List<OrderInfo> hlogList=hlogListPage.getPageItems();
			ArrayList<String[]> list = new ArrayList<String[]>();
			Map<Integer, String> gates = RYFMapUtil.getGateMap();
			Map<Integer,String> gateRouteMap=RYFMapUtil.getGateRouteMap();
			//Map<Integer, String> authorType = AppParam.authorType;
			long totleBankFee = 0;
			String totleAmount=Ryt.div100(hlogListPage.getSumResult().get(AppParam.AMT_SUM).toString());
			String totleSysAmtFee=Ryt.div100(hlogListPage.getSumResult().get(AppParam.SYS_AMT_FEE_SUM).toString());
			list.add("序号,电银流水号,商户号,商户简称,商户订单号,商户日期,交易金额(元),实际交易金额(元),交易状态,对账状态,对账日期,交易类型,交易银行,支付渠道,系统手续费(元),银行手续费(元),系统时间,银行流水号,失败原因,接入方式"
				.split(","));
			int i = 0;
			String gateRoute = "";
			long totlePayAmt = 0;
			for (OrderInfo h : hlogList) {
				System.out.println(h.getP15());
				if (h.getGid() != null&& !String.valueOf(h.getGid()).equals("")) {
					gateRoute = gateRouteMap.get(h.getGid());
				}else{
					gateRoute = "";
				}
				String[] str = {
						(i + 1) + "",
						h.getTseq() + "",
						h.getMid() + "",
						h.getMid() == null ? "" : RYFMapUtil.getInstance().getMerMap().get(h.getMid()),
						h.getOid(),
						h.getMdate() + "",
						Ryt.div100(h.getAmount()),
						Ryt.div100(h.getPayAmt()),
						AppParam.tlog_tstat.get(Integer.parseInt(h.getTstat() + "")),
						h.getBkChk() == null ? "" : (h.getBkChk() < 0 || h.getBkChk() > 3 ? "" : AppParam.check_state
							.get(Integer.parseInt(h.getBkChk() + ""))),
							h.getP10() + "",
						AppParam.tlog_type.get(Integer.parseInt(h.getType() + "")), gates.get(h.getGate()), gateRoute,
						Ryt.div100(h.getFeeAmt()), Ryt.div100(h.getBankFee()),
						h.getSysDate() + " " + DateUtil.getStringTime(h.getSysTime()), h.getBk_seq1(), h.getError_msg(),h.getP15()};
				totleBankFee += h.getBankFee();
				totlePayAmt += h.getPayAmt();
				i += 1;
				list.add(str);
			}
			String[] str = { "总计:" + i + "条记录", "", "", "", "","" ,totleAmount, Ryt.div100(totlePayAmt),"", "", "", "", "","",
					        totleSysAmtFee, Ryt.div100(totleBankFee) + "", "", "", "" , ""};
			list.add(str);
			String filename = "MERHLOG_" + DateUtil.today() + ".xlsx";
			String name = "交易明细表";
		   return new DownloadFile().downloadXLSXFileBase(list, filename, name);
		}
		
		/**
		 * 当天交易详情查询
		 * @param tseq融易通流水号
		 * @param tableType 1为tlog 2为hlog 3为blog；
		 * @return hlog
		 */
		public OrderInfo queryLogByTseq(String tseq,int tableType,String isBackupTable) {
			String tableName="";
			System.out.println(isBackupTable);
			if(tableType==1) tableName=Constant.TLOG;
			else if(tableType==2&&isBackupTable.equals("0")) tableName=Constant.HLOG;
			else if(tableType==2&&isBackupTable.equals("1")){
				tableName= "hlog_201503";
			}
			else tableName=Constant.BLOG;

			return querymerhlogdao.queryHlogByTseq(tableName, tseq,null);
		}

}
