<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" errorPage="../../error.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

	<head>
		<title>对账计费</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<%
		String path = request.getContextPath();
		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);
		int rand = new java.util.Random().nextInt(10000);
		//String path = request.getContextPath();
		%>
        <link href="<%=path %>/public/css/head.css?<%=rand %>" rel="stylesheet" type="text/css">
         <link href="<%=path %>/public/css/tab.css?<%=rand %>" rel="stylesheet" type="text/css">
        <script type='text/javascript' src='<%=path %>/dwr/engine.js'></script>
        <script type='text/javascript' src='<%=path %>/dwr/util.js'></script>
        <script type='text/javascript' src='<%=path %>/dwr/interface/PageService.js?<%=rand %>'></script>
        <script type='text/javascript' src='<%=path %>/dwr/interface/DoSettlementService.js?<%=rand %>'></script>
        <script type='text/javascript' src='<%=path %>/dwr/interface/DownloadFileService.js?<%=rand %>'></script>
        <script type="text/javascript" src="<%=path %>/public/datePicker/WdatePicker.js?<%=rand %>"></script>
        <SCRIPT type=text/javascript src="<%=path %>/public/js/ryt.js?<%=rand %>"></SCRIPT>
		<SCRIPT type=text/javascript src="<%=path %>/public/js/jquery.idTabs.min.js?<%=rand %>"></SCRIPT>
<!-- 		<script type='text/javascript' src='<%=path %>/public/js/settlement/checkAccount.js?<%=rand %>'></script> -->

		<SCRIPT type="text/javascript" >
	     	jQuery.noConflict();
			(function($) {
				 $(function(){
				     $("#usual1 ul").idTabs();
				     $("#usual1 ul a").click(function(){$("#merHlogTable").hide();});
				     $("#downLoadTxt").click(downLoadErrorData);
				     $("#downLoadExel").click(downLoadExel);
				 });
			   function downLoadErrorData(){
		      	  var text = "\t银行记录的订单号\t银行交易金额\t电银流水号/商户订单号\t电银信息交易金额\t状态\n\t\t";
		      	  text+=$("#dataBody td").map(function(i){
		      	    if(i%5==4)return $(this).text()+"\n";
		      	    else return $(this).text();
		      	  }).get().join("\t\t");
		      	  dwr.engine.setAsync(false);//把ajax调用设置为同步
		      	DownloadFileService.downloadTXTFile(text,jsToday()+"0"+Math.floor(Math.random()*1000)+"ErrorData.txt",
		      	        function(data) {dwr.engine.openInDownload(data);});
		       }
		       function downLoadExel(){
		         var dataArr=new Array();
		         dataArr[0]="银行记录的订单号,银行交易金额,电银流水号/商户订单号 ,电银信息交易金额 ,状态";
		         $("#dataBody tr").each(function(i){
		            dataArr[i+1]=$(this).children("td").map(function(){
		                 return  $(this).text();
		             }).get().join(",");
		         });
		         dwr.engine.setAsync(false);//把ajax调用设置为同步
		      	DownloadFileService.downloadXLSFile(dataArr,jsToday()+"0"+Math.floor(Math.random()*1000)+"ErrorData.xls","对账失败或可疑数据",
		      	        function(data) {dwr.engine.openInDownload(data);});
		       }
			})(jQuery);
			 function uploadFiles() {
				 var type = dwr.util.getValue('settleBank');
				 if(type==''){
				    alert('请选择要对账的银行');
				    return;
				 }
				 var filePath = document.getElementById('uploadFile').value;
			     if (filePath == '') {
			        alert("请选择对账文件!");
			        return ;
			     }
                 //新增上传文件格式验证
                 if(filePath.indexOf('.') != -1 && chkSuffix(filePath)==false){
                     alert("文件格式错误!仅支持txt、xls、xlsx、csv、xml");
                     return;
                 }
			     if(type==20000){
			    	 alert("此支付渠道暂不支持文件对账，请使用接口对账!");
				     return ;
				}
				var file = dwr.util.getValue('uploadFile');
				if(type==20004||type==30010||type==30003||type==40006||type==40007||type==55002){ // 处理excel
			    	DoSettlementService.byUploadFileSettleXLS(type,file,callback);
				}else if(type==30014||type==30018||type==55000||type==56113){ // 处理GBK编码文件（txt、csv）
			    	DoSettlementService.byUploadZWFileSettle(type,file,callback);
				}
				else{
					DoSettlementService.byUploadFileSettle(type,file,callback);
				}
		      }
	          var callback = function(obj){
	               if(obj==null){alert("对账出现异常！请重试。");return;}
	               if(!obj.success){
	              		 alert(obj.errMsg) ;
	                     return;
	               }
	               dwr.util.removeAllRows("dataBody");
	          	   document.getElementById("total").innerHTML=obj.total;
	               document.getElementById("success").innerHTML=obj.success.length;
	               document.getElementById("suspect").innerHTML=obj.suspect.length;
	               document.getElementById("fail").innerHTML=obj.fail.length;
	               document.getElementById("finish").innerHTML=obj.finish;
	               document.getElementById("exception").innerHTML=obj.exception;
	               cellfuncs1=[
	               		       function(failObj){return failObj.bkSeq;},
		               			function(failObj){return failObj.bkAmount;},
		               			function(failObj){return failObj.tseq+"/"+failObj.oid;},
		               			function(failObj){return div100(failObj.amount);},
		               			function(failObj){return "<B>失败</B>";}
	               			  ];
	               cellfuncs2=[
	               		        function(suspectObj){return suspectObj.bkSeq;},
		               			function(suspectObj){return suspectObj.bkAmount;},
								function(suspectObj){return '';},
		               			function(suspectObj){return '';},
		               			function(suspectObj){return "可疑";}
	               			  ];
	               document.getElementById("merHlogTable").style.display="";
	               if(obj.suspect.length==0&&obj.fail.length==0){
	                   document.getElementById("dataBody").appendChild(creatNoRecordTr(5));
	                    return;
	               }
	               if(obj.fail.length!=0){
	               		dwr.util.addRows("dataBody", obj.fail, cellfuncs1,{ escapeHtml:false });
	               }
		           if(obj.suspect.length!=0){
		             	dwr.util.addRows("dataBody", obj.suspect, cellfuncs2);
		           }
		      };
	/******接口对账*****/

 function interfaceCheckAccount(){
	var paramMap={};
	var beginDate = document.getElementById("beginDate").value;
	var endDate = document.getElementById("endDate").value;
	var operId = document.getElementById("operId").value;
	var pwd = document.getElementById("pwd").value;
	var cmbcmerchantNo = document.getElementById("merchantNo").value;
	var bkNo = document.getElementById("bkNo").value;
	var gid = document.getElementById("settleBank2").value;
	if(gid==90000){
		if(operId == ''){
			alert("操作员号不能为空!");
			return false;
		}
		if(pwd == ''){
			alert("操作员密码不能为空!");
			return false;
		}
		if(bkNo == ''){
			alert("分行号不能为空!");
			return false;
		}
		if(cmbcmerchantNo == ''){
			alert("商户号不能为空!");
			return false;
		}
		if (beginDate==""||endDate=="") {
	  		 alert("请选择起始日期和结束日期！");
		return false;
		}
		paramMap={"operId":operId,"pwd":pwd,"beginDate":beginDate,"endDate":endDate,"merchantNo":cmbcmerchantNo,"bkNo":bkNo};
	}else if(gid==20000){
		if (beginDate==""||endDate=="") {
	  		 alert("请选择起始日期和结束日期！");
		return false;
		}
		paramMap={"beginDate":beginDate,"endDate":endDate};
	}else if(gid==10600){
		var psbcMerno = document.getElementById("psbcMerno").value;
		var psbcDate = document.getElementById("psbcDate").value;
		var fileType = document.getElementById("fileType").value;
		if(psbcMerno == ''){
			alert("商户号不能为空!");
			return false;
		}
		if(psbcDate == ''){
			alert("请选择起始日期和结束日期！");
			return false;
		}
		if(fileType == ''){
			alert("请选择对账单类型！");
			return false;
		}
		paramMap={"date":psbcDate,"merNo":psbcMerno,"fileType":fileType};
	}
	 DoSettlementService.byBKInterfaceSettle(gid,paramMap,callback) ;

}
	function showMustInput(){
		var gid = document.getElementById("settleBank2").value;
		var tableinfo = document.getElementById("impl_table").rows;
		for(var i=0;i<tableinfo.length;i++){
				var row= tableinfo[i];
				if(gid==90000){
					if(row.id.indexOf("cmbc")>=0){
						row.style.display="";
						continue;
					}
				}else if(gid==10600){
					if(row.id.indexOf("pub_date")>=0){
						row.style.display="none";
						continue;
					}
					else if(row.id.indexOf("psbc")>=0){
						row.style.display="";
						continue;
					}
				}
				if(row.id.indexOf("pub")>=0){
						row.style.display="";
					}else{
						row.style.display="none";
					}
		}
	}
 		/*
 		function checkBkNo(){
 			DoSettlementService.checkBkNo(function(msg){alert(msg)});
 	 	}
 	    */
 	  function gateRouteIdList(){
		 var gateName = $("gateName").value;
		 PageService.getGRouteByName(gateName,function(m){
			 gate_route_map1=m;
			 dwr.util.removeAllOptions("settleBank");
			 dwr.util.addOptions("settleBank",{'':'全部...'});
			dwr.util.addOptions("settleBank", gate_route_map1);
		 });

	 }
 		window.onload=function(){
 			PageService.getGateRouteMap(function(gateMap){//增值业务的gate
 			   	dwr.util.addOptions("settleBank",gateMap);
 			});

 		};
        //验证后缀名格式
        function chkSuffix(filePath){
            var reg=/(.txt|.xls|.xlsx|.csv|.xml|.sjb)$/;
            return reg.test(filePath);
        }

 		</SCRIPT>

 </head>
<body >
<div class="style">

<div id="usual1" class="usual">
<ul>
    <li><a  class="selected" href="#tab1" id="tablink1">上传文件对账</a></li>
    <li><a  href="#tab2" id="tablink2">通过接口对账</a></li>
   <!--
   <li>
    <input type="button"  style="height: 24px;width: 100px;margin-left: 200px" value="检查" onclick="checkBkNo()"/>
    <font color="red">&nbsp;&nbsp;&nbsp;*</font>
    备付金管理上线后，会产生商户流水记录，点击检查按钮，系统检查相关配置是否完成
   </li>
      -->
   <!--  <li><a  href="#tab3" id="tablink3">手工对账</a></li> -->
</ul>
<div id="tab1">
	    <table width="100%"  align="left" class="tableBorder">
	          <tbody>
	             <tr><td  align="right" width="200px" class="th1">&nbsp; <b>对账银行：</b></td>
	                 <td align="left"><input type="text" id="gateName" name="gateName" onblur="gateRouteIdList()"/>
	                  <select id = "settleBank" style="width: 220px;height: 24px">
	                   <option value="">请选择...</option>
	                 <!-- -->


	                  </select><font color="red">&nbsp;&nbsp;&nbsp;*</font>
	                 </td>
	             </tr>
	             <tr><td  align="right"  class="th1">&nbsp; <b>对账文件：</b></td>
	             <td  align="left"> <input type="file" id="uploadFile" style="height: 24px" accept=".txt,.xls,.xlsx,.csv,.xml,.sjb"/></td>

	              </tr>
	             <tr>
	               <td style="padding-left: 200px" colspan="2" style="height: 30px">
	                 <input style="width: 100px;height: 25px" value=" 确 定 对 账  " type="submit" onclick="uploadFiles()"/></td>
	             </tr>
	          </tbody>
	   </table><br/>

	</div>
	<div id="tab2">
<form id="impl_form">
	    <table width="100%" align="left"  class="tableBorder" id="impl_table">
          <tbody>
              <tr id="pub_one">
                   <td align="right" width="200px" class="th1">&nbsp; <b>对账银行：</b></td>
                   <td align="left">
  					   <select id = "settleBank2" style="width: 155px;height: 24px" onchange="showMustInput();">
	                   <!--  <option value="">请选择...</option>-->
	                   <option value="90000">招商银行-wap</option>
	                   <option value="20000">交通银行-b2b</option>
	                   <option value="10600">邮政储蓄-Web</option>
	                  </select><font color="red">&nbsp;&nbsp;&nbsp;*</font>
              </tr>
               <!-- ========================================================================= -->
              <tr id="cmbc_bankno">
                   <td align="right"  class="th1">&nbsp; <b>分行行号：</b></td>
                   <td align="left"><input type="text" id="bkNo" name="bkNo" class="input"/>&nbsp;<font color="red">*</font>
                   &nbsp;<b>商户号：</b><input  id="merchantNo" name="merchantNo" class="input"/>&nbsp; <font color="red">*</font></td>
              </tr>
              <tr id="cmbc_operId">
                   <td align="right"  class="th1">&nbsp; <b>操作员号：</b></td>
                   <td align="left"><input type="text" id="operId" name="operId" class="input"/>&nbsp;<font color="red">*</font>
                   &nbsp;<b>密码：</b><input type="password" id="pwd" name="pwd" class="input"/>&nbsp; <font color="red">*</font></td>
              </tr>
               <!-- ========================================================================= -->

               <!-- ========================================================================= -->
               <!-- ========================================================================= -->

               <!-- ========================================================================= -->
               <tr id="psbc_merno" style="display: none;">
                    <td align="right"  class="th1">&nbsp; <b>商户号：</b></td>
                    <td align="left">
                           <input type="text" id="psbcMerno" name="psbcMerno" class="input"/>&nbsp;<font color="red">*</font>
                    </td>
               </tr>
               <tr id="psbc_date" style="display: none;">
                    <td align="right"  class="th1">&nbsp; <b>对账文件类型：</b></td>
                    <td align="left">
                          <select id="fileType" name="fileType">
                          			<option value="0">全部</option>
                          			<option value="1">支付</option>
                          			<option value="2">退货</option>
                          </select>
                            <font color="red">*</font>
                    </td>
               </tr>
               <tr id="psbc_date" style="display: none;">
                    <td align="right"  class="th1">&nbsp; <b>清算日期：</b></td>
                    <td align="left">
                            <input id="psbcDate" name="psbcDate" class="Wdate" type="text" value="" onfocus="WdatePicker({skin:'ext',minDate:'2001-12-20',maxDate:'%y-%M-{%d-1}',dateFmt:'yyyyMMdd',readOnly:'true'});" /> <font color="red">*</font>
                    </td>
               </tr>
                <!-- ========================================================================= -->
                 <tr id="pub_date">
                    <td align="right"  class="th1">&nbsp; <b>日期：</b></td>
                    <td align="left">
                            <input id="beginDate" name="beginDate" class="Wdate" type="text" value="" onfocus="WdatePicker({skin:'ext',minDate:'2001-12-20',maxDate:'%y-%M-{%d-1}',dateFmt:'yyyyMMdd',readOnly:'true'});" /> <font color="red">*</font>&nbsp;  至&nbsp;&nbsp;
                            <input id="endDate" name="endDate" class="Wdate" type="text" value="" onfocus="WdatePicker({skin:'ext',minDate:'#F{$dp.$D(\'beginDate\')}',maxDate:'%y-%M-{%d-1}',dateFmt:'yyyyMMdd',readOnly:'true'});" />&nbsp; <font color="red">*</font>
                    </td>
               </tr>
               <tr id="pub_two">
                    <td  colspan="2" style="padding-left: 300px">
                    <input name="num" value="4" type="hidden"/><input value="确 定" type="button" onclick="interfaceCheckAccount()" class="button"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                   <!-- <input class="button" value="重  置" type="reset" type="reset" /> --></td>
               </tr>
         </tbody>
    </table></form>
	</div>

	<!--
	   <div id="tab3">
	   <table width="100%"  align="left" class="tableBorder">
              <tbody>
                  <tr><td  align="right" width="200px" class="th1">&nbsp; <b>电银流水号：</b></td>
                 <td  align="left"> <input type="text"  style="width: 200px;height: 24px"/>
                 <font color="red">&nbsp;&nbsp;&nbsp;*</font>
                 </td>

                  </tr>
                 <tr><td  align="right" width="200px" class="th1">&nbsp; <b>银行交易金额：</b></td>
                 <td  align="left"> <input type="text"  style="width: 200px;height: 24px"/>
                 <font color="red">&nbsp;&nbsp;&nbsp;*</font>
                 </td>

                  </tr>
                   <tr><td  align="right" width="200px" class="th1">&nbsp; <b>银行手续费：</b></td>
                 <td  align="left"> <input type="text"  style="width: 200px;height: 24px"/>

                 </td>

                  </tr>
                   <tr><td  align="right" width="200px" class="th1">&nbsp; <b>交易银行：</b>
                   </td>
                 <td  align="left">

 				<select id = "bankAuthType" style="width: 200px;height: 24px">
                       <option value="">请选择...</option>
                       <option value="10001">交通银行</option>
                       <option value="90000">招商wap</option>
                       <option value="90001">交通银行wap</option>
                       <option value="IPS">hx</option>
                       <option value="PNR">hf</option>
                       <option value="19pay">gy</option>
                       <option value="90002">建设银行wap</option>
                       <option value="90003">工商银行wap</option>
                        <option value="90004">中国银行wap</option>
                      </select>
                 <font color="red">&nbsp;&nbsp;&nbsp;*</font>
                  </td>

                  </tr>

                 <tr>
                   <td style="padding-left: 200px" colspan="2" style="height: 30px">
                     <input style="width: 100px;height: 25px" disabled="disabled" value=" 此功能后续增加  " type="submit" onclick=""/></td>
                 </tr>
              </tbody>
       </table>

    </div>
     -->
	   <table  class="tablelist tablelist2" id="merHlogTable" style="display:none;"><!-- style="display: none;" -->
	  	 <tr><th>银行记录的订单号</th><th>银行交易金额</th><th>电银流水号/商户订单号</th><th>电银信息交易金额</th><th>可疑/失败</th></tr>
       <tbody id="dataBody">
       </tbody>
       <tfoot>
           <tr role="bottom"><td colspan="5" align="center">
           <input value="下载txt"  type="button" class="button" id="downLoadTxt" />
           <input value="下载xls"  type="button" class="button" id="downLoadExel" />
                                     对账文件有效交易总计<span id="total" style="color: blue;"></span>条
            &nbsp;&nbsp;&nbsp;&nbsp; 已对账交易<span id="finish" style="color: blue;"></span>条记录
            &nbsp;&nbsp;&nbsp;&nbsp;对账成功<span id="success" style="color: blue;"></span>条记录
            &nbsp;&nbsp;&nbsp;&nbsp;可疑交易<span id="suspect" style="color: red;"></span>条记录
            &nbsp;&nbsp;&nbsp;&nbsp; 失败交易<span id="fail" style="color: red;"></span>条记录
            &nbsp;&nbsp;&nbsp;&nbsp;对账异常<span id="exception" style="color: red;"></span>条记录
            </td></tr>
       </tfoot>
      </table>
 </div>

</div>
</body>

</html>
