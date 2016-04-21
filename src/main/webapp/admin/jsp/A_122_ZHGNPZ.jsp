<%@ page language="java" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>账户功能配置</title>
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
        <script type="text/javascript" src="../../dwr/engine.js"></script>
        <script type="text/javascript" src="../../dwr/util.js"></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type="text/javascript" src='../../dwr/interface/PageParam.js?<%=rand%>'></script>
        <script type="text/javascript" src="../../dwr/interface/MerchantService.js?<%=rand%>"></script>  
        <script type="text/javascript" src='../../public/js/ryt.js?<%=rand %>'></script>
        <script type="text/javascript">
        var OPEN=1;
       /**
        *增加一个时间项
        */
        function addOP(){
         	var sysDateHrs= document.getElementsByName("hour");
         	if(sysDateHrs.length<5){
         		//加一行
         		 //得到table对象
			     var sysStartTimeTb = document.getElementById("sysStartTimeTb");
			      //插入行号
			     var rowIndexs=sysStartTimeTb.rows.length;
			     //向table中插入一行
			     var newTr=sysStartTimeTb.insertRow(rowIndexs);
			     //创建一个新的td对象
			     var newTd=document.createElement("td");    
			     //初始化td中的文本信息
			     newTd.innerHTML="<tr><td>&nbsp;<select name='hour'><option value=''>请选择...</option></select>时 &nbsp;"+
         					"<select name='minute'><option value=''>请选择...</option></select>分&nbsp;<span id='add' style=\"font-size: large; cursor: pointer;\"onclick='rmOP(this);'>-</span>"+
							"</td></tr>";
			     //向tr中加入td对象
			     newTr.appendChild(newTd);
			     //=========================初始化时间
			    var sysDateHrs= document.getElementsByName("hour");//批量操作时间所有行（小时）
         	   	var sysTimeHrs= document.getElementsByName("minute");//批量操作时间所有行（分钟）
			     for(var j=0;j < 24; j++){
         			var hour=(j+"").length==1?"0"+j:j;
         			sysDateHrs[sysDateHrs.length-1].options.add(new Option(hour,hour));
         			}
         			//插入所有分钟
         		 for ( var m = 0; m < 60; m++) {
						var minute=(m+"").length==1?"0"+m:m;
         			sysTimeHrs[sysTimeHrs.length-1].options.add(new Option(minute,minute));
				 }
         	}else
         		alert("最多允许添加5个时间!");
         }
         
         function rmOP(elmt){
         	if(window.confirm("是否删除？"))
         		elmt.parentNode.parentNode.remove();
         }
          /**
           *查询商户功能配置
           */
        function config(){
         	var mid=$("mid").value.trim();
         	if(mid==""){
         		alert("请输入商户号!");
         		return;
         		}
         	$("sub_mid").innerHTML=mid;
         	MerchantService.getZHGNByMid(mid,handleData);
         }
        
       function handleData(data){
       			var sysDateTime="";
       			var flag=false;//后面用来判断时间是否需要设置
         		if(data.length!=4){
         			alert("返回数据异常!");
         			return;
         		}
         		//selectStr
         		var startTimeStr="<table id='sysStartTimeTb' width='100%'><tr><td>&nbsp;<select name='hour'><option value=''>请选择...</option></select>时 &nbsp;"+
         			"<select name='minute'><option value=''>请选择...</option></select>分&nbsp;<span id='add' style='font-size: large; cursor: pointer;'onclick='addOP()'>+</span>"+
         		    "&nbsp;&nbsp;&nbsp;&nbsp; <span>提示：支持设置的操作时间上限为：5个，可点击+号增加</span></td></tr>";
         		var sltData=data[3];
         		if(sltData.trim()!=""){
         			flag=true;
         			sysDateTime=sltData.split(";");//商户所有时间
         			for(var i=1;i<sysDateTime.length;i++){
         			if(sysDateTime[i].trim()=="")
         					continue;
         			startTimeStr+="<tr><td>&nbsp;<select name='hour'><option value=''>请选择...</option></select>时 &nbsp;"+
         					"<select name='minute'><option value=''>请选择...</option></select>分&nbsp;<span id='add' style=\"font-size: large; cursor: pointer;\"onclick='rmOP(this);'>-</span>"+
							"</td></tr>";								         					
         			}
         		}
         		startTimeStr+="</table>";
         		$("sysStarTime").innerHTML=startTimeStr;
         		var sysDateHrs= document.getElementsByName("hour");//批量操作时间所有行（小时）
         		var sysTimeHrs= document.getElementsByName("minute");//批量操作时间所有行（分钟）
         		//=========将时间插入值
         		for(var i=0;i<sysDateHrs.length;i++){
         			var hm=null;
         			if(flag)
         				hm=sysDateTime[i].trim().split(":");//时分
         			//插入所有小时
         			for(var j=0;j < 24; j++){
         			var hour=(j+"").length==1?"0"+j:j;
         			sysDateHrs[i].options.add(new Option(hour,hour));
         			}
         			if(flag)
         			sysDateHrs[i].value=hm[0];
         			//插入所有分钟
         			for ( var m = 0; m < 60; m++) {
						var minute=(m+"").length==1?"0"+m:m;
         			sysTimeHrs[i].options.add(new Option(minute,minute));
					}
					if(flag)
					sysTimeHrs[i].value=hm[1];
         		}
         		var week=document.getElementsByName("week");//星期
         		var dfconfig=document.getElementsByName("dfconfig");//代付
         		var dkconfig=document.getElementsByName("dkconfig");//代扣
         		for(var i=0;i<data[2].length;i++){
         			if(dfconfig[i]!=undefined && data[0].charAt(i)==OPEN)
         				dfconfig[i].checked="checked";
         			if(dkconfig[i]!=undefined && data[1].charAt(i)==OPEN)
         				dkconfig[i].checked="checked";
         			if(week[i]!=undefined && data[2].charAt(i)==OPEN)
         				week[i].checked="checked";
         		}
         	$("minfoConfig").style.display="";
         	$("mainTb").style.display="none";
         	}
         	/**
         	*保存新的配置
         	*/
        function savaConfig(){
        	var dfCFGStr="";//代付
        	var dkCFGStr="";//代扣
        	var weekStr="";//星期
        	var sysHandleTimesStr="";//操作时间
        	//获取代付配置
        	var dfconfig=document.getElementsByName("dfconfig");
            //获取代扣配置
           var dkconfig=document.getElementsByName("dkconfig");
            //获取星期配置
           var week=document.getElementsByName("week");//星期
           //获取操作时间配置
           var sysDateHrs= document.getElementsByName("hour");//批量操作时间所有行（小时）
         	var sysTimeHrs= document.getElementsByName("minute");//批量操作时间所有行（分钟）
			for(var i=0; i<week.length; i++){
				if(dfconfig[i]!=undefined && dfconfig[i].checked==true)
					dfCFGStr+="1";
				else if(dfconfig[i]!=undefined)
					dfCFGStr+="0";
				if(dkconfig[i]!=undefined && dkconfig[i].checked==true)		
					dkCFGStr+="1";
				else if(dkconfig[i]!=undefined)
					dkCFGStr+="0";
				if(week[i]!=undefined && week[i].checked==true)	
					weekStr+="1";
				else if(week[i]!=undefined)
					weekStr+="0";
				if(sysDateHrs[i]!=undefined && sysTimeHrs[i]!=undefined && sysTimeHrs[i].value!=""  && sysDateHrs[i].value!="")
					sysHandleTimesStr+=sysDateHrs[i].value+":"+sysTimeHrs[i].value+";";					
			} 
			sysHandleTimesStr=sysHandleTimesStr.substring(0, sysHandleTimesStr.length-1);          
         	//===========================================================校验数据
         	if(dfCFGStr.length!=dfconfig.length || dkCFGStr.length!=dkCFGStr.length || weekStr.length!=week.length){
         			alert("数据异常，提交失败!");
         			return;
         		}
         	var  mid=$("sub_mid").innerHTML;
         			if(window.confirm("将更新商户功能配置,是否继续？")){
         				if(mid==""){
         					alert("提交商户为空!");
         					return;
         				}
         				MerchantService.updateZHGNPZ(mid,dfCFGStr,dkCFGStr,weekStr,sysHandleTimesStr,function(flag){
         					if(flag=="success"){
         						alert("更新配置成功，请刷新!");
         					}else{
         						alert("更新配置失败!");
         						location.href=location.href;
         						}
         				
         				});
         			}
        } 	
       function reflushsavaConfig(){
       MerchantService.reflushsavaConfig(function callback(msg){
       if(msg==true)
        alert('刷新成功');
        else alert('刷新失败');
        location.href=location.href;
        });
       }
        </script>
  </head>
  
  <body>
    <div class="style">
		<table class="tableBorder" id="mainTb">
			<tr>
				<td colspan="2" class="title">账户功能配置</td>
			</tr>
			<tr>
				<td class="th1" align="right">商户号：</td>
				<td><input type="text" id="mid" name="mid"
					style="width: 150px;" />
				</td>
			</tr>
			<tr>
				<td colspan="2" align="center"><input type="button" value="查询"
					onclick="config();" class="button" />
				</td>
			</tr>
		</table>

		<table class="tableBorder" id="minfoConfig" style="display:none;">
			<tr>
				<td colspan="5" class="title">商户【<span style="font-size: large;" id="sub_mid"></span>】账户功能配置</td>
			</tr>
			<tr>
				<td align="right" class="th1" width="20%">代付功能配置：</td>
				<td align="left" width="20%">&nbsp;<input type="checkbox" name="dfconfig"/> 实时代付-企业对企业</td>
				<td align="left" width="20%">&nbsp;<input type="checkbox" name="dfconfig"/> 批量代付-企业对企业</td>
				<td align="left" width="20%">&nbsp;<input type="checkbox" name="dfconfig"/> 实时代付-企业对个人</td>
				<td align="left" width="20%">&nbsp;<input type="checkbox" name="dfconfig"/> 批量代付-企业对个人</td>
			</tr>
			
			<tr>
				<td align="right" class="th1" width="20%">代扣功能配置：</td>
				<td align="left" width="20%">&nbsp;<input type="checkbox" name="dkconfig"/> 实时代扣（企业对个人）</td>
				<td align="left" width="20%">&nbsp;<input type="checkbox" name="dkconfig"/> 批量代扣（企业对个人）</td>
				<td align="left" width="20%"></td>
				<td align="left" width="20%"></td>
			</tr>
			
			<tr>
				<td align="right" class="th1" width="20%">系统批量操作日：</td>
				<td align="left" colspan="4">
				&nbsp;<input type="checkbox" name="week"/> 周一 &nbsp;&nbsp;
				<input type="checkbox" name="week"/> 周二 &nbsp;&nbsp;
				<input type="checkbox" name="week"/> 周三 &nbsp;&nbsp;
				<input type="checkbox" name="week"/> 周四 &nbsp;&nbsp;
				<input type="checkbox" name="week"/> 周五 &nbsp;&nbsp;
				<input type="checkbox" name="week"/> 周六 &nbsp;&nbsp;
				<input type="checkbox" name="week"/> 周日 &nbsp;
				</td>
			</tr>
			
			<tr align="center">
				<td align="right" class="th1" width="20%">系统批量操开始时间：</td>
				<td align="left" colspan="4" id="sysStarTime"></td>
			</tr>
			
			<tr>
				<td colspan="5" align="center">
				<input type="button" value="提交" onclick="savaConfig();" class="button" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<input type="button" value="刷新" onclick="reflushsavaConfig();" class="button" /><font color="red">(修改之后请刷新)</font>
				</td>
			</tr>
		</table>
	</div>
  </body>
</html>
