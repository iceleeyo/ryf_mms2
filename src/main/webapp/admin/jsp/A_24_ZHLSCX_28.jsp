<%@ page language="java" pageEncoding="UTF-8" %>
<% String path = request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>账户流水查询(2.8新)</title>
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
        <script type="text/javascript" src="../../dwr/engine.js?<%=rand %>"></script>
        <script type="text/javascript" src="../../dwr/util.js?<%=rand %>"></script>
        <script type='text/javascript' src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/AdminZHService.js?<%=rand%>'></script> 
        <script type='text/javascript' src='../../public/datePicker/WdatePicker.js?<%=rand%>'></script>
        <script type="text/javascript" src="../../public/js/adminAccount/admin_zhjylscx.js?<%=rand%>"></script>
 		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>'></script>

    </head>
    <body onload="init();">
    <div class="style">
        <form name="MERTLOG"  method="post" action="">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="6">&nbsp;&nbsp;&nbsp;&nbsp;账户流水查询(2.8新)&nbsp;&nbsp;</td>
            </tr>
            
            <tr>
                <td class="th1" align="right" width="10%" >商户号：</td>
                <td align="left" width="30%">
               		<input type="text" id="mid" name="mid"  style="width:150px" size="8px" onkeyup="checkMidInput(this);"/>&nbsp;
                       <!-- <select style="width: 150px" id="smid" name="smid" onchange="initMidInput(this.value)">
                          <option value="">全部...</option>
                      </select> --> 
                </td>
                 
                <td class="th1" bgcolor="#D9DFEE" align="right" width="10%">账户号：</td>
                <td align="left" width="25%">
                	<input type="text" id="aid" name="aid"  maxlength="20"/>
                </td>
                 <td class="th1" bgcolor="#D9DFEE" align="right" width="11%">商户状态：</td>
                <td align="left" width="20%">
                <select style="width: 150px" id="mstate" name="mstate">
                    <option value="">全部...</option>
                </select>
                </td>
            </tr>
            
            <tr>
               <td class="th1" align="right" width="10%">交易日期：</td>
                <td align="left" >
                    <input id="bdate" size="15px"
                    name="bdate" class="Wdate" type="text" onfocus="ryt_area_date('bdate','edate',0,30,0)" />
                    &nbsp;至&nbsp;
                    <input id="edate" size="15px" name="edate" class="Wdate" type="text" disabled="disabled" /><font color="red">*</font>
                </td>
                 <td class="th1" align="right" width="10%">电银流水号：</td>
                <td align="left" width="25%">
                <input type="text" id="tseq" name="tseq"/>
                </td>
                 <td class="th1" bgcolor="#D9DFEE" align="right" width="11%">商户类别：</td>
                <td align="left" width="20%">
                	<select style="width: 150px" id="category" name="category">
						<option value="">全部...</option>
					</select>
                </td>
            </tr>
            <tr>
            <td colspan="6" align="center" style="height: 30px">
            <font color="red">*</font>
              2.8版本以前 查询不到的流水 请在  
    <a href="A_24_ZHLSCX.jsp">          
             账户流水查询(2.8以前)</a>中 进行查询
               
               &nbsp;&nbsp;
                <input class="button" type="button" value = " 查 询 " onclick="queryLS(1);" />
                <input class="button" type="button" value= " 下载XLS " onclick = "downloadLS();"/>
            </td>
            </tr>
        </table>
       </form>

       <table  class="tablelist tablelist2" id="LSTable" style="display: none;">
           
           <tr>
           	 <th>用户</th><th>账户号</th><th>系统日期</th><th>系统时间</th>
             <th>收支标识</th><th>交易金额(元)</th><th>交易手续费(元)</th> <th>变动金额(元)</th><th>可用金额(元)</th> <th>余额(元)</th><th>电银流水号</th><th>流水来源表</th><th>简短说明</th>
           </tr>
           
           <tbody id="resultList">
           </tbody>
       </table>
        </div>
    </body>
</html>
