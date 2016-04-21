<%@ page language="java" pageEncoding="UTF-8" %>
<% String path = request.getContextPath(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>商户资金归集审核</title>
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
        <link href="../../public/css/head.css?<%=rand%>" rel="stylesheet" type="text/css"/>
        <link href="../../public/css/wbox.css?<%=rand%>" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="../../dwr/engine.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../dwr/util.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../dwr/interface/PageService.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../dwr/interface/PrepPayService.js?<%=rand%>"></script>
		<script type="text/javascript" src="../../public/datePicker/WdatePicker.js?<%=rand%>"></script>
		<script type="text/javascript" src="../../public/js/ryt.js?<%=rand%>"></script>
		<script type="text/javascript">
		var merge_state_map={0:"交易申请",2:"交易成功",3:"交易失败"};
		var bk_no_map={};
		window.onload= function initOptions(){
            	dwr.util.addOptions("mergeState",merge_state_map);
            	PrepPayService.getBkNoMap(function(data){
            		bk_no_map=data; 
            		dwr.util.addOptions("bankNo", data);
            	});
		}
		//资金归集审核
		function queryMergeReq(pageNo){
				var dateType=$("dateType").value;
				var bdate=$("bdate").value;
				var edate=$("edate").value;
				var mergeState=$("mergeState").value;
				var bkNo=$("bankNo").value;
				if(bdate==""||edate==""){
					alert("请选择起始日期和结束日期！");
					return false;
				}
				var trType=1;//1-资金归集
				PrepPayService.queryTransPage( pageNo,dateType,bdate, edate, bkNo, trType, mergeState,callbackFun);
			}
		//回调函数
		function callbackFun(pageObj){ 
	    	$("mergerListTable").style.display="";
	    	 dwr.util.removeAllRows("mergerList");
		     var cellFuncs =[
		               function(obj) {  
		                    if(obj.trState==0){
		                      return "<input name='mergerCheck' type='checkbox' value='"+obj.id+"'/>"
		                    } 
		                    return "";
		                },
		                function(obj) { return bk_no_map[obj.bkNo]; },
		                function(obj) { return obj.addOperId; },
		                function(obj) { return obj.trDate+" "+getStringTime(obj.trTime); },
		                function(obj) { return div100(obj.trAmt); },
		                function(obj) { return merge_state_map[obj.trState]; },
		                function(obj) { return obj.valiOperId; },
		                function(obj) { return obj.valiDate==0?"":obj.valiDate; },
		                function(obj) { return createTip(8,obj.remark); }
		                ]
		       var str="&nbsp;&nbsp;<input value='全选' type='button' onclick='checkReverseByName(\"mergerCheck\")'/>&nbsp;&nbsp;";
		            str+="&nbsp;&nbsp;<input value='审核成功' type='button' onclick='verifyMerger(2)'/>&nbsp;&nbsp;&nbsp;&nbsp;";
		            str+="&nbsp;&nbsp;<input value='审核失败' type='button' onclick='verifyMerger(3)'/>";
		    paginationTable(pageObj,"mergerList",cellFuncs,str,"queryMergeReq");
	    }
	   	  //审核成功、失败
	     function verifyMerger(handleType){
	          		var idArr=getValuesByName("mergerCheck");
	          		 if(idArr.length<1){
	          		 	alert("请选择至少一条记录！");
	          		 	return false;
	          		 }
	          		 if(!confirm("你确认提交操作？"))return;
	          		 PrepPayService.verifyMergerFund(handleType,idArr,function(backData){
	          		 			if(backData=="ok"){
	          		 			   alert("操作成功！");
	          		 			   queryMergeReq(1);
	          		 			}else{
	          		 			   alert(backData);
	          		 			}
	          		 });
	     }
		</script>
    </head>
    <body >
    <div class="style">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" colspan="8">&nbsp;&nbsp;&nbsp;&nbsp;商户资金归集审核</td></tr>
            <tr>
                <td class="th1" align="right" width="17%">
                	<select id="dateType"><option value="1">归集申请日期</option><option value="3">归集审核日期</option></select>
                </td>
                <td align="left" width="30%">
                    <input id="bdate" size="15px"
                    name="bdate" class="Wdate" type="text" onfocus="ryt_area_date('bdate','edate',0,30,0)" />
                    &nbsp;至&nbsp;
                    <input id="edate" size="15px" name="edate" class="Wdate" type="text" disabled="disabled" /><font color="red">*</font>
                </td>
                <td class="th1" align="right" width="17%">归集状态：</td>
                <td align="left" >
                <select style="width: 150px" id="mergeState" name="mergeState">
                    	<option value="">全部...</option>
                	</select>
                </td>
             </tr>
             <tr>
               <td class="th1" align="right" >银行：</td>
                <td align="left"><span id="span_authType" class="span_val"></span>
                	<select style="width: 150px" id="bankNo" name="bankNo">
                    	<option value="">全部...</option>
                	</select>
                </td>
                <td class="th1" align="right" ></td>
                <td align="left" >
                </td>
            </tr>
            <tr>
            <td colspan="8" align="center" style="height: 30px">
                <input class="button" type="button" value = " 查 询 " onclick="queryMergeReq(1);" />
            </td>
            </tr>
        </table>

       <table  class="tablelist tablelist2" id="mergerListTable" style="display:none;" >
           <thead>
           <tr>
             <th sort="false">选择</th><th>银行</th><th>归集请求操作员ID</th>
             <th>归集时间</th><th>归集金额</th>
             <th>归集状态</th><th>归集审核操作员ID</th>
             <th>归集审核时间</th><th>归集原因</th>
           </tr>
           </thead>
           <tbody id="mergerList"></tbody>
       </table>
        </div>
    </body>
</html>
