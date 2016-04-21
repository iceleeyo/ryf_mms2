<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib  uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>划款结果处理</title>
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
        <script type="text/javascript" src="../../dwr/interface/PageService.js?<%=rand %>"></script>
        <script type="text/javascript" src="../../dwr/interface/AutoSettlementService.js?<%=rand %>"></script>
		<script type="text/javascript" src="../../public/datePicker/WdatePicker.js"></script>
		<script type="text/javascript" src="../../public/js/ryt.js?<%=rand%>"></script>
		<script type="text/javascript">
		var pay_state={5:"处理中", 6:"失败"};
		function initOption(){
		   initMinfos();
		   dwr.util.addOptions("payState",pay_state);
		   document.getElementById("payState").options[3].selected=true;
		}
		var bdate,edate,batch,mid,payState;
		function checkSubmit(){
		   	 bdate = document.getElementById("bdate").value;
			 edate = document.getElementById("edate").value;
			 batch = document.getElementById("batch").value.trim();
			 mid = document.getElementById("mid").value;
			 payState = document.getElementById("payState").value;
			 if(mid !='' && !isFigure(mid)){
					alert("商户号只能是整数!");
					document.getElementById("mid").value = '';
					return false; 
			 }
			if (bdate == '' || edate == '') {
				alert("请选择划款确认日期!");
				return false;
			}
			if (!isFigure(batch.trim())) {
				alert("批次号只能为数字，请重新输入！");
				   document.getElementById("batch").value = '';
		   		   document.getElementById("batch").focus();
				return false;
			}
			return true;
		}
		//下载
		function downLoadTransferResult(){
			if(!checkSubmit())return;
			dwr.engine.setAsync(false);
			AutoSettlementService.downLoadResultHandle(mid ,payState ,bdate, edate ,batch,
							function(data){dwr.engine.openInDownload(data);});
			
		}
	
		// 查询
		function queryTransferResult(pageNo){
			if(!checkSubmit())return;
			AutoSettlementService.queryResultHandle(pageNo, mid ,payState ,bdate, edate ,batch,callbackList);
		}
		function callbackList(dataList){
		    dwr.util.removeAllRows("resultList");
		    document.getElementById("resultHandleTable").style.display ="";
			var cellFuncs = [										 
			                 function(obj) {  
									if(obj.state==5) return "";

									return "<input type='checkbox'  name='slogIds' value='"+obj.oid + "'/>";},
			                 function(obj) { return obj.oid; },
			                 function(obj) { return obj.tseq; },
			                 function(obj) { return obj.gate },
			                 function(obj) { return obj.uid; },
			                 function(obj) { return obj.toAccName; },
			                 function(obj) { return obj.toAccNo; },
			                 function(obj) { return obj.toBkNo; },
			                 function(obj) { return obj.toBkName; },
			                 function(obj) { return div100(obj.payAmt); },
			                 function(obj) { return obj.orgOid; },
			                 function(obj) { return pay_state[obj.state]},//obj.liqState
			                 function(obj) { return obj.operDate; }
			              ]
			var str="<span  style='float:left'>&nbsp;&nbsp;&nbsp;&nbsp;<input value='反选' type='button' onclick='checkReverseByName(\"slogIds\")'/>"+
			        "&nbsp;&nbsp;&nbsp;&nbsp;<input type='button' onclick='transSure()' value=' 划款录入处理 ' id='commitButton'/></span>";//
			 paginationTable(dataList,"resultList",cellFuncs,str,"queryTransferResult");
		}
		//划款确认
		function transSure(){
		     var slogIdArr=getValuesByName("slogIds");
	      	 if(slogIdArr.length<1){
	      		 alert("请选择至少一条记录！");
	      		 return false;
	      	 }
	      	if(!confirm("你确认划款？"))return;
		    AutoSettlementService.transferMoneySure(slogIdArr,function(msg){
		      if(msg=="ok"){
		         alert("划款成功！");
		      }else{
		       alert(msg);
		      }
		      queryTransferResult(document.getElementById("pageChoose").value);
		    });
		}
		</script>
        
</head>
<body onload="initOption();">
<div class="style">
<input type="hidden" name="operId" id="operId" value="${sessionScope.SESSION_LOGGED_ON_USER.operId}"/>
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="4">&nbsp;&nbsp;&nbsp;&nbsp;划款结果处理</td></tr>
            <tr>
               <td class="th1" align="right" width="10%">商户号：</td>
                <td align="left" width="40%">
                <input type="text" id="mid" name="mid" size="8px" onkeyup="checkMidInput(this);"/>
                  <!-- <select style="width: 150px" id="smid" name="smid" onchange="initMidInput(this.value)">
                     <option value="">全部...</option>
                   </select> -->
                 </td>
                 <td class="th1" align="right" width="10%">划款状态：</td>
                   <td align="left" >
                  <select style="width: 120px" id="payState" name="payState" >
                   </select>
                 </td>
            </tr>
            <tr>
	            <td class="th1" align="right" >划款确认日期：</td>
                 <td align="left" >
                    <input id="bdate" size="15px"
                    name="bdate" class="Wdate" type="text" onfocus="ryt_area_date('bdate','edate',0,30,0)" />
                    &nbsp;至&nbsp;
                    <input id="edate" size="15px" name="edate" class="Wdate" type="text" disabled="disabled" /><font color="red">*</font>
                 </td>
                 <td class="th1" align="right" >批次号：</td>
                 <td align="left" >
                    <input id="batch" size="15px" name="batch"  type="text"  />
                 </td>
            </tr>
            <tr>
            <td colspan="4" align="center" style="height: 30px">
                <input class="button" type="button" value = "查 询 " onclick="queryTransferResult(1);" />&nbsp;&nbsp;&nbsp;&nbsp; 
                <input class="button" type="button" value = "下载 " onclick="downLoadTransferResult();" />
            </td>
            </tr>
        </table>
     <table width="100%" class="tablelist tablelist2" id="resultHandleTable" style="display:none;">
       <thead> <tr valign="middle" class="title2">
            <th align="center" sort="false">选择</th>
            
            <th>电银流水号</th>
             <th>银行流水号</th>
            <th>付款银行</th>
            <th>商户号</th>
            <th>收款方开户名</th>
            <th>收款方账号</th>
            <th>收款方联行号</th> 
            <th>收款方银行</th>
            <th>清算金额</th>
            <th>结算批次号</th>
            <th sort="false">划款状态</th>
            <th>划款确认日期</th>
            
        </tr></thead>
        <tbody id="resultList"></tbody>   
     </table>
</div>
</body>
</html>
