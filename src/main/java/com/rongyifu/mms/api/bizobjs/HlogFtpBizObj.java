package com.rongyifu.mms.api.bizobjs;

import java.io.File;
import java.util.List;
import java.util.Map;


import org.apache.commons.lang.StringUtils;

import com.rongyifu.mms.api.BizObj;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.bean.MidFtp;
import com.rongyifu.mms.bean.RyfFtp;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.SettlementDao;
import com.rongyifu.mms.service.MerSettlementService;
import com.rongyifu.mms.utils.SftpUtil;
/**
 * 手动方式生成数据，并上传到sftp上
 * @author shdy
 *
 */
public class HlogFtpBizObj implements BizObj {

	@Override
	public Object doBiz(Map<String, String> params) throws Exception {
	
		String mid = params.get("mid");
		String date = params.get("date");
		if(StringUtils.isBlank(mid)){
			throw new Exception("商户号不能为空");
		}
		if(StringUtils.isBlank(date)){
			throw new Exception("日期不能为空");
		}
		MerSettlementService service= new MerSettlementService();
		SettlementDao dao=new SettlementDao();
		MidFtp midFtp = dao.getMidFtpByMid(mid);
		String url=midFtp.getFtpUrl();
		List<Hlog> hlogs = dao.madeSettlement(mid, Integer.parseInt(date));
		File file = service.createSettlementFile(hlogs, mid,Integer.parseInt(date));
		//获取登录ftp的信息
		RyfFtp ryfFtp = dao.getRyfFtpById("RYF_DZ");
		SftpUtil util = null;
		try {
				util = new SftpUtil(ryfFtp.getFtpIp(), ryfFtp.getFtpName(), null, ryfFtp.getFtpPort(), ryfFtp.getFtpPrivateKey(),null);
				util.connect();
				util.cd(url);//商户上传到的目录
				String path = file.getPath();
				String name = file.getName();
				util.uploadFile(name, path);
				if(file.exists())
					file.delete();
				} finally {
					if (util != null) {
						util.close();
					}
					if(file.exists())
						file.delete();
				}
		
		return "成功";
	}

}
