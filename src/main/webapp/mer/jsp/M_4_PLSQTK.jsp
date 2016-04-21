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
        <title>批量退款申请</title>
        <meta http-equiv="pragma" content="no-cache"/>
        <meta http-equiv="cache-control" content="no-cache"/>
        <meta http-equiv="Expires" content="0"/>  
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <link href="../../public/css/head.css?<%=rand %>" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="../../dwr/engine.js"></script>
        <script type="text/javascript" src="../../dwr/util.js"></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand %>'></script>
        <script type="text/javascript" src='../../public/js/ryt.js?<%=rand %>'></script>
        <script type="text/javascript" src='../../dwr/interface/MerRefundmentService.js?<%=rand %>'></script>
        <script type="text/javascript">
        var listArray = new Array();
        function uploadBatchFile(){
            var tt = document.getElementById("refund_type").value;

            document.getElementById("hidden_tt").value = tt ;
            
            var path = document.getElementById("RefTXTfileid").value;
            if (path == '') {
                alert("请选择文件!");
                return false;
            }
            if ((path.toLocaleLowerCase().indexOf("." + "txt") == -1)) {
                alert("请选择TXT文件!");
                return false;
            }
            var file = dwr.util.getValue('RefTXTfileid');
             loadingMessage("处理中，请稍候......");
            MerRefundmentService.uploadBatchRefundFil(file,tt,function(list){
            	dwr.util.removeAllRows("dataList");
            	  var obj = document.getElementById("dataList");
            	  document.getElementById("refundmentResult").style.display="";
                if(list==null){
                	listArray.length = 0;
                    document.getElementById("action_but").style.display='none';
                    var tr = obj.insertRow(-1);
                    var td = tr.insertCell(-1); ;
                    td.setAttribute("colSpan",8);
                    td.innerHTML = "您上传的文件有错或文件中没有记录";
                }else{
                	document.getElementById("action_but").style.display='';
                    var totle = 0;
                    for(var i=0;i<list.length;i++){
                        var o = list[i];
                        listArray.push(o);//加入listArray
                    	var tr = obj.insertRow(-1);
                        var td1 = tr.insertCell(-1); 
                        td1.innerHTML = i+1;
                        var td2 = tr.insertCell(-1); 
                        td2.innerHTML =  o.initTseq==null?"":o.initTseq ;
                        var td3 = tr.insertCell(-1); 
                        td3.innerHTML =  o.initMid==null?"":o.initMid ;
                        var td4 = tr.insertCell(-1); 
                        td4.innerHTML =  o.initMerOid==null?"":o.initMerOid  ;
                        var td5 = tr.insertCell(-1); 
                        td5.innerHTML =   o.initMdate==null?"":o.initMdate ;
                        var td6 = tr.insertCell(-1);
                        td6.innerHTML = o.initRefAmt;
                        var td7 = tr.insertCell(-1);
                        var td8 = tr.insertCell(-1);

                        
                        totle += Number(o.initRefAmt)*100;
                    }
                    var tr = obj.insertRow(-1);
                    var td = tr.insertCell(-1); 
                    td.setAttribute("colSpan",8);
                    td.innerHTML = "总申请笔数  <font color='red'><b>"+list.length+"</b></font> 笔   ，总申请金额  <font color='red'><b>"+div100(totle)+"</b></font>  元 ";
                }
            });
        }


        function doAction(){
            if(listArray.length==0){
                return;
            }
            var tt = document.getElementById("hidden_tt").value;
            var refundReason = document.getElementById("refundReason").value;
            var loginMid = document.getElementById("loginMid").value;
            if(refundReason.length>60){
                alert("退款原因不能超过60长度");
                return;
            }
            loadingMessage("处理中，请稍候......");
            MerRefundmentService.doBatchRefund(listArray,loginMid,refundReason,tt,function(list){
           		 dwr.util.removeAllRows("dataList");
				var dataList=document.getElementById("dataList");
            	document.getElementById("action_but").style.display='none';
            	for(var i=0;i<list.length;i++){
                    var o = list[i];
                    var tr = dataList.insertRow(-1);
                    var td1 = tr.insertCell(-1); 
                    td1.innerHTML = i+1;
                    var td2 = tr.insertCell(-1); 
                    td2.innerHTML =  o.initTseq ;

                    var td3 = tr.insertCell(-1); 
                    td3.innerHTML =  o.initMid==null?"":o.initMid ;

                    var td4 = tr.insertCell(-1); 
                    td4.innerHTML =  o.initMerOid==null?"":o.initMerOid ;

                    var td5 = tr.insertCell(-1); 
                    td5.innerHTML =  o.initMdate==null?"":o.initMdate ;

                    
                    var td6 = tr.insertCell(-1);
                    td6.innerHTML = o.initRefAmt;

                    var td7 = tr.insertCell(-1);
                    td7.innerHTML = o.state == 0 ? "成功" : "失败" ;
                    
                    var td8 = tr.insertCell(-1);
                    td8.innerHTML = o.state == 1 ? o.errMsg : "";
                }
			listArray=null;
			listArray= new Array();//清空数据
            });
        }
        </script>
        
    </head>

    <body >
    
    <div class="style" >
        <table width="100%"  align="left"  class="tableBorder">
          <tr><td class="title" align="left" colspan="2">&nbsp;&nbsp;批量退款申请</td></tr>
          <tr>
             <td class="th1" align="right">&nbsp;<b>退款类型：</b></td>
             <td align="left">
                <select style="width: 200px" id="refund_type">
                    <option value="BYTSEQ">按电银流水号批量退款</option>
                    <option value="BYMEROID">按商户订单号批量退款</option>
                </select>
             
             </td>
          </tr>
          <tr>
             <td class="th1" align="right">&nbsp;<b>TXT文件：</b></td>
             <td align="left"><input type="file"  id="RefTXTfileid" name="RefTXTfileid" value="" style="width: 200px;height: 24px;" accept=".txt" /></td>
          </tr>
          <tr >
               <td class="th1" align="right">
               <input type="hidden" id="loginMid" value="${sessionScope.SESSION_LOGGED_ON_USER.mid}"/>
               </td>
               <td height="30px">&nbsp;&nbsp;
              <input class="button" value="  上 传  " type="submit" onclick="return uploadBatchFile()"/>
              &nbsp;&nbsp;&nbsp;&nbsp;
              </td>
          </tr>
        </table>
        
      <table class="tablelist tablelist2" style="display: none;" id="refundmentResult">
             <tr>
              <th>序号</th>
              <th>电银流水号</th>
              <th>商户号</th>
              <th>商户订单号</th>
              <th>商户日期</th>
              <th>退款金额(元)</th>
              <th>退款结果</th>
              <th>失败的原因</th>
           </tr>
           <tbody id="dataList"></tbody>
      </table><br />
       <table  style="display: none;" class="tableBorder" width="100%" id="action_but" >
         <tr> 
            <td class="th1" align="right" width="200px" valign="top"><b>退款原因:</b></td>
            <td align="left" height="30px">
            <textarea rows="2" cols="50" id="refundReason"></textarea>
            </td>
          </tr>
         <tr >
              <td align="left" colspan="2">
              <input id="hidden_tt" type="hidden" /> 
              <input class="button" value="申 请 退 款 " type="button" style="height: 30px;width: 120px;margin-left: 200px;" onclick="doAction();"/> 
              </td>
           </tr>
           </table>
      </div>
    </body>
</html>
