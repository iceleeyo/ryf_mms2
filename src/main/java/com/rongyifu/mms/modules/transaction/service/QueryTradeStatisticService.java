package com.rongyifu.mms.modules.transaction.service;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.io.FileTransfer;

import com.rongyifu.mms.bean.TradeStatistics;
import com.rongyifu.mms.modules.bgservice.SmsSendDailyBean;
import com.rongyifu.mms.modules.transaction.dao.TradeStatisticDao;
import com.rongyifu.mms.service.DownloadFile;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.RYFMapUtil;

public class QueryTradeStatisticService {
	private final Logger logger = Logger.getLogger(getClass());
	
	private TradeStatisticDao tsDao = new TradeStatisticDao();
	
	private static Map<Short,String> transModes = new HashMap<Short,String>();
	static{
		transModes.put((short)1, "个人网银支付");
		transModes.put((short)3, "信用卡支付");
		transModes.put((short)5, "增值业务");
		transModes.put((short)6, "语音支付");
		transModes.put((short)7, "企业网银支付");
		transModes.put((short)8, "手机WAP支付");
		transModes.put((short)10, "POS支付");
		transModes.put((short)18, "快捷支付");
	}
	
	/**
	 * 交易总表
	 */
	public static final int JYZB = 0;
	
	/**
	 * 交易分表
	 */
	public static final int JYFB = 1;
	/**
	* @title: doStatistics
	* @description: 根据日期统计订单信息
	* @author li.zhenxing
	* @date 2014-12-25
	* @param date 订单系统日期
	* @param isTlog 是否查询tlog
	*/ 
	public boolean doStatistics(Integer date,Boolean isTlog){
		if(!DateUtil.validate(date+"") || null == isTlog){
			logger.error("参数错误");
			return false;
		}
		//1.先清空trans_statistics表中日期为date的数据
		tsDao.deleteStatisticsByDate(date);
		//统计并插入数据
		tsDao.doStatistics(date,isTlog);
		return true;
	}
	/**
	* @title: exportTransExcel 交易报表统计
	* @description: 
	* 生成交易数据总表和分表，总表为：各个交易类型的交易情况以及成功占比；
	* 分表为：该交易类型的总交易情况，交易金额以及成功占比，包含该交易类型下所有银行，不论是否有订单都要在报表中包含；
	* @author li.zhenxing
	* @date 2014-12-23
	* @param bdate 
	* @param edate
	* @param type 0总表 1 分表
	*/ 
	public FileTransfer exportTransExcel(Integer bdate,Integer edate,Integer type){
		if(!DateUtil.validate(bdate+"")||!DateUtil.validate(edate+"")||(type != JYZB && type != JYFB)){
			logger.error("参数错误");
			return null;
		}
		//要建一个统计信息表 通过定时任务 统计交易信息 按交易类型 交易日期进行统计 然后存入统计信息表中
		try {
			String fileName = "TRADE_STATISTICS_";
			List<DownloadFile.SheetInfo> data = new ArrayList<DownloadFile.SheetInfo>();//下载xls文件的所有数据
			List<Short[]> mergInfo = new ArrayList<Short[]>();
			CurrentPage<TradeStatistics> page = tsDao.queryForPage(bdate, edate, type, -1);
			List<TradeStatistics> pageItems = page.getPageItems();
			if(CollectionUtils.isNotEmpty(pageItems)){
				Map<Integer,String> map = RYFMapUtil.getGateMap();
				if(JYFB == type){//分表
					fileName += "JYFB_";
					//合并单元格
					//int firstRow, int lastRow, int firstCol, int lastCo
					mergInfo.add(new Short[] { (short) 1, (short) 2, (short) 1, (short) 1 });
					mergInfo.add(new Short[] { (short) 1, (short) 2, (short) 2, (short) 2 });
					mergInfo.add(new Short[] { (short) 1, (short) 1, (short) 3, (short) 5 });
					List<Object[]> sheetData = new ArrayList<Object[]>();
					//填充数据
					short transMode = (short)-1;
					int count = 1; //序号
					DownloadFile.SheetInfo sheetInfo = null;
					for (TradeStatistics ts : pageItems) {
						if(!ts.getTransMode().equals(transMode)){//一种transMode的统计数据处理完成时设置sheet信息
							if ((short)-1 != transMode) {//当transMode等于初始值-1是不需要设置sheet信息
								sheetInfo = new DownloadFile.SheetInfo();
								sheetInfo.setSheetName(transModes.get(transMode));//sheet名称
								sheetInfo.setTitle(transModes.get(transMode) + "各银行统计报表");//标题
								sheetInfo.setData(sheetData);//把sheet数据设置到sheetInfo对象
								sheetData = new ArrayList<Object[]>();//下一个transMode的sheetData
								sheetInfo.setMergeInfo(mergInfo);//设置表头合并单元格的信息
								data.add(sheetInfo);//把sheet添加加到workbook
								count = 1;//重置count
							}
							transMode = ts.getTransMode();//保存当前记录的transmode
							//填充表头
							sheetData.add(new String[] { "序号", "银行", DateUtil.formatDate(bdate) +" 至 " + DateUtil.formatDate(edate), "", ""});
							sheetData.add(",,总笔数,成功笔数,成功金额,成功率".split(","));
						}
						//填充数据
						sheetData.add(new Object[]{count,map.get(ts.getGate()),ts.getTotalCount(),ts.getSuccessCount(),centToYuan(ts.getSuccessAmt()),toPercent(ts.getTotalCount()==0?0.0:(ts.getSuccessCount()+0.0)/ts.getTotalCount())});
						count++;
					}
					//跳出循环后 将最后一个sheetInfo添加到excel
					sheetInfo = new DownloadFile.SheetInfo();
					sheetInfo.setSheetName(transModes.get(transMode));//sheet名称
					sheetInfo.setTitle(transModes.get(transMode) + "各银行统计报表");//标题
					sheetInfo.setData(sheetData);//把sheet数据设置到sheetInfo对象
					sheetInfo.setMergeInfo(mergInfo);//设置表头合并单
					data.add(sheetInfo);
				}else{//总表
					fileName += "JYZB_";
					List<Object[]> sheetData = new ArrayList<Object[]>();
					sheetData.add("交易类型,总笔数,成功笔数,成功金额,成功率".split(","));
					for (TradeStatistics ts : pageItems) {
						sheetData.add(new Object[]{transModes.get(ts.getTransMode()),ts.getTotalCount(),ts.getSuccessCount(),centToYuan(ts.getSuccessAmt()),toPercent(ts.getTotalCount()==0?0.0:(ts.getSuccessCount()+0.0)/ts.getTotalCount())});
					}
					DownloadFile.SheetInfo sheetInfo = new DownloadFile.SheetInfo();
					sheetInfo.setData(sheetData);
					sheetInfo.setTitle("交易统计报表");
					sheetInfo.setSheetName("交易统计报表");
					data.add(sheetInfo);
				}
				fileName += DateUtil.format(new Date(), "yyyyMMddHHmmss")+".xlsx";
			}
			return new DownloadFile().downloadXLSXFilePro(data, fileName);
		} catch (Exception e) {
			logger.error("生成交易报表失败, "+e.getMessage(), e);
			return null;
		}
	}//CurrentPage<OrderInfo>
	
	private String toPercent(Double d){
		NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);
		return nf.format(d);
	}
	
	private String centToYuan(Long cent){
		NumberFormat nf = NumberFormat.getInstance();
		nf.setGroupingUsed(false);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);
		return nf.format(cent/100.0);
	}
	/**
	* @title: queryTrade
	* @description: 分页查询
	* @author li.zhenxing
	* @date 2014-12-25
	* @param bdate
	* @param edate
	* @param type 0总表 1 分表
	* @param pageNo
	* @return
	*/ 
	public CurrentPage<TradeStatistics> queryTradeStatistics(Integer bdate,Integer edate,Integer type,Integer pageNo){
		if(!DateUtil.validate(bdate+"") || !DateUtil.validate(edate+"") || type == null || (type != JYZB && type != JYFB) ||pageNo == null || pageNo <= 0){
			return new CurrentPage<TradeStatistics>();
		}
		return tsDao.queryForPage(bdate, edate, type, pageNo);
	}

//	public List<SmsSendDailyBean> querytrade(Integer beginDate, Integer endDate) {
//		List<SmsSendDailyBean> dataList = initBeanList();
//		List<Map<String, Object>> data = new QueryTradeStatisticDao().transactionStatistics(beginDate, endDate);
//		for(Map<String, Object> item : data){
//			Integer transType = Integer.parseInt(String.valueOf(item.get("type")));
//			Integer allCount = Integer.parseInt(String.valueOf(item.get("all_count")));
//			Integer succCount = Integer.parseInt(String.valueOf(item.get("succ_count")));
//			String succAmount = String.valueOf(item.get("succ_amount"));
//			Double successRate = Double.parseDouble(String.valueOf(succCount)) / allCount.intValue();
//			
//			for(SmsSendDailyBean bean : dataList){
//				if(transType.equals(bean.getType())){
//					bean.setCount(allCount);
//					bean.setSuccessConut(succCount);
//					bean.setSuccessRate(successRate);
//					bean.setSumSuccessAmount(Ryt.div100(succAmount));
//				}
//			}
//		}
//		
//		return dataList;
//	}
	
//	private List<SmsSendDailyBean> initBeanList(){
//		List<SmsSendDailyBean> dataList = new ArrayList<SmsSendDailyBean>();
//		Integer type[] = new Integer[]{1, 3, 6, 7, 8, 18};
//		for(Integer item : type){
//			SmsSendDailyBean bean = new SmsSendDailyBean();
//			bean.setType(item);
//			bean.setCount(0);
//			bean.setSuccessConut(0);
//			bean.setSumSuccessAmount("0.00");
//			bean.setSuccessRate(0.00);
//			
//			dataList.add(bean);
//		}
//		return dataList;
//	}	
}
