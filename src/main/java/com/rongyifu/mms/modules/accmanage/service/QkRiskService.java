package com.rongyifu.mms.modules.accmanage.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.io.FileTransfer;

import com.rongyifu.mms.bean.QkCardInfo;
import com.rongyifu.mms.common.AppParam;
import com.rongyifu.mms.modules.accmanage.dao.QkRiskDao;
import com.rongyifu.mms.service.DownloadFile;
import com.rongyifu.mms.utils.CurrentPage;
import com.rongyifu.mms.utils.DateUtil;
import com.rongyifu.mms.utils.RYFMapUtil;

//快捷用户信息service
public class QkRiskService {

	private QkRiskDao qk = new QkRiskDao();
	
	/**
	 * 根据条件快捷支付用户信息
	 * @return CurrentPage
	 */
	@RemoteMethod
	public CurrentPage<QkCardInfo> queryQkCardInfos(Integer pageNo, String mid, String phoneNo, String cardNo,String bdate,String edate,String abbrev,String type) {

		return qk.queryQkCardInfo(pageNo,new AppParam().getPageSize(), mid, phoneNo, cardNo, bdate, edate,abbrev,type);
	}
	
	
	// 快捷支付用户信息下载
		public FileTransfer downloadQk(String mid, String phoneNo, String cardNo, String bdate,
				String edate,String abbrev,String type)throws Exception {
			
			CurrentPage<QkCardInfo> tlogListPage = qk.queryQkCardInfo(1, -1, mid, phoneNo, cardNo, bdate, edate,abbrev,type);
			List<QkCardInfo> tlogList=tlogListPage.getPageItems();
			ArrayList<String[]> list = new ArrayList<String[]>();
			Map<Integer, String> gates = RYFMapUtil.getGateMap();
			list.add("序号,商户号,用户平台ID,姓名,身份证号,开户银行,银行卡号,手机号,开通快捷日期,数据来源".split(","));
			int i = 0;
			for (QkCardInfo h : tlogList) {
				String[] str = { (i + 1) + "", h.getMid() + "", h.getUserId() + "", h.getCardName(), h.getPidNo() + "",
						gates.get(h.getGateId()), h.getCardNo() + "",
						h.getPhoneNo(), h.getRegiTimeStr(), h.getAbbrev()};
				i += 1;
				list.add(str);
			}
			String[] str = { "总计:" + i + "条记录"};
			list.add(str);
			String filename = "QKRISK_" + DateUtil.today() + ".xlsx";
			String name = "快捷用户信息表";
			return new DownloadFile().downloadXLSXFileBase(list, filename, name);
		}
//		
		/**
		 * 快捷支付用户信息详情查询
		 * @param authCode快捷授权码
		 * @return QkCardInfo
		 */
		public QkCardInfo queryQKByCode(String authCode) {

			return qk.queryQKByTseq(authCode);
		}
	
	
}
