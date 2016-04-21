<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
<%
response.setHeader("Pragma","No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", 0);
int rand = new java.util.Random().nextInt(10000);
%>  
	<meta http-equiv="pragma" content="no-cache"/>
	<meta http-equiv="cache-control" content="no-cache"/>
	<meta http-equiv="expires" content="0"/>    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3"/>
		<title>银行对账文件配置</title>
		<link href="../../public/css/head.css" rel="stylesheet" type="text/css"/>
	    <link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css"/>
		<script type="text/javascript" src="../../dwr/engine.js?<%=rand%>"></script>
		<script type="text/javascript" src="../../dwr/util.js?<%=rand%>"></script>
		<script type="text/javascript" src="../../dwr/interface/SysManageService.js?<%=rand%>"></script>
		<script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
		<script type="text/javascript" src='../../public/js/ryt.js?<%=rand%>'></script>
        <script type="text/javascript">
        var way_gate_map={};
         window.onload=function(){
         	PageService.getWayGateMap(function(wayMap){//增值业务的gate
 			   /*for(var attr in wayMap){//复制属性
 			   		h_merge_bk_1[attr]=wayMap[attr];
 			   }*/
 			   way_gate_map=wayMap;
 			   dwr.util.addOptions("gate",wayMap);
 			});
         	queryFileConfigPage(1);
         }
         //查询所有银行的对账文件格式配置
         function queryFileConfigPage(pageNo){
            SysManageService.querySettleFileForm(pageNo,backFun);
         }
         //查询后所有后的回调函数
         function backFun(configList){
	           var cellFuncs = [
	             function(obj) { return way_gate_map[obj.gate]; },
	             function(obj) { return obj.dtBegin; },
	             function(obj) { return obj.dtSep; },
	             function(obj) { return obj.rowSize; },
	             function(obj) { return obj.tseqCol; },
	             function(obj) { return obj.bkSeqCol; },
	             function(obj) { return obj.merOidCol; },
	             function(obj) { return obj.dateCol;},
	             function(obj) { return obj.amtCol;},
	             function(obj) { return obj.amtForm=="Y"?"元":"分"; },
	             function(obj) { return obj.className; },
	             function(obj) { return "<input name='' type='button' value='修改' onclick='querySettleFileForm("+obj.gate+")' />"; }
	         	]
  			paginationTable(configList,"fileConfigList",cellFuncs,"","queryFileConfigPage");
         }
         //新增配置
         function addSettleFileConfig(flag){
       		 var objMap={"gate":{"name":"银行名称","notNull":true} };
       		 if(!validateObj(objMap))return;
       		 var fileConfigForm= wrapObj("settleFileConfigForm");
       		 SysManageService.addBkSettleFileConfig(fileConfigForm,editOrSaveBackFun);
         }
         //提交修改
         function subEditSettleFileForm(){
          	  var fileConfigForm= wrapObj("update_settleFileConfigForm",function(attrName){
       		 		     return attrName.replace("update_","");
       		 	});

       		 SysManageService.editSettleFileForm(fileConfigForm,editOrSaveBackFun);
         }
         function editOrSaveBackFun(backData){
         		if(backData=="ok"){
         			alert("操作成功！");
         			window.location.reload();
         		}else{
         		    alert(backData);
         		}
         }
         //查询单个银行的配置
         function querySettleFileForm(gateId){//detailBox 
         	 SysManageService.getSettleFileFormById(gateId,function(settleFileFormObj){
         	     jQuery("#update_settleFileConfigDiv").wBox({title:"银行对账文件配置修改",show:true});//显示box
         	 	 disbandObj(settleFileFormObj);
         	 });
         	 
         }
		 //拆散对象，给input赋值
		 function disbandObj(obj){
		 	for(var attr in obj){
		       if(obj[attr]!=null){
		       		document.getElementById("update_"+attr).value=obj[attr];
		       		if(attr=="gate"){
		       			document.getElementById("gateName").innerHTML=way_gate_map[obj[attr]];
		       		};
		       }
		 	}
		 }
		 function showConfig(){
		    var config_style=document.getElementById('fileConfigTable_demo').style;
		    if(config_style.display=="none"){
			  document.getElementById('fileConfigTable_demo').style.display='';
		    }else{
		      document.getElementById('fileConfigTable_demo').style.display='none';
		    }
		 }
        </script>
	</head>
  <body>
	  <div class="style">
	  <form action="#" id="settleFileConfigForm">
	  <table class="tableBorder" id="settleFileConfigTable">
	  <tr>
	  	<td colspan="4" class="title">&nbsp; 银行对账文件配置    /&nbsp;说明：(数据的位置都是从0开始，由技术人员配置<font color="red">*</font>)</td>
	  </tr>
	  <tr>
	  	<td class="th1" align="right" width="20%">银行名称：</td>
	  	<td width="30%"><select id="gate"><option value="">全部...</option></select>&nbsp;</td>
	  	<td class="th1" align="right" width="20%">数据开始行数：</td>
	  	<td><input type="text" id="dtBegin" maxlength="3"/>&nbsp;</td>
	  </tr>
	  <tr>
	  	<td class="th1" align="right">数据分割符：</td>
	  	<td><input type="text" id="dtSep"  maxlength="10"/>&nbsp;&nbsp;(支持正则表示式)</td>  
	  	<td class="th1" align="right">数据总项数：</td>
	  	<td><input type="text" id="rowSize" maxlength="3"/>&nbsp;</td>
	  </tr>
	  <tr>
	  	<td class="th1" align="right">电银流水号位置：</td>
	  	<td><input type="text" id="tseqCol" maxlength="3"/>&nbsp;</td>
	  	<td class="th1" align="right">银行流水号位置：</td>
	  	<td><input type="text" id="bkSeqCol" maxlength="3"/>&nbsp;</td>
	  </tr>
	  <tr>
	  	<td class="th1" align="right">商户订单号位置：</td>
	  	<td><input type="text" id="merOidCol" maxlength="3"/>&nbsp;</td>
	  	<td class="th1" align="right">交易日期位置：</td>
	  	<td><input type="text" id="dateCol" maxlength="3"/>&nbsp;</td>
	  </tr>
	 <tr>
	  	<td class="th1" align="right">交易金额位置：</td>
	  	<td><input type="text" id="amtCol" maxlength="3"/>&nbsp;</td>
	  	<td class="th1" align="right">交易金额单位：</td>
	  	<td>
	  	<select id="amtForm" style="width: 50px;"><option value="Y">元</option><option value="F">分</option></select>
	  	&nbsp;</td>
	  </tr>
	 <tr>
	  	<td class="th1" align="right">实现类的名称：</td>
	  	<td><input type="text" id="className" maxlength="50" style="width: 260px;"/></td>
	  	<td class="th1" align="right"></td>
	  	<td></td>
	  </tr>
	  <tr align="center">
	  	<td colspan="4"><input type="button" value="新增" class="button" onclick="addSettleFileConfig()"/></td>
	  </tr>
	  </table>
	  </form>
	  <table class="tablelist tablelist2" id="fileConfigTable">
	  <thead>
	  <tr>
	  <th>银行名称</th><th>数据开始行数</th><th>数据分割符</th>
	  <th>数据总列数</th><th>电银流水位置</th><th>银行流水位置</th><th>商户订单号位置</th>
	  <th>交易日期位置</th><th>交易金额位置</th><th>交易金额单位</th><th>实现类的名称</th>
	  <th>操作</th></tr></thead>
	  <tbody id="fileConfigList"></tbody>
	  </table>
	  
	  <a href="javascript:showConfig();">&gt;&gt;查看如何配置</a>
<table id="fileConfigTable_demo" class="tablelist" style="display:none;">
	  <thead>
	  <tr>
	  <th>银行名称</th><th>数据开始行数</th><th>数据分割符</th>
	  <th>数据总列数</th><th>电银流水位置</th><th>银行流水位置</th><th>商户订单号位置</th>
	  <th>交易日期位置</th><th>交易金额位置</th><th>交易金额单位</th><th>实现类的名称</th></tr>
	  </thead>
	  <tbody id="fileConfigList">
	  <tr><td>广发银行(增值)</td><td>2</td><td>\|</td><td>3</td><td>100</td><td>100</td><td>1</td><td>0</td><td>2</td><td>元</td><td></td></tr>
	  <tr><td>北京银行(增值)</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td>分</td><td>com.rongyifu.mms.settlement.CheckDataBJImp</td></tr>
	  <tr><td>光大银行(增值)</td><td>0</td><td>\|</td><td>14</td><td>100</td><td>100</td><td>3</td><td>1</td><td>7</td><td>元</td><td></td></tr>
	  <tr><td>交通银行(增值)</td><td>10</td><td>\s{2,}</td><td>10</td><td>100</td><td>4</td><td>1</td><td>2</td><td>6</td><td>元</td><td></td></tr>
	  </tbody>
	  </table>
	  
	  
	  <div style="display: none;" id="update_settleFileConfigDiv">
	<form action="#" id="update_settleFileConfigForm" >
	<table class="detailBox tableBorder" id="settleFileConfigTable2" style="width: 700px;height: 200px;">
	  <tr>
	  	<td class="th1" align="right">银行名称：</td>
	  	<td><input id="update_gate"  type="hidden"/><span id="gateName"></span></td>
	  	<td class="th1" align="right">数据开始行数：</td>
	  	<td><input type="text" id="update_dtBegin" maxlength="3"/>&nbsp;</td>
	  </tr>
	  <tr>
	  	<td class="th1" align="right">数据分割符：</td>
	  	<td><input type="text" id="update_dtSep"  maxlength="10"/>&nbsp;&nbsp;(支持正则表示式)</td>  
	  	<td class="th1" align="right">数据总项数：</td>
	  	<td><input type="text" id="update_rowSize" maxlength="3"/>&nbsp;</td>
	  </tr>
	  <tr>
	  	<td class="th1" align="right">电银流水号位置：</td>
	  	<td><input type="text" id="update_tseqCol" maxlength="3"/>&nbsp;</td>
	  	<td class="th1" align="right">银行流水号位置：</td>
	  	<td><input type="text" id="update_bkSeqCol" maxlength="3"/>&nbsp;</td>
	  </tr>
	  <tr>
	  	<td class="th1" align="right">商户订单号位置：</td>
	  	<td><input type="text" id="update_merOidCol" maxlength="3"/>&nbsp;</td>
	  	<td class="th1" align="right">交易日期位置：</td>
	  	<td><input type="text" id="update_dateCol" maxlength="3"/>&nbsp;</td>
	  </tr>
	 <tr>
	  	<td class="th1" align="right">交易金额位置：</td>
	  	<td><input type="text" id="update_amtCol" maxlength="3"/>&nbsp;</td>
	  	<td class="th1" align="right">交易金额单位：</td>
	  	<td>
	  	<select id="update_amtForm" style="width: 50px;"><option value="Y">元</option><option value="F">分</option></select>
	  	&nbsp;</td>
	  </tr>
	 <tr>
	  	<td class="th1" align="right">实现类的名称：</td>
	  	<td colspan="3"><input type="text" id="update_className" style="width: 260px;"/>&nbsp;</td>
	  </tr>
	  <tr align="center">
	  	<td colspan="4" height="25px">
	  	<input type="button" value="修 改" class="button" onclick="subEditSettleFileForm()"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	  	<input type="button" value="关闭" class="button wBox_close" /></td>
	  </tr>
	  </table>
	  </form>
	  </div>
	  </div>
  </body>
</html>
