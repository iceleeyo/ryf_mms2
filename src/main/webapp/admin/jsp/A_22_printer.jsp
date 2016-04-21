	<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>打印预览</title>
	 	<script type='text/javascript' src='../../dwr/engine.js'></script>
        <script type='text/javascript' src='../../dwr/util.js'></script>
        <script type="text/javascript" src="../../dwr/interface/RypCommon.js"></script>
        <script type="text/javascript" src="../../dwr/interface/SearchFeeLiqBathService.js"></script>
		<script type='text/javascript' src='../../public/js/ryt.js'></script>
		<script type="text/javascript" src="../../dwr/interface/PageService.js"></script>
		<style type="text/css">
        th
        {
            white-space: nowrap;
        }
        td
        {	padding-top:5px;
        	padding-bottom:5px;
            white-space: nowrap;
        }
        
    </style>
	<script type="text/javascript">
		var hhNum=18;
		String.prototype.getQueryString = function(name)
		{
		var reg = new RegExp("(^|&|\\?)"+ name +"=([^&]*)(&|$)"), r;   
		if ( r=this.match(reg) ) return unescape(r[2]);   
		return "";   
		};
		 var count=1;
		 var sumAmtDY=0;
		 var sumAmtYH=0;
		 var sumAmtZDDY=0;
		 var sumAmtZDYH=0;
		 var cellFuncsBk = [
		 							 function(obj) 	{ return count;},
				                     function(obj) { return obj.mid; },
				                     function(obj) { return mer_category_map[obj.category]; },
				                     function(obj) { return (obj.liqType==1)?"全额结算":"净额结算";},
				                     function(obj) { return obj.batch; },
				                     function(obj) { return obj.lastLiqDate;},
				                     function(obj) { return obj.liqDate;},
				                     function(obj) { return obj.tabDate;},
				                     function(obj) { return obj.abbrev; },
				                     function(obj) { return div100(obj.transAmt); },
				                     function(obj) { return obj.purCnt; },
				                     function(obj) { return div100(obj.refAmt);},
				                     function(obj) { return obj.refCnt; },
				                     function(obj) { return div100(obj.manualAdd); },
				                     function(obj) { return obj.addCnt; },
				                     function(obj) { return div100(obj.manualSub);},
				                     function(obj) { return obj.subCnt;},
				                     function(obj) { return div100(obj.feeAmt); },
				                     function(obj) { return div100(obj.refFee);},
				                     function(obj) { return handleBkName(obj.bankName+"("+obj.bankBranch+")");},
				                     function(obj) { return obj.bankAcctName;},
				                     function(obj) { return obj.bankAcct;},
				                     function(obj) { count++;sumAmtYH+=obj.liqAmt;return div100(obj.liqAmt);}
				                     
				                 ];
		 var cellFuncsZDBk = [
		 							 function(obj) 	{ return count;},
				                     function(obj) { return obj.mid; },
				                     function(obj) { return mer_category_map[obj.category]; },
				                     function(obj) { return (obj.liqType==1)?"全额结算":"净额结算";},
				                     function(obj) { return obj.batch; },
				                     function(obj) { return obj.lastLiqDate;},
				                     function(obj) { return obj.liqDate;},
				                     function(obj) { return obj.tabDate;},
				                     function(obj) { return obj.abbrev; },
				                     function(obj) { return div100(obj.transAmt); },
				                     function(obj) { return obj.purCnt; },
				                     function(obj) { return div100(obj.refAmt);},
				                     function(obj) { return obj.refCnt; },
				                     function(obj) { return div100(obj.manualAdd); },
				                     function(obj) { return obj.addCnt; },
				                     function(obj) { return div100(obj.manualSub);},
				                     function(obj) { return obj.subCnt;},
				                     function(obj) { return div100(obj.feeAmt); },
				                     function(obj) { return div100(obj.refFee);},
				                     function(obj) { return handleBkName(obj.bankName+"("+obj.bankBranch+")");},
				                     function(obj) { return obj.bankAcctName;},
				                     function(obj) { return obj.bankAcct;},
				                     function(obj) { count++;(sumAmtZDYH+=obj.liqAmt);return div100(obj.liqAmt);}
				                 ];
		 var cellFuncsDy = [			
		 							 function(obj) 	{ return count;},
				                     function(obj) { return obj.mid; },
				                     function(obj) { return mer_category_map[obj.category]; },
				                     function(obj) { return (obj.liqType==1)?"全额结算":"净额结算";},
				                     function(obj) { return obj.batch; },
				                     function(obj) { return obj.lastLiqDate;},
				                     function(obj) { return obj.liqDate;},
				                     function(obj) { return obj.tabDate;},
				                     function(obj) { return obj.abbrev; },				                     
				                     function(obj) { return div100(obj.transAmt); },
				                     function(obj) { return obj.purCnt; },
				                     function(obj) { return div100(obj.refAmt);},
				                     function(obj) { return obj.refCnt; },
				                     function(obj) { return div100(obj.manualAdd); },
				                     function(obj) { return obj.addCnt; },
				                     function(obj) { return div100(obj.manualSub);},
				                     function(obj) { return obj.subCnt;},
				                     function(obj) { return div100(obj.feeAmt); },
				                     function(obj) { return div100(obj.refFee);},  
				                     function(obj) { return obj.aname;},
				                     function(obj) { return obj.aid;},
				                     function(obj) {count++;(sumAmtDY+=obj.liqAmt); return div100(obj.liqAmt);}
				                 ];
		  var cellFuncsZDDy = [			
		 							 function(obj) 	{ return count;},
				                     function(obj) { return obj.mid; },
				                     function(obj) { return mer_category_map[obj.category]; },
				                     function(obj) { return (obj.liqType==1)?"全额结算":"净额结算";},
				                     function(obj) { return obj.batch; },
				                     function(obj) { return obj.lastLiqDate;},
				                     function(obj) { return obj.liqDate;},
				                     function(obj) { return obj.tabDate;},
				                     function(obj) { return obj.abbrev; },				                     
				                     function(obj) { return div100(obj.transAmt); },
				                     function(obj) { return obj.purCnt; },
				                     function(obj) { return div100(obj.refAmt);},
				                     function(obj) { return obj.refCnt; },
				                     function(obj) { return div100(obj.manualAdd); },
				                     function(obj) { return obj.addCnt; },
				                     function(obj) { return div100(obj.manualSub);},
				                     function(obj) { return obj.subCnt;},
				                     function(obj) { return div100(obj.feeAmt); },
				                     function(obj) { return div100(obj.refFee);},  
				                     function(obj) { return obj.aname;},
				                     function(obj) { return obj.aid;},
				                     function(obj) {count++;(sumAmtZDDY+=obj.liqAmt); return div100(obj.liqAmt);}
				                 ];	
	function printPage(){
		document.getElementById("pnt").style.display="none";
		document.getElementById("ps").style.display="none";
		window.print();
		window.close();
	}			              			                 
	function ref(){
		 var mid =  window.location.href.getQueryString("mid").trim();
	      var date_b = window.location.href.getQueryString("dateb").trim();
		  var date_e = window.location.href.getQueryString("datee").trim();
		  var merType = window.location.href.getQueryString("merType").trim();
		  var liqGid = window.location.href.getQueryString("liqGid").trim();
		  var gid = window.location.href.getQueryString("gid").trim();
		  if (mid!='' && !isFigure(mid)){
			  alert("商户号只能是整数!");$("mid").value='';
		      return false;
		  }
		  if (date_b == '' || date_e == ''){
		      alert("查询日期错误!");
		      return false;
		  }
		  if (!judgeDate(date_b, date_e)){
		      alert("开始日期应先于结束日期");
		      return false;
		  }
    	  if(liqGid==''){
			  alert("结算渠道不能为空!");
			  return; 
		  }else if(liqGid == 0||liqGid==2){
		  	document.getElementById("bkZhTb").style.display="block";
		  	document.getElementById("dyZhTb").style.display="none";
		  	document.getElementById("ZdbkZhTb").style.display="none";
		  	document.getElementById("ZddyZhTb").style.display="none";
		  	SearchFeeLiqBathService.queryPrintTableData(merType,mid,date_b, date_e,liqGid,gid,function(resData){
		  		dwr.util.addRows("TableOne",resData,cellFuncsBk,{escapeHtml:false});
		  		document.getElementById("sumYH").innerText=div100(sumAmtYH);
		  	});
		  }else if(liqGid ==3){
		  	document.getElementById("ZdbkZhTb").style.display="block";
		  	document.getElementById("bkZhTb").style.display="none";
		  	document.getElementById("dyZhTb").style.display="none";
		  	document.getElementById("ZddyZhTb").style.display="none";
		  	SearchFeeLiqBathService.queryPrintTableData(merType,mid,date_b, date_e,liqGid,gid,function(resData){
		  		dwr.util.addRows("TableThree",resData,cellFuncsZDBk,{escapeHtml:false});
		  		document.getElementById("sumZdYH").innerText=div100(sumAmtZDYH);
		  		
		  	});
		  }
		  else if(liqGid==1 || liqGid==5){
		  	document.getElementById("dyZhTb").style.display="block";
		  	document.getElementById("bkZhTb").style.display="none";
		  	document.getElementById("ZdbkZhTb").style.display="none";
		  	document.getElementById("ZddyZhTb").style.display="none";
		  	SearchFeeLiqBathService.queryPrintTableData(merType,mid,date_b, date_e,liqGid,gid,function(resData){
		  		dwr.util.addRows("TableTwo",resData,cellFuncsDy,{escapeHtml:false});
		  		document.getElementById("sumDY").innerText=div100(sumAmtDY);
		  	});
		  }else if(liqGid==4){
		    document.getElementById("ZddyZhTb").style.display="block";
		  	document.getElementById("bkZhTb").style.display="none";
		  	document.getElementById("dyZhTb").style.display="none";
		  	document.getElementById("ZdbkZhTb").style.display="none";		
		  	SearchFeeLiqBathService.queryPrintTableData(merType,mid,date_b, date_e,liqGid,gid,function(resData){
		  		dwr.util.addRows("TableFour",resData,cellFuncsZDDy,{escapeHtml:false});
		  		document.getElementById("sumZDDY").innerText=div100(sumAmtZDDY);
		  	});
		  }else{
		  	alert("结算对象类型错误!");
		  	return;
		  }
			}
			
	function printSetup(){
		wb.execwb(8,1); 
	}
	//function printPreview(){
		//wb.execwb(7,1);
	//}
	function handleBkName(bkName){
		var len=bkName.length;
		var shang=parseInt(len/hhNum);
		var yu=len%hhNum;
		var resStr="";
		if(shang>0){
			for(var i=0;i<shang;i++){
				resStr+=bkName.substring(i*hhNum,(i*hhNum+hhNum))+"</br>";
			}
			if(yu>0){
				resStr+=bkName.substring(shang*hhNum,bkName.length);
			}
			else if(yu==0){
				resStr=resStr.substring(0, resStr.length-5);
			}
			return resStr;
		}else{
			return bkName;
		}
	}
	
	function hideArea(){
			document.getElementById("ps").style.display="none";
		  	document.getElementById("pnt").style.display="none";
	}
	</script>
</head>
<body onload="ref();">
	<div align="left" id="ps">
		* 注意，如果您使用的IE浏览器且第一次在本机使用打印功能，请参照如下设置<br/>
	     &nbsp; 1.工具-Internet选项-安全-可信任站点-站点-添加。(将该站点添加到可信任列表)<br/>
	     &nbsp; 2.工具-Internet选项-安全-可信任站点，将该区域的安全级别设置为“低”，并保存。<br/>
	     &nbsp; 3.点击打印设置，将“启用缩小字体填充”勾上，“页眉”以及“页脚”下的选项建议全部选择空。
	</div>
	<div id="pnt" align="left">
		<br/>
		<OBJECT id=wb height=0 width=0    
		classid=CLSID:8856F961-340A-11D0-A96B-00C04FD705A2 name="wb"></OBJECT>  
		<input type="button" class="button"  onclick="printPage();" value=" 打 印 "/>
		<input type="button" class="button"  onclick="printSetup();" value=" 打印设置 "/>
		<hr/><div align="right"><span onclick="hideArea();" style="cursor:pointer;">&uarr;隐藏以上区域</span></div>
		
	</div>
<div  id="bkZhTb" style="display: none;">
<div align="center"><b style="font-size:35px ">商户资金结算表-银行账户</b></div><br/>
	<table>
	<tr>
		<td colspan="21">
		<table border="thin" cellspacing="0" ><!-- border-collapse:collapse; -->
		<thead>
		<tr>
		<th>序号</th><th>商户号</th><th>商户类别</th><th>结算类型</th><th>批次号</th><th>初始日期</th><th>截止日期</th><th>制表日期</th><th>商户名称</th><th>交易金额</th><th>交易笔数</th><th>退款金额</th>
		<th>退款笔数</th><th>手工调增<br/>金额</th><th>手工调增<br/>笔数</th><th>手工调减<br/>金额</th><th>手工调减<br/>笔数</th><th>系统手续费</th>
		<th>退回商户<br/>手续费</th><th>开户银行名</th><th>开户账户名称</th><th>开户账户号</th><th>应结算金额</th>
		</tr></thead>
	    <tbody id="TableOne" style="text-align: center;"></tbody>
		</table>
		</td>
	</tr>
	<tr><td colspan="21"><br/></td></tr>
	<tr style="font-size: large;" >
			<td colspan="2" width="9.4%" align="left">制 表:</td><td></td><td  colspan="2" width="9.4%" align="right">复核:</td><td > </td>
		    <td colspan="2" align="right" width="9.4%">结算主管:</td><td ></td><td colspan="2" align="right" width="9.4%">划款制单:</td><td></td>
		    <td colspan="2" align="right" width="9.4%">划款复核:</td><td></td><td colspan="2" align="right" width="9.4%">划款时间:</td><td> </td><td align="right"  colspan="3" width="14.1%">总计:&nbsp;&nbsp;<span id="sumYH"></span>&nbsp;元</td>
	</tr>
		
		</table>
</div>

<div id="dyZhTb" style="display: none;">
	<div align="center"><b style="font-size:35px ">商户资金结算表-电银账户</b></div><br/>
	<table>
	<tr>
		<td colspan="21">
	<table  border="thin"  cellspacing="0" >
		<thead>
		<tr>
		<th>序号</th><th>商户号</th><th>商户类别</th><th>结算类型</th><th>批次号</th><th>初始日期</th><th>截止日期</th><th>制表日期</th><th>商户名称</th>
		<th>交易金额</th><th>交易笔数</th><th>退款金额</th><th>退款笔数</th><th>手工调增<br/>金额</th><th>手工调增<br/>笔数</th><th>手工调减<br/>金额</th>
		<th>手工调减<br/>笔数</th><th>系统手续费</th><th>退回商户<br/>手续费</th><th>电银账户名</th><th>电银账户号</th><th>应结算金额</th>
		</tr></thead>
	    <tbody id="TableTwo" style="text-align: center;"></tbody>
	</table>
	</td>
	</tr>
	<tr><td colspan="21"><br/></td></tr>
	<tr style="font-size: large;">
			<td colspan="2" width="9.4%" align="left">制 表:</td><td></td><td  colspan="2" width="9.4%" align="right">复核:</td><td > </td>
		    <td colspan="2" align="right" width="9.4%">结算主管:</td><td ></td><td colspan="2" align="right" width="9.4%">划款制单:</td><td></td>
		    <td colspan="2" align="right" width="9.4%">划款复核:</td><td></td><td colspan="2" align="right" width="9.4%">划款时间:</td><td> </td><td align="right"  colspan="3" width="14.1%">总计:&nbsp;&nbsp;<span id="sumDY"></span>&nbsp;元</td>
	</tr>
		
		</table>
	</div>
<div id="ZdbkZhTb" style="display: none;">	
<div align="center"><b style="font-size:35px ">商户资金结算表-银行账户</b></div><br/>
	<table>
	<tr>
		<td colspan="21">
		<table border="thin" cellspacing="0" ><!-- border-collapse:collapse; -->
		<thead>
		<tr>
		<th>序号</th><th>商户号</th><th>商户类别</th><th>结算类型</th><th>批次号</th><th>初始日期</th><th>截止日期</th><th>制表日期</th><th>商户名称</th><th>交易金额</th><th>交易笔数</th><th>退款金额</th>
		<th>退款笔数</th><th>手工调增<br/>金额</th><th>手工调增<br/>笔数</th><th>手工调减<br/>金额</th><th>手工调减<br/>笔数</th><th>系统手续费</th>
		<th>退回商户<br/>手续费</th><th>开户银行名</th><th>开户账户名称</th><th>开户账户号</th><th>应结算金额</th>
		</tr></thead>
	    <tbody id="TableThree" style="text-align: center;"></tbody>
		</table>
		</td>
	</tr>
	<tr><td colspan="20"><br/></td></tr>
	<tr style="font-size: large;" >
			<td colspan="2" width="9.4%" align="left">制 表:</td><td></td><td  colspan="2" width="9.4%" align="right">复核:</td><td > </td>
		    <td colspan="2" align="right" width="9.4%">结算主管:</td><td ></td><td colspan="2" align="right" width="9.4%">划款制单:</td><td></td>
		    <td colspan="2" align="right" width="9.4%">划款复核:</td><td></td><td colspan="2" align="right" width="9.4%">划款时间:</td><td> </td><td align="right"  colspan="3" width="14.1%">总计:&nbsp;&nbsp;<span id="sumZdYH"></span>&nbsp;元</td>
	</tr>
		
		</table>
</div>
<div id="ZddyZhTb" style="display: none;">
<div align="center"><b style="font-size:35px ">商户资金结算表-电银账户</b></div><br/>
	<table>
	<tr>
		<td colspan="21">
		<table border="thin" cellspacing="0" ><!-- border-collapse:collapse; -->
		<thead>
		<tr>
		<th>序号</th><th>商户号</th><th>商户类别</th><th>结算类型</th><th>批次号</th><th>初始日期</th><th>截止日期</th><th>制表日期</th><th>商户名称</th><th>交易金额</th><th>交易笔数</th><th>退款金额</th>
		<th>退款笔数</th><th>手工调增<br/>金额</th><th>手工调增<br/>笔数</th><th>手工调减<br/>金额</th><th>手工调减<br/>笔数</th><th>系统手续费</th>
		<th>退回商户<br/>手续费</th><th>电银账户名</th><th>电银账户号</th><th>应结算金额</th>
		</tr></thead>
	    <tbody id="TableFour" style="text-align: center;"></tbody>
		</table>
		</td>
	</tr>
	<tr><td colspan="20"><br/></td></tr>
	<tr style="font-size: large;" >
			<td colspan="2" width="9.4%" align="left">制 表:</td><td></td><td  colspan="2" width="9.4%" align="right">复核:</td><td > </td>
		    <td colspan="2" align="right" width="9.4%">结算主管:</td><td ></td><td colspan="2" align="right" width="9.4%">划款制单:</td><td></td>
		    <td colspan="2" align="right" width="9.4%">划款复核:</td><td></td><td colspan="2" align="right" width="9.4%">划款时间:</td><td> </td><td align="right"  colspan="3" width="14.1%">总计:&nbsp;&nbsp;<span id="sumZDDY"></span>&nbsp;元</td>
	</tr>
		
		</table>
</div>
</body>
</html>