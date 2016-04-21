package mmsTest.modules.sysmanage;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.rongyifu.mms.bean.BankNoInfo;
import com.rongyifu.mms.modules.sysmanage.service.BankInfoService;
import com.rongyifu.mms.utils.CurrentPage;

public class Test_BankInfoService {
	private BankInfoService mcs = new BankInfoService();
	private final Logger logger = Logger.getLogger(getClass());

	@Test
	public void testQueryById(){
		BankNoInfo bni = mcs.queryById(1);
	}
	
	@Test
	public void testAdd(){
		BankNoInfo bni = new BankNoInfo();
		bni.setBkName("bkName");
		bni.setBkNo("bkNo");
		bni.setType("Type");
		bni.setGid("gid");
		int count = mcs.add(bni);
	}
	
	@Test
	public void delById(){
		int count = mcs.delById(-1);
	}
	
	@Test
	public void update(){
		BankNoInfo bni = new BankNoInfo();
		bni.setBkName("bkName1");
		bni.setBkNo("bkNo1");
		bni.setGid("gid1");
		bni.setId(-1);
		mcs.update(bni);
	}

	@Test
	public void queryForPage(){
		BankNoInfo bni = new BankNoInfo();
		bni.setBkName("bkName1");
		bni.setBkNo("bkNo1");
		bni.setGid("gid1");
		CurrentPage<BankNoInfo> page = mcs.queryForPage(bni, 1);
	}
}
