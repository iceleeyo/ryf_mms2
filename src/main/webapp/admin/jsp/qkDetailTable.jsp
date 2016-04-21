<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<table id="hlogDetail"  class="tableBorder detailBox" style="display: none;">
          <!--  <tr><td class="title" colspan="6" align="center">商户订单详细资料</td></tr> -->
           <tr>
               <td align="left" class="th1" width="17%" >商户号：</td>
               <td align="left" width="17%" id="v_mid"></td>
               <td align="left" class="th1" width="17%">用户平台id：</td>
               <td align="left" width="" id="v_userId"></td>
           </tr>
           <tr >
               <td align="left" class="th1" >姓名：</td>
               <td align="left" id="v_cardName"></td>
               <td align="left" class="th1" >身份证：</td>
               <td align="left" id="v_pidNo"></td>
               
           </tr>
           <tr >
           		<td align="left" class="th1" >开户银行：</td>
               <td align="left" id="v_gateId"></td>
               <td align="left" class="th1" >银行卡号：</td>
               <td align="left" id="v_cardNo"></td>
               
           </tr>
           <tr >
           	   <td align="left" class="th1" >手机号：</td>
               <td align="left" id="v_phoneNo"></td>
               <td align="left" class="th1" >开户日期：</td>
               <td align="left" id="v_bindTime"></td>
               
           </tr>
           <tr >
               <td align="left" class="th1" >数据来源：</td>
               <td align="left" id="v_abbrev"></td>
               <td align="left" class="th1" ></td>
               <td align="left" ></td>
           </tr>
           
           <tr><td colspan="6" height="30px"  align="center"> <input type="button"  value = "返回" class="wBox_close button"/>
			
        </table>
</body>
</html>