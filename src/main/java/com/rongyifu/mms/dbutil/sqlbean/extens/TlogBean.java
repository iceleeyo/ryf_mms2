package com.rongyifu.mms.dbutil.sqlbean.extens;

import com.rongyifu.mms.bean.LoginUser;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.SystemDao;
import org.apache.log4j.Logger;

/****
 * 继承TlogBean
 * @author admin
 *
 */
public class TlogBean extends com.rongyifu.mms.dbutil.sqlbean.TlogBean {
	Logger log4j=Logger.getLogger(TlogBean.class);
    SystemDao sysDao=new SystemDao();
	private String tseq_str;
    private String p1PT;
    private String phoneNoPT;
    private String cardNoPT;
    private String mobileNoPT;

	public String getTseq_str() {
		return tseq_str;
	}
	

	public void setTseq_str(String tseq_str) {
		this.tseq_str = tseq_str;
	}


	/***
	 * 重写tlogbean.setTseq为tseq_str赋值
	 */
	public void setTseq(Long tseq){
		if(tseq_str==null){
			tseq_str=tseq.toString();
		}
		super.setTseq(tseq);
	}

    /**
     * 增加权限控制代码
     * 页面取值p1PT
     * @return the p1PT
     */
    public String getP1PT() {
        LoginUser user=sysDao.getLoginUser();
        return Ryt.getProperty(user,p1PT);
    }

    /**
     * @param p1PT the p1PT to set
     */
    public void setP1PT(String p1PT) {
        this.p1PT = p1PT;
    }

    public void setP1(String p1){
        if(p1PT==null){
            p1PT=p1;
        }
        super.setP1(p1);
    }

    /**
     * @return the phoneNoPT
     */
    public String getPhoneNoPT() {
        LoginUser user=sysDao.getLoginUser();
        return Ryt.getProperty(user,phoneNoPT);
    }

    /**
     * @param phoneNoPT the phoneNoPT to set
     */
    public void setPhoneNoPT(String phoneNoPT) {
        this.phoneNoPT = phoneNoPT;
    }

    public void setPhone_no(String phoneNo){
        if(phoneNoPT==null){
            phoneNoPT=phoneNo;
        }
        super.setPhone_no(phoneNo);
    }
    /**
     * @return the cardNoPT
     */
    public String getCardNoPT() {
        LoginUser user=sysDao.getLoginUser();
        return Ryt.getProperty(user,cardNoPT);
    }

    /**
     * @param cardNoPT the cardNoPT to set
     */
    public void setCardNoPT(String cardNoPT) {
        this.cardNoPT = cardNoPT;
    }

    public void setCard_no(String cardNo){
        if(cardNoPT==null){
            cardNoPT=cardNo;
        }
        super.setCard_no(cardNo);
    }
    /**
     * @return the mobileNoPT
     */
    public String getMobileNoPT() {
        LoginUser user=sysDao.getLoginUser();
        return Ryt.getProperty(user,mobileNoPT);
    }

    /**
     * @param mobileNoPT the mobileNoPT to set
     */
    public void setMobileNoPT(String mobileNoPT) {
        this.mobileNoPT = mobileNoPT;
    }

    public void setMobile_no(String mobileNo){
        if(mobileNoPT==null){
            mobileNoPT=mobileNo;
        }
        super.setMobile_no(mobileNo);
    }

   
	
}
