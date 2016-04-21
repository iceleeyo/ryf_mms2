<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
         <title>代付失败报警配置</title>
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
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
        <link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css"/>
        <link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css"></link>
  		<script type='text/javascript' src='../../dwr/engine.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../dwr/util.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/DfSbBjPzService.js?<%=rand%>'></script>
		<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>' ></script>
		<script type='text/javascript' src='../../public/js/ryt_util.js?<%=rand%>' ></script>
		
<script type="text/javascript">
var gids={};
var query_cash={};
		function init(){
			DfSbBjPzService.initDFYEBJ(function(data){
				 gids=data;
				 dwr.util.addOptions("gid", data);
			});	
		}

		var cellFuncs = [
		      			function(obj) { return obj.name; },
		      			function(obj) { return obj.failCount; },
		      			function(obj) { return obj.sucRate; },
		      			function(obj) { return "<button onclick='edit("+obj.gid+");'>修  改</button>"; }
		      		];
		
		function queryBankInfo(pageNo){
			var gid=$("gid").value;
			DfSbBjPzService.queryDFBankInfo(gid,pageNo,callback);
			}
			
		function callback(b2egates){
				$("bankInfo").style.display="";
				dwr.util.removeAllRows("resultList");
				if(b2egates.length<=0){
					alert("银企直连网关信息不存在!");
					return;
				}
				  for(var i=0;i<b2egates.pageItems.length;i++){
		     		  var g = b2egates.pageItems[i];
		     		  query_cash[g.gid] = g;
		     	  }
		paginationTable(b2egates, "resultList", cellFuncs, "", "queryBankInfo");
		}
		
		function  edit(gid){
			if(gid!="")
			 	jQuery("#editPanel").wBox({title:"代付报警配置",show:true});
			 else{
			 	alert("网关号为空！");
			 	return;
			 }
			var b2eGate=query_cash[gid];
		 	$("update_gid").value=b2eGate.gid;
		 	$("fail_count").value=b2eGate.failCount;
			$("suc_rate").value=b2eGate.sucRate;
			 
		}
		
		function updateGate(){
			var fcount=$("fail_count").value.trim();
			var sucrate=$("suc_rate").value.trim();
			var gid=$("update_gid").value;
			if(gid==""){
				alert("信息异常！");
				return;
			}
			
			if(!isNumber(fcount)){
				alert("格式错误！");
				return;
			}
			
			DfSbBjPzService.updateB2eGateConfig(gid,fcount,sucrate,function(flag){
				if(flag==true)
					alert("修改成功!");
				else
					alert("修改失败！");
				jQuery("#editPanel").click();
				queryBankInfo(1);
			});
		}
		
		</script>

</head>
<body onload="init();">
 <div class="style">
     <table width="100%"  class="tableBorder">
        <tbody>
            <tr>
                <td class="title" colspan="6">&nbsp; 代付失败交易报警配置</td>
            </tr>
            <tr>
				 <td class="th1" align="left" width="11%">&nbsp;代付渠道：</td>
                <td colspan="5" align="left" width="30%">
					<select id="gid">
						<option value="">请选择...</option>
					</select>
				</td>
            </tr>
               <tr>
           		<td colspan="6" align="center" style="height: 30px">
				<input class="button" type="button" value=" 查 询 " onclick="queryBankInfo(1);" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</td>
            </tr>
        </tbody>
    </table>
    <br/>
    
    
   <table class="tablelist tablelist2" id="bankInfo">
    <thead><tr><th >代付银行</th><th >连续失败笔数</th><th>成功率</th><th>操作</th></tr></thead>
    <tbody id="resultList"></tbody>
    </table>

    <table class="tableBorder" id="editPanel" style="display:none;width:300px;height:120px;margin-top:0px;">
        <tbody>
            <tr>
				<td class="th1" bgcolor="#D9DFEE" align="right" width="35%">失败笔数:</td>
				<td align="left">
					<input type="text" id="fail_count" size="10" />笔 
				</td>
			</tr>
            <tr>
                <td class="th1" align="right">&nbsp;成功率：</td>
                <td align="left">
               		 <select id="suc_rate" style="width: 90px;" >
               		 	<option value="10">10</option>
               		 	<option value="20">20</option>
               		 	<option value="30">30</option>
               		 	<option value="40">40</option>
               		 	<option value="50">50</option>
               		 	<option value="60">60</option>
               		 	<option value="70">70</option>
               		 	<option value="80">80</option>
               		 	<option value="90">90</option>
               		 	<option value="100">100</option>
               		 </select>%
                </td>
            </tr>
            <tr>
               <td colspan="4" align="center" height="20px;">
               <input type="hidden"  id="update_gid"  name="update_gid" />
				<input value="确  认"	type="button" onclick="updateGate();" class="wBox_close button"  /> 
               </td>
            </tr>
        </tbody>
    </table>
    
</div>
</body>
</html>