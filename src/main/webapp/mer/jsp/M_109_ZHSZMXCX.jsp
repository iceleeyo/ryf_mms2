<%@ page language="java" pageEncoding="UTF-8" %>
<% String path = request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>账户收支明细查询</title>
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
        <link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css"/>
		<script type="text/javascript" src="../../dwr/engine.js?<%=rand %>"></script>
        <script type="text/javascript" src="../../dwr/util.js?<%=rand %>"></script>
        <script type="text/javascript" src='../../dwr/interface/AdminZHService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/MerZHService.js?<%=rand%>'></script> 
        <script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
        <script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
        <script type="text/javascript" src="../../public/js/merchant/mer_zhzfmxcx.js?<%=rand%>"></script>
 		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>
 		<script type='text/javascript' src='../../public/js/ryt_util.js?<%=rand%>'></script>
 		<script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
    </head>
    <body onload="init();">
    <div class="style">
		<form name="MERTLOG" method="post" action="">
			<table width="100%" align="left" class="tableBorder">
				<tr>
					<td class="title" colspan="4">&nbsp;&nbsp;&nbsp;&nbsp;账户收支明细查询&nbsp;&nbsp;
					</td>
				</tr>
				<tr>
					<!-- <td class="th1" align="right" width="15%">账户号：</td>
					<td align="left" width="35%"><select
						style="width: 150px" id="aid" name="aid">
							<!--   <option value="">全部...</option>\-\->
					</select></td>-->
					<td class="th1" bgcolor="#D9DFEE" align="right" width="15%">交易类型：</td>
					<td align="left" width="35%">
						<select style="width: 150px" id="ptype" name="ptype">
							<option value="">请选择...</option>
						</select>
					</td>
					<td class="th1" bgcolor="#D9DFEE" align="right" width="15%">系统订单号：</td>
					<td align="left"><input type="text" name="oid" id="oid"
						maxlength="30" />
					</td>
				</tr>
				<tr>
					<td class="th1" align="right" width="15%">交易日期：</td>
					<td align="left"><input id="bdate" size="15px" name="bdate"
						class="Wdate" type="text"
						onfocus="ryt_area_date('bdate','edate',0,30,0)" /> &nbsp;至&nbsp;
						<input id="edate" size="15px" name="edate" class="Wdate"
						type="text" disabled="disabled" /><font color="red">*</font></td>
					<td class="th1" bgcolor="#D9DFEE" align="right" width="15%"></td>
					<td align="left" width="35%">
					</td>
				</tr>
				<tr>
					<td colspan="4" align="center" style="height: 30px"><input
						class="button" type="button" value=" 查 询 " onclick="queryMX(1);" />
						<input class="button" type="button" value=" 下载XLS "
						onclick="downloadMX();" /></td>
				</tr>
			</table>
		</form>

		<table  class="tablelist tablelist2" id="zhjymxcxTable" style="display: none;">
           <thead>
           <tr>
           <th>用户号</th><th>账户号</th><th>订单号</th><th>批次号</th><th>交易时间</th><th>交易类型</th><th>收入(元)</th><th>支出(元)</th>
             <th>账户余额(元)</th>
           
           </tr>
           </thead>
           <tbody id="resultList">
           </tbody>
       </table>
        <%@include file="ZHJXCHDetail.jsp" %>
       <table id="remarkDetail"  class="tableBorder detailBox" style="display: none;">
          <!--  <tr><td class="title" colspan="6" align="center">商户订单详细资料</td></tr> -->
           <tr>
               <td align="right" class="th1" width="10%" >说明描述：</td>
               <td align="left" width="15%" >
               <textarea rows="5" cols="90" id="i_remark" disabled="disabled" ></textarea>
               </td>
           </tr>
           
           </tr>
           <tr><td colspan="6" height="30px"  align="center"> <input type="button"  value = "返回" class="wBox_close button"/>
			<!-- <input type="button" onclick="fanhui()" value = "返  回" class="button"/>--><br/></td></tr>
        </table>

        </div>
    </body>
</html>
