<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>手工同步代付结果</title>
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
        <script type="text/javascript" src='../../dwr/interface/SGSyncDFResultService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
        <script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
        <script type="text/javascript" src="../../public/js/adminAccount/admin_sgsyncdf.js?<%=rand%>"></script>
    </head>
    
    <body onload="initB2EGate()">
    <div class="style">
        <form name="MERTLOG"  method="post" action="">
         <input type="hidden"  name="uid" id="uid"  />
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="4">&nbsp;&nbsp;&nbsp;&nbsp;手工同步代付结果&nbsp;&nbsp;</td></tr>
             <tr>
             
                <%-- <input type="hidden"  name="mid" id="mid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}" /> --%>
               <td class="th1" align="right" width="15%">商户号：</td>
                <td align="left" width="35%">
                <input type="text" id="mid" name="mid" style="width: 150px;"  onkeyup="checkMidInput(this);"/>
                  <!-- <select style="width: 150px" id="smid" name="smid"  onchange="initMidInput(this.value);query4ZH();">
                     <option value="">请选择...</option>
                   </select> -->
                 </td><td class="th1" bgcolor="#D9DFEE" align="right" width="15%">批次号：</td><td align="left"  width="35%">
         <input type="text" id="trans_flow" name="trans_flow"   size="20" maxlength="20"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
               <!-- <input class="button" type="button" value = " 查  询 " onclick="queryDaiFaJingBan()"/> -->
               </td>
               
            </tr>
            <tr><td class="th1" bgcolor="#D9DFEE" align="right" >交易类型：</td><td ><select style="width: 150px" id="type" name="type">
                  <option value="0">请选择</option>
                  <option value="11">对私代付</option>
                  <option value="12">对公代付</option>
                </select>  
                </td>
                <td class="th1" align="right" >代付流水号：</td>
                <td><input type="text" id="tseq" name="tseq"   size="20"/></td>
            </tr>
            <tr>
            <td class="th1" bgcolor="#D9DFEE" align="right" >商户状态：</td>
             <td align="left" width="35%">
                     <select style="width: 150px" id="mstate" name="mstate">
                    <option value="">全部...</option>
                </select>
                </td>
               <td  class="th1" align="right">系统日期： </td>
               <td>
             <input id="bdate" size="15px"
                    name="bdate" class="Wdate" type="text" onfocus="ryt_area_date('bdate','edate',0,30,0)" />
                    &nbsp;至&nbsp;
                    <input id="edate" size="15px" name="edate" class="Wdate" type="text" disabled="disabled" />
               </td>
            </tr>
             <tr>
            <td class="th1" bgcolor="#D9DFEE" align="right" >代付状态：</td>
            <td align="left" width="20%"><select style="width: 150px"
						id="state" name="state">
							<option value="0">全部...</option>
							<option value="1">处理中</option>
							<option value="2">交易成功</option>
							<option value="3">交易失败</option>
							<option value="4">请求银行失败</option>
					</select></td>
               <td  class="th1" align="right">支付渠道： </td>
               <td>
            <select style="width: 150px"
						id="gateRouteId" name="gateRouteId">
							<option value="0">全部...</option>
							<!-- <option value="1">处理中</option>
							<option value="2">交易成功</option>
							<option value="3">交易失败</option>
							<option value="4">请求银行失败</option> -->
					</select>
               </td>
            </tr>
         <!--    <tr>
            <td class="th1" align="right">每页显示：</td>
              <td><select id="num">                       
			           <option value = "15">15</option>
			           <option value = "20">20</option>
			           <option value = "30" selected="selected">30</option>
			           <option value = "40">40</option>
			           <option value = "50">50</option>
		           </select>&nbsp;条。</td>
		      <td colspan="2"></td>
            </tr> -->
            <tr>
            	<td colspan="4" align="center">
            		<input class="button" type="button" value = "查询 " onclick="queryDataforSGSYNC(1)"/>
            		<input class="button" type="button" style="width:100px;" value = "下载" onclick="downSGDFData()"/>
            	</td>
            </tr>
        </table>
       </form>
       <table  class="tablelist tablelist2" id="fxpzTable" >
           <thead>
           <tr>
              <th>操作</th><th>代付流水号</th><th>交易类型</th><th>批次号</th><th>商户号</th><th>账户号</th><th>账户名称</th>
		    <th>支付渠道</th> <th>交易金额(元)</th><th>交易手续费(元)</th><th>付款金额(元)</th> <th>代付状态</th><th>收款银行</th><th>收款账户名</th><th>收款账号</th>
           </tr>
           </thead>
           <tbody id="resultList">
           </tbody>
       </table>
          <%@include file="SGDFOption.jsp" %>
      </div>   
    </body>
</html>

           
           
    
