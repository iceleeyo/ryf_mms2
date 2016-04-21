<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
         <title>修改银行网关</title>
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
        <script type="text/javascript" src='../../dwr/interface/SysManageService.js?<%=rand%>'></script>
        <script type="text/javascript" src="../../public/js/ryt.js?<%=rand%>"></script>
	    <script type="text/javascript" src="../../public/js/GreyFrame.js?<%=rand%>"></script>
        <script type="text/javascript" src="../../public/js/sysmanage/bankGate.js?<%=rand%>"></script> 
</head>
<body onload="initOptions();">
 <div class="style">
     <table width="100%"  class="tableBorder">
        <tbody>
            <tr>
                <td class="title" colspan="2">&nbsp; 增加银行网关（带<font color="red">*</font>的为必填项）</td>
            </tr>
                <tr>
                <td class="th1" align="right" width="35%">&nbsp;交易方式：</td>
                <td  align="left">
                     <select id="trans_mode" onchange="query4NewGate(this.value);" ></select>
                &nbsp;<font color="red">*</font></td>  
               
               </tr>
                <tr>
               
               <td class="th1" align="right">&nbsp;网关名称：</td>
               <td  align="left"><select id="gate_desc_short" ></select>&nbsp;<font color="red">*</font>	
               <a href="#"  onclick="addGate();" class='box_detail'>网关添加 </a>
               </td>   
             </tr>
            <tr>
				
				 <td class="th1" align="right">&nbsp;支付渠道：</td>
                 <td  align="left"><input type="text" id="gateName" name="gateName" onblur="gateRouteIdList()"/>
                     <select id="gateRouteId1"></select>
                &nbsp;<font color="red">*</font>
                </td>    
            </tr>
                <tr>
               
                <td class="th1" align="right">&nbsp;支付银行网关号：</td>
                <td  align="left"><input type="text" id="gateid" value="" size="20" maxlength="10"/>
                &nbsp;<font color="red">*</font></td>
            </tr>
            <tr>     
                <td class="th1" align="right">&nbsp;计费公式：</td>
                <td colspan="3" align="left">
                     <input id="fee_model" type="text" /> 
                &nbsp;<font color="red">*</font>
                <a href="feemodel_details.html" target="feeFrame"><font color="blue">查看公式详情</font>
                </a>
                </td>
           </tr>
            <tr>
                <td class="th1" align="right">&nbsp;是否退还手续费：</td>
                <td  align="left">
                     <select id="fee_flag">
                        <option value="0">不退</option>
                        <option value="1">退回</option>
                    </select>
                &nbsp;<font color="red">*</font></td>
               </tr>
               
               <tr>
                <td colspan="4" align="center"><input type="button" id="submitModelID" value="提 交"  class="button" onclick="addGates()"/></td>
            </tr>
        </tbody>
    </table>
 <!-- -============================ -->
 
    <table class="tableBorder" >
    <tr><td class="title" colspan="5">&nbsp;&nbsp; 银行网关信息修改</td></tr>
    <tr>
        <td align="center">
                         交易类型:&nbsp;<select id="trans_model_x" >
                         <option value="-1">全部...</option>
                         </select>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                          支付渠道:&nbsp;<input type="text" id="gateName1" name="gateName1" onblur="gateRouteIdList1()"/><select id="gateRouteId2" >
                          <option value="-1">全部...</option>
                          </select>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <input type="button" value=" 查 询 " onclick="queryGatesByTJ()" class="button"/> 
        </td>
     </tr>
    </table>
    <br/>
    
   <table class="tablelist tablelist2" id="listGate">
    <thead><tr><th >电银信息网关号</th><th >网关名称</th><th>交易方式</th><th>支付渠道</th><th>支付银行网关号</th>
    <th>计费公式</th><th>操作</th></tr></thead>
    <tbody id="gateRouteTable"></tbody>
    </table>

    <table class="tableBorder" id="detailGate" style="display:none;width:700px;height: 210px;margin-top:0px;">
        <tbody>
            <tr>
                <td class="th1" align="right" width="35%">&nbsp;电银信息网关号：</td>
                <td align="left"><input type="text" id="ryt_gate2" value="" size="30" disabled="disabled"/>&nbsp;</td>
            </tr>
            <tr>
                <td class="th1" align="right">&nbsp;网关名称：</td>
                <td align="left"><input type="text" size="30" readonly="readonly" id="gate_desc_short2" value="" disabled="disabled"/></td>
            </tr>
            <tr>
             <td class="th1" align="right">&nbsp;支付渠道：</td>
                <td align="left">
                   <input id="gid2" size="30" readonly="readonly" disabled="disabled"/>
               </td> 
               
                </tr>
            <tr>
                <td class="th1" align="right">&nbsp;交易方式：</td>
                <td  align="left">
                   <input id="trans_mode2" size="30" readonly="readonly" disabled="disabled"/>
                </td>        
             </tr>
             <tr>
               <td class="th1" align="right">&nbsp;支付银行网关号：</td>
                 <td  align="left"><input type="text" id="gateid2" value="" size="30" maxlength="10" />&nbsp;<font color="red">*</font></td>
            </tr>
             <tr> 
                <td class="th1" align="right">&nbsp;计费公式：</td>
                <td align="left">
                     <input id="fee_model2" type="text" size='30'/> &nbsp;<font color="red">*</font>
                     <a href="feemodel_details.html" target="feeFrame"><font color="blue">查看公式详情</font></a>
                </td>
            </tr>
               <tr> 
                <td class="th1" align="right">&nbsp;是否退还手续费：</td>
                <td  align="left">
                     <select id="fee_flag2">
                        <option value="0">不退</option>
                        <option value="1">退回</option>
                    </select>
                &nbsp;<font color="red">*</font></td>
               </tr>
            <tr>
                <td colspan="4" align="center"  height="20px">
                <input type="hidden" id = "edited_id2" ></input>
                 <input type="hidden" id = "trans_model_id2" ></input>
                <input type="button" value="修  改" onclick="editGates();"  id="submitModelID" class="button"/>&nbsp;&nbsp;
                <input type="button" value="返  回" class="wBox_close button" />
                </td>
            </tr>
        </tbody>
    </table>
    
    <table class="tableBorder" id="add4Gate" style="display:none;width:450px;height: 210px;margin-top:0px;">
        <tbody>
            <tr>
                <td class="th1" align="right" width="35%">&nbsp;网关号：</td>
                <td align="left">
                <input type="text" id="v_gate"  size="20"  maxlength="11"/>
                <font>(最多11位数字)</font>
                </td>
            </tr>
            <tr>
                <td class="th1" align="right">&nbsp;状态标志：</td>
                <td align="left" >
                <select id="v_stat_flag"   style="width: 150px">
                <option value="0">正  常</option>
                <option value="1">关  闭</option></select>
                </td>
            </tr>
            <tr>
             <td class="th1" align="right">&nbsp;交易方式：</td>
                <td align="left">
                <select id="v_trans_mode" style="width: 150px">
                    </select>
               </td> 
               
                </tr>
            <tr>
                <td class="th1" align="right">&nbsp;网关名称：</td>
                <td  align="left">
                   <input id="v_gate_name" size="20"   type="text" maxlength="10"/><font>(最多10个字符)</font>
                </td>        
             </tr>
            
            <tr>
                <td colspan="4" align="center"  height="20px">
                <input type="button" value="添  加" onclick="addRytGate();"  class="button"/>&nbsp;&nbsp;
                <input type="button" value="返  回" class="wBox_close button" />
                </td>
            </tr>
        </tbody>
    </table>
</div>
</body>
</html>