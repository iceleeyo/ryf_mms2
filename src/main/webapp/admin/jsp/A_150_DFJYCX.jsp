<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>代付交易查询</title>
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
        <script type="text/javascript" src="../../dwr/interface/DfService.js"></script>
        <script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
        <script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
        <script type="text/javascript" src="../../public/js/transaction/admin_daifu.js?<%=rand%>"></script>
    </head>
    
    <body>
    <div class="style">
        <form id="queryForm" method="post" action="">
         <table width="100%" align="left" class="tableBorder">
            <tr>
            	<td class="title" colspan="6">&nbsp;&nbsp;&nbsp;&nbsp;代付交易查询&nbsp;&nbsp;</td>
            </tr>
            <tr>
            	<td class="th1" align="right">账户号：</td>
            	<td><input id="accountId" name="" type="text" maxlength="20"/></td>
            	<td class="th1" align="right">交易银行：</td>
            	<td>
					<select id="gate">
						<option value="">全部...</option>
					</select>
				</td>
            	<td class="th1" align="right">代付渠道：</td>
            	<td>
					<select id="gid">
						<option value="">全部...</option>
					</select>
				</td>
            </tr>
            <tr>
               <td class="th1" align="right">系统日期： </td>
               <td>
            	 <input id="bdate" size="15px" name="bdate" class="Wdate" type="text" onfocus="ryt_area_date('bdate','edate',0,30,0)" />
                    &nbsp;至&nbsp;
                 <input id="edate" size="15px" name="edate" class="Wdate" type="text" disabled="disabled" />
                 &nbsp;<font color="red">*</font>
               </td>
               <td class="th1" align="right">交易状态：</td>
               <td>
               		<select id="tstat">
               			<option value="">全部...</option>
               		</select>
               </td>
               <td class="th1" align="right">交易类型：</td>
               <td>
               		<select id="type">
               			<option value="">全部...</option>
               		</select>
               </td>
            </tr>
            <tr>
            	<td class="th1" align="right">数据来源：</td>
            	<td>
               		<select id="dataSource">
               			<option value="">全部...</option>
               			<option value="1">支付系统</option>
               			<option value="2">账户系统</option>
               			<option value="3">清算系统</option>
               			<option value="4">资金托管系统</option>
               			<option value="5">POS系统</option>
               			<option value="6">新账户系统</option>
               		</select>
         		</td>
            	<td class="th1" align="right">电银流水号：</td>
            	<td><input id="tseq" name="" type="text" maxlength="20"/></td>
            	<td class="th1" align="right">订单号：</td>
            	<td><input id="orderId" name="" type="text" maxlength="20"/></td>
            </tr>
            <tr>
            	<td colspan="6" align="center">
            		<input class="button" type="button" value = "查询" onclick="queryForPage(1)"/>
            		<input class="button" type="button" style="width:100px;" value = "下载XLS" onclick="downloadXls(0)"/>
            	</td>
            </tr>
        </table>
       </form>
       <table class="tablelist tablelist2" id="dfjyTable" style="display: none;" >
           <thead>
           <tr>
             <th>电银流水号</th><th>账户号</th><th>订单号</th><th>交易金额(元)</th><th>交易状态</th><th>交易类型</th>
             <th>交易银行</th><th>代付渠道</th><th>数据来源</th><th>系统时间</th><th>银行流水号</th><th>失败原因</th>
           </tr>
           </thead>
           <tbody id="resultList">
           </tbody>
       </table>
      </div>   
	<table id="pupUpTable" class="tableBorder" style="display:none; width: 100%; height: auto; margin-top: 0px;white-space: nowrap;">
	</table>
    </body>
</html>
