package mmsTest.merchant;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.rongyifu.mms.merchant.MenuService;

public class Test_MenuService {
		
	/**
	* @title: testAuthConflict
	* @description: 测试是否有重复的菜单项
	* @author li.zhenxing
	* @date 2015-5-5
	*/ 
	@Test
	public void testAuthConflict(){
		assertFalse(MenuService.isAuthIdConflict());
	}
}
