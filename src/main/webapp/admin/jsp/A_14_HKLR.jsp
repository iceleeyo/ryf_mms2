<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>划款录入</title>
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
        <link href="../../public/css/head.css?<%=rand %>" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="../../dwr/engine.js"></script>
        <script type="text/javascript" src="../../dwr/util.js"></script>
        <script type="text/javascript" src="../../dwr/interface/AutoSettlementService.js?<%=rand %>"></script>
        <script type="text/javascript" src="../../dwr/interface/PageService.js?<%=rand %>"></script>
		<script type="text/javascript" src="../../public/datePicker/WdatePicker.js?<%=rand %>"></script>
		<script type="text/javascript" src="../../public/js/ryt.js?<%=rand%>"></script>
		<script type="text/javascript">
   // var m_minfos={};
	function querySettlement(pageNo) {
		var bdate = document.getElementById("bdate").value;
		var edate = document.getElementById("edate").value;
		var batch = document.getElementById("batch").value;
		var mid = document.getElementById("mid").value;
		 if(mid !='' && !isFigure(mid)){
				alert("商户号只能是整数!");
				$("mid").value = '';
				return false; 
		 }
		if (bdate == '' || edate == '') {
			alert("请选择结算发起日期!");
			return false;
		}
		if (!isFigure(batch.trim())) {
			alert("批次号只能为数字，请重新输入！");
			   document.getElementById("batch").value = '';
	   		   document.getElementById("batch").focus();
			return false;
		}
			AutoSettlementService.getTransferData(pageNo, bdate, edate, mid, batch,resultData);
		
	}
	function resultData(dataList){
	    dwr.util.removeAllRows("resultList");
	    document.getElementById("transferInputTable").style.display ="";
		var cellFuncs = [										 
		                 function(obj) { return  obj.state==2?"<input type='checkbox' id='toCheck' name='batchs' value='"+obj.batch + "'/>":"";},
		                 function(obj) { return obj.mid; },
		                 function(obj) { return  m_minfos[obj.mid]; },
		                 function(obj) { return div100(obj.liqAmt); },
		                 function(obj) { return obj.batch; },
		                 function(obj) { return obj.state==2?"已制表":"已发起";},
		                 function(obj) { return obj.liqDate; }
		              ]
		var str="<span  style='float:left'>&nbsp;&nbsp;&nbsp;&nbsp;<input value='全选' type='button' onclick='checkReverseByName(\"batchs\")'/>"+
		        "&nbsp;&nbsp;&nbsp;&nbsp;"+
		        
		        "请选择付款银行：<select id='b2egid'><option value='40000'>交通银行(银企直连)</option>"+
	            "<option value='40001'>中国银行(银企直连)</option></select>"+
		        "&nbsp;&nbsp;&nbsp;&nbsp;<input type='button' onclick='transferInput()' value=' 划款录入 ' id='commitButton'/></span>";//
		 paginationTable(dataList,"resultList",cellFuncs,str,"querySettlement");
	}
	//划款录入
	function transferInput(){
         var batchArr=getValuesByName("batchs");
      	 if(batchArr.length<1){
      		 alert("请选择至少一条记录！");
      		 return false;
      	 }
      	var gid = dwr.util.getValue('b2egid');
        if(!confirm("你确认划款录入？")) return;
	    AutoSettlementService.transferMoneyInput(batchArr,gid,function(msg){
	      if(msg=="ok"){
	         alert("划款录入成功！");
	      }else{
	         alert(msg);
	      }
	      querySettlement(document.getElementById("pageChoose").value);
	    });
	}
		</script>
        
</head>
<body onload="initMinfos();">
<div class="style">
<input type="hidden" name="operId" id="operId" value="${sessionScope.SESSION_LOGGED_ON_USER.operId}"/>
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="6">&nbsp;&nbsp;&nbsp;&nbsp;划款录入</td></tr>
            <tr>
            <td class="th1" align="right" width="10%">商户号：</td>
                <td align="left" >
                <input type="text" id="mid" name="mid"   size="8px" onkeyup="checkMidInput(this);"/>
                 <!--  <select style="width: 150px" id="smid" name="smid" onchange="initMidInput(this.value)">
                     <option value="">全部...</option>
                   </select> -->
                 </td>
                  <td class="th1" align="right" width="10%">结算发起日期：</td>
                 <td align="left" >
                    <input id="bdate" size="15px"
                    name="bdate" class="Wdate" type="text" onfocus="ryt_area_date('bdate','edate',0,30,0)" />
                    &nbsp;至&nbsp;
                    <input id="edate" size="15px" name="edate" class="Wdate" type="text" disabled="disabled" /><font color="red">*</font>
                 </td>
                 <td class="th1" align="right" width="10%">批次号：</td>
                 <td align="left" >
                    <input id="batch" size="15px" name="batch"  type="text" maxlength="12" />
                 </td>
            </tr>
            <tr>
            <td colspan="6" align="center" style="height: 30px">
            
            
            
            
            
                <input class="button" type="button" value = "查 询 " onclick="querySettlement(1);" />&nbsp; 
            </td>
            </tr>
        </table>
     <table width="100%" class="tablelist tablelist2" id="transferInputTable" style="display:none;">
       <thead> <tr valign="middle" class="title2">
            <th align="center" sort="false">选择</th>
            <th>商户号</th>
            <th>商户名</th>
            <th>清算金额</th>
            <th>结算批次号</th>
            <th>结算状态</th>
            <th>发起日期</th>
        </tr></thead>
        <tbody id="resultList"></tbody>   
     </table>
</div>
</body>
</html>
