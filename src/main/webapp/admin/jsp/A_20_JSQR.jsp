<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>结算确认</title>
 <%
response.setHeader("Pragma","No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", 0);
int rand = new java.util.Random().nextInt(10000);
%>
        <meta http-equiv="pragma" content="no-cache"/>
        <meta http-equiv="cache-control" content="no-cache"/>
        <meta http-equiv="Expires" content="0"/>  
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css"/>
        <script type='text/javascript' src='../../dwr/engine.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../dwr/util.js?<%=rand%>'></script>
       <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
       <script type='text/javascript' src='../../dwr/interface/RypCommon.js?<%=rand%>'></script>
       <script type='text/javascript' src='../../dwr/interface/SettlementVerifyService.js?<%=rand%>'></script>
       <script type="text/javascript" src="../../public/js/settlement/admin_verify_settlement.js?<%=rand%>"></script>
	   <script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
	   <script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
	   
	   <script type='text/javascript' src='../../dwr/interface/DoSettlementService.js?<%=rand %>'></script>
	   <script type='text/javascript' src='../../dwr/interface/SettlementNoticeService.js?<%=rand%>'></script>
       
	   
	   <script type='text/javascript'>
        function checkBkNo(){
            DoSettlementService.checkBkNo(function(msg){alert(msg)});
        }
        </script>
        
    </head>
    <body onload="init();">
    
    <div class="style">
    <table class="tableBorder" >
        <tr>
          <td class="title" colspan="6">&nbsp;结算确认&nbsp;&nbsp;</td>
        </tr>
        <tr>
         <td class="th1" bgcolor="#D9DFEE" align="right" width="10%"> 商户号：</td>
         <td align="left" width="30%">
             	<input type="text" id="mid" name="mid"  size="8px" style="width: 150px" onkeyup="checkMidInput(this);"/>
                   &nbsp;<!-- <select style="width: 150px" id="smid" name="smid" onchange="initMidInput(this.value)">
                     <option value="">全部...</option>
                    </select> --> 
         </td>
         <td class="th1" bgcolor="#D9DFEE" align="right" width="10%" >结算状态:</td>
         <td align="left"  >
           <select id="state" name="state" >
             <option value="-1">全部</option>
             <option value="2" selected="selected">已制表</option>
             <option value="3">已确认</option>                            
           </select>
         </td>
         <td class="th1" align="right" >商户状态：</td>
          <td align="left">
                <select style="width: 150px" id="mstate" name="mstate">
                    <option value="">全部...</option>
                </select>
           </td>
       </tr>
       <tr>
        <td class="th1" bgcolor="#D9DFEE" align="right" height="30px">结算发起日期：</td>
         <td align="left">
          <input id="beginDate" name="beginDate" class="Wdate" type="text" value=""
                        onfocus="WdatePicker({skin:'ext',minDate:'1998-12-20',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});" />
                            至&nbsp;<input id="endDate" name="endDate"  class="Wdate" type="text" value=""
                        onfocus="WdatePicker({skin:'ext',minDate:'#F{$dp.$D(\'beginDate\')}',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});"/>&nbsp;<font color="red">*</font>
         </td>
         <td class="th1" bgcolor="#D9DFEE" align="right"  height="30px">批次号：</td>
         <td align="left" ><input type="text" id="batch" name="batch"/></td>
         <td width="10%" align="right" class="th1">&nbsp;结算渠道：</td>
			<td align="left">&nbsp;
				<select name="liqGid" id="liqGid" >
				    <option value="3">自动结算</option>
					<option value="1">手工结算 -结算到电银账户</option>
					<option value="2">手工结算 -结算到银行卡</option>
					<option value="4">自动代付</option>
					<option value="5">账户系统-企业</option>
					<option value="6">账户系统-个人</option>
				</select>
			</td>
       </tr>
       <!-- 新增支付渠道 -->
       <tr><td class="th1" bgcolor="#D9DFEE" align="right"  height="30px">支付渠道：</td><td align="left"><select name="gateRouteId"  id="gateRouteId" ><option value="">全部...</option></select></td></tr>
       
       <tr>
       
         <td colspan="6" align="center">
                    <input type="button"  class="button" value="查询" onclick="query(1)"/>
                   <!--  
                    <input type="button"  style="height: 24px;width: 100px;margin-left: 30px" value="检查" onclick="checkBkNo()"/>
    <font color="red">&nbsp;&nbsp;&nbsp;*</font>
    备付金管理上线后，产生商户流水记录，点击检查按钮，系统检查相关配置是否完成
                      -->
                    
                    <input type="hidden" name="search" value="yes"/>
            </td>
       
       
      </tr>
     </table>

    <table id="detailResultList" class="tablelist tablelist2"  style="display:none" >
     <thead>
        <tr valign="middle" class="title2">
          <th sort="false"><input type="checkbox" onclick="allSelected()" id="allSelect" disabled="disabled"/>全选</th><th>商户号</th>
          <th>商户简称</th><th>支付金额</th><th>退款金额</th><th>交易净额</th><th>系统手续费</th><th>退回商户手续费</th><th>手工调增</th><th>手工调减</th>
          <th>清算金额</th><th>结算批次号</th><th>发起日期</th><th>结算状态</th><th sort="false">详细信息</th></tr></thead>
       <tbody id="bodyTable"></tbody>
    </table>
		<input type= "hidden" id = "userAuthIndex" value="<%=request.getParameter("userAuthIndex") %>"/>

     <!------------------------详情表----------------->
    <div id="settlement_detail" style="display: none;" align="center">
    <table id="detailResultList" class="tablelist tablelist2" >
        <tbody>
         <tr  valign="middle" class="title2">
            <th>商户号</th><th>商户简称</th><th>支付金额</th><th>退款金额</th><th>交易净额</th><th>系统手续费</th>
            <th>手工调增</th><th>手工调减</th><th>退回商户手续费</th><th>清算金额</th><th>结算批次号</th></tr>
         <tr><!-- 由于mid ，batch与上面表的重复加了"_"以区分-->
             <td align="center" id="mid_"></td>
             <td align="center" id="name"></td>
             <td align="center" id="payAmt"></td>
             <td align="center" id="refAmt"></td>
             <td align="center" id="tradeAmt"></td>
             <td align="center" id="feeAmt"></td>
             <td align="center" id="manualAdd"></td>
             <td align="center" id="manualSub"></td>
             <td align="center" id="refFee"></td>
             <td align="center" id="liqAmt"></td>
             <td align="center" id="batch_"></td>
         </tr>
         <tr>
         
         <td colspan="11">
         本次结算条件为：<input type="text" value="" id="liq_cond" style="border-style:none;background-color:transparent;color:red" readonly="readonly"/>
                        结算方式：<input type="text" value="" id="liq_type" style="border-style:none;background-color:transparent;color:red" readonly="readonly"/>
                        总共<input type="text" value="" id="count" style="border-style:none;background-color:transparent;color:red;width:20px" readonly="readonly"/>条记录
         </td>
                    </tr>
                </tbody>
            </table>
    <table class="tablelist tablelist2" >
       <tr>
         <th align="center">商户号</th><th align="center">商户简称</th><th align="center">银行名</th>
         <th align="center">交易流水号</th><th align="center">支付金额</th><th align="center">退款金额</th>
         <th align="center">交易净额</th><th align="center">系统手续费</th><th>退回商户手续费</th></tr>
       <tbody id="bodyTable2"></tbody>
    </table>
    <input value="返回" class="button" type="button" onclick="backSettlement();"/>
    </div>
     <!--------------------- 详情表----------------->
   </div>
  </body>
</html>
