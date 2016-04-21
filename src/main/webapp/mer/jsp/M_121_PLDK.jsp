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
        <title>批量代扣</title>
        <meta http-equiv="pragma" content="no-cache"/>
        <meta http-equiv="cache-control" content="no-cache"/>
        <meta http-equiv="Expires" content="0"/>  
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <link href="../../public/css/head.css?<%=rand %>" rel="stylesheet" type="text/css"/>
        <link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css"/> 
        <script type="text/javascript" src="../../dwr/engine.js"></script>
        <script type="text/javascript" src="../../dwr/util.js"></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand %>'></script>
        <script type="text/javascript" src='../../public/js/ryt.js?<%=rand %>'></script>
        <script type='text/javascript' src="../../public/js/money_util.js?<%=rand%>"></script>
        <script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand %>'></script>
        <script type="text/javascript" src="../../public/js/md5.js"></script>
        <script type="text/javascript" src='../../dwr/interface/DaiKouService.js?<%=rand%>'></script> 
        <script type="text/javascript">
         var success = {};
          var count=1;
          var cellFuncs = [
		      			function(obj) { return count++; },
		      			function(obj) { return obj.dkAccNo; },
		      			function(obj) { return obj.dkAccName; },
		      			function(obj) { return obj.dkBankNo; },
		      			function(obj) { return obj.dkIDType; },
		      			function(obj) { return obj.dkIDNo; },
		      			function(obj) { return obj.dkKZType; },
		      			function(obj) { return obj.dkAmt; },
		      			function(obj) { return obj.remark; },
		      			function(obj) { return obj.errMsg; }
		      		];
		  function uploadExcelFile(){
		    var fileContent=$("dkFile").value;
		    if(fileContent==""){
           		alert("请选择上传文件!");
           		return;
           }else{
	           	flag_index=fileContent.lastIndexOf(".");
	           	var extension=fileContent.substring(flag_index+1,fileContent.length);
	            if(extension!="xls"){
		            alert("上传文件格式为xls!");
		            return;
	            }
           }
            var file = dwr.util.getValue('dkFile');
            loadingMessage("处理中，请稍候......");
            DaiKouService.uploadPLDKExcelFile(file,function(data){
           		  count=1;
           		  $("explain").style.display="none";
				 if(data.errMsg!=null && data.errMsg!=undefined){
				 	alert(data.errMsg);
				 	success={};
				 	return;
				 }else if(!data.flag){
				 	success={};
				 	dwr.util.removeAllRows("dataList");
				 	dwr.util.addRows("dataList",data.fobjs,cellFuncs,{escapeHtml:false});
				 	//统计行
	              var pageTr = $("dataList").insertRow(-1);
	                var pageTd = pageTr.insertCell(-1);
	                pageTd.setAttribute("height","30px");
	                pageTd.setAttribute("colspan",cellFuncs.length);
	                //排序时要设置role="bottom"底部才不会参与排序
	                pageTr.setAttribute("role","bottom");
	                var str = "";
	                str += "<span style='float:left;display:inline-block'>总计 <span class='redfont'>"+data.sum_lines+"</span> 条，其中 <span class='redfont'>" + (data.fobjs.length)+ "</span> 条无效数据,请检查后重新上传!";
	                str = str+"</span>"; 
	                pageTd.innerHTML =str.toString();
	                $("tbinfos").style.display="";
				 }else if(data.sobjs.length>0 && data.fobjs==null){
				 	success = data.sobjs;
				 	$("batchnumber").innerHTML=data.batchNo;
				 	$("count").innerHTML=data.sobjs.length;
				 	$("orderamt").innerHTML=data.sum_amt;
				 	$("miandiv").style.display="none";
				 	$("confirmDiv").style.display="";
				 }else{
				 	alert("数据异常");
				 }
            }
            );
            
		  }
		  /**
		   *提交订单
		   */
		  function submitOrder(){
		  	if(success.length<=0)
				return;
			var batchNo=$("batchnumber").innerHTML;
		  	DaiKouService.submitOrder(batchNo,success,function(flag){
		  		if(flag=="success")
		  			alert("订单提交成功！");
		  		else
		  			alert("订单提交异常！");
		  		location.href=location.href;
		  	});
		  }
        </script>
        
    </head>
    <body>
    <div class="style" id="miandiv">
    
        <table width="100%"  align="left"  class="tableBorder">
          <tr><td class="title" align="left" colspan="2" > 批量代扣</td></tr>
          <tr>
             <td class="th1" align="right" width="30%">&nbsp;<b>上传代扣清单：</b></td>
             <td align="left" width="50%"><input type="file"  class="largeInput" id="dkFile" name="dkFile" value="" style="width: 300px; " />
             </td>
          </tr>
          <!-- <tr>
          	<td class="th1" align="right" width="30%">&nbsp;<b>订单描述：</b></td>
          	  <td align="left" width="50%"><input type="text"  class="largeInput" id="orderDescribe" name="orderDescribe" value="" />
             </td>
          </tr>-->
          <tr>
          	<td  align="center" colspan="2">&nbsp;<input class="button" value=" 上 传 "  type="submit" onclick="uploadExcelFile();"/> &nbsp;<input class="button" value="模板下载"  class="button" type="button" onclick="javascript:download(3);" /></td>
          </tr>          
        </table>
		<table width="100%" border="0" id="explain">
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
              <th>扣款人账号</th>
              <th>扣款人户名</th>
              <th>扣款人开户行号</th>
              <th>扣款人证件类型</th>
              <th>扣款人证件号码</th>
              <th>卡/折标志</th>
              <th>交易金额</th>
              <th>用途</th>
              <th>错误信息</th>
           </tr>
           <tbody id="dataList"></tbody>
      </table>
      </div>
      </div>
    
     <div class="style" id="confirmDiv" style="display: none">
     	 <table width="100%"  align="center"  class="tableBorder">
          <tr align="center"><td class="title" align="left" colspan="2" >&nbsp;&nbsp;生成付款单</td></tr>
          <tr>
           	 <td class="th2" align="right" width="35%">&nbsp;<b>交易批次号：</b></td>
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
         	<td colspan="2" align="center" >
         	<input type="hidden"  name="mid" id="mid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}" />
         	<input style="height: 28px;" type="button" value = "确认提交" onclick="submitOrder();" />
             </td>
         </tr>
          </table>
     </div>
      
    </body>
</html>
