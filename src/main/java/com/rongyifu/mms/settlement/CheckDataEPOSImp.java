package com.rongyifu.mms.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckDataEPOSImp implements SettltData {

    @Override
    public List<SBean> getCheckData(String bank, String fileContent) throws Exception {
        // 商户订单号,银行订单号,易宝订单号,订单金额,支付时间, 商品名称,扩展信息,支付银行,分账明细
        // 0810612316,55381548,5166005445,100.00,2008-12-17 11:15:23,"376906","test",BOCO-NET,p1:p1@yeepay.com:20

        List<SBean> res = new ArrayList<SBean>();
        String[] datas = fileContent.split("\n");
        for (int i = 3; i < datas.length - 1; i++) {
            String[] values = datas[i].split(",");
            SBean bean = new SBean();
            bean.setGate(bank);
            bean.setTseq(values[0]);
            bean.setDate(values[4].substring(0, 10).replaceAll("-", ""));
            bean.setAmt(values[3].replaceAll(",", ""));
            res.add(bean);
        }
        return res;
    }

    @Override
    public List<SBean> getCheckData(String bank, Map<String, String> m) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}
