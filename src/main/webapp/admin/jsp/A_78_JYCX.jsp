<%@ page language="java" pageEncoding="UTF-8" %>
<% String path = request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>交易查询</title>
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
		var bk_no_map={};
		function initOption(){
			dwr.util.addOptions("transType",trans_type_map);
			dwr.util.addOptions("transState",trans_state_map);
			PrepPayService.getBkNoMap(function(data){
            		bk_no_map=data; 
            		dwr.util.addOptions("bkNo", data);
            });
		}
		function queryTrans(pageNo){
		  var dateType=$("dateType").value;
		  var bdate=$("bdate").value;
		  var edate=$("edate").value;
		  var bkNo=$("bkNo").value;
		  var transType=$("transType").value;
		  var transState=$("transState").value;
		  if(bdate==""||edate==""){
		     alert("请选择起始日期和结束日期！");
		     return false;
		  }
		  PrepPayService.queryTransPage(pageNo,dateType,bdate,edate,bkNo,transType,transState,callbackFun);
		}
					//回调函数
		function callbackFun(pageObj){ 
	    	$("transListTable").style.display="";
	    	 dwr.util.removeAllRows("transList");
	    	 //交易申请日期，交易金额，交易类型，交易银行，银行退回手续费，交易处理日期，交易状态，交易申请原因
		     var cellFuncs =[
		                function(obj) { return obj.id; },
		                function(obj) { return obj.addDate; },
		                function(obj) { return div100(obj.trAmt); },
		                function(obj) { return trans_type_map[obj.trType]; },
		                function(obj) { return bk_no_map[obj.bkNo]; },
		                function(obj) { return div100(obj.bkRefFee); },
		                function(obj) { return obj.valiDate; },
		                function(obj) { return trans_state_map[obj.trState]; },
		                function(obj) { return createTip(8,obj.remark); }
		                ]
		           paginationTable(pageObj,"transList",cellFuncs,"","queryTrans");
	    }
		</script>

    </head>
    <body onload="initOption();">
    <div class="style">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="8">&nbsp;&nbsp;&nbsp;&nbsp;备份金银行交易查询(差错退款/归集/银行调账)</td></tr>
            <tr>
                <td class="th1" align="right" width="15%">
                	<select id="dateType"><option value="1">申请日期</option><option value="2">处理日期</option></select>
                </td>
                <td align="left" width="35%">
                    <input id="bdate" size="15px"
                    name="bdate" class="Wdate" type="text" onfocus="ryt_area_date('bdate','edate',0,30,0)" />
                    &nbsp;至&nbsp;
                    <input id="edate" size="15px" name="edate" class="Wdate" type="text" disabled="disabled" /><font color="red">*</font>
                </td>
                <td class="th1" bgcolor="#D9DFEE" align="right" width="15%">银行：</td>
                <td align="left" width="*%">
                <select style="width: 150px" id="bkNo" name="bkNo">
                    <option value="">全部...</option>
                </select>
                </td>
            </tr>
            <tr>
               <td class="th1" bgcolor="#D9DFEE" align="right">交易类型：</td>
                <td align="left">
                <select style="width: 150px" id="transType" name="transType">
                    <option value="">全部...</option>
                </select>
                </td>
                <td class="th1" bgcolor="#D9DFEE" align="right" >交易状态：</td>
                <td align="left">
                <select style="width: 150px" id="transState" name="transState">
                    <option value="">全部...</option>
                </select>
                </td>
            </tr>
            <tr>
            <td colspan="4" align="center" style="height: 30px">
                <input class="button" type="button" value = " 查 询 " onclick="queryTrans(1);" />
            </td>
            </tr>
        </table>

       <table  class="tablelist tablelist2" id="transListTable" style="display:none;" >
           <thead>
           <tr>
             <th>电银流水号</th><th>交易申请日期</th><th>交易金额</th>
             <th>交易类型</th><th>交易银行</th>
             <th>银行退回手续费</th><th>交易处理日期</th>
             <th>交易状态</th><th>交易申请原因</th>
           </tr>
           </thead>
           <tbody id="transList"></tbody>
       </table>
       

       <table id="hlogDetail"  class="tableBorder detailBox" style="display: none;">
           <tr>
               <td align="left" class="th1" width="10%" >商户日期：</td>
               <td align="left" width="15%" id="v_mdate"></td>
               <td align="left" class="th1" width="8%" >商户号：</td>
               <td align="left" width="15%" id="v_mid"></td>
               <td align="left" class="th1" width="8%">商户简称：</td>
               <td align="left" width="15%" id="v_midName"></td>
           </tr>
           <tr><td colspan="6" height="30px"  align="center"> <input type="button"  value = "返回" class="wBox_close button"/></td></tr>
        </table>
        </div>
    </body>
</html>
