<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>代发审核</title>
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
        <link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css" />
        <script type="text/javascript" src="../../dwr/engine.js?<%=rand %>"></script>
        <script type="text/javascript" src="../../dwr/util.js?<%=rand %>"></script>
        <script type="text/javascript" src="../../public/js/ryt.js?<%=rand %>"></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type="text/javascript" src="../../dwr/interface/DaiFuService.js"></script>
        <script type="text/javascript" src='../../dwr/interface/MerZHService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/AdminZHService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
         <script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
        <script type="text/javascript" src="../../public/js/adminAccount/admin_dbjbsh.js?<%=rand%>"></script>
    </head>
    
    <body onload="initB2EGate()">
    <div class="style">
        <form name="MERTLOG"  method="post" action="">
        <input type="hidden"  name="uid" id="uid"  />
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="8">&nbsp;&nbsp;&nbsp;&nbsp;代发审核&nbsp;&nbsp;</td></tr>
             <tr>             
                <%-- <input type="hidden"  name="mid" id="mid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}" /> --%>
               <td class="th1" align="right" width="20%" colspan="2">商户号：</td>
                <td align="left" width="26%" colspan="2">
                <input type="text" id="mid" name="mid" style="width: 145px;"  onchange="query4ZH();" onkeyup="checkMidInput(this);"/>
                  <!-- <select style="width: 145px" id="smid" name="smid"  onchange="initMidInput(this.value);query4ZH();">
                     <option value="">请选择...</option>
                   </select> -->
                 </td><td class="th1" bgcolor="#D9DFEE" colspan="2" align="right" width="20%">批次号：</td><td align="left" colspan="2">
         <input type="text" id="trans_flow" name="trans_flow"   size="20" maxlength="20"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;       
               <!-- <input class="button" type="button" value = " 查  询 " onclick="queryDaiFaJingBan()"/> -->
               </td>
               
            </tr>
            <tr><td class="th1" bgcolor="#D9DFEE" align="right" width="20%" colspan="2">交易类型：</td><td colspan="2"><select style="width: 150px" id="ptype" name="ptype">
                <option value="0">请选择</option>
                    <option value="5">对公代付</option>
					<option value="7">对私代付</option>
                </select>&nbsp;&nbsp;&nbsp; </td>
                <td class="th1" bgcolor="#D9DFEE" colspan="2" align="right" width="20%">系统订单号：</td><td colspan="2"><input type="text" id="oid"/></td>
                
                </tr>
              <tr>
              <td class="th1" align="right" width="20%" colspan="2">商户状态：</td>
               <td align="left" colspan="2">
                     <select style="width: 150px" id="mstate" name="mstate">
                    <option value="">全部...</option>
                </select>
                </td>
                <td class="th1" bgcolor="#D9DFEE" colspan="2" align="right" width="20%" >系统日期：</td>
                <td><input id="bdate" size="15px"
                    name="bdate" class="Wdate" type="text" onfocus="ryt_area_date('bdate','edate',0,30,0)" />
                    &nbsp;至&nbsp;
                    <input id="edate" size="15px" name="edate" class="Wdate" type="text" disabled="disabled" />
                </td>
              </tr>
                <tr>
            <td class="th1" align="right" colspan="2">每页显示：</td>
              <td colspan="2"><select id="num" >
			           <option value = "15">15</option>
			           <option value = "20">20</option>
			           <option value = "30" selected="selected">30</option>
			           <option value = "40">40</option>
			           <option value = "50">50</option>
		           </select>&nbsp;条。</td>
		      <td  colspan="4"></td>
            </tr> 
                <tr><td colspan="8" align="center"> <input class="button" type="button" value = " 查  询 " onclick="queryDaiFaJingBan(1)"/>
                 <input class="button" type="button" value = " 下 载" onclick="downqueryDaiFaJingBansh()"/>
                </td></tr>
        </table>
       </form><%@include file="JBSHOption.jsp" %>
       <table  class="tablelist tablelist2" id="fxpzTable"  style="display: none;">
           <thead>
           <tr>
              <th>操作</th><th>系统订单号</th><th>交易类型</th><th>批次号</th><!-- <th>支付银行</th> --><th>用户</th><th>账户ID</th><th>账户名称</th>
		    <th>交易金额(元)</th><th>交易手续费(元)</th><th>付款金额(元)</th><th>交易银行</th><th>户名</th><th>账户</th>
           </tr>
           </thead>
           <tbody id="resultList">
           </tbody>
       </table>
      </div>   
    </body>
</html>

           
           
    
