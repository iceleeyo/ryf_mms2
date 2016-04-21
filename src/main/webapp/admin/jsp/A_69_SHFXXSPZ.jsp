<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>风险交易录入</title>
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
        <link href="../../public/css/head.css" rel="stylesheet" type="text/css"/> 
        <script type="text/javascript" src="../../dwr/engine.js?<%=rand %>"></script>
        <script type="text/javascript" src="../../dwr/util.js?<%=rand %>"></script>
        <script type="text/javascript" src='../../dwr/interface/PageService.js?<%=rand%>'></script>
        <script type="text/javascript" src="../../dwr/interface/MerRiskConService.js?<%=rand%>"></script>  
        <script type="text/javascript" src='../../public/js/ryt.js?<%=rand %>'></script>
       <script type="text/javascript">
            function queryMinfo(){
                var mid = $("mid").value;
                if(mid==''){
                    alert("请输入商户号查询");
                    return;
                }
                if(!isFigure(mid)){ alert("商户号只能是整数");return;}
                dwr.engine.setAsync(false);
          	    MerRiskConService.getOneMinfo(mid,function(m){
					if(m==null){
						alert("商户不存在!");
						return false;
					}else{
						
						$("mids").value=$("mid").value;
						$("editMerInfoFXTB").style.display = '';
						//dwr.util.setValue('card_type',m.qkrisk.cardType);
						if(m.qkrisks.length>0){
								for ( var i = 0; i < m.qkrisks.length; i++) {
									if(m.qkrisks.length==2){
										 if (m.qkrisks[i].cardType=="C"){
		      						 	
		      						 	dwr.util.setValues({
		  								single_limit :m.qkrisks[i].singleLimit/100,
	                            		day_limit :m.qkrisks[i].dayLimit/100,
	                            		day_succ_count :m.qkrisks[i].daySuccCount,
	                           			day_fail_count :m.qkrisks[i].dayFailCount
	                           			});
		      						 }
		      						
		      						 if(m.qkrisks[i].cardType=="D"){
		      						/*  jQuery("#card_type1").val(m.qkrisks[i].cardType); */
		      						 	dwr.util.setValues({
		  								single_limit1 :m.qkrisks[i].singleLimit/100,
	                            		day_limit1 :m.qkrisks[i].dayLimit/100,
	                            		day_succ_count1 :m.qkrisks[i].daySuccCount,
	                           			day_fail_count1 :m.qkrisks[i].dayFailCount
	                           			});
		      						 }
									}
		    					else{
		    						if (m.qkrisks[i].cardType=="C"){
		      						 	
		      						 	dwr.util.setValues({
		  								single_limit :m.qkrisks[i].singleLimit/100,
	                            		day_limit :m.qkrisks[i].dayLimit/100,
	                            		day_succ_count :m.qkrisks[i].daySuccCount,
	                           			day_fail_count :m.qkrisks[i].dayFailCount
	                           			});
		      						 }
		      						 else{
			      						 	dwr.util.setValues({
			  								single_limit :0,
		                            		day_limit :0,
		                            		day_succ_count :0,
		                           			day_fail_count :0
		                           			});
		      						 
		      						 }
		      						 if(m.qkrisks[i].cardType=="D"){
		      						 	dwr.util.setValues({
		  								single_limit1 :m.qkrisks[i].singleLimit/100,
	                            		day_limit1 :m.qkrisks[i].dayLimit/100,
	                            		day_succ_count1 :m.qkrisks[i].daySuccCount,
	                           			day_fail_count1 :m.qkrisks[i].dayFailCount
	                           			});
		      						 }
		      						 else{
			      						 	dwr.util.setValues({
			  								single_limit1 :0,
		                            		day_limit1 :0,
		                            		day_succ_count1 :0,
		                           			day_fail_count1 :0
		                           			});
		      						 }
		    					}
		      						
		      						 
								}
								//jQuery("#card_type").val(m.qkrisk.cardType);
							}
							 else{
								
								dwr.util.setValues({
		  								single_limit1 :0,
	                            		day_limit1 :0,
	                            		day_succ_count1 :0,
	                           			day_fail_count1 :0,
	                           			single_limit :0,
	                            		day_limit :0,
	                            		day_succ_count :0,
	                           			day_fail_count :0
	                           			});
							}
						 
							dwr.util.setValues({
						    trans_limit :m.transLimit/100,
						    mer_chk_flag :m.merChkFlag,
						    refund_flag :m.refundFlag,
							daySuccAcc : m.accSuccCount,
							daySuccId : m.idSuccCount,
							daySuccPhone : m.phoneSuccCount,
							dayfailAcc :m.accFailCount,
							dayfailId : m.idFailCount,
							dayfailPhone :m.phoneFailCount,
							merTradeType :m.merTradeType,
							merTradeType_name:trade_type_map[m.merTradeType],
							merAbbrev :m.abbrev,
							dayMaxAmt:m.amtLimitD/100,
							monthMaxAmt:m.amtLimitM/100,
							cardMaxAmt:m.cardLimit/100,
							daywhiteAccSuccCount:m.whiteAccSuccCount,
                            daywhiteIdSuccCount:m.whiteIdSuccCount,
                            daywhitePhoneSuccCount:m.whitePhoneSuccCount,
                            daywhiteAccFailCount:m.whiteAccFailCount,
                            daywhiteIdFailCount:m.whiteIdFailCount,
                            daywhitePhoneFailCount:m.whitePhoneFailCount
                           
							});
							
						
					}
					 
				});
				/*  dwr.engine.setAsync(true);
				var card =document.getElementById("card_type").value;
						  if(card=="C"){
							jQuery("#card_type").attr("checked",true);
						}
						else if(card=='on'){
							jQuery("#card_type").val("");
						} 
						else if(card==""){
						jQuery("#card_type").removeAttr("checked");
						
						} 
				var card1 =document.getElementById("card_type1").value;
						  if(card1=="D"){
							jQuery("#card_type1").attr("checked",true);
						}
						else if(card1=='on'){
							jQuery("#card_type1").val("");
						} 
						else if(card1==""){
						jQuery("#card_type1").removeAttr("checked");
						
						}  */
            }        
//修改商户风险信息
function editMinfoFX(){
	if(!checkEdit())return false;
	var minfo = new Object();
	var qkrisk = new Object();
	var qkrisk1 = new Object();
	minfo.accSuccCount =  dwr.util.getValue("daySuccAcc").trim();
	minfo.idSuccCount = dwr.util.getValue("daySuccId").trim();
	minfo.phoneSuccCount = dwr.util.getValue("daySuccPhone").trim();
	minfo.accFailCount = dwr.util.getValue("dayfailAcc").trim();
	minfo.idFailCount = dwr.util.getValue("dayfailId").trim();
	minfo.phoneFailCount = dwr.util.getValue("dayfailPhone").trim();
	minfo.merTradeType = dwr.util.getValue("merTradeType").trim();/**/
	minfo.id=dwr.util.getValue("mids").trim();
	minfo.transLimit=dwr.util.getValue("trans_limit").trim();
	minfo.merChkFlag=dwr.util.getValue("mer_chk_flag").trim();
	minfo.refundFlag=dwr.util.getValue("refund_flag").trim();
	minfo.amtLimitD=dwr.util.getValue("dayMaxAmt").trim();
	minfo.amtLimitM=dwr.util.getValue("monthMaxAmt").trim();
	minfo.cardLimit=dwr.util.getValue("cardMaxAmt").trim();
	minfo.whiteAccSuccCount=dwr.util.getValue("daywhiteAccSuccCount").trim();
	minfo.whiteIdSuccCount=dwr.util.getValue("daywhiteIdSuccCount").trim();
	minfo.whitePhoneSuccCount=dwr.util.getValue("daywhitePhoneSuccCount").trim();
	minfo.whiteAccFailCount=dwr.util.getValue("daywhiteAccFailCount").trim();
	minfo.whiteIdFailCount=dwr.util.getValue("daywhiteIdFailCount").trim();
	minfo.whitePhoneFailCount=dwr.util.getValue("daywhitePhoneFailCount").trim();
	
	/* qkrisk.cardType=dwr.util.getValue("card_type").trim(); */
	
	/* alert(document.getElementById("card_type").value); */
	/* qkrisk.cardType=document.getElementById("card_type").value; */
	qkrisk.cardType='C';
	qkrisk.singleLimit=dwr.util.getValue("single_limit").trim();
	qkrisk.dayLimit=dwr.util.getValue("day_limit").trim();
	qkrisk.daySuccCount=dwr.util.getValue("day_succ_count").trim();
	qkrisk.dayFailCount=dwr.util.getValue("day_fail_count").trim();
	qkrisk.mid=dwr.util.getValue("mids").trim();
	
	/* qkrisk1.cardType=document.getElementById("card_type1").value; */
	qkrisk1.cardType='D';
	qkrisk1.singleLimit=dwr.util.getValue("single_limit1").trim();
	qkrisk1.dayLimit=dwr.util.getValue("day_limit1").trim();
	qkrisk1.daySuccCount=dwr.util.getValue("day_succ_count1").trim();
	qkrisk1.dayFailCount=dwr.util.getValue("day_fail_count1").trim();
	qkrisk1.mid=dwr.util.getValue("mids").trim();
	
	MerRiskConService.editMinfoFX(minfo,qkrisk,qkrisk1,function(msg){
		if(msg=='ok'){
			alert("修改成功");
		}else{
			alert(msg);
		}
	});
}

/* jQuery(function(){
		jQuery(":checkbox[name='check']").live('click', function() {
			
					if(!this.checked){
						 jQuery("#card_type").val("");
						 jQuery("#card_type").removeAttr("checked"); 
					}
					else{
						 jQuery("#card_type").attr("checked",this.checked);
						jQuery("#card_type").val("C"); 
					}
				

			
		});
		jQuery(":checkbox[name='check1']").live('click', function() {
			
					if(!this.checked){
						 jQuery("#card_type1").val("");
						 jQuery("#card_type1").removeAttr("checked"); 
					}
					else{
						 jQuery("#card_type1").attr("checked",this.checked);
						jQuery("#card_type1").val("D"); 
					}
				

			
		});
	}); */
	
//检验输入是否是数字
function checkEdit(){
     var arrId=["daySuccAcc","daySuccId","daySuccPhone","dayfailAcc","dayfailId","dayfailPhone",
     "daywhiteAccSuccCount","daywhiteIdSuccCount","daywhitePhoneSuccCount","daywhiteAccFailCount","daywhiteIdFailCount","daywhitePhoneFailCount",
     "day_succ_count","day_fail_count"];
     var arrMsg=["同一卡号成功","同一身份证号成功","同一手机号成功","同一卡号失败","同一身份证号失败","同一手机号失败",
                 "白名单同一卡号成功","白名单同一身份证号成功","白名单同一手机号成功","白名单同一卡号失败","白名单同一身份证号失败","白名单同一手机号失败",
                 "信用卡同一卡号同一天成功","信用卡同一卡号同一天失败"];
		for ( var i = 0; i < arrId.length; i++) {
		    if(!isFigure(dwr.util.getValue(arrId[i]))){
		       alert(arrMsg[i]+"交易次数必须是正整数！");
		     return false;
		   } 
		}
		var trans_limit =document.getElementById("trans_limit").value;
		var dayMaxAmt =document.getElementById("dayMaxAmt").value;
		var monthMaxAmt =document.getElementById("monthMaxAmt").value;
		var cardMaxAmt=document.getElementById("cardMaxAmt").value;
		var singleLimit=document.getElementById("single_limit").value;
		var dayLimit=document.getElementById("day_limit").value;
		
		if(!(isInteger(trans_limit)||trans_limit=="0")){
				alert("单笔限制金额必须是正整数或0！");
				return false;
		}
		if(!(isInteger(dayMaxAmt)||dayMaxAmt=="0")){
			alert("日累计交易金额必须是正整数或0！");
			return false;
		}
		if(!(isInteger(monthMaxAmt)||monthMaxAmt=="0")){
			alert("月累计交易金额必须是正整数或0！");
			return false;
		}
		if(!(isInteger(cardMaxAmt)||cardMaxAmt=="0")){
			alert("信用卡单笔限额必须是正整数或0！");
			return false;
		}
		if(!(isInteger(singleLimit)||singleLimit=="0")){
			alert("快捷支付信用卡单笔限额必须是正整数或0！");
			return false;
		}
		if(!(isInteger(dayLimit)||dayLimit=="0")){
			alert("快捷支付信用卡日限额必须是正整数或0！");
			return false;
		}
       return true;
}
var trade_type_map={};
function initParam(){
	initMinfos();
	PageService.getTradeTypeMap(function(data){trade_type_map=data;});
}
        </script>
        
    </head>

    <body onload="initParam();">
    
    <div class="style">
         <table width="100%"  align="left"  class="tableBorder">
            <tr><td class="title" align="left">商户风险系数配置</td></tr>
            <tr>
                <td align="center" height="35px">&nbsp;商户号：
                    <input type="text" id="mid" name="mid"   onkeyup="checkMidInput(this);" style="width:140px;height: 18px;"/>
                        <!-- <select style="width: 150px" id="smid" name="smid" onchange="initMidInput(this.value)">
                     <option value="">全部...</option>
                   </select> -->&nbsp;&nbsp;<font color="red">*</font>
                 <input type="button" value = "查  询" name="submitQuery" onclick="queryMinfo();" class="button"/>
                </td>
            </tr>
        </table>
       	<input name="mids" id="mids" type="hidden"/>
<!--  -->
 <table class="tableBorder" id="editMerInfoFXTB" cellpadding="0" cellspacing="0" style="display: none">
       <tr><td class="title" align="center" colspan="4">商户风险系数配置</td></tr>
       <tr >
           <td class="th1" align="right" width="15%" height="25px">商户简称：</td>
           <td  width="30%">&nbsp;&nbsp;<input type="text" id="merAbbrev" disabled="disabled" /></td>
				<td class="th1" align="right" width="25%" height="25px">所属行业：</td>
           <td width="30%">&nbsp;&nbsp;
           <input id="merTradeType" type="hidden" />
           <input type="text" id="merTradeType_name" disabled="disabled"/></td>
       </tr>
          <tr>
        <td class="th1" align="right">单笔限制金额(元)：</td>
        <td align="left">&nbsp;&nbsp; <input type="text" id="trans_limit" maxlength="7" value="0"/>(为0表示无上限，只能为整数)</td>
        <td  class="th1" align="right"> 联机查询：</td>
        <td align="left">&nbsp;&nbsp;
       				 <select id="mer_chk_flag">
                            <option value="1">允许查当天交易</option>
                           <!-- <option value="2">允许查当天和历史交易</option> -->
                            <option value="0">不允许</option>
                     </select>
         </td>
      </tr>
           <tr>
        <td class="th1" align="right">是否允许退款：</td> 
        <td align="left">&nbsp;&nbsp;
               <select id="refund_flag">
                     <option value="1"> 允许  </option>
                     <option value="0"> 不允许</option>
                </select>
         </td>
        <td align="left"></td>
        <td align="left"></td>
      </tr>
       
      <tr>
      	<td class="th1" colspan="4"  align="center"> 信用卡支付</td>
      </tr>
      
	<tr ><td class="th1"> </td>
		<td class="th1" ><b>同一卡号同一天成功交易次数</b></td>
		<td class="th1" ><b>同一身份证号同一天成功交易次数</b></td>
		<td class="th1" ><b>同一手机号同一天成功交易次数</b></td>
	</tr>
       <tr>
        <td class="th1" align="right">日成功交易次数：</td>
        <td align="left">&nbsp;&nbsp;<input type="text" id="daySuccAcc" value='0' size="20" maxlength="5"/></td>
        <td align="left">&nbsp;&nbsp;<input type="text" id="daySuccId" value='0' size="20" maxlength="5"/></td>
        <td align="left">&nbsp;&nbsp;<input type="text" id="daySuccPhone" value='0' size="20" maxlength="5"/></td>
       </tr>
       <tr>
        <td class="th1" align="right">日失败交易次数：</td>
        <td align="left">&nbsp;&nbsp;<input type="text" id="dayfailAcc" value='0' size="20" maxlength="5"/></td>
        <td align="left">&nbsp;&nbsp;<input type="text" id="dayfailId" value='0' size="20" maxlength="5"/></td>
        <td align="left">&nbsp;&nbsp;<input type="text" id="dayfailPhone" value='0' size="20" maxlength="5"/></td>
      </tr>
      
    <tr ><td class="th1" colspan="4"> </td></tr>  
	<tr>
		<td class="th1" align="right">同一卡号日累计消费金额：</td>
		<td>&nbsp;&nbsp;<input type="text" maxlength="7" id="dayMaxAmt" name="dayMaxAmt"/></td>
		<td class="th1" align="right">同一卡号月累计消费金额：</td>
		<td>&nbsp;&nbsp;<input type="text" maxlength="7" id="monthMaxAmt" name="monthMaxAmt"/></td>
	</tr>
	
	<tr>
		<td class="th1" align="right">信用卡单笔限额：</td>
		<td>&nbsp;&nbsp;<input type="text" maxlength="7" id="cardMaxAmt" name="cardMaxAmt"/>(为0表示无上限，只能为整数)</td>
		<td class="th1" align="right"></td>
		<td></td>
	</tr>
		<tr><td class="th1"> </td>
		<td class="th1" ><b>白名单同一卡号同一天成功交易次数</b></td>
		<td class="th1" ><b>白名单同一身份证号同一天成功交易次数</b></td>
		<td class="th1" ><b>白名单同一手机号同一天成功交易次数</b></td>
	</tr>
       <tr>
        <td class="th1" align="right">日成功交易次数：</td>
        <td align="left">&nbsp;&nbsp;<input type="text" id="daywhiteAccSuccCount" value='0' size="20" maxlength="5"/></td>
        <td align="left">&nbsp;&nbsp;<input type="text" id="daywhiteIdSuccCount" value='0' size="20" maxlength="5"/></td>
        <td align="left">&nbsp;&nbsp;<input type="text" id="daywhitePhoneSuccCount" value='0' size="20" maxlength="5"/></td>
       </tr>
       <tr>
        <td class="th1" align="right">日失败交易次数：</td>
        <td align="left">&nbsp;&nbsp;<input type="text" id="daywhiteAccFailCount" value='0' size="20" maxlength="5"/></td>
        <td align="left">&nbsp;&nbsp;<input type="text" id="daywhiteIdFailCount" value='0' size="20" maxlength="5"/></td>
        <td align="left">&nbsp;&nbsp;<input type="text" id="daywhitePhoneFailCount" value='0' size="20" maxlength="5"/></td>
      </tr>
	
	 <tr>
      	<td class="th1" colspan="4"  align="center">快捷支付</td>
      </tr>
	 <!--  <tr>
      	<td >卡种限制</td>
      	<td ><input id="card_type" name="check" type="checkbox"   />信用卡</td>
      	<td ><input id="card_type1" name="check1" type="checkbox"   />借记卡</td>
      	<td ></td>
      </tr> -->
	 <tr>
      	<td class="th1" align="right" >信用卡单笔限额</td>
      	<td align="left">&nbsp;&nbsp;<input type="text" id="single_limit" value='0' size="20" maxlength="5"/></td>
      	<td class="th1" align="right">信用卡同一卡号同一天成功交易次数</td>
      	<td align="left">&nbsp;&nbsp;<input type="text" id="day_succ_count" value='0' size="20" maxlength="5"/></td>
      </tr>
       <tr>
      	<td class="th1" align="right">信用卡日限额</td>
      	<td align="left">&nbsp;&nbsp;<input type="text" id="day_limit" value='0' size="20" maxlength="5"/></td>
      	<td class="th1" align="right">信用卡同一卡号同一天失败交易次数</td>
      	<td align="left">&nbsp;&nbsp;<input type="text" id="day_fail_count" value='0' size="20" maxlength="5"/></td>
      </tr>
	   <tr>
      	<td class="th1" align="right">借记卡单笔限额</td>
      	<td align="left">&nbsp;&nbsp;<input type="text" id="single_limit1" value='0' size="20" maxlength="5"/></td>
      	<td class="th1" align="right">借记卡同一卡号同一天成功交易次数</td>
      	<td align="left">&nbsp;&nbsp;<input type="text" id="day_succ_count1" value='0' size="20" maxlength="5"/></td>
      </tr>
       <tr>
      	<td class="th1" align="right">借记卡日限额</td>
      	<td align="left">&nbsp;&nbsp;<input type="text" id="day_limit1" value='0' size="20" maxlength="5"/></td>
      	<td class="th1" align="right">借记卡同一卡号同一天失败交易次数</td>
      	<td align="left">&nbsp;&nbsp;<input type="text" id="day_fail_count1" value='0' size="20" maxlength="5"/></td>
      </tr>
     <tr><td colspan="4" align="center"><input type="button" value=" 修 改 "  onclick="editMinfoFX();" class="button"/> &nbsp;&nbsp; 
		<!-- <input type="button" value="刷新"  class="button"/> --></td></tr>
    </table>
        
      </div>
    </body>
</html>
