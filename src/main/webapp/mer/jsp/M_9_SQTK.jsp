<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java"  errorPage="../../error.jsp" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>退款申请</title>
<%
String path = request.getContextPath();
response.setHeader("Pragma","No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", 0);
int rand = new java.util.Random().nextInt(10000);
%>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
    <META http-equiv="Expires" content="0">  
        <link href="../../public/css/head.css?<%=rand %>" rel="stylesheet" type="text/css">
<script type='text/javascript' src='../../dwr/engine.js'></script>
<script type='text/javascript' src='../../dwr/util.js'></script>
<script type='text/javascript' src='../../dwr/interface/PageService.js?<%=rand%>'></script>
<script type='text/javascript' src='../../dwr/interface/CommonService.js?<%=rand%>'></script>
<script type='text/javascript' src='../../dwr/interface/MerRefundmentService.js?<%=rand%>'></script>
<script type="text/javascript" src="../../public/datePicker/WdatePicker.js"></script>
<script type="text/javascript" src="../../public/js/refundment/mer_jsp_refund_apply.js?<%=rand%>"></script>
<SCRIPT type=text/javascript src="../../public/js/ryt.js?<%=rand%>"></SCRIPT>  

<script type="text/javascript" >
			(function($) { 
				 $(function(){ 
				     $("#searchType2").change(function (){//选择类型时
			             if($("#searchType2").val()==1){
			                $("#typeName2").text("电银流水号：");
			                $("#tseq").show();
			                $("#oid").hide();
			                $("#tkdate").hide();
			             }
			             else{
			               $("#typeName2").text("商户订单号：");
			               $("#oid").show();
			               $("#tkdate").show();
			               $("#tseq").hide();
			               
			             }
					});
					$("#searchButt").click(function(){//点击查询时
					   query4OneHlog2($("#searchType2").val());
					});
					
				  });   
			})(jQuery);
</script>
</head>
<body>


<div class="style"><input value="${sessionScope.SESSION_LOGGED_ON_USER.mid }" id="mid" name="mid" type="hidden"/>
<table width="100%"  align="left" class="tableBorder">
<tr ><td class="title" colspan="2">&nbsp;&nbsp;&nbsp;单笔申请退款</td></tr>
       <tr><td class="th1" align="right" width="200px">&nbsp;<b>类型：</b></td>
		<td>&nbsp;&nbsp;<select id="searchType2"><option value="1">按电银流水号</option><option value="2">按商户订单号</option></select></td>
		</tr>    
       <tr>
         <td  class="th1" align="right">&nbsp;<b id="typeName2">电银流水号：</b></td>
         <td align="left" >&nbsp;&nbsp;<input type="text" id="tseq" name="tseq" class="input"/>
			<input type="text" id="oid" name="oid" class="input" style="display: none;" maxlength="30"/>&nbsp;
              <font color="red">*</font>  
         </td></tr>
       <tr style="display: none" id="tkdate">
         <td  class="th1" align="right">&nbsp;<b id="typeName2">商户日期：</b></td>
         <td align="left" >&nbsp;&nbsp;<input id="c_mdate" name="org_mdate" class="Wdate" type="text"
           onFocus="WdatePicker({skin:'ext',minDate:'1998-12-20',maxDate:'%y-%M-%d',dateFmt:'yyyyMMdd',readOnly:'true'});"/>
         </td></tr>
       <tr>
        <td colspan="2" style="padding-left:200px " >
        <input type="button"  value="查  询"  class="button" id="searchButt"/>
    </td>
  </tr>            
</table>

<!-- ************************ -->
<div id="detail">
<table width="100%"  class="tableBorder" id="detailResultList"  style="display: none">
<thead>
<tr><td class="title" colspan="5">&nbsp;&nbsp;<b>订单详情：</b></td></tr>
</thead>
<tbody>
                     <tr height="25px" valign="middle" class="title2">
                       <td class="tdstyle" align="center" >银行</td>
                       <td align="left"><input id="v_gate" type="text"  size="40" readonly="readonly" /></td>
                       <td class="tdstyle" align="center">商户简称</td>
                       <td align="left" ><input id="v_name" type="text" size="40" readonly="readonly" /></td>
                     </tr>
                     <tr class="trstyletr">
                       <td class="tdstyle" align="center">币种</td>
                       <td align="left">人民币</td>
                       <td class="tdstyle" align="center">电银流水号</td>
                       <td align="left"><input id="v_tseq" type="text" size="40"  readonly="readonly" /></td>
                     </tr>
                     <tr class="trstyletr">
                       <td class="tdstyle" align="center">订单号</td>
                       <td align="left"><input id="v_oid" type="text" size="40" readonly="readonly" /></td>
                       <td class="tdstyle" align="center">商户日期</td>
                       <td align="left"><input id="v_mdate" type="text" size="40" readonly="readonly" /></td>
                     </tr>
                     <tr class="trstyletr">
                       <td class="tdstyle" align="center">交易金额(元)</td>
                       <td align="left"><input id="v_orgAmt" type="text" size="40" readonly="readonly"/></td>
                       <td class="tdstyle" align="center">系统手续费</td>
                       <td align="left"><input id="v_fee_amt" type="text" size="40" readonly="readonly" /></td>
                     </tr>
                     <tr>
                       <td class="tdstyle" align="center">总申请退款金额(元)</td>
                       <td align="left"><input id="v_refund_amt" type="text" size="40" readonly="readonly" /></td>
                       <td class="tdstyle" align="center">可退款金额(元)</td>
                       <td align="left" id="can" colspan="3" ><input id="v_keshenqing" size="40" type="text" readonly="readonly"/></td>
                     </tr>
                     <tr>
                       <td class="tdstyle" align="center">是否可退款</td>
                       <td align="left"><input type="radio" name="toCheck" id="toCheck"><span id="refundState" style="color:red"></span></td>
                       <td class="tdstyle" align="center">&nbsp;</td>
                       <td align="left">&nbsp;</td>
                     </tr>
                 </tbody></table>
               
                <table width="100%"  id="refundTable" style="display: none" class="tableBorder" >
                    <tr>
                        <td width="240px"  style="color: red;" align="right">注意：</td>
                        <td  style="color: red;" valign="top">只能对成功交易进行退款;如当天无法完成退款, 根据各银行提供的服务不同, 退款将在15-25天内完成</td>
                    </tr>
                    <tr>
                        <td  align="right">退款金额：</td>
                        <td  align="left" >
                             <input type="text" maxlength="12"  id="refundAmount" name="refundAmount" value="" >
                        </td>
                    </tr>
                    <tr>
                        <td  align="right">退款原因：</td>
                        <td align="left">
                        <textarea  id="refundReason2" cols="30" rows="4" maxlength="100" onKeyDown="return isMaxLen(this,'intro_info');" onBlur="return isMaxLen(this,'intro_info');" onKeyUp="return isMaxLen(this,'intro_info');"></textarea>
                        <br>最多可输入100个字，还能输入<input readOnly size="3" id="intro_info" value="100" name="intro_info" />个字
                        </td>
                    </tr>
                    
                    <tr>
                         <td  colspan="2">
                         <input type="button" id="sureRefund" name="sureRefund" style="margin-left:250px;" value="确定退款" onclick=" confirmRefund2();" >
                         </td>
                    </tr>
               </table>
</div>
   </div>  
</body>
</html>
