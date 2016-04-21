package mmsTest.modules.transaction;

import org.apache.log4j.Logger;
import org.directwebremoting.io.FileTransfer;
import org.junit.Test;

import com.rongyifu.mms.modules.transaction.service.UploadHlogService;

public class Test_UploadHlogService {
	
	private UploadHlogService svc = new UploadHlogService();
	private Logger logger = Logger.getLogger(getClass());
	
	@Test
	public void testUploadHLog(){
		StringBuilder buffer = new StringBuilder("	序号,	订单号,	产品类型名称,	供应商名称,	渠道名称,	客户号,	供应商订单号,	订单成本价,	支付价格,	商户结算金额,	银行补贴金额,	活动状态,	活动名称,	产品数量,	消费积分数量,	消费优惠劵ID,	订单状态,	支付状态,	支付流水,	支付时间,	下单时间,	产品ID,	产品名称,	商户信息,	联系人姓名,	联系人电话,	备注,	快递公司,	运单号,	邮编,	收件人姓名,	收件人电话,	收件人地址,	商户名称,	分行号,	优惠劵金额,	活动优惠金额");
		String crlf = System.getProperty("line.separator");
		buffer.append(crlf);
		buffer.append("1,GP141226235647170737,团购,,手机银行,54237218,,169,35,39,4,活动单,最红交博汇团购活动（第四季）,1,0,,处理中,已支付,D065DCFF,20141226,20141226,1172,CARTELO鳄鱼男士商务男袜纯棉短袜冬季休闲袜子5双礼盒装,诸暨市帝文电子商务有限公司,胡雪骥,18602185348,,-,-,200000,胡雪骥,18602185348,上海上海市华江路860号交通银行江桥支行,拍鞋网（福建）网络科技有限公司,310320,0,4");
		buffer.append(crlf);
		buffer.append("2,GP141226235608906052,团购,,手机银行,10006893,,169,65,69,4,活动单,最红交博汇团购活动（第四季）,1,0,,处理中,已支付,D0662262,20141226,20141226,1174,南极人保暖内衣加厚加绒女男士双层黄金甲情侣保暖衣套装特价,上海大程商贸有限公司,王宁宁,18537830126,浅灰色XXXL,-,-,475000,王宁宁,18537830126,河南开封市夷山大街北段交通银行公司部,拍鞋网（福建）网络科技有限公司,6999,0,4");
		buffer.append(crlf);
		buffer.append("3,GP141226235404814272,团购,,手机银行,14940847,,598,135,138,3,活动单,最红交博汇团购活动（第四季）,1,0,,处理中,已支付,D065E5C6,20141226,20141226,1175,卓狼冬装男士棉袄韩版羽绒棉服2014新款男款棉衣冬季外套公牛棉衣,晋江速拼鞋服贸易有限公司,陈丽琼,13901936245,要红色，尺寸XXL,-,-,200333,陈丽琼,13901936245,上海上海市新村路599号,拍鞋网（福建）网络科技有限公司,310364,0,3");
		String fileName = "test.txt";
		FileTransfer file = new FileTransfer(fileName,null,buffer.toString().getBytes());
		String msg = svc.uploadHlog(file, fileName, 0);
		logger.info(msg);
	}
}
