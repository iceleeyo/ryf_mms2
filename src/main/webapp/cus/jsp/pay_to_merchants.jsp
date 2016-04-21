<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    
    <title>我要付款</title>
     <%
		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);
		int rand = new java.util.Random().nextInt(10000);
		%>
	<meta http-equiv="pragma" content="no-cache"/>
	<meta http-equiv="cache-control" content="no-cache"/>
	<meta http-equiv="expires" content="0"/>    
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3"/>
	<meta http-equiv="description" content="This is my page"/>
	<link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css"/>
	<link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="../../dwr/engine.js"></script>
    <script type="text/javascript" src="../../dwr/util.js"></script>
    <script type="text/javascript" src="../../dwr/interface/PageService.js?<%=rand%>"></script>
    <script type="text/javascript" src='../../dwr/interface/CompanyService.js?<%=rand%>'></script> 
    <script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
 	<script type="text/javascript" src="../../public/js/ryt.js?<%=rand%>"></script>
 	<script type="text/javascript" src="../../public/js/money_util.js?<%=rand%>"></script>
 	<script type="text/javascript" src="../../public/js/company/payToMerchant.js?<%=rand%>"></script>
  </head>
  <body>
  <div class="style">
 	<div id="mainDiv">
     <table width="100%"  align="left"  class="tableBorder" id="orderInputTable">
            <tr><td class="title" colspan="6">&nbsp;&nbsp;&nbsp;&nbsp;生成付款单&nbsp;&nbsp;
            </td></tr>
            <tr>
                <td class="th2" align="right" width="35%">付款方企业全称：</td>
                <td align="left" width="85%" > 
               		<input type="text" id="comName" name="comName" disabled="disabled" class="bigInput" value="${sessionScope.SESSION_LOGGED_ON_CUS_NAME}"/>
                </td>
             </tr>  
             <tr>
                <td class="th2" align="right" >付款金额：</td>
                <td align="left"  >
                 <input type="text"  name="transAmt" id="transAmt" value="" maxlength="11" onchange="setChineseMoney(this.value);" class="largeInput"/> 
                 <span class="redPoint">*</span>
                 &nbsp;&nbsp;<span id="ChineseMoney" class="redfont"></span>
               </td>
            </tr>
             <tr>  
                <td class="th2" align="right" >对方账户号：</td>
                <td align="left" >
               <input type="text" id="mid" name="mid"  class="bigInput" maxlength="20" />&nbsp;<span class="redPoint">*</span>
               &nbsp;&nbsp;&nbsp;&nbsp;<input type="button" value="选择对方银行账户" onclick="queryAccOption();"/>
                </td>
            </tr>
              <tr>
                <td class="th2" align="right" >对方企业客户：</td>
                <td align="left"  ><span id="comCustomer"></span>
                </td>
             </tr>  
             <tr>  
                <td class="th2" align="right" >对方银行账号：</td>
                <td align="left"><span id="bkAccNo"></span>
               			 <input type="hidden" id="hidde_bkAccId" name="hidde_bkAccId" />
                </td>
            </tr>
              <tr>
              	<td class="th2" align="right">
              	 短信通知：<!-- <input type="checkbox" id="isSendSms"/> -->&nbsp;&nbsp;
              	</td>
              	<td >&nbsp;<input type="checkbox" id="isSendSms"/></td>
             </tr>  
             
             <tr id="phoneNoTr" >  
                <td class="th2" align="right">付款方手机号：</td>
                <td align="left" >
                <input type="text" id="payToPhoneNo" maxlength="20"/>
                </td>
            </tr>
              <tr>
                <td class="th2" align="right">订单描述：</td>
                <td colspan="3" ><textarea rows="6" cols="40" id="remark"  name="corp_intro" ></textarea><br/>
              </td>
             </tr>  
            <tr>
            <td colspan="6" align="center" style="height: 30px">
                <input class="button" type="button" value = " 下一步 " onclick="confirmMsg();" />
            </td>
            </tr>
        </table>
        <form action="#" method="get" target="_blank" id="toBkAction">
      	<input type="hidden"  name="p"  id="paramVal"/>
      	<input type="hidden"  name="k"  id="keyVal"/>
        </form>
     <div style="display: none;width: 100%" id="confirmPayMsgDiv">
       <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="8">&nbsp;&nbsp;&nbsp;&nbsp;付款确认&nbsp;&nbsp;</td></tr>
            <tr>
                <td class="th2"  align="right" width="35%">订单号：</td>
                <td  class="largeTh" align="left"  id="confirm_ordId"></td>
            </tr>
            <tr>
                 <td class="th2" align="right">付款金额：</td>
                <td align="left"  class="largeTh"><span id="confirm_payAmt" class="redfont"></span></td>
            </tr>
            <tr>
                 <td class="th2" align="right">服务费：</td>
                <td align="left" class="largeTh"><span id="confirm_transFee" class="redfont"></span> </td>
            </tr>
           <tr>  
                <td class="th2" align="right">服务费支付方：</td>
                <td align="left" class="largeTh">&nbsp;对方支付 </td>
            </tr>
            <tr>
                 <td class="th2" align="right">应付金额：</td>
                <td class="largeTh" align="left" >
                <span id="confirm_transAmt" class="redfont"></span>&nbsp;&nbsp;&nbsp;大写：
                <span id="confirm_transAmt_uppercase" class="redfont"></span> </td>
            </tr>
            <tr>
               <td class="th2" align="right">对方企业客户名称：</td>
                <td class="largeTh" align="left"  id="confirm_aname" ></td>
            </tr>
            <!-- 
             <tr>
                <td class="th2" align="right">对方银行名：</td>
                <td class="largeTh" align="left"  id="confirm_bkName"> </td>
            </tr>
             -->
            <tr>
                <td class="th2" align="right">对方银行账号：</td>
                <td class="largeTh" align="left"  id="confirm_bkAcc"></td>
            </tr>
             <tr>
                <td class="th2" align="right">  我方手机号：</td>
                <td class="largeTh" align="left"  id="confirm_myPhoneNo"></td>
            </tr>
            <tr>
                <td class="th2" align="right">订单描述：</td>
                <td class="largeTh" align="left" id="confirm_remark"></td>
            </tr>
            
             <tr>
                <td class="th2" align="right">付款银行：</td>
                <td class="largeTh" align="left"  id="confirm_gate"></td>
            </tr>
            <tr>
            <td colspan="8" align="center" style="height: 30px">
                <input style="height: 28px;" type="button" value = "去B2B网银页面支付 "  onclick="submitOrder();"/>&nbsp;&nbsp;&nbsp;
                <input style="height: 28px;"  type="button" value = "返回修改订单 "  onclick="backModify();"/>
            </td>
            </tr>
        </table>
         </div>
         </div>
         
         <!-- **************************************** -->
   	 <div style="display: none;width: 560px;height: 170px;" id="operMsgDiv" align="center">
    	<div style="width: 80%" >
    	<h2>请在打开的页面付款，充值完成前请不要关闭本页面</h2>
    	<input type="hidden" id="ordId"/>
    	<hr class="hrStyle"/>
    	<p align="left">如果付款成功，请点击按钮 <input type="button" value="已完成付款" class="wBox_close button" onclick="queryOrder();"/></p>
		<p align="left">如果付款不成功，你的银行卡未被扣款，请点击按钮 <input type="button" value="返回付款页面" class="wBox_close button" onclick="backPayPage();"/></p>
    	<p align="left">如果您的银行卡已被扣款，但付款不成功，这可能是网络传输问题造成的，请放心我们会尽快把这笔款项打到你账户。</p>
       </div>
     </div>
     <br/><br/>
       
     <div id="orderResult" style="display:none;" align="center">
       <div class="msgDiv">
	    <br/><br/><br/>
	    <h2 id="order_other">您刚才提交的订单号为：<a href="javascript:;" id="ordId_result"></a> 
	    	<br/>支付状态为：<span style="color:red;" id="stateStr"></span><br/>
	    	</h2>
	    	<br/><br/><br/>
	    <p>如果对支付情况还有疑问，请联系我们！</p><br/>
	    </div>
   </div>&nbsp;&nbsp;
   
      
        <div id="comBkMsg" style="width: 300px;height: 220px;font-size: 14px;display: none;overflow-x:auto;">
	        <ul>
	        <li>对方企业名称：</li>
	        <li id="targetComName" class="largerLi" style="font-weight: bold;"></li>
	        <li class="largerLi"><hr class="hrStyle"/></li>
	        <li class="largerLi"><ul id="bkAccOptions"></ul></li>
	        <li id="targetComName" class="largerLi">&nbsp;&nbsp;</li>
	        <li style="margin-left: 50px;"><input type="button" value="确定" class="wBox_close button" onclick="selectAcc();"/></li>
	        </ul>
	        <br />
	        
        </div>
     </div>
  </body>
</html>
