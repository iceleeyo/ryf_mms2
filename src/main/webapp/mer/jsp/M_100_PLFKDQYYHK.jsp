<%@ page language="java" pageEncoding="UTF-8" %>
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
        <title>批量付款到企业银行</title>
        <meta http-equiv="pragma" content="no-cache"/>
        <meta http-equiv="cache-control" content="no-cache"/>
        <meta http-equiv="Expires" content="0"/>  
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <link href="../../public/css/head.css?<%=rand %>" rel="stylesheet" type="text/css"/>
        <link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css"/> 
        <script type="text/javascript" src="../../dwr/engine.js"></script>
        <script type="text/javascript" src="../../dwr/util.js"></script>
        <script type="text/javascript" src='../../public/js/ryt.js?<%=rand %>'></script>
        <script type="text/javascript" src="../../public/js/md5.js"></script>
        <script type='text/javascript' src="../../public/js/money_util.js?<%=rand%>"></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand %>'></script>
        <script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
        <script type="text/javascript" src='../../dwr/interface/DfB2BBatchService.js?<%=rand%>'></script>
        <script type="text/javascript">
        var success = {};
        var count=1;
        var batchB2B;
        var DXamt=0;
		var cellFuncs = [
		      			function(obj) { return count++; },
		      			function(obj) { return obj.accName; },
		      			function(obj) { return obj.bkNo; },
		      			function(obj) { return obj.openBkNo; },
		      			function(obj) { return obj.accNo; },
		      			function(obj) { return obj.errProvId; },
		      			function(obj) { return obj.trAmt; },
		      			function(obj) { return obj.errMsg; }
		      		];
        
        var listArray = new Array();
        function uploadExcelFile(){
       		var aid=$("aid").value;
        	if(aid==""||aid==undefined){
				alert("请选择付款账户！");
				return;
			}
           // var bkNo = dwr.util.getValue("bkNo");
           var txt=$("tXtFile").value;
           if(txt==""){
               alert("请选择上传文件!");
           	   return;
           }else{
               flag_index=txt.lastIndexOf(".");
           	   var extension_txt=txt.substring(flag_index+1,txt.length);
               if(chkSuffix(txt)==false){
	               alert("文件格式错误!仅支持xls、xlsx");
	               return;
            }
           }
           var payAcc = dwr.util.getValue("orderDescribe");
           var file = dwr.util.getValue('tXtFile');
           loadingMessage("处理中，请稍候......");
           DfB2BBatchService.batchForExcel(payAcc,file,function(obj){
            	$("tbinfos").style.display="";
                count=1;
                dwr.util.removeAllRows("dataList");
            	if(obj.errMsg!=null&&obj.flag==false){
            	    success = {};//清空缓存
            	    alert(obj.errMsg);
            		return;
            	}else if(obj.errMsg==null&&obj.flag==false&&obj.fobjs!=null){
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
            	}else if(obj.bkName=="ok"){
           			success = obj.sobjs;
           			batchB2B=obj.batch;
           		    $("batchnumber").innerHTML=obj.batch.batchNumber;//批次号
           			$("count").innerHTML=obj.batch.count;//记录条数
           			$("orderamt").innerHTML=formatNumber(Number(obj.batch.orderAmt).toFixed(2));//订单金额
           			$("feeamt").value=formatNumber(Number(obj.batch.feeAmt).toFixed(2));//手续费
           			$("discr").innerHTML=obj.batch.orderDescribe;//描述
           			$("allamt").value=formatNumber(Number(obj.batch.allrAmt).toFixed(2));//应付金额
           			$("confirm_transAmt_uppercase").value = atoc(obj.batch.allrAmt);//大写应付金额
           			DXamt=obj.batch.allrAmt;
           			$("onediv").style.display="none";
           			$("twodiv").style.display="block";
    				DfB2BBatchService.queryAccount($("mid").value,function(data){
						var balance=div100(data.balance);//用户余额
						var tranAmt=($("allamt").value).replaceAll(",","");
						var isok=balance-tranAmt;
						if(isok<0&&$("balance").style.display!="none"){
							$("balance").disabled = "disabled";
						}else{
							$("balance").disabled = "";
						}
					});
            	}
            	  
            });
        }

        
		function download(flag){
			if(flag==1){
        		DfB2BBatchService.downloadPubDFBkNo(function(data){
        				dwr.engine.openInDownload(data);
				});
			}
			if(flag==2){
				DfB2BBatchService.downloadProvId(function(data){
        				dwr.engine.openInDownload(data);
				});
			}
			if(flag==3){
				DfB2BBatchService.downloadXLSFileBase(function(data){
        				dwr.engine.openInDownload(data);
				});
			}
		}
		function balance(){
			$("payamt").innerHTML=$("orderamt").innerHTML;
			$("trfee").innerHTML=$("feeamt").value;
			$("trpayamt").innerHTML=$("allamt").value;
			$("allAmt_uppercase").innerHTML=$("confirm_transAmt_uppercase").value;
			jQuery("#paypwd").wBox({title:"<img src='../../public/images/xitu.gif'>请确输入您的支付密码",show:true});
		}
		//余额支付
		function payForBalance(){
			if(!window.confirm("确认支付？")){
				return;
			}
		
			var paypwd=$("pay_pwd_bak").value;//支付密码
			if(paypwd==""){
				alert("支付密码不能为空!");
				return;
			}
			
			var batchNo=$("batchnumber").innerHTML;//批次号
			DfB2BBatchService.doActions(batchNo,hex_md5(paypwd),success,function(ret){
				var flag = ret.split("\|")[0];
            	var msg = ret.split("\|")[1];
            	if(flag != "0"){
            		alert(msg);
            		return;
            	}
			
				$("balance").disabled="disabled";
				alert("支付成功！");
				location.href="M_100_PLFKDQYYHK.jsp";
				return;
			});
		}
		
		String.prototype.replaceAll  = function(s1,s2){
			return this.replace(new RegExp(s1,"gm"),s2);   
		}; 
		function initAid(){
		var mid=$("mid").value;
       		 DfB2BBatchService.getJSZHByUid(mid,function(zh){
       			 dwr.util.addOptions("aid", zh);
       		 });
		}
		function pwd_bak(value){
			 $("pay_pwd_bak").value=value;
		}
        //新增文件格式验证代码
        function chkSuffix(file){
            var reg=/(.xls|.xlsx)$/;
            return reg.test(file);
        }
        </script>
        
    </head>
	<!--hasButtonAuth([103,102]); 权限  -->
    <body onload="initAid();">
    
    <div class="style" id="onediv">
    
        <table width="100%"  align="left"  class="tableBorder">
          <tr><td class="title" align="left" colspan="2" >&nbsp;&nbsp;批量付款到企业银行</td></tr>
         	<tr>
             <td class="th1" align="right" width="30%">&nbsp;<b>付款账户：</b></td>
             <td align="left" width="50%"><select style="width: 200px" id="aid" name="aid"></select>  </td>
          </tr>
          <tr>
             <td class="th1" align="right" width="30%">&nbsp;<b>转账交易文件：</b></td>
             <td align="left" width="50%"><input type="file" id="tXtFile" name="tXtFile" value="" style="width: 300px; " accept=".xls,.xlsx" />
             &nbsp;&nbsp;&nbsp;
             </td>
          </tr>
          <tr>
          	<td class="th1" align="right" width="30%">&nbsp;<b>订单描述：</b></td>
          	  <td align="left" width="50%"><input type="text"  id="orderDescribe" name="orderDescribe" value="" />
             &nbsp;&nbsp;&nbsp;</td>
          </tr>
          <tr>
          	<td  align="center" colspan="2">&nbsp;<input class="button" value=" 上 传 "  type="submit" onclick="uploadExcelFile();"/>&nbsp;&nbsp;<input class="button" value="模板下载"  class="button" type="button" onclick="javascript:download(3);" /></td>
          </tr>          
        </table>
		<table width="100%" border="0">
			<tr>
				<td>
					<fieldset>
						<legend>上传说明</legend>
						1、上传格式：Excel2003文档(example.xls) <br />						
						2、<a href="javascript:download(1);">银行编号对应表.txt</a> <br/>
						3、<a href="javascript:download(2);">省份编号对应表.txt</a>
					</fieldset></td>
			</tr>
		</table>
		<div id="tbinfos" style="display: none">
      <table class="tablelist tablelist2" id = "DataTableId">
             <tr>
              <th>序号</th>
              <th>收款人姓名</th>
              <th>收款银行</th>
              <th>收款银行联行行号</th>
              <th>收款帐号</th>
              <th>开户所在省份</th>
              <th>订单金额</th>
              <th>错误信息</th>
           </tr>
           <tbody id="dataList"></tbody>
      </table>
      </div>
      </div>
     <table id="paypwd" class="tableBorder"
		style="display: none;width: 600px;height: 120px;">
		<tbody>

			<tr>
				<td class="th1" align="right" width="30%">&nbsp; 付款金额：</td>
				<td width="70%" align="left">
					<span id="payamt" class="redfont"></span>&nbsp;元
				</td>
			</tr>
			<tr>
				<td class="th1" align="right" width="30%">&nbsp; 服务费：</td>
				<td width="70%" align="left">
					<span id="trfee" class="redfont"></span>&nbsp;元
				</td>
			</tr>
			<tr>
				<td class="th1" align="right" width="30%">&nbsp; 总金额：</td>
				<td width="70%" align="left">
					<span id="trpayamt" class="redfont"></span>&nbsp;元&nbsp;&nbsp;&nbsp;&nbsp;大写：<span id="allAmt_uppercase" class="redfont"></span>
				</td>
			</tr>
			<tr>
				<td class="th1" align="right" width="30%">&nbsp; 支付密码：</td>
				<td width="70%" align="left">
					<input id="payPassword" name="payPassword" type="password" onblur="pwd_bak(this.value);"/>
				</td>
			</tr>
			<tr>
				<td colspan="2" align="center">
				</td>
			</tr>
			<tr>

				<td colspan="2" align="center">
					<input type="button" value="账户余额支付" class="button" onclick="payForBalance();"/>
					&nbsp;&nbsp;
					<input type="button" value="返 回" class="wBox_close button" />
				</td>
			</tr>
		</tbody>
	</table>
     
     <div class="style" id="twodiv" style="display: none">
     	 <table width="100%"  align="center"  class="tableBorder">
          <tr align="center">
          	<td class="title" align="left" colspan="2" >&nbsp;&nbsp;生成付款单</td>
          </tr>
          <tr>
           	 <td class="th2" align="right" width="35%">&nbsp;<b>批次号：</b></td>
             <td align="left"  ><span id="batchnumber" ></span></td>
         </tr>
         <tr>
          <td class="th2" align="right" width="35%">&nbsp;<b>交易总笔数：</b></td>
             <td align="left"  ><span id="count" class="redfont"></span>&nbsp;笔</td>
         </tr>
       	
         <tr>
           	 <td class="th2" align="right" width="35%">&nbsp;<b>订单金额合计：</b></td>
             <td align="left" ><span id="orderamt" class="redfont"></span>&nbsp;元</td>
         </tr>
         <tr>
         	<td class="th2" align="right" width="35%">&nbsp;<b>订单描述：</b></td>
             <td align="left"  ><span id="discr" ></span></td>
         </tr>
         <tr>
         	<td colspan="2" align="center" >
	         	<input type="hidden"  name="mid" id="mid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}" />
	         	<input type="button" id="balance"  name="payway"  class="button"  value = "确认提交 " onclick="balance();"/>&nbsp;&nbsp;&nbsp;
	            <input type="hidden" id="feeamt" value=""/><input type="hidden" id="allamt" value=""/><input type="hidden" id="confirm_transAmt_uppercase" value=""/>
	            <input type="hidden" id="pay_pwd_bak" value=""/>
            </td>
         </tr>
          </table>
     </div>
      
    </body>
</html>