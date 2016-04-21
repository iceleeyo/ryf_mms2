<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>账户充值</title>
<%
response.setHeader("Pragma","No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", 0);
int rand = new java.util.Random().nextInt(10000);
%>
        <meta http-equiv="pragma" content="no-cache" />
        <meta http-equiv="cache-control" content="no-cache"/>
        <meta http-equiv="Expires" content="0"/>  
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css"/> 
        <link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css"/>
         <link href="../../public/css/tab.css?<%=rand %>" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="../../dwr/engine.js"></script>
        <script type="text/javascript" src="../../dwr/util.js"></script>
        <script type="text/javascript" src='../../dwr/interface/MerZHService.js?<%=rand%>'></script> 
        <script type="text/javascript" src='../../dwr/interface/MerchantService.js'></script>
        <script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../public/js/adminAccount/mer_zhcz.js?<%=rand%>'></script>
        <script type='text/javascript' src="../../public/js/money_util.js?<%=rand%>"></script>
       	<script type=text/javascript src="../../public/js/jquery.idTabs.min.js?<%=rand %>"></script>
        <script type="text/javascript">
			jQuery.noConflict();
	(function($) { 
		 $(function(){$("#usual1 ul").idTabs();});
	})(jQuery);
		
	</script>
        
    </head>
    <body onload=init();>
    <div class="style" >
   	 <div id="mainDiv">
    	<div id="usual1" class="usual" > 
					<ul>  
					    <li><a  class="selected" href="#tab1" id="tablink1" >网银充值</a></li> 
					    <li><a  href="#tab2" id="tablink2"  >线下充值</a></li>
					</ul> 
	    
		
        <form name="MERTLOG"  method="post" action="">
          <div id="tab1">
         <table width="100%"  align="left"  class="tableBorder">
            <tr>
                <td class="th2" bgcolor="#D9DFEE" align="right" width="40%">我要充值的账户：</td>
                <td align="left"  >
                 <input type="hidden"  name="mid" id="mid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}" />
	             	&nbsp;&nbsp;<span id="aid" style="font-weight: bold;font-size: medium;color: red;">暂无账户</span>
	             	   </td>
	             	  <td align="left" >(账户为结算账户)&nbsp;<span class="redPoint">*</span></td>
            </tr>
            <tr>
                <td class="th2" bgcolor="#D9DFEE" align="right" width="40%">充值金额：</td>
                <td  align="left"  ><input type="text"  id="payAmt" name="payAmt"  maxlength="11" onchange="setChineseMoney(this.value,1)" class="largeInput"/> 
                 </td> 
                <td align="left" width="50%">元 &nbsp;&nbsp;<span id="balance_chineseAmt"class="redPoint">*</span></td>
            </tr>
            <tr>
            <td colspan="3" align="center">
            <br/>
	            <table width="100%"><tr>
	            <td  style="text-align: right;vertical-align: top;border: none;height: 60px;"><span class="redPoint">*</span>选择企业银行网银：&nbsp;&nbsp;</td>
	            <td style="border: none;width:850px;vertical-align: top;">
	            <div id="bank_list" class="banks"></div>
	            </td>
	            </tr>
	            </table>
            </td>
            </tr>
           <tr>
           <td colspan="3" align="center">
           	         <input name="payway" id="forbank" style="display: block" class="button" type="button" value = "网银支付" onclick="confirmMsg()" />
           </td>
           </tr>
        </table>
        <div></div>
        </div>
        <div id="tab2">
         <table width="100%"  align="left"  class="tableBorder">
            <tr>
                <td class="th2" bgcolor="#D9DFEE" align="right" width="40%">我要充值的账户：</td>
                <td align="left"  >
                 <input type="hidden"  name="mid" id="mid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}" />
	             		&nbsp;&nbsp;<span id="aids" style="font-weight: bold;font-size: medium;color: red;">暂无账户</span>
	             	 </td>
	             	  <td>(账户为结算账户)&nbsp; <span class="redPoint">*</span> </td>
             
            </tr>	
            <tr>
                <td class="th2" bgcolor="#D9DFEE" align="right" width="40%">充值金额：</td>
                <td  align="left"  ><input type="text" id="payAmts"   name="payAmts"  maxlength="11" onchange="setChineseMoney(this.value,2)"  class="largeInput"/> 
                 </td> 
                 <td align="left" colspan="3" width="50%"> 元
                   &nbsp;&nbsp;<span id="offline_chineseAmt"class="redPoint">*</span></td>
            </tr>
            <tr>
            <td colspan="3" align="center">
            <br/>
            </td>
            </tr>
           <tr>
           <td colspan="3" align="center">
           			 <input name="payway" id="offline" style="display: block"   class="button" type="button" value = "线下支付" onclick="payForOffline()" />
           </td>
           </tr>
        </table>
        <div></div>
        </div>
       </form>
       </div>
    <form action="#" method="get" target="_blank" id="toBkAction" enctype="application/x-www-form-urlencoded">
      	<input type="hidden"  name="p"  id="paramVal"/>
      	<input type="hidden"  name="k"  id="keyVal"/>
     </form>
    <div style="display: none;width: 480px;height: 230px;" id="confirmPayMsgDiv">
    	<table  style="height: 100%;width: 100%"  class="table_bigFont" >
    	<!-- <tr><td width="200px;" align="right" class="boldFont">昵称：</td><td id="confirm_name"></td></tr> -->
    	<tr><td align="right" class="boldFont">电银账号：</td><td id="confirm_name"></td></tr>
    	<tr><td align="right" class="boldFont">充值金额：</td><td ><span id="confirm_payAmt" class="redfont"></span>&nbsp;&nbsp;元</td></tr>
    	<tr><td align="right" class="boldFont">手续费：</td><td ><span id="confirm_transFee" class="redfont"></span>&nbsp;&nbsp;元</td></tr>
    	<tr><td align="right" class="boldFont">支付总金额：</td><td ><span id="confirm_transAmt" class="redfont"></span>&nbsp;&nbsp;元</td></tr>
    	<tr><td align="right" class="boldFont">选择的银行：</td><td id="confirm_bk"></td></tr>
    	<tr align="center" ><td height="20px;" colspan="2"><br/>
    	<input value="确认提交" class="button wBox_close" type="button" onclick="submitOrder();"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    	<input value="返回修改" class="wBox_close button" type="button"/></td></tr>
    	</table>
     </div>
     <div style="display: none;width: 480px;height: 230px;" id="offlineconfirmMsgDiv">
  	  <table class="tableBorder">
    	<tr><td align="right"  class="boldFont" width="40%">订单号：</td><td><span id="order_id" class="redfont" style="font-size: 10"></span></td></tr>
    	<tr><td align="right" class="boldFont">电银账号：</td><td id="confirm_names"></td></tr>
    	<tr><td align="right" class="boldFont">充值金额：</td><td ><span id="paymoney" class="redfont"></span>&nbsp;&nbsp;元</td></tr>
    	<tr><td align="right" class="boldFont">手续费：</td><td ><span id="transFee" class="redfont"></span>&nbsp;&nbsp;元</td></tr>
    	<tr><td align="right" class="boldFont">支付总金额：</td><td ><span id="tranPayAmt" class="redfont"></span>&nbsp;&nbsp;元</td></tr>
    	<tr><td align="right" class="boldFont">支付方式：</td><td id="confirm_bk_way"><span  class="redfont">线下支付</span></td></tr>
    	<tr align="center" ><td height="20px;" colspan="2"><br/>
    	<input value="确认提交" class="button wBox_close" type="button" onclick="submitForOffLine();"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    	<input value="取消" class="wBox_close button" type="button"/></td></tr>
    	</table>
     </div>
   	 <div style="display: none;width: 560px;height: 170px;" id="operMsgDiv" align="center">
    	<div style="width: 80%" >
    	<h2>请在打开的页面充值，充值完成前请不要关闭本页面</h2>
    	<input type="hidden" id="ordId"/>
    	<hr class="hrStyle"/>
    	<p align="left">如果充值成功，请点击按钮 <input type="button" value="已完成充值" class="wBox_close button" onclick="queryOrder();"/></p>
		<p align="left">如果充值不成功，你的银行卡未被扣款，请点击按钮 <input type="button" value="返回充值页面" class="wBox_close button"/></p>
    	<p align="left">如果您的银行卡已被扣款，但充值不成功，这可能是网络传输问题造成的，请放心我们会尽快把这笔款项打到你账户。</p>
       </div>
     </div>
    </div>
    <br/><br/>
     <div id="orderResult" style="display:none;" align="center">
       <div class="msgDiv">
	    <br/><br/><br/>
	    <h2 id="order_other">您刚才提交的订单号为：<a href="javascript:;" id="ordId_result"></a> 
	    	<br/>支付状态为：<span style="color:red;" id="stateStr"></span><br/>
	    	</h2><br/><br/><br/>
	    <p>如果对支付情况还有疑问，请联系我们！</p><br/>
	    </div>
     </div>
   </div>&nbsp;&nbsp;	
    <br/>
     <%--
    <img style="width: 600px; heigth: 300px"  src="../../public/persion/cz03.png"/> 
   
    -下一步页面-整个页面覆层，浏览器重新打开一个窗口跳转到银行页面-------------------------------------
    
     <img style="width: 800px; heigth: 300px"  src="../../public/persion/cz04.jpg"/> 
      </div>   
       --%>
   
   
    </body>
</html>

           
           
    
