<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" errorPage="../../error.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>手续费查询</title>
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
        <script type='text/javascript' src='../../dwr/engine.js?<%=rand %>'></script>
        <script type='text/javascript' src='../../dwr/util.js?<%=rand %>'></script>
       	<script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand %>'></script>
		<script type="text/javascript" src='../../dwr/interface/MerchantService.js?<%=rand %>'></script>
       	<script type="text/javascript" src='../../dwr/interface/RypCommon.js?<%=rand %>'></script>
        <script type="text/javascript" src='../../public/js/ryt.js?<%=rand %>'></script>
		<script type="text/javascript">
		    function feeSearch(){
		    	var mid = document.getElementById("mid").value;
		    	var bank = document.getElementById("bank").value;
		        if(mid == ''||mid==0 ){alert("请选择商户"); return false; }
		        if(!isFigure(mid)){ alert("商户号只能是整数!"); return false;}
		        MerchantService.searchGatesFee(mid, bank,function(bankList){
		        	document.getElementById("bankfee").style.display="";
		        	dwr.util.removeAllRows("bankfeeBody");
		            if(bankList==null||bankList.length==0){
		    	       document.getElementById("bankfeeBody").appendChild(creatNoRecordTr(5));
		    		   return;
		            }
			         var cellfuncs = [
			                         function(obj) { return obj.mid; },
			                         function(obj) { return m_minfos[obj.mid]; },
			                         function(obj) { return h_gate[obj.gate]; },
			                         function(obj) { return obj.calcMode; }
	                     			]
	              dwr.util.addRows("bankfeeBody", bankList, cellfuncs);
		        })
		    }

		  	var h_gate = {};
		    function initOption(){
		  	PageService.getGatesMap(function(map){
				h_gate = map;
				dwr.util.addOptions("bank", h_gate);
			    initMinfos();
			});
		   }
		</script>
	</head>

	<body onload="initOption();">
	
	  <div class="style">
			<table class="tableBorder">
				<tbody>
					<tr>
						<td class="title" colspan="4">
							&nbsp;&nbsp; 手续费查询&nbsp;&nbsp;(
							<font color="red">*</font>银行名称M:信用卡支付,W:wap支付,C:充值卡支付,I:语音/快捷支付,其余:网银支付)
						</td>
					</tr>
					<tr><td class="th1" align="right" width="20%">&nbsp;商户号：</td>
						<td align="left" id="selectMerTypeId">&nbsp;<input type="text" id="mid" name="mid"   size="8px" onkeyup="checkMidInput(this);" value=""/>
                  <!-- <select style="width: 150px" id="smid" name="smid" onchange="initMidInput(this.value)">
                     <option value="">全部...</option>
                   </select> --><font color="red">*必选项</font>
						</td>
						<td class="th1" align="right" width="20%">&nbsp;银行：</td>
						<td align="left">&nbsp;<select name="bank" id="bank"><option value="0">全部...</option></select></td>
					</tr>
					<tr>
						<td colspan="4" align="center">
							<input name="search" value="yes" type="hidden"/>
							<input name="userAuthIndex" value="" type="hidden"/>
							<input type="submit" value="查  询" class="button" onclick="feeSearch();"/>
						</td>
					</tr>
				</tbody>
			</table>
		<table class="tablelist tablelist2" id="bankfee" style="display:none;">
			<thead>
				<tr><th align="center">商户号</th>
					<th align="center">商户简称</th>
					<th align="center">银行</th>
					<th align="center">手续费计算公式</th>
					<!-- <th align="center">状态</th> -->
				</tr></thead>
			<tbody id="bankfeeBody">	
			</tbody>
		</table>
		
		</div>
	</body>
</html>
