package com.rongyifu.mms.bank.query;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.epos.facade.EposFacade;
import com.rongyifu.mms.bean.BankQueryBean;
import com.rongyifu.mms.bean.GateRoute;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.common.Ryt;

/**
 * 易宝订单查询
 * 
 * @author zhang.liting
 * 
 */
public class EposQuery extends ABankQuery {
    private static final Log log = LogFactory.getLog(EposQuery.class);

    @Override
    public BankQueryBean queryOrderStatusFromBank(GateRoute gate, Hlog order) {
        BankQueryBean bankquerybean = new BankQueryBean();
        String P0_Cmd = "QueryOrdDetail";
        String P2_Order = order.getTseq();// 订单号(测试：9200099389)
        String Pv_Ver = "";// 版本号
        String P3_ServiceType = "";// 查询类型

        String P1_MerId = gate.getMerNo();// 商户号(测试：00005)
        String merKey = gate.getMerKey();// 商户密钥
        String reqUrl = gate.getRequestUrl(); // 请求路径：https://www.yeepay.com/app-merchant-proxy/command

        Map<String, String> hm = new HashMap<String, String>();
        hm.put("p0_Cmd", P0_Cmd);// //业务类型
        hm.put("p2_Order", P2_Order);// 商户订单号
        hm.put("pv_Ver", Pv_Ver);// 版本号
        hm.put("p3_ServiceType", P3_ServiceType);// 查询类型

        Map<String, String> result = null;
        try {
            result = EposFacade.trxRequest(reqUrl, P1_MerId, merKey, hm, "POST", "gbk");
            log.info("EposQuery:result=" + result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Epos查询失败！");
        }
        
        String R1_Code = result.get("r1_Code");
        String Rb_PayStatus = result.get("rb_PayStatus");
        if (!Ryt.empty(R1_Code) && R1_Code.equals("1")) {
            if ("SUCCESS".equals(Rb_PayStatus)) {
                bankquerybean.setOrderStatus(QueryCommon.ORDER_STATUS_SUCCESS);
                return bankquerybean;
            } else if ("CANCELED".equals(Rb_PayStatus)) {
                bankquerybean.setErrorMsg("已取消");
                bankquerybean.setOrderStatus(QueryCommon.ORDER_STATUS_FAILURE);
            } else if ("PROCESS".equals(Rb_PayStatus)) {
                bankquerybean.setErrorMsg("支付中;");
                bankquerybean.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
            } else if ("INIT".equals(Rb_PayStatus)) {
                bankquerybean.setErrorMsg("未支付");
                bankquerybean.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
            } else {
                bankquerybean.setErrorMsg("未知原因");
                bankquerybean.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
            }
        } else {
            bankquerybean.setErrorMsg("订单不存在");
            bankquerybean.setOrderStatus(QueryCommon.ORDER_STATUS_OTHER);
        }

        return bankquerybean;

    }
}
