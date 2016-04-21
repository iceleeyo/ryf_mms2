package com.rongyifu.mms.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.rongyifu.mms.bean.Hlog;
import com.rongyifu.mms.bean.OperInfo;
import com.rongyifu.mms.bean.RefundLog;
import com.rongyifu.mms.common.Ryt;
import com.rongyifu.mms.dao.BillListDownloadDao;
import com.rongyifu.mms.dao.MerOperDao;
import com.rongyifu.mms.utils.LogUtil;
import com.rongyifu.mms.utils.MD5;

/**
 * 对账接口
 * 
 * 请求参数 商户号 操作员 密码 订单日期（一次只能提取一天的订单数据） 接口类型 返回格式： <res> <status> <value></value>
 * <!—0成功1失败--> <msg></msg> <!—如果调用失败，则返回错误信息--> </status> <data> <!—
 * 对账数据，跟商户下载的对账文件内容一致 --> </data> </res>
 * 
 * @author lv.xiaofeng
 */
public class ReconciliationInterface extends HttpServlet {
	private String TTILE_STR = "<?xml version=\"1.0\" encoding=\"utf-8\"?><res><status><value>";
	private static final long serialVersionUID = -5432564263688860844L;
	MerOperDao merOperDao = new MerOperDao();
	private static final String END_STR = "</record></data></res>";
	private static final int LOCKHOUR = 3;
	// 记录错误次数的map，其中operinfo里面的mtype所记录的值为错误次数
	private static Map<String, Map<String, OperInfo>> errNumsMap = new HashMap<String, Map<String, OperInfo>>();
	// 记录IP连续错误次数
	private static Map<String, Integer> ipErrNumsMap = new HashMap<String, Integer>();

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		int flag = 1;// 是否成功标识符
		String errMsg = "";// 错误信息
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("UTF-8");
		String clientIp = request.getRemoteAddr();// 客户端IP
		PrintWriter out = response.getWriter();
		String mid = handleParam(request.getParameter("mid"));// 商户号
		String operId = handleParam(request.getParameter("operId"));// 用户名
		String operPass = handleParam(request.getParameter("operPass"));// 密码
		String billType = handleParam(request.getParameter("billType"));// 类型
																		// 1.支付对账单
																		// 2.退款对账单
		String date = handleParam(request.getParameter("date"));// 日期
		String dateType = handleParam(request.getParameter("dateType"));// 日期类型
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("mid", mid);
		paramMap.put("operId", operId);
		paramMap.put("operPass", Ryt.empty(operPass) ? "" : MD5.getMD5(operPass.getBytes()));
		paramMap.put("billType", billType);
		paramMap.put("date", date);
		paramMap.put("dateType", dateType);
		paramMap.put("ip", clientIp);
		LogUtil.printInfoLog("ReconciliationInterface", "doGet", "请求参数：",
				paramMap);
		// 基本数据校验
		if (!basicVerify(paramMap, out)) {
			return;
		}
		operPass = toHexMd5(operPass);
		BillListDownloadDao bdd = new BillListDownloadDao();
		if (VerifiedIdentity(mid, operId, operPass, out, clientIp)) {
			if ("1".equals(billType.trim())) {
				String dateField = null;
				if ("1".equals(dateType))
					dateField = "sys_date"; // 系统日期
				else
					dateField = "mdate"; // 商户日期

				List<Hlog> list = bdd.iqueryPayBill(mid, "2", date, dateField);
				flag = 0;
				execute(buildStr(flag, errMsg, list, billType), out);
				return;
			} else if ("2".equals(billType.trim())) {
				String dateField = null;
				if ("3".equals(dateType))
					dateField = "pro_date"; // 退款经办日期
				else
					dateField = "req_date"; // 退款确认日期

				List<RefundLog> list = bdd.iqueryBackBill(mid, date, dateField);
				flag = 0;
				execute(buildStr(flag, errMsg, list, billType), out);
				return;
			} else {
				errMsg = "指定账单类型不存在！";
				execute(buildStr(flag, errMsg, null, ""), out);
				return;
			}
		}

	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * 验证身份
	 * 
	 * @param mid
	 *            商户号
	 * @param operId
	 *            操作员ID
	 * @param operPass
	 *            操作员密码
	 * @return
	 */
	private boolean VerifiedIdentity(String mid, String operId,
			String operPass, PrintWriter out, String clientIp) {
		String errMsg = "";
		int flag = 1;
		if (isTopForClientIp(clientIp)) {
			errMsg = "本机已连续错误超过五次，请" + LOCKHOUR + "小时以后重试！";
			execute(buildStr(flag, errMsg, null, ""), out);
			return false;
		}
		if (operIsLock(mid, operId)) {
			errMsg = "该操作员已被锁定，请" + LOCKHOUR + "小时以后重试！";
			execute(buildStr(flag, errMsg, null, ""), out);
			return false;
		}
		if (merOperDao.hasMid(mid)) {
			List<OperInfo> list = merOperDao.hasOperInMid(mid, operId);
			if (list.size() > 0) {
				// 验证通过
				if (merOperDao.checkOperPass(mid, operId, operPass).size() > 0) {
					// 操作员存在且密码正确
					removeMinfoFromErrmap(mid, operId);
					removeClientIpFromErrmap(clientIp);
					return true;
				}// 验证不通过
				else {
					if (addClientIpErrNums(clientIp) >= 5) {
						errMsg = "本机已连续错误尝试超过五次，请" + LOCKHOUR + "小时以后重试！";
						lockClientIp(clientIp);
						execute(buildStr(flag, errMsg, null, ""), out);
						return false;
					}
					if (isTopLimit(mid, operId) == 3) {
						errMsg = "操作员密码已连续错误超过三次，账户即将被锁定！";
						lockOper(mid, operId);
						execute(buildStr(flag, errMsg, null, ""), out);
						return false;
					}
					errMsg = "操作员密码错误！";
					execute(buildStr(flag, errMsg, null, ""), out);
					return false;
				}
			} else {
				if (addClientIpErrNums(clientIp) >= 5) {
					errMsg = "本机已连续错误尝试超过五次，请" + LOCKHOUR + "小时以后重试！";
					lockClientIp(clientIp);
					execute(buildStr(flag, errMsg, null, ""), out);
					return false;
				}
				errMsg = "操作员不存在！";
				execute(buildStr(flag, errMsg, null, ""), out);
				return false;
			}
		}
		if (addClientIpErrNums(clientIp) >= 5) {
			errMsg = "本机已连续错误尝试超过五次，请" + LOCKHOUR + "小时以后重试！";
			lockClientIp(clientIp);
			execute(buildStr(flag, errMsg, null, ""), out);
			return false;
		}
		errMsg = "该商户不存在！";
		execute(buildStr(flag, errMsg, null, ""), out);
		return false;
	}

	/**
	 * 构建输出XML
	 * 
	 * @param flag
	 *            是否成功标识符 0成功 1失败
	 * @param errMsg
	 *            返回给客户端的错误信息
	 * @param data
	 *            查询到的数据
	 * @return 返回构建成的XML数据
	 */
	private String buildStr(int flag, String errMsg, List data, String billType) {
		String allStr = "";
		// 查询成功
		if (flag == 0 && errMsg.equals("")) {
			String info = toXML(data, billType);
			allStr = TTILE_STR + flag + "</value><msg>" + errMsg
					+ "</msg></status><data><record_num>" + data.size()
					+ "</record_num><field_num>10</field_num><record>" + info
					+ END_STR;
		} else if (!errMsg.equals("")) {
			allStr = TTILE_STR
					+ flag
					+ "</value><msg>"
					+ errMsg
					+ "</msg></status><data><record_num>0</record_num><field_num>10</field_num><record>"
					+ END_STR;
		} else {
			errMsg = "未知错误";
			allStr = TTILE_STR
					+ flag
					+ "</value><msg>"
					+ errMsg
					+ "</msg></status><data><record_num>0</record_num><field_num>10</field_num><record>"
					+ END_STR;

		}
		return allStr;
	}

	/**
	 * 执行返回数据
	 * 
	 * @param str
	 *            返回给用户的数据
	 */
	private void execute(String str, PrintWriter out) {
		int resLength = str.length();
		if(resLength < 500)
			LogUtil.printInfoLog("response: " + str);
		else
			LogUtil.printInfoLog("return length: " + str.length());
		
		out.write(str);
		out.flush();
		out.close();
	}

	/**
	 * 数据基本校验
	 */
	private boolean basicVerify(Map<String, String> paramMap, PrintWriter out) {
		String errMsg = "";
		int flag = 1;
		String mid = paramMap.get("mid");
		String operId = paramMap.get("operId");
		String operPass = paramMap.get("operPass");
		String billType = paramMap.get("billType");
		String date = paramMap.get("date");
		String dateType = paramMap.get("dateType");
		if (mid == null || mid.equals("")) {
			errMsg = "商户不能为空！";
			execute(buildStr(flag, errMsg, null, billType), out);
			return false;
		} else if (!isNum(mid)) {
			errMsg = "商户号错误！";
			execute(buildStr(flag, errMsg, null, billType), out);
			return false;
		}
		if (operId == null || operId.equals("")) {
			errMsg = "操作员号不能为空！";
			execute(buildStr(flag, errMsg, null, billType), out);
			return false;
		} else if (!isNum(operId)) {
			errMsg = "操作员号错误！";
			execute(buildStr(flag, errMsg, null, billType), out);
			return false;
		}
		if (operPass == null || operPass.equals("")) {
			errMsg = "密码不能为空！";
			execute(buildStr(flag, errMsg, null, billType), out);
			return false;
		}
		if (billType == null || billType.equals("")) {
			errMsg = "查询类型不能为空！";
			execute(buildStr(flag, errMsg, null, billType), out);
			return false;
		} else if (!billType.equals("1") && !billType.equals("2")) {
			errMsg = "查询类型不存在！";
			execute(buildStr(flag, errMsg, null, billType), out);
			return false;
		}
		if (date == null || date.equals("")) {
			errMsg = "查询时间不能为空！";
			execute(buildStr(flag, errMsg, null, billType), out);
			return false;
		} else if (!isNum(date)) {
			errMsg = "查询时间错误！";
			execute(buildStr(flag, errMsg, null, billType), out);
			return false;
		}
		if (Ryt.empty(dateType)) {
			errMsg = "日期类型不能为空！";
			execute(buildStr(flag, errMsg, null, billType), out);
			return false;
		} else {
			if (billType.equals("1") && !dateType.equals("1")
					&& !dateType.equals("2")) {
				errMsg = "日期类型错误！";
				execute(buildStr(flag, errMsg, null, billType), out);
				return false;
			} else if (billType.equals("2") && !dateType.equals("3")
					&& !dateType.equals("4")) {
				errMsg = "日期类型错误！";
				execute(buildStr(flag, errMsg, null, billType), out);
				return false;
			}
		}

		return true;
	}

	/**
	 * 将数据转换成xml
	 * 
	 * @param list
	 *            数据源
	 * @return
	 */
	public String toXML(List list, String billType) {
		StringBuffer str = new StringBuffer();
		BillListDownloadDao bdd = new BillListDownloadDao();
		// 查询所有银行
		Map<String, String> gateMap = bdd.getAllGate();
		// String[] types = { "初始化", "网上支付", "充值消费卡支付", "信用卡支付交易", "退款交易",
		// "增值业务交易", "语音支付", "B2B交易", "手机WAP支付" };
		// 商户号， 商户订单号，商户日期，交易金额，手续费，电银流水号，交易日期，交易类型，订单状态
		if ("1".equals(billType.trim())) {
			for (int i = 0; i < list.size(); i++) {
				Hlog hl = (Hlog) list.get(i);

				str.append(hl.getMid() == null ? "" : hl.getMid());// 商户号
				str.append("|").append(hl.getOid() == null ? "" : hl.getOid());// 商户订单号
				str.append("|").append(
						hl.getMdate() == null ? "" : hl.getMdate());// 商户日期
				str.append("|").append(
						hl.getAmount() == null ? "" : (Ryt.div100(hl
								.getAmount())));// 交易金额
				str.append("|").append(
						hl.getFeeAmt() == null ? ""
								: Ryt.div100(hl.getFeeAmt()));// 系统手续费
				str.append("|")
						.append(hl.getTseq() == null ? "" : hl.getTseq());// 电银流水号
				str.append("|").append(
						hl.getSysDate() == null ? "" : hl.getSysDate());// 交易日期
				str.append("|")
						.append(hl.getType() == null ? "" : hl.getType());// 交易类型
																			// types[hl.getType()]
				str.append("|")
						.append(hl.getTstat() == null ? "" : hl.getTstat())
						.append(";");
				;// 订单状态

				// str.append("|").append(
				// hl.getGate() == null ? "" : hl.getGate())//
				// 交易银行gateMap.get(hl.getGate()+ "")
			}
		} else if ("2".equals(billType.trim())) {
			/**
			 * 退款流水号 原电银流水号 原商户订单号 原银行流水号 原交易日期 原交易金额（元） 原交易银行 退款金额（元）
			 * 退还商户手续费(元) 经办日期 退款确认日期 退款状态 oid tseq org_oid org_bk_seq org_mdate
			 * org_amt gid ref_amt mer_fee pro_date req_date stat
			 */
			// 退款对账单下载格式：
			// 商户号， 原商户订单号，原商户日期，退款金额，退回手续费，退款流水号，退款确认日期，退款经办日期，退款状态，原电银流水号
			for (int i = 0; i < list.size(); i++) {
				RefundLog rl = (RefundLog) list.get(i);
				// str.append(rl.getOid());// 退款流水号
				// str.append("|" + rl.getTseq());// 原电银流水号
				// str.append("|" + rl.getOrg_oid());// 原商户订单号
				// str.append("|" + rl.getOrgBkSeq());// 原银行流水号
				// str.append("|" + rl.getOrg_mdate());// 原交易日期
				// str.append("|" + Ryt.div100(rl.getOrgAmt()));// 原交易金额（元）
				// str.append("|" + rl.getGid() );//
				// 原交易银行gateMap.get(rl.getGid() + "")
				// str.append("|" + Ryt.div100(rl.getRef_amt()));// 退款金额（元）
				// str.append("|" + Ryt.div100(rl.getMerFee()));// 退还商户手续费(元)
				// str.append("|" + rl.getPro_date());// 经办日期
				// str.append("|" + rl.getReq_date());// 退款确认日期
				// str.append("|退款成功;");// 退款状态

				str.append(rl.getMid()); // 商户号
				str.append("|" + rl.getOrg_oid());// 原商户订单号
				str.append("|" + rl.getOrg_mdate());// 原商户日期
				// str.append("|" + rl.getMdate());// 商户申请退款日期
				str.append("|" + Ryt.div100(rl.getRef_amt()));// 退款金额（元）
				str.append("|" + Ryt.div100(rl.getMerFee()));// 退还商户手续费(元)
				str.append("|" + rl.getOid());// 退款流水号
				str.append("|" + rl.getReq_date());// 退款确认日期
				str.append("|" + rl.getPro_date());// 经办日期
				str.append("|" + rl.getStat());// 退款状态
				str.append("|" + rl.getTseq() + ";");// 原电银流水号
			}
		}

		return str.toString();
	}

	/**
	 * MD5加密
	 * 
	 * @param password
	 *            文本
	 */
	private String toHexMd5(String password) {
		if (password == null)
			return "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(password.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();// 32位加密， 16位取 8,24
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static boolean isNum(String str) {
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	}

	private String handleParam(String str) {
		return Ryt.empty(str) ? "" : Ryt.checkSingleQuotes(str.trim());
	}

	/**
	 * 判断操作员是否被锁
	 * 
	 * @param midInfo
	 * @return true锁定，false正常
	 */
	public boolean operIsLock(String mid, String operId) {
		Map<String, OperInfo> midInfo = errNumsMap.get(mid);
		// 判断操作员是否已经锁定
		if (midInfo != null) {
			OperInfo operInfo = midInfo.get(operId);
			if (operInfo != null && operInfo.getState() == 1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 清除操作员错误次数信息
	 * 
	 * @param mid
	 *            商户号
	 * @param operId
	 *            操作员ID
	 */
	public static void removeMinfoFromErrmap(String mid, String operId) {
		if (errNumsMap.get(mid) != null)
			errNumsMap.get(mid).remove(operId);
		LogUtil.printInfoLog("ReconciliationInter", "removeMinfoFromErrmap",
				"===========商户" + mid + "的操作员:" + operId + "已解锁！============");
	}

	public static void removeClientIpFromErrmap(String clientIp) {
		if (ipErrNumsMap.get(clientIp) != null)
			ipErrNumsMap.remove(clientIp);
		LogUtil.printInfoLog("ReconciliationInter", "removeClientIpFromErrmap",
				"===========Ip：" + clientIp + "已解锁！============");
	}

	/**
	 * 判断操作员密码错误是否上限
	 * 
	 * @param mid
	 *            商户号
	 * @param operId
	 *            操作员ID
	 * @return 返回1机表示第一次错 返回3则表示错误已经上线了，操作员ID将被锁定3小时
	 */
	private int isTopLimit(String mid, String operId) {
		if (errNumsMap.get(mid) != null) {
			if (errNumsMap.get(mid).get(operId) != null) {
				if (errNumsMap.get(mid).get(operId).getMtype() == null) {
					errNumsMap.get(mid).get(operId).setMtype(1);
				} else {
					errNumsMap
							.get(mid)
							.get(operId)
							.setMtype(
									errNumsMap.get(mid).get(operId).getMtype() + 1);
				}
				return errNumsMap.get(mid).get(operId).getMtype();
			} else {
				OperInfo oper = new OperInfo();
				oper.setState(0);
				oper.setMtype(1);
				errNumsMap.get(mid).put(operId, oper);
			}
		} else {
			errNumsMap.put(mid, new HashMap<String, OperInfo>());
			OperInfo oper = new OperInfo();
			oper.setState(0);
			oper.setMtype(1);
			errNumsMap.get(mid).put(operId, oper);
		}
		return 1;
	}

	/**
	 * 锁定操作员
	 * 
	 * @param mid
	 *            商户号
	 * @param operId
	 *            操作员号
	 */
	private void lockOper(String mid, String operId) {
		if (errNumsMap.get(mid) != null
				&& errNumsMap.get(mid).get(operId) != null) {
			// 设置状态为关闭
			errNumsMap.get(mid).get(operId).setState(1);
		}
		Timer timer = new Timer();
		timer.schedule(new LockOperTimer(mid, operId, timer),
				LOCKHOUR * 60 * 60 * 1000);
		// timer.schedule(new LockOperTimer(mid,operId,timer), 30*1000);//test
	}

	/**
	 * 用户IP地址是否连续错误超过5次
	 * 
	 * @param clientIp
	 *            客户端IP
	 * @return 返回true表示已经超过5次，false则没超过
	 */
	private boolean isTopForClientIp(String clientIp) {
		if (ipErrNumsMap.get(clientIp) == null) {
			return false;
		}
		Integer errNum = ipErrNumsMap.get(clientIp);
		if (errNum >= 5)
			return true;
		else
			return false;
	}

	/**
	 * 增加指定IP地址错误次数
	 * 
	 * @param clientIp
	 *            客户端IP
	 * @return 返回错误次数
	 */
	private int addClientIpErrNums(String clientIp) {
		if (ipErrNumsMap.get(clientIp) == null) {
			ipErrNumsMap.put(clientIp, 1);
			return 1;
		}
		Integer errNum = ipErrNumsMap.get(clientIp);
		Integer newErrNum = errNum + 1;
		ipErrNumsMap.put(clientIp, newErrNum);
		return newErrNum;
	}

	/**
	 * 锁定指定IP地址
	 * 
	 * @param clientIp
	 *            指定IP
	 */
	private void lockClientIp(String clientIp) {
		Timer timer = new Timer();
		timer.schedule(new LockClientIp(clientIp, timer),
				LOCKHOUR * 60 * 60 * 1000);
	}
}

class LockOperTimer extends TimerTask {
	Timer timerControler = null;
	String mid = "";
	String operId = "";

	public LockOperTimer(String mid, String operId, Timer timer) {
		this.mid = mid;
		this.operId = operId;
		this.timerControler = timer;
	}

	@Override
	public void run() {
		ReconciliationInterface.removeMinfoFromErrmap(mid, operId);
		timerControler.cancel();
	}
}

class LockClientIp extends TimerTask {
	String clientIp = "";
	Timer timerControler = null;

	public LockClientIp(String clientIp, Timer timerControler) {
		this.clientIp = clientIp;
		this.timerControler = timerControler;
	}

	@Override
	public void run() {
		ReconciliationInterface.removeClientIpFromErrmap(clientIp);
		timerControler.cancel();
	}

}