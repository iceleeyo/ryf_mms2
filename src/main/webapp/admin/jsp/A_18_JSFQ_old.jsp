<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" errorPage="../../error.jsp" isErrorPage="false"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
    <head>
    <title>结算发起</title>
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
        <script type='text/javascript' src='../../dwr/engine.js'></script> 
        <script type='text/javascript' src='../../dwr/util.js'></script>
        <script type='text/javascript' src='../../dwr/interface/SettlementService.js?<%=rand%>'></script>
        <script type='text/javascript' src='../../dwr/interface/PageService.js?<%=rand %>'></script>
        <script type='text/javascript' src='../../public/datePicker/WdatePicker.js'></script>
       	<script type='text/javascript' src='../../public/js/ryt.js?<%=rand%>"'></script>
        <script type='text/javascript'>
        var settleminfosCache = {};
        function getSettleMinfos(date){
        	 SettlementService.getSettleMinfos2(date,function(alist){
        		 dwr.util.removeAllRows("settleMinfoTable");
                 dwr.util.removeAllRows("noSettleMinfoTable");
        		 for(var i = 0 ; i< alist.length; i++){
                     theObj = alist[i];
                     settleminfosCache[theObj.mid] = theObj;
                     if(theObj.perioded){
                    	 addRowToSettleMinfoTable(theObj);
                     }else{
                    	 addRowToNoSettleMinfoTable(theObj);
                     }
                 }
             })
        }
        //非结算周期内增加一行
        function addRowToNoSettleMinfoTable(theObj){
            var tableToAdd = document.getElementById("noSettleMinfoTable");
            var t2Tr = tableToAdd.insertRow(-1);
            var t2TableRowID = theObj.mid+"1" ;//注意这句：标识以后面删除时dom对象的唯一ID
            t2Tr.setAttribute("id", t2TableRowID);
            var t2Td1 = t2Tr.insertCell(-1);
            t2Td1.innerHTML = "<input value='<<增加'  name='disabledbut' type='button' onclick='changeTale(1,\""+theObj.mid+"\")' style='width: 50px;float: left'  />";
            var t2Td2 = t2Tr.insertCell(-1);
            t2Td2.innerHTML = theObj.name;
            var t2Td3 = t2Tr.insertCell(-1);
            t2Td3.innerHTML = theObj.lastLiqDate == 0 ? '' : theObj.lastLiqDate ;
        }
        //结算周期内增加一行
        function addRowToSettleMinfoTable(theObj){
       	   var tableToAdd = document.getElementById("settleMinfoTable");
           var t1Tr = tableToAdd.insertRow(-1);
           var t1TableRowID = theObj.mid ;//注意这句：标识以后面删除时dom对象的唯一ID
           t1Tr.setAttribute("id", t1TableRowID+"0");
           var t1Td1 = t1Tr.insertCell(-1);
           t1Td1.innerHTML = "<input type='hidden' name='selected_mid' value="+theObj.mid+" /><input style='width: 50px;float: left' value='删除>>' onclick='changeTale(0,\""+theObj.mid+"\")' name='disabledbut' type='button' />";;
           var t1Td2 = t1Tr.insertCell(-1);
           t1Td2.innerHTML = theObj.name;
           var t1Td3 = t1Tr.insertCell(-1);
           t1Td3.innerHTML = theObj.lastLiqDate == 0 ? '' : theObj.lastLiqDate ;
           var t1Td4 = t1Tr.insertCell(-1);
           t1Td4.innerHTML = theObj.flag ? "可以" : theObj.msg==null ? '' : (theObj.msg=="没有可结算的订单"?'<font color=red>'+ (theObj.msg) + '<font color=red>':theObj.msg=="合同过期!"?'<font style="font-weight:bold">'+ (theObj.msg) + '<font style="font-weight:bold">':'<font color=blue>'+ (theObj.msg) + '<font color=blue>');
           var t1Td5 = t1Tr.insertCell(-1);
           t1Td5.innerHTML = theObj.resule==null?"":theObj.resule;
        }

        function changeTale(t,mid){
        	//t 1 zj;0 sc
            if(t==0){//结算周期内删除
                var rowToDelete = document.getElementById(mid+"0");
                var tableToDelete = document.getElementById("settleMinfoTable");
                //从表格中移除用户数据行
                tableToDelete.removeChild(rowToDelete);
                var theObj = settleminfosCache[mid];
                addRowToNoSettleMinfoTable(theObj);
            }
            if(t==1){//非结算周期内增加
           	 var rowToDelete = document.getElementById(mid+"1");
                 var tableToDelete = document.getElementById("noSettleMinfoTable");
                 //从表格中移除用户数据行
                 tableToDelete.removeChild(rowToDelete);
                 var theObj = settleminfosCache[mid];
                 addRowToSettleMinfoTable(theObj);
            }
        }
        function disabledBut(self){
            var buts = document.getElementsByName("disabledbut");
            for(var f = 0;f<buts.length;f++){
            	buts[f].disabled = 'disabled';
            }
            $("cxxzbut").disabled = '';
            $("kjsfqbut").disabled = '';
            self.disabled = 'disabled';
        }

        function selectSettleMinfos(self){
            var buts = document.getElementsByName("disabledbut");
            for(var f = 0;f<buts.length;f++){
                buts[f].disabled = '';
            }
            $("qrxzbut").disabled = '';
            $("kjsfqbut").disabled = 'disabled';
            self.disabled = 'disabled';
        }
        var resultminfos = new Array();
        function checkSettleMinfos(self){
        	resultminfos.length = 0 ;//清空缓存
        	var minfos = document.getElementsByName("selected_mid");
        	var exp_date = document.getElementById("exp_date").value;
        	var sellectedMinfos = new Array();
        	for(var f = 0;f<minfos.length;f++){
        		sellectedMinfos.push(settleminfosCache[minfos[f].value]);
            }
        	SettlementService.checkMinfo2(sellectedMinfos,exp_date,function(alist){
        		dwr.util.removeAllRows("settleMinfoTable");
                $("jsfqbut").disabled = '';
                self.disabled = 'disabled';
                $("cxxzbut").disabled = 'disabled';
                for(var i = 0 ; i< alist.length; i++){
                    theObj = alist[i];
                    if(theObj.flag)
                    resultminfos.push(theObj);
                    addRowToSettleMinfoTable(theObj);
                }
                var buts = document.getElementsByName("disabledbut");
                for(var f = 0;f<buts.length;f++){
                    buts[f].disabled = 'disabled';
                }
            });
        }

        function beginSettlement(self){
        	self.disabled = 'disabled';
        	$("cxxzbut").disabled = 'disabled';
        	var exp_date=document.getElementById("exp_date").value;
        	//alert(resultminfos[0].mid);
        	SettlementService.beginSettlement2(resultminfos,exp_date,function(alist){
        		dwr.util.removeAllRows("settleMinfoTable");
        		for(var i = 0 ; i< alist.length; i++){
                    addRowToSettleMinfoTable(alist[i]);
                }
        		var buts = document.getElementsByName("disabledbut");
                for(var f = 0;f<buts.length;f++){
                    buts[f].disabled = 'disabled';
                }
        		
            });
        }
        function nextStep(){
            var exp_date = document.getElementById("exp_date").value;
			if(exp_date == ''){
				alert("请选择要截至日期");
				return false;
			}
			document.getElementById("begin_settle").style.display="none";
			document.getElementById("settleBody").style.display="";
			getSettleMinfos(exp_date);
        }
        </script>
    </head>
    <body>
     <div class="style">
         <table class="tableBorder" width="80%" align="center"  id="begin_settle">
	       <tbody>
			<tr>
				<td colspan="2" align="left" class="title" height="25">
				<b><font color="#ffffff">&nbsp;&nbsp; 结算发起</font> </b>
				</td>
			</tr>
		      <tr>
			<td class="th1" width="30%" align="right" bgcolor="#d9dfee">&nbsp;截至日期：</td>
			<td width="70%" align="left">
			&nbsp;&nbsp;<input type="text" id="exp_date" name= "exp_date" size=20 
			onfocus="WdatePicker({skin:'ext',dateFmt:'yyyyMMdd',maxDate:'%y-%M-%d',readOnly:'true'});" class="Wdate"/>
			&nbsp;&nbsp;&nbsp;&nbsp;<input  value="下一步" type="button" class="button" onclick="nextStep()"/>
			</td>
		   </tr>
		</tbody>
	</table>
	<!-- ***************next*************** -->
<div style="display: none;" id="settleBody">
        <table  class="tablelist tablelist2">
           <thead>
           <tr><th colspan="10">选择商户发起结算</th> </tr> </thead>
       <tbody>
        <tr>
          <td colspan="10">
           <input type="button"  value="返回" onclick="window.location.href='A_18_JSFQ.jsp'" style="margin-left: 40px;margin-right: 20px;height: 30px;width: 100px;margin-top: 8px"/> 
           <input type="button"  value="确定选择"  id="qrxzbut" onclick="disabledBut(this)"   style="height: 30px;width: 100px;margin-right: 20px;"/> 
           <input type="button"  value="重新选择"  id="cxxzbut"  onclick="selectSettleMinfos(this)"   disabled="disabled"  style="height: 30px;width: 100px;margin-right: 20px;"/>
           <input type="button"  value="可结算发起?" id="kjsfqbut"  disabled="disabled" onclick="checkSettleMinfos(this)"  style="height: 30px;width: 100px;margin-right: 20px;"/> 
           <input type="button"  value="结算发起"   id="jsfqbut"    disabled="disabled" onclick="beginSettlement(this)" style="height: 30px;width: 100px;margin-right: 20px;"/> 
          </td>
        </tr>
       </tbody> </table>
 
    
    <div style="display:inline;float: left;margin-left: 40px;margin-top: 5px;width: 45%;margin-right: 20px">
    <table align="left" class="tablelist tablelist2">
     <thead>
        <tr>
            <th colspan="5" style="background-color: #C0C9E0">
            <b>结算周期内的商户</b></th>
        </tr>
         <tr>
            <th>选择</th><th>商户</th><th>上次结算日期</th><th>可结算发起？</th><th>结算发起结果</th>
        </tr>
      </thead>
      <tbody id="settleMinfoTable"> </tbody>
      </table>
    </div>
    
   <div style="display:inline;float: left;margin-top: 5px;width: 40%;margin-right: 10px">
      <table align="right" class="tablelist tablelist2">
        <thead>
         <tr>
            <th colspan="3" style="background-color: #C0C9E0">
            <b>非结算周期内的商户</b></th>
         </tr>
         <tr><th>选择</th><th>商户</th><th>上次结算日期</th> </tr>
        </thead>
        <tbody id="noSettleMinfoTable"></tbody>
      </table>
    </div>
  </div>
</div>
</body>
</html>
