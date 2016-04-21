<%@ page language="java" pageEncoding="UTF-8" %>
<% String path = request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>商户流水查询</title>
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
        <script type="text/javascript" src="../../dwr/engine.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../dwr/util.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../dwr/interface/PageService.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../dwr/interface/PrepPayService.js?<%=rand%>"></script>
		<script type="text/javascript" src="../../public/datePicker/WdatePicker.js?<%=rand%>"></script>
		<script type="text/javascript" src="../../public/js/ryt.js?<%=rand%>"></script>
		<script type="text/javascript">

			var trans_type={0:"支付",1:"支付手续费",2:"退款",3:"退款退回手续费",4:"调增",5:"调减",6:"结算"};
			function queryAccountDetail(pageNo){
			   var mid=$("mid").value;
			   var bdate=$("bdate").value;
			   var edate=$("edate").value;
			   if(bdate==""||edate==""){
			       alert("请选择起始日期和结束日期！");
			      return false;
			   }
			   if(pageNo==-1){
			       dwr.engine.setAsync(false);
			       PrepPayService.downloadAccountDetail( mid, bdate, edate,function(data){dwr.engine.openInDownload(data);});
			   }else{
			       PrepPayService.queryAccountDetail( pageNo, mid, bdate, edate,callbackFun);
			   }
			}
			//回调函数
			function callbackFun(pageObj){ 
	    	   $("transAcountTable").style.display="";
	    	   dwr.util.removeAllRows("transAcountList");
		             var cellFuncs = [
		                         function(obj) { return obj.trFlag; },
		                         function(obj) { return obj.objId; },
		                         function(obj) { return m_minfos[obj.objId]; },
		                         function(obj) { return obj.trDate+" "+getStringTime(obj.trTime); },
		                         function(obj) { return trans_type[obj.trType]; },
		                         function(obj) { return div100(obj.trAmt); }
		                     ]
		              paginationTable(pageObj,"transAcountList",cellFuncs,"","queryAccountDetail");
	          }
		</script>
    </head>
    <body onload="initMinfos();">
    <div class="style">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="8">&nbsp;&nbsp;&nbsp;&nbsp;商户流水查询</td></tr>
            <tr>
                <td class="th1" align="right" width="10%">商户号：</td>
                <td align="left" width="25%">
                <input type="text" id="mid" name="mid" style="width: 50px;" maxlength="6"  onkeyup="checkMidInput(this);"/>
                  <select style="width: 150px" id="smid" name="smid" onchange="initMidInput(this.value)">
                     <option value="">全部...</option>
                   </select>
                 </td>
                <td class="th1" align="right" width="20%">交易日期：</td>
                <td align="left" >
                    <input id="bdate" size="15px"
                    name="bdate" class="Wdate" type="text" onfocus="ryt_area_date('bdate','edate',0,30,0)" />
                    &nbsp;至&nbsp;
                    <input id="edate" size="15px" name="edate" class="Wdate" type="text" disabled="disabled" /><font color="red">*</font>
                </td>
            </tr>
            <tr>
            <td colspan="8" align="center" style="height: 30px">
                <input class="button" type="button" value = " 查 询 " onclick="queryAccountDetail(1);" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                 <input class="button" type="button" value = " 下载 " onclick="queryAccountDetail(-1);" />
            </td>
            </tr>
        </table>

       <table  class="tablelist tablelist2" id="transAcountTable" style="display:none;" >
           <thead>
           <tr>
             <th>操作标识符</th><th>商户号</th><th>商户简称</th>
             <th>商户日期</th><th>操作类型</th><th>交易金额</th>
             
           </tr>
           </thead>
           <tbody id="transAcountList"></tbody>
       </table>
        </div>
    </body>
</html>
