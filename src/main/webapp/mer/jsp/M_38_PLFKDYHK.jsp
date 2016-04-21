<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%
	String path = request.getContextPath();
	response.setHeader("Pragma","No-cache");
	response.setHeader("Cache-Control","no-cache");
	response.setDateHeader("Expires", 0);
	int rand = new java.util.Random().nextInt(10000);
	%>
<title>批量付款到个人银行账户</title>
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="../../public/css/head.css?<%=rand %>" rel="stylesheet" type="text/css" />
<link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="../../dwr/engine.js"></script>
<script type="text/javascript" src="../../dwr/util.js"></script>
<script type="text/javascript" src="../../public/js/md5.js"></script>
<script type="text/javascript" src='../../public/js/ryt.js?<%=rand %>'></script>
<script type="text/javascript" src='../../dwr/interface/DfB2CBatchService.js?<%=rand %>'></script>
<script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand %>'></script>
<script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>

<script type="text/javascript">

        var success = {};
        var slist={};
        var prov={};
        var bknos = {};
        var sumAmt=null;//交易金额
        var sum_amt=null;//实际支付金额
        var fee_amt=null;//手续费
        var balance=null;//账户余额
        var flag=null;
        function initData(){
	    	DfB2CBatchService.getGates(function(dfGates){
	        	bknos = dfGates;
	        });
	        var mid=$("mid").value;
        	DfB2CBatchService.getJSZHByUid(mid,function(zh){
        		dwr.util.addOptions("payAcc", zh);
        		});
	        PageParam.initMerInfo('add',function(l){	
		    	prov=l[0];
			});
        }
		var cellFuncs = [
		      			function(obj) { return obj.accName; },
		      			function(obj) { return obj.accNo; },
		      			function(obj) { return bknos[obj.bkNo]; },
		      			function(obj) { return obj.openBkNo; },
		      			function(obj) { return prov[obj.toProvId]; },
		      			function(obj) { return obj.cardFlag==0?"卡": obj.cardFlag==0?"存折":""; },
		      			function(obj) { return obj.trAmt; },
		      			function(obj) { return obj.trFee; },
		      			function(obj) { return obj.use; },
		      			function(obj) { return obj.errMsg; }
		      		];
        
        var listArray = new Array();
        function uploadBatchFile(){
           // var bkNo = dwr.util.getValue("bkNo");
           var payAcc=$("payAcc").value;
           var txt=$("tXtFile").value;
           if(payAcc==""){
           		alert("请选择账户！");
           		return;
           }
           if(txt!=""){
           	flag_index=txt.lastIndexOf(".");
           	var extension_txt=txt.substring(flag_index+1,txt.length);
            if(chkSuffix(txt)==false){
	            alert("文件格式错误!仅支持xls、xlsx");
	            return;
            }
           }else{
           	alert("请选择上传文件！");
           	return;
           }
          
            var payAcc = dwr.util.getValue("payAcc");
            var file = dwr.util.getValue('tXtFile');
            loadingMessage("处理中，请稍候......");
            DfB2CBatchService.batchAction(payAcc,file,function(obj){
            	dwr.util.removeAllRows("dataList");
				if(!obj.flag){
					success = {};//清空缓存
					alert(obj.errMsg);
				    return;
				}
				success = obj.flag;
				slist=obj.sobjs;
				if(obj.fobjs.length!=0){
					document.getElementById("DataTableId").style.display="block";
	            	dwr.util.addRows("dataList",obj.fobjs,cellFuncs,{escapeHtml:false});
					//统计行
	                var pageTr = $("dataList").insertRow(-1);
	                var pageTd = pageTr.insertCell(-1);
	                pageTd.setAttribute("height","30px");
	                pageTd.setAttribute("colspan",cellFuncs.length);
	                //排序时要设置role="bottom"底部才不会参与排序
	                pageTr.setAttribute("role","bottom");
	                var str = "";
	                str += "<span style='float:left;display:inline-block'>总计 <span class='redfont'>"+obj.sum_lines+"</span> 条，其中 <span class='redfont'>" + (obj.fobjs.length)+ "</span> 条无效数据,请检查后重新上传!";
	                str = str+"</span>"; 
	                pageTd.innerHTML =str.toString();
	                return;
				}
				document.getElementById("DataTableId").style.display="none";
				document.getElementById("batchTitleTable").style.display="none";
				sumAmt=div100(obj.sumAmt+obj.fee_amt*100);//交易金额全局变量赋值
		        sum_amt=obj.sum_amt;//实际支付金额
		        fee_amt=obj.fee_amt;//手续费
		        balance=obj.acc.balance;//账户余额
                var str = "<div class=\"style\"><table class=\"tableBorder\">";
                str += "<tr align=\"center\"><td class=\"title\" align=\"left\" colspan=\"2\" >&nbsp;&nbsp;生成付款单</td>";
                str += "<tr><td class=\"th1\" align=\"right\" width=\"50%\" >订单批次号：</td><td width=\"50%\"><span style='float:left;display:inline-block'id='batchNo'>" + obj.batch.batchNumber+ "</td></tr>";
                str += "<tr><td class=\"th1\" align=\"right\" width=\"50%\" >订单数量：</td><td width=\"50%\"><span style='float:left;display:inline-block'>共<font color='blue'>" + obj.sum_lines+ "</font>条</td></tr>";
               // str += "账户余额：<font color='blue'>" + obj.acc.allBalance + "</font>元&nbsp;&nbsp;&nbsp;&nbsp;";
                str += "<tr><td class=\"th1\" align=\"right\">交易金额：</td><td><font color='blue'>" + obj.sum_amt + "</font>元</td></tr>";
                str += "<tr><td class=\"th1\" align=\"right\">手续费：</td><td><font color='blue'>" + obj.fee_amt + "</font>元</td></tr>";
                str += "<tr><td class=\"th1\" align=\"right\">实际支付金额：</td><td><font color='blue'>" + sumAmt + "</font>元</td></tr>";
				//if(0>0){
				//	 str += "<tr><td colspan=\"2\"  rowspan=\"2\" align=\"center\"><input type= 'button' value='去掉错误数据' onclick='deleteErrorData("+div100(obj.sumAmt)+")'/></td></tr>" ;
				//}else 
				if(sum_amt!=0){
					str += "<tr ><td colspan=\"2\" align=\"center\" ><input type= \"button\" class=\"button\" value=\" 确认提交 \" onclick='confirmSinglePay()'></input></td></tr>" ;
				}else{
					alert("支付金额为零！");
				}
				
                /* str = str+"</span>";  */
                str=str+"</table></div>";
                //pageTd.innerHTML =str;
                //dwr.util.addOptions("b2egateid",bk_c_b2b );
                document.getElementById("result").innerHTML=str;
                jQuery("#alert").css("display","none");
                
                
            });
        }
        
        function doAction(){
			if(!window.confirm("确认支付？")){
				return;
			}
			var md5str = dwr.util.getValue("payPwd");
			if(md5str==''){
				alert('请输入支付密码');
				return;
			}  
            if(success==false){
                return;
            }
            loadingMessage("处理中，请稍候......");
            var batchNo=document.getElementById("batchNo").innerHTML;
            DfB2CBatchService.doActions(batchNo, hex_md5(md5str), slist, function(ret){
            	dwr.util.removeAllRows("dataList");
            	var flag = ret.split("\|")[0];
            	var msg = ret.split("\|")[1];
            	if(flag != "0"){
            		alert(msg);
            		return;
            	}
            	$("balancePay").disabled="disabled";
				jQuery("#table3").click();
            	window.location.href="M_38_PLFKDYHK.jsp";
            });
        }
        
		function download(flag){
			if(flag==1){
        		DfB2CBatchService.downloadBkNo(function(data){
        				dwr.engine.openInDownload(data);
				});
			}
			if(flag==2){
				DfB2CBatchService.downloadProvId(function(data){
        				dwr.engine.openInDownload(data);
				});
			}
			if(flag==3){
				DfB2CBatchService.downloadBatch(function(data){
        				dwr.engine.openInDownload(data);
				});
			}
		}
		
		//点击确认提交弹出输入密码层
		function confirmSinglePay(){
			$("sumAmt").innerHTML=sum_amt+"元";
			$("fee_amt").innerHTML=fee_amt+"元";
			$("sum_amt").innerHTML=sumAmt+"元";
			$("balance").innerHTML=div100(balance)+"元";
			jQuery("#table3").wBox({title:"<img src='../../public/images/xitu.gif'>请输入支付密码",show:true});
			
		}
        //新增上传文件格式验证
        function chkSuffix(file){
            var reg=/(.xls|.xlsx)$/;
            return reg.test(file);
        }
		
        </script>

</head>

<body onload="initData()">

	<div class="style">

		<table width="100%" align="left" class="tableBorder"
			id="batchTitleTable">
			<tr>
				<td class="title" align="left" colspan="2">&nbsp;&nbsp;批量付款到个人银行账户</td>
			</tr>
			<tr>
				<td class="th1" align="right" width="40%"><b>付出账户：</b>
				</td>
				<td align="left"><input type="hidden" name="mid" id="mid"
					value="${sessionScope.SESSION_LOGGED_ON_USER.mid}" /> <select
					style="width: 200px" id="payAcc">
						<!-- <option value="">请选择...</option> -->
				</select><br /></td>
			</tr>
			<tr>
				<td class="th1" align="right"><b>EXCEL文件：</b>
				</td>
				<td align="left"><input type="file" id="tXtFile" name="tXtFile"
					value="" style="width: 200px;height: 24px;" accept=".xls,.xlsx" /></td>
			</tr>

			<tr align="center">
				<td colspan="2"><input class="button" value="上传 " type="submit"
					onclick="uploadBatchFile();" /> &nbsp; <input class="button"
					value="模板下载" class="button" type="button"
					onclick="javascript:download(3);" /></td>
			</tr>


		</table>
		<table id="alert" width="100%" border="0">
			<tr>
				<td>
					<fieldset>
						<legend>上传说明</legend>
						1、上传格式：xls(Excel 2003文档) <br />
						<!-- 2、上传内容：收款人户名,收款人账号，收款银行（银行对应表编号），开户所在省份（省份对应表编号），卡折标志（添代号：0代表卡,1代表存折），交易金额，用途&nbsp;&nbsp;(数据之间用逗号隔开，每一行为一条完整付款数据)<br /> -->
						<!-- 3、上传模板：<a href="javascript:download(3);" >批量上传文件格式.xls</a><br /> -->
						2、<a href="javascript:download(1);">银行编号对应表.txt</a> <br /> 3、<a
							href="javascript:download(2);">省份编号对应表.txt</a>
					</fieldset>
				</td>
			</tr>
		</table>


		<table class="tablelist tablelist2" id="DataTableId"
			style="display: none;">
			<tr>
				<th>收款人姓名</th>
				<th>收款帐号</th>
				<th>收款银行</th>
				<th>收款银行联行行号</th>
				<th>开户所在省份</th>
				<th>卡折标志</th>
				<th>付款金额(元)</th>
				<th>手续费(元)</th>
				<th>用途</th>
				<th>错误信息</th>
			</tr>
			<tbody id="dataList"></tbody>
		</table>
		<div id="result"></div>

	</div>
	<table id="table3" class="tableBorder"
		style="display: none;width: 300px;height: 100px;">
		<tbody>

			<tr>
				<td class="th1" align="right" width="30%">&nbsp; 交易金额：</td>
				<td width="70%" align="left"><span id="sumAmt"></span> <input
					type="hidden" id="hidden_type" value="" /></td>
			</tr>
			<tr>
				<td class="th1" align="right" width="30%">&nbsp; 手续费：</td>
				<td width="70%" align="left"><span id="fee_amt"></span> <input
					type="hidden" id="hidden_type" value="" /></td>
			</tr>
			<tr>
				<td class="th1" align="right" width="30%">&nbsp; 实际支付：</td>
				<td width="70%" align="left"><span id="sum_amt"></span> <input
					type="hidden" id="hidden_type" value="" /></td>
			</tr>
			<tr>
				<td class="th1" align="right" width="30%">&nbsp; 账户余额：</td>
				<td width="70%" align="left"><span id="balance"></span> <input
					type="hidden" id="hidden_type" value="" /></td>
			</tr>
			<tr>
				<td class="th1" align="right" width="30%">&nbsp; 支付密码：</td>
				<td width="70%" align="left"><input type="password" id="payPwd"
					size="20" /> <input type="hidden" id="hidden_type" value="" /></td>
			</tr>
			<tr>
				<td colspan="2" align="center">
				</td>
			</tr>
			<tr>

				<td colspan="2" align="center"><input type="button"
					value="账户金额支付" id="balancePay" onclick="doAction();" class="button" />&nbsp;&nbsp;
					<input type="button" value="返 回" class="wBox_close button" /></td>
			</tr>
		</tbody>
	</table>
</body>
</html>
