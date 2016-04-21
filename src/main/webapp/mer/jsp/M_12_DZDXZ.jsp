<%@ page language="java" pageEncoding="UTF-8" %>
<% String path = request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>对帐单下载</title>
<%
response.setHeader("Pragma","No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", 0);
int rand = new java.util.Random().nextInt(10000);
%>
        <meta http-equiv="pragma" content="no-cache"></meta>
        <meta http-equiv="cache-control" content="no-cache"></meta>
        <meta http-equiv="Expires" content="0" />  
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
        <link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css"></link>
         <link href="../../public/css/tab.css?<%=rand %>" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="../../public/datePicker/WdatePicker.js"></script>      
        <script type='text/javascript' src='../../dwr/engine.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../dwr/util.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/MerSettlementService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/CommonService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../public/js/ryt.js?<%=rand%>'></script>
	   	<script type=text/javascript src="../../public/js/jquery.idTabs.min.js?<%=rand %>"></script>
  <script  type="text/javascript">
	jQuery.noConflict();
	(function($) { 
		 $(function(){$("#usual1 ul").idTabs();})
	})(jQuery);	 	       
  function judgeLoadFile(loadtype,tstat) { 
      var p={}; 
      var mid = $("mid").value;
      
      p.mid=$("mid").value;
      p.tstat=tstat;
      p.downType=loadtype;
      

      if(tstat==2){//2支付对账单

		        if ($("bdate").value == ''){
		            alert("请选择起始日期!");
		            return false;
		        }   
		        if ($("edate").value==''){
		            alert("请选择结束日期!");
		            return false;
		        }
	            p.gate=$("gate").value;
	            p.type=$("type").value;
	            p.date_begin=$("bdate").value;
	            p.date_end=$("edate").value;
	            p.date_type=$("date").value;
      }else{ //3退款对账单
      	   if ($("bdate2").value == ''){
		            alert("请选择起始日期!");
		            return false;
		        }   
		        if ($("edate2").value==''){
		            alert("请选择结束日期!");
		            return false;
		        }
	            p.gate=$("gate2").value;
	            p.date_begin=$("bdate2").value;
	            p.date_end=$("edate2").value;
	            p.date_type=$("date2").value;
      }
      if (loadtype == 'txt') {
          //tstat
		  dwr.engine.setAsync(false);//把ajax调用设置为同步
          MerSettlementService.downloadBillTXTData(p,function(data){dwr.engine.openInDownload(data);})
      } else {
			return false;
		}
	}
     function init(){
    	 CommonService.getMerGatesMap($("mid").value,function(map){
	    	 dwr.util.addOptions("gate", map);
	    	 dwr.util.addOptions("gate2", map);
    	 });
	 	dwr.util.addOptions("type", h_type);
     }
    </script>
</head>
<body onload="init();"><!-- setTimeout('deleteOpt()',500); -->

<div class="style">
 <input type="hidden"  id="mid" name="mid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}" /><!-- 支付订单/ 退款订单 -->
<div id="usual1" class="usual"> 
<ul>  
    <li><a  class="selected" href="#tab1" id="tablink1">支付成功文件下载</a></li> 
    <li><a  href="#tab2" id="tablink2">退款成功文件下载</a></li>
</ul> 
<div id="tab1">
 <!-- <input type="hidden" id="tstat" name="tstat"  value="2"/> -->

  <table  width="100%"    class="tableBorder">
    <tbody>
    <tr>
        <td class="th1"  align="right" width="30%">商户号：</td>
        <td align="left" > &nbsp;&nbsp;${sessionScope.SESSION_LOGGED_ON_USER.mid}
        </td> 
    </tr>
    <tr>    
        <td class="th1"  align="right" > 银行：</td>
        <td align="left" >
            &nbsp;<select style="width:150px" id="gate" name="gate" >
                <option value="">全部...</option>
            </select>
        </td>
    </tr>
                     
    <tr>
        <td class="th1"  align="right" >交易状态：</td>
        <td align="left"> &nbsp;交易成功
            <!-- 
            <select style="width:150px" id="tstat" name="tstat" >
                <option value="">交易成功</option>
                <option value="">已对账</option>
            </select>
             -->
        </td>
     </tr>
   
   <tr> 
        <td class="th1"  align="right">交易类型：
        </td>
        <td align="left" id="type_id">
            &nbsp;<select style="width:150px" id="type" name="type" >
                <option value="">全部...</option>
            </select>
         </td>
   </tr>
    <tr>
      <td class="th1"  align="right" height="30px">
      
            <select style="width:80px" id="date" name="date" >
                <option value="sys_date">系统日期</option>
                 <option value="mdate">商户日期</option>
            </select>
      
      
      </td>
      <td align="left">
      &nbsp;<input id="bdate" name="bdate" class="Wdate" type="text" onfocus="ryt_area_date('bdate','edate',0,90,0)"/>&nbsp;&nbsp;至
      <input id="edate" name="edate"  class="Wdate" type="text"/><font color="red">*</font>
      </td>
    </tr>
     <tr>
       <td colspan="2" align="left" height="30px">
         <input type="button" value="下载TXT报表" style="width: 100px;height: 25px;margin-left: 350px" onclick="judgeLoadFile('txt',2)"/>
         <!--  <input type="button" value="下载Excel报表" style="width: 100px;height: 25px;margin-left: 10px" onClick="judgeLoadFile('xls',2)"/>
         <input type="button" value="下载订单号列表" style="width: 100px;height: 25px;margin-left: 10px" onClick="judgeLoadFile('orlist',2)"/>-->
       </td>
     </tr></tbody>
    </table><br/>
    
    <div style="float: left;width: 100%;">
    <p>&nbsp;&nbsp;</p>
        <p>说明：</p>
        <p>&nbsp;&nbsp;<font color="red">*</font>银行名称M：信用卡支付,W：wap支付,C：充值卡支付,I：语音/快捷支付,其余：网银支付</p>
        <p>支付格式文件模板</p>
        <hr style="border:1px silver dashed; "/>
<pre>
TRADEDETAIL-START,${sessionScope.SESSION_LOGGED_ON_USER.mid},20110225,28,S
${sessionScope.SESSION_LOGGED_ON_USER.mid},201101141420169205,20110114,5340.00,106.80,9505,20110114,8,2
${sessionScope.SESSION_LOGGED_ON_USER.mid},201101141040308694,20110114,1650.00,33.00,9393,20110114,8,2
${sessionScope.SESSION_LOGGED_ON_USER.mid},201101141129409197,20110114,3500.00,70.00,9419,20110114,8,2
.........
TRADEDETAIL-END
</pre>
        <hr style="border:1px silver dashed; "/>
        <p>&nbsp;&nbsp;</p>
        <p>格式说明</p>
        <p>第一行：开始标示字符，商户号，下载日期，总下载记录数，下载成功/失败标示</p>
        <p>支付账单下载，内容行依次为：  商户号，  商户订单号，商户日期，交易金额，手续费，电银流水号，交易日期，交易类型，订单状态</p>
        <p>结尾行：TRADEDETAIL-END 结束标示</p>
	</div>
    
	</div>
    <!--******* 退款订单*********  -->
	<div id="tab2">
    <table    width="100%" id="table2" class="tableBorder"><tbody>
    <tr>
        <td class="th1"  align="right" width="30%">商户号：</td>
        <td align="left" > 
            &nbsp;&nbsp;${sessionScope.SESSION_LOGGED_ON_USER.mid}   
        </td> 
    </tr>
    <tr>    
        <td class="th1"  align="right" > 银行：</td>
        <td align="left" >
            &nbsp;<select style="width:150px" id="gate2" name="gate2" >
                <option value="">全部...</option>
            </select>
        </td>
    </tr>
                     
    <tr>
        <td class="th1"  align="right" >退款状态：</td>
        <td align="left"> &nbsp;退款成功
        </td>
     </tr>
   
    <tr>
      <td class="th1" align="right" height="30px">  
            <select style="width:110px" id="date2" name="date2" >
                <option value="pro_date">退款经办日期 </option>
                 <option value="req_date">退款确认日期</option>
            </select>
      </td>
      <td align="left">
      &nbsp;<input id="bdate2" name="bdate2" class="Wdate" type="text" onfocus="ryt_area_date('bdate2','edate2',0,90,0)"/>&nbsp;&nbsp;至
      <input id="edate2" name="edate2"  class="Wdate" type="text"/><font color="red">*</font>
      </td>
    </tr>
     <tr>
       <td colspan="2" align="left" height="30px">
         <input type="button" value="下载TXT报表" style="width: 100px;height: 25px;margin-left: 350px" onclick="judgeLoadFile('txt',3)"/>
        <!-- <input type="button" value="下载Excel报表" style="width: 100px;height: 25px;margin-left: 10px" onClick="judgeLoadFile('xls',3)"/>
         <input type="button" value="下载退款订单号列表" style="width: 130px;height: 25px;margin-left: 10px" onClick="judgeLoadFile('orlist',3)"/> -->
       </td>
     </tr></tbody>
    </table><br/>

    <div style="float: left;width: 100%;">
    <p>&nbsp;&nbsp;</p>
        <p>说明：</p>
        <p>&nbsp;&nbsp;<font color="red">*</font>银行名称M：信用卡支付,W：wap支付,C：充值卡支付,I：语音/快捷支付,其余：网银支付</p>
        <p>退款格式文件模板</p>
        <hr style="border:1px silver dashed; "/>
<pre>
TRADEDETAIL-START,${sessionScope.SESSION_LOGGED_ON_USER.mid},20110225,28,S
${sessionScope.SESSION_LOGGED_ON_USER.mid},FT11110110143874,20111101,3650.00,73.00,995,20111101,20111101,2,7756
${sessionScope.SESSION_LOGGED_ON_USER.mid},FT111101101655914,20111101,7300.00,146.00,994,20111101,20111101,2,7747
.........
TRADEDETAIL-END
</pre>
        <hr style="border:1px silver dashed; "/>
        <p>&nbsp;&nbsp;</p>
        <p>格式说明</p>
        <p>第一行：开始标示字符，商户号，下载日期，总下载记录数，下载成功/失败标示</p>
      <p> 
 退款账单下载，内容行依次为：商户号， 原商户订单号，原商户日期，退款金额，退回手续费，退款流水号，退款确认日期，退款经办日期，退款状态，原电银流水号
      </p>
        <p>结尾行：TRADEDETAIL-END 结束标示</p>
    <!--  -->
</div>
    </div>
</div>
</div>
</body>

</html>